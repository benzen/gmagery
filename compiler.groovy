@Grapes(
    [@Grab(group='commons-lang', module='commons-lang', version='2.6'),
    @Grab(group='org.jsoup', module='jsoup', version='1.11.2')]
)


// import groovy.util.XmlSlurper
import groovy.xml.MarkupBuilder
import java.io.StringWriter
import groovy.util.IndentPrinter
import org.apache.commons.lang.StringEscapeUtils
import groovy.json.JsonSlurper
import org.jsoup.Jsoup

class Compiler{
  def static  compile(file, data){
    data = data instanceof String ? new JsonSlurper().parseText(data) : data
    def writer = new StringWriter()
    def printer = new IndentPrinter(writer, "    ", true, false)

    // def template = new XmlParser().parseText(file)
    def template = Jsoup.parseBodyFragment(file)
    def containsComments = file.contains("<!--")
    def rootTag = template.getElementsByAttribute("data-tagname").first()
    def tag = rootTag.attributes().get("data-tagname")

    def children = rootTag.childNodes()

    def removeEmptyLines = {s -> s.replaceAll(/\n\s*\n/, "\n")}
    def paramExp = /\{\{\s*(\w*[.\w*]*)\s*\}\}/
    def extractVariables = {s -> (s =~ (paramExp)).collect { it[1]} }
    def escapeHtml = { StringEscapeUtils.escapeHtml("$it") }
    def attributesToFilter = ["data-if", "data-unless", "data-each", "data-key", "onclick"]
    def booleanAttributes = ["allowfullscreen", "async", "autofocus", "autoplay", "capture", "checked", "controls", "default", "defer", "disabled", "formnovalidate", "hidden", "itemscope", "loop", "multiple", "muted", "novalidate", "open", "readonly", "required", "reversed", "selected"]
    def stringifyObject
    stringifyObject = { result, fetchingLengthOfArray = false ->
      if(result == null) {
        ""
      } else if (result instanceof Map){
        "[object Object]"
      } else if (result instanceof List) {
        if(fetchingLengthOfArray){
          result.size()
        } else {
          result.collect({stringifyObject(it)}).join(',')
        }
      } else {
        result
      }
    }
    def getProperty = {object, String property, escape = true ->
      if(property == null) { return null }
      def fetchingLengthOfArray = property.endsWith(".length")
      def cleanedProperty = property.trim().replaceAll(/\.length$/,"")
      def result = cleanedProperty.tokenize('.').inject(object, {obj, prop -> obj?."$prop" })

      if(escape){
        stringifyObject(result, fetchingLengthOfArray)
      } else {
        result
      }

    }

    def compileStrWithData = { str, localData ->
      str?.replaceAll(paramExp, {p ->
        def paramName = extractVariables(p)[0]
        escapeHtml(getProperty(localData, paramName))
      })
    }

    def escapeAttributes = { child ->
      child.attributes().collectEntries {
        def value = compileStrWithData(it.value, data)
        if(booleanAttributes.contains(it.key) && value == null){
          [it.key, "true"]
        } else {
          [it.key, value]
        }

      }.findAll {
        !(booleanAttributes.contains(it.key) && ["", "false"].contains(it.value))
      }
    }
    def containsIf = { escapedAttributes -> escapedAttributes?.find({ it.key == "data-if" }) }
    def containsUnless = { escapedAttributes -> escapedAttributes?.find({ it.key == "data-unless" })}
    def extractIfValue = { escapedAttributes, model ->
      def ifAttr = containsIf(escapedAttributes)
      getProperty(model, ifAttr?.value)
    }
    def extractUnlessValue = {escapedAttributes, model ->
      def unlessAttr = containsUnless(escapedAttributes)
      getProperty(model, unlessAttr?.value)
    }
    def buildComponent
    buildComponent = { mb, child, model ->

      if(child instanceof org.jsoup.nodes.Comment){
        mb.mkp.yield("")
      }else if (child instanceof org.jsoup.nodes.TextNode){
        def str = compileStrWithData(child.text(), model)
        mb.mkp.yieldUnescaped(removeEmptyLines(str))
      } else {
        def escapedAttributes = escapeAttributes(child)
        def eachAttr =  escapedAttributes?.find({ it.key == "data-each" })

        def ifAttr = containsIf(escapedAttributes)
        def unlessAttr = containsUnless(escapedAttributes)

        def filteredAttributes = escapedAttributes.findAll({ !attributesToFilter.contains(it.key)})

        if(eachAttr){
          def eachAttrValue = eachAttr?.value
          def listPath = (eachAttrValue =~ /\w* in (\w*[\.\w*]*)/)[0][1]
          def iterVar = (eachAttrValue =~ /(\w*) in \w*[\.\w*]*/)[0][1]

          def list = getProperty(model, listPath, false)
          if (list instanceof List) {
            list.collect {
              def localModel =  model + [:]
              localModel.put(iterVar, it)

              def ifAttrValue = extractIfValue(escapedAttributes, localModel)
              def unlessAttrValue = extractUnlessValue(escapedAttributes, localModel)

              if((ifAttr == null|| ifAttrValue) && (unlessAttr == null || !unlessAttrValue)){
                mb."${child.tagName()}"(filteredAttributes){
                  child.childNodes().collect {child2 ->
                    buildComponent(mb, child2, localModel)
                  }
                }
              }
            }
          }

        } else {
          def ifAttrValue = extractIfValue(escapedAttributes, model)
          def unlessAttrValue = extractUnlessValue(escapedAttributes, model)
          if((ifAttr == null|| ifAttrValue) && (unlessAttr == null || !unlessAttrValue)){
            mb."${child.tagName()}"(filteredAttributes){
              child.childNodes().collect {child2 ->
                buildComponent(mb, child2, model)
              }
            }
          }
        }
      }
    }

    def mb = new MarkupBuilder(printer)
    mb.escapeAttributes = false
    mb.doubleQuotes = true

    mb."$tag"('') {
     children.collect { child ->
        buildComponent(mb, child, data)
     }
  }

   def html = writer.toString().trim()
   containsComments ? "<!DOCTYPE html>\n$html" : html
  }

}

@Grapes(
    @Grab(group='commons-lang', module='commons-lang', version='2.6')
)


import groovy.util.XmlSlurper
import groovy.xml.MarkupBuilder
import java.io.StringWriter
import groovy.util.IndentPrinter
import org.apache.commons.lang.StringEscapeUtils
import groovy.json.JsonSlurper

class Compiler{
  def static  compile(file, data){
    data = data instanceof String ? new JsonSlurper().parseText(data) : data
    def writer = new StringWriter()
    def printer = new IndentPrinter(writer, "    ", true, false)
    def template = new XmlParser().parseText(file)
    def containsComments = file.contains("<!--")
    def tag = template."@data-tagname"
    def children = template.'*'
    def removeEmptyLines = {s -> s.replaceAll(/\n\s*\n/, "\n")}
    def paramExp = /\{\{\s*(\w*[.\w*]*)\s*\}\}/
    def extractVariables = {s -> (s =~ (paramExp)).collect { it[1]} }
    def escapeHtml = { StringEscapeUtils.escapeHtml("$it") }
    def attributesToFilter = ["data-if", "data-unless", "data-each"]
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
    def alwaysAutoCLosingElements = [ "area",  "base",  "br",  "col",  "command",  "embed",  "hr",  "img",  "input",  "keygen",  "link",  "menuitem",  "meta",  "param",  "source",  "track",  "wbr"]

    def compileStrWithData = { str, localData ->
      str.replaceAll(paramExp, {p ->
        def paramName = extractVariables(p)[0]
        escapeHtml(getProperty(localData, paramName))
      })
    }

    def escapeAttributes = { child ->
      child.attributes().collectEntries {
        [it.key, compileStrWithData(it.value, data)]
      }
    }

    def buildComponent
    buildComponent = { mb, child, model ->

      if (child instanceof String){
        def str = compileStrWithData(child, model)
        mb.mkp.yieldUnescaped(str)
      } else if (child.'*'[0] instanceof groovy.util.Node){
        def escapedAttributes = escapeAttributes(child)
        def filteredAttributes = escapedAttributes.findAll({ !attributesToFilter.contains(it.key)})
        def eachAttr =  escapedAttributes?.find({ it.key == "data-each" })

        if(eachAttr){
          def eachAttrValue = eachAttr?.value
          def listPath = (eachAttrValue =~ /\w* in (\w*[\.\w*]*)/)[0][1]
          def iterVar = (eachAttrValue =~ /(\w*) in \w*[\.\w*]*/)[0][1]

          def list = getProperty(model, listPath, false)

          list.collect {
            def localModel =  model + [:]
            localModel.put(iterVar, it)
            mb."${child.name()}"(filteredAttributes){
              child.children().collect {child2 ->
                buildComponent(mb, child2, localModel)
              }
            }
          }
        } else {
          mb."${child.name()}"(filteredAttributes){
            child.children().collect {child2 ->
              buildComponent(mb, child2, model)
            }
          }
        }


      } else { //child is a groovy.util.Node but without children
        def escapedAttributes = escapeAttributes(child)
        def ifAttr = escapedAttributes?.find({ it.key == "data-if" })
        def ifAttrValue = getProperty(model, ifAttr?.value)

        def unlessAttr = escapedAttributes?.find({ it.key == "data-unless" })
        def unlessAttrValue = getProperty(model, unlessAttr?.value)

        def filteredAttributes = escapedAttributes.findAll({ !attributesToFilter.contains(it.key)})

        if((ifAttr == null|| ifAttrValue) && (unlessAttr == null || !unlessAttrValue)){
          if(alwaysAutoCLosingElements.contains(child.name())){
            mb."${child.name()}"(filteredAttributes){}
          } else {
            mb."${child.name()}"(removeEmptyLines(compileStrWithData(child.text(), model)), filteredAttributes){}
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

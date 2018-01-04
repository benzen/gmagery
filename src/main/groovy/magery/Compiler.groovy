package org.magery

import org.magery.AST.Each
import org.magery.AST.EmbeddedData
import org.magery.AST.ConditionalDataEmbed
import org.magery.AST.If
import org.magery.AST.Raw
import org.magery.AST.Template
import org.magery.AST.TemplateCall
import org.magery.AST.TemplateChildren
import org.magery.AST.Unless
import org.magery.AST.Variable
import org.magery.AST.TemplateEmbed
import groovy.json.JsonSlurper
import groovy.util.IndentPrinter
import groovy.xml.MarkupBuilder
import java.io.StringWriter
import org.apache.commons.lang.StringEscapeUtils
import org.jsoup.Jsoup



class Compiler{
  def static ignoredAttributes = ["data-tagname", "data-if", "data-unless", "data-each", "data-key"]
  def static booleanAttributes = ["allowfullscreen", "async", "autofocus", "autoplay", "capture", "controls", "checked", "default", "defer", "disabled", "formnovalidate", "open", "readonly", "hidden", "itemscope", "loop", "muted", "multiple", "novalidate", "open", "required", "reversed", "selected"]
  def static selfClosingTags = ["area", "base", "br", "col", "embed", "hr", "img", "input", "keygen", "link", "menuitem", "meta", "param", "source", "track", "wbr"]

  def static compileNode(node, output, queue, isRoot){
    if(node instanceof org.jsoup.nodes.TextNode){
      compileTextNode(node, output)
    } else if (node instanceof org.jsoup.nodes.Comment || node instanceof org.jsoup.nodes.DocumentType){
    } else if(node instanceof org.jsoup.select.Elements){
      node.each { compileNode(it, output, queue, isRoot)}
    } else if (node  instanceof org.jsoup.nodes.Element) {
      compileElement(node, output, queue, isRoot)
    } else if(node instanceof Collection){
      node.each {
        compileNode(it, output, queue, isRoot)
      }
    } else{
      throw new Exception(" Unhandeled node type ${node.class} ${node.tagName}")
    }

  }

  //XXX This may not be the best way to do this
  def static compileVariables(str, output){
    def start = 0
    def end = 0
    def isText = true
    def chunk = ""
    while (start < str?.size()){
      if(isText){
          end = str.indexOf("{{", start)
          end = end == -1 ? str.size() : end
          chunk = str.substring(start, end).replace("}}", "")
          output.push(new Raw(StringEscapeUtils.escapeHtml(chunk)))
      }else {
          end = str.indexOf("}}", start)
          end = end == -1 ? str.size() : end
          chunk = str.substring(start, end).replace("{{", "")
          output.push(new Variable(chunk.trim().tokenize(".")))
      }
      isText = !isText
      start = end
    }
  }
  def static compileTextNode(node, output){
    // println "#$node.wholeText#"
    def text = node.wholeText
    compileVariables(text, output)
  }
  def static compileElement(node, output, queue, isRoot){
    def tagName = node.tagName().toLowerCase()
    def isComponent = false
    if(tagName == "template"){
      if(!isRoot){
        queue.push(node)
        return
      }
      isComponent = true
      tagName = node.attr("data-tagname").toLowerCase()
    }

    if(tagName == "template-children"){
      output.push(new TemplateChildren())
      return
    }
    else if ( tagName == "template-embed"){
      output.push(new TemplateEmbed(node.attr("template")))
      return
    }
    if(node.hasAttr("data-each")){
      def value = node.attr("data-each")
      def parts = value.split(" in ")
      def name = parts[0]
      def path = parts[1].trim().tokenize(".")
      def eachOutput = new Each(name, path)
      output.push(eachOutput)
      output = eachOutput
    }
    if(node.hasAttr("data-if")){
      def value = node.attr("data-if")
      def path = value.trim().tokenize(".")
      def ifOutput = new If(path)
      output.push(ifOutput)
      output = ifOutput
    }
    if(node.hasAttr("data-unless")){
      def value = node.attr("data-unless")
      def path = value.trim().tokenize(".")
      def unlessOutput = new Unless(path)
      output.push(unlessOutput)
      output = unlessOutput
    }

    if(node.tagName().contains("-")){
        def context = [:]
        node.attributes().each {
          if (! (ignoredAttributes.contains(it.key) || it.key.substring(0, 2) == "on" || it.key == "data-embed")){

            context."$it.key" = []
            compileVariables(it.value, context."$it.key")
          }
        }
      def templateName = [new Raw(node.tagName().toLowerCase())]
      def embedData = node.attr("data-embed") == "true"

      if(tagName == "template-call"){
        def templateRaw = node.attr("template")
        templateName = []
        compileVariables(templateRaw, templateName)
      }
      def templateOutput = new TemplateCall(templateName, context, embedData)
      output.push(templateOutput)
      node.childNodes().each { childNode ->
        compileNode(childNode, templateOutput, queue, false)
      }
      return
    }

    output.push(new Raw("<$tagName"))

    node.attributes()
    .grep({!ignoredAttributes.contains(it.key)})
    .grep({ it.key.indexOf("on") != 0})
    .each({
      if(booleanAttributes.contains(it.key)){
        if(it.value ==~ /\{\{.*\}\}/){
          def rawPath = it.value.substring(2, it.value.size() - 2).trim()
          if(rawPath){
            def path = rawPath.tokenize(".")
            def ifOutput = new If(path)
            ifOutput.push(new Raw(" ${Runtime.escapeHtml(it.key)}"))
            output.push(ifOutput)
            return
          }
        }
        output.push(new Raw(" ${Runtime.escapeHtml(it.key)}"))
      } else if (it.key == "data-embed"){
            output.push(new Raw(" data-context=\""))
            output.push(new EmbeddedData())
            output.push(new Raw("\""))
        } else {
        output.push(new Raw(" ${Runtime.escapeHtml(it.key)}=\""))
        compileVariables(it.value, output)
        output.push(new Raw("\""))
      }
    })
    if(isComponent ){
      output.push(new ConditionalDataEmbed())
    }

    output.push(new Raw(">"))
    if(!selfClosingTags.contains(tagName) ){
      node.childNodes().each {
        compileNode(it, output, queue, false)

      }
      output.push(new Raw("</$tagName>"))
    }

  }

  def static compileTree(tree, output){
    def queue = []
    compileNode(tree, output, queue, false)
    while(queue.size() > 0){
      def node = queue.first()
      queue = queue.minus(node)
      def template = new Template(node.attr("data-tagname"), outerHtml(node))
      compileNode(node, template, queue, true)
      output.push(template)

    }
  }

  def static writeNode(node, output){
    output.push(node.toString())
  }
  def static outerHtml(node){
    def results = []
    writeNode(node, results)
    results.join("").toString()
  }

  def static compileFile(fileName, output){
    def str = new File(fileName).text
    def tree = Jsoup.parseBodyFragment(str).body().children()
    compileTree(tree, output)
  }

  def static compileToString(fileName){
    def output = []
    def results = []
    compileFile(fileName, output)
    output.each({it.toGroovy(results)})
    results.join("").toString()
  }

  def static compileTemplates(fileName, templates = [:]){
    def src = compileToString(fileName)
    def binding = new Binding([templates: templates, runtime: Runtime])
    def gs = new GroovyShell(binding)
    gs.evaluate(src)
    templates
  }

}

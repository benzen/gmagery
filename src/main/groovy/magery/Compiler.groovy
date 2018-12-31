package org.magery

import org.magery.AST.Each
import org.magery.AST.EmbeddedData
import org.magery.AST.ConditionalDataEmbed
import org.magery.AST.If
import org.magery.AST.Raw
import org.magery.AST.Template
import org.magery.AST.TemplateCall
import org.magery.AST.TemplateChildren
import org.magery.AST.Comment
import org.magery.AST.Unless
import org.magery.AST.Variable
import org.magery.AST.Attributes
import org.magery.AST.TemplateEmbed
import org.apache.commons.lang3.StringEscapeUtils
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.nodes.Document
import org.jsoup.parser.ParseSettings

class Compiler{
  def static compileNode(node, output, queue, isRoot){
    if(node instanceof org.jsoup.nodes.TextNode){
      compileTextNode(node, output)
    } else if (node instanceof org.jsoup.nodes.Comment){
      compileComment(node, output)
    } else if(node instanceof org.jsoup.select.Elements){
      node.each { compileNode(it, output, queue, isRoot)}
    } else if (node  instanceof org.jsoup.nodes.Element) {
      compileElement(node, output, queue, isRoot)
    } else{
      throw new Exception("Unhandeled node type ${node.class} ${node.tagName}")
    }
  }

  def static compileComment(node, output){
    output.push(new Comment(node.data))
  }
  def static compileVariables(text){
    def compileVariablesRec
    compileVariablesRec = {str, isText, acc ->
      if(str.size() == 0){ return acc.flatten() }
      if(isText){
        def end = str.indexOf("{{")
        end = end == -1 ? str.size() : end
        def chunk = str.substring(0, end).replace("}}", "")
        return compileVariablesRec(str.substring(end, str.size()), !isText, [acc, new Raw(StringEscapeUtils.escapeXml(chunk))])
      } else {
        def end = str.indexOf("}}")
        end = end == -1 ? str.size() : end
        def chunk = str.substring(0, end).replace("{{", "")
        return compileVariablesRec(str.substring(end, str.size()), !isText, [acc, new Variable(chunk.trim().tokenize("."))])
      }
    }
    compileVariablesRec(text, true, [])
  }


  def static compileTextNode(node, output){
    def text = node.wholeText
    def vOuput = compileVariables(text)
    vOuput.each {
      output.push(it)
    }
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
      def eachOutput = new Each(node.attr("data-each"))
      output.push(eachOutput)
      output = eachOutput
    }
    if(node.hasAttr("data-if")){
      def ifOutput = new If(node.attr("data-if"))
      output.push(ifOutput)
      output = ifOutput
    }
    if(node.hasAttr("data-unless")){
      def unlessOutput = new Unless(node.attr("data-unless"))
      output.push(unlessOutput)
      output = unlessOutput
    }

    if(node.tagName().contains("-")){

      def context = attributesToContext(node.attributes())
      def templateName = [new Raw(tagName)]
      if(tagName == "template-call"){
        def templateRaw = node.attr("template")
        templateName = compileVariables(templateRaw)
      }
      def embedData = node.attr("data-embed") == "true"
      def templateOutput = new TemplateCall(templateName, context, embedData)
      output.push(templateOutput)
      node.childNodes().each { childNode ->
        compileNode(childNode, templateOutput, queue, false)
      }
      return
    }

    output.push(new Raw("<$tagName"))
    output.push(new Attributes(node.attributes()))

    if(isComponent &&  node.attr("data-embed") != "true"){
      output.push(new ConditionalDataEmbed())
    }

    output.push(new Raw(">"))
    if(!Html.SELF_CLOSING_TAGS.contains(tagName) ){
      node.childNodes().each {
        compileNode(it, output, queue, false)

      }
      output.push(new Raw("</$tagName>"))
    }

  }
  static def attributesToContext(attributes){
    def context = attributes
     .findAll({!Html.IGNORED_ATTRIBUTES.contains(it.key)})
     .findAll({it.key.substring(0, 2) != "on"})
     .findAll({it.key != "data-embed"})
     .collectEntries {
       ["$it.key", compileVariables(it.value)]
     }
    context.each({
      if(containsUpperCase(it.key)){
        throw new Exception("Attribute \"$it.key\" is illegal for an attribute, use dashed-case instead of camel case.")
      }
    })
    context
  }

  static boolean containsUpperCase(str){
    def upperCasedChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("")
    upperCasedChar.any({ str.contains(it) })
  }

  def static compileTree(tree, output){
    def queue = []
    compileNode(tree, output, queue, false)
    while(queue.size() > 0){
      def node = queue.first()
      queue = queue.minus(node)
      def templateName = node.attr("data-tagname")
      def template = new Template(templateName, outerHtml(node))
      compileNode(node, template, queue, true)
      output.push(template)
    }
  }

  def static writeNode(node){
    node.toString()
  }
  def static outerHtml(node){
    writeNode(node)
  }

  def static compileFile(fileName, output){
    def file = getClass().getResource(fileName).file
    def str = new File(file).text
    Checker.checkAll(fileName, str)
    Parser parser = Parser.htmlParser()
    parser.settings(new ParseSettings(true, true)) // tag, attribute preserve case
    def tree = parser.parseInput(str, "").body().children()

    compileTree(tree, output)
  }

  def static compileToString(fileName){
    def output = []
    compileFile(fileName, output)

    output
    .collect({it.toGroovy()})
    .flatten()
    .join("")
    .toString()
  }

  def static compileTemplates(fileName, templates = [:]){
    def src = compileToString(fileName)
    def binding = new Binding([templates: templates, runtime: Runtime])
    def gs = new GroovyShell(binding)
    gs.evaluate(src)
    templates
  }

}

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

  def static compileNode(node, isRoot) {
    if (node instanceof org.jsoup.select.Elements){
      node.collect({ compileNode(it, isRoot) })
    } else if (node instanceof org.jsoup.nodes.Element){
      compileElment(node, isRoot)
    } else if (node instanceof org.jsoup.nodes.TextNode) {
      compileText(node)
    } else if (node instanceof org.jsoup.nodes.Comment) {
      compileComment(node)
    } else {
      throw new RuntimeException("Unkown node type ${node.class}")
    }
  }
  def static compileText(node) {
    def text = node.wholeText
    compileVariables(text)
  }

  def static compileComment(node) {
    [new Comment(node.data)]
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

  def static compileElment(node, isRoot){
    def tagname = node.tagName()  == "template" ?  node.attr("data-tagname") : node.tagName()
    def compileAll =
      compileUnless(node, tagname) >>
      compileIf(node, tagname) >>
      compileEach(node, tagname) >>
      compileTemplate(node, tagname, isRoot) >>
      compileTemplateEmbed(node, tagname) >>
      compileTemplateChildren(node, tagname)

    compileAll node.tagName().contains("-") ? compileTemplateCall(node, tagname, isRoot) : compileHtml(node, tagname, isRoot)
  }

  def static compileTemplateChildren(node, tagname){
    { children ->
      if(tagname != "template-children") {return children}
      [new TemplateChildren()]
    }
  }

  def static compileTemplateEmbed(node, tagname){
    { children ->
      if(tagname != "template-embed"){ return children }
      [new TemplateEmbed(node.attr("template"))]
    }
  }

  def static compileTemplate(node, tagname, isRoot){
    { children ->
      if(node.tagName() != "template"){ return children }
      if(!isRoot){

        [children, compileNode(node, true)]
      }
      new Template(tagname, node.toString(), children)

    }
  }

  def static compileEach(node, tagname){
    { children ->
      if(!node.hasAttr("data-each")) { return children }
      new Each(node.attr("data-each"), children)
    }
  }

  def static compileIf(node, tagname){
    { children ->
      if(!node.hasAttr("data-if")) { return children }
      new If(node.attr("data-if"), children)
    }
  }

  def static compileUnless(node, tagname){
    { children ->
      if(!node.hasAttr("data-unless")) { return children }
      new Unless(node.attr("data-unless"), children)
    }
  }

  def static compileHtml(node, tagname, isRoot){
    [
      new Raw("<$tagname"),
      new Attributes(node.attributes()),
      (node.tagName() == "template" &&  node.attr("data-embed") != "true") ?  new ConditionalDataEmbed() : new Raw(""),
      new Raw(">"),
      Html.SELF_CLOSING_TAGS.contains(tagname) ? new Raw("") : [
        node.childNodes().collect({compileNode(it, false)}),
        new Raw("</$tagname>"),
        ]
    ].flatten()
  }

  def static compileTemplateCall(node, tagname, isRoot){

    def context = attributesToContext(node.attributes())
    def templateName = [new Raw(tagname)]

    if(node.tagName() == "template-call"){
      def templateRaw = node.attr("template")
      templateName = compileVariables(templateRaw)
    }

    def embedData = node.attr("data-embed") == "true"
    def template_node = new TemplateCall(templateName, context, embedData)
    template_node.children = node.childNodes().collect( { childNode -> compileNode(childNode, false) }).flatten()
    template_node
  }

  def static attributesToContext(attributes){
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
    str ==~ /.*[A-Z]+.+/
  }

  def static compileTree(tree){
    compileNode(tree, false).flatten()
  }

  def static compileFile(file){
    def str = file.text
    Checker.checkAll(file.path, str)
    Parser parser = Parser.htmlParser()
    parser.settings(new ParseSettings(true, true)) // tag, attribute preserve case
    def doc = parser.parse(str, "").outputSettings(new Document.OutputSettings().prettyPrint(false))
    def tree = doc.body().children()

    compileTree(tree)
  }

  def static compileToString(file){
    compileFile(file)
    .flatten()
    .collect({it.toGroovy()})
    .flatten()
    .join("")
    .toString()
  }

  def static compileTemplates(File file, templates = [:]){
    def src = compileToString(file)
    def binding = new Binding([templates: templates, runtime: Runtime])
    def gs = new GroovyShell(binding)
    gs.evaluate(src)
    templates
  }
}

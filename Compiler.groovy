@Grapes(
    [@Grab(group='commons-lang', module='commons-lang', version='2.6'),
    @Grab(group='org.jsoup', module='jsoup', version='1.11.2')]
)

import groovy.xml.MarkupBuilder
import java.io.StringWriter
import groovy.util.IndentPrinter
import org.apache.commons.lang.StringEscapeUtils
import groovy.json.JsonSlurper
import org.jsoup.Jsoup
import AST.Raw
import AST.Variable
import AST.Template
import AST.If
import AST.Unless
import AST.Each
import org.apache.commons.lang.StringEscapeUtils


class Compiler{
  def static ignoredAttributes = ["data-tagname", "data-if", "data-unless", "data-each", "data-key"]

  def static compileNode(node, output, queue, isRoot){
    if(node instanceof org.jsoup.nodes.TextNode){
      compileTextNode(node, output)
    } else if (node instanceof org.jsoup.nodes.Comment){
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
        while (start < str.size()){

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
    compileVariables(node.text(), output)
  }
  def static compileElement(node, output, queue, isRoot){
    def tagName = node.tagName().toLowerCase()

    if(tagName == "template"){
      if(!isRoot){
        queue.push(node)
        return
      }
      def isComponent = false
      tagName = node.attr("data-tagname").toLowerCase()
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
    output.push(new Raw("<$tagName"))
    node.attributes
    .grep({!ignoredAttributes.contains(it.key)})
    .grep({ it.key.indexOf("on") != 0})
    .each({
      output.push(new Raw(" ${Runtime.escapeHtml(it.key)}="))
      compileVariables(it.value, output)
    })
    output.push(new Raw(">"))
    node.childNodes().each {
      compileNode(it, output, queue, false)
    }
    output.push(new Raw("</$tagName>"))

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
    def str = new File(fileName).text.trim()
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

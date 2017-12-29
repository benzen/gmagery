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
import org.apache.commons.lang.StringEscapeUtils


class Compiler{
  def static ignoredAttributes = ["data-tagname"]

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

  def static compileVariables(str, output){
    def paramExp = /(.*)\{\{|(.*)\}\}|(.*)/
    def parts = str =~ paramExp
    parts.each { match ->
      def beforeVar = match[1]
      def inVar = match[2]
      def outsideVar = match[3]
      if(beforeVar) {
        output.push(new Raw(StringEscapeUtils.escapeHtml(beforeVar.replace("{{", ""))))
      } else if(outsideVar){
        output.push(new Raw(StringEscapeUtils.escapeHtml(outsideVar)))
      } else if (inVar){
        output.push(new Variable(inVar.replace("}}", "").trim().tokenize(".")))
      }
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
    output.push(new Raw("<$tagName"))
    node.attributes.grep({!ignoredAttributes.contains(it.key)}).each({
      output.push(new Raw(" $it.key=\"$it.value\""))
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

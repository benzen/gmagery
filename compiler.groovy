// https://mvnrepository.com/artifact/commons-lang/commons-lang
@Grapes(
    @Grab(group='commons-lang', module='commons-lang', version='2.6')
)


import groovy.util.XmlSlurper
import groovy.xml.MarkupBuilder
import java.io.StringWriter
import groovy.util.IndentPrinter
import org.apache.commons.lang.StringEscapeUtils
import groovy.json.JsonSlurper


def compile(file, data){
  data = data instanceof String ? new JsonSlurper().parseText(data) : data
  def writer = new StringWriter()
  def printer = new IndentPrinter(writer, "    ")
  def template = new XmlParser().parseText(file)
  def containsComments = file.contains("<!--")
  def tag = template."@data-tagname"
  def children = template.'*'
  def removeEmptyLines = {s -> s.replaceAll(/\n\s*\n/, "\n")}
  def paramExp = /\{\{(\w*)}\}/
  def extractVariables = {s -> (s =~ (paramExp)).collect { it[1]} }
  def escapeHtml = { StringEscapeUtils.escapeHtml(it) }

  def buildComponent
  buildComponent = { mb, child ->
    if (child instanceof String){
      def str = child.replaceAll(paramExp, {p ->
        def paramName = extractVariables(p)[0]
          def value = data[paramName]
          escapeHtml(value)
      })
      mb.mkp.yieldUnescaped(str)
    } else if (child.'*'[0] instanceof groovy.util.Node){
      mb."${child.name()}"{
        child.children().collect {child2 ->
          buildComponent(mb, child2)
        }
      }
    } else {
      mb."${child.name()}"(removeEmptyLines(child.text())){}
    }
  }

  def mb = new MarkupBuilder(printer)
  mb."$tag"('') {
   children.collect { child ->
      buildComponent(mb, child)
   }
}

 def html = writer.toString().trim()
 containsComments ? "<!DOCTYPE html>\n$html" : html
}
def ifThenElse = {pred, ifBranch, elseBranch ->}
def tests = [
   "0001-empty-template",
   "0002-flat-children",
   "0003-nested-children",
   "0004-html-comments",
   "0101-escape-text"
]
.collect {
  [
    test: it,
    template: new File("magery-tests/components/$it/template.html").text.trim(),
    expected: new File("magery-tests/components/$it/expected.html").text.trim(),
    data: new File("magery-tests/components/$it/data.json").text.trim()
  ]
}
.each {
  println "Testing $it.test"
    def compiledTemplate = compile(it.template, it.data)
    def expected = it.expected

    // println compiledTemplate
    // println expected

    assert  compiledTemplate ==  expected


}

// Hello, &lt;script&gt;alert(&quot;TEST&quot;);&lt;/script&gt;!
// Hello, &lt;script&gt;alert("TEST");&lt;/script&gt;!

import groovy.util.XmlSlurper
import groovy.xml.MarkupBuilder
import java.io.StringWriter
import groovy.util.IndentPrinter



def compile(file, data){
  def writer = new StringWriter()
  def printer = new IndentPrinter(writer, "    ")
  def template = new XmlParser().parseText(file)
  def tag = template."@data-tagname"
  def children = template.'*'
  def buildComponent
  buildComponent = { mbp, child ->
    if (child.'*'[0] instanceof groovy.util.Node){
      mbp."${child.name()}"{
        child.children().collect {child2 ->
          buildComponent(mbp, child2)
        }
      }
    } else {
      mbp."${child.name()}"(child.text()){}
    }


  }

  def mbp = new MarkupBuilder(printer)
  mbp."$tag"('') {
   children.collect { child ->
      buildComponent(mbp, child)
   }
}
 writer.toString().trim()
}
def ifThenElse = {pred, ifBranch, elseBranch ->}
def tests = [
   "0001-empty-template",
   "0002-flat-children",
   "0003-nested-children"
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


import Compiler

def normalizeXmlString = { str ->

  new XmlSlurper(false, false, true).parseText(str).toString()
}
def tests = [
   "0001-empty-template",
   "0002-flat-children",
   "0003-nested-children",
   "0004-html-comments",
   "0101-escape-text",
   "0102-escape-attribute",
   "0103-existing-nested-property",
   "0104-missing-nested-property",
   "0105-underscore-variable-in-attribute",
   "0106-underscore-variable-in-text",
   "0107-variable-substitution-text",
   "0108-variable-substitution-number",
   "0109-variable-substitution-true",
   "0110-variable-substitution-false",
   "0112-variable-substitution-undefined",
   "0113-variable-substitution-object",
   "0114-variable-substitution-array",
   "0115-variable-substitution-length-property",
   "0116-stringify-array-items",
   "0201-data-if-true",
   "0202-data-if-false",
   "0203-data-if-undefined",
   "0204-data-if-zero",
   "0205-data-if-empty-string",
   "0206-data-if-empty-array",
   // "0207-underscore-variable-in-data-if",
   "0301-data-unless-true",
   "0302-data-unless-false",
   "0303-data-unless-undefined",
   "0304-data-unless-zero",
   "0305-data-unless-empty-string",
   "0306-data-unless-empty-array",
   "0401-data-each"
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

    def compiledTemplate =  Compiler.compile(it.template, it.data)
    def expected = it.expected

    assert  compiledTemplate ==  expected


}

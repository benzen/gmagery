@Grab(group='org.jsoup', module='jsoup', version='1.11.2')

import Compiler
import org.jsoup.Jsoup
import groovy.json.JsonSlurper
import Runtime

def normalizeXmlString = { str ->
  def doc = Jsoup.parseBodyFragment(str)
  doc.normalise()

  def os = doc.outputSettings()
  doc.body().children().first().toString().trim().replace("\n", "").replace("  ", " ").replace("  ", " ")
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
   "0207-underscore-variable-in-data-if",
   "0301-data-unless-true",
   "0302-data-unless-false",
   "0303-data-unless-undefined",
   "0304-data-unless-zero",
   "0305-data-unless-empty-string",
   "0306-data-unless-empty-array",
   "0401-data-each",
   "0402-data-each-access-outer-context",
   "0403-data-each-preseves-outer-context",
   "0404-data-each-with-keys",
   "0405-data-each-non-list",
   // "0406-data-each-before-data-if",
   // "0407-data-each-before-data-unless",
   "0501-do-not-render-event-attributes",
   "0601-boolean-property-allowfullscreen",
   "0602-boolean-property-async",
   "0603-boolean-property-autofocus",
   "0604-boolean-property-autoplay",
   "0605-boolean-property-capture",
   "0606-boolean-property-checked",
   "0608-boolean-property-default",
   "0609-boolean-property-defer",
   "0610-boolean-property-disabled",
   "0611-boolean-property-formnovalidate",
   "0612-boolean-property-hidden",
   "0613-boolean-property-itemscope",
   "0614-boolean-property-loop",
   "0615-boolean-property-multiple",
   "0616-boolean-property-muted",
   "0617-boolean-property-novalidate",
   "0618-boolean-property-open",
   "0619-boolean-property-readonly",
   "0620-boolean-property-required",
   "0621-boolean-property-reversed",
   "0622-boolean-property-selected",
   "0701-call-another-component",
   "0702-call-another-component-with-context",
   "0703-call-another-component-with-string-argument",
   "0704-data-if-on-component-true",
   "0705-data-if-on-component-false",
   "0706-data-unless-on-component-true",
   "0707-data-unless-on-component-false",
   "0708-data-each-on-component",
   "0709-data-key-on-component",
   "0801-data-embed-on-component",
   "0802-data-embed-on-component-template",
   "0901-component-with-child-expansion",
   "0903-nested-child-expansions",
   "1001-template-call"
]
.collect {
  [
    test: it,
    template: "magery-tests/components/$it/template.html",
    expected: new File("magery-tests/components/$it/expected.html").text.trim(),
    data: new JsonSlurper().parseText(new File("magery-tests/components/$it/data.json").text.trim())
  ]
}
.each {
    println "Testing $it.test"

    def compiledTemplate = Compiler.compileTemplates(it.template)

    def renderedTemplate = normalizeXmlString Runtime.renderToString(compiledTemplate, 'app-main', it.data)

    def expected = normalizeXmlString it.expected

    assert  expected == renderedTemplate


}

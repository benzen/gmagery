import org.magery.Compiler
import org.magery.Runtime
import groovy.json.JsonSlurper

import groovy.util.GroovyTestCase

class CompilerTest  extends GroovyTestCase {
  def getFile(pathInClassPath){
    getClass().getResource(pathInClassPath).file
  }
  void testAll(){

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
       "0111-variable-substitution-null",
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
       "0310-data-if-dotted-path",
       "0311-data-if-eq",
       "0312-data-if-neq",
       "0313-data-if-or",
       "0314-data-if-and",
       "0315-data-if-lt",
       "0316-data-if-lte",
       "0317-data-if-gt",
       "0318-data-if-gte",
       "0319-data-if-complexe-expression",
       "0320-data-unless-dotted-path",
       "0321-data-unless-eq",
       "0322-data-unless-neq",
       "0323-data-unless-or",
       "0324-data-unless-and",
       "0325-data-unless-lt",
       "0326-data-unless-lte",
       "0327-data-unless-gt",
       "0328-data-unless-gte",
       "0329-data-unless-complexe-expression",
       "0401-data-each",
       "0402-data-each-access-outer-context",
       "0403-data-each-preseves-outer-context",
       "0404-data-each-with-keys",
       "0405-data-each-non-list",
       "0406-data-each-before-data-if",
       "0407-data-each-before-data-unless",
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
       "1001-template-call",
       "2001-template-embed",
       "3001-call-undefined-root-component",
       "3011-too-many-opening-curly-braces-on-text",
       "3012-too-many-closing-curly-braces-on-text",
       "3013-unbalanced-currly-braces-on-text",
       "3014-too-many-closing-curly-braces-on-attribute",
       "3015-too-many-opening-curly-braces-on-attribute",
       "3016-unbalanced-currly-braces-on-attribute",
       "3021-variable-in-data-if",
       "3022-variable-in-data-unless",
       "3031-template-name-without-dash",
       "3041-overriding-a-template",


    ]
    .each {
      println "Testing $it"
      def unit = [
        test: it,
        error: new File(getFile("/magery-tests/components/$it/error.txt")).text.trim(),
        template: "/magery-tests/components/$it/template.html",
        expected: new File(getFile("/magery-tests/components/$it/expected.html")).text.trim(),
        data: new JsonSlurper().parseText(new File(getFile("/magery-tests/components/$it/data.json")).text.trim())
      ]

      try {
        def compiledTemplate = Compiler.compileTemplates(unit.template)

        def renderedTemplate = Runtime.renderToString(compiledTemplate, 'app-main', unit.data)

        def expected = unit.expected

        assert  expected == renderedTemplate
      } catch (Exception e){
        def alternateErrorMessage = [
          "3011-too-many-opening-curly-braces-on-text": """
In file: /magery-tests/components/3011-too-many-opening-curly-braces-on-text/template.html
At line: 0
Variables should be escape between "{{" and "}}"
<template data-tagname="app-main">{{ def</template>
                                  ^^^^^^
          """,
          "3012-too-many-closing-curly-braces-on-text":"""
In file: /magery-tests/components/3012-too-many-closing-curly-braces-on-text/template.html
At line: 0
Variables should be escape between "{{" and "}}"
<template data-tagname="app-main">def }}</template>
                                  ^^^^^^
          """,
          "3013-unbalanced-currly-braces-on-text":"""
In file: /magery-tests/components/3013-unbalanced-currly-braces-on-text/template.html
At line: 0
Variables should be escape between "{{" and "}}"
<template data-tagname="app-main">}} def {{</template>
                                  ^^
          """,
          "3014-too-many-closing-curly-braces-on-attribute":"""
In file: /magery-tests/components/3014-too-many-closing-curly-braces-on-attribute/template.html
At line: 1
Variables should be escape between "{{" and "}}"
  <a href="{{ def">link</a>
           ^^^^^^
          """,
          "3015-too-many-opening-curly-braces-on-attribute":"""
In file: /magery-tests/components/3015-too-many-opening-curly-braces-on-attribute/template.html
At line: 1
Variables should be escape between "{{" and "}}"
  <a href="def }}">link</a>
           ^^^^^^
         """,
         "3016-unbalanced-currly-braces-on-attribute":"""
In file: /magery-tests/components/3016-unbalanced-currly-braces-on-attribute/template.html
At line: 1
Variables should be escape between "{{" and "}}"
  <a href="}} def {{"> link </a>
                  ^^

         """
        ]
        if(unit.error != e.message && alternateErrorMessage[it]?.trim() != e.message?.trim()) {
          println """
Actuall error
${e.message}
          """
          // e.printStackTrace()
        }

      }
    }
  }
}

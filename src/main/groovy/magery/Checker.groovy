package org.magery

class Checker{
  static def checkAll(fileName, str){
    [
      [exp: /data-if\s*=\s*"(\{\{.*\}\})"/,               message: "Value for attribute data-if must not contains \"{{\" or \"}}\""],
      [exp: /data-unless="(\{\{.*\}\})"/,                 message: "Value for attribute data-unless must not contains \"{{\" or \"}}\""],
      [exp: /<\s*template\sdata-tagname\s*=\s*"([^-]*)"/, message: "Template name \"app\" is incorrect, it's mandatory that template name include a \"-\" character"],
      [exp: /[>]([^\{<]*\}\})/,                           message: "Variables should be escape between \"{{\" and \"}}\""],
      [exp: /(\{\{[^\}]*)\"/,                             message: "Variables should be escape between \"{{\" and \"}}\""],
      [exp: /(\{\{[^\}]*)[<]/,                            message: "Variables should be escape between \"{{\" and \"}}\""],
      [exp: /\"([^\{<]*\}\})/,                            message: "Variables should be escape between \"{{\" and \"}}\""],
      [exp: /(\{\{[^\}]*^)/,                               message: "Variables should be escape between \"{{\" and \"}}\""],

    ].each {
      check(fileName, str, it.exp, it.message)
    }
  }

  static def check(fileName, str, exp, message){
    def lineIndex = 0
    str.eachLine { line ->
      def expMatcher = line =~ exp
      if(expMatcher){
        def matchResult = expMatcher[0][1]
        def start = line.indexOf(matchResult)
        def size = matchResult.size()

        // matchResult = expMatcher.toMatchResult()
        def underline = "${" ".multiply(start)}${"^".multiply(size)}"
        def msg = """
In file: ${fileName}
At line: $lineIndex
$message
$line
$underline
        """.trim()
        throw new Exception(msg)
      }
      lineIndex =+ 1
    }
  }
}

package org.magery

class Checker{
  static def checkAll(fileName, str){
    [
      [exp: /\{\{[^\}]*$/, message: "Variables should be escape between \"{{\" and \"}}\""],
      [exp: /^[^\{]*\}\}/, message: "Variables should be escape between \"{{\" and \"}}\""],
      [exp: /data-if\s*=\s*"\{\{.*\}\}"/, message: "Value for attribute data-if must not contains \"{{\" or \"}}\""],
      [exp: /data-unless="\{\{.*\}\}"/, message: "Value for attribute data-unless must not contains \"{{\" or \"}}\""],
      [exp: /<\s*template\sdata-tagname\s*=\s*"[^-]*"/, message: "Template name \"app\" is incorrect, it's mandatory that template name include a \"-\" character"],
    ].each {
      check(fileName, str, it.exp, it.message)
    }
  }

  static def check(fileName, str, exp, message){
    def lineIndex = 0
    str.eachLine { line ->
      def expMatcher = line =~ exp
      def carrets = ""
      def spaces = ""
      def matchResult
      if(expMatcher){
        matchResult = expMatcher.toMatchResult()
        if(matchResult.start() > 0){
          carrets = (matchResult.start()..matchResult.end()-1).collect({"^"}).join("")
          spaces = (0..matchResult.start()-1).collect({" "}).join("")
        } else {
          carrets = (matchResult.start()..matchResult.end()-3).collect({"^"}).join("")
          spaces = (0..matchResult.start()+1).collect({" "}).join("")
        }

        def msg = """
In file: $fileName
At line: $lineIndex
$message
$line
$spaces$carrets
        """
        throw new Exception(msg)
      }
      lineIndex =+ 1
    }
  }
}

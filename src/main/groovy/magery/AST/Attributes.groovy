package org.magery.AST

import org.magery.Compiler
import org.magery.Runtime
import org.magery.Html

class Attributes{
  def attributes
  Attributes(attributes){
    this.attributes =
    attributes
    .grep({!Html.IGNORED_ATTRIBUTES.contains(it.key)})
    .grep({ it.key.indexOf("on") != 0})
    .collect({
      if(containsUpperCase(it.key)){
        throw new Exception("Attribute \"$it.key\" is illegal for an attribute, use dashed-case instead of camel case.")
      }
      if(Html.BOOLEAN_ATTRIBUTES.contains(it.key)){
        if(it.value ==~ /\{\{.*\}\}/){
          def rawPath = it.value.substring(2, it.value.size() - 2).trim()
          if(rawPath){
            def ifOutput = new If(rawPath)
            ifOutput.push(new Raw(" ${Runtime.escapeHtml(it.key)}"))
            return [ifOutput]
          }
        }
        return [new Raw(" ${Runtime.escapeHtml(it.key)}")]
      } else if (it.key == "data-embed"){
          return [
            new Raw(" data-context='"),
            new EmbeddedData(),
            new Raw("'")
          ]
        } else {
          return [
            new Raw(" ${Runtime.escapeHtml(it.key)}=\""),
            Compiler.compileVariables(it.value),
            new Raw("\"")
          ]
      }
    }).flatten()
  }
  boolean containsUpperCase(str){
    def upperCasedChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("")
    upperCasedChar.any({ str.contains(it)})
  }

  List<String> toGroovy(){
    attributes.collect({ it.toGroovy() })

  }
}

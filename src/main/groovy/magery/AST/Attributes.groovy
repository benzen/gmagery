package org.magery.AST

import org.magery.Compiler
import org.magery.Runtime

class Attributes{
  def attributes
  Attributes(attributes){
    this.attributes = 
    attributes.grep({!Compiler.IGNORED_ATTRIBUTES.contains(it.key)})
              .grep({ it.key.indexOf("on") != 0})
              .collect({
                if(Compiler.BOOLEAN_ATTRIBUTES.contains(it.key)){
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
  List<String> toGroovy(){
    attributes.collect({ it.toGroovy() })
    
  }
}
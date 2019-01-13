package org.magery.AST


public class Variable {
  def path
  Variable(path){
    this.path = path
  }

  List<String> toGroovy(){
    def quotedPath = path.collect { "\"$it\"" }
    [
      "output << runtime.escapeHtml(runtime.toString(runtime.lookup(data, ${quotedPath})))\n"
    ]
  }
}

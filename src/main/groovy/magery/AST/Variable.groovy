package org.magery.AST

public class Variable {
  def path
  Variable(path){
    this.path = path
  }

  String toString(){
    "{{$path.join(".")}}"
  }
  def toGroovy(results){
    def quotedPath = path.collect { "\"$it\"" }
    results.push("output.push(runtime.escapeHtml(runtime.toString(runtime.lookup(data, ${quotedPath}))))\n")
  }
}

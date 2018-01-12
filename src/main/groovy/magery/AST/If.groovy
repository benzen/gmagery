package org.magery.AST

public class If {
  def path
  def children
  If(value){
    def trimmedValue = value.trim()
    if(trimmedValue.contains("{{")){
      throw new Exception("Value for attribute data-if is \"${trimmedValue}\" must not contains \"{{\" or \"}}\"")
    }
    this.path = trimmedValue.tokenize(".")
    this.children = []
  }
  def push(node){
    this.children.push(node)
  }
  List<String> toGroovy(){
    def quotedPath = path.collect {"\"$it\""}
    [
      "if(runtime.lookup(data, $quotedPath)){\n",
      children.collect({it.toGroovy()}),
      "}\n",
    ].flatten()
  }
}

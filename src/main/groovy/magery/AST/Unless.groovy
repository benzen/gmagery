package org.magery.AST

public class Unless {
  def path
  def children
  Unless(value){
    def trimmedValue = value.trim()
    if(trimmedValue.contains("{{")){
      throw new Exception("Value for attribute data-unless is \"${trimmedValue}\" must not contains \"{{\" or \"}}\"")
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
      "if(!runtime.lookup(data, $quotedPath)){\n",
      children.collect {
        it.toGroovy()
      },
      "}\n"
    ]
  }
}

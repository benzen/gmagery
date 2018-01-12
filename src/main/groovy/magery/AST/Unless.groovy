package org.magery.AST

public class Unless {
  def path
  def children
  Unless(path){
    this.path = path
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
    ].flatten()
  }
}

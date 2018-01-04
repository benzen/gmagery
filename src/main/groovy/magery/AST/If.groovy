package org.magery.AST

public class If {
  def path
  def children
  If(path){
    this.path = path
    this.children = []
  }
  def push(node){
    this.children.push(node)
  }
  def toGroovy(results){
    def quotedPath = path.collect {"\"$it\""}
    results.push("if(runtime.lookup(data, $quotedPath)){\n")
    children.each {
      it.toGroovy(results)
    }
    results.push("}\n")
  }
}

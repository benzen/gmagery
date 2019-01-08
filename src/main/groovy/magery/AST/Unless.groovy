package org.magery.AST

import org.code3.simpleELParser.Compiler
import org.magery.ExpressionCodeGenerator

public class Unless {
  def ast
  def children
  Unless(value){
    this.ast = new Compiler().compile(value)
    this.children = []
  }
  def push(node){
    this.children.push(node)
  }
  List<String> toGroovy(){
    def code = new ExpressionCodeGenerator().generateCode(this.ast)
    [
      "if(!($code)){\n",
      children.collect({it.toGroovy()}),
      "}\n",
    ]
  }
}

package org.magery.AST

public class TemplateChildren {
  def toGroovy(results){
    results.push("if( inner != null){\n")
    results.push("inner()\n")
    results.push("}\n")
  }
}

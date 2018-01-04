package org.magery.AST

public class TemplateEmbed {
  def name
  TemplateEmbed(name){
    this.name = name
  }
  def toGroovy(results){
    results.push("output.push(runtime.source(templates, \"$name\"))\n")
  }
}

package org.magery.AST

public class TemplateEmbed {
  def name
  TemplateEmbed(name){
    this.name = name
  }
  List<String> toGroovy(){
    ["output << runtime.source(templates, \"$name\")\n"]
  }
}

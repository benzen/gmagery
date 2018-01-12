package org.magery.AST

public class TemplateChildren {
  List<String> toGroovy(){
    [
      "if( inner != null){\n",
      "inner()\n",
      "}\n"
    ]
  }
}

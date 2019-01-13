package org.magery.AST

public class Template {
  def name
  def src
  def children

  Template(name, src, children = []){
    this.name = name
    this.src = src
    this.children = children
  }

  List<String> toGroovy(){
    def id = UUID.randomUUID().toString().replace("-","_")
    [
      "if(templates[\"$name\"]) {\n",
      "throw new Exception(\"Template \\\"$name\\\" is already defined and there is another template with the same name.\")\n",
      "}\n",
      "def fn_$id = {templates, data, output, inner, embedData ->\n",
      children.collect({ node -> node.toGroovy() }),
      "}\n",
      "templates[\"$name\"] = [fn:fn_$id, src: \"\"\"$src\"\"\"]\n",
    ]
  }

}

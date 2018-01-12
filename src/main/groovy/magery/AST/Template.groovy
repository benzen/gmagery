package org.magery.AST

public class Template {
  def name
  def src
  def children

  Template(name, src){
    this.name = name
    this.src = src
    children = []
  }

  List<String> toGroovy(){
    def id = UUID.randomUUID().toString().replace("-","_")
    [
      "def fn_$id = {templates, data, output, inner, embedData ->\n",
      children.collect({ node -> node.toGroovy() }),
      "}\n",
      "templates[\"$name\"] = [fn:fn_$id, src: \"\"\"$src\"\"\"]\n",
    ].flatten()
  }
  def push(o){
    children.push(o)
  }
}

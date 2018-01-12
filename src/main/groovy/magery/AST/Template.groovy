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

  String toGroovy(results){
    def id = UUID.randomUUID().toString().replace("-","_")
    results.push("def fn_$id = {templates, data, output, inner, embedData ->\n")
    children.each { node -> node.toGroovy(results)}
    results.push("}\n")
    results.push("templates[\"$name\"] = [fn:fn_$id, src: \"\"\"$src\"\"\"]\n")
  }
  def push(o){
    children.push(o)
  }
}

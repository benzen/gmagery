package AST

public class Template {
  def name
  def src
  def children

  Template(name, src){
    this.name = name
    this.src = src
    children = []
  }
  String toString(){
    "<Template name='$name' src='$src'>"
  }
  String toGroovy(results){
    results.push("def fn = {templates, data, output, inner, embedData ->\n")
    children.each { node -> node.toGroovy(results)}
    results.push("}\n")
    // results.push("templates[\"$name\"] = [fn:fn, src:'$src']")
    results.push("templates[\"$name\"] = [fn:fn]")
    // println results
  }
  def push(o){
    children.push(o)
  }
}

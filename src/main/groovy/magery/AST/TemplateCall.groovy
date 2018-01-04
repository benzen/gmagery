package org.magery.AST

public class TemplateCall {
  def children
  def name
  def context
  def embededData

  TemplateCall(name, context, embededData){
    this.name = name
    this.context = context
    this.embededData = embededData
    this.children = []
  }

  def attrValueToGroovy(attr){
    if(attr.size() == 1){
      if(attr[0] instanceof Variable) {

        return "runtime.lookup(data, ${attr[0].path})"
      } else {
        return "\"${attr[0].text}\""
      }
    }
    def parts =  attr.collect { part ->
      if( part instanceof Variable){
        def quotedPath = part.path.collect { "\"$it\""}

        "runtime.lookup(data, ${quotedPath})"
      } else {
        "\"${part}\""
      }
    }
    parts.grep({it != "\"\""}).join(" + ")
  }

  def toGroovy(results){
    def id = UUID.randomUUID().toString().replace("-","_")
    if(children.size() != 0){
      results.push("def fn_${id} = {\n")
      children.each {it.toGroovy(results)}
      results.push("}\n")
    }
    if(context){
      results.push("runtime.render(templates, ${attrValueToGroovy(this.name)},[ \n")
      context.each({ k, v ->
        results.push("$k: ${attrValueToGroovy(v)},\n")
      })
      if(children.size() != 0){
        results.push("], output, fn_$id, ${embededData})\n")
      } else {
        results.push("], output, inner, ${embededData})\n")
      }
    } else {
      if(children.size() != 0){
        results.push("runtime.render(templates, ${attrValueToGroovy(this.name)}, data, output, fn_$id)\n")
      } else {
        results.push("runtime.render(templates, ${attrValueToGroovy(this.name)}, data, output, inner)\n")
      }

    }

  }

  def push(node){
    this.children.push(node)
  }

}

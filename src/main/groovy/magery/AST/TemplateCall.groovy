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
      } else if(part instanceof Raw){
        "\"${part.text}\""
      } else {
        "\"${part}\""
      }
    }
    parts.grep({it != "\"\""}).join(" + ")
  }

  List<String> toGroovy(){
    def id = UUID.randomUUID().toString().replace("-","_")
    def hasChildren = children.size() != 0
    def childrenFn = !hasChildren ? [] : [
      "def fn_${id} = {\n",
      children.collect( {it.toGroovy()}),
      "}\n",
    ].flatten()
    
    if( context && hasChildren){
      childrenFn + [
        "runtime.render(templates, ${attrValueToGroovy(this.name)},[ \n",
        context.collect({ k, v ->
          "$k: ${attrValueToGroovy(v)},\n"
        }),
        "], output, fn_$id, ${embededData})\n",
      ].flatten()
    } else if( context && !hasChildren) {
      [
        "runtime.render(templates, ${attrValueToGroovy(this.name)},[ \n",
        context.collect({ k, v ->
          "$k: ${attrValueToGroovy(v)},\n"
        }),
        "], output, inner, ${embededData})\n"
      ].flatten()
    } else if(!context && hasChildren) {
      childrenFn + [
      ["runtime.render(templates, ${attrValueToGroovy(this.name)}, data, output, fn_$id)\n"]
      ]
    } else if (!context && !hasChildren) {
      [ "runtime.render(templates, ${attrValueToGroovy(this.name)}, data, output, inner)\n" ]
    }
  }

  def push(node){
    this.children.push(node)
  }

}

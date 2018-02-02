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
    attr
    .collect { part ->
      if( part instanceof Variable){
        def quotedPath = part.path.collect { "\"$it\""}
        "runtime.lookup(data, ${quotedPath})"
      } else if(part instanceof Raw){
        "\"${part.text}\""
      } else {
        "\"${part}\""
      }
    }
    .grep({it != "\"\""})
    .join(" + ")
  }

  List<String> toGroovy(){
    def id = UUID.randomUUID().toString().replace("-","_")
    def hasChildren = children.size() != 0
    def childrenFn = hasChildren ? [
      "def fn_${id} = {\n",
      children.collect( {it.toGroovy()}),
      "}\n",
    ] : []

    def contextAsString = context ?
    [ "[ \n",
      context.collect({ k, v ->
        "'$k': ${attrValueToGroovy(v)},\n"
      }),
      "],"
    ].flatten().join("") : "[:]"
    def inner = hasChildren ? "fn_$id" : "inner"


    childrenFn + [
      "runtime.render(templates, ${attrValueToGroovy(this.name)}, $contextAsString, output, $inner, ${embededData})\n",
    ]

  }

  def push(node){
    this.children.push(node)
  }

}

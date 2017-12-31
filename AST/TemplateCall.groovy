package AST
  public class TemplateCall {
    def children
    def name
    def context
    def embededData

    TemplateCall(name){
      this.name = name
      this.children = []
    }

    def attrValueToPython(attr){
      "\"${attr[0].text}\""
    }

    def toGroovy(results){
      results.push("runtime.render(templates, ${attrValueToPython(this.name)}, data, output, inner)\n")
    }

    def push(node){
      this.children.push(node)
    }

  }

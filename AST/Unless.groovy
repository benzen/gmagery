package AST
public class Unless {
  def path
  def children
  Unless(path){
    this.path = path
    this.children = []
  }
  def push(node){
    this.children.push(node)
  }
  def toGroovy(results){
    def quotedPath = path.collect {"\"$it\""}
    results.push("if(!runtime.lookup(data, $quotedPath)){\n")
    children.each {
      it.toGroovy(results)
    }
    results.push("}\n")
  }
}

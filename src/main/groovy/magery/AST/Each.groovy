package org.magery.AST

public class Each {
  def name
  def path
  def children
  Each(name, path){
    this.name = name
    this.path = path
    children = []
  }
  def push(o){
    this.children.push(o)
  }
  List<String> toGroovy(){
    def id = UUID.randomUUID().toString().replace("-", "_")
    def quotedPath = path.collect { "\"$it\"" }
    [
      "def fn_$id = { localData ->\n",
      "def dataBackup = data\n",
      "data = localData\n",
      children.collect({ it.toGroovy() }),
      "data = dataBackup\n",
      "}\n",
      "runtime.each(data, \"$name\", $quotedPath, fn_$id)\n"
    ].flatten()
  }
}

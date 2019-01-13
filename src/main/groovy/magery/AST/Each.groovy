package org.magery.AST

public class Each {
  def name
  def path
  def children
  Each(value, children = []){

    def parts = value.split(" in ")
    def name = parts[0]
    def path = parts[1].trim().tokenize(".")

    this.name = name
    this.path = path
    this.children = children
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
    ]
  }
}

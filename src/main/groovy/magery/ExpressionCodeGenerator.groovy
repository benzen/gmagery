package org.magery

public class ExpressionCodeGenerator {
  def generateCode(ast){
    def output = []
    def binaryOperators = ["and", "or", "lte", "lt", "gt", "gte", "eq", "neq"]
    def unaryOperators = ["not"]
    def data = ["int", "float", "lp", "rp"]
    def generateCodeRec
    generateCodeRec = { node, acc ->
      if(binaryOperators.contains(node.type) ){
        generateCodeRec(node.children[0], acc)
        acc << node.value
        generateCodeRec(node.children[1], acc)


      } else if (unaryOperators.contains(node.type)){
        acc <<  node.value
        generateCodeRec node.children, acc
      } else if (node.type == "string") {
        def quotedString = "\"${node.value}\""
        acc << quotedString
      } else if (node.type == "var") {
        def quotedPath = "${node.value.tokenize('.').collect({"\"$it\""})}"
        acc << "runtime.lookup(data, $quotedPath)"
      } else if (data.contains(node.type)) {
        acc << "$node.value"
      } else {
        throw new RuntimeException("Unknow expression operator $node.type")
      }
    }

    generateCodeRec(ast, output)

    return output.join("")

  }
}

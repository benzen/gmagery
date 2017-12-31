package AST
public class EmbeddedData {
  EmbeddedData(){}
  def toGroovy(results){
    results.push("output.push(runtime.escapeHtml(runtime.encodeJson(data)))\n")
  }
}

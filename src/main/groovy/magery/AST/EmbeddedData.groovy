package org.magery.AST

public class EmbeddedData {
  EmbeddedData(){}
  def toGroovy(results){
    results.push("output.push(runtime.escapeHtmlButDoubleQuote(runtime.encodeJson(data)))\n")
  }
}

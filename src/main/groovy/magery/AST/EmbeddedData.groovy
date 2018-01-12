package org.magery.AST

public class EmbeddedData {
  EmbeddedData(){}
  List<String> toGroovy(){
    [
      "output.push(runtime.escapeHtmlButDoubleQuote(runtime.encodeJson(data)))\n"
    ]
  }
}

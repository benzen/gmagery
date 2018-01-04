package org.magery.AST

public class ConditionalDataEmbed {
  def toGroovy(results){
    results.push("if(embedData){\n")
    new Raw(" data-context='").toGroovy(results)
    new EmbeddedData().toGroovy(results)
    new Raw("'").toGroovy(results)
    results.push("}\n")
  }
}

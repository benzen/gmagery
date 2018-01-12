package org.magery.AST

public class ConditionalDataEmbed {
  List<String> toGroovy(){
    [
      "if(embedData){\n",
      new Raw(" data-context='").toGroovy(),
      new EmbeddedData().toGroovy(),
      new Raw("'").toGroovy(),
      "}\n"
    ].flatten()
  }
}

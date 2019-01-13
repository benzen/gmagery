package org.magery.AST

import org.apache.commons.lang3.StringEscapeUtils

public class Raw {
  def text
  Raw(s){
    text = s
  }

  List<String> toGroovy(){
    text = StringEscapeUtils.escapeJava(text)
    ["output << \"${text}\"\n"]
  }

}

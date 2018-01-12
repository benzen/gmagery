package org.magery.AST

import org.apache.commons.lang.StringEscapeUtils

public class Raw {
  def text
  Raw(s){
    text = s
  }

  List<String> toGroovy(){
    text = StringEscapeUtils.escapeJava(text)
    ["output.push(\"${text}\")\n"]
  }

}

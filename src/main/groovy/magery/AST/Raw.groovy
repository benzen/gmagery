package org.magery.AST

import org.apache.commons.lang.StringEscapeUtils

public class Raw {
  def text
  Raw(s){
    text = s
  }

  String toString(){
    text
  }

  String toGroovy(results){
    text = StringEscapeUtils.escapeJava(text)
    def str = "output.push(\"${text}\")\n"
    results.push(str)
  }

}

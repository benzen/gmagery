package org.magery.AST

import org.apache.commons.lang3.StringEscapeUtils

class Comment{
  def text

  Comment(text){
    this.text = text
  }

  List<String> toGroovy(){
    text = StringEscapeUtils.escapeJava(text)
    ["output << \"<!--$text-->\"\n"]
  }
}

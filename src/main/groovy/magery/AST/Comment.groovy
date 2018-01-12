package org.magery.AST

import org.apache.commons.lang.StringEscapeUtils

class Comment{
  def text
  Comment(text){
    this.text = text
  }
  def toGroovy(results){
    text = StringEscapeUtils.escapeJava(text)
    results.push("output.push(\"<!--$text-->\")\n")
  }
}

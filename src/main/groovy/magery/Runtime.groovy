package org.magery

import org.apache.commons.lang3.StringEscapeUtils
import groovy.json.JsonOutput

class Runtime {
  def static render(templates, templateName, data, output, inner = null, embedData = false){
    if(!templates[templateName]){
        throw new Exception("No such template \"$templateName\"")
    }
    templates[templateName].fn.call(templates, data, output, inner, embedData)

  }

  def static renderToString(templates, templateName, data){
    def output = []
    render(templates, templateName, data, output)
    output.join('')
  }

  def static escapeHtml(str){
    StringEscapeUtils.escapeXml(str)
  }
  def static escapeHtmlButDoubleQuote(str){
    StringEscapeUtils.escapeXml(str).replaceAll("&quot;", "\"")
  }


  def static toString(thing){
    if(thing == null ) return ""
    if(thing instanceof Map){ return "[object Object]"}
    if(thing instanceof List){ return thing.collect({toString(it)}).join(',')}
    "$thing"
  }

  def static lookup(data, path){
    if(path == null) { return null }
    def result = path.inject(data, {obj, prop ->
      prop == "length" ? obj.size() : obj?."$prop"
   })
   result == false ? null : result
  }

  def static each(data, name, path, fn){
    def l = lookup(data, path)
    if(l instanceof List) {
      l.each { item ->
        def localData = [*:data]
        localData.put(name, item)
        fn(localData)
      }
    }
  }
  def static encodeJson(data){
    JsonOutput.toJson(data)
  }

  def static source(templates, templateName){
    if(templates[templateName]){
      templates[templateName].src
    }
  }
}

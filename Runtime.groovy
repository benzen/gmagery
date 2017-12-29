import org.apache.commons.lang.StringEscapeUtils

class Runtime {
  def static render(templates, templateName, data, output, inner= null, embedData = false){
    if(templates[templateName]){
      templates[templateName].fn.call(templateName, data, output, inner, embedData)
    }
  }

  def static renderToString(templates, templateName, data){
    def output = []
    render(templates, templateName, data, output)
    output.join('').toString()
  }
  def static escapeHtml(str){
    StringEscapeUtils.escapeHtml(str)
  }
  def static toString(thing){
    if(thing == null ) return ""
    if(thing instanceof Map){ return "[object Object]"}
    if(thing instanceof List){ return thing.join(',')}
    "$thing"
  }
  def static lookup(data, path){
    // println "data $data path $path"
    if(path == null) { return null }

    path.inject(data, {obj, prop ->
      println "prop $prop"
      prop == "length" ? obj.size() : obj?."$prop"
     })

  }
}

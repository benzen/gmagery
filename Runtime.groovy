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
    def r = StringEscapeUtils.escapeHtml(str)
    println r
    r
  }
  def static toString(thing){
    println "stringify $thing"
      "$thing"
  }
  def static lookup(data, path){
    println "data $data path $path"
    if(path == null) { return null }
    def fetchingLengthOfArray = path.contains("length")
    def cleanedProperty = path.minus("length")
    cleanedProperty.inject(data, {obj, prop -> obj?."$prop" })
  }
}

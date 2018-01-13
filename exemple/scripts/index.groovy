import org.magery.Compiler
import org.magery.Runtime
try{
  def templates = [:]
  templates = Compiler.compileTemplates("public/templates/app-title.html", templates)
  templates = Compiler.compileTemplates("public/templates/app-root.html", templates)
  
  def str = Runtime.renderToString(templates, "app-root", [key: "value", title: "Sake"])
  println str
} catch (Exception e) {
  println "ERROR $e"
}

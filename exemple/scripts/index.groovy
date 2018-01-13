import org.magery.Compiler
import org.magery.Runtime
try{
  def templates = [:]
  templates = Compiler.compileTemplates("public/templates/app-title.html", templates)
  templates = Compiler.compileTemplates("public/templates/app-root.html", templates)
  templates = Compiler.compileTemplates("public/templates/app-button.html", templates)
  
  def str = Runtime.renderToString(templates, "app-root", [key: "value", title: "Sake", counter: 0])
  println """
  <head>
    <script src="/js/magery-compiler.js" type="text/javascript"></script>
    <script src="/js/magery-patcher.js" type="text/javascript"></script>
    <script src="/js/redux.js" type="text/javascript"></script>
    ${new File("public/templates/app-title.html").text}
    ${new File("public/templates/app-root.html").text}
    ${new File("public/templates/app-button.html").text}
    
  </head>
  <body>
    $str
    <script src="/js/app.js" type="text/javascript"></script>
  </body>
  """
} catch (Exception e) {
  println "ERROR $e"
}

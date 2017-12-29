package AST
public class Raw {
  def text
  Raw(s){
    text = s
  }

  String toString(){
    text
  }

  String toGroovy(results){
    results.push("output.push('$text')\n")
  }
}

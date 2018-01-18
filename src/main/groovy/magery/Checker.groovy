package org.magery

class Checker{
  static def checkAll(templateTree){
    def tests = [
      "checkEscaping",
      "checkDataIf",
      "checkDataUnless",
    ]
    tests.each {
      Checker."$it"(templateTree)
    }
  }

  static def checkEscaping(node){
    def checkEscapingInText = { text ->
      def nbOpenedVariableBraces = (text =~ /\{\{/)
      def nbClosedVariableBraces = (text =~ /\}\}/)
      def nbPairesOfBraces = (text =~ /\{\{[^\}]*\}\}/)

      if( nbOpenedVariableBraces.size() != nbClosedVariableBraces.size() ||
          nbPairesOfBraces.size() != nbOpenedVariableBraces.size() ){
          throw new Exception("In text \"${text.trim()}\" variable should be escaped with \"{{\" before and  \"}}\"")
      }
    }
    if(node  instanceof org.jsoup.nodes.Element){
      node.attributes().each({
        checkEscapingInText(it.value)

      })
      node.childNodes().each({
        checkEscaping(it)
      })
    } else if(node instanceof org.jsoup.nodes.TextNode){
      checkEscapingInText(node.wholeText)
    } else if(node instanceof org.jsoup.select.Elements){
      node.each { checkEscaping(it) }
    } else if (node instanceof org.jsoup.nodes.Comment){
      //Left blank intentionally
    }else {
      throw new Exception("Unhandeled Node type ${node.class}")
    }
  }
  static def checkDataIf(node){
        def checkDataIfInAttributes = { attributes ->
          def attr = attributes.each({
            if(it.key == "data-if" && it.value.contains("{{")){
              throw new Exception("Value for attribute data-if is \"${it.value.trim()}\" must not contains \"{{\" or \"}}\"")
            }
          })

        }
        if(node  instanceof org.jsoup.nodes.Element){
          checkDataIfInAttributes(node.attributes())
          node.childNodes().each({
            checkDataIf(it)
          })
        } else if(node instanceof org.jsoup.nodes.TextNode){
          //Left blank intentionally
        } else if(node instanceof org.jsoup.select.Elements){
          node.each { checkDataIf(it) }
        } else if (node instanceof org.jsoup.nodes.Comment){
          //Left blank intentionally
        }else {
          throw new Exception("Unhandeled Node type ${node.class}")
        }
  }
  static def checkDataUnless(node){
    def checkDataUnlessInAttributes = { attributes ->
      def attr = attributes.each({
        if(it.key == "data-unless" && it.value.contains("{{")){
          throw new Exception("Value for attribute data-unless is \"${it.value.trim()}\" must not contains \"{{\" or \"}}\"")
        }
      })

    }
    if(node  instanceof org.jsoup.nodes.Element){
      checkDataUnlessInAttributes(node.attributes())
      node.childNodes().each({
        checkDataUnless(it)
      })
    } else if(node instanceof org.jsoup.nodes.TextNode){
      //Left blank intentionally
    } else if(node instanceof org.jsoup.select.Elements){
      node.each { checkDataUnless(it) }
    } else if (node instanceof org.jsoup.nodes.Comment){
      //Left blank intentionally
    }else {
      throw new Exception("Unhandeled Node type ${node.class}")
    }
  }

}

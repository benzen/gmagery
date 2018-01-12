# magery-groovy
A groovy (jvm) implementation of magery

This repository contains a groovy (jvm) implementation of magery templating system.
Magery can be used as client and as server templating system.

Magery is nicely explained here [here](https://github.com/caolan/magery/)

At the moment this implementation passes the test-suite defined [here](https://github.com/caolan/magery-tests) with problem with 
whitespace

# Usage

    def templates = Compiler.compileTemplates("/my/path/to/template/file.html")
    
One file can contains more than one template.
You can combine multiple file of templates, before rendering anything. This can be useful if you choose  to
have a template by file. Remember that all template share the same global namespace, so you can't have two
template that use the same name.
  
    def templates = Compiler.compileTemplates("file-a.html")
    //at this point only template of `file-a` are in the variable `templates`
    templates = Compile.compileTemplates("file-b.html", templates)
    //At this point both the template inside file `file-a.html` and thoses 
    //inside `file-b.html` are compiled into  the variable `templates`
    
Once your templates are compiled, you can render you template:

    Runtime.renderToString(templates, "app-main", data)
    //Here templates is the result of one or multiple call of Compiler.compileTemplates(...)
    // "app-main" is the root component of my app
    //data is a combination of map and array
    
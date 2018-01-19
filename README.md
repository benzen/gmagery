# magery-groovy
A groovy (jvm) implementation of magery

This repository contains a groovy (jvm) implementation of magery templating system.
Magery can be used as client and as server templating system.

Magery is nicely explained here [here](https://github.com/caolan/magery/)

At the moment this implementation passes the test-suite defined [here](https://github.com/caolan/magery-tests) with problem with
whitespace

#Use as dependecy

Using Grab for the groovy-peoples

  @Grab(group='org.magery', module='groovy-magery', version='0.1'),

Using maven for the standard folks

  <dependency>
    <groupId>org.magery</groupId>
    <artifactId>groovy-magery</artifactId>
    <version>0.1</version>
  </dependency>


I do not intend to release a version yet (mvn publish kind of release).
For two reason, there is still work ongoing on the js implementation and on the groovy implmentation.
Still if you want to use it anyway, you will be able to install your local version with this:

    mvn clean install


# Usage

```groovy
def templates = Compiler.compileTemplates("/my/path/to/template/file.html")
```

One file can contains more than one template.
You can combine multiple file of templates, before rendering anything. This can be useful if you choose  to
have a template by file. Remember that all template share the same global namespace, so you can't have two
template that use the same name.

```groovy
def templates = Compiler.compileTemplates("file-a.html")
//at this point only template of `file-a` are in the variable `templates`
templates = Compile.compileTemplates("file-b.html", templates)
//At this point both the template inside file `file-a.html` and thoses
//inside `file-b.html` are compiled into  the variable `templates`
```

Once your templates are compiled, you can render you template:

```groovy
Runtime.renderToString(templates, "app-main", data)
//Here templates is the result of one or multiple call of Compiler.compileTemplates(...)
// "app-main" is the root component of my app
//data is a combination of map and array
```

# Example

js-magery give a lot of extensive exemple about the templating language possibilities.
In this repository, we have an exemple of server rendering a bunch of templates,
and front-end taking it over without any reload or anything.

To run this exemple

    mvn clean install
    cd exemple
    groovy server.groovy;; then open your browser on http://localhost:9090/index.html

# Compilation

This project use maven for dependency and build.
So to build, standard command will do:

    mvn clean test package

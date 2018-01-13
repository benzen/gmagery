//
var MageryCompiler=function(t){var e={};function a(n){if(e[n])return e[n].exports;var r=e[n]={exports:{},id:n,loaded:false};t[n].call(r.exports,r,r.exports,a);r.loaded=true;return r.exports}a.m=t;a.c=e;a.p="";return a(0)}([function(module,exports,__webpack_require__){exports.compileToString=__webpack_require__(1).compileToString;exports.compile=function(target,templates,runtime){runtime=runtime||window.Magery;templates=templates||{};if(typeof target==="string"){return exports.compile(document.querySelectorAll(target),templates,runtime)}var compiled=eval(exports.compileToString(target))(runtime);for(var k in compiled){templates[k]=compiled[k]}return templates}},function(t,e,a){var n=a(2);var r=a(3);var i=["data-tagname","data-each","data-if","data-unless","data-key"];function s(t){return"p.lookup(data, "+JSON.stringify(t)+")"}function o(t){var e=[];n.eachAttribute(t,function(t,a){if(i.indexOf(t)===-1&&t.substr(0,2)!=="on"){e.push(JSON.stringify(t)+": "+d(a))}});return"{"+e.join(", ")+"}"}function u(t,e){var a=e.indexOf("(");var n=e.lastIndexOf(")");var r=e.substring(0,a);var i=e.substring(a+1,n);return"p.eventListener("+JSON.stringify(t)+", "+JSON.stringify(r.split("."))+", "+JSON.stringify("["+i+"]")+", "+"data, "+"handlers);\n"}function f(t){var e="";n.eachAttribute(t,function(t,a){var n=t.match(/^on(.*)/);if(n){e+=u(n[1],a)}});return e}function l(t,e,a,l){if(t.tagName==="TEMPLATE-EMBED"){return}if(t.tagName==="TEMPLATE-CHILDREN"){a("inner();\n");return}if(!l&&t.tagName==="TEMPLATE"&&t.dataset.tagname){e.push(t);return}if(t.dataset.each){var p=t.dataset.each.split(/\s+in\s+/);var c=p[0];var m=n.propertyPath(p[1]);a("p.each("+"data, "+JSON.stringify(c)+", "+s(m)+", "+"function (data) {\n")}if(t.dataset.if){var v=n.propertyPath(t.dataset.if);a("if (p.isTruthy("+s(v)+")) {\n")}if(t.dataset.unless){var h=n.propertyPath(t.dataset.unless);a("if (!p.isTruthy("+s(h)+")) {\n")}var y=t.tagName=="TEMPLATE"?t.content.childNodes:t.childNodes;var N=true;if(t.tagName=="TEMPLATE-CALL"){a("p.render("+"templates"+", "+d(t.getAttribute("template"))+", "+o(t)+", handlers"+", "+(t.dataset.key?d(t.dataset.key):"null")+", function () {"+f(t)+"}"+(y.length?", p.wrapChildren(function (p) {":");")+"\n");N=false}else if(t.tagName.indexOf("-")!==-1){a("p.render("+"templates"+", "+JSON.stringify(t.tagName.toLowerCase())+", "+o(t)+", handlers"+", "+(t.dataset.key?d(t.dataset.key):"null")+", function () {"+f(t)+"}"+(y.length?", p.wrapChildren(function (p) {":");")+"\n");N=false}else{var T=t.tagName;if(T==="TEMPLATE"&&t.dataset.tagname){T=t.dataset.tagname.toUpperCase()}if(l){if(t.dataset.key){a("p.enterTag("+JSON.stringify(T)+", "+"root_key || "+d(t.dataset.key)+");\n")}else{a("p.enterTag("+JSON.stringify(T)+", root_key || null);\n")}}else if(t.dataset.key){a("p.enterTag("+JSON.stringify(T)+", "+d(t.dataset.key)+");\n")}else{a("p.enterTag("+JSON.stringify(T)+", null);\n")}n.eachAttribute(t,function(t,e){if(t==="data-template"){t="data-bind"}if(i.indexOf(t)!==-1){return}var n=t.match(/^on(.*)/);if(n){a(u(n[1],e))}else if(r.attributes[t]&r.BOOLEAN_ATTRIBUTE){if(e===""){a("p.attribute("+JSON.stringify(t)+", true);\n")}else{a("if (p.isTruthy("+d(e)+")) {\n");a("p.attribute("+JSON.stringify(t)+", true);\n");a("}\n")}}else{a("p.attribute("+JSON.stringify(t)+", "+d(e)+");\n")}});if(l){a("if (extra_attrs) { extra_attrs(); }\n")}}n.eachNode(y,function(t){g(t,e,a,false)});if(N){a("p.exitTag();\n")}else if(y.length){a("}));\n")}if(t.dataset.unless){a("}\n")}if(t.dataset.if){a("}\n")}if(t.dataset.each){a("});\n")}}function d(t,e){var a=t.split(/{{\s*|\s*}}/);var r=a.length;var i=-1;while(++i<r){if(i%2){var o=n.propertyPath(a[i]);a[i]=o}}if(r==1&&!a[0]&&e){return"true"}var u=[];var f=-1;while(++f<r){if(a[f].length){u.push(f%2?s(a[f]):JSON.stringify(a[f]))}}if(!u.length){return JSON.stringify("")}return u.join(" + ")}function p(t,e){var a=d(t.textContent);if(a[0]!=='"'){a='""+'+a}if(t.parentNode.tagName==="TEXTAREA"){e('p.attribute("value", '+a+");\n")}else{e("p.text("+a+");\n")}}function c(t,e,a){n.eachNode(t.childNodes,function(t){g(t,e,a,false)})}function g(t,e,a,n){switch(t.nodeType){case 1:l(t,e,a,n);break;case 3:p(t,a);break;case 11:c(t,e,a);break}}function m(t){}e.compile=function(t,e){var a=[];if(!(t instanceof NodeList)){t=[t]}for(var n=0,r=t.length;n<r;n++){var i=t[n];g(i,a,m,!(i.tagName=="TEMPLATE"&&i.dataset&&i.dataset.hasOwnProperty("tagname")))}e("({\n");while(a.length){i=a.shift();if(i.dataset.tagname.indexOf("-")===-1){throw new Error("Error compiling template '"+i.dataset.tagname+"': data-tagname must include a hyphen")}e(JSON.stringify(i.dataset.tagname)+": ");e("Magery._template("+"function (p, data, handlers, root_key, extra_attrs, inner) {\n");e("var templates = this;\n");g(i,a,e,true);e("})"+(a.length?",":"")+"\n")}e("})\n")};e.compileToString=function(t){var a="(function (Magery) { return ";e.compile(t,function(t){a+=t});return a+"})"}},function(t,e){var a=1;var n=3;var r=11;e.isDocumentFragment=function(t){return t.nodeType===r};e.isElementNode=function(t){return t.nodeType===a};e.isTextNode=function(t){return t.nodeType===n};e.eachNode=function(t,e){var a=0;var n=t[0];while(n){var r=n;n=n.nextSibling;e(r,a++,t)}};e.mapNodes=function(t,a){var n=[];e.eachNode(t,function(e,r){n[r]=a(e,r,t)});return n};e.trim=function(t){return t.replace(/^\s+|\s+$/g,"")};e.propertyPath=function(t){return t.split(".").filter(function(t){return t})};e.lookup=function(t,e){var a=t;for(var n=0,r=e.length;n<r;n++){if(a===undefined||a===null){return""}a=a[e[n]]}return a===undefined||a===null?"":a};e.templateTagName=function(t){var e=/^TEMPLATE-([^\s/>]+)/.exec(t.tagName);return e&&e[1].toLowerCase()};e.shallowClone=function(t){var e={};for(var a in t){e[a]=t[a]}return e};e.eachAttribute=function(t,e){var a=t.attributes;for(var n=0,r=t.attributes.length;n<r;n++){e(t.attributes[n].name,t.attributes[n].value)}}},function(t,e){var a=e.BOOLEAN_ATTRIBUTE=1;var n=e.USE_PROPERTY=2;var r=e.USE_STRING=4;e.attributes={allowfullscreen:a,async:a,autofocus:a,autoplay:a,capture:a,checked:a|n,controls:a,default:a,defer:a,disabled:a,formnovalidate:a,hidden:a,itemscope:a,loop:a,multiple:a|n,muted:a|n,novalidate:a,open:a,readonly:a,required:a,reversed:a,selected:a|n,value:n|r}}]);
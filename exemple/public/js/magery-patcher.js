var Magery=function(t){var e={};function r(n){if(e[n])return e[n].exports;var i=e[n]={exports:{},id:n,loaded:false};t[n].call(i.exports,i,i.exports,r);i.loaded=true;return i.exports}r.m=t;r.c=e;r.p="";return r(0)}([function(t,e,r){var n=r(1).Patcher;e._template=function(t){var e=function(e,r,i){var a=new n(e);return t.call(this,a,r,i)};e._render=t;return e}},function(module,exports,__webpack_require__){var transforms=__webpack_require__(2);var utils=__webpack_require__(4);var html=__webpack_require__(3);var ELEMENT_NODE=1;var TEXT_NODE=3;function matches(t,e,r){return(t.tagName===e||t.nodeType===TEXT_NODE&&e==="#text")&&t.key==r}function align(t,e,r,n){if(e&&matches(e,r,n)){return e}if(n&&t.keymap){return t.keymap[n]||null}return null}function deleteChildren(t,e,r){while(r){var n=r;r=r.nextSibling;t.removeChild(e,n)}}function deleteUnvisitedAttributes(t,e){var r=e.attributes;var n=[];var i,a;for(i=0,a=r.length;i<a;i++){var o=r[i];if(!e.visited_attributes.hasOwnProperty(o.name)){n.push(o.name)}}for(i=0,a=n.length;i<a;i++){t.removeAttribute(e,n[i])}}function deleteUnvisitedEvents(t,e){if(!e.bound_events){return}for(var r in e.bound_events){if(!e.visited_events.hasOwnProperty(r)){t.removeEventListener(e,r,e.bound_events[r].fn);delete e.bound_events[r]}}}function Patcher(t,e){this.transforms=e||transforms;this.root=t;this.reset()}exports.Patcher=Patcher;Patcher.prototype.reset=function(){this.parent=this.root.parentNode;this.current=this.root};Patcher.prototype.stepInto=function(t){t.visited_attributes={};t.visited_events={};this.parent=t;this.current=t.firstChild};Patcher.prototype.enterTag=function(t,e){var r=align(this.parent,this.current,t,e);if(!r){r=this.transforms.insertElement(this.parent,this.current,t);if(e){if(!this.parent.keymap){this.parent.keymap={}}this.parent.keymap[e]=r;r.key=e}}else if(!this.current){this.transforms.appendChild(this.parent,r)}else if(r!==this.current){this.transforms.replaceChild(this.parent,r,this.current)}if(!this.template_root){this.template_root=r}this.stepInto(r)};Patcher.prototype.EVENT={};function makeHandler(node,type){return function(event){var handler=node.bound_events[type];if(handler.path){var context=utils.shallowClone(handler.data);context.event=event;with(context){var args=eval(handler.args)}var fn=utils.lookup(node.handlers,handler.path);if(!fn){throw new Error("on"+type+": no '"+handler.path.join(".")+"' handler defined")}fn.apply(handler.template_root,args)}}}function setListener(t,e){if(!t.bound_events){t.bound_events={}}if(!t.bound_events.hasOwnProperty(e)){var r=makeHandler(t,e);t.bound_events[e]={fn:r};transforms.addEventListener(t,e,r)}t.visited_events[e]=null}Patcher.prototype.eventListener=function(t,e,r,n,i){var a=this.parent;if(a.handlers!==i){a.handlers=i}setListener(a,t);var o=a.bound_events[t];o.path=e;o.args=r;o.data=n;o.template_root=this.template_root};Patcher.prototype.attribute=function(t,e){var r=this.parent;if(r.getAttribute(t)!==e){this.transforms.setAttribute(r,t,e)}r.visited_attributes[t]=null};Patcher.prototype.text=function(t){var e=align(this.parent,this.current,"#text",null);if(!e){e=this.transforms.insertTextNode(this.parent,this.current,t)}else if(e.textContent!==t){this.transforms.replaceText(e,t)}this.current=e.nextSibling};function getListener(t,e){return t.bound_events&&t.bound_events[e]&&t.bound_events[e].fn}Patcher.prototype.exitTag=function(){deleteChildren(this.transforms,this.parent,this.current);var t=this.parent;this.parent=t.parentNode;this.current=t.nextSibling;deleteUnvisitedAttributes(this.transforms,t);deleteUnvisitedEvents(this.transforms,t)};Patcher.prototype.skip=function(t,e){var r=align(this.parent,this.current,t,e);if(!this.current){this.transforms.appendChild(this.parent,r)}else if(r!==this.current){this.transforms.replaceChild(this.parent,r,this.current)}this.current=r.nextSibling};Patcher.prototype.lookup=utils.lookup;Patcher.prototype.isTruthy=function(t){if(Array.isArray(t)){return t.length>0}return t};Patcher.prototype.each=function(t,e,r,n){for(var i=0,a=r.length;i<a;i++){var o=utils.shallowClone(t);o[e]=r[i];n(o)}};Patcher.prototype.render=function(t,e,r,n,i,a,o){if(!t[e]){this.enterTag(e.toUpperCase(),null);this.exitTag();return}var s=t[e];if(s._render){var u=this.template_root;this.template_root=null;s._render.call(t,this,r,n,i,a,o);this.template_root=u}else{this.enterTag(e.toUpperCase(),null);a();s(this.parent,r,n,o);var l=this.parent;this.parent=l.parentNode;this.current=l.nextSibling}};Patcher.prototype.wrapChildren=function(t){var e=this;return function(r){if(r){var n=new Patcher(r);n.parent=r;n.current=r.firstChild;t(n)}else{t(e)}}}},function(t,e,r){var n=r(3);e.insertTextNode=function(t,e,r){var n=document.createTextNode(r);t.insertBefore(n,e);return n};e.replaceText=function(t,e){t.textContent=e;return t};e.replaceChild=function(t,e,r){t.replaceChild(e,r);return e};e.appendChild=function(t,e){t.appendChild(e);return e};e.insertElement=function(t,e,r){var n=document.createElement(r);t.insertBefore(n,e);return n};e.removeChild=function(t,e){t.removeChild(e);return e};e.setAttribute=function(t,e,r){if(n.attributes[e]&n.USE_PROPERTY){t[e]=r}t.setAttribute(e,r);return t};e.removeAttribute=function(t,e){if(n.attributes[e]&n.USE_PROPERTY){t[e]=false}t.removeAttribute(e);return t};e.addEventListener=function(t,e,r){t.addEventListener(e,r,false);return t};e.removeEventListener=function(t,e,r){t.removeEventListener(e,r);return t}},function(t,e){var r=e.BOOLEAN_ATTRIBUTE=1;var n=e.USE_PROPERTY=2;var i=e.USE_STRING=4;e.attributes={allowfullscreen:r,async:r,autofocus:r,autoplay:r,capture:r,checked:r|n,controls:r,default:r,defer:r,disabled:r,formnovalidate:r,hidden:r,itemscope:r,loop:r,multiple:r|n,muted:r|n,novalidate:r,open:r,readonly:r,required:r,reversed:r,selected:r|n,value:n|i}},function(t,e){var r=1;var n=3;var i=11;e.isDocumentFragment=function(t){return t.nodeType===i};e.isElementNode=function(t){return t.nodeType===r};e.isTextNode=function(t){return t.nodeType===n};e.eachNode=function(t,e){var r=0;var n=t[0];while(n){var i=n;n=n.nextSibling;e(i,r++,t)}};e.mapNodes=function(t,r){var n=[];e.eachNode(t,function(e,i){n[i]=r(e,i,t)});return n};e.trim=function(t){return t.replace(/^\s+|\s+$/g,"")};e.propertyPath=function(t){return t.split(".").filter(function(t){return t})};e.lookup=function(t,e){var r=t;for(var n=0,i=e.length;n<i;n++){if(r===undefined||r===null){return""}r=r[e[n]]}return r===undefined||r===null?"":r};e.templateTagName=function(t){var e=/^TEMPLATE-([^\s/>]+)/.exec(t.tagName);return e&&e[1].toLowerCase()};e.shallowClone=function(t){var e={};for(var r in t){e[r]=t[r]}return e};e.eachAttribute=function(t,e){var r=t.attributes;for(var n=0,i=t.attributes.length;n<i;n++){e(t.attributes[n].name,t.attributes[n].value)}}}]);
package org.magery

class Html {
  def static IGNORED_ATTRIBUTES = [
    "data-tagname",
    "data-if",
    "data-unless",
    "data-each",
    "data-key"
  ]
  
  def static BOOLEAN_ATTRIBUTES = [
    "allowfullscreen",
    "async",
    "autofocus",
    "autoplay",
    "capture",
    "controls",
    "checked",
    "default",
    "defer",
    "disabled",
    "formnovalidate",
    "open",
    "readonly",
    "hidden",
    "itemscope",
    "loop",
    "muted",
    "multiple",
    "novalidate",
    "open",
    "required",
    "reversed",
    "selected"
  ]

  def static SELF_CLOSING_TAGS = [
    "area",
    "base",
    "br",
    "col",
    "embed",
    "hr",
    "img",
    "input",
    "keygen",
    "link",
    "menuitem",
    "meta",
    "param",
    "source",
    "track",
    "wbr"
  ]
}

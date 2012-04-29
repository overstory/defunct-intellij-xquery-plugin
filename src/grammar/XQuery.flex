package uk.co.overstory.xquery.parser;

import java.util.Stack;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import uk.co.overstory.xquery.psi.XqyTypes;

%%

%{
  StringBuffer string = new StringBuffer();
  private Stack<Integer> stack = new Stack<Integer>();

  public void yypushState(int newState) {
    //System.out.println ("Pushing state: " + newState);
    stack.push (yystate());
    yybegin (newState);
  }

  public void yypopState() {
    if (stack.empty()) {
      System.out.println ("yypopStack: stack empty, going to YYINITIAL");
      yybegin (YYINITIAL);
      return;
    }

    //System.out.println ("Popped state");
    yybegin (stack.pop());
  }

  public _XqyLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _XqyLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

S=[\ \t\f\n\r]+

/*
AsciiAlpha=[A-Za-z]+
Alpha=[:letter:]
*/
Digit=[:digit:]
Digits={Digit}+
OptionalDigits={Digit}*

/*
Hex={Digit} | [aAbBcCdDeEfF]
*/

DecimalLiteral= ( ('.' {Digits}) | ( {Digits} '.' {OptionalDigits} ) )
DoubleLiteral= ( ( "." {Digits} ) | ( {Digits} ( "." {OptionalDigits} )? ) [eE] [+\-]? {Digits} )

/*
ElementContentChar=  ( [^{}<&] )*
*/

/*
CommentContents= ( ( [^(:] | '('+ [^(:] | ':'+ [^:)] )+ | '(' ) '('* & '(' | ( ( [^(:] | '('+ [^(:] | ':'+ [^:)] )+ | ':' ) ':'* & ':'
CommentContents= ~":)"
*/
/* FIXME: Need to properly parse nested comments */
CommentContents=  ( [^(:] | \( [^:] | ([:]+ [^)]) )*
XmlCommentContents=  ( [^-] | '-' [^-] )*
CdataContents=  ( [^\]] | ']' [^\]] | ']' ']' [^\]] )*
PragmaContents=  ( [^#)] | '#'+ [^)] )*
PiContents=  ( [^?>] | '?'+ [^>] )*

/* NUMBER={DIGIT}+ | "\u" {HEX}+ */

/*
DecimalLiteral="." Digits | Digits "." OptionalDigits
DoubleLiteral=( "." Digits | Digits ( "." OptionalDigits )? ) [eE] ( "+" | "-" )? Digits
*/

/*
ESC="\\" ( [^] | "u" {Hex}{Hex}{Hex}{Hex} )
CHAR={ESC} | [^\r\n\'\"\\]
*/

/*
HighChars=[\uE000-\uFFFD] | [\u10000-\u10FFFF]
Char=\u0009 | \u000A | \u000D | [\u0020-\uD7FF] | {HighChars}
NotDash={Char} - '-'
ElementContentChar=\u0009 | \u000A | \u000D | [\u0020-\u0025] | [\u0027-\u003b] | [\u003d-\u007a] | \u007c | [\u007e-\uD7FF] | {HighChars}
QuotAttrContentChar=\u0009 | \u000A | \u000D | [\u0020-\u0021] | [\u0023-\u0025] | [\u0027-\u003b] | [\u003d-\u007a] | \u007c | [\u007e-\uD7FF] | {HighChars}
AposAttrContentChar=\u0009 | \u000A | \u000D | [\u0020-\u0025] | [\u0028-\u003b] | [\u003d-\u007a] | \u007c | [\u007e-\uD7FF] | {HighChars}
*/

/* FIXME: This probably belongs in the completion contributor class extension */
XFunctionQname=( "attribute" | "namespace" | "binary" | "comment" | "document-node" | "element" | "empty-sequence" | "if" | "item" | "node" | "processing-instruction" | "schema-attribute" | "schema-element" | "text" | "typeswitch" )
XFunctionName=( "ancestor" | "ancestor-or-self" | "and" | "ascending" | "case" | "cast" | "castable" | "catch" | "child" | "collation" | "declare" | "private" | "default" | "descendant" | "descendant-or-self" | "descending" | "div" | "document" | "else" | "empty" | "eq" | "every" | "except" | "following" | "following-sibling" | "for" | "ge" | "gt" | "idiv" | "import" | "instance" | "intersect" | "is" | "le" | "let" | "lt" | "mod" | "module" | "ne" | "or" | "order" | "ordered" | "parent" | "preceding" | "preceding-sibling" | "property" | "return" | "satisfies" | "self" | "some" | "stable" | "to" | "treat" | "try" | "union" | "unordered" | "validate" | "where" | "xquery" )
OpNCName=( "and" | "ascending" | "case" | "cast" | "castable" | "collation" | "default" | "descending" | "div" | "else" | "empty" | "eq" | "except" | "for" | "ge" | "gt" | "idiv" | "instance" | "intersect" | "is" | "le" | "let" | "lt" | "mod" | "ne" | "or" | "order" | "return" | "satisfies" | "stable" | "to" | "treat" | "union" | "where" | "version" | "variable" | "function" | "as" )
Keyword=( {XFunctionQname} | {XFunctionName} | {OpNCName} )

id=([A-Za-z\_] [A-Za-z0-9\-\_]*)

/*
Nmstart               ({Letter}|_)
Nmchar                ({Letter}|{Digit}|[._-])
NCName                {Nmstart}{Nmchar}*
QName                 ({NCName}":")?{NCName}
SchemaGlobalTypeName  type\({SO}{QName}{SO}\)
SchemaGlobalContext   ({QName}|{SchemaGlobalTypeName})
Char                  [\x09\x0D\x0A\x20-\xFD]
NumericLiteral        ((\.[0-9]+)|([0-9]+(\.[0-9]+)?))([eE][+-]?[0-9]+)?
StringLiteral         (\"((\"\")|[^"])*\")|(\'((\'\')|[^'])*\')
*/


%state STRING_QUOTE
%state STRING_APOST
%state COMMENT
%state XML_COMMENT
%state CDATA
%state PRAGMA
%state PRAGMA_QNAME
%state PI
%state PI_TARGET

%state START_TAG
%state END_TAG
%state ELEM_CONTENT

%%

<START_TAG> {
  "{" { yypushState(YYINITIAL); return XqyTypes.XQY_LEFT_BRACE; }
  ">" { yypopState(); yypushState(ELEM_CONTENT); return XqyTypes.XQY_GT; }
  "/>" { yypopState(); return XqyTypes.XQY_EMPTY_TAG_END; }
  {id} { return XqyTypes.XQY_ID; }
  ":" { return XqyTypes.XQY_COLON; }
  "=" { return XqyTypes.XQY_EQUAL; }
  {S} { return com.intellij.psi.TokenType.WHITE_SPACE; }
  "\"" { yypushState(STRING_QUOTE); }
  "'"  { yypushState(STRING_APOST); }
  [^ \t\r\n] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<ELEM_CONTENT> {
  "{" { yypushState(YYINITIAL); return XqyTypes.XQY_LEFT_BRACE; }
  "<" {yypushState(START_TAG); return XqyTypes.XQY_LT; }
  "</" { yypopState(); yypushState(END_TAG); return XqyTypes.XQY_END_TAG_START; }
  "{{" | "}}" | [^{}<]+ { return XqyTypes.XQY_ELEMENT_CONTENT_CHAR; }
  "<![CDATA[" { yypushState(CDATA); return XqyTypes.XQY_CDATA_START; }
  "<!--" { yypushState(XML_COMMENT); return XqyTypes.XQY_XML_COMMENT_START; }
  "<?" { yypushState(PI_TARGET); return XqyTypes.XQY_PI_START; }
}

<END_TAG> {
  "{" { yypushState(YYINITIAL); return XqyTypes.XQY_LEFT_BRACE; }
  ">" { yypopState(); return XqyTypes.XQY_GT; }
  {id} { return XqyTypes.XQY_ID; }
  ":" { return XqyTypes.XQY_COLON; }
  [^ \t\r\n] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<STRING_QUOTE> {
  \"\"      { string.append(yytext()) ; }
  [^\r\n\"\\]+  { string.append(yytext()); }
  \n       { string.append(yytext()); return XqyTypes.XQY_STRING; }
  \r       { string.append(yytext()); return XqyTypes.XQY_STRING; }
  \\        { string.append(yytext()); }
  \"        { yypopState(); return XqyTypes.XQY_STRING; }
}

<STRING_APOST> {
  \'\'      { string.append(yytext()) ; }
  [^\r\n\'\\]+  { string.append(yytext()); }
  \n       { string.append(yytext()); return XqyTypes.XQY_STRING; }
  \r       { string.append(yytext()); return XqyTypes.XQY_STRING; }
  \\        { string.append(yytext()); }
  \'        { yypopState(); return XqyTypes.XQY_STRING; }
}

<COMMENT> {
  {CommentContents} { return XqyTypes.XQY_COMMENT_CONTENTS; }
  "(:" { yypushState(COMMENT); return XqyTypes.XQY_COMMENT_START; }
  ":)" { yypopState(); return XqyTypes.XQY_COMMENT_END; }
}

<XML_COMMENT> {
  {XmlCommentContents} { return XqyTypes.XQY_XML_COMMENT_CONTENTS;}
  "-->" { yypopState(); return XqyTypes.XQY_XML_COMMENT_END; }
}

<PI> {
  {PiContents} { return XqyTypes.XQY_PI_CONTENTS;}
  "?>" { yypopState(); return XqyTypes.XQY_PI_END; }
}
<PI_TARGET> {
  {id} { yypopState(); yypushState(PI); return XqyTypes.XQY_ID; }
}

<CDATA> {
  {CdataContents} { return XqyTypes.XQY_CDATA_CONTENTS;}
  "]]>" { yypopState(); return XqyTypes.XQY_CDATA_END; }
}

<PRAGMA> {
  {PragmaContents} { return XqyTypes.XQY_PRAGMA_CONTENTS;}
  "#)" { yypopState(); return XqyTypes.XQY_PRAGMA_END; }
}
<PRAGMA_QNAME> {
/* FIXME
  ({id} ":" {id}) { yybegin(PRAGMA); return XqyTypes.XQY_ID; }
*/
  {id} { yybegin(PRAGMA); return XqyTypes.XQY_ID; }
  {S} { return com.intellij.psi.TokenType.WHITE_SPACE; }
}

<YYINITIAL> {

  {S} { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "\""   { string.setLength(0); yypushState(STRING_QUOTE); }
  "'"  { string.setLength(0); yypushState(STRING_APOST); }

  "(:" { yypushState(COMMENT); return XqyTypes.XQY_COMMENT_START; }
  "<!--" { yypushState(XML_COMMENT); return XqyTypes.XQY_XML_COMMENT_START; }
  "<?" { yypushState(PI_TARGET); return XqyTypes.XQY_PI_START; }
  "(#" { yypushState(PRAGMA_QNAME); return XqyTypes.XQY_PRAGMA_START; }
  "<![CDATA[" { yypushState(CDATA); return XqyTypes.XQY_CDATA_START; }

  {Keyword} { return XqyTypes.XQY_KEYWORD; }

  ( {DoubleLiteral} | {DecimalLiteral} | {Digits} ) { return XqyTypes.XQY_NUMBER; }  /* NumericLiteral */

  {id} { return XqyTypes.XQY_ID; }  /* non-keyword NCName */

  "//" { return XqyTypes.XQY_SLASH_SLASH; }
  "/" { return XqyTypes.XQY_SLASH; }
  ":=" { return XqyTypes.XQY_OP_BIND; }
  "::" { return XqyTypes.XQY_AXIS; }

  "</" { return XqyTypes.XQY_END_TAG_START; }
  "/>" { return XqyTypes.XQY_EMPTY_TAG_END; }

  "&#" { return XqyTypes.XQY_CHAR_REF_START; }
  "&#x" { return XqyTypes.XQY_CHAR_HEX_REF_START; }

  "(" { return XqyTypes.XQY_LEFT_PAREN; }
  ")" { return XqyTypes.XQY_RIGHT_PAREN; }

  "{" { yypushState(YYINITIAL); return XqyTypes.XQY_LEFT_BRACE; }
  "}" { yypopState(); return XqyTypes.XQY_RIGHT_BRACE; }

  "[" { return XqyTypes.XQY_LEFT_BRACKET; }
  "]" { return XqyTypes.XQY_RIGHT_BRACKET; }

  "<" { yypushState(START_TAG); return XqyTypes.XQY_LT; }
  ">" { return XqyTypes.XQY_GT; }

  "&" { return XqyTypes.XQY_AMP; }
  "@" { return XqyTypes.XQY_AT_SIGN; }

  ";" { return XqyTypes.XQY_SEMICOLON; }
  ":" { return XqyTypes.XQY_COLON; }
  "," { return XqyTypes.XQY_COMMA; }
  "." { return XqyTypes.XQY_DOT; }
  ".." { return XqyTypes.XQY_DOTDOT; }
  "$" { return XqyTypes.XQY_DOLLAR; }
  "=" { return XqyTypes.XQY_EQUAL; }
  "!=" { return XqyTypes.XQY_NOT_EQUAL; }
  "<=" { return XqyTypes.XQY_LESS_EQUAL; }
  ">=" { return XqyTypes.XQY_GREATER_EQUAL; }
  "?" { return XqyTypes.XQY_QMARK; }
  "|" { return XqyTypes.XQY_VERT_BAR; }
  "+" { return XqyTypes.XQY_PLUS_SIGN; }
  "*" { return XqyTypes.XQY_STAR; }

  "-" { return XqyTypes.XQY_MINUS_SIGN; }


/*
  [^] {yybegin(YYINITIAL); return com.intellij.psi.TokenType.BAD_CHARACTER; }
*/
}

.|\n {yypopState(); return com.intellij.psi.TokenType.BAD_CHARACTER; }

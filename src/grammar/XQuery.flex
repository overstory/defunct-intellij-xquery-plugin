package uk.co.overstory.xquery.parser;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import uk.co.overstory.xquery.psi.XqyTypes;

%%

%{
  StringBuffer string = new StringBuffer();

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


AsciiAlpha=[A-Za-z]+
Alpha=[:letter:]
Digit=[:digit:]
Digits={Digit}+
OptionalDigits={Digit}*

Hex={Digit} | [aAbBcCdDeEfF]

DecimalLiteral= ( ('.' {Digits}) | ( {Digits} '.' {OptionalDigits} ) )
DoubleLiteral= ( ( "." {Digits} ) | ( {Digits} ( "." {OptionalDigits} )? ) [eE] [+\-]? {Digits} )


/*
CommentContents= ( ( [^(:] | '('+ [^(:] | ':'+ [^:)] )+ | '(' ) '('* & '(' | ( ( [^(:] | '('+ [^(:] | ':'+ [^:)] )+ | ':' ) ':'* & ':'
CommentContents= ~":)"
*/
/* FIXME: Need to properly parse nested comments */
CommentContents=  ( [^(:] | ([:]+ [^)]) )*
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

id=([A-Za-z\_] [A-Za-z0-9\.\-\_]*)


%state STRING_QUOTE
%state STRING_APOST
%state COMMENT
%state XML_COMMENT
%state CDATA
%state PRAGMA
%state PRAGMA_QNAME
%state PI
%state PI_TARGET

%%

/* TODO: Need state for XML comments?  CDATA?  Pragmas? */

<STRING_QUOTE> {
  \"                             { yybegin(YYINITIAL); return XqyTypes.XQY_STRING; }
  \"\"                           { string.append('"') ; }
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\n                            { string.append('\n'); }
  \\r                            { string.append('\r'); }
  \\                             { string.append('\\'); }
}

<STRING_APOST> {
  \'                             { yybegin(YYINITIAL); return XqyTypes.XQY_STRING; }
  \'\'                           { string.append("'") ; }
  [^\n\r\'\\]+                   { string.append( yytext() ); }
  \\n                            { string.append('\n'); }
  \\r                            { string.append('\r'); }
  \\                             { string.append('\\'); }
}

<COMMENT> {
  {CommentContents} { return XqyTypes.XQY_COMMENT_CONTENTS;}
  ":)" { yybegin(YYINITIAL); return XqyTypes.XQY_COMMENT_END; }
}

<XML_COMMENT> {
  {XmlCommentContents} { return XqyTypes.XQY_XML_COMMENT_CONTENTS;}
  "-->" { yybegin(YYINITIAL); return XqyTypes.XQY_XML_COMMENT_END; }
}

<PI> {
  {PiContents} { return XqyTypes.XQY_PI_CONTENTS;}
  "?>" { yybegin(YYINITIAL); return XqyTypes.XQY_PI_END; }
}
<PI_TARGET> {
  {id} { yybegin(PI); return XqyTypes.XQY_ID; }
}

<CDATA> {
  {CdataContents} { return XqyTypes.XQY_CDATA_CONTENTS;}
  "]]>" { yybegin(YYINITIAL); return XqyTypes.XQY_CDATA_END; }
}

<PRAGMA> {
  {PragmaContents} { return XqyTypes.XQY_PRAGMA_CONTENTS;}
  "#)" { yybegin(YYINITIAL); return XqyTypes.XQY_PRAGMA_END; }
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

  \"  { string.setLength(0); yybegin(STRING_QUOTE); }
  \'  { string.setLength(0); yybegin(STRING_APOST); }
  "(:" { yybegin(COMMENT); return XqyTypes.XQY_COMMENT_START; }
  "<!--" { yybegin(XML_COMMENT); return XqyTypes.XQY_XML_COMMENT_START; }
  "<?" { yybegin(PI_TARGET); return XqyTypes.XQY_PI_START; }
  "(#" { yybegin(PRAGMA_QNAME); return XqyTypes.XQY_PRAGMA_START; }
  "<![CDATA[" { yybegin(CDATA); return XqyTypes.XQY_CDATA_START; }

  {Keyword} { return XqyTypes.XQY_KEYWORD; }

  ( {DoubleLiteral} | {DecimalLiteral} | {Digits} ) { return XqyTypes.XQY_NUMBER; }  /* NumericLiteral */

  {id} { return XqyTypes.XQY_ID; }  /* non-keyword NCName */


  "//" {yybegin(YYINITIAL); return XqyTypes.XQY_SLASH_SLASH; }
  "/" {yybegin(YYINITIAL); return XqyTypes.XQY_SLASH; }
  ":=" {yybegin(YYINITIAL); return XqyTypes.XQY_OP_BIND; }
  "::" {yybegin(YYINITIAL); return XqyTypes.XQY_AXIS; }
  "</" {yybegin(YYINITIAL); return XqyTypes.XQY_END_TAG_START; }
  "/>" {yybegin(YYINITIAL); return XqyTypes.XQY_EMPTY_TAG_END; }
  "&#" {yybegin(YYINITIAL); return XqyTypes.XQY_CHAR_REF_START; }
  "&#x" {yybegin(YYINITIAL); return XqyTypes.XQY_CHAR_HEX_REF_START; }

  "(" {yybegin(YYINITIAL); return XqyTypes.XQY_LEFT_PAREN; }
  ")" {yybegin(YYINITIAL); return XqyTypes.XQY_RIGHT_PAREN; }
  "{" {yybegin(YYINITIAL); return XqyTypes.XQY_LEFT_BRACE; }
  "}" {yybegin(YYINITIAL); return XqyTypes.XQY_RIGHT_BRACE; }
  "[" {yybegin(YYINITIAL); return XqyTypes.XQY_LEFT_BRACKET; }
  "]" {yybegin(YYINITIAL); return XqyTypes.XQY_RIGHT_BRACKET; }
  "<" {yybegin(YYINITIAL); return XqyTypes.XQY_LT; }
  ">" {yybegin(YYINITIAL); return XqyTypes.XQY_GT; }
  "&" {yybegin(YYINITIAL); return XqyTypes.XQY_AMP; }
  "@" {yybegin(YYINITIAL); return XqyTypes.XQY_AT_SIGN; }

  ";" {yybegin(YYINITIAL); return XqyTypes.XQY_SEMICOLON; }
  ":" {yybegin(YYINITIAL); return XqyTypes.XQY_COLON; }
  "," {yybegin(YYINITIAL); return XqyTypes.XQY_COMMA; }
  "." {yybegin(YYINITIAL); return XqyTypes.XQY_DOT; }
  ".." {yybegin(YYINITIAL); return XqyTypes.XQY_DOTDOT; }
  "$" {yybegin(YYINITIAL); return XqyTypes.XQY_DOLLAR; }
  "=" {yybegin(YYINITIAL); return XqyTypes.XQY_EQUAL; }
  "!=" {yybegin(YYINITIAL); return XqyTypes.XQY_NOT_EQUAL; }
  "<=" {yybegin(YYINITIAL); return XqyTypes.XQY_LESS_EQUAL; }
  ">=" {yybegin(YYINITIAL); return XqyTypes.XQY_GREATER_EQUAL; }
  "?" {yybegin(YYINITIAL); return XqyTypes.XQY_QMARK; }
  "|" {yybegin(YYINITIAL); return XqyTypes.XQY_VERT_BAR; }
  "+" {yybegin(YYINITIAL); return XqyTypes.XQY_PLUS_SIGN; }
  "*" {yybegin(YYINITIAL); return XqyTypes.XQY_STAR; }

  "-" {yybegin(YYINITIAL); return XqyTypes.XQY_MINUS_SIGN; }


/*
  [^] {yybegin(YYINITIAL); return com.intellij.psi.TokenType.BAD_CHARACTER; }
*/
}

.|\n {yybegin(YYINITIAL); return com.intellij.psi.TokenType.BAD_CHARACTER; }

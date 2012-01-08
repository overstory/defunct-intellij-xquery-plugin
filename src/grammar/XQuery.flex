package uk.co.overstory.xquery.parser;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import uk.co.overstory.xquery.psi.XqyTypes;
import static uk.co.overstory.xquery.XqyParserDefinition.XQY_BLOCK_COMMENT;

%%

%{
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

S=( #x0020 | #x0009 | #x000D | #x000A )+

COMMENT_CONTENTS=( ( [^(:] | "("+ [^(:] | ":"+ [^:)] )+ | "(" ) "("* & "(" | ( ( [^(:] | "("+ [^(:] | ":"+ [^:)] )+ | ":" ) ":"* & ":"
/* BLOCK_COMMENT="(:" ( {COMMENT_CONTENTS} | {BLOCK_COMMENT} )* ":)" */
BLOCK_COMMENT="(:" ( {COMMENT_CONTENTS}  )* ":)"

AsciiAlpha=[A-Za-z]+
Alpha=[:letter:]
Digit=[:digit:]
Digits={Digit}+
OptionalDigits={Digit}*

Hex={Digit} | [aAbBcCdDeEfF]

/*
ID_BODY={Alpha} | {Digit} | "_"
ID={Alpha} ({ID_BODY}) * | "<" ([^<>])+ ">"
*/
/* NUMBER={DIGIT}+ | "0x" {HEX}+ */

/*
DecimalLiteral="." Digits | Digits "." OptionalDigits
DoubleLiteral=( "." Digits | Digits ( "." OptionalDigits )? ) [eE] ( "+" | "-" )? Digits
*/

/*
ESC="\\" ( [^] | "u" {Hex}{Hex}{Hex}{Hex} )
CHAR={ESC} | [^\r\n\'\"\\]
*/

HighChars=[#xE000-#xFFFD] | [#x10000-#x10FFFF]
Char=#x0009 | #x000A | #x000D | [#x0020-#xD7FF] | {HighChars}
NotDash={Char} - '-'
ElementContentChar=#x0009 | #x000A | #x000D | [#x0020-#x0025] | [#x0027-#x003b] | [#x003d-#x007a] | #x007c | [#x007e-#xD7FF] | {HighChars}
QuotAttrContentChar=#x0009 | #x000A | #x000D | [#x0020-#x0021] | [#x0023-#x0025] | [#x0027-#x003b] | [#x003d-#x007a] | #x007c | [#x007e-#xD7FF] | {HighChars}
AposAttrContentChar=#x0009 | #x000A | #x000D | [#x0020-#x0025] | [#x0028-#x003b] | [#x003d-#x007a] | #x007c | [#x007e-#xD7FF] | {HighChars}

STRING_BAD1=\" ({Char} | \') *
STRING_BAD2=\' {Char} *
String={STRING_BAD1} \" | {STRING_BAD2} \'

BAD_TOKENS={STRING_BAD1} | {STRING_BAD2}


%%
<YYINITIAL> {
  {S} {yybegin(YYINITIAL); return com.intellij.psi.TokenType.WHITE_SPACE; }
  {BLOCK_COMMENT} {yybegin(YYINITIAL); return XQY_BLOCK_COMMENT; }

  "\"\"" {yybegin(YYINITIAL); return XqyTypes.XQY_ESCAPEQUOT; }
  "''" {yybegin(YYINITIAL); return XqyTypes.XQY_ESCAPEAPOS; }
  "\"" {yybegin(YYINITIAL); return XqyTypes.XQY_DBL_QUOTE; }
  "'" {yybegin(YYINITIAL); return XqyTypes.XQY_APOST; }

  {Digits} {yybegin(YYINITIAL); return XqyTypes.XQY_DIGITS; }
  {OptionalDigits} {yybegin(YYINITIAL); return XqyTypes.XQY_OPTIONALDIGITS; }
  {Hex} {yybegin(YYINITIAL); return XqyTypes.XQY_HEX; }

  {AsciiAlpha} {yybegin(YYINITIAL); return XqyTypes.XQY_ASCIIALPHA; }

/*
  {Alpha} {yybegin(YYINITIAL); return XqyTypes.XQY_ALPHA; }
  {ID} {yybegin(YYINITIAL); return XqyTypes.XQY_ID; }
  {String} {yybegin(YYINITIAL); return XqyTypes.XQY_STRING; }
*/

  "<?" {yybegin(YYINITIAL); return XqyTypes.XQY_PI_START; }
  "?>" {yybegin(YYINITIAL); return XqyTypes.XQY_PI_END; }
  "(#" {yybegin(YYINITIAL); return XqyTypes.XQY_PRAGMA_START; }
  "#)" {yybegin(YYINITIAL); return XqyTypes.XQY_PRAGMA_END; }
  "<!--" {yybegin(YYINITIAL); return XqyTypes.XQY_XML_COMMENT_START; }
  "-->" {yybegin(YYINITIAL); return XqyTypes.XQY_XML_COMMENT_END; }
  "<![CDATA[" {yybegin(YYINITIAL); return XqyTypes.XQY_CDATA_START; }
  "]>" {yybegin(YYINITIAL); return XqyTypes.XQY_CDATA_END; }

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
  "#" {yybegin(YYINITIAL); return XqyTypes.XQY_HASH; }
  "@" {yybegin(YYINITIAL); return XqyTypes.XQY_AT_SIGN; }

  ";" {yybegin(YYINITIAL); return XqyTypes.XQY_SEMICOLON; }
  ":" {yybegin(YYINITIAL); return XqyTypes.XQY_COLON; }
  "," {yybegin(YYINITIAL); return XqyTypes.XQY_COMMA; }
  "." {yybegin(YYINITIAL); return XqyTypes.XQY_DOT; }
  ".." {yybegin(YYINITIAL); return XqyTypes.XQY_DOTDOT; }
  "$" {yybegin(YYINITIAL); return XqyTypes.XQY_DOLLAR; }
  "_" {yybegin(YYINITIAL); return XqyTypes.XQY_UNDERSCORE; }
  "=" {yybegin(YYINITIAL); return XqyTypes.XQY_EQUAL; }
  "!=" {yybegin(YYINITIAL); return XqyTypes.XQY_NOT_EQUAL; }
  "<=" {yybegin(YYINITIAL); return XqyTypes.XQY_LESS_EQUAL; }
  ">=" {yybegin(YYINITIAL); return XqyTypes.XQY_GREATER_EQUAL; }
  "?" {yybegin(YYINITIAL); return XqyTypes.XQY_QMARK; }
  "|" {yybegin(YYINITIAL); return XqyTypes.XQY_VERT_BAR; }
  "+" {yybegin(YYINITIAL); return XqyTypes.XQY_PLUS_SIGN; }
  "-" {yybegin(YYINITIAL); return XqyTypes.XQY_PLUS_SIGN; }
  "*" {yybegin(YYINITIAL); return XqyTypes.XQY_STAR; }
  "'" {yybegin(YYINITIAL); return XqyTypes.XQY_APOST; }
  "\"" {yybegin(YYINITIAL); return XqyTypes.XQY_DBL_QUOTE; }

  {ElementContentChar} {yybegin(YYINITIAL); return XqyTypes.XQY_ELEMENTCONTENTCHAR; }
  {QuotAttrContentChar} {yybegin(YYINITIAL); return XqyTypes.XQY_QUOTATTRCONTENTCHAR; }
  {AposAttrContentChar} {yybegin(YYINITIAL); return XqyTypes.XQY_APOSATTRCONTENTCHAR; }

  {NotDash} {yybegin(YYINITIAL); return XqyTypes.XQY_NOTDASH; }

  {BAD_TOKENS} {yybegin(YYINITIAL); return com.intellij.psi.TokenType.BAD_CHARACTER; }
  [^] {yybegin(YYINITIAL); return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

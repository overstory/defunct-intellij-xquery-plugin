package uk.co.overstory.xquery.editor;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import uk.co.overstory.xquery.XqyParserDefinition;
import uk.co.overstory.xquery.parser.XqyLexer;

import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static uk.co.overstory.xquery.psi.XqyTypes.*;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 7:05 PM
 */
public class XqySyntaxHighlighter extends SyntaxHighlighterBase
{
	private static final TextAttributesKey[] EMPTY = new TextAttributesKey[0];
	public static final TextAttributesKey ILLEGAL = createTextAttributesKey ("XQY_ILLEGAL", SyntaxHighlighterColors.INVALID_STRING_ESCAPE.getDefaultAttributes ());
	public static final TextAttributesKey COMMENT = createTextAttributesKey("XQY_COMMENT", SyntaxHighlighterColors.JAVA_BLOCK_COMMENT.getDefaultAttributes());
	public static final TextAttributesKey STRING = createTextAttributesKey("XQY_STRING", SyntaxHighlighterColors.STRING.getDefaultAttributes());
	public static final TextAttributesKey NUMBER = createTextAttributesKey("XQY_NUMBER", SyntaxHighlighterColors.NUMBER.getDefaultAttributes());
	public static final TextAttributesKey KEYWORD = createTextAttributesKey ("XQY_KEYWORD", SyntaxHighlighterColors.KEYWORD.getDefaultAttributes ());
	public static final TextAttributesKey PARENTHS = createTextAttributesKey("XQY_PARENTHS", SyntaxHighlighterColors.PARENTHS.getDefaultAttributes());
	public static final TextAttributesKey BRACES = createTextAttributesKey("XQY_BRACES", SyntaxHighlighterColors.BRACES.getDefaultAttributes());
	public static final TextAttributesKey BRACKETS = createTextAttributesKey("XQY_BRACKETS", SyntaxHighlighterColors.BRACKETS.getDefaultAttributes());
	public static final TextAttributesKey ANGLES = createTextAttributesKey("XQY_ANGLES", SyntaxHighlighterColors.BRACES.getDefaultAttributes());
	public static final TextAttributesKey COMMA = createTextAttributesKey ("XQY_COMMA", SyntaxHighlighterColors.COMMA.getDefaultAttributes ());
	public static final TextAttributesKey DOT = createTextAttributesKey ("XQY_DOT", SyntaxHighlighterColors.DOT.getDefaultAttributes ());
	public static final TextAttributesKey SEMICOLON = createTextAttributesKey ("XQY_SEMICOLON", SyntaxHighlighterColors.JAVA_SEMICOLON.getDefaultAttributes ());

	@NotNull
	@Override
	public Lexer getHighlightingLexer ()
	{
		return new XqyLexer();
	}

	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights (IElementType type)
	{
//System.out.println ("Type: " + type.toString ());

		if (type == TokenType.BAD_CHARACTER) {
			return pack (ILLEGAL);
		}
		if (type == XQY_COMMENT) {
			return pack (COMMENT);
		}
		if (type == XQY_STRING) {
			return pack (STRING);
		}
		if ((type == XQY_KEYWORD) || (type == XQY_FUNCTIONQNAME) || (type == XQY_FUNCTIONNAME) || (type == XQY_OPNCNAME)) {
			return pack (KEYWORD);
		}
		if (type == XQY_COMMA) {
			return pack (COMMA);
		}
		if (type == XQY_DOT) {
			return pack (DOT);
		}
		if (type == XQY_SEMICOLON) {
			return pack (SEMICOLON);
		}
		if (type == XQY_LEFT_PAREN || type == XQY_RIGHT_PAREN) {
			return pack(PARENTHS);
		}
		if (type == XQY_LEFT_BRACE || type == XQY_RIGHT_BRACE) {
			return pack(BRACES);
		}
		if (type == XQY_LEFT_BRACKET || type == XQY_RIGHT_BRACKET) {
			return pack(BRACKETS);
		}
		if (type == XQY_LEFT_BRACKET || type == XQY_RIGHT_BRACKET) {
			return pack(BRACKETS);
		}
		if (type == XQY_LT || type == XQY_GT) {
			return pack(ANGLES);
		}
		if (type == XQY_NUMBER || type == XQY_DIGITS || type == XQY_NUMERICLITERAL) {
			return pack(NUMBER);
		}

		return EMPTY;
	}
}

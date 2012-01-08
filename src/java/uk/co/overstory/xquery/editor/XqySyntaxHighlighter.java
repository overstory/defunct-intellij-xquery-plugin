package uk.co.overstory.xquery.editor;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.tree.IElementType;
import uk.co.overstory.xquery.parser.XqyLexer;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 7:05 PM
 */
public class XqySyntaxHighlighter implements SyntaxHighlighter
{
	private static final TextAttributesKey[] EMPTY = new TextAttributesKey[0];

	@NotNull
	@Override
	public Lexer getHighlightingLexer ()
	{
		return new XqyLexer();
	}

	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights (IElementType iElementType)
	{
		return EMPTY;
	}
}

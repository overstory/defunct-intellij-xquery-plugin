package uk.co.overstory.xquery.editor;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.TokenType;
import uk.co.overstory.xquery.psi.XqyTypes;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/10/12
 * Time: 10:07 PM
 */
public class XqyQuoteHandler extends SimpleTokenSetQuoteHandler
{
	public XqyQuoteHandler()
	{
		super (XqyTypes.XQY_STRING, TokenType.BAD_CHARACTER);
	}
}

package uk.co.overstory.xquery.refactor;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.ObjectPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.refactoring.rename.RenameInputValidator;
import com.intellij.util.ProcessingContext;
import uk.co.overstory.xquery.parser.XqyLexer;
import uk.co.overstory.xquery.psi.XqyCompositeElement;
import uk.co.overstory.xquery.psi.XqyTypes;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/12/12
 * Time: 5:50 PM
 */
public class XqyRenameInputValidator implements RenameInputValidator
{
	@Override
	public ElementPattern<? extends PsiElement> getPattern ()
	{
		return new QNamePattern();
	}

	// FIXME: This is probably too simplistic
	// FIXME: Need to be context aware - ie, rename namespace prefix should not allow ":"
	@Override
	public boolean isInputValid (String newName, PsiElement element, ProcessingContext context)
	{
		final XqyLexer lexer = new XqyLexer();

		lexer.start (newName, 0, newName.length(), 0);

		IElementType token = lexer.getTokenType();

		if (token != XqyTypes.XQY_ID) return false;

		lexer.advance();
		token = lexer.getTokenType();

		if (token == null) return true;

		if (token != XqyTypes.XQY_COLON) return false;

		lexer.advance();
		token = lexer.getTokenType();

		return lexer.getTokenType() == XqyTypes.XQY_ID;
	}

	private class QNamePattern extends ObjectPattern<XqyCompositeElement, QNamePattern>
	{
		protected QNamePattern ()
		{
			super (XqyCompositeElement.class);
		}
	}
}

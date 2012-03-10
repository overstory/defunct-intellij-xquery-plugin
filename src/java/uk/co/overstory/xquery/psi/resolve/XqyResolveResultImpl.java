package uk.co.overstory.xquery.psi.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 2/29/12
 * Time: 7:11 PM
 */
public class XqyResolveResultImpl implements ResolveResult
{
	private final boolean valid;
	private final PsiElement element;

	public XqyResolveResultImpl (PsiElement element, boolean valid)
	{
		this.element = element;
		this.valid = valid;
	}

	@Override
	public PsiElement getElement ()
	{
		return element;
	}

	@Override
	public boolean isValidResult ()
	{
		return valid;
	}
}

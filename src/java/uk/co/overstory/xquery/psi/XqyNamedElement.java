package uk.co.overstory.xquery.psi;

import com.intellij.psi.PsiNameIdentifierOwner;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 1:26 AM
 */
public interface XqyNamedElement extends XqyCompositeElement, PsiNameIdentifierOwner
{
	@NotNull
	public XqyQName getQName();
}

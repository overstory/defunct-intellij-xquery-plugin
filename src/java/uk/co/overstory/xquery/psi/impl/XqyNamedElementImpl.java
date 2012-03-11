package uk.co.overstory.xquery.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import uk.co.overstory.xquery.psi.XqyNamedElement;
import uk.co.overstory.xquery.psi.XqyQName;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 12:14 PM
 */
public class XqyNamedElementImpl extends XqyCompositeElementImpl implements XqyNamedElement
{
	public XqyNamedElementImpl (ASTNode node)
	{
		super (node);
//System.out.println ("XqyNamedElementImpl constructor: " + node.getText());
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException
	{
//System.out.println ("XqyNamedElementImpl.setName name=" + name + ", type=" + toString());
		return getQName().replace (XqyElementFactory.createQNameFromText (getProject(), name));
	}

	@Override
	public String getName() {
		return getQName().getText();
	}

	@Override
	public PsiElement getNameIdentifier()
	{
		return getQName();
	}

	@Override
	@NotNull
	public XqyQName getQName() {
		return PsiTreeUtil.getChildOfType (this, XqyQName.class);
	}

	// -------------------------------------------------------------

//	@NotNull
//	private PsiElement getId()
//	{
//		XqyQName qname = getQName();
//		XqyPrefixedName prefixed = qname.getPrefixedName();
//		XqyUnprefixedName unprefixed = qname.getUnprefixedName();
//		XqyLocalPart localPart = (prefixed == null) ? unprefixed.getLocalPart() : prefixed.getLocalPart();
//
//		return localPart.getNCName().getId();
//	}

	@Override
	public String toString() {
		return super.toString() + "/" + getName();
	}
}

package uk.co.overstory.xquery.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import uk.co.overstory.xquery.psi.XqyNamedElement;
import uk.co.overstory.xquery.psi.XqyPrefixedName;
import uk.co.overstory.xquery.psi.XqyQName;
import uk.co.overstory.xquery.psi.XqyUnprefixedName;

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
	private volatile String myCachedName;

	public XqyNamedElementImpl (ASTNode node)
	{
		super (node);
//System.out.println ("XqyNamedElementImpl constructor: " + node.getText());
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException
	{
System.out.println ("XqyNamedElementImpl.setName name=" + name);
		getId().replace (XqyElementFactory.createLeafFromText (getProject(), name));

		return this;
	}

	@Override
	public String getName() {
		if (myCachedName == null) {
			myCachedName = getId().getText();
		}
//System.out.println ("XqyNamedElementImpl.getName myCachedName=" + myCachedName);
		return myCachedName;
	}

	@Override
	public PsiElement getNameIdentifier()
	{
		return getId();
	}

	@Override
	@NotNull
	public XqyQName getQName() {
		return PsiTreeUtil.getChildOfType (this, XqyQName.class);
	}


	// -------------------------------------------------------------

	@NotNull
	private PsiElement getId()
	{
		XqyPrefixedName prefixedname = getQName().getPrefixedName ();
		XqyUnprefixedName unprefixedname = getQName().getUnprefixedName();

		if (prefixedname != null) {
			// TODO: Handle namespace prefix
			return prefixedname.getLocalPart().getNCName().getId();
		}

		return unprefixedname.getLocalPart().getNCName().getId();
	}

	@Override
	public String toString() {
		return super.toString() + ":" + getName();
	}
}

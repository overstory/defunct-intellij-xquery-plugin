package uk.co.overstory.xquery.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import uk.co.overstory.xquery.psi.XqyNamedElement;
import uk.co.overstory.xquery.psi.XqyTypes;

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
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException {
		getId().replace(XqyElementFactory.createLeafFromText(getProject(), s));
		return this;
	}

	@Override
	public String getName() {
		if (myCachedName == null) {
			myCachedName = getId().getText();
		}
		return myCachedName;
	}

	@Override
	public PsiElement getNameIdentifier() {
		return getId();
	}

	// -------------------------------------------------------------

	@NotNull
	public PsiElement getId() {
		ASTNode child = getNode().findChildByType(XqyTypes.XQY_QNAME);
		return child == null ? null : child.getPsi();
	}


	@Override
	public String toString() {
		return super.toString() + ":" + getName();
	}

}

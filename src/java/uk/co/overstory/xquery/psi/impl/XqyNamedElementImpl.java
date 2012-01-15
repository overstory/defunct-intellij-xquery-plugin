package uk.co.overstory.xquery.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import uk.co.overstory.xquery.psi.XqyFunctiondecl;
import uk.co.overstory.xquery.psi.XqyNamedElement;
import uk.co.overstory.xquery.psi.XqyPrefixedname;
import uk.co.overstory.xquery.psi.XqyQname;
import uk.co.overstory.xquery.psi.XqyTypes;
import uk.co.overstory.xquery.psi.XqyUnprefixedname;
import uk.co.overstory.xquery.psi.XqyVardecl;

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
System.out.println ("XqyVardeclImpl constructor");
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
	public PsiElement getNameIdentifier()
	{
		return getId();
	}

	// -------------------------------------------------------------

	@NotNull
	private PsiElement getId()
	{
		if (this instanceof XqyVardecl)  {
			return qNameValue (((XqyVardecl) this).getVarname().getQname());
		}

		if (this instanceof XqyFunctiondecl)  {
			return qNameValue (((XqyFunctiondecl) this).getFunctionname().getQname());
		}

		return null;
	}

	private PsiElement qNameValue (XqyQname qNameNode)
	{
		XqyPrefixedname prefixedname = qNameNode.getPrefixedname();
		XqyUnprefixedname unprefixedname = qNameNode.getUnprefixedname();

		if (prefixedname != null) {
			// TODO: Handle namespace prefix
			return unprefixedname.getLocalpart().getNcname().getId();
		}

		return unprefixedname.getLocalpart().getNcname().getId();
	}

	@Override
	public String toString() {
		return super.toString() + ":" + getName();
	}
}

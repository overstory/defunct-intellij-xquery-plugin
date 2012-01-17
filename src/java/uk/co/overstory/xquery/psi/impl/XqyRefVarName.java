package uk.co.overstory.xquery.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import uk.co.overstory.xquery.psi.XqyQname;
import uk.co.overstory.xquery.psi.XqyRefvarname;

import org.jetbrains.annotations.NotNull;


/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/14/12
 * Time: 5:04 PM
 */
public class XqyRefVarName extends XqyQnameImpl implements XqyRefvarname
{
	public XqyRefVarName (ASTNode node)
	{
		super (node);
		System.out.println ("XqyRefVarName constructor called node=" + node.getText());
	}

	@NotNull
	@Override
	public XqyQname getQname()
	{
		return this;
	}

	@Override
	public PsiReference getReference()
	{
		return new XqyReferenceImpl<XqyRefVarName> (this, TextRange.from (0, getTextLength()))
		{
			@Override
			public PsiElement handleElementRename (String newElementName) throws IncorrectOperationException
			{
				myElement.getQname().replace (XqyElementFactory.createLeafFromText (getElement().getProject(), newElementName));

				return myElement;
			}
		};
	}

	// ------------------------------------------------------------

}

package uk.co.overstory.xquery.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import uk.co.overstory.xquery.psi.XqyQName;
import uk.co.overstory.xquery.psi.XqyRefFunctionName;

import org.jetbrains.annotations.NotNull;


/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/14/12
 * Time: 5:04 PM
 */
public class XqyFunctionNameReference extends XqyQNameImpl implements XqyRefFunctionName
{
	public XqyFunctionNameReference (ASTNode node)
	{
		super (node);
//System.out.println ("XqyFunctionNameReference constructor called node=" + node.getText());
	}

	@NotNull
	public XqyQName getQName()
	{
		return this;
	}

	@Override
	public PsiReference getReference()
	{
//System.out.println ("XqyFunctionNameReference.getReference: " + super.toString() + "/" + super.getText());

		return new XqyReferenceImpl<XqyFunctionNameReference> (this, TextRange.from (0, getTextLength()))
		{
			@Override
			public PsiElement handleElementRename (String newElementName) throws IncorrectOperationException
			{
System.out.println ("XqyFunctionNameReference.handleElementRename: " + super.myElement.getText() + " to " + newElementName);
				myElement.getQName().replace (XqyElementFactory.createLeafFromText (getElement().getProject(), newElementName));

				return myElement;
			}
		};
	}

	// ------------------------------------------------------------

}

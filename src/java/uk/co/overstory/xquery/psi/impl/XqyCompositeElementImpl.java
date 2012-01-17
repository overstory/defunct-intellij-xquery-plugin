package uk.co.overstory.xquery.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import uk.co.overstory.xquery.psi.XqyCompositeElement;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 1:15 AM
 */
public class XqyCompositeElementImpl extends ASTWrapperPsiElement implements XqyCompositeElement
{
	public XqyCompositeElementImpl (ASTNode node)
	{
		super (node);
//System.out.println ("XqyCompositeElementImpl: constructor, node=" + node);
	}

	@Override
	public String toString() {
		return getNode().getElementType().toString();
	}

}

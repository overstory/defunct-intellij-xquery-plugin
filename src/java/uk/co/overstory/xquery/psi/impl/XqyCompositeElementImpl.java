package uk.co.overstory.xquery.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import uk.co.overstory.xquery.psi.ResolveUtil;
import uk.co.overstory.xquery.psi.XqyCompositeElement;
import uk.co.overstory.xquery.psi.XqyExpr;
import uk.co.overstory.xquery.psi.XqyFunctionDecl;
import uk.co.overstory.xquery.psi.XqyProlog;
import uk.co.overstory.xquery.psi.XqyRefFunctionName;

import org.jetbrains.annotations.NotNull;

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

	@SuppressWarnings("SimplifiableIfStatement")
	@Override
	public boolean processDeclarations (@NotNull PsiScopeProcessor processor,
		@NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place)
	{
		if (shouldDescend (place, lastParent)) {
			return ResolveUtil.processChildren (this, processor, state, null, place);

		}

		return true;
	}

	// ---------------------------------------------------------------

	private boolean shouldDescend (PsiElement reference, PsiElement lastParent)
	{
		if (reference instanceof XqyRefFunctionName) {
			return !((this instanceof XqyExpr));
		}

		if ((this instanceof XqyFunctionDecl) && (lastParent instanceof XqyProlog)) {
			return false;
		}

		return ! (this instanceof XqyExpr);

//		return ( ! (
//			(this instanceof XqyFunctionDecl) ||
//			(this instanceof XqyVersionDecl) ||
//			(this instanceof XqySetter) ||
//			(this instanceof XqyImport) ||
//			(this instanceof XqyNamespaceDecl)
//		));
	}

}

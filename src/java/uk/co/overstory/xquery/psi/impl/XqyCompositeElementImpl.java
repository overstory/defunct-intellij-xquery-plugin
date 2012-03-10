package uk.co.overstory.xquery.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import uk.co.overstory.xquery.psi.ResolveUtil;
import uk.co.overstory.xquery.psi.XqyCompositeElement;
import uk.co.overstory.xquery.psi.XqyImport;
import uk.co.overstory.xquery.psi.XqyNamespaceDecl;
import uk.co.overstory.xquery.psi.XqySetter;
import uk.co.overstory.xquery.psi.XqyVersionDecl;

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
	public boolean processDeclarations (@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place)
	{
		if (canContainVariableDecls (this)) {
			return ResolveUtil.processChildren (this, processor, state, null, place);

		}

		return true;
	}

	// ---------------------------------------------------------------

	// This is a simplistic optimization, it could probably be made better, or eliminated
	private boolean canContainVariableDecls (Object that)
	{
		return ( ! (
			(that instanceof XqyVersionDecl) ||
				(that instanceof XqySetter) ||
				(that instanceof XqyImport) ||
				(that instanceof XqyNamespaceDecl)
		));
	}

}

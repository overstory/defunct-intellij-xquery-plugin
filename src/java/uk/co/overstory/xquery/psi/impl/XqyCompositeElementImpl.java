package uk.co.overstory.xquery.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import uk.co.overstory.xquery.psi.ResolveUtil;
import uk.co.overstory.xquery.psi.XqyCaseClause;
import uk.co.overstory.xquery.psi.XqyCompositeElement;
import uk.co.overstory.xquery.psi.XqyForClause;
import uk.co.overstory.xquery.psi.XqyImport;
import uk.co.overstory.xquery.psi.XqyLetClause;
import uk.co.overstory.xquery.psi.XqyModule;
import uk.co.overstory.xquery.psi.XqyNamespaceDecl;
import uk.co.overstory.xquery.psi.XqyQuantifiedExpr;
import uk.co.overstory.xquery.psi.XqySetter;
import uk.co.overstory.xquery.psi.XqyTryCatchExpr;
import uk.co.overstory.xquery.psi.XqyTypeswitchExpr;
import uk.co.overstory.xquery.psi.XqyVarName;
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

	@Override
	public boolean processDeclarations (@NotNull PsiScopeProcessor processor,
		@NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place)
	{
//System.out.println ("XqyCompositeElementImpl.processDeclarations: " + toString() + "/" + getName());

		if (this instanceof XqyVarName) {
			if (getText().equals (place.getText())) {
System.out.println ("  candidate VarName: " + toString() + "/" + getText());
				return processor.execute (this, ResolveState.initial());
			}
			return true;
		}

		if (canContainVariableDecls()) {
//System.out.println ("   processing children of: " + toString() + "/" + getText());
			return ResolveUtil.processChildren (this, processor, state, null, place);
		}

		return true;
	}

	// This is a simplistic optimization, it could probably be made better, or eliminated
	private boolean canContainVariableDecls()
	{
		return ( ! (
			(this instanceof XqyVersionDecl) ||
			(this instanceof XqySetter) ||
			(this instanceof XqyImport) ||
			(this instanceof XqyNamespaceDecl)
		));
	}
}

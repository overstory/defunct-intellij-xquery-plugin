package uk.co.overstory.xquery.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import uk.co.overstory.xquery.psi.ResolveUtil;
import uk.co.overstory.xquery.psi.XqyCaseClause;
import uk.co.overstory.xquery.psi.XqyCatchClause;
import uk.co.overstory.xquery.psi.XqyCompositeElement;
import uk.co.overstory.xquery.psi.XqyExpr;
import uk.co.overstory.xquery.psi.XqyExprSingle;
import uk.co.overstory.xquery.psi.XqyFLWORExpr;
import uk.co.overstory.xquery.psi.XqyFunctionDecl;
import uk.co.overstory.xquery.psi.XqyLetClause;
import uk.co.overstory.xquery.psi.XqyRefFunctionName;
import uk.co.overstory.xquery.psi.XqyTryCatchExpr;
import uk.co.overstory.xquery.psi.XqyVarDecl;
import uk.co.overstory.xquery.psi.XqyVarName;

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
		if ( ! shouldDescend (place)) {
			return true;
		}

		PsiElement current = (this instanceof XqyFLWORExpr) ? findPrecedingFlworSibling (place) : getLastChild();

//		if (this instanceof XqyFLWORExpr) {
//			current = findPrecedingFlworSibling (place);
//		}

		while (current != null) {
			if ( ! current.processDeclarations (processor, state, null, place)) {
				return false;
			}

			current = current.getPrevSibling();
		}

		return true;
	}

	private PsiElement findPrecedingFlworSibling (PsiElement reference)
	{
		for (PsiElement e = getLastChild(); e != null; e = e.getPrevSibling()) {
			if (PsiTreeUtil.isAncestor (e, reference, false)) {
				return e.getPrevSibling();
			}
		}

		return null;
	}

	// ---------------------------------------------------------------

	// ToDo: handle quantified exprs

	@SuppressWarnings("SimplifiableIfStatement")
	private boolean shouldDescend (PsiElement reference)
	{
		if (reference instanceof XqyRefFunctionName) {
			return !((this instanceof XqyExpr));
		}

		if (((this instanceof XqyFunctionDecl) || (this instanceof XqyCatchClause) || (this instanceof XqyCaseClause)) &&
			( ! PsiTreeUtil.isAncestor (this, reference, false))) {
			return false;
		}

		if (((this instanceof XqyVarDecl) || (this instanceof XqyLetClause)) && PsiTreeUtil.isAncestor (this, reference, false)) {
			return false;
		}

		return ! ((this instanceof XqyExpr) || (this instanceof XqyExprSingle));
	}
}

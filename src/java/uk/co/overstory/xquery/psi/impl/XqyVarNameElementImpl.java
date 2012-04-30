package uk.co.overstory.xquery.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import uk.co.overstory.xquery.XqyIcons;
import uk.co.overstory.xquery.psi.XqyForVar;
import uk.co.overstory.xquery.psi.XqyLetClause;
import uk.co.overstory.xquery.psi.XqyLetVar;
import uk.co.overstory.xquery.psi.XqyRefVarName;

import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/10/12
 * Time: 7:24 PM
 */
public class XqyVarNameElementImpl extends XqyNamedElementImpl
{
	public XqyVarNameElementImpl (ASTNode node)
	{
		super (node);
	}

	@Override
	public boolean processDeclarations (@NotNull PsiScopeProcessor processor,
		@NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place)
	{
//System.out.println ("XqyVarNameElementImpl.processDeclarations: " + toString() + "/" + getName());

		if ( ! (place instanceof XqyRefVarName)) {
			return true;
		}

		if (getText().equals (place.getText())) {
//System.out.println ("  candidate VarName: " + toString() + "/" + getText());
			return processor.execute (this, ResolveState.initial());
		}

		// TODO: Suppress vars with same name in wider scope
		if ((state == XqyReferenceImpl.variantResolveState) && inScope (place)) {
			processor.execute (this, null);
		}

		return true;
	}

	// FIXME: Need to constrain to immediate ancestor only (quantified)
	// This is a hack that seems unnecessary, the FLWOR check in findPrecedingFlworSibling
	// in XqyCompositeElementImpl should be enough
	private boolean inScope (PsiElement place)
	{
		if ((this.getParent().getParent() instanceof XqyLetVar) &&
			PsiTreeUtil.getParentOfType (this, XqyLetClause.class, false) == PsiTreeUtil.getParentOfType (place, XqyLetClause.class, false))
		{
			return false;
		}

		return true;
	}

	@Override
	public Icon getIcon (int flags)
	{
		return XqyIcons.VARIABLE;
	}
}

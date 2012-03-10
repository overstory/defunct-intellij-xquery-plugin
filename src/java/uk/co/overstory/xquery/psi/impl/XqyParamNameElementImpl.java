package uk.co.overstory.xquery.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import uk.co.overstory.xquery.psi.XqyRefVarName;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/10/12
 * Time: 7:24 PM
 */
public class XqyParamNameElementImpl extends XqyNamedElementImpl
{
	public XqyParamNameElementImpl (ASTNode node)
	{
		super (node);
	}

	// FIXME: This is identical to XqyVarNameElementImpl, they should be combined
	@Override
	public boolean processDeclarations (@NotNull PsiScopeProcessor processor,
		@NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place)
	{
//System.out.println ("XqyCompositeElementImpl.processDeclarations: " + toString() + "/" + getName());

		if ( ! (place instanceof XqyRefVarName)) {
			return true;
		}

		if (getText().equals (place.getText())) {
//System.out.println ("  candidate VarName: " + toString() + "/" + getText());
			return processor.execute (this, ResolveState.initial());
		}

		if (state == XqyReferenceImpl.variantResolveState) {
			processor.execute (this, ResolveState.initial());
		}

		return true;
	}
}

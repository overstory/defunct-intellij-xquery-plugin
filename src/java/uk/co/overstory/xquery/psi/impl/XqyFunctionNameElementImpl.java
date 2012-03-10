package uk.co.overstory.xquery.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import uk.co.overstory.xquery.psi.XqyFunctionName;
import uk.co.overstory.xquery.psi.XqyRefFunctionName;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/10/12
 * Time: 7:24 PM
 */
public class XqyFunctionNameElementImpl extends XqyNamedElementImpl
{
	public XqyFunctionNameElementImpl (ASTNode node)
	{
		super (node);
	}

	@Override
	public boolean processDeclarations (@NotNull PsiScopeProcessor processor,
		@NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place)
	{
//System.out.println ("XqyFunctionNameElementImpl.processDeclarations: " + toString() + "/" + getName());

		if ( ! (place instanceof XqyRefFunctionName)) {
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

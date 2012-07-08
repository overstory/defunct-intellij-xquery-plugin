package uk.co.overstory.xquery.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import uk.co.overstory.xquery.XqyIcons;
import uk.co.overstory.xquery.psi.XqyExprSingle;
import uk.co.overstory.xquery.psi.XqyFunctionDecl;
import uk.co.overstory.xquery.psi.XqyParamList;
import uk.co.overstory.xquery.psi.XqyRefFunctionName;
import uk.co.overstory.xquery.psi.util.TreeUtil;

import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

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

		if (getText().equals (place.getText()) && (myArgCount () == callerArgCount (place))) {
//System.out.println ("  candidate VarName: " + toString() + "/" + getText());
			return processor.execute (this, ResolveState.initial ());
		}

		if (state == XqyReferenceImpl.variantResolveState) {
			processor.execute (this, ResolveState.initial());
		}

		return true;
	}

	private int callerArgCount (PsiElement place)
	{
		XqyExprSingle [] callParams = PsiTreeUtil.getChildrenOfType (place.getParent(), XqyExprSingle.class);
		return (callParams == null) ? 0 : callParams.length;
	}

	@SuppressWarnings("unchecked")
	private int myArgCount()
	{
		XqyFunctionDecl funcDecl = PsiTreeUtil.getParentOfType (this, XqyFunctionDecl.class);
		XqyParamList paramList = (XqyParamList) TreeUtil.getDescendentElementAtPath (funcDecl, XqyParamList.class);

		return (paramList == null) ? 0 : paramList.getParamList().size();
	}

	@Override
	public Icon getIcon (int flags)
	{
		return XqyIcons.FUNCTION;
	}
}

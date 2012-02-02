package uk.co.overstory.xquery.psi;

import com.intellij.psi.PsiPolyVariantReference;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/31/12
 * Time: 8:17 AM
 */
public interface XqyReference extends PsiPolyVariantReference
{
	String getLocalname();

	String getNamespaceName();

	boolean hasNamespace();
}

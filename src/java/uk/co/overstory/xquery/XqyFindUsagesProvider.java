package uk.co.overstory.xquery;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import uk.co.overstory.xquery.psi.XqyNamespacedecl;
import uk.co.overstory.xquery.psi.XqyVardecl;
import uk.co.overstory.xquery.psi.XqyFunctiondecl;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/11/12
 * Time: 11:27 AM
 */
public class XqyFindUsagesProvider implements FindUsagesProvider
{
	@Override
	public WordsScanner getWordsScanner()
	{
		return null;
	}

	@Override
	public boolean canFindUsagesFor (@NotNull PsiElement psiElement)
	{
		return (psiElement instanceof XqyVardecl) || (psiElement instanceof XqyFunctiondecl) || (psiElement instanceof XqyNamespacedecl);
	}

	@Override
	public String getHelpId (@NotNull PsiElement psiElement)
	{
		return null;
	}

	@NotNull
	@Override
	public String getType (@NotNull PsiElement element)
	{
		return ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE);
	}

	@NotNull
	@Override
	public String getDescriptiveName (@NotNull PsiElement element)
	{
		return ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE);
	}

	@NotNull
	@Override
	public String getNodeText (@NotNull PsiElement element, boolean useFullName)
	{
		return ElementDescriptionUtil.getElementDescription(element, UsageViewNodeTextLocation.INSTANCE);
	}
}

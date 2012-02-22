package uk.co.overstory.xquery;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import uk.co.overstory.xquery.psi.XqyFunctionName;
import uk.co.overstory.xquery.psi.XqyVarName;

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
		return ((psiElement instanceof XqyVarName) || (psiElement instanceof XqyFunctionName));
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
		if (element instanceof XqyFunctionName) return "Function";

		return "Variable";

//		return ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE);
	}

	@NotNull
	@Override
	public String getDescriptiveName (@NotNull PsiElement element)
	{
		if (element instanceof XqyFunctionName) return element.getText() + "()";

		return "$" + element.getText();

//		return ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE);
	}

	@NotNull
	@Override
	public String getNodeText (@NotNull PsiElement element, boolean useFullName)
	{
System.out.println ("XqyFindUsagesProvider.getNodeText: " + element.getText());
		return element.getText();

//		return ElementDescriptionUtil.getElementDescription(element, UsageViewNodeTextLocation.INSTANCE);
	}
}

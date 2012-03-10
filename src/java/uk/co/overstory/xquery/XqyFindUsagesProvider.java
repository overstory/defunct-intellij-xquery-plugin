package uk.co.overstory.xquery;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import uk.co.overstory.xquery.parser.XqyLexer;
import uk.co.overstory.xquery.psi.XqyFunctionName;
import uk.co.overstory.xquery.psi.XqyParamName;
import uk.co.overstory.xquery.psi.XqyRefVarName;
import uk.co.overstory.xquery.psi.XqyTypes;
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
	private static final TokenSet identifierTokens = TokenSet.create (XqyTypes.XQY_VARNAME, XqyTypes.XQY_VARREF, XqyTypes.XQY_FUNCTIONNAME);
	private static final TokenSet commentTokens = TokenSet.create (XqyTypes.XQY_COMMENT, XqyTypes.XQY_CDATASECTION, XqyTypes.XQY_XML_COMMENT_CONTENTS);
	private static final TokenSet literalTokens = TokenSet.create (XqyTypes.XQY_NUMERICLITERAL, XqyTypes.XQY_STRINGLITERAL);

	public XqyFindUsagesProvider ()
	{
System.out.println ("XqyFindUsagesProvider.constructor");
	}

	@Override
	public WordsScanner getWordsScanner()
	{
System.out.println ("XqyFindUsagesProvider.getWordsScanner");
		return new DefaultWordsScanner (new XqyLexer(), identifierTokens, commentTokens, literalTokens);
	}

	@Override
	public boolean canFindUsagesFor (@NotNull PsiElement psiElement)
	{
System.out.println ("XqyFindUsagesProvider.canFindUsagesFor: " + psiElement.getText ());
		return ((psiElement instanceof XqyVarName) || (psiElement instanceof XqyParamName) || (psiElement instanceof XqyFunctionName));
	}

	@Override
	public String getHelpId (@NotNull PsiElement element)
	{
System.out.println ("XqyFindUsagesProvider.getHelpId: " + element.getText());
		return null;
	}

	@NotNull
	@Override
	public String getType (@NotNull PsiElement element)
	{
System.out.println ("XqyFindUsagesProvider.getType: " + element.getText());
		if (element instanceof XqyFunctionName) return "Function";

		return "Variable";

//		return ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE);
	}

	@NotNull
	@Override
	public String getDescriptiveName (@NotNull PsiElement element)
	{
System.out.println ("XqyFindUsagesProvider.getDescriptiveName: " + element.getText());
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

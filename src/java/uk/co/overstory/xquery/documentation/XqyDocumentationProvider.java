package uk.co.overstory.xquery.documentation;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import uk.co.overstory.xquery.completion.FunctionDefs;
import uk.co.overstory.xquery.psi.XqyDollarVarName;
import uk.co.overstory.xquery.psi.XqyRefFunctionName;
import uk.co.overstory.xquery.psi.XqyVarName;
import uk.co.overstory.xquery.psi.impl.XqyFileImpl;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 7/22/12
 * Time: 2:03 PM
 */
public class XqyDocumentationProvider implements DocumentationProvider
{
	private final FunctionDefs functionDefs = FunctionDefs.instance();


	@Override
	public String getQuickNavigateInfo (PsiElement element, PsiElement originalElement)
	{
		return "<pre>This is the getQuickNavigateInfo response</pre>";
	}

	@Override
	public List<String> getUrlFor (PsiElement element, PsiElement originalElement)
	{
		return null;  // FIXME: auto-generated
	}

	@Override
	public String generateDoc (PsiElement element, PsiElement originalElement)
	{
		if (element instanceof XqyRefFunctionName) {
			FunctionDefs.Function func = functionDefs.getFunction (element.getText());

			if (func == null) {
				XqyFileImpl fileImpl = (XqyFileImpl) element.getContainingFile();

				func = functionDefs.getFunction (fileImpl.getDefaultFunctionNsPrefix() + ":" + element.getText());
			}

			if (func == null) {
				return "<p>No documentation available for function " + element.getText() + "</p>";
			}

			return func.docAsHtml();
		}

		if (element instanceof XqyVarName) {
			XqyDollarVarName dvName = PsiTreeUtil.getParentOfType (element, XqyDollarVarName.class);

			if (dvName != null) {
				PsiElement parent = dvName.getParent();

				if (parent != null) {
					return "<p>" + parent.getText() + "</p>";
				}
			}

			if (dvName == null) return "<p>No information found for variable " + element.getText() + "</p>";
		}

		return null;
	}

	@Override
	public PsiElement getDocumentationElementForLookupItem (PsiManager psiManager, Object object, PsiElement element)
	{
		return (PsiElement) object;
	}

	@Override
	public PsiElement getDocumentationElementForLink (PsiManager psiManager, String link, PsiElement context)
	{
		return null;
	}
}

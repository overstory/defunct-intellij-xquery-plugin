package uk.co.overstory.xquery;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/11/12
 * Time: 8:39 AM
 */
public class XqyDocumentationProvider implements DocumentationProvider
{
	@Override
	public String getQuickNavigateInfo (PsiElement element, PsiElement originalElement)
	{
		return null;
	}

	// ToDo: Generate links to ML dev site docs here?
	@Override
	public List<String> getUrlFor (PsiElement element, PsiElement originalElement)
	{
		return null;
	}

	@Override
	public String generateDoc (PsiElement element, PsiElement originalElement)
	{
		return null;  // FIXME: auto-generated
	}

	@Override
	public PsiElement getDocumentationElementForLookupItem (PsiManager psiManager, Object object, PsiElement element)
	{
		return null;
	}

	@Override
	public PsiElement getDocumentationElementForLink (PsiManager psiManager, String link, PsiElement context)
	{
		return null;
	}
}

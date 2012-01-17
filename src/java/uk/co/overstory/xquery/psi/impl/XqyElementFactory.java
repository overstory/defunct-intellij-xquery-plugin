package uk.co.overstory.xquery.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import uk.co.overstory.xquery.XqyLanguage;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 12:25 PM
 */
public class XqyElementFactory
{
	private XqyElementFactory ()
	{
System.out.println ("XqyElementFactory: constructor");
	}

	public static PsiElement createLeafFromText (Project project, String text)
	{
System.out.println ("createLeafFromText: text=" + text);
		PsiFile fileFromText = PsiFileFactory.getInstance (project).createFileFromText ("a.xqy", XqyLanguage.INSTANCE, text);

		return PsiTreeUtil.getDeepestFirst (fileFromText);
	}
}

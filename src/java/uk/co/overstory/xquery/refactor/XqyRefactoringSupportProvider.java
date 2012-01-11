package uk.co.overstory.xquery.refactor;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/11/12
 * Time: 7:59 AM
 */
public class XqyRefactoringSupportProvider extends RefactoringSupportProvider
{
	@Override
	public boolean isAvailable (@NotNull PsiElement context)
	{
		return super.isAvailable (context);
	}
}

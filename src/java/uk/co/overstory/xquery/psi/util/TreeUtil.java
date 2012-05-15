package uk.co.overstory.xquery.psi.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 5/15/12
 * Time: 6:27 PM
 */
public class TreeUtil
{
	private TreeUtil()
	{
	}

	public static <T extends PsiElement> T getDescendentElementAtPath (
		@NotNull T element,
		@NotNull Class<? extends T>... pathSteps)
	{
		T parent = element;
		T child = null;

		for (Class<? extends T> step : pathSteps) {
			child = PsiTreeUtil.getChildOfType (parent, step);

			if (child == null) return null;

			parent = child;
		}

		return child;
	}

	public static String getTextOfDescendentElementAtPath (
		@NotNull PsiElement parent,
		@NotNull Class<? extends PsiElement>... pathSteps)
	{
		PsiElement element = getDescendentElementAtPath (parent, pathSteps);

		if (element == null) return null;

		return element.getText();
	}
}

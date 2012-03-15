package uk.co.overstory.xquery.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import uk.co.overstory.xquery.XqyIcons;
import uk.co.overstory.xquery.bundles.XqyMessageBundle;

import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/15/12
 * Time: 8:34 AM
 */
@SuppressWarnings({"UnusedDeclaration", "UnresolvedPropertyKey"})
public class NewXqueryFileAction  extends AnAction
{
	public NewXqueryFileAction()
	{
		this (XqyMessageBundle.message ("newfile.menu.action.text"),
			XqyMessageBundle.message ("newfile.menu.action.description"),
			XqyIcons.FILE);

	}

	public NewXqueryFileAction (String text)
	{
		this (text, XqyMessageBundle.message ("newfile.menu.action.description"), XqyIcons.FILE);
	}

	public NewXqueryFileAction (String text, String description, @Nullable Icon icon)
	{
		super (text, description, icon);
	}

	@Override
	public void actionPerformed (AnActionEvent e)
	{
		// FIXME: auto-generated
	}
}

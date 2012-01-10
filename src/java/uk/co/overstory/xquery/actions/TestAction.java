package uk.co.overstory.xquery.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/9/12
 * Time: 10:39 AM
 */
public class TestAction extends AnAction
{
	public void actionPerformed (AnActionEvent e)
	{
		System.out.println ("Test action invoked");
	}
}

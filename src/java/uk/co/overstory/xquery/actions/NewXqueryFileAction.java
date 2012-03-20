package uk.co.overstory.xquery.actions;

import com.intellij.CommonBundle;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.IdeView;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import uk.co.overstory.xquery.XqyIcons;
import uk.co.overstory.xquery.bundles.XqyMessageBundle;
import uk.co.overstory.xquery.psi.XqyTokenType;
import uk.co.overstory.xquery.psi.XqyWhitespace;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/15/12
 * Time: 8:34 AM
 */
@SuppressWarnings({"UnusedDeclaration", "UnresolvedPropertyKey"})
public class NewXqueryFileAction extends CreateElementActionBase
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

	@NotNull
	protected final PsiElement[] invokeDialog(final Project project, final PsiDirectory directory) {
		MyInputValidator validator = new MyInputValidator (project, directory);
		Messages.showInputDialog (project, getDialogPrompt(), getDialogTitle(), Messages.getQuestionIcon(), "", validator);

		return validator.getCreatedElements();
	}

	protected String getDialogPrompt() {
		return XqyMessageBundle.message ("newfile.dlg.prompt");
	}

	protected String getDialogTitle() {
		return XqyMessageBundle .message ("newfile.dlg.title");
	}

	@NotNull
	@Override
	protected PsiElement[] create (String newName, PsiDirectory directory) throws Exception
	{
		return doCreate (newName, directory);
	}

	@Override
	protected String getErrorTitle ()
	{
		return CommonBundle.getErrorTitle();
	}

	@Override
	protected String getCommandName()
	{
		return XqyMessageBundle.message("newfile.command.name");
	}

	@Override
	protected String getActionName (PsiDirectory directory, String newName)
	{
		return XqyMessageBundle.message("newfile.menu.action.text");
	}

	// -----------------------------------------------------------

	private PsiElement[] doCreate (String newName, PsiDirectory directory)
	{
		PsiFile file = XqyTemplatesFactory.createFileFromTemplate (directory, newName, "XQueryFile.xqy");
		PsiElement lastChild = file.getLastChild();
		Project project = directory.getProject();

		if ((lastChild != null) && (lastChild.getNode() != null) &&
			(lastChild.getNode().getElementType() != null))        // FIXME: tests for whitespace
		{
			file.add (createWhiteSpace (project));
		}

		file.add (createWhiteSpace (project));
		PsiElement child = file.getLastChild();

		return child != null ? new PsiElement[]{file, child} : new PsiElement[]{file};
	}

	private static PsiElement createWhiteSpace(Project project)
	{
		PsiFile dummyFile = PsiFileFactory.getInstance (project).createFileFromText ("dummy.xqy", "\n");

 		return dummyFile.getFirstChild();
	}

}

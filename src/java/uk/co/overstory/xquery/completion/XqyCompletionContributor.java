package uk.co.overstory.xquery.completion;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import uk.co.overstory.xquery.psi.XqyFLWORExpr;

import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/12/12
 * Time: 5:49 PM
 */
public class XqyCompletionContributor extends CompletionContributor
{
	public XqyCompletionContributor()
	{
		extend (CompletionType.BASIC, psiElement().afterLeaf (":="), new BindSuggestProvider());
		extend (CompletionType.BASIC, psiElement().afterLeaf ("version"), new XqueryVersionSuggestProvider());  // FIXME: smarten this up

//		extend (CompletionType.BASIC,
//			psiElement ().inside (PlatformPatterns.instanceOf (XqyFLWORExpr.class)),
//			new FlworSuggestProvider ());

		// --------------------------------------------------------

	}
	private void addLocalInScopeFunctionSuggestions (@NotNull CompletionParameters parameters,
		ProcessingContext context, @NotNull CompletionResultSet result)
	{
		// ToDo: Get suggestions of in-scope functions in this module, relative to parameters.getPosition()
	}

	private void addImportedFunctionSuggestions (@NotNull CompletionParameters parameters,
		ProcessingContext context, @NotNull CompletionResultSet result)
	{
		// ToDo: Get in-scope functions in imported modules
	}

	private void addPredefinedFunctionSuggestions (@NotNull CompletionParameters parameters,
		ProcessingContext context, @NotNull CompletionResultSet result)
	{
		// ToDo: Get list of standard modules, as per XQuery declared version
		result.addElement (LookupElementBuilder.create ("fn:count").setTypeText ("xs:integer"));
		result.addElement (LookupElementBuilder.create ("fn:exists").setTypeText ("xs:boolean"));
		result.addElement (LookupElementBuilder.create ("xdmp:estimate").setTypeText ("xs:integer"));
		result.addElement (LookupElementBuilder.create ("xdmp:log").setTypeText ("empty-sequence()"));
		result.addElement (LookupElementBuilder.create ("cts:search").setTypeText ("node()*"));
	}

	// ----------------------------------------------------------------

	private class AppendSemiColonInsertHandler implements InsertHandler<LookupElement>
	{

		@Override
		public void handleInsert (InsertionContext context, LookupElement lookupElement)
		{
			Document document = context.getDocument();
			Editor editor = context.getEditor();
			CaretModel caretModel = editor.getCaretModel();

			int offset = caretModel.getOffset();
			document.insertString (offset, ";");
			caretModel.moveToOffset (offset);
		}
	}

	private class VarRefInsertHandler implements InsertHandler<LookupElement>
	{
		@Override
		public void handleInsert (InsertionContext context, LookupElement lookupElement)
		{
			Document document = context.getDocument();
			Project project = context.getProject();
			Editor editor = context.getEditor();
			CaretModel caretModel = editor.getCaretModel();

			int offset = caretModel.getOffset ();
			document.insertString (offset, ";");
			caretModel.moveToOffset (offset);

			AutoPopupController.getInstance (project).autoPopupMemberLookup(editor, null);
		}
	}

	private class FuncRefInsertHandler implements InsertHandler<LookupElement>
	{
		@Override
		public void handleInsert (InsertionContext context, LookupElement lookupElement)
		{
			Document document = context.getDocument();
			Project project = context.getProject();
			Editor editor = context.getEditor();
			CaretModel caretModel = editor.getCaretModel();

			int offset = caretModel.getOffset();
			document.insertString (offset, ";");
			caretModel.moveToOffset (offset - 2);

			AutoPopupController.getInstance (project).autoPopupMemberLookup (editor, null);
		}
	}


	private class BindSuggestProvider extends CompletionProvider<CompletionParameters>
	{
		@Override
		protected void addCompletions (@NotNull CompletionParameters parameters,
			ProcessingContext context, @NotNull CompletionResultSet result)
		{
			result.addElement (LookupElementBuilder.create ("$")
				.setBold (true)
				.setTailText ("<variable name>", true)
				.setInsertHandler (new VarRefInsertHandler()));

			result.addElement (LookupElementBuilder.create ("()")
				.setBold (true)
				.setTailText ("function", true)
				.setInsertHandler (new FuncRefInsertHandler()));

			addLocalInScopeFunctionSuggestions (parameters, context, result);
			addImportedFunctionSuggestions (parameters, context, result);
			addPredefinedFunctionSuggestions (parameters, context, result);
		}
	}

	private class FlworSuggestProvider extends CompletionProvider<CompletionParameters>
	{
		@Override
		protected void addCompletions (@NotNull CompletionParameters parameters,
			ProcessingContext context, @NotNull CompletionResultSet result)
		{
			PsiElement pos = parameters.getPosition();
			PsiElement flwor = PsiTreeUtil.getParentOfType (pos, XqyFLWORExpr.class);



			PsiElement parent = pos.getParent ();

//			PsiElement parent = (pos.getParent ().getParent () == null) ? pos.getParent () : pos.getParent ().getParent ();
//			PsiElement prev = pos.getPrevSibling ();
//			PsiElement next = pos.getNextSibling ();

			System.out.println ("XqyCompletionContributor.addCompletions");
			while (parent != null) {
				System.out.println ("  " + parent.toString());
				parent = parent.getParent();
			}

//			System.out.println ("XqyCompletionContributor.addCompletions, pos=" + pos.toString() + "/" + pos.getText () +
//				", parent=" + parent.toString () + "/" + parent.getText () +
//				", prev=" + ((prev == null) ? "<none>" : (prev.toString () + "/" + prev.getText ())) +
//				", next=" + ((next == null) ? "<none>" : (next.toString () + "/" + next.getText ()))
//			);

//			result.addElement(LookupElementBuilder.create ("monkeybutt"));
		}
	}

	private class XqueryVersionSuggestProvider extends CompletionProvider<CompletionParameters>
	{
		@Override
		protected void addCompletions (@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result)
		{
			result.addElement (LookupElementBuilder.create ("'1.0-ml'")
				.setTypeText ("MarkLogic Extensions", true)
				.setInsertHandler (new AppendSemiColonInsertHandler()));
			result.addElement (LookupElementBuilder.create ("'1.0'")
				.setTypeText ("XQuery 1.0 Strict", true)
				.setInsertHandler (new AppendSemiColonInsertHandler()));
			result.addElement (LookupElementBuilder.create ("'3.0'")
				.setTypeText ("XQuery 3.0 Strict", true)
				.setInsertHandler (new AppendSemiColonInsertHandler()));
		}
	}
}

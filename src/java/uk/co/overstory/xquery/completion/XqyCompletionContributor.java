package uk.co.overstory.xquery.completion;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import uk.co.overstory.xquery.XqyIcons;
import uk.co.overstory.xquery.psi.XqyFLWORExpr;
import uk.co.overstory.xquery.psi.XqyReference;
import uk.co.overstory.xquery.psi.impl.XqyFunctionNameReference;
import uk.co.overstory.xquery.psi.impl.XqyReferenceImpl;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/12/12
 * Time: 5:49 PM
 */
public class XqyCompletionContributor extends CompletionContributor
{
	private final FunctionDefs functionDefs = FunctionDefs.instance();

	public XqyCompletionContributor()
	{
		extend (CompletionType.BASIC, psiElement().afterLeaf (":="), new BindSuggestProvider());
		extend (CompletionType.BASIC, psiElement().afterLeaf ("version"), new XqueryVersionSuggestProvider());
		extend (CompletionType.BASIC, psiElement().inside (PlatformPatterns.instanceOf (XqyFunctionNameReference.class)), new BuiltinFunctionsProvider());

//		extend (CompletionType.BASIC,
//			psiElement ().inside (PlatformPatterns.instanceOf (XqyFLWORExpr.class)),
//			new FlworSuggestProvider ());

		// --------------------------------------------------------

	}



	// --------------------------------------------------------

	private class BuiltinFunctionsProvider extends CompletionProvider<CompletionParameters>
	{
		@Override
		protected void addCompletions (@NotNull CompletionParameters parameters,
			ProcessingContext context, @NotNull CompletionResultSet result)
		{
			List<FunctionDefs.Function> functions = functionDefs.getFunctions();

			for (FunctionDefs.Function func : functions) {
				if (func.isHidden()) continue;

				LookupElementBuilder lookup = LookupElementBuilder.create (func.getFullName())
					.setBold (false)
					.setTailText ("()")
					.setIcon (XqyIcons.FUNCTION)
					.setTypeText (func.getReturnType());

				// ToDo: This should be sensitive to default element namespace declaration
				if (func.getPrefix().equals ("fn")) {
					lookup = lookup.addLookupString (func.getLocalName());
				}

				result.addElement (lookup);
			}
		}
	}


	private void addLocalInScopeFunctionSuggestions (@NotNull CompletionParameters parameters,
		ProcessingContext context, @NotNull CompletionResultSet result)
	{
		// ToDo: Get suggestions of in-scope functions in this module, relative to parameters.getPosition()
	}

	private void addImportedFunctionPrefixSuggestions (@NotNull CompletionParameters parameters,
		ProcessingContext context, @NotNull CompletionResultSet result)
	{
		// ToDo: Get in-scope functions in imported modules
	}

	private void addPredefinedFunctionPrefixSuggestions (@NotNull CompletionParameters parameters,
		ProcessingContext context, @NotNull CompletionResultSet result)
	{
		List<FunctionDefs.Category> categories = functionDefs.getCategories();

		for (FunctionDefs.Category cat : categories) {
			if (cat.getFunctionCount() == 1) {
				FunctionDefs.Function func = functionDefs.getFunctionsForPrefix (cat.getPrefix()).get (0);

				result.addElement (
					LookupElementBuilder.create (func.getFullName () + "()")
						.setPresentableText (func.getFullName ())
						.setIcon (XqyIcons.FUNCTION)
						.setTailText (" FIXME-ARGS", true)
						.setTypeText (func.getReturnType ())
				);

				continue;
			}

			result.addElement (
				LookupElementBuilder.create (cat.getPrefix() + ":()")
					.setPresentableText (cat.getPrefix () + ":")
					.setIcon (XqyIcons.FUNCTION)
					.setTailText (" " + cat.getDesc() + " (" + cat.getFunctionCount() + " functions)", true)
					.setInsertHandler (new FuncRefInsertHandler())
			);
		}
	}

	// ----------------------------------------------------------------

	// ToDo: Smarten up to not add semicolon if already present.  Fix this up.
	private class AppendSemiColonInsertHandler implements InsertHandler<LookupElement>
	{
		@Override
		public void handleInsert (InsertionContext context, LookupElement lookupElement)
		{
			Document document = context.getDocument();
			Editor editor = context.getEditor();
			CaretModel caretModel = editor.getCaretModel();

			int offset = caretModel.getOffset();
//			String tmp = document.getText (TextRange.from (context.getTailOffset(), 5));

			document.insertString (offset, ";");
			caretModel.moveToOffset (offset);
		}
	}

	private class VarRefInsertHandler implements InsertHandler<LookupElement>
	{
		@Override
		public void handleInsert (InsertionContext context, LookupElement lookupElement)
		{
//			Document document = context.getDocument();
			Project project = context.getProject();
			Editor editor = context.getEditor();
			CaretModel caretModel = editor.getCaretModel();

			int offset = caretModel.getOffset();
//			document.insertString (offset, ";");
			caretModel.moveToOffset (offset);

			AutoPopupController.getInstance (project).autoPopupMemberLookup (editor, null);
		}
	}

	private class FuncRefInsertHandler implements InsertHandler<LookupElement>
	{
		@Override
		public void handleInsert (InsertionContext context, LookupElement lookupElement)
		{
//			Document document = context.getDocument();
			Project project = context.getProject();
			Editor editor = context.getEditor();
			CaretModel caretModel = editor.getCaretModel();

			int offset = caretModel.getOffset();
//			document.insertString (offset, ";");
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
			result.addElement (PrioritizedLookupElement.withPriority (
				LookupElementBuilder.create ("$")
				.setBold (true)
				.setIcon (XqyIcons.VARIABLE)
				.setTailText (" <variable name>", true)
				.setInsertHandler (new VarRefInsertHandler()),
				100000.0));

			result.addElement (PrioritizedLookupElement.withPriority (
				LookupElementBuilder.create ("()")
				.setBold (true)
				.setIcon (XqyIcons.FUNCTION)
				.setTailText (" <local function>", true)
				.setInsertHandler (new FuncRefInsertHandler()),
				99999.0));

			addLocalInScopeFunctionSuggestions (parameters, context, result);
			addImportedFunctionPrefixSuggestions (parameters, context, result);
			addPredefinedFunctionPrefixSuggestions (parameters, context, result);
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
			result.addElement (LookupElementBuilder.create ("'0.9-ml'")
				.setTypeText ("MarkLogic pre-1.0", true)
				.setInsertHandler (new AppendSemiColonInsertHandler()));
		}
	}
}

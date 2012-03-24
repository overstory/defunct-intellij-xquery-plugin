package uk.co.overstory.xquery.components;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import gnu.trove.THashSet;
import uk.co.overstory.xquery.psi.XqyCompositeElement;
import uk.co.overstory.xquery.psi.XqyFile;
import uk.co.overstory.xquery.psi.XqyFunctionName;
import uk.co.overstory.xquery.psi.XqyNamedElement;
import uk.co.overstory.xquery.psi.XqyParamName;
import uk.co.overstory.xquery.psi.XqyRefFunctionName;
import uk.co.overstory.xquery.psi.XqyRefVarName;
import uk.co.overstory.xquery.psi.XqyVarName;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/24/12
 * Time: 4:49 PM
 */
public class UnusedItemsHighlightPassFactory extends AbstractProjectComponent implements TextEditorHighlightingPassFactory
{
	private static final Key<CachedValue<Set<PsiElement>>> USED_ITEMS_KEY = Key.create("USED_ITEMS_KEY");

	// ToDo: check usage of namespaces
	// ToDo: check types and arity of function params
	// ToDo: highlight when variables hide other variables
	// ToDo: check types of expressions against declared types of variables
	// ToDo: check return type of function of return value, where possible
	// ToDo: check validity of option values
	// ToDo: warn about missing return type for functions

	public UnusedItemsHighlightPassFactory (Project project, TextEditorHighlightingPassRegistrar highlightingPassRegistrar)
	{
		super (project);

		highlightingPassRegistrar.registerTextEditorHighlightingPass (this, new int[]{Pass.UPDATE_ALL,}, null, true, -1);
	}
	@NonNls
	@NotNull
	public String getComponentName()
	{
		return "XqyUnusedItemsHighlightPassFactory";
	}

	@Nullable
	public TextEditorHighlightingPass createHighlightingPass (@NotNull PsiFile file, @NotNull final Editor editor)
	{
		return file instanceof XqyFile ? new XqyHighlightUnusedPass (myProject, (XqyFile)file, editor, editor.getDocument()) : null;
	}

	private static Set<PsiElement> getUsedElements (final PsiFile file)
	{
		CachedValue<Set<PsiElement>> value = file.getUserData(USED_ITEMS_KEY);

		if (value != null) return value.getValue();

		value = CachedValuesManager.getManager (file.getProject ()).createCachedValue (
			new CachedValueProvider<Set<PsiElement>> () {
				@SuppressWarnings("OverlyComplexAnonymousInnerClass")
				@Override
				public Result<Set<PsiElement>> compute()
				{
					final THashSet<PsiElement> psiElements = new THashSet<PsiElement>();
					file.acceptChildren (new PsiRecursiveElementWalkingVisitor() {
						@Override
						public void visitElement(PsiElement element)
						{
							if (element instanceof XqyRefVarName || element instanceof XqyRefFunctionName) {
								PsiReference reference = element.getReference();
								PsiElement target = reference != null? reference.resolve() : null;

								if (target instanceof XqyNamedElement) {
									psiElements.add (target);
								}
							}
							super.visitElement(element);
						}
					});
					return new Result<Set<PsiElement>>(psiElements, PsiModificationTracker.MODIFICATION_COUNT);
				}
			}, false
		);

		file.putUserData (USED_ITEMS_KEY, value);

		return value.getValue();
	}


	static class XqyHighlightUnusedPass extends TextEditorHighlightingPass {

		private final XqyFile myFile;
		private final List<HighlightInfo> myHighlights = new ArrayList<HighlightInfo>();

		XqyHighlightUnusedPass (Project myProject, XqyFile file, Editor editor, Document document)
		{
			super (myProject, document, true);

			myFile = file;
		}

		@Override
		public void doCollectInformation (ProgressIndicator progress)
		{
			final Set<PsiElement> usedElements = getUsedElements (myFile);

			for (XqyCompositeElement element : myFile.getDeclarations()) {
				if ( ! usedElements.contains (element)) {
					TextRange textRange = element.getTextRange();
					String message = unusedMessageForElement (element);

					if (externallyVisible (element)) continue;	// ToDo: weak warning for non-private vars/funcs?

					myHighlights.add (new HighlightInfo (HighlightInfoType.WARNING,
						textRange.getStartOffset (), textRange.getEndOffset (), message, message));
				}
			}
		}

		private boolean externallyVisible (XqyCompositeElement element)
		{
			return false;  // FIXME: Check if declaration is private, no warning if so
		}

		private String unusedMessageForElement (XqyCompositeElement element)
		{
			if (element instanceof XqyVarName) return "Unused variable";
			if (element instanceof XqyParamName) return "Unused parameter";
			if (element instanceof XqyFunctionName) return "Unused function";

			return "Unused declaration";
		}

		@Override
		public void doApplyInformationToEditor() {
			UpdateHighlightersUtil.setHighlightersToEditor (myProject, myDocument, 0, myFile.getTextLength (), myHighlights, getColorsScheme (), getId ());
		}
	}}

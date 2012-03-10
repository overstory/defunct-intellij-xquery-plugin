package uk.co.overstory.xquery.psi.resolve;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.containers.HashSet;
import uk.co.overstory.xquery.refactor.XqyRefactoringSupportProvider;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/31/12
 * Time: 8:35 AM
 */
public class XqyResolveProcessor implements PsiScopeProcessor
{
	private final Set<PsiElement> myProcessedElements = new java.util.HashSet<PsiElement> ();
	private final HashSet<ResolveResult> candidates = new HashSet<ResolveResult>();
	private final String name;
	private final PsiElement place;
	private final boolean incompleteCode;

	public XqyResolveProcessor (String name, PsiElement place, boolean incompleteCode)
	{
		this.name = name;
		this.place = place;
		this.incompleteCode = incompleteCode;
	}

	@Override
	public boolean execute (PsiElement element, ResolveState state)
	{
System.out.println ("XqyResolveProcessor.execute: " + element.toString() + "/" + element.getText());
		if (element instanceof PsiNamedElement && !myProcessedElements.contains (element))
		{
			PsiNamedElement namedElement = (PsiNamedElement) element;
			boolean isAccessible = isAccessible (namedElement);

			candidates.add (new XqyResolveResultImpl (namedElement, isAccessible));
			myProcessedElements.add (namedElement);

//			 return !ListDeclarations.isLocal(element);
			//todo specify as it's possible!

			// ToDo: Determine whether to continue of not: what are the criteria
			return false;
		}

		return true;

	}

	@Override
	public <T> T getHint (Key<T> hintKey)
	{
		return null;
	}

	@Override
	public void handleEvent (Event event, @Nullable Object associated)
	{
	}

	public ResolveResult[] getCandidates()
	{
		return candidates.toArray (new ResolveResult[candidates.size()]);
	}


	private boolean isAccessible(PsiNamedElement namedElement) {
		//todo implement me
		return true;
	}

}

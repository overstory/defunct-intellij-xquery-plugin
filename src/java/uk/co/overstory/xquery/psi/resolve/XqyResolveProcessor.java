package uk.co.overstory.xquery.psi.resolve;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.containers.HashSet;

import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/31/12
 * Time: 8:35 AM
 */
public class XqyResolveProcessor implements PsiScopeProcessor
{
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
		return false;  // FIXME: auto-generated
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

	public ResolveResult[] getCandidates ()
	{
		return candidates.toArray(new ResolveResult[candidates.size()]);
	}
}

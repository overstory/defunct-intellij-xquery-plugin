package uk.co.overstory.xquery.psi.impl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.Processor;
import uk.co.overstory.xquery.psi.XqyFunctiondecl;
import uk.co.overstory.xquery.psi.XqyNamedElement;
import uk.co.overstory.xquery.psi.XqyVardecl;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/14/12
 * Time: 4:15 PM
 */
public class XqyReferenceImpl<T extends PsiElement> extends PsiReferenceBase<T>
{
	public XqyReferenceImpl (@NotNull T element, TextRange range)
	{
		super (element, range);
	}

	@Override
	public PsiElement resolve()
	{
		return ResolveCache.getInstance (myElement.getProject()).resolveWithCaching (this, MY_RESOLVER, true, false);
	}

	private PsiElement resolveInner()
	{
		final Ref<PsiElement> result = Ref.create(null);
		final String text = getRangeInElement().substring (myElement.getText());

		processResolveVariants(new Processor<PsiElement>()
		{
			@Override
			public boolean process(PsiElement psiElement)
			{
				if (psiElement instanceof PsiNamedElement)
				{
					if (text.equals(((PsiNamedElement)psiElement).getName()))
					{
						result.set(psiElement);
						return false;
					}
				}

				return true;
			}
		});
		return result.get();
	}

	private static final ResolveCache.Resolver MY_RESOLVER =
		new ResolveCache.Resolver()
		{
			@Override
			public PsiElement resolve (PsiReference psiReference, boolean incompleteCode)
			{
				return ((XqyReferenceImpl)psiReference).resolveInner();
			}
		};

	// ----------------------------------------------------------------

	@NotNull
	@Override
	public Object[] getVariants()
	{
		final ArrayList<LookupElement> list = new ArrayList<LookupElement>();

		processResolveVariants (new Processor<PsiElement>()
		{
			@Override
			public boolean process(PsiElement psiElement)
			{
				if (psiElement instanceof XqyNamedElement)
				{
					LookupElementBuilder builder =
						LookupElementBuilder.create ((PsiNamedElement) psiElement).setIcon (psiElement.getIcon (Iconable.ICON_FLAG_OPEN));
					list.add (isBoldable (psiElement) ? builder.setBold() : builder);
				}

				return true;
			}
		});

		return list.toArray (new Object[list.size()]);
	}

	private boolean isBoldable (PsiElement e)
	{
		if ((e instanceof XqyVardecl) || (e instanceof XqyFunctiondecl)) return true;

		return false;
	}

	private void processResolveVariants (final Processor<PsiElement> processor)
	{
		// FIXME: Need to return PsiElement instances for variables/functions defined in this file

//		PsiFile file = myElement.getContainingFile();
//
//		if (!(file instanceof XqyFile)) return;
//
//		final boolean ruleMode = myElement instanceof BnfStringLiteralExpression;
//
//		BnfAttrs attrs = PsiTreeUtil.getParentOfType (myElement, BnfAttrs.class);
//
//		if (attrs != null && !ruleMode) {
//			if (!ContainerUtil.process (attrs.getChildren (), processor)) return;
//			final int textOffset = myElement.getTextOffset();
//			ContainerUtil.process(((BnfFile)file).getAttributes(), new Processor<BnfAttrs>() {
//				@Override
//				public boolean process(BnfAttrs attrs) {
//					return attrs.getTextOffset() <= textOffset && ContainerUtil.process(attrs.getAttrList(), processor);
//				}
//			});
//		}
//		else {
//			ContainerUtil.process (((BnfFile)file).getRules(), processor);
//		}
	}
}

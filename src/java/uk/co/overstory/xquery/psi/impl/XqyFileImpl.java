package uk.co.overstory.xquery.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.Processor;
import uk.co.overstory.xquery.XqyFileType;
import uk.co.overstory.xquery.XqyLanguage;
import uk.co.overstory.xquery.psi.XqyCompositeElement;
import uk.co.overstory.xquery.psi.XqyFile;
import uk.co.overstory.xquery.psi.XqyFunctionName;
import uk.co.overstory.xquery.psi.XqyVarName;

import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/10/12
 * Time: 7:37 PM
 */
public class XqyFileImpl  extends PsiFileBase implements XqyFile
{
	private CachedValue<List<XqyCompositeElement>> declarationsValue;

	public XqyFileImpl (FileViewProvider fileViewProvider)
	{
		super (fileViewProvider, XqyLanguage.INSTANCE);
//System.out.println ("XqyFileImpl constructor, provider=" + getName ());
	}

	@NotNull
	@Override
	public FileType getFileType()
	{
		return XqyFileType.INSTANCE;
	}

	@Override
	public String toString()
	{
		return "XqyFile:" + getName();
	}

	@Override
	public List<XqyCompositeElement> getDeclarations()
	{
		if (declarationsValue == null) {
			declarationsValue = CachedValuesManager.getManager (getProject()).createCachedValue (new CachedValueProvider<List<XqyCompositeElement>>()
			{
				@Override
				public Result<List<XqyCompositeElement>> compute()
				{
					return Result.create (findDeclarations(), XqyFileImpl.this);
				}
			}, false);
		}
		return declarationsValue.getValue();
	}

	// -----------------------------------------------------------

	private List<XqyCompositeElement> findDeclarations()
	{
		final List<XqyCompositeElement> result = new ArrayList<XqyCompositeElement> ();

		processChildrenDummyAware (this, new Processor<PsiElement>()
		{
			@Override
			public boolean process (PsiElement psiElement)
			{
				if ((psiElement instanceof XqyVarName) || (psiElement instanceof XqyFunctionName)) {
					result.add ((XqyCompositeElement) psiElement);
				}

				return true;
			}
		});
		return result;
	}

	@SuppressWarnings("OverlyComplexAnonymousInnerClass")
	private static boolean processChildrenDummyAware (PsiElement element, final Processor<PsiElement> processor)
	{
		return new Processor<PsiElement>()
		{
			@Override
			public boolean process (PsiElement psiElement)
			{
				for (PsiElement child : psiElement.getChildren()) {
					if (child instanceof GeneratedParserUtilBase.DummyBlock) {
						process (child);
					} else {
						processor.process (child);
					}

					processChildrenDummyAware (child, processor);
				}

				return true;
			}
		}.process (element);
	}
}

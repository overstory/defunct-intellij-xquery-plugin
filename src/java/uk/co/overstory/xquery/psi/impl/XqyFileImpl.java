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
import uk.co.overstory.xquery.psi.XqyFile;
import uk.co.overstory.xquery.psi.XqyFunctiondecl;
import uk.co.overstory.xquery.psi.XqyVardecl;

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
	private CachedValue<List<XqyVardecl>> myVariablesValue;
	private CachedValue<List<XqyFunctiondecl>> myFunctionsValue;

	public XqyFileImpl (FileViewProvider fileViewProvider)
	{
		super (fileViewProvider, XqyLanguage.INSTANCE);
System.out.println ("XqyFileImpl constructor, provider=" + getName ());
	}

	@NotNull
	@Override
	public FileType getFileType()
	{
		return XqyFileType.INSTANCE;
	}

	@Override
	public String toString ()
	{
		return "XqyFile:" + getName();
	}

	@Override
	public List<XqyVardecl> getVariables()
	{
		if (myVariablesValue == null) {
			myVariablesValue = CachedValuesManager.getManager (getProject()).createCachedValue (new CachedValueProvider<List<XqyVardecl>>()
			{
				@Override
				public Result<List<XqyVardecl>> compute()
				{
					return Result.create (calcVariables(), XqyFileImpl.this);
				}
			}, false);
		}
		return myVariablesValue.getValue();
	}

	@Override
	public List<XqyFunctiondecl> getFunctions()
	{
		if (myFunctionsValue == null) {
			myFunctionsValue = CachedValuesManager.getManager (getProject()).createCachedValue (new CachedValueProvider<List<XqyFunctiondecl>>()
			{
				@Override
				public Result<List<XqyFunctiondecl>> compute()
				{
					return Result.create (calcFunctions (), XqyFileImpl.this);
				}
			}, false);
		}
		return myFunctionsValue.getValue();
	}

	// -----------------------------------------------------------

	private List<XqyVardecl> calcVariables()
	{
System.out.println ("calcVariables");
		final List<XqyVardecl> result = new ArrayList<XqyVardecl> ();

		processChildrenDummyAware (this, new Processor<PsiElement> ()
		{
			@Override
			public boolean process (PsiElement psiElement)
			{
System.out.println ("  looking at: " + psiElement.getText ());
				if (psiElement instanceof XqyVardecl) {
System.out.println ("  adding: " + psiElement.getText ());
					result.add ((XqyVardecl) psiElement);
				}
				return true;
			}
		});
		return result;
	}

	private List<XqyFunctiondecl> calcFunctions()
	{
System.out.println ("calcFunctions");
		final List<XqyFunctiondecl> result = new ArrayList<XqyFunctiondecl>();

		processChildrenDummyAware (this, new Processor<PsiElement>()
		{
			@Override
			public boolean process (PsiElement psiElement)
			{
System.out.println ("  looking at: " + psiElement.toString ());
				if (psiElement instanceof XqyFunctiondecl) {
System.out.println ("  adding: " + psiElement.toString ());
					result.add ((XqyFunctiondecl) psiElement);
				}
				return true;
			}
		});
		return result;
	}

	private static boolean processChildrenDummyAware (PsiElement element, final Processor<PsiElement> processor)
	{
		return new Processor<PsiElement>()
		{
			@Override
			public boolean process (PsiElement psiElement)
			{
				for (PsiElement child = psiElement.getFirstChild(); child != null; child = child.getNextSibling()) {
System.out.println ("Processing child: " + child.toString ());
					if (child instanceof GeneratedParserUtilBase.DummyBlock) {
						if (!process (child)) {
							return false;
						}
					} else if (!processor.process (child)) {
						return false;
					}
				}
				return true;
			}
		}.process (element);
	}


}

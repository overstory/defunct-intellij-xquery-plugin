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
import uk.co.overstory.xquery.psi.XqyFunctionDecl;
import uk.co.overstory.xquery.psi.XqyVarDecl;

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
	private CachedValue<List<XqyVarDecl>> myVariablesValue;
	private CachedValue<List<XqyFunctionDecl>> myFunctionsValue;

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
	public String toString ()
	{
		return "XqyFile:" + getName();
	}

	@Override
	public List<XqyVarDecl> getVariables()
	{
		if (myVariablesValue == null) {
			myVariablesValue = CachedValuesManager.getManager (getProject()).createCachedValue (new CachedValueProvider<List<XqyVarDecl>>()
			{
				@Override
				public Result<List<XqyVarDecl>> compute()
				{
					return Result.create (calcVariables(), XqyFileImpl.this);
				}
			}, false);
		}
		return myVariablesValue.getValue();
	}

	@Override
	public List<XqyFunctionDecl> getFunctions()
	{
		if (myFunctionsValue == null) {
			myFunctionsValue = CachedValuesManager.getManager (getProject()).createCachedValue (new CachedValueProvider<List<XqyFunctionDecl>>()
			{
				@Override
				public Result<List<XqyFunctionDecl>> compute()
				{
					return Result.create (calcFunctions (), XqyFileImpl.this);
				}
			}, false);
		}
		return myFunctionsValue.getValue();
	}

	// -----------------------------------------------------------

	private List<XqyVarDecl> calcVariables()
	{
System.out.println ("calcVariables");
		final List<XqyVarDecl> result = new ArrayList<XqyVarDecl> ();

		processChildrenDummyAware (this, new Processor<PsiElement> ()
		{
			@Override
			public boolean process (PsiElement psiElement)
			{
System.out.println ("  looking at: " + psiElement.getText ());
				if (psiElement instanceof XqyVarDecl) {
System.out.println ("  adding: " + psiElement.getText ());
					result.add ((XqyVarDecl) psiElement);
				}
				return true;
			}
		});
		return result;
	}

	private List<XqyFunctionDecl> calcFunctions()
	{
System.out.println ("calcFunctions");
		final List<XqyFunctionDecl> result = new ArrayList<XqyFunctionDecl>();

		processChildrenDummyAware (this, new Processor<PsiElement>()
		{
			@Override
			public boolean process (PsiElement psiElement)
			{
System.out.println ("  looking at: " + psiElement.toString ());
				if (psiElement instanceof XqyFunctionDecl) {
System.out.println ("  adding: " + psiElement.toString ());
					result.add ((XqyFunctionDecl) psiElement);
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

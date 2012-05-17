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
import uk.co.overstory.xquery.psi.XqyModule;
import uk.co.overstory.xquery.psi.XqyModuleDecl;
import uk.co.overstory.xquery.psi.XqyModuleImport;
import uk.co.overstory.xquery.psi.XqyNamespaceDecl;
import uk.co.overstory.xquery.psi.XqyVarName;
import uk.co.overstory.xquery.psi.XqyVersionDecl;
import uk.co.overstory.xquery.psi.XqyXqueryVersionString;
import uk.co.overstory.xquery.psi.util.TreeUtil;

import org.intellij.grammar.parser.GeneratedParserUtilBase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/10/12
 * Time: 7:37 PM
 */
public class XqyFileImpl extends PsiFileBase implements XqyFile
{
	private CachedValue<List<XqyCompositeElement>> declarationsValue;
	private CachedValue<Map<String,String>> namespacesValue;

	private static final Map<String,String> builtinNSs_1_0_ml = new HashMap<String, String>();
	static {
		builtinNSs_1_0_ml.put ("cts", "http://marklogic.com/cts");
		builtinNSs_1_0_ml.put ("dav", "DAV:");
		builtinNSs_1_0_ml.put ("dbg", "http://marklogic.com/xdmp/debug");
		builtinNSs_1_0_ml.put ("dir", "http://marklogic.com/xdmp/directory");
		builtinNSs_1_0_ml.put ("err", "http://www.w3.org/2005/xqt-errors");
		builtinNSs_1_0_ml.put ("error", "http://marklogic.com/xdmp/error");
		builtinNSs_1_0_ml.put ("fn", "http://www.w3.org/2005/xpath-functions");
		builtinNSs_1_0_ml.put ("local", "http://www.w3.org/2005/xquery-local-functions");
		builtinNSs_1_0_ml.put ("lock", "http://marklogic.com/xdmp/lock");
		builtinNSs_1_0_ml.put ("map", "http://marklogic.com/xdmp/map");
		builtinNSs_1_0_ml.put ("math", "http://marklogic.com/xdmp/math");
		builtinNSs_1_0_ml.put ("prof", "http://marklogic.com/xdmp/profile");
		builtinNSs_1_0_ml.put ("prop", "http://marklogic.com/xdmp/property");
		builtinNSs_1_0_ml.put ("sec", "http://marklogic.com/xdmp/security");
		builtinNSs_1_0_ml.put ("spell", "http://marklogic.com/xdmp/spell");
		builtinNSs_1_0_ml.put ("xdmp", "http://marklogic.com/xdmp");
		builtinNSs_1_0_ml.put ("xml", "http://www.w3.org/XML/1998/namespace");
		builtinNSs_1_0_ml.put ("xmlns", "http://www.w3.org/2000/xmlns/");
		builtinNSs_1_0_ml.put ("xqe", "http://marklogic.com/xqe");
		builtinNSs_1_0_ml.put ("xqterr", "http://www.w3.org/2005/xqt-errors");
		builtinNSs_1_0_ml.put ("xs", "http://www.w3.org/2001/XMLSchema");
	}

	private static final Map<String,String> builtinNSs_1_0 = new HashMap<String, String>();
	static {
		builtinNSs_1_0.put ("err", "http://www.w3.org/2005/xqt-errors");
		builtinNSs_1_0.put ("fn", "http://www.w3.org/2005/xpath-functions");
		builtinNSs_1_0.put ("local", "http://www.w3.org/2005/xquery-local-functions");
		builtinNSs_1_0.put ("xml", "http://www.w3.org/XML/1998/namespace");
		builtinNSs_1_0.put ("xmlns", "http://www.w3.org/2000/xmlns/");
		builtinNSs_1_0.put ("xs", "http://www.w3.org/2001/XMLSchema");
	}

	private static final Map<String,String> builtinNSs_0_9_ml = new HashMap<String, String>();
	static {
		builtinNSs_0_9_ml.put ("cts", "http://marklogic.com/cts");
		builtinNSs_0_9_ml.put ("dav", "DAV:");
		builtinNSs_0_9_ml.put ("dbg", "http://marklogic.com/xdmp/debug");
		builtinNSs_0_9_ml.put ("dir", "http://marklogic.com/xdmp/directory");
		builtinNSs_0_9_ml.put ("err", "http://marklogic.com/xdmp/error");
		builtinNSs_0_9_ml.put ("error", "http://marklogic.com/xdmp/error");
		builtinNSs_0_9_ml.put ("fn", "http://www.w3.org/2003/05/xpath-functions");
		builtinNSs_0_9_ml.put ("local", "http://www.w3.org/2005/xquery-local-functions");
		builtinNSs_0_9_ml.put ("lock", "http://marklogic.com/xdmp/lock");
		builtinNSs_0_9_ml.put ("map", "http://marklogic.com/xdmp/map");
		builtinNSs_0_9_ml.put ("math", "http://marklogic.com/xdmp/math");
		builtinNSs_0_9_ml.put ("prof", "http://marklogic.com/xdmp/profile");
		builtinNSs_0_9_ml.put ("prop", "http://marklogic.com/xdmp/property");
		builtinNSs_0_9_ml.put ("sec", "http://marklogic.com/xdmp/security");
		builtinNSs_0_9_ml.put ("spell", "http://marklogic.com/xdmp/spell");
		builtinNSs_0_9_ml.put ("xdt", "http://www.w3.org/2003/05/xpath-datatypes");
		builtinNSs_0_9_ml.put ("xdmp", "http://marklogic.com/xdmp");
		builtinNSs_0_9_ml.put ("xml", "http://www.w3.org/XML/1998/namespace");
		builtinNSs_0_9_ml.put ("xmlns", "http://www.w3.org/2000/xmlns/");
		builtinNSs_0_9_ml.put ("xqe", "http://marklogic.com/xqe");
		builtinNSs_0_9_ml.put ("xqterr", "http://www.w3.org/2005/xqt-errors");
		builtinNSs_0_9_ml.put ("xs", "http://www.w3.org/2001/XMLSchema");
	}

	// ---------------------------------------------------------------

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
	public String getXQueryVersion()
	{
		XqyModule module = this.findChildByClass (XqyModule.class);

		if (module == null) return getDefaultXQueryVersion();

		@SuppressWarnings("unchecked")
		String version = TreeUtil.getTextOfDescendentElementAtPath (module, XqyVersionDecl.class, XqyXqueryVersionString.class);

		return (version == null) ? getDefaultXQueryVersion() : scrubString (version);
	}

	// ToDo: Get this from an options setting configurable by user
	@Override
	public String getDefaultXQueryVersion()
	{
		return "1.0-ml";
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

	@Override
	public Map<String,String> getNamespaceMappings()
	{
		if (namespacesValue == null) {
			namespacesValue = CachedValuesManager.getManager (getProject()).createCachedValue (new CachedValueProvider<Map<String,String>>()
			{
				@Override
				public Result<Map<String,String>> compute()
				{
					return Result.create (findNamespaceDeclarations(), XqyFileImpl.this);
				}
			}, false);
		}

		return namespacesValue.getValue();
	}

	// ToDo: Look at module decl and default NS decl.  Chase namespace map if needed to get prefix.
	@Override
	public String getDefaultFunctionNsPrefix()
	{
		return "fn";
	}

	// -----------------------------------------------------------

	private Map<String, String> builtinNamespacesMap()
	{
		String xqueryVersion = getXQueryVersion();

		if ("1.0-ml".equals (xqueryVersion)) {
			return builtinNSs_1_0_ml;
		}
		if ("1.0".equals (xqueryVersion)) {
			return builtinNSs_1_0;
		}
		if ("0.9-ml".equals (xqueryVersion)) {
			return builtinNSs_0_9_ml;
		}

		return builtinNSs_1_0_ml;
	}

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

	private Map<String,String> findNamespaceDeclarations()
	{
		final Map<String,String> result = new HashMap<String,String>();

		result.putAll (builtinNamespacesMap());

		processChildrenDummyAware (this, new Processor<PsiElement>()
		{
			@Override
			public boolean process (PsiElement psiElement)
			{
				if (psiElement instanceof XqyNamespaceDecl) {
					XqyNamespaceDecl e = (XqyNamespaceDecl) psiElement;

					result.put (e.getNamespaceName().getText(), scrubString (e.getNamespaceValue().getText()));
				}

				if (psiElement instanceof XqyModuleDecl) {
					XqyModuleDecl e = (XqyModuleDecl) psiElement;

					result.put (e.getNamespaceName().getText(), scrubString (e.getNamespaceValue().getText()));
				}

				if (psiElement instanceof XqyModuleImport) {
					XqyModuleImport e = (XqyModuleImport) psiElement;

					result.put (e.getNamespaceName().getText(), scrubString (e.getNamespaceValue().getText()));
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

	private static String scrubString (String text)
	{
		if (text.startsWith ("\"") && text.endsWith ("\"")) {
			return text.substring (1, text.length() - 1);
		}

		if (text.startsWith ("'") && text.endsWith ("'")) {
			return text.substring (1, text.length() - 1);
		}

		return text;
	}
}

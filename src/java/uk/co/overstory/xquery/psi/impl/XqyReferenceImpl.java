package uk.co.overstory.xquery.psi.impl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import uk.co.overstory.xquery.psi.*;
import uk.co.overstory.xquery.psi.resolve.XqyResolveProcessor;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/14/12
 * Time: 4:15 PM
 */
public class XqyReferenceImpl<T extends XqyCompositeElementImpl> extends PsiPolyVariantReferenceBase<T> implements XqyReference
{
	static final ResolveState declResolveState = new ResolveState();
	static final ResolveState variantResolveState = new ResolveState();

	private static final XqyReferenceResolver declResolver = new XqyReferenceResolver (declResolveState);
	private static final XqyReferenceResolver variantResolver = new XqyReferenceResolver (variantResolveState);

	public XqyReferenceImpl (@NotNull T element, TextRange range)
	{
		super (element, range);
	}

	public PsiElement resolve()
	{
//System.out.println ("XqyReferenceImpl: resolve(): " + myElement.toString() + "/" + myElement.getText());
		ResolveResult[] results = multiResolve (false);

		return results.length == 1 ? results[0].getElement() : null;
	}

	@NotNull
	@Override
	public Object[] getVariants()
	{
//System.out.println ("XqyReferenceImpl.getVariants");
		final ResolveCache resolveCache = ResolveCache.getInstance (getProject());

		ResolveResult[] results = resolveCache.resolveWithCaching (this, variantResolver, true, false);
		LookupElement [] lookups = new LookupElement[results.length];

		for (int i = 0; i < results.length; i++) {
			PsiElement element = results[i].getElement();

			// ToDo: make a proper mutable builder to accumlulate attributes, use it to build LookUpElement
			LookupElementBuilder builder = LookupElementBuilder.create ((PsiNamedElement) element)
				.setIcon (element.getIcon (0))
				.setBold (boldForElement (element))	// FIXME
				.setTailText (tailTextForElement (element), true)
				.setTypeText (typeTextForElement (element), false);

			lookups [i] = builder;
		}

		return lookups;
	}

	@NotNull
	public Project getProject() {
		return myElement.getContainingFile().getProject();
	}

	@Override
	public String getLocalname()
	{
		XqyQName qname = qnameFor (getElement());

		XqyPrefixedName prefixed = qname.getPrefixedName();
		XqyUnprefixedName unprefixed = qname.getUnprefixedName();
		XqyLocalPart localPart = (prefixed == null) ? unprefixed.getLocalPart() : prefixed.getLocalPart();

		return localPart.getText();
	}

	@Override
	public String getNamespaceName()
	{

		return null;  // FIXME: auto-generated
	}

	@Override
	public boolean hasNamespace()
	{
		XqyQName qname = qnameFor (getElement());

		return (qname.getPrefixedName() != null) && (qname.getPrefixedName().getPrefix ().getText () != null);
	}

	@NotNull
	@Override
	public ResolveResult[] multiResolve (boolean incompleteCode)
	{
//System.out.println ("XqyReferenceImpl.multiResolve(" + incompleteCode + ")");

		final ResolveCache resolveCache = ResolveCache.getInstance (getProject());

		return resolveCache.resolveWithCaching (this, declResolver, true, incompleteCode);
	}

	// ---------------------------------------------------------

	private String typeTextForElement (PsiElement element)
	{
		XqySequenceType seqType = findTypeDeclaration (element);

		if (seqType != null) {
			return seqType.getText();
		}

		if (PsiTreeUtil.getParentOfType (element, XqyPositionalVar.class) != null) return "xs:integer";

		if (PsiTreeUtil.getParentOfType (element, XqyCatchClause.class) != null) return "element(error)";

		return null;
	}

	private XqySequenceType findTypeDeclaration (PsiElement element)
	{
		XqyDollarVarName dollarVar = PsiTreeUtil.getParentOfType (element, XqyDollarVarName.class);

		for (PsiElement el = dollarVar.getNextSibling(); el != null; el = el.getNextSibling()) {
			if (el instanceof XqyTypeDeclaration) {
				return ((XqyTypeDeclaration) el).getSequenceType();
			}
			if (el instanceof XqySequenceType) {
				return (XqySequenceType) el;
			}
		}

//		PsiElement parent = element.getParent();
//
//		for (PsiElement el : parent.getChildren()) {
//			if (el instanceof XqyTypeDeclaration) {
//				return ((XqyTypeDeclaration) el).getSequenceType();
//			}
//			if (el instanceof XqySequenceType) {
//				return (XqySequenceType) el;
//			}
//		}

		return null;
	}

	private String tailTextForElement (PsiElement element)
	{
		if (element instanceof XqyFunctionName) return tailTextForFunction ((XqyFunctionName) element);

		if (element instanceof XqyVarName) return tailTextForVariable (element);

		return "???";
	}

	private String tailTextForVariable (PsiElement element)
	{
		if (PsiTreeUtil.getParentOfType (element, XqyParam.class) != null) return " - parameter";
		if (PsiTreeUtil.getParentOfType (element, XqyQuantVar.class) != null) return " - quantified";
		if (PsiTreeUtil.getParentOfType (element, XqyLetVar.class) != null) return " - let";
		if (PsiTreeUtil.getParentOfType (element, XqyCaseVar.class) != null) return " - case";
		if (PsiTreeUtil.getParentOfType (element, XqyCatchClause.class) != null) return " - catch";
		if (PsiTreeUtil.getParentOfType (element, XqyVarDecl.class) != null) return " - prolog var";

		if (PsiTreeUtil.getParentOfType (element, XqyForVar.class) != null) {
			if (PsiTreeUtil.getParentOfType (element, XqyPositionalVar.class) != null) {
				XqyForVar forVar = PsiTreeUtil.getParentOfType (element, XqyForVar.class);
				String forVarname = forVar.getDollarVarName().getVarName().getText();

				return " - position ($" + forVarname + ")";
			}
			return " - for";
		}

		return " - variable";
	}

	private String tailTextForFunction (XqyFunctionName functionName)
	{
		XqyFunctionDecl functionDecl = PsiTreeUtil.getParentOfType (functionName, XqyFunctionDecl.class);
		XqyParamList paramList = functionDecl.getParamList();

		if (paramList == null) return "()";

		List<XqyParam> params = paramList.getParamList();
		StringBuilder sb = new StringBuilder ("(");

		for (XqyParam param : params) {
			if (sb.length () != 1) sb.append (", ");

			sb.append ("$").append (param.getDollarVarName().getVarName().getText());

			if (param.getTypeDeclaration() != null) {
				sb.append (" ");
				sb.append (param.getTypeDeclaration().getText());
			}
		}

		sb.append (")");

		return sb.toString();
	}


	private boolean boldForElement (PsiElement element)
	{
		if ((element instanceof XqyVarName) && (PsiTreeUtil.getParentOfType (element, XqyVarDecl.class) != null)) {
			return true;
		}

		return false;
	}

	private XqyQName qnameFor (T element)
	{
		if (element instanceof XqyRefFunctionName) return ((XqyRefFunctionName) element).getQName();
		if (element instanceof XqyRefVarName) return ((XqyRefVarName) element).getQName();

		return null;
	}

	// ---------------------------------------------------------

	private static class XqyReferenceResolver implements ResolveCache.PolyVariantResolver<XqyReference>
	{
		private final ResolveState resolveState;
		private static final ResolveResult[] EMPTY_RESULT = new ResolveResult[0];

		private XqyReferenceResolver (ResolveState resolveState)
		{
			this.resolveState = resolveState;
		}

		public ResolveResult[] resolve (XqyReference reference, boolean incompleteCode)
		{
			final String localname = reference.getLocalname();

			if (localname == null) {
				return null;
			}

			PsiElement ref = reference.getElement();

			XqyResolveProcessor processor = new XqyResolveProcessor (localname, ref, incompleteCode);

			ResolveUtil.treeWalkUp (ref, processor, resolveState);

			// FIXME: This isn't right yet
			if (reference.hasNamespace()) {
				final String nsName = reference.getNamespaceName();

				XqyResolveProcessor nsProcessor = new XqyResolveProcessor (nsName, ref, incompleteCode);

				resolveNamespace (reference, nsProcessor);
			}

			ResolveResult[] candidates = processor.getCandidates();

			if (candidates.length > 0) {
				return candidates;
			}

			return EMPTY_RESULT;
		}

		private void resolveNamespace (XqyReference symbol, XqyResolveProcessor processor)
		{
			// process namespaces
//			final Project project = symbol.getProject();
//			final GlobalSearchScope scope = GlobalSearchScope.allScope (project);
//			final Collection<ClNs> nses = StubIndex.getInstance ().get (ClojureNsNameIndex.KEY, symbol.getNameString (), project, scope);
//
//			for (ClNs ns : nses) {
//				ResolveUtil.processElement (processor, ns);
//			}
		}

/*
		private void resolveImpl (XqyReference symbol, XqyResolveProcessor processor)
		{
			final XqyReference qualifier = symbol.getQualifierSymbol ();

			//process other places
			if (qualifier == null) {
				ResolveUtil.treeWalkUp (symbol, processor);
			} else {
				for (ResolveResult result : qualifier.multiResolve (false)) {
					final PsiElement element = result.getElement();

					if (element != null) {
						final PsiElement sep = symbol.getSeparatorToken ();

						if (sep != null) {
							if ("/".equals (sep.getText ())) {

								//get class elements
								if (element instanceof PsiClass) {
									element.processDeclarations (processor, ResolveState.initial (), null, symbol);
								}

								//get namespace declarations
								if (element instanceof ClSyntheticNamespace) {
									final String fqn = ((ClSyntheticNamespace) element).getQualifiedName ();
									// namespace declarations
									for (PsiNamedElement named : NamespaceUtil.getDeclaredElements (fqn, element.getProject ())) {
										if (!ResolveUtil.processElement (processor, named)) {
											return;
										}
									}
								}

							} else if (".".equals (sep.getText ())) {
								element.processDeclarations (processor, ResolveState.initial (), null, symbol);
							}
						}
					}
				}
			}
		}
*/

	}


// ------------------------------------------------------------------------

// ------------------------------------------------------------------------


/*
	@Override
	public PsiElement resolve()
	{
System.out.println ("XqyReferenceImpl<T>.resolve()");
		return ResolveCache.getInstance (myElement.getProject()).resolveWithCaching (this, MY_RESOLVER, true, false);
	}

	private PsiElement resolveInner()
	{
		final Ref<PsiElement> result = Ref.create(null);
		final String text = getRangeInElement().substring (myElement.getText());
System.out.println ("resolveInner(): " + myElement.toString () + "=" + text);

		processResolveVariants(new Processor<PsiElement>()
		{
			@Override
			public boolean process(PsiElement psiElement)
			{
				if (psiElement instanceof PsiNamedElement)
				{
System.out.println ("process: name=" + ((PsiNamedElement)psiElement).getName());
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
System.out.println ("XqyReferenceImpl<T>.getVariants()");
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
		if ((e instanceof XqyVarname) || (e instanceof XqyFunctionname)) return true;

		return false;
	}

	private void processResolveVariants (final Processor<PsiElement> processor)
	{
		// FIXME: Need to return PsiElement instances for variables/functions defined in this file

System.out.println ("processResolveVariants: " + myElement.toString() + "=" + myElement.getText());

		PsiFile file = myElement.getContainingFile();

		if (!(file instanceof XqyFile)) return;

		if (myElement instanceof XqyVarNameReference) {
			ContainerUtil.process (((XqyFile) file).getDeclarations(), processor);
		}

		if (myElement instanceof XqyFunctionname) {		// FIXME: Not unique to function call
			ContainerUtil.process (((XqyFile) file).getFunctions(), processor);
		}



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
*/
}

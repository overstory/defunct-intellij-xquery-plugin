package uk.co.overstory.xquery.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import uk.co.overstory.xquery.completion.FunctionDefs;
import uk.co.overstory.xquery.psi.*;
import uk.co.overstory.xquery.psi.impl.XqyFileImpl;
import uk.co.overstory.xquery.psi.util.TreeUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/24/12
 * Time: 2:42 PM
 */
@SuppressWarnings("OverlyCoupledClass")
public class XqyAnnotator implements Annotator, DumbAware
{
	private static final String ALLOWED_XQUERY_VERSIONS = "'1.0-ml', '1.0', '3.0' or '0.9-ml'";

	private static final String [] knownXQueryVersionStrings = {
		"0.9-ml", "1.0", "1.0-ml", "3.0"		// FIXME: This should be defined in a property or something, matching parse rules in effect
	};

	private static final String [] typeConstructorPrefixes = {
		"xs:", "cts:", "map:"
	};

//	private static final String DEFAULT_XQUERY_TYPE = "item()*";

	private final FunctionDefs functionDefs = FunctionDefs.instance();

	@Override
	public void annotate (@NotNull PsiElement element, @NotNull AnnotationHolder holder)
	{
		if (element instanceof XqyXqueryVersionString) {
			annotateXQueryVersion (element, holder);
		}

		if (element instanceof XqyVarDecl) {
			checkForDupeGlobalVars (element, holder);
		}

		if (element instanceof XqyFunctionDecl) {
			checkForDupeFunctions (element, holder);
		}

		if (element instanceof XqyPrefix) {
			annotateNamespacePrefix (element, holder);
		}

		if ((element instanceof XqyRefVarName) || (element instanceof XqyRefFunctionName)) {
			annotateReference (element, holder);
		}
	}

	private void annotateNamespacePrefix (PsiElement element, AnnotationHolder holder)
	{
		String prefix = element.getText();
		String value = findNamespaceValue (element, prefix);

		if (value == null) {
			holder.createErrorAnnotation (element, "Undefined namespace prefix '" + prefix + "'");
		} else {
			holder.createInfoAnnotation (element, prefix + "=\"" + value + "\"");
		}
	}

	private String findNamespaceValue (PsiElement element, String prefix)
	{
		XqyFileImpl fileImpl = (XqyFileImpl) element.getContainingFile();

		Map<String,String> nsMap = fileImpl.getNamespaceMappings();

		return nsMap.get (prefix);
	}

	private void annotateXQueryVersion (PsiElement element, AnnotationHolder holder)
	{
		if ( ! validXqueryVersion (element)) {
			holder.createErrorAnnotation (element, "Unknown version, should be: " + ALLOWED_XQUERY_VERSIONS);
		} else {
			holder.createInfoAnnotation (element, ALLOWED_XQUERY_VERSIONS);
		}
	}

	@SuppressWarnings("unchecked")
	private void annotateReference (PsiElement element, AnnotationHolder holder)
	{
		PsiReference reference = element.getReference();
		Object resolve = reference == null ? null : reference.resolve();

		// Annotate unknown variables
		if ((element instanceof XqyRefVarName) && (resolve == null) && ( ! variableInScope (element))) {
			PsiElement localPart = TreeUtil.getDescendentElementAtPath (element, XqyQName.class, XqyLocalPart.class);
			holder.createErrorAnnotation (localPart, "Undefined variable '$" + element.getText() + "'");
			return;
		}

		// Annotate function calls with short description
		if ((element instanceof XqyRefFunctionName)) {
			annotateForBuiltinFunction (element, holder);
			annotateForUserDefinedFunction (element, holder, resolve);

			// ToDo: Need to understand why this is not run if ns of qname is error annotated
			// Annotate unknown functions
			if ((resolve == null) && ( ! functionInScope (element))) {
				PsiElement localPart = TreeUtil.getDescendentElementAtPath (element, XqyQName.class, XqyLocalPart.class);
				holder.createErrorAnnotation (localPart, "Undefined function '" + element.getText() + "()'");
				return;
			}
		}
	}

	private void annotateForBuiltinFunction (PsiElement element, AnnotationHolder holder)
	{
		String functionName = ((XqyRefFunctionName) element).getQName().getText();
		FunctionDefs.Function func = functionDefs.getFunction (functionName);

		if (func == null) {
			XqyFileImpl fileImpl = (XqyFileImpl) element.getContainingFile();

			func = functionDefs.getFunction (fileImpl.getDefaultFunctionNsPrefix() + ":" + functionName);
		}

		if (func == null) return;

		annotateFunction (element, holder, func);
	}

	private void annotateForUserDefinedFunction (PsiElement element, AnnotationHolder holder, Object resolved)
	{
		if (resolved == null) return;

		XqyFunctionDecl functionDecl = null;

		if (resolved instanceof XqyFunctionDecl) {
			functionDecl = (XqyFunctionDecl) resolved;
		} else {
			functionDecl = PsiTreeUtil.getParentOfType ((PsiElement) resolved, XqyFunctionDecl.class);
		}

		if (functionDecl == null) return;

		FunctionDefs.Function func = functionDefForUserDefined (functionDecl);

		if (func != null) {
			annotateFunction (element, holder, func);
		}
	}

	private void annotateFunction (PsiElement element, AnnotationHolder holder, FunctionDefs.Function func)
	{
		@SuppressWarnings("unchecked")
		PsiElement localPart = TreeUtil.getDescendentElementAtPath (element, XqyQName.class, XqyLocalPart.class);

		if (localPart == null) localPart = element;

		holder.createInfoAnnotation (localPart, func.getSummary());
		holder.createInfoAnnotation (localPart, func.getFullName() + " " + func.paramListAsString() +
			((func.getReturnType() == null) ? "" : (" as " + func.getReturnType())));

		List<FunctionDefs.Parameter> params = func.getParameters();
		XqyExprSingle[] callParams = PsiTreeUtil.getChildrenOfType (element.getParent(), XqyExprSingle.class);
		int callParamCount = (callParams == null) ? 0 : callParams.length;
		int minParamCount = func.getMinParamCount();

		if ( ! func.isVarargs()) {
			if ((callParamCount > params.size()) || (callParamCount < minParamCount)) {
				holder.createErrorAnnotation (localPart, "Wrong # params: got " + callParamCount + ", expected " +
					((params.size() == minParamCount) ? params.size() : minParamCount + " - " + params.size()) +
					": " + func.paramListAsString());
			}
		}

		// FIXME: Check arg types: Chase each call param, if func or var ref, check type of ref'ed item against func param def.
	}

	@SuppressWarnings("unchecked")
	private FunctionDefs.Function functionDefForUserDefined (XqyFunctionDecl functionDecl)
	{
		XqyFunctionName functionName = (XqyFunctionName) TreeUtil.getDescendentElementAtPath (functionDecl, XqyFunctionName.class);
		String prefix = TreeUtil.getTextOfDescendentElementAtPath (functionName, XqyQName.class, XqyPrefix.class);
		String localName = TreeUtil.getTextOfDescendentElementAtPath (functionName, XqyQName.class, XqyLocalPart.class);
		String fullName = TreeUtil.getTextOfDescendentElementAtPath (functionName, XqyQName.class);
		boolean priv = ! "private".equals (TreeUtil.getTextOfDescendentElementAtPath (functionDecl, XqyVisibility.class));
		String returnType = TreeUtil.getTextOfDescendentElementAtPath (functionDecl, XqyTypeDeclaration.class, XqySequenceType.class);

		FunctionDefs.Function func =  new FunctionDefs.Function (prefix, localName, fullName, priv, false, returnType);

		XqyParamList paramList = (XqyParamList) TreeUtil.getDescendentElementAtPath (functionDecl, XqyParamList.class);

		if ((paramList != null) && (paramList.getParamList() != null)) {
			for (XqyParam xparam : paramList.getParamList()) {
				String paramName = xparam.getDollarVarName().getVarName().getText ();
				String paramType = TreeUtil.getTextOfDescendentElementAtPath (xparam, XqyTypeDeclaration.class, XqySequenceType.class);

				func.addParam (new FunctionDefs.Parameter (paramName, paramType, false));
			}
		}

		return func;
	}

	// -----------------------------------------------------------

	@SuppressWarnings("unchecked")
	private void checkForDupeFunctions (@NotNull PsiElement funcDecl, @NotNull AnnotationHolder holder)
	{
		PsiElement funcNameElement = PsiTreeUtil.findChildOfType (funcDecl, XqyFunctionName.class);
		String funcName = getTextOfElement (funcNameElement);

		if (funcName == null) return;

		PsiElement [] children = funcDecl.getParent().getChildren();

		for (PsiElement element : children) {
			if (element == funcDecl) continue;	// self
			if ( ! (element instanceof XqyFunctionDecl)) continue;

			PsiElement targetFunctionNameElement = PsiTreeUtil.findChildOfType (element, XqyFunctionName.class);
			PsiElement targetLocalNameElement = TreeUtil.getDescendentElementAtPath (targetFunctionNameElement, XqyQName.class, XqyLocalPart.class);
			String targetName = getTextOfElement (targetFunctionNameElement);

			if (targetName == null) continue;

			if (funcName.equals (targetName)) {
				if (arrityOf (funcDecl) == arrityOf (element)) {
					holder.createErrorAnnotation (targetLocalNameElement, "Duplicate function declaration '" + targetName + "()'");
				} else {
					holder.createWeakWarningAnnotation (targetLocalNameElement, "Another function '" + targetName + "()' exists with different arity");
				}
			}
		}
	}

	private int arrityOf (PsiElement element)
	{
		PsiElement paramList = PsiTreeUtil.findChildOfType (element, XqyParamList.class);

		if (paramList == null) return 0;

		PsiElement [] params = PsiTreeUtil.getChildrenOfType (paramList, XqyParam.class);

		return (params == null) ? 0 : params.length;
	}

	private void checkForDupeGlobalVars (@NotNull PsiElement varDecl, @NotNull AnnotationHolder holder)
	{
		PsiElement varNameElement = PsiTreeUtil.findChildOfType (varDecl, XqyVarName.class);
		String varName = getTextOfElement (varNameElement);

		if (varName == null) return;

		for (PsiElement element : varDecl.getParent().getChildren()) {
			if (element == varDecl) continue;	// self
			if ( ! (element instanceof XqyVarDecl)) continue;

			PsiElement targetVarNameElement = PsiTreeUtil.findChildOfType (element, XqyVarName.class);
			String targetName = getTextOfElement (targetVarNameElement);

			if (targetName == null) continue;

			if (varName.equals (targetName)) {
				holder.createErrorAnnotation (targetVarNameElement, "Duplicate variable declaration '$" + targetName + "'");
			}
		}
	}

	private boolean validXqueryVersion (PsiElement xqueryVersionString)
	{
		for (String version: knownXQueryVersionStrings) {
			if (xqueryVersionString.getText().matches ("['\"]" + version + "['\"]")) return true;
		}

		return false;
	}

	private boolean functionInScope (PsiElement refFunctionName)
	{
		String name = refFunctionName.getText();

		if (FunctionDefs.instance().getFunction (name) != null) return true;

		XqyFileImpl fileImpl = (XqyFileImpl) refFunctionName.getContainingFile();

		if (FunctionDefs.instance().getFunction (fileImpl.getDefaultFunctionNsPrefix() + ":" + name) != null) return true;

		for (String prefix : typeConstructorPrefixes) {
			if (name.startsWith (prefix)) return true;
		}

		// ToDo: Need to look at imported modules for functions in namespaces
//		for (String prefix : fileImpl.getNamespaceMappings().keySet()) {
//			if (name.startsWith (prefix)) return true;
//		}

		return false;
	}

	private boolean variableInScope (PsiElement refVarName)
	{
		String name = refVarName.getText();

		// FIXME: get complete list, test namespace against imports and chase in imported module
		if ("cts:text".equals (name)) return true;

		return false;
	}

	private String getTextOfElement (PsiElement element)
	{
		if (element == null) return null;

		String s = element.getText();

		if ((s == null) || (s.length () == 0)) return null;

		return s;
	}

}

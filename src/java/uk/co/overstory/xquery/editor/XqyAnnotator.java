package uk.co.overstory.xquery.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import uk.co.overstory.xquery.completion.FunctionDefs;
import uk.co.overstory.xquery.psi.XqyExprSingle;
import uk.co.overstory.xquery.psi.XqyFunctionDecl;
import uk.co.overstory.xquery.psi.XqyFunctionName;
import uk.co.overstory.xquery.psi.XqyParam;
import uk.co.overstory.xquery.psi.XqyParamList;
import uk.co.overstory.xquery.psi.XqyRefFunctionName;
import uk.co.overstory.xquery.psi.XqyRefVarName;
import uk.co.overstory.xquery.psi.XqyVarDecl;
import uk.co.overstory.xquery.psi.XqyVarName;
import uk.co.overstory.xquery.psi.XqyXqueryVersionString;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/24/12
 * Time: 2:42 PM
 */
public class XqyAnnotator implements Annotator, DumbAware
{
	private static final String ALLOWED_XQUERY_VERSIONS = "'1.0-ml', '1.0', '3.0' or '0.9-ml'";

	private static final String [] knownXQueryVersionStrings = {
		"0.9-ml", "1.0", "1.0-ml", "3.0"		// FIXME: This should be defined in a property or something, matching parse rules in effect
	};

	private static final String [] definedFunctionPrefixes = {
		// FIXME: This should be obtained from known namespaces as per XQuery version decl
		// Should not include namespaces like "search", those will be resolved by looking at imports
		"fn:", "xdmp:", "cts:", "map:", "math:", "spell:", "admin:", "prof:", "dbg:", "sec:", "thsr:", "trgr", "err:", "error:"
	};

	private final FunctionDefs functionDefs = FunctionDefs.instance();

	@Override
	public void annotate (@NotNull PsiElement element, @NotNull AnnotationHolder holder)
	{
		if (element instanceof XqyXqueryVersionString) {
			if ( ! validXqueryVersion (element)) {
				holder.createErrorAnnotation (element, "Unknown version, should be: " + ALLOWED_XQUERY_VERSIONS);
			} else {
				holder.createInfoAnnotation (element, ALLOWED_XQUERY_VERSIONS);
			}
		}

		if (element instanceof XqyVarDecl) {
			checkForDupeGlobalVars (element, holder);
		}

		if (element instanceof XqyFunctionDecl) {
			checkForDupeFunctions (element, holder);
		}

		PsiReference reference = element.getReference();
		Object resolve = reference == null ? null : reference.resolve();

		// Annotate unknown variables
		if ((element instanceof XqyRefVarName) && (resolve == null)) {
			holder.createErrorAnnotation (element, "Unknown variable '$" + element.getText() + "'");
			return;
		}

		// Annotate function calls with short description
		if ((element instanceof XqyRefFunctionName)) {
			String functionName = ((XqyRefFunctionName) element).getQName().getText();
			FunctionDefs.Function func = functionDefs.getFunction (functionName);

			// FIXME: This should be sensitive to default function namespace
			if (func == null) func = functionDefs.getFunction ("fn:" + functionName);

			if (func != null) {
				holder.createInfoAnnotation (element, func.getSummary());
				holder.createInfoAnnotation (element, func.getFullName () + " " + func.paramListAsString());

				List<FunctionDefs.Parameter> params = func.getParameters();
				XqyExprSingle [] xparams = PsiTreeUtil.getChildrenOfType (element.getParent(), XqyExprSingle.class);
				int xcount = (xparams == null) ? 0 : xparams.length;
				int minParamCount = func.getMinParamCount();

				if ((xcount > params.size()) || (xcount < minParamCount)) {
					holder.createErrorAnnotation (element, "Wrong # params: got " + xcount +  ", expected " +
						((params.size() == minParamCount) ? params.size() : minParamCount + " - " + params.size()) +
						": " + func.paramListAsString());
				}
			}

			// Annotate unknown functions
			if ((resolve == null) && ( ! functionInScope (element))) {
				holder.createErrorAnnotation (element, "Unknown function '" + element.getText () + "'()");
				return;
			}
		}
	}

	// -----------------------------------------------------------

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
			String targetName = getTextOfElement (targetFunctionNameElement);

			if (targetName == null) continue;

			if (funcName.equals (targetName)) {
				if (arrityOf (funcDecl) == arrityOf (element)) {
					holder.createErrorAnnotation (targetFunctionNameElement, "Duplicate function declaration '" + targetName + "()'");
				} else {
					holder.createWeakWarningAnnotation (targetFunctionNameElement, "Another function '" + targetName + "()' exists with different arity");
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
		String name = refFunctionName.getText();	// FIXME, should look at QName

		for (String prefix: definedFunctionPrefixes) {
			if (name.startsWith (prefix)) return true;
		}

		// FIXME: need to check imports, etc.

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

package uk.co.overstory.xquery.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import uk.co.overstory.xquery.psi.XqyRefFunctionName;
import uk.co.overstory.xquery.psi.XqyRefVarName;
import uk.co.overstory.xquery.psi.XqyXqueryVersionString;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/24/12
 * Time: 2:42 PM
 */
public class XqyAnnotator implements Annotator, DumbAware
{
	private static final String ALLOWED_XQUERY_VERSIONS = "'1.0-ml', '1.0' or '0.9-ml'";

	private static final String [] knownXQueryVersionStrings = {
		"0.9-ml", "1.0", "1.0-ml", "3.0", "3.0-ml"		// FIXME: This should be defined in a property or something, matching parse rules in effect
	};

	private static final String [] definedFunctionPrefixes = {
		// FIXME: This should be obtained from known namespaces as per XQuery version decl
		// Should not include namespaces like "search", thos will be resolved by looking at imports
		"fn:", "xdmp:", "cts:", "map:", "math:", "spell:", "admin:", "prof:", "dbg:", "sec:", "thsr:", "trgr", "err:", "error:"
	};


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

		PsiReference reference = element.getReference();
		Object resolve = reference == null ? null : reference.resolve();

		if ((element instanceof XqyRefVarName) && (resolve == null)) {
			holder.createErrorAnnotation (element, "Unknown variable '$" + element.getText () + "'");
			return;
		}

		if ((element instanceof XqyRefFunctionName) && (resolve == null) && ( ! functionInScope (element))) {
			holder.createErrorAnnotation (element, "Unknown function '" + element.getText () + "'()");
			return;
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

		return false;
	}
}

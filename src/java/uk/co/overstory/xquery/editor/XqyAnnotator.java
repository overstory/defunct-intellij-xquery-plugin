package uk.co.overstory.xquery.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import uk.co.overstory.xquery.psi.XqyParamName;
import uk.co.overstory.xquery.psi.XqyRefFunctionName;
import uk.co.overstory.xquery.psi.XqyRefVarName;
import uk.co.overstory.xquery.psi.XqyReference;
import uk.co.overstory.xquery.psi.XqyStringLiteral;
import uk.co.overstory.xquery.psi.XqyVarName;
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
	@Override
	public void annotate (@NotNull PsiElement element, @NotNull AnnotationHolder holder)
	{
//		System.out.println ("XqyAnnotator.annotate called for " + element.toString () + "/" + element.getText ());

		if ((element instanceof XqyXqueryVersionString) && ( ! validXqueryVersion (element))) {
			holder.createErrorAnnotation (element, "invalid XQuery version");
		}

		PsiReference reference = element.getReference();
		Object resolve = reference == null ? null : reference.resolve();

		if ((element instanceof XqyRefVarName) && (resolve == null)) {
			holder.createWarningAnnotation (element, "unresolved variable reference");
			return;
		}

		if ((element instanceof XqyRefFunctionName) && (resolve == null) && ( ! functionInScope (element))) {
			holder.createWarningAnnotation (element, "unresolved function reference");
			return;
		}


		// Need to determine if any references to var/param/functtion
//		if ((element instanceof XqyParamName) && (reference == null)) {
//			holder.createWarningAnnotation (element, "unused parameter");
//			return;
//		}
//
//		if ((element instanceof XqyVarName) && (reference == null)) {
//			holder.createErrorAnnotation (element, "unused variable");
//			return;
//		}

		// ToDo: check types of expressions against declared types of variables
		// ToDo: warn about variable decls masking others in scope
	}

	private static final String [] knownXQueryVersionStrings = {
		"0.9-ml", "1.0", "1.0-ml", "3.0", "3.0-ml"		// FIXME: This should be defined in a property or something, matching parse rules in effect
	};

	private boolean validXqueryVersion (PsiElement xqueryVersionString)
	{
		for (String version: knownXQueryVersionStrings) {
			if (xqueryVersionString.getText().matches ("['\"]" + version + "['\"]")) return true;
		}

		return false;
	}

	private static final String [] definedFunctionPrefixes = {
		"fn:", "xdmp:", "cts:", "prof:", "debug:"		// FIXME: This is incomplete, should be obtained from known namespaces as per XQuery version decl
	};

	private boolean functionInScope (PsiElement refFunctionName)
	{
		String name = refFunctionName.getText ();	// FIXME, should look at QName

		for (String prefix: definedFunctionPrefixes) {
			if (name.startsWith (prefix)) return true;
		}

		return false;
	}
}

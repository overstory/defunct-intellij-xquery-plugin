package uk.co.overstory.xquery.psi;

import com.intellij.psi.PsiFile;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 2:02 AM
 */
public interface XqyFile extends PsiFile
{
	String getXQueryVersion();

	String getDefaultXQueryVersion();

	List<XqyCompositeElement> getDeclarations();

	List<XqyModuleImport> getModuleImports();

	Map<String,String> getNamespaceMappings();

	String getDefaultFunctionNsPrefix();
}

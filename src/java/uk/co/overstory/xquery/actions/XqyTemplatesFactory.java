package uk.co.overstory.xquery.actions;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.JavaTemplateUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import uk.co.overstory.xquery.XqyIcons;
import uk.co.overstory.xquery.bundles.XqyMessageBundle;

import org.jetbrains.annotations.NonNls;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/15/12
 * Time: 8:06 AM
 */
public class XqyTemplatesFactory implements FileTemplateGroupDescriptorFactory
{
	@NonNls
	private static final String XQY_FILE = "XQueryFile.xqy";
	@NonNls
	static final String NAME_TEMPLATE_PROPERTY = "NAME";
 	@NonNls
	static final String LOW_CASE_NAME_TEMPLATE_PROPERTY = "lowCaseName";


	@Override
	public FileTemplateGroupDescriptor getFileTemplatesDescriptor()
	{
System.out.println ("FileTemplateGroupDescriptor called" );
		FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor (XqyMessageBundle.message ("file.template.group.title.xquery"), XqyIcons.FILE);

		group.addTemplate (new FileTemplateDescriptor (XQY_FILE, XqyIcons.FILE));

		return group;
	}

	protected static PsiFile createFileFromTemplate (final PsiDirectory directory,
		String className, @NonNls String templateName, @NonNls String... parameters)
		throws IncorrectOperationException
	{
		String name = StringUtil.trimEnd (className, ".xqy");
		String fileName = name + ".xqy";
		FileTemplate template = FileTemplateManager.getInstance ().getJ2eeTemplate (templateName);
		Properties properties = new Properties (FileTemplateManager.getInstance().getDefaultProperties());
		JavaTemplateUtil.setPackageNameAttribute (properties, directory);
		properties.setProperty (NAME_TEMPLATE_PROPERTY, name);
		properties.setProperty (LOW_CASE_NAME_TEMPLATE_PROPERTY, name.substring(0, 1).toLowerCase() + name.substring(1));

		for (int i = 0; i < parameters.length; i += 2) {
			properties.setProperty (parameters[i], parameters[i + 1]);
		}

		String text;

		try {
			text = template.getText (properties);
		}
		catch (Exception e) {
			throw new RuntimeException ("Unable to load template for " + FileTemplateManager.getInstance().internalTemplateToSubject (templateName), e);
		}

		final PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());
		final PsiFile file = factory.createFileFromText (fileName, text);

		return (PsiFile) directory.add (file);
	}

}

package uk.co.overstory.xquery.actions;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import uk.co.overstory.xquery.XqyIcons;
import uk.co.overstory.xquery.bundles.XqyMessageBundle;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/15/12
 * Time: 8:06 AM
 */
public class XqyTemplatesFactory implements FileTemplateGroupDescriptorFactory
{
	private static final String XQY_FILE = "XQuery.xqy";

	@Override
	public FileTemplateGroupDescriptor getFileTemplatesDescriptor ()
	{
		final FileTemplateGroupDescriptor group =
			new FileTemplateGroupDescriptor (XqyMessageBundle.message ("file.template.group.title.clojure"), XqyIcons.FILE);
		group.addTemplate(new FileTemplateDescriptor (XQY_FILE, XqyIcons.FILE));

		return group;
	}


}

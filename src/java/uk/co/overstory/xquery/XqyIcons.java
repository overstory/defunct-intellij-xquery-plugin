package uk.co.overstory.xquery;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 6:52 PM
 */
@SuppressWarnings("ConstantDeclaredInInterface")
public interface XqyIcons
{
	// ToDo: make better icon images
	Icon FILE = IconLoader.getIcon ("/icons/file.png");
	Icon VARIABLE = IconLoader.getIcon ("/icons/variable.png");
	Icon FUNCTION = IconLoader.getIcon ("/icons/function.png");
	Icon PARAM = IconLoader.getIcon ("/icons/param.png");
}

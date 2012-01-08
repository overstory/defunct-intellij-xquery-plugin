package uk.co.overstory.xquery.psi;

import com.intellij.psi.tree.IElementType;
import uk.co.overstory.xquery.XqyLanguage;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 1:23 AM
 */
public class XqyCompositeElementType extends IElementType
{
	public XqyCompositeElementType (String debug)
	{
		super (debug, XqyLanguage.INSTANCE);
	}
}

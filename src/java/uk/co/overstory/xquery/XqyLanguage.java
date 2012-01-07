package uk.co.overstory.xquery;

import com.intellij.lang.Language;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/6/12
 * Time: 12:14 PM
 */
public class XqyLanguage extends Language
{
	public static final XqyLanguage INSTANCE = new XqyLanguage();

	protected XqyLanguage ()
	{
		super ("XQY");
	}

	@Override
	public String getDisplayName ()
	{
		return "XQuery";
	}
}

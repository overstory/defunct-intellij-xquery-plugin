package uk.co.overstory.xquery;

import com.intellij.openapi.fileTypes.LanguageFileType;

import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 6:47 PM
 */
public class XqyFileType extends LanguageFileType
{
	public static final XqyFileType INSTANCE = new XqyFileType();

	protected XqyFileType()
	{
		super (XqyLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public String getName ()
	{
		return "XQuery";
	}

	@NotNull
	@Override
	public String getDescription ()
	{
		return "XQuery Language Plugin";
	}

	@NotNull
	@Override
	public String getDefaultExtension ()
	{
		return "xqy";
	}

	@Override
	public Icon getIcon ()
	{
		return XqyIcons.FILE;
	}
}

package uk.co.overstory.xquery.psi;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/6/12
 * Time: 12:20 PM
 */
public class XqyTokenType extends IElementType
{
	public XqyTokenType (@NotNull @NonNls String debugName, @Nullable Language language)
	{
		super (debugName, language);
	}

	public XqyTokenType (String debugName, Language language, boolean register)
	{
		super (debugName, language, register);
	}
}

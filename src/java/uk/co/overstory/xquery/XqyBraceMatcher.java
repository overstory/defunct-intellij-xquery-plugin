package uk.co.overstory.xquery;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import uk.co.overstory.xquery.psi.XqyTypes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/10/12
 * Time: 5:27 PM
 */
public class XqyBraceMatcher implements PairedBraceMatcher
{
	private static final BracePair[] PAIRS = new BracePair[]{
		new BracePair(XqyTypes.XQY_LEFT_BRACE, XqyTypes.XQY_RIGHT_BRACE, false),
		new BracePair(XqyTypes.XQY_LEFT_BRACKET, XqyTypes.XQY_RIGHT_BRACKET, false),
		new BracePair(XqyTypes.XQY_LEFT_PAREN, XqyTypes.XQY_RIGHT_PAREN, false),
		new BracePair(XqyTypes.XQY_LT, XqyTypes.XQY_GT, false),
		new BracePair(XqyTypes.XQY_LT, XqyTypes.XQY_EMPTY_TAG_END, false),
		new BracePair(XqyTypes.XQY_END_TAG_START, XqyTypes.XQY_GT, false),
		new BracePair(XqyTypes.XQY_CDATA_START, XqyTypes.XQY_CDATA_END, false),
		new BracePair(XqyTypes.XQY_PI_START, XqyTypes.XQY_PI_END, false),
		new BracePair(XqyTypes.XQY_PRAGMA_START, XqyTypes.XQY_PRAGMA_END, false),
		new BracePair(XqyTypes.XQY_XML_COMMENT_START, XqyTypes.XQY_XML_COMMENT_END, false),
	};

	@Override
	public BracePair[] getPairs ()
	{
		return PAIRS;
	}

	@Override
	public boolean isPairedBracesAllowedBeforeType (@NotNull IElementType lbraceType, @Nullable IElementType contextType)
	{
		return true;
	}

	@Override
	public int getCodeConstructStart (PsiFile file, int openingBraceOffset)
	{
		return openingBraceOffset;
	}
}

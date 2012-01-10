package uk.co.overstory.xquery;

import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;
import uk.co.overstory.xquery.psi.XqyTypes;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/10/12
 * Time: 5:34 PM
 */
public class XqyCommenter implements CodeDocumentationAwareCommenter
{
	@Override
	public IElementType getLineCommentTokenType ()
	{
		return null;
	}

	@Override
	public IElementType getBlockCommentTokenType ()
	{
		return XqyTypes.XQY_COMMENT;
	}

	@Override
	public IElementType getDocumentationCommentTokenType ()
	{
		return XqyTypes.XQY_COMMENT;	// FIXME: differentiate this for XQDoc-style comments?
	}

	@Override
	public String getDocumentationCommentPrefix ()
	{
		return "(:: ";
	}

	@Override
	public String getDocumentationCommentLinePrefix ()
	{
		return null;
	}

	@Override
	public String getDocumentationCommentSuffix ()
	{
		return " :)";
	}

	@Override
	public boolean isDocumentationComment (PsiComment element)
	{
		return false;
	}

	@Override
	public String getLineCommentPrefix ()
	{
		return null;
	}

	@Override
	public String getBlockCommentPrefix ()
	{
		return "(: ";
	}

	@Override
	public String getBlockCommentSuffix ()
	{
		return " :)";
	}

	@Override
	public String getCommentedBlockCommentPrefix ()
	{
		return null;
	}

	@Override
	public String getCommentedBlockCommentSuffix ()
	{
		return null;
	}
}

package uk.co.overstory.xquery.psi;

import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 1:28 AM
 */
public interface XqyParenthesized extends XqyExpr
{
	@Nullable
	public XqyExpr getExpr();
}

package uk.co.overstory.xquery.test;

import com.intellij.psi.tree.IElementType;
import uk.co.overstory.xquery.parser.XqyLexer;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/10/12
 * Time: 2:26 PM
 */
public class TestLexer
{
	private static final String XQUERY=
		"xquery version \"1.0-ml\";\n" +
		"\n" +
		"(: This is a test :)\n" +
		"\n" +
		"declare variable $hello as xs:string+ := ('Hello world');\n" +
		"declare variable $good-bye := <hello>goodbye</hello>;\n" +
		"declare variable $fare_well* := 10.0e+5;\n" +
		"\n" +
		"($hello, $good-bye, $fare_well)[1]";


	public static void main (String[] args)
	{
		XqyLexer lexer = new XqyLexer ();
		IElementType tokenType = null;

		lexer.start (XQUERY);

		while ((tokenType = lexer.getTokenType()) != null)  {
			String token = lexer.getTokenText ();

			System.out.println ("-->" + token + "<-- " + tokenType);
			lexer.advance ();
		}

	}
}

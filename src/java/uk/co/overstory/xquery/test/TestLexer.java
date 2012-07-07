package uk.co.overstory.xquery.test;

import com.intellij.psi.tree.IElementType;
import uk.co.overstory.xquery.parser.XqyLexer;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/10/12
 * Time: 2:26 PM
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class TestLexer
{
	private static final String TEST =
		"declare function get-document-materials-for-user (\n" +
			"\t$zz:empty as node(),\n" +
			"\t$zz:empty as node(),\n" +
			"\t$zz:node as node(),\n" +
			"\t$doc as document-node(),\n" +
			"\t$materials-json as xs:string\n" +
			") as element(Id)*\n" +
			"{\n" +
			"\ttry {\n" +
			"\t\tlet $foo := $doc/namespace::*\n" +
			"\t\tlet $doc-date as xs:date := $doc//meta:Info/meta:Date\n" +
			"\t\tlet $materials-maps as map:map* := map-materials($materials-json)\n" +
			"\t\tlet $candidate-ids as xs:string* :=\n" +
			"\t\t\tfor $map in $materials-maps\n" +
			"\t\t\tlet $from := map:get ($map, \"from\")\n" +
			"\t\t\tlet $from-date := if ($from castable as xs:date) then xs:date ($from) else xs:date (\"0001-01-01\")\n" +
			"\t\t\tlet $to := map:get ($map, \"to\")\n" +
			"\t\t\tlet $to-date := if ($to castable as xs:date) then xs:date ($to) else xs:date (\"9999-12-31\")\n" +
			"\t\t\twhere (($doc-date ge $from-date) and ($doc-date le $to-date))\n" +
			"\t\t\treturn map:get ($map, \"id\")\n" +
			"\n" +
			"\t\tfor $id in get-document-material-ids-for-doc ($doc, $candidate-ids)\n" +
			"\t\treturn element { \"Id\" } { $id/node() }\n" +
			"\t} catch ($e) {\n" +
			"\t\t()   (: Probably here because doc doesn't have a meta:Date element :)\n" +
			"\t}\n" +
			"};\n";

	private static final String XQUERY=
		"xquery version '1.0-ml';\n" +
			"123\n" +
			"123.\n" +
			".123\n" +
			"123.45\n" +
			"1.0e+5\n" +
			"            <options xmlns=\"xdmp:eval\">\n" +
			"        \t  <database>{xdmp:database(\"CitationTracker\")}</database>\n" +
			"        \t</options>\n" +
			"<foo>bar</foo>\n" +
			"<foo2>{$blah}</foo2>" +
			"\"multi\n  line\n  string\"" +
			"\n" +
			"(: embedded (parens) :)\n" +
			"\n" +
			"(# myqname this is a pragma #)\n" +
			"" +
			"<?target This is a processing instruction ?>\n" +
			"" +
			"(: This is a comment:with a semicolon :)\n" +
			"(: This is a comment :)\n" +
			"\n" +
			"declare variable $data := <![CDATA[ foo bar baz ]]>;\n" +
			"\n" +
			"declare variable $hello as xs:string := ('Hello world');\n" +
			"declare variable $good-bye := <hello>goodbye</hello>;\n" +
			"declare variable $toodles := <ta-ta/>;\n" +
			"declare variable $fare_well := 1.0e+5;\n" +
			"declare variable $pi := <?foo bar?>;\n" +
			"declare variable $xml-with-comment :=\n" +
			"     <foo>\n" +
			"        <!-- xml comment -->\n" +
			"        <? target blah blah ?>\n" +
			"        Some content\n" +
			"     </foo>\n" +
			"\n" +
			"declare variable $foo := 'foobar';\n\n" +
			"declare function myfunc ($arg1 as xs:string, $arg2 as element(foo)*) as empty-sequence()" +
			"{" +
			"	xdmp:current-dateTime()" +
			"};" +
			"\n" +
			"($hello, $good-bye, $fare_well)[1]\n";

	private TestLexer ()
	{
	}


	public static void main (String[] args)
	{
		XqyLexer lexer = new XqyLexer ();
		IElementType tokenType = null;

		lexer.start (TEST);

		while ((tokenType = lexer.getTokenType()) != null)  {
			String token = lexer.getTokenText();
			CharSequence seq = lexer.getTokenSequence();

			System.out.println ("-->" + seq + "<-- " + tokenType + " (start: " + lexer.getTokenStart() + ", end: " + lexer.getTokenEnd() + ", len: " + seq.length () + ")");

			lexer.advance();
		}

	}
}

package uk.co.overstory.xquery.editor;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import gnu.trove.THashMap;
import uk.co.overstory.xquery.XqyIcons;

import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

import java.util.Map;

import static uk.co.overstory.xquery.editor.XqySyntaxHighlighter.*;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/9/12
 * Time: 11:34 AM
 */
public class XqyColorSettingsPage implements ColorSettingsPage
{
	private static final AttributesDescriptor[] ATTRS;

	static {
		ATTRS = new AttributesDescriptor[] {
			new AttributesDescriptor("illegal character", ILLEGAL),
			new AttributesDescriptor("comment", COMMENT),
			new AttributesDescriptor("var decalaration", VAR_DECL),
			new AttributesDescriptor("function decalaration", FUNCTION_DECL),
			new AttributesDescriptor("string", STRING),
			new AttributesDescriptor("number", NUMBER),
			new AttributesDescriptor("keyword", KEYWORD),
			new AttributesDescriptor("assignment operator", OP_BIND),
			new AttributesDescriptor("dolar sign", DOLLAR_SIGN),
			new AttributesDescriptor("parenthesis", PARENTHS),
			new AttributesDescriptor("braces", BRACES),
			new AttributesDescriptor("brackets", BRACKETS),
			new AttributesDescriptor("angles", ANGLES),
			new AttributesDescriptor("comma", COMMA),
			new AttributesDescriptor("dot", DOT),
			new AttributesDescriptor("semicolon", SEMICOLON),
			new AttributesDescriptor("cdata", CDATA),
		};
	}

	private static final String XQUERY_DEMO_TEXT =
		"xquery version '1.0-ml';\n" +
			"\n" +
			"(: This is a comment :)\n" +
			"\n" +
			"(# this is a pragma #)\n" +
			"\n" +
			"declare namespace snurg=\"http://snurg.com\";\n" +
			"\n" +
			"declare variable $hello as xs:string := ('Hello world');\n" +
			"declare variable $good-bye := <hello>goodbye</hello>;\n" +
			"declare variable $toodles := <ta-ta/>;\n" +
			"declare variable $fare_well := 'toodles';\n" +
			"\n" +
			"declare variable $an-int := 42;\n" +
			"declare variable $a-decimal := 142.37;\n" +
			"declare variable $a-double := 1.0e+5;\n" +
			"declare variable $silly-string as xs:string := 'This string hasn''t a clue';\n" +
			"\n" +
			"declare variable $some-cdata := <![CDATA[ foo bar baz comment ]>;\n" +
			"declare variable $pi := <?foo bar?>;\n" +
			"declare variable $xml-with-comment :=\n" +
			"     <foo>\n" +
			"        <!-- xml comment -->\n" +
			"        Some content\n" +
			"        <an-element>blah</an-element>\n" +
			"        <an-empty-element attr=\"foo\"/>\n" +
			"        <element-with-curlies>this is {{so}} cool<element-with-curlies>\n" +
			"     </foo>\n" +
			"\n" +
			"declare variable $snurg:foo := 'foobar';\n" +
			"\n" +
			"declare function myfunc ($arg1 as xs:integer, $arg2 as element(foo)*)\n" +
			"    as empty-sequence()\n" +
			"{\n" +
			"    fn:current-dateTime()\n" +
			"};\n" +
			"\n" +
			"($hello, $good-bye, $fare_well)[1]";

	@NotNull
	@Override
	public String getDisplayName ()
	{
		return "XQuery";
	}

	@Override
	public Icon getIcon ()
	{
		return XqyIcons.FILE;
	}

	@NotNull
	@Override
	public AttributesDescriptor[] getAttributeDescriptors ()
	{
		return ATTRS;
	}

	@NotNull
	@Override
	public ColorDescriptor[] getColorDescriptors ()
	{
		return ColorDescriptor.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	public SyntaxHighlighter getHighlighter()
	{
		return new XqySyntaxHighlighter();
	}

	@NotNull
	@Override
	public String getDemoText ()
	{
		return XQUERY_DEMO_TEXT;
	}

	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap ()
	{
		return new THashMap<String, TextAttributesKey> ();
	}
}

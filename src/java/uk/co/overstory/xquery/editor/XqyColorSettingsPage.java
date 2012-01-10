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
			new AttributesDescriptor("string", STRING),
			new AttributesDescriptor("number", NUMBER),
			new AttributesDescriptor("keyword", KEYWORD),
			new AttributesDescriptor("parenthesis", PARENTHS),
			new AttributesDescriptor("braces", BRACES),
			new AttributesDescriptor("brackets", BRACKETS),
			new AttributesDescriptor("angles", ANGLES),
			new AttributesDescriptor("comma", COMMA),
			new AttributesDescriptor("dot", DOT),
			new AttributesDescriptor("semicolon", SEMICOLON),
		};
	}

	private static final String XQUERY_DEMO_TEXT =
		"xquery version \"1.0-ml\";\n" +
			"\n" +
			"(: This is a test :)\n" +
			"\n" +
			"declare variable $hello as xs:string := ('Hello world');\n" +
			"declare variable $good-bye := <hello>goodbye</hello>;\n" +
			"declare variable $fare_well := 1.0e+5;\n" +
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

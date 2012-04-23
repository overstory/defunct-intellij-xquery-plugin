package uk.co.overstory.xquery.completion;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 4/22/12
 * Time: 6:02 PM
 */
public class FunctionDefs
{
	private static final FunctionDefs INSTANCE = new FunctionDefs();

	public static FunctionDefs instance()
	{
		return INSTANCE;
	}

	// ------------------------------------------------------------

	private static final String ML_CATEGORIES_PATH = "completion/functions/marklogic-function-categories.xml";
	private static final String ML_FUNCTIONS_PATH = "completion/functions/marklogic-functions.xml";

	private final SAXParserFactory spf = SAXParserFactory.newInstance();
	private final List<Category> categories = new ArrayList<Category>();
	private final List<Function> functions = new ArrayList<Function>();
	private final Map<String, Function> functionMap = new HashMap<String, Function>();

	// ------------------------------------------------------------

	public List<Category> getCategories()
	{
		return Collections.unmodifiableList (categories);
	}

	public List<Function> getFunctions()
	{
		return Collections.unmodifiableList (functions);
	}

	public Function getFunction (String name)
	{
		return functionMap.get (name);
	}

	public List<Function> getFunctionsForPrefix (String prefix)
	{
		List<Function> list = new ArrayList<Function>();

		for (Function func : functions) {
			if (func.getPrefix ().equals (prefix)) list.add (func);
		}

		return Collections.unmodifiableList (list);
	}

	// ------------------------------------------------------------
	// Private constructor

	private FunctionDefs()
	{
		loadCategories (categories, ML_CATEGORIES_PATH);
		loadFunctions (functions, ML_FUNCTIONS_PATH);

		Map<String, Category> categoryMap = new HashMap<String, Category>();

		for (Category cat : categories) categoryMap.put (cat.getPrefix(), cat);

		for (Function func : functions) {
			if (func.isHidden ()) continue;

			functionMap.put (func.getFullName(), func);

			Category cat = categoryMap.get (func.getPrefix());

			if (cat != null) cat.incrementCount();
		}
	}

	private void loadFunctions (List<Function> functions, String path)
	{
		try {
			InputStream is = getClass().getClassLoader ().getResourceAsStream (path);

			if (is == null) {
				System.out.println ("FunctionDefs.loadFunctions: could not load: " + path);
				return;
			}

			FunctionDefFunctionParser parser = new FunctionDefFunctionParser (functions);
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler (parser);
			xmlReader.parse (new InputSource (is));
			is.close();
		} catch (Exception e) {
			System.out.println ("Problem loading XML: " + e);
			e.printStackTrace ();
		}
	}

	private void loadCategories (List<Category> categories, String path)
	{
		try {
			InputStream is = getClass().getClassLoader ().getResourceAsStream (path);

			if (is == null) {
				System.out.println ("FunctionDefs.loadCategories: could not load: " + path);
				return;
			}

			FunctionDefCategoryParser parser = new FunctionDefCategoryParser (categories);
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler (parser);
			xmlReader.parse (new InputSource (is));
			is.close();
		} catch (Exception e) {
			System.out.println ("Problem loading XML: " + e);
			e.printStackTrace ();
		}
	}

	// ------------------------------------------------------------
	// ------------------------------------------------------------

	public static class Function
	{
		private final List<Parameter> parameters = new ArrayList<Parameter>();
		private final String localName;
		private final String fullName;
		private final String prefix;
		private final boolean hidden;
		private String returnType = null;

		private String summary = "";
		private String example = "";
		private String usage = "";
		private int minParamCount = 0;

		public Function (String prefix, String localName, String fullName, String returnType, boolean hidden)
		{
			this.prefix = prefix;
			this.localName = localName;
			this.fullName = fullName;
			this.hidden = hidden;
			this.returnType = returnType;
		}

		private void addParam (Parameter param)
		{
			parameters.add (param);

			if ( ! param.isOptional()) minParamCount++;
		}

		public String getPrefix()
		{
			return prefix;
		}

		public String getLocalName()
		{
			return localName;
		}

		public String getFullName()
		{
			return fullName;
		}

		public String getReturnType()
		{
			return returnType;
		}

		public boolean isHidden()
		{
			return hidden;
		}

		public String getSummary()
		{
			return summary;
		}

		public String getExample()
		{
			return example;
		}

		public String getUsage()
		{
			return usage;
		}

		public int getMinParamCount()
		{
			return minParamCount;
		}

		public List<Parameter> getParameters()
		{
			return Collections.unmodifiableList (parameters);
		}

		public String paramListAsString()
		{
			StringBuilder sb = new StringBuilder("(");

			for (Parameter param : getParameters ()) {
				if (sb.length() > 1) sb.append (", ");

				if (param.isOptional()) sb.append ("[");

				sb.append ("$").append (param.getName());

				String type = param.getType();

				if ((type != null) && (type.length() > 0)) {
					sb.append (" as ").append (type);
				}

				if (param.isOptional()) sb.append ("]");
			}

			sb.append (")");

			return sb.toString();
		}
	}

	public static class Parameter
	{
		private final String name;
		private final String type;
		private final boolean optional;
		private String description = "";

		private Parameter (String name, String type, boolean optional)
		{
			this.name = name;
			this.type = type;
			this.optional = optional;
		}

		public String getName ()
		{
			return name;
		}

		public String getType ()
		{
			return type;
		}

		public boolean isOptional ()
		{
			return optional;
		}

		public String getDescription ()
		{
			return description;
		}
	}

	private static class FunctionDefFunctionParser extends DefaultHandler
	{
		private final StringBuilder text = new StringBuilder();
		private final List<Function> functions;

		private Function func = null;
		private Parameter param = null;

		private FunctionDefFunctionParser (List<Function> functions)
		{
			this.functions = functions;
		}

		@Override
		public void startElement (String namespaceName, String name, String qname, Attributes attributes) throws SAXException
		{
			if (qname.equals ("function")) {
				text.setLength (0);
				func = new Function (attributes.getValue ("lib"),
					attributes.getValue ("name"),
					attributes.getValue ("fullname"),
					"item()*",
					Boolean.valueOf (attributes.getValue ("hidden")));
			}

			if (qname.equals ("param")) {
				text.setLength (0);
				param = new Parameter (attributes.getValue ("name"),
					attributes.getValue ("type"),
					Boolean.valueOf (attributes.getValue ("optional")));
			}

			if (qname.equals ("return") || qname.equals ("summary") || qname.equals ("example") || qname.equals ("usage"))
			{
				text.setLength (0);
				return;
			}

			text.append (" ");	// replace unrecognized tags with space, for now.
		}

		@Override
		public void endElement (String namespaceName, String name, String qname) throws SAXException
		{
			if (qname.equals ("function")) functions.add (func);
			if (qname.equals ("param")) func.addParam (param);

			if (qname.equals ("return")) func.returnType = text.toString();
			if (qname.equals ("summary")) func.summary = text.toString();
			if (qname.equals ("example")) func.example = text.toString();
			if (qname.equals ("usage")) func.usage = text.toString();

			text.append (" ");	// replace end tags with space for now, those above don't matter
		}

		@Override
		public void characters (char[] chars, int start, int length) throws SAXException
		{
			text.append (chars, start, length);
		}
	}

	// ------------------------------------------------------------
	// ------------------------------------------------------------

	public static class Category
	{
		private final String prefix;
		private final String desc;
		private int functionCount = 0;

		public Category (String prefix, String desc)
		{
			this.prefix = prefix;
			this.desc = desc;
		}

		public String getPrefix()
		{
			return prefix;
		}

		public String getDesc()
		{
			return desc;
		}

		public int getFunctionCount()
		{
			return functionCount;
		}

		private void incrementCount()
		{
			functionCount++;
		}
	}

	// ------------------------------------------------------------

	private static class FunctionDefCategoryParser extends DefaultHandler
	{
		private final List<Category> categories;
		private String prefix = null;
		private String desc = null;

		private FunctionDefCategoryParser (List<Category> categories)
		{
			this.categories = categories;
		}

		@Override
		public void startElement (String namespaceName, String localName, String qname, Attributes attributes) throws SAXException
		{
			if (qname.equals ("category")) {
				prefix = attributes.getValue ("prefix");
				desc = attributes.getValue ("desc");
			}
		}

		@Override
		public void endElement (String namespaceName, String localName, String qname) throws SAXException
		{
			if (qname.equals ("category")) {
				categories.add (new Category (prefix, desc));
			}
		}
	}
}

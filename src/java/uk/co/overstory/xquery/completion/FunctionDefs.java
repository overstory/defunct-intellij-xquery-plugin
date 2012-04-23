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

	// ------------------------------------------------------------

	public List<Category> getCategories()
	{
		return Collections.unmodifiableList (categories);
	}

	public List<Function> getFunctions()
	{
		return Collections.unmodifiableList (functions);
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

	private FunctionDefs()
	{
		loadCategories (categories, ML_CATEGORIES_PATH);
		loadFunctions (functions, ML_FUNCTIONS_PATH);

		Map<String, Category> categoryMap = new HashMap<String, Category>();

		for (Category cat : categories) categoryMap.put (cat.getPrefix(), cat);

		for (Function func : functions) {
			Category cat = categoryMap.get (func.getPrefix ());

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
		private String localName = null;
		private String fullName = null;
		private String prefix = null;
		private String returnType = null;
		private boolean hidden = false;

		public Function (String prefix, String localName, String fullName, String returnType, boolean hidden)
		{
			this.prefix = prefix;
			this.localName = localName;
			this.fullName = fullName;
			this.hidden = hidden;
			this.returnType = returnType;
		}

		public String getPrefix ()
		{
			return prefix;
		}

		public String getLocalName ()
		{
			return localName;
		}

		public String getFullName ()
		{
			return fullName;
		}

		public String getReturnType ()
		{
			return returnType;
		}

		public boolean isHidden ()
		{
			return hidden;
		}
	}

	private static class FunctionDefFunctionParser extends DefaultHandler
	{
		private final List<Function> functions;
		private String prefix = null;
		private String localName = null;
		private String fullName = null;
		private String returnType = null;
		private boolean hidden = false;
		private String text = null;

		private FunctionDefFunctionParser (List<Function> functions)
		{
			this.functions = functions;
		}

		@Override
		public void startElement (String namespaceName, String name, String qname, Attributes attributes) throws SAXException
		{
			if (qname.equals ("function")) {
				prefix = attributes.getValue ("lib");
				localName = attributes.getValue ("name");
				fullName = attributes.getValue ("fullname");
				hidden = Boolean.valueOf (attributes.getValue ("hidden"));
				returnType = "item()*";
			}
		}

		@Override
		public void endElement (String namespaceName, String name, String qname) throws SAXException
		{
			if (qname.equals ("function")) {
				functions.add (new Function (prefix, localName, fullName, returnType, hidden));
			}

			if (qname.equals ("return")) {
				returnType = text;
			}
		}

		@Override
		public void characters (char[] chars, int start, int length) throws SAXException
		{
			text = new String (chars, start, length);
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

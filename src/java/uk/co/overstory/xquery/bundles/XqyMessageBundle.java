package uk.co.overstory.xquery.bundles;

import com.intellij.CommonBundle;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/15/12
 * Time: 8:23 AM
 */
public class XqyMessageBundle
{
	private static Reference<ResourceBundle> ourBundle;

	@NonNls
	private static final String BUNDLE = "uk.co.overstory.xquery.bundles.XqyMessageBundle";

	private XqyMessageBundle ()
	{
	}

	public static String message (@PropertyKey(resourceBundle = BUNDLE) String key, Object... params)
	{
		return CommonBundle.message (getBundle (), key, params);
	}

	private static ResourceBundle getBundle()
	{
		ResourceBundle bundle = null;

		if (ourBundle != null) bundle = ourBundle.get();

		if (bundle == null) {
			bundle = ResourceBundle.getBundle(BUNDLE);
			ourBundle = new SoftReference<ResourceBundle> (bundle);
		}
		return bundle;
	}
}

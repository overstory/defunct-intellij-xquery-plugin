package uk.co.overstory.xquery.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import uk.co.overstory.xquery.XqyFileType;
import uk.co.overstory.xquery.XqyLanguage;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/10/12
 * Time: 7:37 PM
 */
public class XqyFileImpl  extends PsiFileBase implements PsiFile
{
	public XqyFileImpl (FileViewProvider fileViewProvider)
	{
		super (fileViewProvider, XqyLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public FileType getFileType ()
	{
		return XqyFileType.INSTANCE;
	}

	@Override
	public String toString ()
	{
		return "XqyFile:" + getName();
	}
}

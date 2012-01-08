package uk.co.overstory.xquery;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/8/12
 * Time: 6:45 PM
 */
public class XqyFileTypeFactory extends FileTypeFactory
{
	@Override
	public void createFileTypes (@NotNull FileTypeConsumer fileTypeConsumer)
	{
		fileTypeConsumer.consume(XqyFileType.INSTANCE);
	}
}

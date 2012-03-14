package uk.co.overstory.xquery;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import uk.co.overstory.xquery.psi.impl.XqyFileImpl;

import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 3/12/12
 * Time: 5:49 PM
 */
public class XqyCompletionContributor extends CompletionContributor
{
	public XqyCompletionContributor()
	{
		extend (CompletionType.BASIC,
			psiElement().inFile (PlatformPatterns.instanceOf (XqyFileImpl.class)),
			new CompletionProvider<CompletionParameters> ()
			{
				@Override
				protected void addCompletions (@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result)
				{
					// FIXME: auto-generated
				}
			});
	}
}

package uk.co.overstory.xquery;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import uk.co.overstory.xquery.parser.XQueryParser;
import uk.co.overstory.xquery.parser.XqyLexer;
import uk.co.overstory.xquery.psi.XqyTokenType;
import uk.co.overstory.xquery.psi.XqyTypes;
import uk.co.overstory.xquery.psi.impl.XqyFileImpl;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/6/12
 * Time: 12:11 PM
 */
public class XqyParserDefinition implements ParserDefinition
{
	public static final IFileElementType XQY_FILE_ELEMENT_TYPE = new IFileElementType("XQY_FILE", XqyLanguage.INSTANCE);
	public static final TokenSet WS = TokenSet.create (TokenType.WHITE_SPACE, XqyTypes.XQY_S);
	public static final IElementType XQY_BLOCK_COMMENT = new XqyTokenType ("XQY_BLOCK_COMMENT", XqyLanguage.INSTANCE);
	public static final TokenSet COMMENTS = TokenSet.create(XQY_BLOCK_COMMENT, XqyTypes.XQY_COMMENT, XqyTypes.XQY_COMMENT_START, XqyTypes.XQY_COMMENT_CONTENTS, XqyTypes.XQY_COMMENT_END);
	public static final TokenSet LITERALS = TokenSet.create(XqyTypes.XQY_LITERAL, XqyTypes.XQY_STRING);

	@NotNull
	public Lexer createLexer (Project project)
	{
		return new XqyLexer();
	}

	public PsiParser createParser (Project project)
	{
		return new XQueryParser ();
	}

	public IFileElementType getFileNodeType()
	{
		return XQY_FILE_ELEMENT_TYPE;
	}

	@NotNull
	public TokenSet getWhitespaceTokens()
	{
		return WS;
	}

	@NotNull
	public TokenSet getCommentTokens()
	{
		return COMMENTS;
	}

	@NotNull
	public TokenSet getStringLiteralElements()
	{
		return LITERALS;
	}

	@NotNull
	public PsiElement createElement (ASTNode astNode)
	{
		return XqyTypes.Factory.createElement (astNode);
	}

	public PsiFile createFile (FileViewProvider fileViewProvider)
	{
		return new XqyFileImpl (fileViewProvider);
	}

	public SpaceRequirements spaceExistanceTypeBetweenTokens (ASTNode astNode, ASTNode astNode1)
	{
		return SpaceRequirements.MAY;
	}
}

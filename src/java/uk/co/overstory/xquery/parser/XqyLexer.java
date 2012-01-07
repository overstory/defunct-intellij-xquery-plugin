/*
 * Copyright 2011-2011 Gregory Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.overstory.xquery.parser;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.lexer.LookAheadLexer;
import com.intellij.lexer.MergingLexerAdapter;
import uk.co.overstory.xquery.XqyParserDefinition;

/**
 * Created by IntelliJ IDEA.
 * User: gregory
 * Date: 13.07.11
 * Time: 22:50
 */
public class XqyLexer extends LookAheadLexer
{

	public XqyLexer() {
		super(new MergingLexerAdapter (new FlexAdapter (new _XqyLexer()), XqyParserDefinition.COMMENTS));
	}

	@Override
	protected void lookAhead(Lexer baseLexer) {
		super.lookAhead(baseLexer);
	}
}

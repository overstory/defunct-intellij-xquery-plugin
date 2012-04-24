package uk.co.overstory.xquery.completion.handlers;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

/**
* Created by IntelliJ IDEA.
* User: ron
* Date: 4/24/12
* Time: 9:33 PM
*/
public class FuncRefInsertHandler implements InsertHandler<LookupElement>
{
	private final boolean doPopup;

	public FuncRefInsertHandler (boolean doPopup)
	{
		this.doPopup = doPopup;
	}

	@Override
	public void handleInsert (InsertionContext context, LookupElement lookupElement)
	{
		Document document = context.getDocument();
		Editor editor = context.getEditor();
		CaretModel caretModel = editor.getCaretModel();
		CharSequence cs = document.getText ();
		int offset = caretModel.getOffset();

		for (int i = offset; i < cs.length(); i++) {
			if (cs.charAt (i) == '(') return;
			if ( ! Character.isWhitespace (cs.charAt (i))) break;
		}

		document.insertString (offset, "()");
		caretModel.moveToOffset (offset);

		if (doPopup) {
			AutoPopupController.getInstance (context.getProject()).autoPopupMemberLookup (editor, null);
		}
	}
}

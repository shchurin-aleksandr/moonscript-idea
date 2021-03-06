/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

package com.eightbitmage.moonscript.lang;

import com.eightbitmage.moonscript.lang.psi.MoonPsiFile;
import com.eightbitmage.moonscript.lang.psi.statements.MoonBlock;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class MoonEnterHandlerDelegate implements EnterHandlerDelegate {
    @Override
    public Result preprocessEnter(PsiFile file, Editor editor, Ref<Integer> caretOffsetRef, Ref<Integer> caretAdvance,
                                  DataContext dataContext, EditorActionHandler originalHandler) {

        if (! (file instanceof MoonPsiFile))
            return Result.Continue;

        Document document = editor.getDocument();
        CharSequence text = document.getCharsSequence();
        int caretOffset = caretOffsetRef.get();

        PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);

        PsiElement e = file.findElementAt(caretOffset);
        PsiElement e1 = file.findElementAt(caretOffset - 1);

        // consume any whitespace until end of line looking for a token
        if (e != null) while (e instanceof PsiWhiteSpace || e instanceof MoonBlock) {
            if (e.getText().indexOf('\n') >= 0) break;

            e = e.getNextSibling();
        }
        // if e points to an end token, then insert a linefeed and indent the 'end' correctly
        if (e != null && e.getText().equals("end")) {
            if (text.charAt(caretOffset-1) == '\n')
                editor.getCaretModel().moveToOffset(caretOffset-1);
            try {
                CodeStyleManager.getInstance(file.getProject()).
                        adjustLineIndent(file, caretOffset);
            } catch (IncorrectOperationException ignored) {
            }
            PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);
            //originalHandler.execute(editor, dataContext);

            caretOffsetRef.set(editor.getCaretModel().getOffset());
            return Result.DefaultForceIndent;
        }
        if (CodeInsightSettings.getInstance().SMART_INDENT_ON_ENTER) {
            if (e1 != null) {
                if (e1.getText().equals("end") || e1.getText().equals("else") || e1.getText().equals("elseif") ||
                    e1.getText().equals("}") || e1.getText().equals("until")) {
                    PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);
                    try {
                        CodeStyleManager.getInstance(file.getProject()).
                                adjustLineIndent(file, caretOffset - e1.getTextLength());

                        originalHandler.execute(editor, dataContext);
                    } catch (IncorrectOperationException ignored) {
                    }

                    caretOffsetRef.set(editor.getCaretModel().getOffset());
                    return Result.Stop;
                }
            }
        }
        return Result.Continue;
    }

    @Override
    public Result postProcessEnter(@NotNull PsiFile psiFile, @NotNull Editor editor, @NotNull DataContext dataContext) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
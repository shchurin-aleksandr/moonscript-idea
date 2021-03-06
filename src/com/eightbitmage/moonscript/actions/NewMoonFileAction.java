/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.eightbitmage.moonscript.actions;

import com.eightbitmage.moonscript.MoonBundle;
import com.eightbitmage.moonscript.MoonIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 17.04.2009
 * Time: 20:19:17
 *
 * @author Joachim Ansorg
 */
public class NewMoonFileAction extends NewMoonActionBase {
    private static final Logger log = Logger.getInstance("#NewActionBase");

    public NewMoonFileAction() {
        super(MoonBundle.message("newfile.menu.action.text"),
                MoonBundle.message("newfile.menu.action.description"),
                MoonIcons.MOON_ICON);
    }


    protected String getDialogPrompt() {
        return MoonBundle.message("newfile.dialog.prompt");
    }

    protected String getDialogTitle() {
        return MoonBundle.message("newfile.dialog.title");
    }

    protected String getCommandName() {
        return MoonBundle.message("newfile.command.name");
    }

    protected String getActionName(PsiDirectory directory, String newName) {
        return MoonBundle.message("newfile.menu.action.text");
    }

    @NotNull
    protected PsiElement[] doCreate(String newName, PsiDirectory directory) {
        PsiFile file = createFileFromTemplate(directory, newName, MoonTemplatesFactory.NEW_SCRIPT_FILE_NAME);
        PsiElement child = file.getLastChild();
        return child != null ? new PsiElement[]{file, child} : new PsiElement[]{file};
    }
}
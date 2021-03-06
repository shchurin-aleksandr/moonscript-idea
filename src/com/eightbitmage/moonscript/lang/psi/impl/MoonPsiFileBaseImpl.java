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

package com.eightbitmage.moonscript.lang.psi.impl;

import com.eightbitmage.moonscript.lang.psi.MoonPsiFileBase;
import com.eightbitmage.moonscript.lang.psi.statements.MoonFunctionDefinitionStatement;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public abstract class MoonPsiFileBaseImpl extends PsiFileBase implements MoonPsiFileBase {

    private MoonFunctionDefinitionStatement[] funcs_cache;

    protected MoonPsiFileBaseImpl(FileViewProvider viewProvider, @NotNull Language language) {
        super(viewProvider, language);
    }

}

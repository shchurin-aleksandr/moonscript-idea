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

package com.eightbitmage.moonscript.lang.psi.impl.symbols;

import com.eightbitmage.moonscript.lang.psi.symbols.MoonIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.MoonSymbol;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/4/11
 * Time: 10:36 PM
 */
public class MoonWrapperReferenceElementImpl extends MoonReferenceElementImpl {
    @Override
    public boolean isSameKind(MoonSymbol symbol) {
        assert false;
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public MoonWrapperReferenceElementImpl(ASTNode node) {
        super(node);
    }

    public PsiElement getElement() {
        return findChildByClass(MoonIdentifier.class);
    }

    public PsiReference getReference() {
        return this;
    }

    @Override
    public String getName() {
        return ((PsiNamedElement)getElement()).getName();
    }

    @Override
    public String toString() {
        return "Reference: " + getName();
    }

}

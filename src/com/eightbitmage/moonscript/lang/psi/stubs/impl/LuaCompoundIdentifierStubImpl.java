/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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

package com.eightbitmage.moonscript.lang.psi.stubs.impl;

import com.eightbitmage.moonscript.lang.parser.LuaElementTypes;
import com.eightbitmage.moonscript.lang.psi.stubs.api.LuaCompoundIdentifierStub;
import com.eightbitmage.moonscript.lang.psi.symbols.LuaCompoundIdentifier;
import com.eightbitmage.moonscript.lang.psi.symbols.LuaGlobal;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/21/11
 * Time: 7:33 PM
 */
public class LuaCompoundIdentifierStubImpl extends StubBase<LuaCompoundIdentifier>
        implements LuaCompoundIdentifierStub {

    private final StringRef myName;
    private final boolean isGlobalDeclaration;

    public LuaCompoundIdentifierStubImpl(LuaCompoundIdentifier e) {
        this(null, e);
    }

    public LuaCompoundIdentifierStubImpl(StubElement parent, StringRef name, boolean isDeclaration) {
        super(parent, LuaElementTypes.GETTABLE);
        myName = name;
        this.isGlobalDeclaration = isDeclaration;
    }

    public LuaCompoundIdentifierStubImpl(StubElement parentStub, LuaCompoundIdentifier psi) {
        super(parentStub, LuaElementTypes.GETTABLE);

        myName = StringRef.fromString(psi.getName());
        isGlobalDeclaration = psi.isCompoundDeclaration() && psi.getScopeIdentifier() instanceof LuaGlobal;
    }

    @Override
    public String getName() {
        return myName.getString();  
    }

    @Override
    public boolean isGlobalDeclaration() {
        return isGlobalDeclaration;
    }
}

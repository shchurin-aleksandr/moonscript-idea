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
package com.eightbitmage.moonscript.lang.structure.impl;

import com.eightbitmage.moonscript.lang.psi.LuaFunctionDefinition;
import com.eightbitmage.moonscript.lang.psi.LuaPsiFile;
import com.eightbitmage.moonscript.lang.structure.LuaStructureViewTreeElement;
import com.eightbitmage.moonscript.lang.structure.itemsPresentations.impl.LuaFileItemPresentation;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;

import java.util.ArrayList;
import java.util.List;


/**
 * User: Dmitry.Krasilschikov
 * Date: 30.10.2007
 */
public class LuaFileStructureViewElement extends LuaStructureViewTreeElement {
  public LuaFileStructureViewElement(LuaPsiFile luaFileBase) {
    super(luaFileBase);
  }

  public ItemPresentation getPresentation() {
    return new LuaFileItemPresentation((LuaPsiFile) myElement);
  }

  public TreeElement[] getChildren() {
    List<LuaStructureViewTreeElement> children = new ArrayList<LuaStructureViewTreeElement>();

    LuaPsiFile file = (LuaPsiFile) getValue();

    if (file == null)
        return new LuaStructureViewTreeElement[0];

    for(LuaFunctionDefinition st : file.getFunctionDefs()) {
           children.add(new LuaFunctionStructureViewElement(st));
    }
      
    return children.toArray(new LuaStructureViewTreeElement[children.size()]);
  }
}

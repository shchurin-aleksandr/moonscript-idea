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

package com.eightbitmage.moonscript.lang.moondoc.psi.impl;

import com.eightbitmage.moonscript.lang.moondoc.lexer.MoonDocTokenTypes;
import com.eightbitmage.moonscript.lang.moondoc.parser.MoonDocElementTypes;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocComment;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocCommentOwner;
import com.eightbitmage.moonscript.lang.moondoc.psi.api.MoonDocTag;
import com.eightbitmage.moonscript.lang.psi.MoonPsiElement;
import com.eightbitmage.moonscript.lang.psi.util.MoonPsiUtils;
import com.eightbitmage.moonscript.lang.psi.visitor.MoonElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.text.CharArrayUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author ilyas
 */
public class MoonDocCommentImpl extends LazyParseablePsiElement implements MoonDocElementTypes, MoonDocComment {
  public MoonDocCommentImpl(CharSequence text) {
    super(LUADOC_COMMENT, text);
  }

  public String toString() {
      MoonDocCommentOwner owner = getOwner();
      return "LuaDoc: " + StringUtil.notNullize(owner != null ? owner.toString() : null, "no owner");
  }

  public IElementType getTokenType() {
    return getElementType();
  }

  public void accept(MoonElementVisitor visitor) {
    visitor.visitDocComment(this);
  }

  public void acceptChildren(MoonElementVisitor visitor) {
    PsiElement child = getFirstChild();
    while (child != null) {
      if (child instanceof MoonPsiElement) {
        ((MoonPsiElement)child).accept(visitor);
      }

      child = child.getNextSibling();
    }
  }

  public MoonDocCommentOwner getOwner() {
    return MoonDocCommentUtil.findDocOwner(this);
  }

  @NotNull
  public MoonDocTag[] getTags() {
    final MoonDocTag[] tags = PsiTreeUtil.getChildrenOfType(this, MoonDocTag.class);
    return tags == null ? MoonDocTag.EMPTY_ARRAY : tags;
  }

  @Nullable
  public MoonDocTag findTagByName(@NonNls String name) {
    if (!getText().contains(name)) return null;
    for (PsiElement e = getFirstChild(); e != null; e = e.getNextSibling()) {
      if (e instanceof MoonDocTag && ((MoonDocTag)e).getName().equals(name)) {
        return (MoonDocTag)e;
      }
    }
    return null;
  }

  @NotNull
  public MoonDocTag[] findTagsByName(@NonNls String name) {
    if (!getText().contains(name)) return MoonDocTag.EMPTY_ARRAY;
    ArrayList<MoonDocTag> list = new ArrayList<MoonDocTag>();
    for (PsiElement e = getFirstChild(); e != null; e = e.getNextSibling()) {
      if (e instanceof MoonDocTag && CharArrayUtil.regionMatches(((MoonDocTag)e).getName(), 1, name)) {
        list.add((MoonDocTag)e);
      }
    }
    return list.toArray(new MoonDocTag[list.size()]);
  }

  @NotNull
  public PsiElement[] getDescriptionElements() {
    ArrayList<PsiElement> array = new ArrayList<PsiElement>();
    for (PsiElement child = getFirstChild(); child != null; child = child.getNextSibling()) {

      if (child instanceof PsiWhiteSpace)
          continue;
      final ASTNode node = child.getNode();
      if (node == null) continue;
      final IElementType i = node.getElementType();
      if (i == LDOC_TAG) break;
      if (i == MoonDocTokenTypes.LDOC_COMMENT_DATA ) {
        array.add(child);
      }
    }
    return MoonPsiUtils.toPsiElementArray(array);
  }

   // Return the first line of the description
   // up to and including the first '.'
   @NotNull
   @Override
   public String getSummaryDescription() {
       PsiElement[] elems = getDescriptionElements();

       if (elems.length == 0)
           return "";

       String first = StringUtil.notNullize(elems[0].getText());

       int pos = first.indexOf('.');

       if (pos>0)
           return first.substring(0, pos);

       return first;
   }
}

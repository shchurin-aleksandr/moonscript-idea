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

package com.eightbitmage.moonscript.lang.psi.impl.statements;

import com.eightbitmage.moonscript.lang.lexer.LuaTokenTypes;
import com.eightbitmage.moonscript.lang.psi.LuaNamedElement;
import com.eightbitmage.moonscript.lang.psi.LuaReferenceElement;
import com.eightbitmage.moonscript.lang.psi.symbols.LuaLocalDeclaration;
import com.eightbitmage.moonscript.lang.psi.symbols.LuaSymbol;
import com.eightbitmage.moonscript.lang.psi.util.LuaAssignment;
import com.eightbitmage.moonscript.lang.psi.util.LuaAssignmentUtil;
import com.eightbitmage.moonscript.lang.psi.visitor.LuaElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.eightbitmage.moonscript.lang.psi.expressions.LuaDeclarationExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.LuaExpression;
import com.eightbitmage.moonscript.lang.psi.expressions.LuaExpressionList;
import com.eightbitmage.moonscript.lang.psi.expressions.LuaIdentifierList;
import com.eightbitmage.moonscript.lang.psi.statements.LuaAssignmentStatement;
import com.eightbitmage.moonscript.lang.psi.statements.LuaLocalDefinitionStatement;
import com.eightbitmage.moonscript.lang.psi.statements.LuaStatementElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 6, 2010
 * Time: 10:00:19 AM
 */
public class LuaLocalDefinitionStatementImpl extends LuaStatementElementImpl implements LuaLocalDefinitionStatement,
        LuaStatementElement, LuaAssignmentStatement {
    public LuaLocalDefinitionStatementImpl(ASTNode node) {
        super(node);

        LuaExpressionList exprs = getRightExprs();

        if (exprs != null) {
            List<LuaExpression> vals = exprs.getLuaExpressions();
            LuaLocalDeclaration[] defs = getDefinedAndAssignedSymbols();
            for (int i = 0; i < defs.length && i < vals.size(); i++) {
                LuaLocalDeclaration def = defs[i];
                LuaExpression expr = vals.get(i);

                if (expr instanceof LuaNamedElement) def.setAliasElement(expr);
            }
        }
    }

    @Override
    public void accept(LuaElementVisitor visitor) {
        visitor.visitDeclarationStatement(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof LuaElementVisitor) {
            ((LuaElementVisitor) visitor).visitDeclarationStatement(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public LuaDeclarationExpression[] getDeclarations() {
        List<LuaDeclarationExpression> decls = new ArrayList<LuaDeclarationExpression>();
        LuaIdentifierList list = findChildByClass(LuaIdentifierList.class);

        assert list != null;
        for(LuaSymbol sym : list.getSymbols()) {
            if (sym instanceof LuaDeclarationExpression)
                decls.add((LuaDeclarationExpression) sym);

            if (sym instanceof LuaReferenceElement) {
                PsiElement e = ((LuaReferenceElement) sym).getElement();

                if (e instanceof LuaDeclarationExpression)
                    decls.add((LuaDeclarationExpression) e);
            }
        }

        return decls.toArray(new LuaDeclarationExpression[decls.size()]);
    }

    @Override
    public LuaExpression[] getExprs() {
        LuaExpressionList list = findChildByClass(LuaExpressionList.class);
        if (list == null) return new LuaExpression[0];

        return list.getLuaExpressions().toArray(new LuaExpression[list.count()]);
    }



    // locals are undefined within the statement, so  local a,b = b,a
    // should not resolve a to a or b to b. So to handle this we process
    // our declarations unless we are walking from a child of ourself.
    // in our case its, (localstat) <- (expr list) <- (expression) <- (variable) <- (reference )

    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState resolveState,
                                       PsiElement lastParent, @NotNull PsiElement place) {
        // If we weren't found as a parent of the reference
        if (!PsiTreeUtil.isAncestor(this, place, true)) {
            final LuaDeclarationExpression[] decls = getDeclarations();
            for (int i = decls.length - 1; i >= 0; i--) {
                LuaDeclarationExpression decl = decls[i];
                if (!processor.execute(decl, resolveState)) return false;
            }
        }

        return true;
    }

    @Override
    public LuaIdentifierList getLeftExprs() {
        return findChildByClass(LuaIdentifierList.class);
    }

    @Override
    public LuaExpressionList getRightExprs() {
        return findChildByClass(LuaExpressionList.class);
    }

    @NotNull
    @Override
    public LuaAssignment[] getAssignments() {
        return LuaAssignmentUtil.getAssignments(this);
    }


    @Override
    public LuaExpression getAssignedValue(LuaSymbol symbol) {
        return LuaAssignment.FindAssignmentForSymbol(getAssignments(), symbol);
    }


    @Override
    public IElementType getOperationTokenType() {
        return LuaTokenTypes.ASSIGN;
    }

    @Override
    public PsiElement getOperatorElement() {
        return findChildByType(getOperationTokenType());
    }

    @Override
    public LuaSymbol[] getDefinedSymbols() {
        return getDeclarations();
    }

    @Override
    public LuaLocalDeclaration[] getDefinedAndAssignedSymbols() {
        LuaAssignment[] assignments = getAssignments();

        if (assignments.length == 0) return LuaLocalDeclaration.EMPTY_ARRAY;

        LuaLocalDeclaration[] syms = new LuaLocalDeclaration[assignments.length];
        for (int i = 0, assignmentsLength = assignments.length; i < assignmentsLength; i++) {
            LuaAssignment assign = assignments[i];
            syms[i] = (LuaLocalDeclaration) assign.getSymbol();
        }
        return syms;
    }

    @Override
    public LuaExpression[] getDefinedSymbolValues() {
        LuaExpressionList exprs = getRightExprs();

        if (exprs == null) return LuaExpression.EMPTY_ARRAY;

        List<LuaExpression> vals = exprs.getLuaExpressions();

        return vals.toArray(new LuaExpression[vals.size()]);
    }
}
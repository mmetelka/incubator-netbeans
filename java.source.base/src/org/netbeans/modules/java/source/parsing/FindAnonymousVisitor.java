/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.netbeans.modules.java.source.parsing;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.lib.nbjavac.services.NBTreeMaker.IndexedClassDecl;

/**
 * Partial reparse helper visitor.
 * Finds anonymous and local classes in given method tree.
 * @author Tomas Zezula
 */
class FindAnonymousVisitor extends TreeScanner<Void,Void> {

    private static enum Mode {COLLECT, CHECK};

    int firstInner = -1;
    int noInner;
    boolean hasLocalClass;
    final Set<Tree> docOwners = new HashSet<Tree>();
    private Mode mode = Mode.COLLECT;            
    
    public final void reset () {
        this.firstInner = -1;
        this.noInner = 0;
        this.hasLocalClass = false;
        this.mode = Mode.CHECK;
    }

    @Override
    public Void visitClass(ClassTree node, Void p) {
        if (firstInner == -1) {
            firstInner = ((IndexedClassDecl)node).index;
        }
        if (node.getSimpleName().length() != 0) {
            hasLocalClass = true;
        }
        noInner++;
        handleDoc(node);
        return super.visitClass(node, p);
    }

    @Override
    public Void visitMethod(MethodTree node, Void p) {
        handleDoc(node);
        return super.visitMethod(node, p);
    }

    @Override
    public Void visitVariable(VariableTree node, Void p) {
        handleDoc(node);
        return super.visitVariable(node, p);
    }

    private void handleDoc (final Tree tree) {
        if (mode == Mode.COLLECT) {
            docOwners.add(tree);
        }
    }

}

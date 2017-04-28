/*
 * Sonar ESQL Plugin
 * Copyright (C) 2013-2017 Thomas Pohl and EXXETA AG
 * http://www.exxeta.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exxeta.iss.sonar.esql.tree.impl.expression;

import com.exxeta.iss.sonar.esql.api.symbols.Type;
import com.exxeta.iss.sonar.esql.api.symbols.TypeSet;
import com.exxeta.iss.sonar.esql.api.tree.Tree;
import com.exxeta.iss.sonar.esql.api.tree.expression.ArrayLiteralTree;
import com.exxeta.iss.sonar.esql.api.tree.expression.ExpressionTree;
import com.exxeta.iss.sonar.esql.api.tree.lexical.SyntaxToken;
import com.exxeta.iss.sonar.esql.api.tree.symbols.type.TypableTree;
import com.exxeta.iss.sonar.esql.api.visitors.DoubleDispatchVisitor;
import com.exxeta.iss.sonar.esql.tree.impl.EsqlTree;
import com.exxeta.iss.sonar.esql.tree.impl.lexical.InternalSyntaxToken;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ArrayLiteralTreeImpl extends EsqlTree implements ArrayLiteralTree, TypableTree {

  private SyntaxToken openBracket;
  private final List<ExpressionTree> elements;
  private final List<Tree> elementsAndCommas;
  private SyntaxToken closeBracket;
  private TypeSet types = TypeSet.emptyTypeSet();

  public ArrayLiteralTreeImpl(InternalSyntaxToken openBracket, InternalSyntaxToken closeBracket) {
    this.openBracket = openBracket;
    this.elements = Collections.emptyList();
    this.elementsAndCommas = Collections.emptyList();
    this.closeBracket = closeBracket;
  }

  public ArrayLiteralTreeImpl(List<Tree> elementsAndCommas) {
    this.elementsAndCommas = elementsAndCommas;
    this.elements = ImmutableList.copyOf(Iterables.filter(elementsAndCommas, ExpressionTree.class));
  }

  public ArrayLiteralTreeImpl complete(InternalSyntaxToken openBracket, InternalSyntaxToken closeBracket) {
    this.openBracket = openBracket;
    this.closeBracket = closeBracket;
    return this;
  }

  @Override
  public SyntaxToken openBracket() {
    return openBracket;
  }

  @Override
  public List<ExpressionTree> elements() {
    return elements;
  }

  @Override
  public List<Tree> elementsAndCommas() {
    return elementsAndCommas;
  }

  @Override
  public SyntaxToken closeBracket() {
    return closeBracket;
  }

  @Override
  public Kind getKind() {
    return Kind.ARRAY_LITERAL;
  }

  @Override
  public TypeSet types() {
    return types.immutableCopy();
  }

  @Override
  public void add(Type type) {
    this.types.add(type);
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    return Iterators.concat(
      Iterators.singletonIterator(openBracket),
      elementsAndCommas.iterator(),
      Iterators.singletonIterator(closeBracket)
    );
  }

  @Override
  public void accept(DoubleDispatchVisitor visitor) {
    visitor.visitArrayLiteral(this);
  }
}
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors: Stefan Irimescu, Can Berker Cikis
 *
 */

package org.rumbledb.expressions.arithmetic;


import org.rumbledb.exceptions.ExceptionMetadata;
import org.rumbledb.expressions.AbstractNodeVisitor;
import org.rumbledb.expressions.Expression;
import org.rumbledb.expressions.Node;

import java.util.Arrays;
import java.util.List;

public class AdditiveExpression extends Expression {
    private Expression leftExpression;
    private Expression rightExpression;
    private boolean isMinus;

    public AdditiveExpression(
            Expression leftExpression,
            Expression rightExpression,
            boolean isMinus,
            ExceptionMetadata metadata
    ) {
        super(metadata);
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.isMinus = isMinus;
    }

    @Override
    public <T> T accept(AbstractNodeVisitor<T> visitor, T argument) {
        return visitor.visitAdditiveExpr(this, argument);
    }

    @Override
    public String serializationString(boolean prefix) {
        String result = "(additiveExpr ";
        result += this.getChildren().get(0).serializationString(true);
        result += this.isMinus ? " - " : " +  ";
        result += this.getChildren().get(1).serializationString(true);
        result += ")";
        return result;
    }

    @Override
    public List<Node> getChildren() {
        return Arrays.asList(this.leftExpression, this.rightExpression);
    }

    public boolean isMinus() {
        return this.isMinus;
    }

}
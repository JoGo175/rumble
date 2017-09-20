/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Stefan Irimescu
 *
 */
 package jiqs.jsoniq.runtime.iterator.operational;

import jiqs.jsoniq.compiler.translator.expr.operational.base.OperationalExpressionBase;
import jiqs.exceptions.IteratorFlowException;
import jiqs.jsoniq.item.AtomicItem;
import jiqs.jsoniq.item.BooleanItem;
import jiqs.jsoniq.item.Item;
import jiqs.jsoniq.runtime.iterator.RuntimeIterator;
import jiqs.semantics.types.SequenceType;

import java.util.ArrayList;
import java.util.List;

public class InstanceOfIterator extends UnaryOperationIterator {

    private final SequenceType _sequenceType;

    public InstanceOfIterator(RuntimeIterator child, SequenceType sequenceType) {
        super(child, OperationalExpressionBase.Operator.INSTANCE_OF);
        this._sequenceType = sequenceType;
    }

    @Override
    public AtomicItem next() {
        if(this._hasNext) {
            List<Item> items = new ArrayList<>();
            _child.open(_currentDynamicContext);
            while (_child.hasNext())
                items.add(_child.next());
            _child.close();
            this._hasNext = false;
            //Empty sequence, more items
            if(items.isEmpty() && _sequenceType.getArity() != SequenceType.Arity.OneOrZero
                    && _sequenceType.getArity() != SequenceType.Arity.ZeroOrMore)
                return new BooleanItem(false);
            if(items.size() == 1  && _sequenceType.getArity() != SequenceType.Arity.OneOrZero
                    && _sequenceType.getArity() != SequenceType.Arity.OneOrMore &&
                    _sequenceType.getArity() != SequenceType.Arity.One)
                return new BooleanItem(false);
            for(Item item : items)
                if(!item.isTypeOf(_sequenceType.getItemType()))
                    return new BooleanItem(false);
            return new BooleanItem(true);
        }else
            throw new IteratorFlowException(RuntimeIterator.FLOW_EXCEPTION_MESSAGE);
    }
}

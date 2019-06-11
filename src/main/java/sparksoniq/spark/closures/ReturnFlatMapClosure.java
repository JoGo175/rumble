/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

package sparksoniq.spark.closures;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

import sparksoniq.jsoniq.item.Item;
import sparksoniq.jsoniq.runtime.iterator.RuntimeIterator;
import sparksoniq.semantics.DynamicContext;
import sparksoniq.spark.DataFrameUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ReturnFlatMapClosure implements FlatMapFunction<Row, Item> {
    RuntimeIterator _expression;
    StructType _oldSchema;
    
    private transient Kryo _kryo;
    private transient Input _input;

    public ReturnFlatMapClosure(RuntimeIterator expression, StructType oldSchema) {
        this._expression = expression;
        this._oldSchema = oldSchema;

        _kryo = new Kryo();
        DataFrameUtils.registerKryoClassesKryo(_kryo);
        _input = new Input();
    }

    @Override
    public Iterator<Item> call(Row row) {
        String[] columnNames = _oldSchema.fieldNames();
        
        Set<String> dependencies = _expression.getVariableDependencies();

        // Deserialize row
//        List<Object> deserializedRow = DataFrameUtils.deserializeEntireRow(row, _kryo, _input);
//        List<List<Item>> rowColumns = new ArrayList<>();
//        for (Object columnObject:deserializedRow) {
//            List<Item> column = (List<Item>) columnObject;
//            rowColumns.add(column);
//        }

        // Create dynamic context with deserialized data
        DynamicContext context = new DynamicContext();
        for (int columnIndex = 0; columnIndex < columnNames.length; columnIndex++) {
            String field = columnNames[columnIndex];
            System.out.println("Field: " + field);
            List<Item> i = DataFrameUtils.deserializeRowField(row, columnIndex, _kryo, _input); //rowColumns.get(columnIndex);
            context.addVariableValue(field, i);
        }

        // Apply expression to the context
        List<Item> results = new ArrayList<>();
        _expression.open(context);
        while (_expression.hasNext()) {
            results.add(_expression.next());
        }
        _expression.close();

        return results.iterator();
    }
    
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        _kryo = new Kryo();
        DataFrameUtils.registerKryoClassesKryo(_kryo);
        _input = new Input();
    }
}

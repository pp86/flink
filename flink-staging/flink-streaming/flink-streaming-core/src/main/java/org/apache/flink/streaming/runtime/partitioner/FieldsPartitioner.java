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
 */

package org.apache.flink.streaming.runtime.partitioner;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.runtime.plugable.SerializationDelegate;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;

/**
 * Partitioner that selects the same (one) channel for two Tuples having a
 * specified fields equal.
 * 
 * @param <T>
 *            Type of the Tuple
 */
public class FieldsPartitioner<T> extends StreamPartitioner<T> {
	private static final long serialVersionUID = 1L;

	private int[] returnArray = new int[1];
	KeySelector<T, ?> keySelector;

	public FieldsPartitioner(KeySelector<T, ?> keySelector) {
		super(PartitioningStrategy.GROUPBY);
		this.keySelector = keySelector;
	}

	@Override
	public int[] selectChannels(SerializationDelegate<StreamRecord<T>> record,
			int numberOfOutputChannels) {
		Object key;
		try {
			key = keySelector.getKey(record.getInstance().getValue());
		} catch (Exception e) {
			throw new RuntimeException("Could not extract key from " + record.getInstance().getValue(), e);
		}
		returnArray[0] = Math.abs(key.hashCode() % numberOfOutputChannels);

		return returnArray;
	}
}

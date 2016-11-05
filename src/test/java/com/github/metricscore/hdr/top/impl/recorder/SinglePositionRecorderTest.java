/*
 *
 *  Copyright 2016 Vladimir Bukhtoyarov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.github.metricscore.hdr.top.impl.recorder;

import com.github.metricscore.hdr.top.TestData;
import com.github.metricscore.hdr.top.impl.collector.PositionCollector;
import com.github.metricscore.hdr.top.impl.collector.PositionCollectorTestUtil;
import org.junit.Test;

import static com.github.metricscore.hdr.top.TestData.first;
import static com.github.metricscore.hdr.top.TestData.second;
import static com.github.metricscore.hdr.top.TestData.third;
import static com.github.metricscore.hdr.top.impl.recorder.PositionRecorderTestUtil.*;


public class SinglePositionRecorderTest {

    private PositionRecorder recorder = new SinglePositionRecorder(TestData.THRESHOLD_NANOS, 1000);
    private PositionCollector collector = PositionCollector.createCollector(1);

    @Test
    public void test() {
        assertEmpty(recorder);

        update(recorder, first);
        checkOrder(recorder, first);

        update(recorder, second);
        checkOrder(recorder, second);

        update(recorder, third);
        checkOrder(recorder, third);

        update(recorder, first);
        checkOrder(recorder, third);
    }

    @Test
    public void testAddInto() {
        recorder.addInto(collector);
        PositionCollectorTestUtil.assertEmpty(collector);

        update(recorder, first);
        recorder.addInto(collector);
        PositionCollectorTestUtil.checkOrder(collector, first);
    }

    @Test
    public void testReset() {
        update(recorder, first);

        recorder.reset();
        assertEmpty(recorder);
    }

}
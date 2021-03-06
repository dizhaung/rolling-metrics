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

package com.github.rollingmetrics.top.impl;


import com.github.rollingmetrics.top.Position;
import com.github.rollingmetrics.top.Top;
import com.github.rollingmetrics.util.CachingSupplier;
import com.github.rollingmetrics.util.Clock;
import com.github.rollingmetrics.top.Top;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class SnapshotCachingTop implements Top {

    private final Top target;
    private final CachingSupplier<List<Position>> cache;

    public SnapshotCachingTop(Top target, long cachingDurationMillis, Clock clock) {
        this.target = target;
        this.cache = new CachingSupplier<>(cachingDurationMillis, clock, target::getPositionsInDescendingOrder);
    }

    @Override
    public void update(long timestamp, long latencyTime, TimeUnit latencyUnit, Supplier<String> descriptionSupplier) {
        target.update(timestamp, latencyTime, latencyUnit, descriptionSupplier);
    }

    @Override
    public List<Position> getPositionsInDescendingOrder() {
        return cache.get();
    }

    @Override
    public int getSize() {
        return target.getSize();
    }

    @Override
    public String toString() {
        return "SnapshotCachingTop{" +
                "target=" + target +
                ", cache=" + cache +
                '}';
    }
}

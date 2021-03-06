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

package com.github.rollingmetrics.counter;

import com.github.rollingmetrics.util.Clock;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class SmoothlyDecayingRollingCounterPerOperationBenchmark {

    @State(Scope.Benchmark)
    public static class ResetOnSnapshotCounterWithLongResettingPeriodState {

        public final WindowCounter counter = new SmoothlyDecayingRollingCounter(Duration.ofSeconds(3600), 7);
    }

    @State(Scope.Benchmark)
    public static class ResetOnSnapshotCounterWithShortResettingPeriodState {
        private final Clock clock = new Clock() {
            // this timer implementation will lead to invalidate each chunk after each increment
            final AtomicLong timeMillis = new AtomicLong();
            @Override
            public long currentTimeMillis() {
                return timeMillis.addAndGet(1000L);
            }
        };
        public final WindowCounter counter =  new SmoothlyDecayingRollingCounter(Duration.ofSeconds(1), 10, clock);
    }

    @State(Scope.Benchmark)
    public static class IncrementAtomicState {
        AtomicLong sum = new AtomicLong();
    }

    @Benchmark
    public long baseLineCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Benchmark
    public long baseLineIncrementAtomic(IncrementAtomicState state) {
        return state.sum.addAndGet(1);
    }

    @Benchmark
    public void benchmarkAddToCounterWithLongResettingPeriod(ResetOnSnapshotCounterWithLongResettingPeriodState state) {
        state.counter.add(1);
    }

    @Benchmark
    public void benchmarkAddToCounterWithShortResettingPeriod(ResetOnSnapshotCounterWithShortResettingPeriodState state) {
        state.counter.add(1);
    }

    @Benchmark
    public long readSum(ResetOnSnapshotCounterWithLongResettingPeriodState state) {
        return state.counter.getSum();
    }

    public static class OneThread {
        public static void main(String[] args) throws RunnerException {
            Options opt = new OptionsBuilder()
                    .include(((Class) SmoothlyDecayingRollingCounterPerOperationBenchmark.class).getSimpleName())
                    .warmupIterations(5)
                    .measurementIterations(5)
                    .threads(1)
                    .forks(1)
                    .build();
            try {
                new Runner(opt).run();
            } catch (RunnerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class FourThread {
        public static void main(String[] args) throws RunnerException {
            Options opt = new OptionsBuilder()
                    .include(((Class) SmoothlyDecayingRollingCounterPerOperationBenchmark.class).getSimpleName())
                    .warmupIterations(5)
                    .measurementIterations(5)
                    .threads(4)
                    .forks(1)
                    .build();
            try {
                new Runner(opt).run();
            } catch (RunnerException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

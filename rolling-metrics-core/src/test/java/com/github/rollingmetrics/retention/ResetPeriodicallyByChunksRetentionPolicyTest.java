/*
 *    Copyright 2017 Vladimir Bukhtoyarov
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.github.rollingmetrics.retention;

import org.junit.Test;

import java.time.Duration;

public class ResetPeriodicallyByChunksRetentionPolicyTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldDisAllowLessThenOneChunks() {
        RetentionPolicy.resetPeriodicallyByChunks(Duration.ofSeconds(1), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRollingWindowShouldBeDisallowed() {
        RetentionPolicy.resetPeriodicallyByChunks(null, 3);
    }

    @Test
    public void shouldAllowOneChunk() {
        RetentionPolicy.resetPeriodicallyByChunks(Duration.ofSeconds(1), 1);
    }

    @Test
    public void shouldAllowTwoChunks() {
        RetentionPolicy.resetPeriodicallyByChunks(Duration.ofSeconds(1), 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDisallowNegativeDuration() {
        RetentionPolicy.resetPeriodicallyByChunks(Duration.ofMillis(-1), 2);
    }

}
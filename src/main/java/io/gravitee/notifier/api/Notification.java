/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.notifier.api;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.gravitee.notifier.api.jackson.RawJsonDeserializer;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class Notification implements Serializable {

    private String type;

    private List<Period> periods;

    @JsonDeserialize(using = RawJsonDeserializer.class)
    @JsonRawValue
    private String configuration;

    /**
     * Indicates if the specified timestamp matches with one of the time periods defined for this notification.
     *
     * @param timestamp the timestamp to check against the time periods.
     * @return <code>true</code> if the timestamp matches one of the time periods, <code>false</code> else.
     */
    public boolean canNotify(long timestamp) {
        final List<Period> periods = this.getPeriods();

        if (periods == null || periods.isEmpty()) {
            return true;
        }

        final LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);

        return periods.stream().anyMatch(period -> period.isIncluded(localDateTime));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }
}

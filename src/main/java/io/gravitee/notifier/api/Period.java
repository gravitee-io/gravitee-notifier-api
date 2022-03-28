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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.*;
import java.util.List;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class Period implements Serializable {

    private static final String DEFAULT_ZONE_ID = ZoneId.systemDefault().getId();
    private static final int DEFAULT_BEGIN_HOUR = 0;
    private static final int DEFAULT_END_HOURS = 86399;

    /**
     * List of the days covered by this period of time.
     * See {@link DayOfWeek#getValue()}.
     */
    private List<Integer> days;

    /**
     * The zone id. Default is the current time zone.
     * See {@link ZoneId#SHORT_IDS} for a list of all possible zone ids.
     */
    private String zoneId;

    /**
     * The begin hour express in seconds elapsed since the beginning of the day.
     */
    private int beginHour;

    /**
     * The end hour express in seconds elapsed since the beginning of the day.
     */
    private int endHour;

    @JsonCreator
    protected Period(
        @JsonProperty(value = "days") List<Integer> days,
        @JsonProperty(value = "zoneId", required = true) String zoneId,
        @JsonProperty(value = "beginHour", required = true) Integer beginHour,
        @JsonProperty(value = "endHour", required = true) Integer endHour
    ) {
        this.days = days;
        this.zoneId = zoneId;
        this.beginHour = beginHour == null ? DEFAULT_BEGIN_HOUR : beginHour;
        this.endHour = endHour == null ? DEFAULT_END_HOURS : endHour;
    }

    public boolean isIncluded(LocalDateTime dateTime) {
        final ZoneId zoneId = ZoneId.of(getZoneId());

        // First set the local notification timestamp at the expected timezone.
        final ZonedDateTime zonedDateTime = dateTime.atZone(ZoneOffset.systemDefault()).withZoneSameInstant(zoneId);

        if (days != null && !days.contains(zonedDateTime.getDayOfWeek().getValue())) {
            // Notification is outside of the wanted notification days.
            return false;
        }

        final OffsetTime time = zonedDateTime.toOffsetDateTime().toOffsetTime();
        final OffsetTime begin = OffsetTime.of(LocalTime.ofSecondOfDay(beginHour), zoneId.getRules().getOffset(Instant.now()));
        final OffsetTime end = OffsetTime.of(LocalTime.ofSecondOfDay(endHour), zoneId.getRules().getOffset(Instant.now()));

        return isBetween(time, begin, end);
    }

    private boolean isBetween(OffsetTime time, OffsetTime begin, OffsetTime end) {
        return time.isEqual(begin) || (time.isAfter(begin) && (time.isBefore(end) || time.isEqual(end)));
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public int getBeginHour() {
        return beginHour;
    }

    public void setBeginHour(int beginHour) {
        this.beginHour = beginHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    @Override
    public String toString() {
        return "Period{" + "days=" + days + ", zoneId='" + zoneId + '\'' + ", beginHour=" + beginHour + ", endHour=" + endHour + '}';
    }

    public static class Builder {

        private List<Integer> days;
        private String zoneId = DEFAULT_ZONE_ID;
        private int beginHour = DEFAULT_BEGIN_HOUR;
        private int endHour = DEFAULT_END_HOURS;

        public Builder days(List<Integer> days) {
            this.days = days;
            return this;
        }

        public Builder zoneId(String zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public Builder beginHour(int beginHour) {
            this.beginHour = beginHour;
            return this;
        }

        public Builder endHour(int endHour) {
            this.endHour = endHour;
            return this;
        }

        public Period build() {
            return new Period(days, zoneId, beginHour, endHour);
        }
    }
}

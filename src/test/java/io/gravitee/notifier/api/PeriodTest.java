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

import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
class PeriodTest {

    @Test
    void isIncluded_hours() {
        final LocalDateTime now = LocalDateTime.now();

        final Period period = new Period.Builder()
            .beginHour(LocalTime.of(0, 0, 0).toSecondOfDay())
            .endHour(LocalTime.of(23, 59, 59).toSecondOfDay())
            .build();

        assertTrue(period.isIncluded(now));
    }

    @Test
    void isNotIncluded_hours() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime hourBefore = now.minus(1, HOURS);

        if (now.getDayOfWeek() != hourBefore.getDayOfWeek()) {
            // Be sure we are on the same day (I know some people capable to run this test at midnight! ;-) ).
            now = now.plus(1, HOURS);
            hourBefore = hourBefore.plus(1, HOURS);
        }

        final Period period = new Period.Builder()
            .beginHour(LocalTime.of(0, 0, 0).toSecondOfDay())
            .endHour(LocalTime.of(hourBefore.getHour(), 59, 59).toSecondOfDay())
            .build();
        assertFalse(period.isIncluded(now));
    }

    @Test
    void isIncluded_days() {
        final LocalDateTime now = LocalDateTime.now();

        final Period period = new Period.Builder()
            .beginHour(LocalTime.of(0, 0, 0).toSecondOfDay())
            .endHour(LocalTime.of(23, 59, 59).toSecondOfDay())
            .days(Collections.singletonList(now.getDayOfWeek().getValue()))
            .build();

        assertTrue(period.isIncluded(now));
    }

    @Test
    void isNotIncluded_days() {
        final LocalDateTime now = LocalDateTime.now();

        final Period period = new Period.Builder()
            .beginHour(LocalTime.of(0, 0, 0).toSecondOfDay())
            .endHour(LocalTime.of(23, 59, 59).toSecondOfDay())
            .days(
                Arrays
                    .stream(DayOfWeek.values())
                    .filter(day -> day != now.getDayOfWeek()) // All days except current one.
                    .map(DayOfWeek::getValue)
                    .collect(Collectors.toList())
            )
            .build();

        assertFalse(period.isIncluded(now));
    }

    @Test
    void isIncluded_otherHourInOtherTimezone() {
        ZoneId systemZone = ZoneId.systemDefault();
        ZoneId chicagoZone = ZoneId.of("America/Chicago");

        LocalDateTime now = LocalDateTime.now(systemZone);
        LocalDateTime nowAtChicago = LocalDateTime.now(chicagoZone);
        LocalDateTime hourAfterAtChicago = nowAtChicago.plus(1, HOURS);

        if (nowAtChicago.getDayOfWeek() != hourAfterAtChicago.getDayOfWeek()) {
            // Be sure we are on the same day (I know some people capable to run this test at midnight! ;-) ).
            nowAtChicago = nowAtChicago.minus(1, HOURS);
            hourAfterAtChicago = hourAfterAtChicago.minus(1, HOURS);
            now = now.minus(1, HOURS);
        }

        final Period period = new Period.Builder()
            .beginHour(LocalTime.of(nowAtChicago.getHour(), 0, 0).toSecondOfDay())
            .endHour(LocalTime.of(hourAfterAtChicago.getHour(), 0, 0).toSecondOfDay())
            .zoneId(chicagoZone.getId())
            .build();

        assertTrue(period.isIncluded(now));
    }

    @Test
    void isNotIncluded_otherHourInOtherTimezone() {
        ZoneId systemZone = ZoneId.systemDefault();
        ZoneId chicagoZone = ZoneId.of("America/Chicago");

        LocalDateTime now = LocalDateTime.now(systemZone);

        final Period period = new Period.Builder()
            .beginHour(LocalTime.of(now.getHour(), 0, 0).toSecondOfDay())
            .endHour(LocalTime.of(now.getHour(), 0, 0).toSecondOfDay())
            .zoneId(chicagoZone.getId())
            .build();

        assertFalse(period.isIncluded(now));
    }

    @Test
    void isNotIncluded_otherDayInOtherTimezone() {
        ZoneId systemZone = ZoneId.systemDefault();
        LocalDateTime now = LocalDateTime.now(systemZone);

        // Find another zone where it is not the same day.
        ZoneId otherDayZone = ZoneOffset.ofHours(now.getHour() < 12 ? -18 : 18);

        final Period period = new Period.Builder()
            .zoneId(otherDayZone.getId())
            .days(Collections.singletonList(now.getDayOfWeek().getValue())) // Set the current but it is another day in the other zone.
            .build();

        assertFalse(period.isIncluded(now));
    }
}

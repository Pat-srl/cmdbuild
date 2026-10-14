/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.time.Duration;
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class Interval {

    public static final Interval ZERO = new Interval(Period.ZERO, Duration.ZERO);

    private final Period period;
    private final Duration duration;

    public Interval(Period period, Duration duration) {
        if (duration.toDays() != 0) {
            period = period.plusDays(duration.toDays());
            duration = duration.minusDays(duration.toDays());
        }
        this.period = checkNotNull(period);
        this.duration = checkNotNull(duration);
    }

    public Period getPeriod() {
        return period;
    }

    public Duration getDuration() {
        return duration;
    }

    public Interval withPeriod(Period period) {
        return new Interval(period, duration);
    }

    public Interval withDuration(Duration duration) {
        return new Interval(period, duration);
    }

    public Duration toDuration() {
        checkArgument(period.getYears() == 0 && period.getMonths() == 0, "cannot convert to duration an interval with nonzero years or months");
        return duration.plusDays(period.getDays());
    }

    public boolean isZero() {
        return period.isZero() && duration.isZero();
    }

    @Override
    public String toString() {
        if (duration.isZero()) {
            return period.toString();
        } else if (period.isZero()) {
            return duration.toString();
        } else {
            return period.toString() + duration.toString().replaceFirst("^P", "");
        }
    }

    public static Interval fromPeriod(Period period) {
        return new Interval(period, Duration.ZERO);
    }

    public static Interval fromDuration(Duration duration) {
        return new Interval(Period.ZERO, duration);
    }

    public static Interval valueOf(String value) {
        checkNotBlank(value);
        Period period;
        Matcher matcher = Pattern.compile("^(P[^T]+)").matcher(value.toUpperCase());
        if (matcher.find()) {
            period = Period.parse(matcher.group(1));
        } else {
            period = Period.ZERO;
        }
        Duration duration;
        matcher = Pattern.compile("^P[^T]*(T.+)").matcher(value.toUpperCase());
        if (matcher.find()) {
            duration = Duration.parse("P" + matcher.group(1));
        } else {
            duration = Duration.ZERO;
        }
        return new Interval(period, duration);
    }

}

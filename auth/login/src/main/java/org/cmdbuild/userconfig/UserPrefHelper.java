/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.userconfig;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;

public interface UserPrefHelper {

    @Nullable
    String serializeDateTime(@Nullable ZonedDateTime dateTime);

    @Nullable
    String serializeDate(@Nullable LocalDate localDate);

    @Nullable
    String serializeTime(@Nullable LocalTime localTime);

    @Nullable
    ZonedDateTime parseDateTime(@Nullable Object value);

    @Nullable
    LocalDate parseDate(@Nullable Object value);

    @Nullable
    LocalTime parseTime(@Nullable Object value);

    @Nullable
    String serializeNumber(@Nullable Number number);

    @Nullable
    Number parseNumber(@Nullable String number);

}

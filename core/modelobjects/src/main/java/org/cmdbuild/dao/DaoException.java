/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao;

import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.NonTransientDataAccessException;

public class DaoException extends RuntimeException {

    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException(Throwable cause, String message, Object... args) {
        super(format(message, args), cause);
    }

    public DaoException(String message, Object... args) {
        super(format(message, args));
    }

    public DaoException(NonTransientDataAccessException cause) {
        super(getMessage(cause));
    }

    private static String getMessage(NonTransientDataAccessException cause) {
        if (cause instanceof DataIntegrityViolationException dataIntegrityViolationException) {
            Matcher matcher = Pattern.compile("new row for relation \\\"(\\w+)\\\" violates check constraint \\\"_cm3_(\\w+)_notnull\\\"").matcher(dataIntegrityViolationException.getMessage());
            if (matcher.find()) {
                return format("CMO 208: attribute \"%s\" of \"%s\" is mandatory but found null", matcher.group(2), matcher.group(1));
            }
            matcher = Pattern.compile("Detail: Key \\(\\\"(\\w+)\\\"\\)=\\((.*)\\) already exists.").matcher(dataIntegrityViolationException.getMessage());
            if (matcher.find()) {
                return format("CMO 201: duplicate value: a card with \"%s\" \"%s\" does already exist", matcher.group(1), matcher.group(2));
            }
        } else if (cause instanceof InvalidDataAccessResourceUsageException invalidDataAccessResourceUsage) {
            Matcher matcher = Pattern.compile("ERROR: (.*)$").matcher(invalidDataAccessResourceUsage.getMostSpecificCause().getMessage());
            if (matcher.find()) {
                return format("CMO 202: %s", matcher.group(1));
            }
        }
        return format("CM0 200: \n\n%s", cause.getMessage());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

public interface PasswordManagementConfiguration {

    boolean isPasswordManagementEnabled();

    boolean isPasswordChangeEnabled();

    boolean getDifferentFromUsername();

    boolean getDifferentFromPrevious();

    int getDifferentFromPreviousCount();

    boolean requireDigit();

    boolean requireLowercase();

    boolean requireUppercase();

    int getPasswordMinLength();

    int getMaxPasswordAgeDays();

    int getForewarningDays();

    boolean isServiceUsersPasswordExpirationEnabled();
}

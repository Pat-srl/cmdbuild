/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import org.cmdbuild.lookup.LookupTypeEnum;

@LookupTypeEnum("CalendarFrequency")
public enum EventFrequency {
    EF_ONCE, EF_DAILY, EF_WEEKLY, EF_MONTHLY, EF_YEARLY

}

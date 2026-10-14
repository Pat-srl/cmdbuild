/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.date;

import java.time.format.DateTimeFormatter;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ExtjsDateUtils {

    public static String extjsDateTimeFormatToJavaDateTimeFormat(String value) {
        return checkNotBlank(value)
                .replaceAll("d", "dd")
                .replaceAll("m", "MM")
                .replaceAll("Y", "yyyy")
                .replaceAll("H", "HH")
                .replaceAll("h", "hh")
                .replaceAll("i", "mm")
                .replaceAll("s", "ss")
                .replaceAll("A", "a");
//        switch (value) {
//            case "d/m/Y H:i:s":
//                return "dd/MM/yyyy";
//            case "d/m/Y H:i:s A":
//            case "d-m-Y H:i:s":
//            case "d-m-Y H:i:s A":
//            case "d.m.Y H:i:s":
//            case "d.m.Y H:i:s A":
//            case "m/d/Y H:i:s":
//            case "m/d/Y H:i:s A":
//            case "Y/m/d H:i:s":
//            case "Y/m/d H:i:s A":
//            case "Y-m-d H:i:s":
//            case "Y-m-d H:i:s A":
//        }
    }

    public static DateTimeFormatter extjsDateTimeFormatToJavaDateTimeFormatter(String value) {
        return DateTimeFormatter.ofPattern(extjsDateTimeFormatToJavaDateTimeFormat(value));
    }

//        dateFormatsData: function () {
//            return [{
//                label: 'dd/mm/yyyy',
//                value: 'd/m/Y'
//            }, {
//                label: 'dd-mm-yyyy',
//                value: 'd-m-Y'
//            }, {
//                label: 'dd.mm.yyyy',
//                value: 'd.m.Y'
//            }, {
//                label: 'mm/dd/yyyy',
//                value: 'm/d/Y'
//            }, {
//                label: 'yyyy/mm/dd',
//                value: 'Y/m/d'
//            }, {
//                label: 'yyyy-mm-dd',
//                value: 'Y-m-d'
//            }];
//        },
//
//        timeFormatsData: function () {
//            return [{
//                value: 'H:i:s',
//                label: CMDBuildUI.locales.Locales.main.preferences.twentyfourhourformat
//            }, {
//                value: 'h:i:s A',
//                label: CMDBuildUI.locales.Locales.main.preferences.twelvehourformat
//            }];
//        },
}

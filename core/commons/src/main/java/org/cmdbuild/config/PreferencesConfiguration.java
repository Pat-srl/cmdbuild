/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

public interface PreferencesConfiguration {

    PreferredOfficeSuite getPreferredOfficeSuite();

    String getPreferredFileCharset();
    
    String getPreferredCsvSeparator();

    enum PreferredOfficeSuite {
        POS_DEFAULT, POS_MSOFFICE
    }

}

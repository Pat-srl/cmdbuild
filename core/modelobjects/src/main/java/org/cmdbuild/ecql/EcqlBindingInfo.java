/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql;

import java.util.List;

public interface EcqlBindingInfo {

	List<String> getClientBindings();

	List<String> getServerBindings();

	List<String> getXaBindings();
}

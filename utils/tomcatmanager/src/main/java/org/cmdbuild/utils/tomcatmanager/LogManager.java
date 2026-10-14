/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.tomcatmanager;

/**
 *
 * @author davide
 */
public interface LogManager {

	/**
	 * flush all log data to destination (logger, file or stdout)
	 */
	public void flushLogs();

}

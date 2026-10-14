/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

import java.io.File;

public interface DumpService {

	void dumpDatabaseToFile(File file);
}

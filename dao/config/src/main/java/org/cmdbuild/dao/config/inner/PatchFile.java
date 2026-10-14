/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import java.io.File;
import jakarta.annotation.Nullable;

public interface PatchFile {

	File getFile();

	@Nullable
	String getCategory();

}

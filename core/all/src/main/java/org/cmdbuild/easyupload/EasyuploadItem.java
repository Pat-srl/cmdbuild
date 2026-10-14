/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

import org.cmdbuild.common.CmContentInfo;

public interface EasyuploadItem extends EasyuploadItemInfo, CmContentInfo {

	static final String EASYUPLOAD_CONTENT = "Content";

	byte[] getContent();

}

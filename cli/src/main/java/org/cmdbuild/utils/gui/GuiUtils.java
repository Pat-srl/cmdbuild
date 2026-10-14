/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.gui;

import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

public class GuiUtils {

	public static Image getCmdbuildLogo() {
		try {
			return ImageIO.read(GuiUtils.class.getResourceAsStream("/org/cmdbuild/utils/gui/logo.png"));
		} catch (IOException ex) {
			throw runtime(ex);
		}
	}

	public static Image getCmdbuildIcon() {
		try {
			return ImageIO.read(GuiUtils.class.getResourceAsStream("/org/cmdbuild/utils/gui/icon.png"));
		} catch (IOException ex) {
			throw runtime(ex);
		}
	}

}

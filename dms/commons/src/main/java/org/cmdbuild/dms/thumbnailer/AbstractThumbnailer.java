/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.dms.thumbnailer;

/**
 *
 * @author ataboga
 */
public abstract class AbstractThumbnailer implements Thumbnailer {

    protected int thumbHeight;
    protected int thumbWidth;

    protected AbstractThumbnailer() {
        this.thumbHeight = 255;
        this.thumbWidth = 255;
    }

    protected AbstractThumbnailer(int thumbHeight, int thumbWidth) {
        this.thumbHeight = thumbHeight;
        this.thumbWidth = thumbWidth;
    }
}

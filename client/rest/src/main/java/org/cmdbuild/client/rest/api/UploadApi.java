/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

public interface UploadApi {

    UploadApi upload(String path, byte[] data);

    UploadApi uploadMany(byte[] zipData);

    UploadData download(String path);

    UploadData downloadAll();

    interface UploadData {

        byte[] toByteArray();
    }
}

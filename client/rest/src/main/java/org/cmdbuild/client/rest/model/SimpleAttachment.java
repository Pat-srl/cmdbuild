/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

public class SimpleAttachment implements Attachment {

	private final String fileName, version, id;

	public SimpleAttachment(String fileName, String version, String id) {
		this.fileName = fileName;
		this.version = version;
		this.id = id;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "SimpleAttachment{" + "fileName=" + fileName + ", version=" + version + ", id=" + id + '}';
	}

}

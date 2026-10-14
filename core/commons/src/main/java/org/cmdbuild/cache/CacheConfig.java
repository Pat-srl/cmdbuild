/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cache;

public enum CacheConfig {
	/**
	 * default cache config (1h timeout)
	 */
	DEFAULT, /**
	 * never expire entries, store locally (use with system objects, such as
	 * db model)
	 */
	SYSTEM_OBJECTS

}

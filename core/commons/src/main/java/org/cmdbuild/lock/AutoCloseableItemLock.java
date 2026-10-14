/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lock;

public interface AutoCloseableItemLock extends ItemLock, AutoCloseable {

    @Override
    void close();

}

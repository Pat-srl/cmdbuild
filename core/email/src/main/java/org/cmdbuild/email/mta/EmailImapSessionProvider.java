/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.Session;
import jakarta.mail.Store;

/**
 *
 * @author afelice
 */
public interface EmailImapSessionProvider extends AutoCloseable {

    /**
     * @return a {@link jakarta.mail.Session} for given emailAcocunt
     */
    Session getSession();

    /**
     * {@link jakarta.mail.Session#getStore()}
     *
     * @return a already connected {@link jakarta.mail.Store}
     * @see jakarta.mail.Store#connect()
     */
    Store getStore();

    void close();
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.Session;
import jakarta.mail.Transport;

/**
 *
 * @author afelice
 */
public interface EmailSmtpSessionProvider extends AutoCloseable {

    /**
     * @return a {@link jakarta.mail.Session} for given emailAcocunt
     */
    Session getSession();

    /**
     * {@link jakarta.mail.Session#getTransport()}
     *
     * @return a already connected {@link jakarta.mail.Transport}
     * @see jakarta.mail.Transport#connect()
     */
    Transport getTransport();

    @Override
    void close();

}

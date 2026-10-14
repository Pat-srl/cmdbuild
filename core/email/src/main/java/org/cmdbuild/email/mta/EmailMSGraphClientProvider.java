/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.microsoft.graph.serviceclient.GraphServiceClient;

/**
 *
 * @author afelice
 */
public interface EmailMSGraphClientProvider extends AutoCloseable {

    GraphServiceClient create();

    public void testConnection();

    @Override
    void close();

}

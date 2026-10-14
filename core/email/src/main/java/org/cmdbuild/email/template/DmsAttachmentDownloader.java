/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.template;

import java.util.List;
import jakarta.activation.DataHandler;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.data.filter.CmdbFilter;

/**
 *
 * @author afelice
 */
public interface DmsAttachmentDownloader {

    public List<DataHandler> downloadAttachments(Card clientCard, CmdbFilter uploadAttachmentsFilter);

}

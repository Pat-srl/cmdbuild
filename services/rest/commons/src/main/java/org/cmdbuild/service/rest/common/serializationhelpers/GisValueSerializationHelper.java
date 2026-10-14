/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisValue;
import org.cmdbuild.service.rest.common.utils.WsSerializationUtils;
import org.cmdbuild.template.SimpleExpressionInputData;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class GisValueSerializationHelper {

    private final DaoService daoService;
    private final DmsService dmsService;
    private final EmailTemplateProcessorService emailTemplateProcessorService;
    private final UserClassService userClassService;

    public GisValueSerializationHelper(DaoService daoService, DmsService dmsService, EmailTemplateProcessorService emailTemplateProcessorService, UserClassService userClassService) {
        this.daoService = checkNotNull(daoService);
        this.dmsService = checkNotNull(dmsService);
        this.emailTemplateProcessorService = checkNotNull(emailTemplateProcessorService);
        this.userClassService = checkNotNull(userClassService);
    }

    public CmMapUtils.FluentMap serializeGisValueWithPanelInfo(GisValue<?> value, GisAttribute gisAttr) {
        Card ownerCard = daoService.getCard(value.getOwnerCardId());
        String imageAttachmentId;
        if (ownerCard.get(gisAttr.getConfig().getInfoWindowImage()) != null) {
            imageAttachmentId = dmsService.getCardAttachmentByMetadataId(toLong(ownerCard.get(gisAttr.getConfig().getInfoWindowImage()))).getDocumentId();
        } else {
            imageAttachmentId = null;
        }
        return WsSerializationUtils.serializeGeoValue(value).with(
                "infoWindowEnabled", gisAttr.getConfig().getInfoWindowEnabled(),
                "infoWindowContent", emailTemplateProcessorService.processExpression(
                        SimpleExpressionInputData.extendedBuilder()
                                .withExpression(gisAttr.getConfig().getInfoWindowContent())
                                .withClientCard(ownerCard)
                                .build()),
                "infoWindowImage", userClassService.getUserClass(gisAttr.getOwnerClassName()).getActiveServiceAttributes().stream().anyMatch(a -> a.getName().equals(gisAttr.getConfig().getInfoWindowImage())) ? imageAttachmentId : null,
                "_owner_description", ownerCard.getDescription());
    }

    public static CmMapUtils.FluentMap<String, Object> serializeGisValueList(List<GisValue> geometries) {
        return response(geometries.stream().map(WsSerializationUtils::serializeGeoValue));
    }
}

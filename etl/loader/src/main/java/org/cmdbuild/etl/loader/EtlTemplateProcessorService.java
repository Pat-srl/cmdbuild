/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import java.util.List;
import jakarta.activation.DataSource;

public interface EtlTemplateProcessorService {

    DataSource exportDataWithTemplate(EtlTemplate template);

    EtlProcessingResult importDataWithTemplate(Object data, EtlTemplate template);

    EtlProcessingResult importDataWithTemplates(List<EtlTemplateWithData> templatesWithData);

}

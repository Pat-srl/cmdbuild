/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.test;

import java.util.Map;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.job.EmailJobConfig;
import org.cmdbuild.email.job.EmailJobConfigImpl;
import org.cmdbuild.email.job.EmailJobRunnerImpl;
import org.cmdbuild.email.template.EmailUtils;
import org.cmdbuild.jobs.JobData;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.email.utils.EmailMtaUtils.parseEmail;

public class EmailMappingProcessingTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testEmailMappingProcessing() {
        JobData jobData = mock(JobData.class);
        when(jobData.isOfType(EmailJobRunnerImpl.EMAIL_JOB_TYPE)).thenReturn(true);
        when(jobData.getConfig()).thenReturn(map(
                "filter_type", "regex",
                "mapper_type", "keyvalue",
                "account_name", "crm: mail da form",
                "filter_reject", false,
                "cronExpression", "*/10 * * * ?",
                "mapper_key_end", "</key>",
                "parsing_active", false,
                "folder_incoming", "gestionecontatti",
                "folder_rejected", "",
                "mapper_key_init", "<key>",
                "parsing_key_end", "",
                "folder_processed", "processed",
                "mapper_value_end", "</value>",
                "parsing_key_init", "",
                "filter_from_regex", "info@cmdbuild.org&#124;tecnoteca@tecnoteca.com",
                "filter_regex_from", "info@cmdbuild.org|tecnoteca@tecnoteca.com",
                "mapper_value_init", "<value>",
                "parsing_value_end", "",
                "parsing_value_init", "",
                "filter_function_name", null,
                "filter_regex_subject", ".*",
                "filter_subject_regex", ".*",
                "action_workflow_active", true,
                "action_workflow_advance", true,
                "action_attachments_active", false,
                "action_notification_active", false,
                "action_workflow_class_name", "GestioneContatti",
                "action_attachments_category", "",
                "action_notification_template", "",
                "action_workflow_fields_mapping", "ContattoSkype={mapper:Skype}&#124;Email={mapper:Email}&#124;Form={mapper:Form}&#124;TipoServizioIngresso={mapper:TipoServizio}&#124;DettaglioInformazioni={mapper:DettaglioInformazioni}&#124;AppellativoIngresso={mapper:Appellativo}&#124;DescrizioneRichiesta={mapper:DescrizioneRichiesta}&#124;Comune={mapper:Citta}&#124;Organizzazione={mapper:Organizzazione}&#124;Nazione={mapper:Nazione}&#124;AmbitoUtilizzo={mapper:AmbitoUtilizzo}&#124;Telefono={mapper:Telefono}&#124;Sito={mapper:Sito}&#124;Nome={mapper:Nome}&#124;Cognome={mapper:Cognome}&#124;OggettoRichiesta={mapper:OggettoRichiesta}&#124;RuoloIngresso={mapper:Ruolo}",
                "action_workflow_attachmentssave", false,
                "action_workflow_attachmentscategory", ""
        ));

        EmailJobConfig config = new EmailJobConfigImpl(jobData);

        Email email = parseEmail(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/email/email_3_raw.txt")));

        String emailPayload = email.getContentPlaintext();

        EasytemplateProcessor processor = EasytemplateProcessorImpl.builder().withResolver("mapper", (x) -> {
            return EmailUtils.processMapperExpr(config.getMapperConfig(), emailPayload, x);
        }).build();

        logger.info("fields =\n{}", mapToLoggableString(config.getActionWorkflowFieldsMapping()));

        Map<String, String> mapped = map(config.getActionWorkflowFieldsMapping()).mapValues(v -> processor.processExpression(v));

        logger.info("mapped =\n{}", mapToLoggableString(mapped));

        assertEquals("FIN", mapped.get("Nazione"));
        assertEquals("+358458572270", mapped.get("Telefono"));
    }

}

package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.easytemplate.EasytemplateResolverNames;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.easytemplate.store.EasytemplateRepository;

@Component
public class TaskEasytemplateProcessorFactory {

	public static final String DB_TEMPLATE = EasytemplateResolverNames.DB_TEMPLATE;

	private final EasytemplateRepository templateRepository;

	public TaskEasytemplateProcessorFactory(EasytemplateRepository templateRepository) {
		this.templateRepository = checkNotNull(templateRepository);
	}

	@Bean
	public EasytemplateProcessor templateResolver() {
		return EasytemplateProcessorImpl.builder().withResolver((input) -> templateRepository.getTemplateOrNull(input), DB_TEMPLATE).build();
	}

}

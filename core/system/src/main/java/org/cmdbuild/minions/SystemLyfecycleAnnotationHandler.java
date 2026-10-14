package org.cmdbuild.minions;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Provider;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.jobs.JobExecutorService.JOBUSER_SYSTEM;
import org.cmdbuild.jobs.JobSessionService;
import static org.cmdbuild.utils.date.CmDateUtils.toDuration;
import static org.cmdbuild.utils.lang.CmExecutorUtils.scheduledExecutorService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmExecutorUtils.waitFor;
import org.cmdbuild.utils.lang.CmReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class SystemLyfecycleAnnotationHandler implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus;
    private final ScheduledExecutorService executor;

    public SystemLyfecycleAnnotationHandler(Provider<JobSessionService> sessionService, EventBusService eventBusService) {//TODO improve provider inject (?)
        checkNotNull(sessionService);
        this.eventBus = eventBusService.getSystemEventBus();
        executor = scheduledExecutorService(getClass().getName(), () -> {
            MDC.put("cm_type", "sys");
            MDC.put("cm_id", "sys:aux");
            sessionService.get().createJobSessionContextWithUser(JOBUSER_SYSTEM, "system lifecycle helper job");
        }, () -> sessionService.get().destroyJobSessionContext());
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executor);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean; // nothing to do
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof MinionComponent)) {//TODO improve this
            ReflectionUtils.doWithMethods(bean.getClass(), (Method method) -> {
                if (method.isAnnotationPresent(PostStartup.class)) {
                    PostStartup postStartup = method.getAnnotation(PostStartup.class);
                    logger.debug("register method {}#{} for PostStartup hook", beanName, method.getName());
                    eventBus.register(new Object() {
                        @Subscribe
                        public void handleSystemStartingServicesEvent(SystemTriggerPostStartupEvent event) throws InterruptedException, ExecutionException {
                            if (isBlank(postStartup.delay())) {
                                waitFor(executor.submit(() -> {
                                    logger.debug("run PostStartup method {}#{}", beanName, method.getName());
                                    try {
                                        CmReflectionUtils.executeMethod(bean, method);
                                    } catch (Exception ex) {
                                        logger.error("error invoking PostStartup method {}.{}", beanName, method.getName(), ex);
                                    }
                                }));
                            } else {
                                Duration delay = toDuration(postStartup.delay());
                                executor.schedule(() -> {
                                    logger.debug("run delayed PostStartup method {}#{}", beanName, method.getName());
                                    try {
                                        CmReflectionUtils.executeMethod(bean, method);
                                    } catch (Exception ex) {
                                        logger.error("error invoking delayed PostStartup method {}.{}", beanName, method.getName(), ex);
                                    }
                                }, delay.toMillis(), TimeUnit.MILLISECONDS);
                            }
                        }
                    });
                }
                if (method.isAnnotationPresent(PreShutdown.class)) {
                    PreShutdown preShutdown = method.getAnnotation(PreShutdown.class);
                    logger.debug("register method {}#{} for PreShutdown hook", beanName, method.getName());
                    eventBus.register(new Object() {
                        @Subscribe
                        public void handleSystemStoppingServicesEvent(SystemTriggerPreShutdownEvent event) {
                            waitFor(executor.submit(() -> {
                                logger.debug("run PreShutdown method {}#{}", beanName, method.getName());
                                try {
                                    CmReflectionUtils.executeMethod(bean, method);
                                } catch (Exception ex) {
                                    logger.error("error invoking PreShutdown method {}.{}", beanName, method.getName(), ex);
                                }
                            }));
                        }
                    });
                }
            });
        }
        return bean;

    }
}

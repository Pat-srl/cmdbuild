/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.webapp.security;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterables.getFirst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.UiConfiguration;
import org.cmdbuild.fault.FaultEventCollectorService;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.webapp.services.ExceptionHandlerService.buildResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import static org.springframework.web.cors.CorsConfiguration.ALL;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 *
 * TODO: there is a lot of duplicated configuration code in this class; should
 * refactor to remove duplication.
 *
 * @author ataboga
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FaultEventCollectorService errorAndWarningCollectorService;
    private final AuthenticationProvider authenticationProvider;
    private final SessionTokenFilter sessionAuthenticationFilter;
    private final UiSessionTokenFilter uiAuthenticationFilter;
    private final LenientSessionTokenFilter lenientAuthenticationFilter;// use lenient filter, so an user can use sessions ws even if it does not have a default group set
    private final UiConfiguration uiConfiguration;

    public SecurityConfiguration(FaultEventCollectorService errorAndWarningCollectorService, AuthenticationProvider authenticationProvider, SessionTokenFilter sessionAuthenticationFilter, UiSessionTokenFilter uiAuthenticationFilter, LenientSessionTokenFilter lenientAuthenticationFilter, UiConfiguration uiConfiguration) {
        this.errorAndWarningCollectorService = errorAndWarningCollectorService;
        this.authenticationProvider = authenticationProvider;
        this.sessionAuthenticationFilter = sessionAuthenticationFilter;
        this.uiAuthenticationFilter = uiAuthenticationFilter;
        this.lenientAuthenticationFilter = lenientAuthenticationFilter;
        this.uiConfiguration = uiConfiguration;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.authenticationProvider(authenticationProvider).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return (HttpServletRequest request) -> {
            CorsConfiguration configuration = new CorsConfiguration();
            if (uiConfiguration.isCorsEnabled()) {
                logger.debug("enable cors for this request");
                try {
                    List<String> allowedOrigins = uiConfiguration.getCorsAllowedOrigins();
                    if (equal(getFirst(allowedOrigins, null), "auto")) {
                        String origin = request.getHeader("Origin"),
                                referer = request.getHeader("Referer");
                        if (isNotBlank(origin)) {
                            allowedOrigins = singletonList(origin);
                        } else if (isNotBlank(referer)) {
                            try {
                                URL url = new URL(referer);
                                url = new URL(url.getProtocol(), url.getHost(), url.getPort(), ""); //TODO improve this
                                allowedOrigins = singletonList(url.toString());
                            } catch (MalformedURLException ex) {
                                throw runtime(ex);
                            }
                        } else {
                            allowedOrigins = list(allowedOrigins).without("auto");
                        }
                    }
                    logger.debug("set cors allowed origins = {}", allowedOrigins);
                    configuration.setAllowedOrigins(allowedOrigins);
                    configuration.setAllowedMethods(asList(ALL));
                    configuration.setAllowedHeaders(asList(ALL));
                    configuration.setAllowCredentials(true);
                } catch (Exception ex) {
                    logger.error(marker(), "error preparing cors config", ex);
                }
            } else {
                logger.debug("cors is not enabled for this request");
            }
            return configuration;
        };
    }

    @Bean
    @Order(10)
    public SecurityFilterChain bootStatusSecurityConfig(HttpSecurity httpSecurity) throws Exception {
        return setExtendedConfigs(httpSecurity
                .securityMatcher("/services/rest/v*/boot/status**"))
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(20)
    public SecurityFilterChain bootSecurityConfig(HttpSecurity httpSecurity, BootAuthorizationManager authorization) throws Exception {
        return setExtendedConfigs(httpSecurity
                .securityMatcher("/services/rest/v*/boot/**"))
                .authorizeHttpRequests(authz -> authz.anyRequest().access(authorization))
                .build();
    }

    @Bean
    @Order(30)
    public SecurityFilterChain openApiSecurityConfig(HttpSecurity httpSecurity, OpenApiAuthorizationManager authorization) throws Exception {
        AuthenticationEntryPoint openApiAuthenticationEntryPoint = (request, response, e) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(toJson(map("success", false, "messages", "openapi not enabled")));
        };
        return setBasicConfigs(httpSecurity
                .securityMatcher("/services/v*/openapi/openapi.json", "/services/v*/openapi/openapi.yaml"))
//                .securityMatcher(
//                        "/services/v4/openapi/openApiV4.json"
//                        ,"/services/v4/openapi/openApiV4.yaml"
//                        ,"/services/v3/openapi/openApiV3.json"
//                        ,"/services/v3/openapi/openApiV3.yaml"
//                ))
                .authorizeHttpRequests(authz -> authz.anyRequest().access(authorization))
                .logout(LogoutConfigurer::disable)
                .exceptionHandling(e -> e.authenticationEntryPoint(openApiAuthenticationEntryPoint))
                .build();
    }

    @Bean
    @Order(40)
    public SecurityFilterChain wsSecurityConfigForRestSession(HttpSecurity httpSecurity) throws Exception {
        List<String> securityMatchers = list(
                "/services/rest/v*/sessions/**", //TODO cleanup this code; we actually need to intercept only PUT on session/{mysession} for session update on login, when the session is still invalid, and POST on sessions for session create
                "/services/custom-login", "/services/custom-login/**",
                "/services/saml/**",
                "/services/rest/v*/users/*/password", //password reset
                "/services/rest/v*/users/*/password/recovery", //password recovery
                "/services/rest/v*/configuration/languages", "/services/rest/v*/configuration/languages/", // do not throw warning if user is not logged with group
                "/services/rest/v*/configuration/public", "/services/rest/v*/configuration/public/" // do not throw warning if user is not logged with group
        );

        return setExtendedConfigs(httpSecurity
                .securityMatchers(authz -> authz.requestMatchers(securityMatchers.toArray(String[]::new))))
                .addFilterBefore(lenientAuthenticationFilter, BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.POST, "/services/rest/v*/sessions", "/services/rest/v*/sessions/").permitAll() //rest ws login; FIXME: the double syntax 'sessions','sessions/' is a workaround, maybe there is a better way :/
                .requestMatchers(HttpMethod.GET, "/services/rest/v*/sessions/current").permitAll() //check if session exists
                .requestMatchers(HttpMethod.GET, "/services/rest/v*/sessions/challenge").permitAll() //challenge for challenge-response rsa auth
                .requestMatchers(HttpMethod.PUT, "/services/rest/v*/users/*/password").permitAll() //password reset
                .requestMatchers(HttpMethod.POST, "/services/rest/v*/users/*/password/recovery").permitAll() //password recovery
                .requestMatchers(HttpMethod.GET, "/services/rest/v*/configuration/languages", "/services/rest/v*/configuration/languages/").permitAll() // required for login with sso or login group selection
                .requestMatchers(HttpMethod.GET, "/services/rest/v*/configuration/public", "/services/rest/v*/configuration/public/").permitAll() // required for login with sso or login group selection
                .requestMatchers("/services/custom-login", "/services/custom-login/**").permitAll()
                .requestMatchers("/services/saml/**").permitAll()
                .anyRequest().authenticated())
                .build();
    }

    @Bean
    @Order(50)
    public SecurityFilterChain wsSecurityConfig(HttpSecurity httpSecurity) throws Exception {
        return setExtendedConfigs(httpSecurity.securityMatcher("/services/**"))
                .addFilterBefore(sessionAuthenticationFilter, BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.POST, "/services/rest/v*/sessions", "/services/rest/v*/sessions/").permitAll() // rest ws login; FIXME: the double syntax 'sessions','sessions/' is a workaround, maybe there is a better way :/
                .requestMatchers("/services/rest/v*/resources/company_logo/**").permitAll()//company logo; TODO: replace with public resources ws
                .requestMatchers("/services/rest/v*/**").authenticated() // rest ws
                .requestMatchers("/services/geoserver/**").authenticated() // geoserver proxy
                .requestMatchers("/services/websocket/v1/main").authenticated() // websocket endpoint
                .requestMatchers("/services/etl/gate/public/**").permitAll()
                .requestMatchers("/services/etl/gate/private/**").authenticated()
                .anyRequest().denyAll()) //default deny
                .build();
    }

    @Bean
    @Order(60)
    public SecurityFilterChain uiSecurityConfig(HttpSecurity httpSecurity) throws Exception {
        return setBasicConfigs(httpSecurity)
                .addFilterBefore(uiAuthenticationFilter, BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                .requestMatchers("/ui/", "/ui/**", "/ui_dev/**", "/ui_dev/").permitAll() //ui
                .requestMatchers("/", "/index", "/error").permitAll()
                .anyRequest().denyAll()) //default deny
                .build();
    }

    private HttpSecurity setBasicConfigs(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(FrameOptionsConfig::sameOrigin));
    }

    private HttpSecurity setExtendedConfigs(HttpSecurity httpSecurity) throws Exception {
        AuthenticationEntryPoint authenticationEntryPoint = (request, response, e) -> {
            Map<String, Object> map = map("success", false, "messages", buildResponseMessages(errorAndWarningCollectorService.getCurrentRequestEventCollector().withError(e)));

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(toJson(map));
        };

        return setBasicConfigs(httpSecurity)
                .logout(LogoutConfigurer::disable)
                .exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint));
    }
}

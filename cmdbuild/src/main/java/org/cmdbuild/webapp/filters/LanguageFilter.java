/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.io.IOException;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.translation.RequestLanguageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Configuration("LanguageFilter")
public class LanguageFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RequestLanguageHolder holder;

    public LanguageFilter(RequestLanguageHolder holder) {
        this.holder = checkNotNull(holder);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        List<String> keys = list("force_lang", "force_language", "CMDBuild-Localization");
        Optional<String> lang = list(list(keys).map(request::getParameter))
                .with(list(keys).map(request::getHeader))
                .accept(l -> {
                    if (request.getCookies() != null) {
                        keys.forEach(key -> stream(request.getCookies()).filter(input -> input.getName().equalsIgnoreCase(key)).map(Cookie::getValue).distinct().collect(toOptional()).ifPresent(l::add));
                    }
                })
                .stream().filter(StringUtils::isNotBlank).findFirst();
        if (lang.isPresent()) {
            logger.debug("set request language = {}", lang.get());
            holder.setRequestLanguage(lang.get());
        }

        filterChain.doFilter(request, response);
    }

}

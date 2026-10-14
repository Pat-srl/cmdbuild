/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email;

import static com.google.common.base.Objects.equal;
import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.email.job.MapperConfig;

/**
 *
 * @author afelice
 */
public class EmailProcessingUtils {
    public static String processMapperExpr(MapperConfig config, String body, String expr) {
        if (isBlank(body) || isBlank(expr)) {
            return "";
        } else {
            Matcher matcher = Pattern.compile(format("%s(.*?)%s\\s*%s(.*?)%s", Pattern.quote(config.getKeyBegin()), Pattern.quote(config.getKeyEnd()), Pattern.quote(config.getValueBegin()), Pattern.quote(config.getValueEnd())), Pattern.DOTALL).matcher(body);
            while (matcher.find()) {
                if (equal(matcher.group(1), expr)) {
                    return matcher.group(2);
                }
            }
            return "";
        }
    }    
}

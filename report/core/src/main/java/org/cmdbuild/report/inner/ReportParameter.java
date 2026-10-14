package org.cmdbuild.report.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import static java.util.function.Function.identity;
import jakarta.annotation.Nullable;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.cmdbuild.common.Constants.DATETIME_TWO_DIGIT_YEAR_FORMAT;
import static org.cmdbuild.common.Constants.DATE_FOUR_DIGIT_YEAR_FORMAT;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import org.cmdbuild.dao.entrytype.AttributePermission;
import org.cmdbuild.dao.entrytype.AttributePermissionMode;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.PermissionScope;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import static org.cmdbuild.report.ReportConst.DUMMY_REPORT_PARAM_OWNER_CODE;
import org.cmdbuild.report.ReportException;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Wrapper for user-defined Jasper Parameter
 *
 * AVAILABLE FORMATS FOR JRPARAMETER NAME 1) reference: "label.class.attribute"
 * - ie: User.Users.Description 2) lookup: "label.lookup.lookuptype" - ie:
 * Brand.Lookup.Brands 3) simple: "label" - ie: My parameter
 *
 * Notes: - The description property overrides the label value - Reference or
 * lookup parameters will always be integers while simple parameters will match
 * original parameter class - All custom parameters are required; set a property
 * (in iReport) with name="required" and value="false" to override
 *
 */
public class ReportParameter {

    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final EntryType DUMMY_REPORT_PARAM_OWNER = ClasseImpl.builder().withId(0l).withName(DUMMY_REPORT_PARAM_OWNER_CODE).build();

    private final JRParameter jrParameter;
    private final String simpleName;
    private final CardAttributeType<?> cardAttributeType;

    private ReportParameter(JRParameter jrParameter, String simpleName, CardAttributeType<?> cardAttributeType) {
        this.jrParameter = checkNotNull(jrParameter);
        this.simpleName = checkNotBlank(simpleName);
        this.cardAttributeType = checkNotNull(cardAttributeType);
    }

    public static ReportParameter dummyParameter(String name) {
        return new ReportParameter(new JRDesignParameter() {
            {
                setName(name);
                setDescription(name);
            }
        }, name, new StringAttributeType(100)) {

            @Override
            public boolean isRequired() {
                return false;
            }
        };
    }

    public static ReportParameter parseJrParameter(JRParameter jrParameter) {
        LOGGER.debug("parse report parameter =< {} >", jrParameter.getName());
        String iReportParamName = checkNotBlank(jrParameter.getName());
        if (!iReportParamName.contains(".")) {
            AttributeMetadata attributeMetadata = propsToAttributeMetadata(jrParameter.getPropertiesMap());
            if (attributeMetadata.isLookup()) {
                return new ReportParameter(jrParameter, iReportParamName, new LookupAttributeType(attributeMetadata.getLookupType()));
            } else if (attributeMetadata.isForeignKey()) {
                return new ReportParameter(jrParameter, iReportParamName, new ForeignKeyAttributeType(attributeMetadata.getForeignKeyDestinationClassName()));
            } else {
                return new ReportParameter(jrParameter, iReportParamName, cardAttrTypeFromJrParameter(jrParameter));
            }
        } else {
            // LEGACY stuff
            if (!iReportParamName.matches("[\\w\\s]*\\.\\w*\\.[\\w\\s]*")) {
                throw new ReportException("invalid parameter format for legacy param = %s", jrParameter);
            }
            String[] split = iReportParamName.split("\\.");
            if (split[1].equalsIgnoreCase("lookup")) {
                return new ReportParameter(jrParameter, split[0], new LookupAttributeType(split[2]));
            } else {
                return new ReportParameter(jrParameter, split[0], new ForeignKeyAttributeType(split[1]));
            }
        }
    }

    public String getDefaultValue() {
        if (jrParameter.getDefaultValueExpression() != null) {
            GroovyShell shell = new GroovyShell();
            Script sc = shell.parse(jrParameter.getDefaultValueExpression().getText());
            Object result = sc.run();

            if (result != null) {
                if (jrParameter.getValueClass() == Date.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FOUR_DIGIT_YEAR_FORMAT);
                    return sdf.format(result);
                } else if (jrParameter.getValueClass() == Timestamp.class || jrParameter.getValueClass() == Time.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_TWO_DIGIT_YEAR_FORMAT);
                    return sdf.format(result);
                }
                return result.toString();
            }
        }
        return null;
    }

    public boolean hasDefaultValue() {
        return (jrParameter.getDefaultValueExpression() != null
                && jrParameter.getDefaultValueExpression().getText() != null && !jrParameter
                .getDefaultValueExpression().getText().equals(""));
    }

    public JRParameter getJrParameter() {
        return jrParameter;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getName() {
        return jrParameter.getName();
    }

    public String getDescription() {
        return defaultString(jrParameter.getDescription(), getSimpleName());
    }

    @Nullable
    public Object parseValue(@Nullable Object value) {
        return convert(value, jrParameter.getValueClass());
    }

    public CardAttributeType<?> getCardAttributeType() {
        return cardAttributeType;
    }

    public Attribute toCardAttribute() {
        return new ReportAttribute(cardAttributeType, this, 0);
    }

    public Attribute toCardAttribute(long id) {
        return new ReportAttribute(cardAttributeType, this, id);
    }

    public boolean isRequired() {
        return propsToAttributeMetadata(jrParameter.getPropertiesMap()).isMandatory();
    }

    public boolean isOptional() {
        return !isRequired();
    }

    private static CardAttributeType<?> cardAttrTypeFromJrParameter(JRParameter jrParameter) {
        Class valueClass = jrParameter.getValueClass();
        if (String.class.equals(valueClass)) {
            int length = firstNotNull(toIntegerOrNull(jrParameter.getPropertiesMap().getProperty("length")), 100);
            return length > 0 ? new StringAttributeType(length) : new StringAttributeType();
        } else if (set(Integer.class, Long.class, Short.class, BigDecimal.class, Number.class).contains(valueClass)) {
            return new IntegerAttributeType();
        } else if (Date.class.equals(valueClass)) {
            return new DateAttributeType();
        } else if (Timestamp.class.equals(valueClass)) {
            return new DateTimeAttributeType();
        } else if (Time.class.equals(valueClass)) {
            return new TimeAttributeType();
        } else if (set(Double.class, Float.class).contains(valueClass)) {
            return new DoubleAttributeType();
        } else if (Boolean.class.equals(valueClass)) {
            return new BooleanAttributeType();
        } else {
            throw new ReportException("invalid value class = %s", valueClass);
        }
    }

    //TODO filter parameters (?)
//        private static final String FILTER = "filter",
//                FILTER_PREFIX = FILTER + ".",
//        public Map<String, String> getFilterParameters() {
//            return map.entrySet().stream().filter(e -> e.getKey().startsWith(FILTER_PREFIX)).collect(toMap(e -> trim(e.getKey().substring(FILTER_PREFIX.length())), Entry::getValue));
//        } 
    private static class ReportAttribute implements Attribute {

        private final CardAttributeType<?> type;
        private final ReportParameter reportParameter;
        private final AttributeMetadata attributeMetadata;
        private final long reportId;

        public ReportAttribute(CardAttributeType<?> type, ReportParameter reportParameter, long reportId) {
            this.type = checkNotNull(type);
            this.reportParameter = checkNotNull(reportParameter);
            this.attributeMetadata = propsToAttributeMetadata(reportParameter.jrParameter.getPropertiesMap());
            this.reportId = reportId;
        }

        @Override
        public EntryType getOwner() {
            return reportId == 0 ? DUMMY_REPORT_PARAM_OWNER : ClasseImpl.builder().withId(reportId).withName(DUMMY_REPORT_PARAM_OWNER_CODE).build();
        }

        @Override
        public CardAttributeType<?> getType() {
            return type;
        }

        @Override
        public String getName() {
            return reportParameter.getName();
        }

        @Override
        public String getDescription() {
            return reportParameter.getDescription();
        }

        @Override
        public AttributePermissionMode getMode() {
            return AttributePermissionMode.APM_WRITE;
        }

        @Override
        public String getDefaultValue() {
            if (reportParameter.hasDefaultValue()) {
                return reportParameter.getDefaultValue();
            }
            return EMPTY;
        }

        @Override
        public AttributeMetadata getMetadata() {
            return attributeMetadata;
        }

        @Override
        public Map<PermissionScope, Set<AttributePermission>> getPermissionMap() {
            return getMetadata().getPermissionMap();
        }

    }

    private final static Map<String, String> JREPORTPROP_ATTRMETADATA_MAPPING = ImmutableMap.copyOf(map(
            "required", AttributeMetadata.MANDATORY,
            "lookupType", AttributeMetadata.LOOKUP_TYPE,
            "targetClass", AttributeMetadata.FK_TARGET_CLASS,
            "preselectIfUnique", AttributeMetadata.PRESELECT_IF_UNIQUE,
            "filter", AttributeMetadata.FILTER
    ));

    private static AttributeMetadata propsToAttributeMetadata(JRPropertiesMap propertiesMap) {
        return new AttributeMetadataImpl(propsToMap(propertiesMap).mapKeys(k -> JREPORTPROP_ATTRMETADATA_MAPPING.getOrDefault(k, k)));
    }

    private static FluentMap<String, String> propsToMap(JRPropertiesMap propertiesMap) {
        return list(propertiesMap.getPropertyNames()).stream().collect(toMap(identity(), propertiesMap::getProperty));
    }
}

/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.ExtendedClassDefinition;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FOR_USER;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_LOOKUP_VALUES;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.user.UserDaoService;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_CLASSE_WIDGETS_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_CLASSE_WIDGET_CONFIG_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_CLASSE_WIDGET_WIDGET_ID_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_ID_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_METADATA_SERIALIZATION;
import static org.cmdbuild.modeldiff.diff.schema.CmDmsModelSchemaNode.isDmsModel;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import static org.cmdbuild.sync.WsClassData_WithPermissionMode.ATTR_METADATA_DEFAULT_CLASSE_PERMISSION_MODE;
import static org.cmdbuild.sync.WsClassData_WithPermissionMode.CLASS_PERMISSION_MODE_METADATA_INTERNAL_KEY;
import static org.cmdbuild.sync.WsClassData_WithPermissionMode.fetchPermissionMode;
import static org.cmdbuild.sync.WsClassData_WithPermissionMode.isReserved;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.contextmenu.ContextMenuComponentService;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.widget.utils.WidgetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <b>Note</b>: can't be all based on ids, because if diffing different systems
 * ids won't match.
 *
 * <p>
 * <b>Node</b>: two modes <code>includeReserved</code>
 * <dl><dt><code>true</code>
 * <dd>loads even <code>reserved</code> {@link Classe}s; uses {@link UserDaoService#getAllClasses()
 * } and {@link UserDaoService#getClass(java.lang.String)};
 * <dt><code>false</code>
 * <dd>loads only <code>user</code> {@link Classe}s; uses
 * {@link UserClassService#getAllUserClasses()} and
 * {@link UserClassService#getUserClass(java.lang.String)}.
 * </dl>
 *
 * @author afelice
 */
@Component
public class ClasseSyncImpl implements ClasseSync {

    public static final String LOOKUP_NOT_FOUND_ISSUE8422_EXC_MSG = "lookup not found for id =";
    public static final Pattern idPattern = Pattern.compile("id = (\\d+)");
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final ObjectMapper OBJECT_MAPPER = CmJsonUtils.getObjectMapper();

    private final UserDaoService daoClassService;
    private final UserClassService classService;
    private final ClassSerializationHelper classHelper;
    private final ClasseRepository classeRepository;
    
    private final ContextMenuComponentService contextMenuComponentService;

    public ClasseSyncImpl(UserDaoService daoClassService, UserClassService classService, ClassSerializationHelper classHelper, ClasseRepository classeRepository,
                ContextMenuComponentService contextMenuComponentService) {
        this.daoClassService = daoClassService;
        this.classService = checkNotNull(classService);
        this.classHelper = checkNotNull(classHelper);
        this.classeRepository = checkNotNull(classeRepository);
        this.contextMenuComponentService = checkNotNull(contextMenuComponentService);
    }

    /**
     *
     * @param classeName
     * @param includeReserved
     * @return
     */
    @Override
    public Classe read(String classeName, boolean includeReserved) {
        if (includeReserved) {
            return daoClassService.getClasse(classeName); // To load even RESERVED (system) classes, used in OpenMaint and Connectors (User and Role)
        } else {
            return classService.getUserClass(classeName);
        }
    }

    @Override
    public ExtendedClass readExtended(String classeName, boolean includeReserved) {
        try {
            if (includeReserved) {
                return classService.getExtendedClass(classeName, CQ_INCLUDE_LOOKUP_VALUES); // To load even RESERVED (system) classes without permission set 
            } else {
                return classService.getExtendedClass(classeName, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES);
            }
        } catch(NullPointerException excNull) {
            final String expMsg = excNull.getMessage();
            if (expMsg.startsWith(LOOKUP_NOT_FOUND_ISSUE8422_EXC_MSG)) {
                throwLookupErr(expMsg, classeName);
            }
            
            throw excNull;
        }
    }

    /**
     * Used for <code>FORMULA</code> {@link Attribute} workaround.
     * 
     * @param classeName
     * @return 
     */
    @Override
    public ExtendedClass readExtented_Fresh(String classeName) {
        classService.invalidateCache_Class(classeName);
        return readExtended(classeName, true);
    }

    @Override
    public ExtendedClass readExtended(Classe classe, boolean includeReserved) {
        return readExtended(classe.getName(), includeReserved);
    }

    @Override
    public List<Classe> readAll(boolean includeReserved) {
        if (includeReserved) {
            return daoClassService.getAllClasses(); // To load even RESERVED (system) classes, used in OpenMaint and Connectors (User and Role)
        } else {
            return classService.getAllUserClasses();
        }
    }

    @Override
    // @todo AFE TBC
    public List<Classe> readAll(boolean includeInactiveElements, boolean includeLookupValues, String filterStr) {
        // @todo AFE come in Class.readAll()
//        List list = (isAdminViewMode(viewMode) ? dao.getAllClasses().stream() : dao.getAllClasses().stream().filter(Classe::isActive)).filter(Classe::isDmsModel)
//                .map(detailed ? compose(helper::buildFullDetailExtendedResponse, classService::getExtendedClass) : helper::buildBasicResponse).collect(toList());
//
//        //TODO duplicate code with class ws, improve this
//        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
//        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
//        if (filter.hasAttributeFilter()) {
//            list = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, m) -> toStringOrNull(m.get(k))).withFilter(filter.getAttributeFilter()).filter(list);
//        }
        return list();
    }
    
    /**
     * Needed to handle <i>JS Components</i> in <i>context menu items</i> for {@link Classe}.
     * 
     * @see <a href="http://gitlab.example.com/cmdbuild/cmdbuild/-/issues/8342">#8342 - backend: support to synchronization of CMDBuild model - handle Classe JSComponent inside Classe contextMenuItems</a>
     * @param componentId
     * @param jsComponentName
     * @return 
     */
    @Override
    public boolean hasContextMenuItem_JSComponent(String componentId, String jsComponentName) {
        try {
            // Raises NullPointerException with message "ui component not found for type = UCT_CONTEXTMENU name =< qrcodegenerator >"
            // if JS component not installed in system
            UiComponentInfo uiComponent = ((UiComponentInfo) contextMenuComponentService.getByCode(componentId));
            
            return uiComponent.getExtjsComponentId().equals(jsComponentName);
        } catch (NullPointerException excNull) {
            // Do nothing 
            return false;
        }
    }    

    @Override
    public ExtendedClass add(String classeName, Map<String, Object> classeCmdbSerialization) {
        WsClassData classeData = buildClasseData(classeCmdbSerialization, true);
        if (isDmsModel(classeCmdbSerialization) || isReserved(classeCmdbSerialization)) {
            // Dms model or a reserved/protected Classe or a Dms model
            return classService.createClass_Reserved(classHelper.extendedClassDefinitionForNewClass(classeData));
        } else {
            // A normal user Classe
            // See ClassWs.create()
            return classService.createClass(classHelper.extendedClassDefinitionForNewClass(classeData));
        }
        // @todo AFE add Classe description translation: _description_translation, _description_plural_translation; per property "attributeGroups": _description_translation         
    }

    @Override
    public ExtendedClass update(String classeName, Map<String, Object> classeCmdbSerialization) {
        logger.debug("Classe =< {} >: from input classeCmdbSerialization its permission mode is {}", classeName, fetchPermissionMode(classeCmdbSerialization));
        WsClassData classeData = buildClasseData(classeCmdbSerialization, true);
        
        // Handles:
        // - reserved/protected Classe;
        // - description translation.
        return update(classeName, classeData); 
    }

    @Override
    public ExtendedClass deactivate(Classe classe) {
        Classe deactivatedClasse = ClasseImpl.copyOf(classe)
                .withMetadata(
                        ClassMetadataImpl.copyOf(classe.getMetadata())
                                .withActive(false)
                                .build()
                ).build();
        return update(classe.getName(), buildClasseData(deactivatedClasse, true));
    }

    @Override
    public void remove(Classe classe) {
        // @todo AFE as in UserClasseService.deleteClass()
        // checkArgument(classe.hasServiceModifyPermission(), "CM: permission denied: user not authorized to drop class");
        // TBC:
//        formTriggerService.deleteForClass(classe);
//        contextMenuService.deleteForClass(classe);
//        widgetService.deleteForClass(classe);
        classeRepository.deleteClass(classe);
    }

    @Override
    public FluentMap<String, Object> serializeClasseProps(ExtendedClass extendedClass) {
        return fixWidgetSerialization(classHelper.buildFullDetailExtendedResponse(extendedClass));
    }
    
    /**
     * Put <code>WidgetId</code> tag and in <code>_config</code> so is not loosed
     * and substituted by dynamically generated one in {@link WidgetUtils#toWidgetData(String)}
     * 
     * @param curClasseCmdbSerialization
     * @return 
     */
    @Override
    public FluentMap<String, Object> fixWidgetSerialization(FluentMap<String, Object> curClasseCmdbSerialization) {
        if (!curClasseCmdbSerialization.containsKey(ATTR_CLASSE_WIDGETS_SERIALIZATION)) {
            return curClasseCmdbSerialization;
        }
        
        List<Map<String, Object>> foundWidgetsSer = (List<Map<String, Object>>)curClasseCmdbSerialization.get(ATTR_CLASSE_WIDGETS_SERIALIZATION);
        if (foundWidgetsSer.isEmpty()) {
            return curClasseCmdbSerialization;
        }
        
        foundWidgetsSer.stream().forEach(w -> {
            if (!w.containsKey(ATTR_CLASSE_WIDGET_WIDGET_ID_SERIALIZATION)) {
                final String id = (String)w.get(ATTR_ID_SERIALIZATION);
                // Add WidgetId to synthesized first level props
                w.put(ATTR_CLASSE_WIDGET_WIDGET_ID_SERIALIZATION, id);
                
                // Add WidgetId to serialized _config, too
                String configStr = (String) w.get(ATTR_CLASSE_WIDGET_CONFIG_SERIALIZATION);               
                Map<String, Object> configMap = map(WidgetUtils.parseSerializedWidgetData(configStr));  
                
                // enclose widget id in double quotes, otherwise will be interpreted as a Groovy script
                configMap.put(ATTR_CLASSE_WIDGET_WIDGET_ID_SERIALIZATION, "\"%s\"".formatted(id)); 
                // Use alphabetic order to ensure easy diffing
                configStr = WidgetUtils.serializeWidgetDataToString(new TreeMap<>(configMap));
                w.put(ATTR_CLASSE_WIDGET_CONFIG_SERIALIZATION, configStr);
            }
        });
                
        return curClasseCmdbSerialization;
    }
    
    /**
     * Handles:
     * <ol>
     * <li><i>reserved</i>/<i>protected</i> {@Classe};
     * <li>
     * </ol>
     * @param classeName
     * @param classeData
     * @param bReservedClasse
     * @return 
     */
    private ExtendedClass update(String classeName, WsClassData classeData) {
        Classe classe = read(classeName, true);
        final ExtendedClassDefinition extendedClassDefinition = classHelper.extendedClassDefinitionForExistingClass(classe, classeData);
        logger.debug("Classe =< {} >: from (syntesized) extendedClassDefinition its permission mode is {}", classeName, extendedClassDefinition.getClassDefinition().getMetadata().getMode());        
        if (classe.isDmsModel() || isReserved(extendedClassDefinition.getClassDefinition().getMetadata())) {
            // A Dms model or a reserved/protected Classe
            return classService.updateClass_Reserved(extendedClassDefinition);
        } else {
            // A normal Classe
            // See ClassWs.update()
            return classService.updateClass(extendedClassDefinition);
        }
        
        // @todo AFE add classe description translation: 
        // - _description_translation, _description_plural_translation;
        // - inside attributeGoups: _description_translation, _description_plural_translation.
    }

    private WsClassData buildClasseData(Classe classe, boolean includeReserved) {
        return buildClasseData(classHelper.buildFullDetailResponse(classe), includeReserved);
    }

    /**
     * {@link Classe} <i>metadata</i> is exploded in CMDBuild serialization and
     * needs to be build again before <i>adding</i>/<i>updating</i>.
     *
     * @param classeCmdbSerialization
     * @return
     */
    private WsClassData buildClasseData(Map<String, Object> classeCmdbSerialization, boolean includeReserved) {
        if (includeReserved) {
            // Workaround to serialize {@link ClassPermissionMode} without modifying the
            // other existing CMDBuild services code.
            Map<String, Object> fixedClasseCmdbSerialization = fixClassePermisisonMode(classeCmdbSerialization);
            // Reconstructs metadata from Classe serialization        
            return getSystemObjectMapper().convertValue(fixedClasseCmdbSerialization, WsClassData_WithPermissionMode.class);
        } else {
            // Reconstructs metadata from Classe serialization        
            return getSystemObjectMapper().convertValue(classeCmdbSerialization, WsClassData.class);
        }
    }

    private Map<String, Object> fixClassePermisisonMode(Map<String, Object> classeCmdbSerialization) {
        // @todo AFE check che crei anche ClassPermissionMode a ALL

        Map<String, Object> fixedClasseCmdbSerialization = map(classeCmdbSerialization).with(CLASS_PERMISSION_MODE_METADATA_INTERNAL_KEY, ATTR_METADATA_DEFAULT_CLASSE_PERMISSION_MODE);
        if (classeCmdbSerialization.get(ATTR_METADATA_SERIALIZATION) != null) {
            Map<String, String> curMetadataSerialization = (Map<String, String>) classeCmdbSerialization.get(ATTR_METADATA_SERIALIZATION);
            final String curClassePermissionModeSerialization = curMetadataSerialization.get(CLASS_PERMISSION_MODE_METADATA_INTERNAL_KEY);
            if (curClassePermissionModeSerialization != null) {
                fixedClasseCmdbSerialization.put(CLASS_PERMISSION_MODE_METADATA_INTERNAL_KEY, curMetadataSerialization.get(CLASS_PERMISSION_MODE_METADATA_INTERNAL_KEY));
            }
        }

        return fixedClasseCmdbSerialization;
    }

    private ObjectMapper getSystemObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * Transform a {@link NullPointerException} with message 
     * <pre>
     * lookup not found for id = 150809
     * </pre>
     * 
     * into a {@link RuntimeException} with message
     * <pre>
     * error loading Classe =< class_name > - check if LookupValue with id = 150809 has Status N
     * </pre>
     * @param expMsg
     * @param classeName
     * @throws RuntimeException 
     */
    private void throwLookupErr(final String expMsg, String classeName) throws RuntimeException {
        Matcher idMatcher = idPattern.matcher(expMsg);
        String newErrMsg = "error loading Classe =< %s > - check if LookupValue Status is N - %s".formatted(classeName, expMsg);
        if (idMatcher.find()) {
            String foundId = idMatcher.group(1);
            newErrMsg = "error loading Classe =< %s > - check if related LookupValue with id = %s has Status N".formatted(classeName, foundId);
        }
        throw runtime(newErrMsg);
    }    
    
} // end ClasseSyncImpl class

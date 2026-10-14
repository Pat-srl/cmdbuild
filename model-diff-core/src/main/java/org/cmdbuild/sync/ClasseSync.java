/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.List;
import java.util.Map;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.widget.utils.WidgetUtils;

/**
 *
 * @author afelice
 */
public interface ClasseSync {
    
    Classe read(String classeName, boolean includeReserved);
    
    default Classe read(String classeName) {
        return read(classeName, false);
    }

    /**
     * Needed to calculate {@link Classe} props serialization, @see {@link #serializeClasseProps(org.cmdbuild.classe.ExtendedClass)
     * }.
     *
     * @param classeName
     * @param includeReserved 
     * <dl><dt><code>true</code>
     *  <dd>loads even <code>reserved</code> {@link Classe}s;
     * <dt><code>false</code>
     *  <dd>loads only <code>user</code> {@link Classe}s;
     * </dl>
     * @return
     */
    ExtendedClass readExtended(String classeName, boolean includeReserved);
    
    default ExtendedClass readExtended(String classeName) {
        return readExtended(classeName, false);
    }
    
    ExtendedClass readExtented_Fresh(String classeName);    
    
    /**
     * Wrapper to read {@link ExtendedClass} from system.
     *
     * @param classe
     * @param includeReserved 
     * <dl><dt><code>true</code>
     *  <dd>loads even <code>reserved</code> {@link Classe}s;
     * <dt><code>false</code>
     *  <dd>loads only <code>user</code> {@link Classe}s;
     * </dl>
     * @return
     */
    ExtendedClass readExtended(Classe classe, boolean includeReserved);

    /**
     * 
     * @param classe 
     * @return 
     */
    default ExtendedClass readExtended(Classe classe) {
        return readExtended(classe, false);
    }
    
    List<Classe> readAll(boolean includeReserved);
    
    default List<Classe> readAll() {
        return readAll(false);
    }

    /**
     * @see {@link ClassWs#readAll()}
     *
     * @param includeInactiveElements
     * @param includeLookupValues
     * @param filterStr
     * @return
     */
    // @todo AFE TBC
    List<Classe> readAll(boolean includeInactiveElements, boolean includeLookupValues, String filterStr);

    boolean hasContextMenuItem_JSComponent(String componentId, String jsComponentName);
    
    ExtendedClass add(String classeName, Map<String, Object> classeCmdbSerialization);

    ExtendedClass update(String classeName, Map<String, Object> classeCmdbSerialization);

    ExtendedClass deactivate(Classe classe);
    
    // @todo AFE TBC
    void remove(Classe classe);

    FluentMap<String, Object> serializeClasseProps(ExtendedClass extendedClass);
    
    /**
     * Put <code>WidgetId</code> tag and in <code>_config</code> so is not loosed
     * and substituted by dynamically generated one in {@link WidgetUtils#toWidgetData(String)}
     * 
     * @param curClasseCmdbSerialization
     * @return 
     */
    public FluentMap<String, Object> fixWidgetSerialization(FluentMap<String, Object> curClasseCmdbSerialization);    
}

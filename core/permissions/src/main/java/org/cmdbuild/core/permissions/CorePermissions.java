/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.cmdbuild.core.permissions;

/**
 *
 * @author afelice
 */
public class CorePermissions {

    // @todo AFE spostare in questo modulo le segueti classi da core-commons:
    // package class: 
    //  1. spostare qui ClassPermission* ed enum relative
    //
    //  2. creare classe ClassPermissionsHanler in cui mettere metodi <nomeClasse>_<operazione> per:
    //    - UserDomainServiceImpl
    //    - UserClassServiceImpl
    //    - EntryTypeMetadataImpl
    //    - ClassImpl
    //    - UserClassUtils
    //    - DaoPermissionUtils
    //    - DomainImpl
    //    - CardWsSerializationHelperv3
    //
    // package attribute: 
    //  3. spostare qui AttributePermission* ed enum relative
    // 
    //  4. creare classe AttributePermissionsHandler in cui mettere metodi <nomeClasse>_<operazione> per:
    //     - UserClassUtils
    //     - AttributeMetadataImpl
    //     - AttributeImpl
    //     - AttrobiteWithoutOwnerImpl
    //     - DaoPermissionsUtils
    //
    // 5. creare unit test per tutti i metodi ed i flussi che li usano, isolando l'init della classe ClassPermissionsHandler/AttributePermissionsHanlder:
    //    usare opportune classi MockClassPermissionsHandler/MockAttributePermissionsHandler
    // 6. identificare nomi più parlanti per i metodi del punto 2. e 4., se possibile, che identifichino operazioni su permessi invece che sull'oggetto 
    // 
    // 7. Per la serializzazione, separare le property legate ai permessi (can_read, can_write, etc.) in serializzatori appositi qui in core-permissions)
    
    public static void main(String[] args) {
        System.out.println("Hello World!");        
    }
}

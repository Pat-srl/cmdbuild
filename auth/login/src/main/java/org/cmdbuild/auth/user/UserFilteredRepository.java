/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.auth.user;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;

/**
 * Placeholder per partizionare {@link UserRepository} dalle classi <code>dao</code>, in particolar modo {@link DaoQueryOptions},
 * in modo da poter spostare {@link UserRepository} nel modulo <code>cmdbuild-core-commons</code> (usato da 
 * {@link NotificationProviderAdapter}
 * 
 * @author afelice
 */
public interface UserFilteredRepository extends UserRepository {
    
    PagedElements<UserData> getMany(DaoQueryOptions queryOptions);
}

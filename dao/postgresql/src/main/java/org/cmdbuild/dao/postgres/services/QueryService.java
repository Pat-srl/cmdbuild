/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Function;

public interface QueryService {

	<T> List<T> query(String query, Function<ResultSet, T> rowHandler);

}

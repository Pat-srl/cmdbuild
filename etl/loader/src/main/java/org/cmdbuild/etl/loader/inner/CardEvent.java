/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.etl.loader.inner;

import java.util.Map;
import org.cmdbuild.dao.beans.DatabaseRecord;

public interface CardEvent {

    DatabaseRecord getCard();

    Map<String, Object> getRecord();

}

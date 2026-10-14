/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.diagram;

import java.util.Collection;
import jakarta.activation.DataSource;

public interface DiagramService {

    DataSource renderDatabaseDiagram(Collection<String> classes);

}

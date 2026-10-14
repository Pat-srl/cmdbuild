/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

public interface EtlTemplate extends EtlTemplateConfig, EtlTemplateReference {

    @Override
    default boolean isDynamic() {
        return false;
    }
}

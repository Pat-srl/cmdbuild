/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.api.fluent.ws;

public interface AttrTypeVisitor {

    void visit(ClassAttribute classAttribute);

    void visit(FunctionInput functionInput);

    void visit(FunctionOutput functionOutput);

}

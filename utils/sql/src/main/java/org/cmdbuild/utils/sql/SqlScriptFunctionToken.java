/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sql;

import java.util.List;
import static org.cmdbuild.utils.io.CmIoUtils.readLines;

public interface SqlScriptFunctionToken {

    String getUnparsedTextBeforeFunctionToken();

    String getFunctionName();

    String getFunctionSignature();

    String getFunctionDefinition();

    default List<String> getUnparsedLinesBeforeFunctionToken() {
        return readLines(getUnparsedTextBeforeFunctionToken());
    }
}

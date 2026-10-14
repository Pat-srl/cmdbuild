/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import org.cmdbuild.utils.cli.utils.CliCommand;
import static org.cmdbuild.utils.cli.utils.CliUtils.hasInteractiveConsole;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class SchemaCollectorCommandRunner extends AbstractRestCommandRunner {

    public SchemaCollectorCommandRunner() {
        super(list("schema"), "manage collect/diff/merge of CMDBuild internal schema (Classes, Processes, Lookups, Dms models&cetegories). See https://tinyurl.com/schemacollectorhelpcli4");
    }

    @CliCommand
    protected void test(String msg) {
        String cmdName = "test";
        if (hasInteractiveConsole()) {
            System.out.println("=== STARTING %s: msg =< %s > ===".formatted(cmdName, msg));
        }        
        
        String returnMsg = login().schemaCollector().test(msg);
        if (hasInteractiveConsole()) {
            System.out.println("%1$s: result obtained =< %2$s >\n=== END %1$s ===".formatted(cmdName, returnMsg));
        } else {
            System.out.println(returnMsg);
        }
        
    }
    
    @CliCommand
    protected void collectSchema(String curSystemMnemonicName, String curSystemId) {
        String cmdName = "collectSchema";
        if (hasInteractiveConsole()) {
            System.out.println("=== STARTING %s: mnenomic name =< %s >, id =< %s > ===".formatted(cmdName, curSystemMnemonicName, curSystemId));
        }

        String createdJsonFilename = login().schemaCollector().collectSchema(curSystemMnemonicName, curSystemId);
        if (hasInteractiveConsole()) {
            System.out.println("%1$s: result written to file ===\n%2$s\n=== END %1$s ===".formatted(cmdName, createdJsonFilename));
        } else {
            System.out.println(createdJsonFilename);
        }
    }

    @CliCommand
    protected void compareSchema(String otherSchemaSerialization, String curSystemMnemonicName) {
        String cmdName = "compareSchema";
        if (hasInteractiveConsole()) {
            System.out.println("=== STARTING %s: \nother schema =< %s >\ncur system mnemonic name =< %s >\n===".formatted(cmdName, otherSchemaSerialization, curSystemMnemonicName));
        }

        String createdJsonFilename = login().schemaCollector().compareSchema(otherSchemaSerialization, curSystemMnemonicName);
        if (hasInteractiveConsole()) {
            System.out.println("%1$s: result written to file ===\n%2$s\n=== END %1$s ===".formatted(cmdName, createdJsonFilename));
        } else {
            System.out.println(createdJsonFilename);
        }
    }

    @CliCommand
    protected void compareSchemaBetween(String newSchemaSerialization, String aSchemaSerialization) {
        String cmdName = "compareSchemaBetween";
        if (hasInteractiveConsole()) {
            System.out.println("=== STARTING %s: \nmew schema =< %s >\na schema name =< %s >\n===".formatted(cmdName, newSchemaSerialization, aSchemaSerialization));
        }

        String createdJsonFilename = login().schemaCollector().compareSchemaBetween(newSchemaSerialization, aSchemaSerialization);
        if (hasInteractiveConsole()) {
            System.out.println("%1$s: result written to file ===\n%2$s\n=== END %1$s ===".formatted(cmdName, createdJsonFilename));
        } else {
            System.out.println(createdJsonFilename);
        }
    }
    
        @CliCommand
    protected void applySchemaDiff(String diffSchemaSerialization) {
        String cmdName = "applySchemaDiff";
        if (hasInteractiveConsole()) {
            System.out.println("=== STARTING %s: \ndiff schema =< %s >===".formatted(cmdName, diffSchemaSerialization));        
        }
        
        String createdJsonFilename = login().schemaCollector().applySchemaDiff(diffSchemaSerialization);        
        if (hasInteractiveConsole()) {
            System.out.println("%1$s: result written to file ===\n%2$s\n=== END %1$s ===".formatted(cmdName, createdJsonFilename));
        } else {
            System.out.println(createdJsonFilename);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CliCommandUtils {

    public static void executeAction(Map<String, CliAction> actions, Iterator<String> iterator) {
        prepareAction(actions, iterator).execute();
    }

    public static ExecutableAction prepareAction(Map<String, CliAction> actions, Iterator<String> iterator) {
        checkArgument(iterator.hasNext(), "invalid command line: no action specified");
        String name = iterator.next().toLowerCase();
        List<String> params = list(iterator);
        String actionName = format("%s_%s", name, params.size());
        CliAction action = checkNotNull(actions.get(actionName), "method not found for name =< %s > param count = %s", name, params.size());
        return new ExecutableAction() {
            @Override
            public CliAction getAction() {
                return action;
            }

            @Override
            public void execute() {
                action.execute(params);
            }
        };
    }

    public interface ExecutableAction {

        CliAction getAction();

        void execute();
    }
}

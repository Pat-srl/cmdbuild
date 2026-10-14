/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.xpdl;

import static com.google.common.base.Predicates.not;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.task.ScriptTaskExtraAttr;
import org.cmdbuild.workflow.river.engine.task.TaskImpl;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XpdlConditionScriptBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Pair<RiverTask, Set<String>> buildOutgoingHandlerWithConditions(String planId, String defaultScriptType, String fromActivityId, Collection<SimpleTransitionData> conditionalTransitions) {
        Set<String> stepTransitionIds = set();
        StringBuilder conditionScriptBuilder = new StringBuilder();
        conditionScriptBuilder.append(format("%s = new ArrayList();\n\n", NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR));
        conditionalTransitions.stream().filter(not(SimpleTransitionData::hasOtherwiseCondition)).forEach((transition) -> {
//            checkArgument(equal(transition.getConditionScriptType(), SCRIPT_LANGUAGE_BEANSHELL), "cannot process outgoing confition %s with script type %s", transition.getFlagId(), transition.getConditionScriptType());
            stepTransitionIds.add(transition.getFlagId());
            conditionScriptBuilder.append(format("if ( %s ) {\n\t%s.add(\"%s\");\n}\n\n", normalizeConditionScript(transition.getConditionScript()), NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR, transition.getFlagId()));
        });
        conditionalTransitions.stream().filter(SimpleTransitionData::hasOtherwiseCondition).forEach((transition) -> {
            stepTransitionIds.add(transition.getFlagId());
            conditionScriptBuilder.append(format("if ( %s.isEmpty() ) {\n\t%s.add(\"%s\");\n}\n", NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR, NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR, transition.getFlagId()));//TODO add unit test for this case, for every script engine
        });
        String conditionScript = conditionScriptBuilder.toString();
        LOGGER.debug("built outgoing handler condition script = \n\n{}\n", conditionScript);

        RiverTask conditionTask = TaskImpl.inline()
                .withPlanId(planId)
                .withTaskId("check_" + fromActivityId)
                .withExtraAttr(new ScriptTaskExtraAttr(defaultScriptType, conditionScript))
                .build();
        return Pair.of(conditionTask, stepTransitionIds);
    }

    private static String normalizeConditionScript(String scriptContent) {
        return scriptContent.replaceAll("[\n\r \t;]+$", "");
    }
}

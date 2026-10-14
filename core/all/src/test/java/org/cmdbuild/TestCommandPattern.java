/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class TestCommandPattern {

    @Test
    public void commandPattern() {
        //arrange:
        CommandExecutor instance = new CommandExecutor();
        List<CommandOnMessage> both = Arrays.asList(new CommandOne(), new CommandTwo());
        List<CommandOnMessage> toNull = Arrays.asList(new CommandOne(), new CommandNull(), new CommandTwo());

        //act:
        Message resultBoth = instance.execute(new Message("a"), both);
        Message resultNull = instance.execute(new Message("b"), toNull);

        //assert:
        assertEquals("aOneTwo", resultBoth.value);
        assertNull(resultNull);
    }
}

/**
 * Command pattern
 *
 * @author afelice
 */
class CommandExecutor {
    Message execute(Message initialMessage, List<CommandOnMessage> commands) {
        Message partial = initialMessage;
        for (CommandOnMessage command : commands) {
            partial = command.doSomething(partial);
            if (partial == null) { // Exit condition
                return null;
            }
        }

        return partial;
    }
}

interface CommandOnMessage {
    Message doSomething(Message msg);
}

class CommandOne implements CommandOnMessage {
    @Override
    public Message doSomething(Message msg) {
        return new Message(msg.value + "One");
    }
}

class CommandTwo implements CommandOnMessage {
    @Override
    public Message doSomething(Message msg) {
        return new Message(msg.value + "Two");
    }
}

class CommandNull implements CommandOnMessage {
    @Override
    public Message doSomething(Message msg) {
        return null;
    }
}

class Message {

    String value;

    Message(String value) {
        this.value = value;
    }
}

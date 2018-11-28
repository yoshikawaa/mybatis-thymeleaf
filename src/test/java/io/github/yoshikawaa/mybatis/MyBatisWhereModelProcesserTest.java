package io.github.yoshikawaa.mybatis;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.thymeleaf.context.Context;

public class MyBatisWhereModelProcesserTest extends AbstractProcesserTest {

    @Test
    public void test1() {

        Context context = new Context();

        String text = engine.process("select", context);
        loggingResult(text);

        assertThat(toList(text), allOf(
                not(hasItem("WHERE")),
                not(hasItem("   first_name = Atsushi ")),
                not(hasItem("   AND last_name = Yoshikawa "))
                        ));
    }

    @Test
    public void test2() {

        Context context = new Context();
        context.setVariable("firstName", "Atsushi");
        context.setVariable("lastName", "Yoshikawa");

        String text = engine.process("select", context);
        loggingResult(text);
        
        assertThat(toList(text), allOf(
                hasItem("WHERE"),
                hasItem("   first_name = Atsushi "),
                hasItem("   AND last_name = Yoshikawa ")
                        ));
    }

    @Test
    public void test3() {

        Context context = new Context();
        context.setVariable("firstName", "Atsushi");

        String text = engine.process("select", context);
        loggingResult(text);

        assertThat(toList(text), allOf(
                hasItem("WHERE"),
                hasItem("   first_name = Atsushi "),
                not(hasItem("   AND last_name = Yoshikawa "))
                        ));
    }

    @Test
    public void test4() {

        Context context = new Context();
        context.setVariable("lastName", "Yoshikawa");

        String text = engine.process("select", context);
        loggingResult(text);

        assertThat(toList(text), allOf(
                hasItem("WHERE"),
                not(hasItem("   first_name = Atsushi ")),
                hasItem("    last_name = Yoshikawa ")
                        ));
    }

}

package io.github.yoshikawaa.mybatis;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.thymeleaf.context.Context;

public class MyBatisSetModelProcesserTest extends AbstractProcesserTest {

    @Test
    public void test1() {

        Context context = new Context();
        context.setVariable("id", "001");

        String text = engine.process("update", context);
        loggingResult(text);

        assertThat(toList(text), allOf(
                not(hasItem("   first_name = Atsushi, ")),
                not(hasItem("   last_name = Yoshikawa "))
                        ));
    }

    @Test
    public void test2() {

        Context context = new Context();
        context.setVariable("id", "001");
        context.setVariable("firstName", "Atsushi");
        context.setVariable("lastName", "Yoshikawa");

        String text = engine.process("update", context);
        loggingResult(text);
        
        assertThat(toList(text), allOf(
                hasItem("   first_name = Atsushi, "),
                hasItem("   last_name = Yoshikawa ")
                        ));
    }

    @Test
    public void test3() {

        Context context = new Context();
        context.setVariable("id", "001");
        context.setVariable("firstName", "Atsushi");

        String text = engine.process("update", context);
        loggingResult(text);

        assertThat(toList(text), allOf(
                hasItem("   first_name = Atsushi "),
                not(hasItem("   last_name = Yoshikawa "))
                        ));
    }

    @Test
    public void test4() {

        Context context = new Context();
        context.setVariable("id", "001");
        context.setVariable("lastName", "Yoshikawa");

        String text = engine.process("update", context);
        loggingResult(text);

        assertThat(toList(text), allOf(
                not(hasItem("   first_name = Atsushi, ")),
                hasItem("   last_name = Yoshikawa ")
                        ));
    }

}

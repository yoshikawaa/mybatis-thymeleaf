package io.github.yoshikawaa.mybatis;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class MyBatisSetModelProcesserTest {

    private static final TemplateEngine engine = new TemplateEngine();

    @BeforeClass
    public static void setupBeforeClass() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/sql/");
        templateResolver.setSuffix(".sql");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCharacterEncoding("UTF8");
        templateResolver.setCheckExistence(true);
        templateResolver.setCacheable(false);
        engine.setTemplateResolver(templateResolver);
        engine.setAdditionalDialects(Collections.singleton(new MybatisDialect()));
    }

    @Test
    public void test1() {

        Context context = new Context();
        context.setVariable("id", "001");

        String text = engine.process("update", context);
        System.out.println(text);

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
        System.out.println(text);
        
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
        System.out.println(text);

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
        System.out.println(text);

        assertThat(toList(text), allOf(
                not(hasItem("   first_name = Atsushi, ")),
                hasItem("   last_name = Yoshikawa ")
                        ));
    }

    private List<String> toList(String text) {
        return Arrays.stream(text.split("(\\r\\n|\\r|\\n)"))
                .filter(t -> t.trim().length() > 0)
                .collect(Collectors.toList());
    }

}

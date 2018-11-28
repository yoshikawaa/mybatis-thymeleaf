package io.github.yoshikawaa.mybatis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class AbstractProcesserTest {

    @Rule
    public TestName testName = new TestName();
    
    protected final TemplateEngine engine = new TemplateEngine();

    @Before
    public void setupBeforeClass() {
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

    protected void loggingResult(String text) {
        System.out.println("test -> " + this.getClass().getSimpleName() + ":" + testName.getMethodName());
        System.out.println("result ->");
        System.out.println(text);
    }
    
    protected List<String> toList(String text) {
        return Arrays.stream(text.split("(\\r\\n|\\r|\\n)"))
                .filter(t -> t.trim().length() > 0)
                .collect(Collectors.toList());
    }

}

package io.github.yoshikawaa.mybatis;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

public class MybatisDialect extends AbstractProcessorDialect {

    private static final String DIALECT_NAME = "MyBatis Dialect";
    private static final String DIALECT_PREFIX = "mybatis";

    public MybatisDialect() {
        this(DIALECT_PREFIX);
    }

    public MybatisDialect(String dialectPrefix) {
        super(DIALECT_NAME, dialectPrefix, StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new MyBatisWhereModelProcesser(getPrefix()));
        processors.add(new MyBatisSetModelProcesser(getPrefix()));
        return processors;
    }

}

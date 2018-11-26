package io.github.yoshikawaa.mybatis;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.AbstractAttributeModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class MyBatisWhereModelProcesser extends AbstractAttributeModelProcessor {

    private static final String ATTRIBUTE_NAME = "where";
    private static final int PRECEDENCE = 1200;

    private static final String TH_IF_ATTRIBUTE_NAME = "th:if";
    private static final String KEYWORD_WHERE = "WHERE";

    public MyBatisWhereModelProcesser(String dialectPrefix) {
        super(TemplateMode.TEXT, dialectPrefix, null, false, ATTRIBUTE_NAME, true, PRECEDENCE, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName, String attributeValue,
            IElementModelStructureHandler structureHandler) {

        if (checkDynamicWherePhrase(context, model)) {

            final IModelFactory modelFactory = context.getModelFactory();
            model.insert(0, modelFactory.createText(KEYWORD_WHERE));
        }

    }

    private boolean checkDynamicWherePhrase(ITemplateContext context, IModel model) {

        for (ITemplateEvent templateEvent : ModelUtils.getTemplateEvents(model)) {
            if (templateEvent instanceof IOpenElementTag) {
                for (IAttribute attr : ((IOpenElementTag) templateEvent).getAllAttributes()) {
                    if (TH_IF_ATTRIBUTE_NAME.equals(attr.getAttributeCompleteName())) {
                        if (ExpressionUtils.execute(context, attr.getValue(), Boolean.class)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}

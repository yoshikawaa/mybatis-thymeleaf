package io.github.yoshikawaa.mybatis;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.element.AbstractAttributeModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;

public class MyBatisSetModelProcesser extends AbstractAttributeModelProcessor {

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.TEXT;
    private static final String ATTRIBUTE_NAME = "set";
    private static final int PRECEDENCE = 1200;

    private static final String STANDARD_PREFIX_NAME = "th";
    private static final String IF_ATTRIBUTE_NAME = "if";

    private static final Pattern PATTERN_EMPTY = Pattern.compile("(\\r\\n|\\r|\\n| |Å@)*");
    private static final Pattern PATTERN_COMMENT = Pattern.compile("(\\r\\n|\\r|\\n| |Å@)*--");
    private static final Pattern PATTERN_COMMA = Pattern.compile(",");

    public MyBatisSetModelProcesser(String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, null, false, ATTRIBUTE_NAME, true, PRECEDENCE, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName, String attributeValue,
            IElementModelStructureHandler structureHandler) {

        final List<ITemplateEvent> templateEvents = ModelUtils.getTemplateEvents(model);
        final IModelFactory modelFactory = context.getModelFactory();

        for (int i = templateEvents.size() - 1; i > 0; i--) {
            ITemplateEvent templateEvent = templateEvents.get(i);
            if (templateEvent instanceof IOpenElementTag) {
                for (IAttribute attr : ((IOpenElementTag) templateEvent).getAllAttributes()) {
                    final AttributeName attrName = attr.getAttributeDefinition().getAttributeName();
                    if (STANDARD_PREFIX_NAME.equals(attrName.getPrefix())
                            && IF_ATTRIBUTE_NAME.equals(attrName.getAttributeName())) {
                        if (ExpressionUtils.execute(context, attr.getValue(), Boolean.class)) {
                            // find text event until close event if th:if expression provide true.
                            int countOpenCloseTag = 1;
                            for (int j = 1; countOpenCloseTag > 0; j++) {
                                ITemplateEvent nextEvent = templateEvents.get(i + j);
                                if (nextEvent instanceof IOpenElementTag) {
                                    countOpenCloseTag++;
                                } else if (nextEvent instanceof ICloseElementTag) {
                                    countOpenCloseTag--;
                                } else if (nextEvent instanceof IText) {
                                    final String text = ((IText) nextEvent).getText();
                                    if (!(StringUtils.isEmpty(text) || PATTERN_EMPTY.matcher(text).matches()
                                            || PATTERN_COMMENT.matcher(text).lookingAt())) {

                                        Matcher matcher = PATTERN_COMMA.matcher(text);
                                        if (matcher.find()) {
                                            // trim "," if last where body.
                                            model.replace(i + j, modelFactory.createText(matcher.replaceFirst("")));
                                        }
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

}

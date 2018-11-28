package io.github.yoshikawaa.mybatis;

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

public class MyBatisWhereModelProcesser extends AbstractAttributeModelProcessor {

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.TEXT;
    private static final String ATTRIBUTE_NAME = "where";
    private static final int PRECEDENCE = 1200;

    private static final String STANDARD_PREFIX_NAME = "th";
    private static final String IF_ATTRIBUTE_NAME = "if";

    private static final String KEYWORD_WHERE = "WHERE";
    private static final Pattern PATTERN_COMMENT = Pattern.compile("--");
    private static final Pattern PATTERN_AND_OR = Pattern.compile("(AND|OR)");

    public MyBatisWhereModelProcesser(String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, null, false, ATTRIBUTE_NAME, true, PRECEDENCE, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName, String attributeValue,
            IElementModelStructureHandler structureHandler) {

        final Pattern patternAndOr = StringUtils.isEmptyOrWhitespace(attributeValue) ? PATTERN_AND_OR
                : Pattern.compile(attributeValue);

        final IModelFactory modelFactory = context.getModelFactory();

        for (int i = 1; i < model.size(); i++) {

            final ITemplateEvent templateEvent = model.get(i);
            if (templateEvent instanceof IOpenElementTag) {

                // find th:if event.
                for (IAttribute attr : ((IOpenElementTag) templateEvent).getAllAttributes()) {
                    final AttributeName attrName = attr.getAttributeDefinition().getAttributeName();
                    if (STANDARD_PREFIX_NAME.equals(attrName.getPrefix())
                            && IF_ATTRIBUTE_NAME.equals(attrName.getAttributeName())) {

                        // skip until close event if expression provide false.
                        if (!ExpressionUtils.execute(context, attr.getValue(), Boolean.class)) {
                            int countOpenCloseTag = 1;
                            while (countOpenCloseTag > 0) {
                                final ITemplateEvent nextEvent = model.get(++i);
                                if (nextEvent instanceof IOpenElementTag) {
                                    countOpenCloseTag++;
                                } else if (nextEvent instanceof ICloseElementTag) {
                                    countOpenCloseTag--;
                                }
                            }
                        }
                    }
                }
            } else if (templateEvent instanceof IText) {
                final String text = ((IText) templateEvent).getText();
                final String trimmedText = text.trim();

                if (!(StringUtils.isEmpty(trimmedText) || PATTERN_COMMENT.matcher(trimmedText).lookingAt())) {

                    // insert WHERE if where body exists.
                    model.insert(0, modelFactory.createText(KEYWORD_WHERE));

                    // trim AND/OR from first where body.
                    if (patternAndOr.matcher(trimmedText).lookingAt()) {
                        model.replace(i + 1, modelFactory.createText(patternAndOr.matcher(text).replaceFirst("")));
                    }
                    break;
                }
            }
        }
    }

}

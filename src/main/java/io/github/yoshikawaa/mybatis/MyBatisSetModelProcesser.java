package io.github.yoshikawaa.mybatis;

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

    private static final Pattern PATTERN_COMMENT = Pattern.compile("--");
    private static final Pattern PATTERN_COMMA = Pattern.compile(",");

    public MyBatisSetModelProcesser(String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, null, false, ATTRIBUTE_NAME, true, PRECEDENCE, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName, String attributeValue,
            IElementModelStructureHandler structureHandler) {

        final Pattern patternComma = StringUtils.isEmptyOrWhitespace(attributeValue) ? PATTERN_COMMA
                : Pattern.compile(attributeValue);

        final IModelFactory modelFactory = context.getModelFactory();

        for (int i = model.size() - 1; i > 0; i--) {

            final ITemplateEvent templateEvent = model.get(i);
            if (templateEvent instanceof IOpenElementTag) {

                // find th:if event.
                for (IAttribute attr : ((IOpenElementTag) templateEvent).getAllAttributes()) {
                    final AttributeName attrName = attr.getAttributeDefinition().getAttributeName();
                    if (STANDARD_PREFIX_NAME.equals(attrName.getPrefix())
                            && IF_ATTRIBUTE_NAME.equals(attrName.getAttributeName())) {

                        // find text event until close event if expression provide true.
                        if (ExpressionUtils.execute(context, attr.getValue(), Boolean.class)) {
                            int countOpenCloseTag = 1;
                            for (int j = 1; countOpenCloseTag > 0; j++) {
                                final ITemplateEvent nextEvent = model.get(i + j);
                                if (nextEvent instanceof IOpenElementTag) {
                                    countOpenCloseTag++;
                                } else if (nextEvent instanceof ICloseElementTag) {
                                    countOpenCloseTag--;
                                } else if (nextEvent instanceof IText) {
                                    final String text = ((IText) nextEvent).getText();
                                    final String trimmedText = text.trim();

                                    if (!(StringUtils.isEmpty(trimmedText)
                                            || PATTERN_COMMENT.matcher(trimmedText).lookingAt())) {

                                        // trim "," from last where body.
                                        final Matcher matcher = patternComma.matcher(text);
                                        if (matcher.find()) {
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

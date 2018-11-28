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

public class MyBatisWhereModelProcesser extends AbstractAttributeModelProcessor {

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.TEXT;
    private static final String ATTRIBUTE_NAME = "where";
    private static final int PRECEDENCE = 1200;

    private static final String STANDARD_PREFIX_NAME = "th";
    private static final String IF_ATTRIBUTE_NAME = "if";

    private static final String KEYWORD_WHERE = "WHERE";
    private static final Pattern PATTERN_EMPTY = Pattern.compile("(\\r\\n|\\r|\\n| |�@)*");
    private static final Pattern PATTERN_COMMENT = Pattern.compile("(\\r\\n|\\r|\\n| |�@)*--");
    private static final Pattern PATTERN_AND_OR = Pattern.compile("(\\r\\n|\\r|\\n| |�@)*(AND|OR)");

    public MyBatisWhereModelProcesser(String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, null, false, ATTRIBUTE_NAME, true, PRECEDENCE, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName, String attributeValue,
            IElementModelStructureHandler structureHandler) {

        final List<ITemplateEvent> templateEvents = ModelUtils.getTemplateEvents(model);
        final IModelFactory modelFactory = context.getModelFactory();

        for (int i = 0; i < templateEvents.size(); i++) {
            ITemplateEvent templateEvent = templateEvents.get(i);
            if (templateEvent instanceof IOpenElementTag) {
                for (IAttribute attr : ((IOpenElementTag) templateEvent).getAllAttributes()) {
                    final AttributeName attrName = attr.getAttributeDefinition().getAttributeName();
                    if (STANDARD_PREFIX_NAME.equals(attrName.getPrefix())
                            && IF_ATTRIBUTE_NAME.equals(attrName.getAttributeName())) {
                        if (!ExpressionUtils.execute(context, attr.getValue(), Boolean.class)) {
                            // skip until close event if th:if expression provide false.
                            int countOpenCloseTag = 1;
                            while (countOpenCloseTag > 0) {
                                ITemplateEvent nextEvent = templateEvents.get(++i);
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
                if (!(StringUtils.isEmpty(text) || PATTERN_EMPTY.matcher(text).matches()
                        || PATTERN_COMMENT.matcher(text).lookingAt())) {
                    // insert WHERE if where body exists.
                    model.insert(0, modelFactory.createText(KEYWORD_WHERE));

                    Matcher matcher = PATTERN_AND_OR.matcher(text);
                    if (matcher.lookingAt()) {
                        // trim AND/OR if first where body.
                        model.replace(i + 1, modelFactory.createText(matcher.replaceFirst("")));
                    }
                    break;
                }
            }
        }
    }

}

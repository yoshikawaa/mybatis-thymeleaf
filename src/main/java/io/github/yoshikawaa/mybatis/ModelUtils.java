package io.github.yoshikawaa.mybatis;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.model.IModel;
import org.thymeleaf.model.ITemplateEvent;

public class ModelUtils {

    public static List<ITemplateEvent> getTemplateEvents(IModel model) {
        
        List<ITemplateEvent> templateEvents = new ArrayList<>();

        for (int i = 0; i < model.size(); i++) {
            templateEvents.add(model.get(i));
        }
        
        return templateEvents;
    }
    
}

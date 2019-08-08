package cn.zyz.wenda.async.handler;

import cn.zyz.wenda.async.EventHandler;
import cn.zyz.wenda.async.EventModel;
import cn.zyz.wenda.async.EventType;
import cn.zyz.wenda.model.Question;
import cn.zyz.wenda.service.QuestionService;
import cn.zyz.wenda.service.SearchService;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class SearchHandler implements EventHandler {
    private final static Logger logger = LoggerFactory.getLogger(SearchHandler.class);
    @Autowired
    private QuestionService questionService;

    @Autowired
    private SearchService searchService;

    @Override
    public void doHandle(EventModel model) {
        Question question = questionService.getQusetionById(model.getEntityId());
        try {
            searchService.indexQuestion(question);
        } catch (IOException | SolrServerException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}

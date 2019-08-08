package cn.zyz.wenda.service;

import cn.zyz.wenda.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    private final static String SOLR_URL = "http://localhost:8983/solr/wenda";
    private final static String QUESTION_TITLE_FIELD = "question_title";
    private final static String QUESTION_CONTENT_FIELD = "question_content";
    private final static HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL)
            .withConnectionTimeout(10000)
            .withSocketTimeout(60000)
            .build();

    public List<Question> queryQuestion(String keyword, int offset, int count, String hlPre, String hlPost) throws IOException, SolrServerException {
        List<Question> questions = new ArrayList<>();
        SolrQuery query = new SolrQuery(keyword);
        query.setStart(offset);
        query.setRows(count);
        query.setHighlight(true);
        query.set("hl.fl", QUESTION_TITLE_FIELD + "," + QUESTION_CONTENT_FIELD);
        query.setHighlightSimplePre(hlPre);
        query.setHighlightSimplePost(hlPost);
        QueryResponse response = client.query(query);
        Map<String, Map<String, List<String>>> map = response.getHighlighting();
        for (Map.Entry<String, Map<String, List<String>>> entry : map.entrySet()) {
            Question question = new Question();
            question.setId(Integer.parseInt(entry.getKey()));
            for (Map.Entry<String, List<String>> e : entry.getValue().entrySet()) {
                if (QUESTION_TITLE_FIELD.equals(e.getKey())) {
                    question.setTitle(e.getValue().get(0));
                }
                if (QUESTION_CONTENT_FIELD.equals(e.getKey())) {
                    question.setContent(e.getValue().get(0));
                }
            }
            questions.add(question);
        }
        return questions;
    }

    public int indexQuestion(Question question) throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", question.getId());
        doc.addField(QUESTION_TITLE_FIELD, question.getTitle());
        doc.addField(QUESTION_CONTENT_FIELD, question.getContent());
        client.add(doc, 1000);
        return client.commit().getStatus();
    }
}

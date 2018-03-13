package cn.edu.ruc.core;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import java.util.ArrayList;
import java.util.List;

public class Indexer {
    //important: pay attention to the strings such ""

    //get auto completion list using lucene
    public static List<String> getMatchingList(String keywords){
        List<String> matchingList = new ArrayList<>();

        if(keywords.trim() == null || keywords.trim() == "")
            return matchingList;

        IndexSearcher searcher = new IndexSearcher(DataUtil.getDirectoryReader());

        QueryParser parser_entity = new QueryParser("entity", new StandardAnalyzer());
        QueryParser parser_feature = new QueryParser("feature", new StandardAnalyzer());
        try {
            Query query_entity = parser_entity.parse(keywords);
            for (ScoreDoc scoreDoc : searcher.search(query_entity, DataUtil.Output_Auto_Size / 2).scoreDocs){
                Document document = searcher.doc(scoreDoc.doc);
                matchingList.add(document.get("name"));
            }
            Query query_feature = parser_feature.parse(keywords);
            for (ScoreDoc scoreDoc : searcher.search(query_feature, DataUtil.Output_Auto_Size / 2 < (DataUtil.Output_Auto_Size - matchingList.size()) ? (DataUtil.Output_Auto_Size - matchingList.size()) : DataUtil.Output_Auto_Size / 2).scoreDocs){
                Document document = searcher.doc(scoreDoc.doc);
                matchingList.add(document.get("name"));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return matchingList;
    }
}

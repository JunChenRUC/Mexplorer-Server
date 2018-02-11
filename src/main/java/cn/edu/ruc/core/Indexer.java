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
    //get auto completion list using lucene
    public static List<String> getMatchingList(String keywords){
        IndexSearcher searcher = new IndexSearcher(DataUtil.getDirectoryReader());

        List<String> matchingList = new ArrayList<>();

        QueryParser parser_context = new QueryParser("context", new StandardAnalyzer());
        Query query_context;
        try {
            query_context = parser_context.parse(keywords);
            for (ScoreDoc scoreDoc : searcher.search(query_context, DataUtil.Output_Auto_Size).scoreDocs){
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

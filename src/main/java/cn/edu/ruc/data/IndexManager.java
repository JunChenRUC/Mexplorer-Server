package cn.edu.ruc.data;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndexManager {
	private DirectoryReader directoryReader = null;
	
	public IndexManager(String inputPath, String outputPath){
		buildIndex(inputPath, outputPath);
	}
	
	public void buildIndex(String inputPath, String outputPath){
		if(!new File(outputPath).isDirectory()) {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
			indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

			BufferedReader reader;
			IndexWriter indexWriter;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath)), "UTF-8"));
				indexWriter = new IndexWriter(FSDirectory.open(Paths.get(outputPath)), indexWriterConfig);

				String tmpString;
				while((tmpString = reader.readLine()) != null){
					indexWriter.addDocument(createEntity(tmpString));
				}

				reader.close();
				indexWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			directoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(outputPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Document createEntity(String tmpString) throws IOException{
    	Document document = new Document();
    	
    	String name = URLDecoder.decode(tmpString, "UTF-8");
    	document.add(new Field("name", name.replaceAll("_", " "), TextField.TYPE_STORED));
    	document.add(new Field("context", getReplaceName(name), TextField.TYPE_STORED));
    	
    	return document;
	}

	private String getReplaceName(String name){
		String REGEX_film = "_\\(\\d*_[a-z]*\\)|_\\([a-z]*\\)";

		Pattern pattern = Pattern.compile(REGEX_film);
		StringBuffer sb = new StringBuffer();
		Matcher matcher = pattern.matcher(name);
		while (matcher.find()) {
			matcher.appendReplacement(sb, "");
		}
		matcher.appendTail(sb);

		return sb.toString().replaceAll("_", " ").replaceAll("##", " ").replaceAll("Category:", "");
	}
	
	public void showIndex(String inputPath) {  
        System.out.println("*****************Read index**********************");
		try {
			int docLength = directoryReader.maxDoc();  
			Document document;
			 for (int i = 0; i < docLength; i++) { 
				document = directoryReader.document(i);
				System.out.println("No. " + (i + 1) + "\tid: " + document.get("id") + "\tname: " + document.get("name") + "\tcontext: " + document.get("context"));
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("*****************Finish**********************\n");  
    } 
	
	public DirectoryReader getDirectoryReader() {
		return directoryReader;
	}
}

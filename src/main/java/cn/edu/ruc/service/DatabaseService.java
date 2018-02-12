package cn.edu.ruc.service;

import java.util.List;

public interface DatabaseService {
	List getHistory();
	List getSearchHistory(int id);
	List<String> saveEntity(List<String> entityStringList);

	String getImg(String entityString);
	String getAbstract(String entityString);
}

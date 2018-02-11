package cn.edu.ruc.service;

import java.util.List;

public interface DatabaseService {
	List getHistory();
	List getSearchHistory(int num);
	List<String>saveEntity(List<String> list);

	String getImg(String name);
	String getAbstract(String query);
}

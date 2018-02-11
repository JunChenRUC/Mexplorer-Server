package cn.edu.ruc.dao;

import cn.edu.ruc.domain.Entity;

import java.util.HashMap;
import java.util.List;


public interface SearchDao {
	String getPwd(String name);

	List getResult();

	void loadData(List list);
	void testLobHandler(String entity,String text);

	void saveQuery(List<Entity> list);
	List getPopular();

	List getSearchAll(int num);
	void saveEntity(List<String> list);

	List<String>getImg();

	void updateImg(HashMap<String, String> mp);

	String geturl(String name);

	String getAbstract(String query);
}

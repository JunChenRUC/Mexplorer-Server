package cn.edu.ruc.service.imp;

import java.util.List;

import cn.edu.ruc.dao.SearchDao;
import cn.edu.ruc.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseServiceImp implements DatabaseService {
	@Autowired
	SearchDao dao;
	
	public List getHistory(){
		return dao.getPopular();
	}

	@Override
	public List getSearchHistory(int id) {
		return dao.getSearchAll(id);
	}

	@Override
	public List<String> saveEntity(List<String> entityStringList) {
		dao.saveEntity(entityStringList);
		return null;
	}

	@Override
	public String getImg(String entityString) {
		return dao.geturl(entityString);
	}

	@Override
	public String getAbstract(String entityString) {
		return dao.getAbstract(entityString);
	}
}

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
	public List getSearchHistory(int num) {
		return dao.getSearchAll(num);
	}

	@Override
	public List<String> saveEntity(List<String> list) {
		dao.saveEntity(list);
		return null;
	}

	@Override
	public String getImg(String name) {
		return dao.geturl(name);
	}

	@Override
	public String getAbstract(String query) {
		return dao.getAbstract(query);
	}
	
}

package cn.edu.ruc.service;

import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;

import java.util.List;

public interface SearchService {
	//get auto completion list
	List<String> getAutoCompletionList(String keywords);

	//get profile of entity
	Profile getProfile(String queryEntityString);

	//get query
	Query getQuery(List<String> entityStringList, List<String> featureStringList);

}

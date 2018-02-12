package cn.edu.ruc.service;

import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;

import java.util.List;

public interface SearchService {
	//get auto completion list
	List<String> getAutoCompletionList(String keywords);

	//get query
	Query getQuery(List<String> entityStringList, List<String> featureStringList);

	//get profile of entity
	Profile getProfile(Entity queryEntity); //If possible, use this

	Profile getProfile(String queryEntityString);

	Profile getProfile(int queryEntityId);
}

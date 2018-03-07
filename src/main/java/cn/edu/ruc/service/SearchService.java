package cn.edu.ruc.service;

import cn.edu.ruc.domain.Task;
import cn.edu.ruc.model.Assess;
import cn.edu.ruc.model.Dropdown;
import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;

import java.util.List;
import java.util.Map;

public interface SearchService {
	//get auto completion list
	Dropdown getDropdown(String keywords);

	//get query
	Query getQuery(List<String> entityStringList, List<String> featureStringList);

	//get profile of entity
	Profile getProfile(String queryEntityString);

	//get assess
	Assess getAssess();

	//send user
	void sendUser(String userId);

	//send entity
	void sendBookmark(String userId, String taskId, String versionId, List<String> relevantEntityStringList);

	//send interaction
	void sendInteraction(String userId, String taskId, String versionId, String area, String option, String content, String timestamp);


}

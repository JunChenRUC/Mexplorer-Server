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

	//get assess
	Assess getAssess();

	//get assess
	Assess getAssess(int id);

	//send user
	void sendUser(String userId);

	//send entity
	void sendBookmark(String userId, int taskId, int versionId, List<String> relevantEntityStringList);

	//send interaction
	void sendInteraction(String userId, int taskId, int versionId, String area, String option, String content, String timestamp);
}

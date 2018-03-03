package cn.edu.ruc.service;

import cn.edu.ruc.model.Query;
import cn.edu.ruc.model.Result;

import java.util.List;

public interface ExploreService {
	//get recommendation
	Result getResult(Query query);

	//send entity
	void sendBookmark(String userId, String taskId, List<String> relevantEntityStringList);

	//send interaction
	void sendInteraction(String userId, String taskId, String area, String option, String content, String timestamp);
}

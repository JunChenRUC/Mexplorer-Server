package cn.edu.ruc.service;

import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;
import cn.edu.ruc.model.Result;

import java.util.List;

public interface ExploreService {
	//get recommendation
	Result getResult(int versionId, List<String> queryEntityStringList, List<String> queryFeatureStringList);

	//get profile of entity
	Profile getProfile(String queryEntityString, List<String> queryFeatureStringList);
}

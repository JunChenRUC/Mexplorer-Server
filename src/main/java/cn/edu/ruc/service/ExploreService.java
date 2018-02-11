package cn.edu.ruc.service;

import cn.edu.ruc.model.Query;
import cn.edu.ruc.model.Recommendation;

public interface ExploreService {
	//get recommendation
	Recommendation getRecommendation(Query query);
}

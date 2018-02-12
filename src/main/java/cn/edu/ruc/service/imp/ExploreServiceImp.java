package cn.edu.ruc.service.imp;

import cn.edu.ruc.core.Ranker;
import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;
import cn.edu.ruc.model.Query;
import cn.edu.ruc.model.Recommendation;
import cn.edu.ruc.service.ExploreService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ExploreServiceImp implements ExploreService {
	@Override
	public Recommendation getRecommendation(Query query) {
		List<Entity> relevantEntityList = Ranker.getRelevantEntityList(query.getEntityList(), query.getFeatureList());
		List<List<Feature>> relevantFeatureListList = Ranker.getRelevantFeatureListList(relevantEntityList);

		return new Recommendation(relevantEntityList, relevantFeatureListList);
	}
}

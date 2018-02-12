package cn.edu.ruc.service.imp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.ruc.core.Indexer;
import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;
import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;
import cn.edu.ruc.service.SearchService;
import cn.edu.ruc.core.Parser;
import cn.edu.ruc.core.Ranker;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImp implements SearchService {
	@Override
	public List<String> getAutoCompletionList(String keywords) {
		return Indexer.getMatchingList(keywords);
	}

	@Override
	public Query getQuery(List<String> queryEntityStringList, List<String> queryFeatureStringList) {
		List<Entity> queryEntityList = Parser.encodeEntityList(queryEntityStringList);
		List<Feature> queryFeatureList = Parser.encodeFeatureList(queryFeatureStringList);

		return new Query(queryEntityList, queryFeatureList);
	}

	@Override
	public Profile getProfile(Entity queryEntity) {
		List<List<Feature>> relevantFeatureListList = Ranker.getRelevantFeatureListList(Arrays.asList(queryEntity));

		return new Profile(queryEntity, relevantFeatureListList);
	}

	@Override
	public Profile getProfile(String queryEntityString) {
		Entity queryEntity = Parser.encodeEntity(queryEntityString);

		Parser.richEntity(queryEntity);

		List<List<Feature>> relevantFeatureListList = Ranker.getRelevantFeatureListList(Arrays.asList(queryEntity));

		return new Profile(queryEntity, relevantFeatureListList);
	}

	@Override
	public Profile getProfile(int queryEntityId) {
		Entity queryEntity = new Entity(queryEntityId);

		Parser.richEntity(queryEntity);

		List<List<Feature>> relevantFeatureListList = Ranker.getRelevantFeatureListList(Arrays.asList(queryEntity));

		return new Profile(queryEntity, relevantFeatureListList);
	}
}

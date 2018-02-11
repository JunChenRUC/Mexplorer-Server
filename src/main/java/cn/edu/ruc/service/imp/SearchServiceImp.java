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
	public Query getQuery(List<String> entityStringList, List<String> featureStringList) {
		List<Entity> entityList = new ArrayList<>();

		for(String entityString : entityStringList) {
			String[] tokens = entityString.split("_");
			Entity entity = Parser.encodeEntity(tokens[0], Double.parseDouble(tokens[1]));

			if(entity != null)
				entityList.add(entity);
		}

		List<Feature> featureList = new ArrayList<>();

		for(String featureString : featureStringList) {
			String[] tokens = featureString.split("_");
			Feature feature = Parser.encodeFeature(tokens[0], Double.parseDouble(tokens[1]));

			if(feature != null)
				featureList.add(feature);
		}

		return new Query(entityList, featureList);
	}

	@Override
	public Profile getProfile(String queryEntityString) {
		Entity entity = Parser.encodeEntity(queryEntityString);
		List<List<Feature>> featureListList = Ranker.getRelevantFeatureListList(Arrays.asList(entity));
		return new Profile(entity, featureListList);
	}
}

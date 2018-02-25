package cn.edu.ruc.service.imp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.ruc.core.DataUtil;
import cn.edu.ruc.core.Indexer;
import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;
import cn.edu.ruc.model.Dropdown;
import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;
import cn.edu.ruc.service.SearchService;
import cn.edu.ruc.core.Parser;
import cn.edu.ruc.core.Ranker;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImp implements SearchService {
	@Override
	public Dropdown getDropdown(String keywords) {
		long time = System.currentTimeMillis();

		Dropdown dropdown = new Dropdown(Indexer.getMatchingList(keywords));

		DataUtil.getLogManager().appendInfo("\nKeywords:\n\t" + keywords + "\nDropdown:" + dropdown + "\nTime: " + (System.currentTimeMillis() - time) / 1000 + "s");

		return dropdown;
	}

	@Override
	public Query getQuery(List<String> queryEntityStringList, List<String> queryFeatureStringList) {
		long time = System.currentTimeMillis();

		List<Entity> queryEntityList = Parser.encodeEntityList(queryEntityStringList);
		List<Feature> queryFeatureList = Parser.encodeFeatureList(queryFeatureStringList);

		Query query = new Query(queryEntityList, queryFeatureList);

		DataUtil.getLogManager().appendInfo("\nQuery entity string list:\n\t" + queryEntityStringList + "\nQuery feature string list:\n\t" + queryFeatureStringList + "\nQuery:" + query + "\nTime: " + (System.currentTimeMillis() - time) / 1000 + "s");

		return query;
	}

	@Override
	public Profile getProfile(String queryEntityString) {
		long time = System.currentTimeMillis();

		Entity queryEntity = Parser.encodeEntity(queryEntityString);

		Parser.decodeDescription(queryEntity);

		List<List<Feature>> relevantFeatureListList = Ranker.getRelevantFeatureListList(Arrays.asList(queryEntity), true);

		Profile profile = new Profile(queryEntity, relevantFeatureListList);

		DataUtil.getLogManager().appendInfo("\nQuery entity string:\n\t" + queryEntityString + "\nProfile:" + profile + "\nTime: " + (System.currentTimeMillis() - time) / 1000 + "s");

		return profile;
	}
}

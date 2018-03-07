package cn.edu.ruc.service.imp;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.edu.ruc.core.*;
import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;
import cn.edu.ruc.domain.Task;
import cn.edu.ruc.model.Assess;
import cn.edu.ruc.model.Dropdown;
import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;
import cn.edu.ruc.service.SearchService;
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

	@Override
	public Assess getAssess() {
		int id = DataUtil.getUserId();
		return new Assess(Assessor.getTaskList(id), Assessor.getVersionList(id));
	}

	@Override
	public void sendUser(String userId) {
		DataUtil.writeUser(userId);
	}

	@Override
	public void sendBookmark(String userId, String taskId, String versionId, List<String> relevantEntityStringList) {
		DataUtil.writeBookmark(userId, taskId, versionId, relevantEntityStringList);
	}

	@Override
	public void sendInteraction(String userId, String taskId, String versionId, String area, String option, String content, String timestamp) {
		DataUtil.writeInteraction(userId, taskId, versionId, area, option, content, timestamp);
	}
}

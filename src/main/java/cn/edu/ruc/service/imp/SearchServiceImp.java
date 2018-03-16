package cn.edu.ruc.service.imp;

import java.util.Arrays;
import java.util.List;

import cn.edu.ruc.core.*;
import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;
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

		List<Entity> queryEntityList = Parser.encodeSourceList(queryEntityStringList);
		List<Feature> queryFeatureList = Parser.encodeFeatureList(queryFeatureStringList);

		Query query = new Query(queryEntityList, queryFeatureList);

		DataUtil.getLogManager().appendInfo("\nQuery entity string list:\n\t" + queryEntityStringList + "\nQuery feature string list:\n\t" + queryFeatureStringList + "\nQuery:" + query + "\nTime: " + (System.currentTimeMillis() - time) / 1000 + "s");

		return query;
	}


	@Override
	public Assess getAssess() {
		int id = DataUtil.getUserId();
		return new Assess(Assessor.getTaskList(id));
	}

	@Override
	public Assess getAssess(int id) {
		return new Assess(Assessor.getTaskList(id));
	}

	@Override
	public void sendUser(String userId) {
		DataUtil.writeUser(userId);
	}

	@Override
	public void sendBookmark(String userId, int taskId, int versionId, List<String> relevantEntityStringList) {
		DataUtil.writeBookmark(userId, taskId, versionId, relevantEntityStringList);
	}

	@Override
	public void sendInteraction(String userId, int taskId, int versionId, String area, String option, String content, String timestamp) {
		DataUtil.writeInteraction(userId, taskId, versionId, area, option, content, timestamp);
	}
}

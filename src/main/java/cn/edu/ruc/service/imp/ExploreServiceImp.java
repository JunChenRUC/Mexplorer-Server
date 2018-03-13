package cn.edu.ruc.service.imp;

import cn.edu.ruc.core.DataUtil;
import cn.edu.ruc.core.Parser;
import cn.edu.ruc.core.Ranker;
import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;
import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;
import cn.edu.ruc.model.Result;
import cn.edu.ruc.service.ExploreService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class ExploreServiceImp implements ExploreService {
	@Override
	public Result getResult(int versionId, List<String> queryEntityStringList, List<String> queryFeatureStringList) {
		long time = System.currentTimeMillis();

		List<Entity> queryEntityList = Parser.encodeSourceList(queryEntityStringList);
		List<Feature> queryFeatureList = Parser.encodeFeatureList(queryFeatureStringList);

		List<Entity> entityList = new ArrayList<>();
		List<List<Feature>> leftFeatureListList = new ArrayList<>();
		List<List<Feature>> rightFeatureListList = new ArrayList<>();

		if(versionId == 1) {
			entityList = Ranker.getRelevantEntityList(queryEntityList, queryFeatureList, true);
		}
		else if(versionId == 2) {
			entityList = Ranker.getRelevantEntityList(queryEntityList, queryFeatureList, false);
			leftFeatureListList = Ranker.getRelevantFeatureListList(entityList, true);
		}
		else if(versionId == 3) {
			entityList = Ranker.getRelevantEntityList(queryEntityList, queryFeatureList, false);
			leftFeatureListList = Ranker.getRelevantFeatureListList(entityList, true);
			rightFeatureListList = Ranker.getRelevantFeatureListList(entityList, false);
		}

		Query query = new Query(queryEntityList, queryFeatureList);

		Profile profile = new Profile(entityList.get(0), Ranker.getRelevantFeatureListList(Arrays.asList(entityList.get(0)), true));

		Result result = new Result(query, entityList, profile, leftFeatureListList, rightFeatureListList);

		DataUtil.getLogManager().appendInfo("\nResult: " + result + "\nTime: " + (System.currentTimeMillis() - time) / 1000 + "s!");

		return result;
	}
}

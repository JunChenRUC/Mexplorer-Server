package cn.edu.ruc.service.imp;

import cn.edu.ruc.core.DataUtil;
import cn.edu.ruc.core.Ranker;
import cn.edu.ruc.domain.Entity;
import cn.edu.ruc.domain.Feature;
import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;
import cn.edu.ruc.model.Result;
import cn.edu.ruc.service.ExploreService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class ExploreServiceImp implements ExploreService {
	@Override
	public Result getResult(Query query) {
		long time = System.currentTimeMillis();

		List<Entity> entityList = Ranker.getRelevantEntityList(query.getEntityList(), query.getFeatureList());

		Profile profile = new Profile(entityList.get(0), Ranker.getRelevantFeatureListList(Arrays.asList(entityList.get(0)), true));

		List<List<Feature>> leftFeatureListList = Ranker.getRelevantFeatureListList(entityList, true);

		List<List<Feature>> rightFeatureListList = Ranker.getRelevantFeatureListList(entityList, false);

		Result result = new Result(query, entityList, profile, leftFeatureListList, rightFeatureListList);

		DataUtil.getLogManager().appendInfo("\nResult: " + result + "\nTime: " + (System.currentTimeMillis() - time) / 1000 + "s!");

		return result;
	}
}

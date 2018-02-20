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

		List<Entity> relevantEntityList = Ranker.getRelevantEntityList(query.getEntityList(), query.getFeatureList());
		List<List<Feature>> relevantFeatureListList = Ranker.getRelevantFeatureListList(relevantEntityList);
		Profile profile = new Profile(relevantEntityList.get(0), Ranker.getRelevantFeatureListList(Arrays.asList(relevantEntityList.get(0))));

		Result result = new Result(query, relevantEntityList, relevantFeatureListList, profile);

		DataUtil.getLogManager().appendInfo("\nResult: " + result + "\nTime: " + (System.currentTimeMillis() - time) / 1000 + "s!");

		return result;
	}
}

package cn.edu.ruc.controller;

import cn.edu.ruc.model.Query;
import cn.edu.ruc.model.Recommendation;
import cn.edu.ruc.service.ExploreService;
import cn.edu.ruc.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


@Controller
@RequestMapping("/")
@Scope("prototype")
public class ExploreController {

	@Autowired
	SearchService searchService;
	@Autowired
	ExploreService exploreService;

	//localhost:8080/getRecommendation?entities=Forrest Gump_1&features=Tom Hanks%23%23Actor%23%23-1_1
	@ResponseBody
	@RequestMapping(value="/getRecommendation", method=RequestMethod.GET)
	public Recommendation getResultEntityList(@RequestParam(required = false, value = "entities") String[] entityStringList, @RequestParam(required = false, value = "features") String[] featureStringList){
		long time = System.currentTimeMillis();

		Query query = searchService.getQuery(Arrays.asList(entityStringList), Arrays.asList(featureStringList));
		Recommendation recommendation = exploreService.getRecommendation(query);

		System.out.println("Query: " + query + "Recommendation: " + recommendation + "Time: " + (System.currentTimeMillis() - time) / 1000 + "s!");

		return recommendation;
	}

	//localhost:8080/getRecommendation?query=
	//you can use an object as the parameter
	/*@ResponseBody
	@RequestMapping(value="/getRecommendation", method=RequestMethod.GET)
	public Recommendation getResultEntityList(@RequestBody Query query){
		long time = System.currentTimeMillis();

		Recommendation recommendation = exploreService.getRecommendation(query);

		System.out.println("Query: " + query + "Recommendation: " + recommendation + "Time: " + (System.currentTimeMillis() - time) / 1000 + "s");
		return recommendation;
	}*/
}

package cn.edu.ruc.controller;


import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;
import cn.edu.ruc.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping("/")
@Scope("prototype")
public class SearchController {
	@Autowired
	SearchService searchService;

	//localhost:8080/getAutoCompletion?keywords=Forrest
	@ResponseBody
	@RequestMapping(value = "getAutoCompletion", method = RequestMethod.GET)
	public List<String> getAutoCompletion(@RequestParam("keywords") String keywords){
		long time = System.currentTimeMillis();

		List<String> autoCompleteList = searchService.getAutoCompletionList(keywords);

		System.out.println("Query: " + keywords + "AutoCompletion: " + autoCompleteList + "Time: " + (System.currentTimeMillis() - time) / 1000 + "s");
		return autoCompleteList;
	}

	//localhost:8080/getProfile?entity=Forrest Gump
	@ResponseBody
	@RequestMapping(value = "getProfile", method = RequestMethod.GET)
	public Profile getProfile(@RequestParam("entity") String entityString){
		long time = System.currentTimeMillis();

		Profile profile = searchService.getProfile(entityString);

		System.out.println("Query: " + entityString + "Profile: " + profile + "Time: " + (System.currentTimeMillis() - time) / 1000 + "s");
		return profile;
	}

	//localhost:8080/getQuery?entities=Forrest Gump_1&features=Tom Hanks%23%23Actor%23%23-1_1
	//an element of the query is composed of an entity (or a feature) and the weight via "_"
	//a feature is composed of an entity, a relation and a direction via ## (should be transferred to %23%23)
	@ResponseBody
	@RequestMapping(value = "getQuery", method = RequestMethod.GET)
	public Query getQuery(@RequestParam(required = false, value = "entities") String[] entityStringList, @RequestParam(required = false, value = "features") String[] featureStringList){
		long time = System.currentTimeMillis();

		Query query = searchService.getQuery(Arrays.asList(entityStringList), Arrays.asList(featureStringList));

		System.out.println("Entity string list: " + entityStringList + "Feature string list: " + featureStringList + "Query: " + query + "Time: " + (System.currentTimeMillis() - time) / 1000 + "s");
		return query;
	}
}

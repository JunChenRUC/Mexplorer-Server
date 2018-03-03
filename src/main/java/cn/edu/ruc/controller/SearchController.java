package cn.edu.ruc.controller;

import cn.edu.ruc.model.Dropdown;
import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;
import cn.edu.ruc.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


@Controller
@RequestMapping("/")
@Scope("prototype")
public class SearchController {
	@Autowired
	SearchService searchService;

	//localhost:8080/controller/getDropdown?keywords=Forrest
	@ResponseBody
	@RequestMapping(value = "getDropdown", method = RequestMethod.GET)
	public Dropdown getDropdown(@RequestParam("keywords") String keywords){
		return searchService.getDropdown(keywords);
	}

	//localhost:8080/controller/getProfile?queryEntity=Forrest Gump
	@ResponseBody
	@RequestMapping(value = "getProfile", method = RequestMethod.GET)
	public Profile getProfile(@RequestParam("queryEntity") String queryEntityString){
		return searchService.getProfile(queryEntityString);
	}

	//localhost:8080/controller/getQuery?queryEntities=Forrest Gump_1&queryFeatures=Tom Hanks%23%23Actor%23%23-1_1
	//an element of the query is composed of an entity (or a feature) and the weight via "_"
	//a feature is composed of an entity, a relation and a direction via ## (should be transferred to %23%23)
	@ResponseBody
	@RequestMapping(value = "getQuery", method = RequestMethod.GET)
	public Query getQuery(@RequestParam(value = "queryEntities") String[] queryEntityStringList, @RequestParam(value = "queryFeatures") String[] queryFeatureStringList){
		return searchService.getQuery(Arrays.asList(queryEntityStringList), Arrays.asList(queryFeatureStringList));
	}
}

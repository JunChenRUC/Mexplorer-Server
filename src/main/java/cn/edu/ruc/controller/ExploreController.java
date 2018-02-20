package cn.edu.ruc.controller;

import cn.edu.ruc.model.Result;
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

	//localhost:8080/getResult?entities=Forrest Gump_1&features=Tom Hanks%23%23Actor%23%23-1_1
	@ResponseBody
	@RequestMapping(value="/getResult", method=RequestMethod.GET)
	public Result getResultEntityList(@RequestParam(required = false, value = "entities") String[] entityStringList, @RequestParam(required = false, value = "features") String[] featureStringList){
		return exploreService.getResult(searchService.getQuery(Arrays.asList(entityStringList), Arrays.asList(featureStringList)));
	}
}

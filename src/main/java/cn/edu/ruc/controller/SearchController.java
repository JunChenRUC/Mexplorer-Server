package cn.edu.ruc.controller;

import cn.edu.ruc.model.Assess;
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

	//localhost:8080/controller/getQuery?queryEntities=Forrest Gump&queryFeatures=Tom Hanks%23%23Actor%23%23-1
	//a feature is composed of an entity, a relation and a direction via ## (should be transferred to %23%23)
	@ResponseBody
	@RequestMapping(value = "getQuery", method = RequestMethod.GET)
	public Query getQuery(@RequestParam(value = "queryEntities") String[] queryEntityStringList, @RequestParam(value = "queryFeatures") String[] queryFeatureStringList){
		return searchService.getQuery(Arrays.asList(queryEntityStringList), Arrays.asList(queryFeatureStringList));
	}

	//localhost:8080/controller/getAssess
	@ResponseBody
	@RequestMapping(value = "getAssess", method = RequestMethod.GET)
	public Assess getAssess(){
		return searchService.getAssess();
	}

	//localhost:8080/controller/sendUser?userId=
	@ResponseBody
	@RequestMapping(value="sendUser", method=RequestMethod.GET)
	public void sendUser(@RequestParam(value = "userId") String userId){
		searchService.sendUser(userId);
	}

	//localhost:8080/controller/sendBookmark?userId=test&taskId=1&versionId=1&relevantEntities=Cast Away_1_7000_timestamp__Forrest Gump_Tom Hanks%23%23Actor%23%23-1
	@ResponseBody
	@RequestMapping(value="sendBookmark", method=RequestMethod.GET)
	public void sendBookmark(@RequestParam(value = "userId") String userId, @RequestParam(value = "taskId") int taskId, @RequestParam(value = "versionId") int versionId, @RequestParam(value = "relevantEntities") String[] relevantEntityStringList){
		searchService.sendBookmark(userId, taskId, versionId, Arrays.asList(relevantEntityStringList));
	}

	//localhost:8080/controller/sendInteraction?userId=test&taskId=1&versionId=1&area=query&option=search&content="entity[], feature[]"&timestamp=1000
	@ResponseBody
	@RequestMapping(value="sendInteraction", method=RequestMethod.GET)
	public void sendInteraction(@RequestParam(value = "userId") String userId, @RequestParam(value = "taskId") int taskId, @RequestParam(value = "versionId") int versionId, @RequestParam(value = "area") String area, @RequestParam(value = "option") String option, @RequestParam(value = "content") String content, @RequestParam(value = "timestamp") String timestamp){
		searchService.sendInteraction(userId, taskId, versionId, area, option, content, timestamp);
	}
}

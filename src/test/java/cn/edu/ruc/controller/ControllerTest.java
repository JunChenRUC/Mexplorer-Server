package cn.edu.ruc.controller;

import cn.edu.ruc.core.DataUtil;
import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;
import cn.edu.ruc.model.Recommendation;
import cn.edu.ruc.service.ExploreService;
import cn.edu.ruc.service.SearchService;
import cn.edu.ruc.service.imp.ExploreServiceImp;
import cn.edu.ruc.service.imp.SearchServiceImp;
import org.junit.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/** 
* ExploreController Tester. 
* 
* @author <Authors name> 
* @since <pre>Feb 10, 2018</pre> 
* @version 1.0 
*/ 
public class ControllerTest {
    private static DataUtil dataUtil;
    private static SearchService searchService;
    private static ExploreService exploreService;

    @BeforeClass
    public static void beforeClass() {
        dataUtil = new DataUtil();
        searchService = new SearchServiceImp();
        exploreService = new ExploreServiceImp();
        System.out.println("@BeforeClass");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("@AfterClass");
    }

    @Before
    public void before() throws Exception {
        System.out.println("@Before");
    }

    @After
    public void after() throws Exception {
        System.out.println("@After");
    }
    /**
     *
     * Method: getAutoCompletion(@RequestParam("keywords") String keywords)
     *
     */
    @Test
    public void testGetAutoCompletion() throws Exception {
        System.out.println("@Test(getAutoCompletion(@RequestParam(\"keywords\") String keywords))");

        List<String> autoCompletion = searchService.getAutoCompletionList("Forrest");

        System.out.println(autoCompletion);
    }


    /**
     *
     * Method: getQuery(@RequestParam(required = false, value = "entities") String[] entityStringList, @RequestParam(required = false, value = "features") String[] featureStringList)
     *
     */
    @Test
    public void testGetQuery() throws Exception {
        System.out.println("@Test(getQuery(@RequestParam(required = false, value = \"entities\") String[] entityStringList, @RequestParam(required = false, value = \"features\") String[] featureStringList))");

        Query query = searchService.getQuery(Arrays.asList(new String[]{"Forrest Gump_1", "Apollo 13 (film)_1"}), Arrays.asList(new String[]{"Tom Hanks##Actor##-1_1"}));

        System.out.println(query);
    }


    /**
     *
     * Method: getProfile(@RequestParam("entity") String entityString)
     *
     */
    @Test
    public void testGetProfile() throws Exception {
        System.out.println("@Test(getProfile(@RequestParam(\"entity\") String entityString))");

        Profile profile = searchService.getProfile("Forrest Gump");

        System.out.println(profile);
    }

    /**
    *
    * Method: getRecommendation(@RequestParam(required = false, value = "entities") String[] entityStringList, @RequestParam(required = false, value = "features") String[] featureStringList)
    *
    */
    @Test
    public void testGetRecommendation() throws Exception {
        System.out.println("@Test(getRecommendation(@RequestParam(required = false, value = \"entities\") String[] entityStringList, @RequestParam(required = false, value = \"features\") String[] featureStringList))");

        Query query = searchService.getQuery(Arrays.asList(new String[]{/*"Forrest Gump_1", "Apollo 13 (film)_1"*/}), Arrays.asList(new String[]{"Tom Hanks##Actor##-1_1"}));

        Recommendation recommendation = exploreService.getRecommendation(query);

        System.out.println(query + "\n" + recommendation);
    }
} 

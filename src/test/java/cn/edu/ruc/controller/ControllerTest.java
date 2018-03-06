package cn.edu.ruc.controller;

import cn.edu.ruc.core.DataUtil;
import cn.edu.ruc.model.Dropdown;
import cn.edu.ruc.model.Profile;
import cn.edu.ruc.model.Query;
import cn.edu.ruc.model.Result;
import cn.edu.ruc.service.ExploreService;
import cn.edu.ruc.service.SearchService;
import cn.edu.ruc.service.imp.ExploreServiceImp;
import cn.edu.ruc.service.imp.SearchServiceImp;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

/** 
* ExploreController Tester. 
* 
* @author <Authors name> 
* @since <pre>Feb 10, 2018</pre> 
* @version 1.0 
*/
@RunWith(Parameterized.class)
public class ControllerTest {
    private static DataUtil dataUtil;
    private static SearchService searchService;
    private static ExploreService exploreService;

    @Parameterized.Parameter(0)
    public String keywords;
    @Parameterized.Parameter(1)
    public String entityString;
    @Parameterized.Parameter(2)
    public List<String> entityStringList;
    @Parameterized.Parameter(3)
    public List<String> featureStringList;

    @Parameterized.Parameters
    public static List<Object> getParameters() {
        return Arrays.asList(new Object[][]{
                {"forrest", "Forrest Gump", Arrays.asList(new String[]{"Forrest Gump_1"}), Arrays.asList(new String[]{})},
                {"Apollo", "Apollo 13 (film)", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"Tom Hanks##Actor##-1_1"})},
                {"Tom Hanks", "JFK (film)", Arrays.asList(new String[]{"Forrest Gump_1", "Apollo 13 (film)_1"}), Arrays.asList(new String[]{"Tom Hanks##Actor##-1_1"})},
                {"", "Tom Hanks", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"Category:Hollywood##Subject##-1_1"})},
        });
    }

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
     * Method: getDropdown(@RequestParam("keywords") String keywords)
     *
     */
    @Test
    public void testDropdown() throws Exception {
        System.out.println("@Test(getDropdown(@RequestParam(\"keywords\") String keywords))");

        searchService.getDropdown(keywords);
    }


    /**
     *
     * Method: getQuery(@RequestParam(required = false, value = "entities") String[] entityStringList, @RequestParam(required = false, value = "features") String[] featureStringList)
     *
     */
    @Test
    public void testGetQuery() throws Exception {
        System.out.println("@Test(getQuery(@RequestParam(required = false, value = \"entities\") String[] entityStringList, @RequestParam(required = false, value = \"features\") String[] featureStringList))");

        searchService.getQuery(entityStringList, featureStringList);
    }


    /**
     *
     * Method: getProfile(@RequestParam("entity") String entityString)
     *
     */
    @Test
    public void testGetProfile() throws Exception {
        System.out.println("@Test(getProfile(@RequestParam(\"entity\") String entityString))");

        searchService.getProfile(entityString);
    }


    /**
    *
    * Method: getResult(@RequestParam(required = false, value = "entities") String[] entityStringList, @RequestParam(required = false, value = "features") String[] featureStringList)
    *
    */
    @Test
    public void testGetResult() throws Exception {
        System.out.println("@Test(getResult(@RequestParam(required = false, value = \"entities\") String[] entityStringList, @RequestParam(required = false, value = \"features\") String[] featureStringList))");

        exploreService.getResult(searchService.getQuery(entityStringList, featureStringList));
    }
} 

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
public class ExploreControllerTest {
    private static DataUtil dataUtil;
    private static SearchService searchService;
    private static ExploreService exploreService;

    @Parameterized.Parameter(0)
    public String keywords;
    @Parameterized.Parameter(1)
    public String queryEntityString;
    @Parameterized.Parameter(2)
    public List<String> queryEntityStringList;
    @Parameterized.Parameter(3)
    public List<String> queryFeatureStringList;
    @Parameterized.Parameter(4)
    public int versionId;

    @Parameterized.Parameters
    public static List<Object> getParameters() {
        return Arrays.asList(new Object[][]{
                {"forrest", "Forrest Gump", Arrays.asList(new String[]{"Forrest Gump"}), Arrays.asList(new String[]{}), 1},
                {"forrest", "Forrest Gump", Arrays.asList(new String[]{"Forrest Gump"}), Arrays.asList(new String[]{}), 2},
                {"forrest", "Forrest Gump", Arrays.asList(new String[]{"Forrest Gump"}), Arrays.asList(new String[]{}), 3},
                {"Apollo", "Hachi: A Dog's Tale", Arrays.asList(new String[]{"Hachi: A Dog's Tale"}), Arrays.asList(new String[]{}), 1},
                {"Apollo", "Apollo 13 (film)", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"Category:Hollywood##Subject##-1"}), 2},
                {"Apollo", "Apollo 13 (film)", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"Category:Hollywood##Subject##-1"}), 3},
                {"Tom Hanks", "JFK (film)", Arrays.asList(new String[]{"JFK (film)"}), Arrays.asList(new String[]{"Tom Hanks##Actor##-1"}), 1},
                {"Tom Hanks", "JFK (film)", Arrays.asList(new String[]{"JFK (film)"}), Arrays.asList(new String[]{"Tom Hanks##Actor##-1"}), 2},
                {"Tom Hanks", "JFK (film)", Arrays.asList(new String[]{"JFK (film)"}), Arrays.asList(new String[]{"Tom Hanks##Actor##-1"}), 3},
                {"", "Tom Hanks", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"Category:Best Film, London Film Festival winners##Subject##-1"}), 3},
                {"", "", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"Mystery##Genre##-1"}), 1},
                {"", "", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"War##Genre##-1", "Romance##Genre##-1"}), 1},
                {"", "", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"Luc Besson##Director##-1"}), 3},
                {"", "", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"Jackie Chan##Actor##-1"}), 3},
                {"", "", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"Comedy##Genre##-1"}), 3},
                {"", "", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"War##Genre##-1"}), 3},
                {"", "", Arrays.asList(new String[]{"Forrest Gump"}), Arrays.asList(new String[]{}), 3},
                {"", "", Arrays.asList(new String[]{}), Arrays.asList(new String[]{"Leonardo DiCaprio##Actor##-1"}), 3}
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

        searchService.getQuery(queryEntityStringList, queryFeatureStringList);
    }

    /**
     *
     * Method: getProfile(@RequestParam("entity") String entityString)
     *
     */
    @Test
    public void testGetProfile() throws Exception {
        System.out.println("@Test(getProfile(@RequestParam(\"entity\") String entityString))");

        exploreService.getProfile(queryEntityString, queryFeatureStringList);
    }


    /**
    *
    * Method: getResult(@RequestParam(required = false, value = "entities") String[] entityStringList, @RequestParam(required = false, value = "features") String[] featureStringList)
    *
    */
    @Test
    public void testGetResult() throws Exception {
        System.out.println("@Test(getResult(@RequestParam(required = false, value = \"entities\") String[] entityStringList, @RequestParam(required = false, value = \"features\") String[] featureStringList))");

        exploreService.getResult(versionId, queryEntityStringList, queryFeatureStringList);
    }
} 

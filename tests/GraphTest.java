import Graph.*;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import TestData.*;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class GraphTest {

    private static Graph graph;

    @Mock
    private static Vertex mockVertex;

    @Mock
    private static Map<String, Vertex> verticesMap;

    //input test data for calculate distance test
    @DataProvider
    public static Object[][] distanceData() {
        return new Object[][] {
                { new ArrayList<>(Arrays.asList("A","B","C")), 9 , 2},
                { new ArrayList<>(Arrays.asList("C","E","B","C")), 9, 3},
                { new ArrayList<>(Arrays.asList("A","E","B","C","D","E")), 28, 5},
        };
    }

    //input test data for find number of possible route test
    @DataProvider
    public static Object[][] possibleRouteData() {
        return new Object[][] {
                {"C", "C", 3, "maximum", 2},
                {"A", "C", 4, "exactly", 3},
                {"C", "C", 30, "distance", 7},
        };
    }

    //input data for finding shortest distance test
    @DataProvider
    public static Object[][] shortestDistanceData() {
        return new Object[][] {
                {"A","C", 9},
                {"B","B", 9},
                {"A", "A", 0},
                {"E", "D", 15},
        };
    }

    @BeforeClass
    public static void setUp() throws Exception{
        mockVertex = mock(Vertex.class);
        verticesMap = Mockito.spy(new HashMap<String, Vertex>());
        //initialize and set up graph
        graph = new Graph(verticesMap);
        ArrayList<ArrayList<String>> graph_data= TestData.getGraphData();
        graph.buildGraph(graph_data);
    }

    @AfterClass
    public static void tearDown() throws Exception{
        mockVertex = null;
        graph = null;
    }

    @Test
    public void testBuildGraph(){

        assertNotNull(
                "getAllVerticesMap() should not return null.",
                graph.getAllVertices());
        assertNotNull(
                "getSize() should not return null.",
                graph.getSize());
        assertEquals(
                "There should be 5 vertices" ,
                5,
                graph.getSize());
    }

    @Test
    @UseDataProvider("distanceData")
    public void testCalculateDistance(ArrayList<String> input, int expected, int count) {
        //call SUT
        final int distance = graph.calculateDistance(input);
        assertEquals("Expect distance is : " + expected, distance, expected);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCalculateDistanceError() {
        //test when there is no route it should throws exception
        ArrayList<String> input = new ArrayList<>(Arrays.asList("A", "E", "D"));
        final int distance = graph.calculateDistance(input);
    }

    @Test
    @UseDataProvider("possibleRouteData")
    public void testFindPossibilePaths(String start_vertex, String end_vertex, int max, String criteria, int expected) {
        final int route_count = graph.findPossibilePaths(start_vertex, end_vertex, max, criteria);
        assertEquals("Expected number of route to be : " + expected, route_count, expected);
    }

    @Test
    @UseDataProvider("shortestDistanceData")
    public void testShortestDistance(String start_vertex, String end_vertex, int expected) {
        int route_count = graph.shortestDistance(start_vertex, end_vertex);
        assertEquals("Expected shortest distance is : " + expected, route_count, expected);
    }

}
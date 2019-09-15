package Graph;

import java.nio.file.Path;
import java.util.*;

public class Graph {

    private Map<String, Vertex> vertices;
    private int size;

    public Graph(Map<String, Vertex> vertices){
        this.size = 0;
        this.vertices = vertices;
    }

    public void addVertex(Vertex vertex){
        this.vertices.put(vertex.getValue(), vertex);
        this.size+=1;
    }

    public void addEdge(Vertex from, Vertex to, Integer weight){
        from.addEdge(to.getValue(), weight);
    }

    public Set getAllVertices(){
        return this.vertices.keySet();
    }

    public Map<String, Integer> getEdge(Vertex vertex){
        return vertex.getAllEdge();
    }

    public int getSize(){
        return this.size;
    }

    public void printGraph(){
        Set keys = vertices.keySet();
        for(Object key : keys) {
            System.out.println(key.toString() + " -- " + "edges : " + vertices.get(key.toString()).getAllEdge());
        }
    }

    public void buildGraph(ArrayList<ArrayList<String>> input) {
        for (ArrayList<String> element : input) {
            for (int i = 0; i < element.size() - 1; i++) {
                if (vertices.get(element.get(i)) == null) {
                    Vertex newVertex = new Vertex(element.get(i), new HashMap<String, Integer>());
                    this.addVertex(newVertex);
                }
            }
            this.addEdge(vertices.get(element.get(0)), vertices.get(element.get(1)), Integer.parseInt(element.get(2)));
        }
    }

    /*******************************************************************************
     * calculate and return total distance which will throw error if graph does not consists of vertex edge
     ********************************************************************************/
    public int calculateDistance(ArrayList<String> route) throws IllegalArgumentException{
        int distance = 0;
        int count = 0;
        for(int i = 0; i < route.size()-1; i++){
            count+=1;
            int edge_distance = this.vertices.get(route.get(i)).getWeight(route.get(i+1));
            if (edge_distance != 0){
                distance += edge_distance;
            }
            else{
                throw new IllegalArgumentException();
            }
        }
        return distance;
    }

    /********************************************************************************
    **utlised breadth first search algorithm to find possible paths with maximum number of routes
     ********************************************************************************/
    public int findPossibilePaths(String start_vertex, String end_vertex, int max, String inputCriteria) throws IllegalArgumentException{

        int count_paths = 0;
        final String criteria = inputCriteria;
        Queue<ArrayList<String>> queue = new LinkedList<>();
        ArrayList<String> route = new ArrayList<>();
        route.add(start_vertex);
        queue.add(route);

        while(!queue.isEmpty()){
            route = queue.remove();
            String current_vertex = route.get(route.size()-1);
            if(criteria == "distance"){
                count_paths += this.processPathDistance(current_vertex, end_vertex, max, route, queue);
            }else {
                count_paths += this.processPathMaxVertices(current_vertex, end_vertex, max, route, queue, criteria);
            }
        }
        return count_paths;
    }

    /********************************************************************************
    find and return # of paths with maximum or exact vertex criteria
     ********************************************************************************/
    private int processPathMaxVertices(String current_vertex, String end_vertex, int max, ArrayList<String> route, Queue<ArrayList<String>> queue, String criteria) throws NullPointerException{
        int count_paths = 0;
        if (current_vertex.equals(end_vertex) && route.size() > 1 && isInCriteriaRoute(max, criteria, route.size())){
            count_paths+=1;
        }
        Map<String, Integer> edge = this.vertices.get(current_vertex).getAllEdge();
        Set keys = edge.keySet();

        if(criteria == "maximum") {
            if (isInCriteriaRoute(max, criteria, route.size())) {
                for (Object key : keys) {
                    ArrayList<String> nextVertex = new ArrayList<>(route);
                    nextVertex.add(key.toString());
                    queue.add(nextVertex);
                }
            }
        }else{
            if ( route.size() <= max + 1) {
                for (Object key : keys) {
                    ArrayList<String> nextVertex = new ArrayList<>(route);
                    nextVertex.add(key.toString());
                    queue.add(nextVertex);
                }
            }
        }

        return count_paths;
    }

    /********************************************************************************
    * find and return # of path with maximum distance criteria
     ********************************************************************************/
    private int processPathDistance(String current_vertex, String end_vertex, int max, ArrayList<String> route, Queue<ArrayList<String>> queue) throws NullPointerException{
        int count_paths = 0;
        int current_distance = calculateDistance(route);
        if (current_vertex.equals(end_vertex) && route.size() > 1 && isInCriteriaDistance(max, current_distance)){
            count_paths+=1;
        }
        Map<String, Integer> edge = this.vertices.get(current_vertex).getAllEdge();
        Set keys = edge.keySet();
        for(Object key: keys){
            ArrayList<String> newRoute = new ArrayList<>(route);
            current_distance = calculateDistance(newRoute);
            if(current_distance < max){
                newRoute.add(key.toString());
                queue.add(newRoute);
            }
        }
        return count_paths;
    }

    /********************************************************************************
    *This return the shortest distance of a give path
    *********************************************************************************/
    public int shortestDistance(String start_vertex, String end_vertex) throws IllegalArgumentException{
        int distance = 0;
        HashMap<String, PathDistance> shortestPath = dikjstraShortestPath(start_vertex, end_vertex);
        distance = shortestPath.get(end_vertex).getDistance();
        return distance;
    }

    /********************************************************************************
    *Utilises Dijkstra shortest path returns a shortest distance give two vertices
    ********************************************************************************/
    public HashMap<String, PathDistance> dikjstraShortestPath(String start_vertex, String end_vertex){
        //create hash map assign every vertex of inf except start vertex to 0
        HashMap<String, PathDistance> path_and_distances = new HashMap<>();
        Set keys = this.vertices.keySet();
        int inf = Integer.MAX_VALUE;

        keys.forEach(key -> path_and_distances.put(key.toString(), new PathDistance(inf, key.toString())));
        PathDistance first = new PathDistance(0, start_vertex);
        path_and_distances.put(start_vertex, first);

        PathDistance from_vertex = new PathDistance(0, start_vertex);
        PriorityQueue<PathDistance> pq =  new PriorityQueue<PathDistance>();
        pq.add(from_vertex);

        while (pq.size() > 0){
            PathDistance current_pd = pq.poll();
            String current_vertex = current_pd.getVertex();
            int current_distance = current_pd.getDistance();

            Map<String, Integer> neighbors = this.vertices.get(current_vertex).getAllEdge();
            for (String neighbor : neighbors.keySet()){
                int new_distance = current_distance + neighbors.get(neighbor);
                int neighbor_distance = path_and_distances.get(neighbor).getDistance();

                if (new_distance < neighbor_distance || (neighbor_distance == 0 && neighbor.equals(start_vertex))){
                    //update new distance to hash_map
                    PathDistance p = path_and_distances.get(neighbor);
                    p.setDistance(new_distance);
                    p.setNewPath(neighbor);
                    //update to priority queue
                    PathDistance new_pd = new PathDistance(new_distance, neighbor);
                    pq.add(new_pd);
                }
            }
        }
        return path_and_distances;
    }

    //*************************
    //Helper Functions
    //*************************
    private boolean isVisited(String key, HashSet<String> routes, String start_vertex){

        for(String route : routes){
            if(route == key && route != start_vertex){
                return true;
            }
        }
        return false;
    }

    private boolean isInCriteriaRoute(int max, String criteria, int number_of_vertex){
        if (criteria == "maximum") {
            return number_of_vertex <= max+1;
        }else {
            return number_of_vertex == max+1;
        }
    }

    private boolean isInCriteriaDistance(int maxDistance, int currentDistance){
        return maxDistance > currentDistance;
    }

    //*************
    //Helper class to hold tuple of vertex and its neighbor path as an array list
    //This is used in Dijkstra algorithm to find the shortest path
    //*************
    class PathDistance implements Comparable<PathDistance>{
        private int distance;
        private ArrayList<String> path;

        public PathDistance(int distance, String vertex){
            this.distance = distance;
            this.path = new ArrayList<>();
            this.addVertices(vertex);
        }

        public int getDistance(){
            return this.distance;
        }

        public String getVertex(){
            return this.path.get(0);
        }

        public ArrayList<String> getVertices(){
            return this.path;
        }

        public void addVertices(String vertex){
            this.path.add(vertex);
        }

        public void setDistance(int distance){
            this.distance = distance;
        }

        public void setNewPath(String newPath){
            this.path.add(newPath);
        }

        @Override
        public int compareTo(PathDistance o) {
            return this.getDistance() - o.getDistance();
        }

    }

}

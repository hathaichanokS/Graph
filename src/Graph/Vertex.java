package Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Vertex {
    private String value;
    private Map<String, Integer> edges;

    public Vertex(String value, Map<String, Integer> edges){
        this.value = value;
        this.edges = edges;
    }

    public void addEdge(String vertex, Integer weight){
        this.edges.put(vertex, weight);
    }

    public Map<String, Integer> getAllEdge(){
        return this.edges;
    }

    public HashMap<String, Integer> getEdge(Vertex connectVertex){
        if (this.edges.get(connectVertex.getValue())!= null ){
            HashMap<String, Integer> edge = new HashMap<>();
            edge.put(connectVertex.getValue(), this.edges.get(connectVertex.getValue()));
            return edge;
        }else{
            return null;
        }
    }

    public Set<String> getEdges(){
        Set<String> edges = new HashSet<>();
        edges = this.edges.keySet();
        return edges;
    }

    public int getWeight(String vertex) throws IllegalArgumentException{
        if(this.edges.get(vertex) != null ){
            return this.edges.get(vertex);
        }else{
            throw new IllegalArgumentException();
        }
    }

    public String getValue(){
        return this.value;
    }


}

package uk.ac.ed.inf.pathFinding;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.Comparator;
import java.util.Map;

public class HeuristicComparator implements Comparator<Map.Entry<LngLat[], Double[]>> {

    /**
     *
     * @param node1 the first node to be compared.
     * @param node2 the second node to be compared.
     * @return Which node is the preferable next node
     */
    @Override
    public int compare(Map.Entry<LngLat[], Double[]> node1, Map.Entry<LngLat[], Double[]> node2) {
        // Sorts the priority queue by the second value, the heuristic
        Double heuristic1 = node1.getValue()[0];
        Double heuristic2 = node2.getValue()[0];

        return Double.compare(heuristic1, heuristic2);
    }
}

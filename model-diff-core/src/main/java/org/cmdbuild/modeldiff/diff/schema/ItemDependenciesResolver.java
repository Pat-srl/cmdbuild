/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Resolves dependency of items instancies (f.e. for {@link Classe},
 * dependencies are through parenthood)
 *
 * <p>
 * Is resolved with <b>Kahn algorithm</b>: 
 * (a queue beside the adjacency matrix) for graph (forest) topological visit.
 * 
 * <p>
 * It's <i>O(n^2)</i> even in worst case <code>A -%gt; B -&gt; C -&gt; D</code>,
 * where adjacency matrix would be<i>O(n^3)</i>.
 *
 * @author afelice
 * @param <T>
 */
public class ItemDependenciesResolver<T extends CmSchemaItemNode> {

    public List<String> resolve(Collection<T> items, Function<T, List<String>> dependenciesFunc) {
        return resolve(items, list(), dependenciesFunc);
    }

    /**
     *
     * @param items
     * @param toSkip item names that has to be skipped (because already
     * existing, for example).
     * @param dependenciesFunc function to get its parent (so a dependency).
     * @return
     */
    public List<String> resolve(Collection<T> items, Collection<String> toSkip, Function<T, List<String>> dependenciesFunc) {
        if (items.size() < 2) {
            return items.stream().map(CmSchemaItemNode::getDistinguishingName).collect(toList());
        }

        Pair<FluentMap<String, Set<String>>, Map<String, Integer>> graph = initializeGraph(items, toSkip, dependenciesFunc);
        return topologicalSort(graph.getLeft(), graph.getRight());
    }

    public static List<String> toNotNull(List<String> dependencies) {
        return list(dependencies).without(Objects::isNull);
    }

    /**
     *
     * @param items
     * @param dependenciesFunc
     * @return (graph, inDegree) with
     * <dl>
     * <dt>graph<dd>sort of <i>adiacency matrix</i>;
     * <dt>inDegree<dd>
     */
    private Pair<FluentMap<String, Set<String>>, Map<String, Integer>> initializeGraph(Collection<T> items, Collection<String> toSkip, Function<T, List<String>> dependenciesFunc) {
        FluentMap<String, Set<String>> graph = map();
        Map<String, Integer> inDegree = map();

        // Initialize graph structure
        for (T item : items) {
            String itemName = item.getDistinguishingName();
            Set<String> dependencies = set();
            final List<String> toAddDependencies = toNotNull(dependenciesFunc.apply(item));
            toAddDependencies.removeAll(toSkip);
            dependencies.addAll(toAddDependencies);
            dependencies.remove(itemName);

            graph.put(itemName, dependencies);
            inDegree.put(itemName, 0);
        }

        // Calculate in-degrees
        graph.forEach((node, deps) -> {
            for (String dep : deps) {
                inDegree.merge(node, 1, Integer::sum);
            }
        });

        return Pair.of(graph, inDegree);
    }

    /**
     * Implements <i>Kahn's algorithm</i> for topological sort of nodes with
     * dependencies.
     *
     * @return
     */
    private List<String> topologicalSort(final FluentMap<String, Set<String>> graph, final Map<String, Integer> inDegree) {
        List<String> result = list();
        Queue<String> noIncomingEdges = new LinkedList<>();
        Map<String, Set<String>> remainingGraph = map();

        // Deep copy the graph to detect cycles
        graph.forEach((key, value)
                -> remainingGraph.put(key, set(value)));

        inDegree.forEach((node, degree) -> {
            if (degree == 0) {
                noIncomingEdges.offer(node);
            }
        });

        while (!noIncomingEdges.isEmpty()) {
            String noIncomingsNode = noIncomingEdges.poll();
            remainingGraph.remove(noIncomingsNode);
            inDegree.remove(noIncomingsNode);
            result.add(noIncomingsNode);

            // Process neighbors
            remainingGraph.entrySet().stream()
                    .filter(entry -> entry.getValue().contains(noIncomingsNode))
                    .sorted(Map.Entry.comparingByKey()) // Ordinamento dei nodi in base al nome
                    .forEach(toUpdateEntry -> {
                        String toUpdateNode = toUpdateEntry.getKey();
                        // Remove edge in remaining graph
                        toUpdateEntry.getValue().remove(noIncomingsNode);

                        // Update indegree    
                        inDegree.merge(toUpdateNode, -1, Integer::sum);
                        if (inDegree.get(toUpdateNode) == 0) {
                            noIncomingEdges.offer(toUpdateNode);
                        }
                    });
        } // end noIncomingEdges while            

        // If we haven't processed all nodes, there must be a cycle
        if (result.size() != graph.size()) {
            // Notify unknown items
            Set<String> unknownItemsMsg = set();
            remainingGraph.forEach((item,deps) -> {
                    deps.forEach(d -> {
                        if (!graph.containsKey(d)) {
                            unknownItemsMsg.add("dependency =< %s > referred from item =< %s >".formatted(d, item));
                            Set<String> newDeps = set(deps);
                            deps.remove(d);
                            remainingGraph.put(item, newDeps);
                        }
                    });
                });
            if (!unknownItemsMsg.isEmpty()) {
                throw runtime("error - unknown dependencies detected: %s".formatted(String.join("; ", unknownItemsMsg)));
            }
            
            // Search if dependency cycle detected
            throwCycle(remainingGraph);
        }

        return result;
    }

    private void throwCycle(Map<String, Set<String>> remainingGraph) {
        throw runtime("error - cyclic dependencies detected: %s", findCycle(remainingGraph));
    }

    private String findCycle(Map<String, Set<String>> remainingGraph) {
        Set<String> visited = new HashSet<>();
        Set<String> currentPath = new HashSet<>();
        Map<String, String> parent = new HashMap<>();

        // Find a node that's part of a cycle
        for (String node : remainingGraph.keySet()) {
            if (!visited.contains(node)) {
                String cycleStart = dfsForCycle(node, visited, currentPath,
                        parent, remainingGraph);
                if (cycleStart != null) {
                    return reportCycle(cycleStart, parent);
                }
            }
        }

        return "sorry, can't find cycle";
    }

    private String dfsForCycle(String node, Set<String> visited,
            Set<String> currentPath, Map<String, String> parent,
            Map<String, Set<String>> graph) {
        visited.add(node);
        currentPath.add(node);

        for (String neighbor : graph.get(node)) {
            if (!visited.contains(neighbor)) {
                parent.put(neighbor, node);
                String cycleNode = dfsForCycle(neighbor, visited, currentPath,
                        parent, graph);
                if (cycleNode != null) {
                    return cycleNode;
                }
            } else if (currentPath.contains(neighbor)) {
                parent.put(neighbor, node);
                return neighbor;
            }
        }

        currentPath.remove(node);
        return null;
    }

    private String reportCycle(String start, Map<String, String> parent) {
        List<String> cycle = new ArrayList<>();
        String current = start;

        do {
            cycle.add(current);
            current = parent.get(current);
        } while (!current.equals(start));
        cycle.add(start);

        Collections.reverse(cycle);
        return "Detected cycle: %s".formatted(String.join(" -> ", cycle));
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.graph;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.graph.ClasseHierarchy.isLeaf;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.multimap;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class ClasseHierarchyUtils {

	public static List<ClasseHierarchy> buildClassHierarchy(List<Classe> classes) {
		return new ClasseHierarchyHelper(classes).buildClassHierarchy();
	}

	private static ClasseGraph buildGraph(List<Classe> sourceClasses) {
		List<ClasseHierarchyInfo> classes = sourceClasses.stream().map((classe) -> new ClasseHierarchyInfo(classe.getName(), classe.getParentOrNull())).collect(toList());
		return new ClasseGraph(classes);
	}

	private static class ClasseHierarchyHelper {

		private final ClasseGraph graph;
		private final Map<String, Classe> classes;

		public ClasseHierarchyHelper(List<Classe> sourceClasses) {
			this.graph = buildGraph(sourceClasses);
			classes = sourceClasses.stream().collect(toImmutableMap(Classe::getName, identity()));
		}

		public List<ClasseHierarchy> buildClassHierarchy() {
			return classes.values().stream().map(this::buildClassHierarchy).collect(toImmutableList());
		}

		private ClasseHierarchy buildClassHierarchy(Classe classe) {
			String classeId = classe.getName();
			Collection<Classe> children = graph.getChildren(classeId).stream().map(this::getClasse).collect(toImmutableList());
			Collection<Classe> descendants = graph.getDescendants(classeId).stream().map(this::getClasse).collect(toImmutableList());
			Collection<Classe> leaves;
			List<Classe> descLeaves = graph.getDescendants(classeId).stream().map(this::getClasse).filter(ClasseHierarchy::isLeaf).collect(toImmutableList());
			if (isLeaf(classe)) {
				leaves = list(classe).with(descLeaves).immutable();
			} else {
				leaves = descLeaves;
			}
			List<Classe> ancestors = graph.getAncestors(classeId).stream().map(this::getClasse).collect(toImmutableList());
			Classe parent = ancestors.stream().findFirst().orElse(null);
			checkArgument(classe.hasParent() ^ parent == null);
			return new ClasseHierarchyImpl(classe, parent, children, descendants, leaves, ancestors);
		}

		private Classe getClasse(String id) {
			return checkNotNull(classes.get(id));
		}

	}

	private static class ClasseHierarchyImpl implements ClasseHierarchy {

		private final Classe thisClass;
		private final Classe parent;
		private final Collection<Classe> children, descendants, leaves;
		private final List<Classe> ancestors;

		public ClasseHierarchyImpl(Classe inner, @Nullable Classe parent, Collection<Classe> children, Collection<Classe> descendants, Collection<Classe> leaves, List<Classe> ancestors) {
			this.thisClass = checkNotNull(inner);
			this.parent = parent;
			this.children = checkNotNull(children);
			this.descendants = checkNotNull(descendants);
			this.leaves = checkNotNull(leaves);
			this.ancestors = checkNotNull(ancestors);
		}

		@Override
		public List<Classe> getAncestors() {
			return ancestors;
		}

		@Override
		public Classe getClasse() {
			return thisClass;
		}

		@Override
		@Nullable
		public Classe getParentOrNull() {
			return parent;
		}

		@Override
		public Collection<Classe> getChildren() {
			return checkNotNull(children, "error accessing hierarchy: class hierarchy is not ready");
		}

		@Override
		public Collection<Classe> getLeaves() {
			return checkNotNull(leaves, "error accessing hierarchy: class hierarchy is not ready");
		}

		@Override
		public Collection<Classe> getDescendants() {
			return checkNotNull(descendants, "error accessing hierarchy: class hierarchy is not ready");
		}

		@Override
		public boolean isAncestorOf(Classe other) {
			return equal(other.getOid(), thisClass.getOid()) || getDescendants().stream().anyMatch((des) -> equal(des.getOid(), other.getOid()));
		}

	}

	private static class ClasseGraph {

		private final Collection<ClasseHierarchyInfo> classes;
		private final Map<String, GraphNode> graphNodesByClasseId = map();
		private final Multimap<String, GraphNode> graphNodesByParentId = multimap();

		public ClasseGraph(Collection<ClasseHierarchyInfo> classes) {
			this.classes = classes;
			buildGraph();
		}

		public List<String> getAncestors(String classeId) {
			checkNotBlank(classeId);
			List<String> res = list();
			String parent = classeId;
			while ((parent = getParentOrNull(parent)) != null) {
				res.add(parent);
			}
			return Lists.reverse(res);
		}

		public @Nullable
		String getParentOrNull(String classeId) {
			return getGraphNodeByClassid(classeId).getParentId();
		}

		public Collection<String> getChildren(String classeId) {
			return getGraphNodeByClassid(classeId).childs.stream().map(GraphNode::getClasseId).collect(toList());
		}

		public Collection<String> getDescendants(String classeId) {
			return getDescendantNodes(classeId).stream().map(GraphNode::getClasseId).collect(toList());
		}

		public Collection<String> getLeaves(String classeId) {
			return getDescendantNodes(classeId).stream().filter(GraphNode::isLeaf).map(GraphNode::getClasseId).collect(toList());
		}

		private Collection<GraphNode> getDescendantNodes(String classeId) {
			Queue<GraphNode> queue = new ConcurrentLinkedQueue<>();
			queue.addAll(getGraphNodeByClassid(classeId).childs);
			List<GraphNode> res = list();
			while (!queue.isEmpty()) {
				GraphNode node = queue.poll();
				res.add(node);
				queue.addAll(node.childs);
			}
			return res;
		}

		private GraphNode getGraphNodeByClassid(String classeId) {
			checkNotBlank(classeId);
			return checkNotNull(graphNodesByClasseId.get(classeId), "node not found for class id = %s", classeId);
		}

		private void buildGraph() {
			indexGraphNodes();
			addParents();
			addChilds();
		}

		private void indexGraphNodes() {
			classes.forEach((classe) -> {
				GraphNode node = new GraphNode(classe.getId(), classe.getParentId());
				graphNodesByClasseId.put(node.getClasseId(), node);
			});
		}

		private void addParents() {
			graphNodesByClasseId.values().forEach((node) -> {
				if (node.hasParent()) {
					graphNodesByParentId.put(node.getParentId(), node);
				}
			});
		}

		private void addChilds() {
			graphNodesByClasseId.values().forEach((node) -> {
				node.childs = graphNodesByParentId.get(node.getClasseId());
			});
		}

	}

	private static class GraphNode {

		private final String classeId, parentId;
		private Collection<GraphNode> childs;

		public GraphNode(String classeId, @Nullable String parentId) {
			this.classeId = checkNotBlank(classeId);
			this.parentId = parentId;
		}

		public String getClasseId() {
			return classeId;
		}

		public @Nullable
		String getParentId() {
			return parentId;
		}

		public boolean hasParent() {
			return parentId != null;
		}

		public boolean isLeaf() {
			return childs.isEmpty();
		}

	}

	private static class ClasseHierarchyInfo {

		private final String id, parentId;

		public ClasseHierarchyInfo(String id, @Nullable String parentId) {
			this.id = checkNotBlank(id);
			this.parentId = parentId;
		}

		public String getId() {
			return id;
		}

		@Nullable
		public String getParentId() {
			return parentId;
		}

	}
}

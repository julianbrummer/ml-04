package uni.ml.learning;

import java.util.HashSet;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Instance;
import uni.ml.dataset.Value;
import uni.ml.dataset.view.DatasetPredicateView;
import uni.ml.dataset.view.DatasetView;
import uni.ml.tree.Classifier;
import uni.ml.tree.InnerNode;
import uni.ml.tree.Leaf;
import uni.ml.tree.Node;
import uni.ml.tree.NodeVisitor;
import uni.ml.tree.TreeStringBuilder;

@RequiredArgsConstructor
public class DecisionTreeModel extends DecisionModel {

	private Node root;
	private final int maxDepth;
	
	/**
	 * To classify an instance of a dataset with a decision tree. 
	 * @author Julian Brummer
	 *
	 */
	private class TreeClassifier implements Classifier, NodeVisitor {

		private Instance testInstance;
		private Value<?> classValue;
		
		@Override
		public void visit(InnerNode node) {
			Value<?> decisionValue = testInstance.value(node.decisionAttribute());
			node.child(decisionValue).accept(this);
		}

		@Override
		public void visit(Leaf node) {
			classValue = node.value();
		}
		
		@Override
		public Value<?> classify(Instance instance, EnumAttribute<?> classAttribute) {
			testInstance = instance;
			root.accept(this);
			return classValue;
		}
	}
	
	/**
	 * Selects the partition attribute resulting in the maximum information gain.
	 */
	public static EnumAttribute<?> selectPartitionAttribute(DatasetView dataset, EnumAttribute<?> classAttribute, Set<EnumAttribute<?>> attributes) {
		EnumAttribute<?> partitionAttribute = null;
		float maxGain = Float.NEGATIVE_INFINITY;
		// iterate over attributes and check information gain using the attribute as a partitioner
		for (EnumAttribute<?> attribute : attributes) {
			float gain = Measures.informationGain(dataset, classAttribute, attribute);
			if (gain > maxGain) {
				maxGain = gain;
				partitionAttribute = attribute;
			}
		}
		return partitionAttribute;
	}
	
	/**
	 * Recursively creates a decision (sub-)tree from an example set. 
	 * @param examples The dataset or a view on a subset.
	 * @param classAttribute The classification/target attribute.
	 * @param attributes A list of attributes from which to select a decision attribute for this node.  
	 * @return The root node of the (sub-)tree.
	 */
	private Node trainModel(DatasetView examples, EnumAttribute<?> classAttribute, Set<EnumAttribute<?>> attributes, int depth) {
	
		if (Measures.entropy(examples, classAttribute) == 0) // all instances have the same value for the target attribute
			return new Leaf(examples.instanceAt(0).value(classAttribute)); // return a leaf with that value
		
		if (attributes.isEmpty() || depth == maxDepth) // return most common value if there are no more attributes to split on
			return new Leaf(Measures.mostCommonValue(examples, classAttribute));
	
		// splitting is possible, so we create an inner node and select the best partition attribute
		InnerNode node = new InnerNode();
		node.decisionAttribute(selectPartitionAttribute(examples, classAttribute, attributes));
		
		// iterate over values of the decision attribute
		for (Value<?> value : node.decisionAttribute()) {
			// select subset containing only instances with the same decision value
			DatasetView subset = DatasetPredicateView.selectInstances(examples, node.decisionAttribute(), value);
			if (subset.hasInstances()) {
				// remove decision attribute and build subtree
				Set<EnumAttribute<?>> remainingAttributes = new HashSet<>(attributes);
				remainingAttributes.remove(node.decisionAttribute());
				node.addChild(value, trainModel(subset, classAttribute, remainingAttributes, depth+1));
			} else {
				node.addChild(value, new Leaf(Measures.mostCommonValue(examples, classAttribute)));
			}
		}
		
		return node;
	}
	
	
	/**
	 * Recursively creates a decision (sub-)tree from a full example set.
	 * All attributes within the dataset (except for the classAttribute) are possible candidates for partition attributes.
	 * @param examples The dataset to create the decision tree from.
	 * @param classAttribute The classification/target attribute. 
	 * @return The root node of the (sub-)tree.
	 */
	@Override
	public void trainModel(DatasetView examples, EnumAttribute<?> classAttribute) {
		this.root = trainModel(examples, classAttribute, examples.attributeSet(classAttribute), 1);
	}
	
	/**
	 * @return The classifier of this model.
	 */
	@Override
	public Classifier classifier() {
		return new TreeClassifier();
	}
	
	@Override
	public String toString() {
		return new TreeStringBuilder().toString(root);
	}
	
}

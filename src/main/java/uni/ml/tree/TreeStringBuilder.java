package uni.ml.tree;

import java.util.Map.Entry;

import uni.ml.dataset.Value;

/**
 * This class is only used to convert a decision tree into a string for console output.
 * @author Julian Brummer
 *
 */
public class TreeStringBuilder implements NodeVisitor {

	private int level;
	private StringBuilder builder;

	private void appendLevelOffset() {
		for (int i = 0; i < level; i++) {
			builder.append("| ");
		}
	}

	@Override
	public void visit(InnerNode node) {
		builder.append(node).append("\n");
		level++;
		for (Entry<Value<?>, Node> child : node.children()) {
			appendLevelOffset();
			builder.append(child.getKey()).append(":");
			child.getValue().accept(this);
		}
		level--;
	}

	@Override
	public void visit(Leaf node) {
		builder.append(node).append("\n");
	}
	
	public String toString(Node node) {
		level = 0;
		builder = new StringBuilder();
		node.accept(this);
		return builder.toString();
	}

}

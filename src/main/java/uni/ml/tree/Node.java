package uni.ml.tree;

/**
 * The base class for inner nodes and leaves.
 * @author Julian Brummer
 *
 */
public abstract class Node {
	/**
	 * Make node visitable, e.g. for printing the tree.
	 */
	public abstract void accept(NodeVisitor visitor);
}

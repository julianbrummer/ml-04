package uni.ml.tree;

/**
 * A visitor for nodes.
 * @author Julian Brummer
 *
 */
public interface NodeVisitor {
	public void visit(InnerNode node);
	public void visit(Leaf node);
}

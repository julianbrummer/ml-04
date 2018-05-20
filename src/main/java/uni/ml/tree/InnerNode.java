package uni.ml.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import uni.ml.dataset.EnumAttribute;
import uni.ml.dataset.Value;


/**
 * An inner node of a decision tree.
 * Such a node has a decision attribute and should contain a children (either inner node or leaf) for each possible value.
 * @author Julian Brummer
 *
 */
@RequiredArgsConstructor
@Accessors(fluent=true)
@ToString(includeFieldNames=false, exclude={"children"})
public class InnerNode extends Node implements Iterable<Node> {
	@Getter @Setter
	private EnumAttribute<?> decisionAttribute;
	private Map<Value<?>, Node> children = new HashMap<>();
	
	/**
	 * Adds a new child to the node.
	 */
	public boolean addChild(Value<?> value, Node child) {
		if (!decisionAttribute.isAllowed(value))
			return false;
		
		children.put(value, child);
		return true;
	}

	public Node child(Value<?> decisionValue) {
		return children.get(decisionValue);
	}
	
	public Set<Entry<Value<?>, Node>> children() {
		return children.entrySet();
	}
	
	/**
	 * Make node visitable, e.g. for printing the tree.
	 */
	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}


	@Override
	public Iterator<Node> iterator() {
		return children.values().iterator();
	}

	
	
	
}

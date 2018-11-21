import java.util.*;

public class TreeNode
{
	/**
	 *	<pre>
	 *	CSCI3220 2018-19 First Term Assignment 4
	 *	This is a class for storing the information of a tree node.
	 *	</pre>
	 *
	 *	@author		Kevin Yuk-Lap Yip
	 *	@version	1.0 (19 November 2018)
	 *
	 *	<pre>
	 *	Change History:
	 *	1.0	- Initial version
	 *	</pre>
	 */

	/*----------------------------------------------------------------------
	 Instance variables
	----------------------------------------------------------------------*/

	/**
	 *	The label of this node
	 */
	protected String label = "";

	/**
	 *	The input/inferred sequence of this node in the downward phase
	 */
	protected String sequence = "";

	/**
	 *	The preferences of this node in the upward phase
	 */
	protected List<Set<Character>> preferences = null;

	/**
	 *	The parent node, null for root node
	 */
	protected TreeNode parent = null;

	/**
	 *	Distance from the parent, NaN for root node
	 *	(Not required for this assignment)
	 */
	protected double distToParent = Double.NaN;

	/**
	 *	The list of children, empty for leaf nodes
	 */
	protected List<TreeNode> children = new ArrayList<TreeNode>();


	/*----------------------------------------------------------------------
	 Public methods
	----------------------------------------------------------------------*/

	/**
	 *	This method gets the label of this node.
	 *	@return					The label of this node
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 *	This method gets the input/inferred sequence of this node.
	 *	@return					The sequence of this node
	 */
	public String getSequence()
	{
		return sequence;
	}

	/**
	 *	This method gets the sequence preferences of this node in the
	 *	upward phase.
	 *	@return					The preferences of this node
	 */
	public List<Set<Character>> getPreferences()
	{
		return preferences;
	}

	/**
	 *	This method gets the parent of this node.
	 *	@return					The parent of this node
	 */
	public TreeNode getParent()
	{
		return parent;
	}

	/**
	 *	This method gets the distance between this node and its parent.
	 *	(Not required for this assignment)
	 *	@return					The distance
	 */
	public double getDistToParent()
	{
		return distToParent;
	}

	/**
	 *	This method gets the list of chldren.
	 *	@return					The list of children
	 */
	public List<TreeNode> getChildren()
	{
//		return Collections.unmodifiableList(children);
		return children;
	}

	/**
	 *	This method sets the label of this node.
	 *	@param		label			The label of this node
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 *	This method sets the input/inferred sequence of this node.
	 *	@param		sequence		The sequence of this node
	 */
	public void setSequence(String sequence)
	{
		this.sequence = sequence;
	}

	/**
	 *	This method sets the sequence preferences of this node in the
	 *	upward phase.
	 *	@param		preferences		The preferences of this node
	 */
	public void setPreferences(List<Set<Character>> preferences)
	{
		this.preferences = preferences;
	}

	/**
	 *	This method sets the parent of this node.
	 *	@param		parent			The parent of this node
	 */
	public void setParent(TreeNode parent)
	{
		this.parent = parent;
	}

	/**
	 *	This method sets the distance between this node and its parent.
	 *	(Not required for this assignment)
	 *	@param		distToParent		The distance
	 */
	public void setDistToParent(double distToParent)
	{
		this.distToParent = distToParent;
	}

	/**
	 *	This method adds a child to this node.
	 *	@param		child			The child
	 */
	public void addChild(TreeNode child)
	{
		this.children.add(child);
	}
}

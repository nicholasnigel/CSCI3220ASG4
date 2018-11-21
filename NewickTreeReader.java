import java.io.*;
import java.util.*;

public class NewickTreeReader
{
	/**
	 *	<pre>
	 *	CSCI3220 2018-19 First Term Assignment 4
	 *	This class defines a reader for data in Newick Tree file format.
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
	 Class constants
	----------------------------------------------------------------------*/

	// Constants for the different sates
	protected static final char NODE_START	= 'S';	// Beginning of a node
	protected static final char LABEL	= 'L';	// Label of a node
	protected static final char COLON	= 'C';	// Colon
	protected static final char DIST	= 'D';	// Distance to the parent node
	protected static final char NODE_END	= 'E';	// End of node
	protected static final char SEMI_COLON	= 'F';	// Semi-colon

	/*----------------------------------------------------------------------
	 Class variables
	----------------------------------------------------------------------*/

	/**
	 *	The underlying tokenizer.
	 */
	protected StringTokenizer t = null;


	/*----------------------------------------------------------------------
	 Public methods
	----------------------------------------------------------------------*/

	/**
	 *	Constructor: create a new reader that reads from a string in
	 *	Newick format.
	 *	@param		s			The string
	 */
	public NewickTreeReader(String s)
	{
		// A strong tokenizer that recognizes all special symbols and
		// white spaces as delimiters, and returns both delimiters and
		// non-delimiters as tokens
		this.t = new StringTokenizer(s, "(,):; \t", true);
	}

	/**
	 *	Read the tree. (Stop at the first semi-colon, ignore everything
	 *	after it.)
	 *	@return					The tree
	 */
	public TreeNode readTree()
	{
		char state = NODE_START;	// Current state, initialized to NODE_START
		TreeNode currNode = null;	// Always point to the node that has been created and is still active
						// (i.e., may still have something to do with it)

		// Read the tokens until encountering the first semi-colon
		while (state != SEMI_COLON)
		{
			String token = t.nextToken();
			if (token == null)		// No more tokens
				throw new RuntimeException("Unexpected end of data.");

			switch (state)
			{
				case NODE_START:	// Expecting a new node to be defined next
					if (token.equals("("))		// The node is an internal node
					{
						TreeNode newNode = new TreeNode();
						newNode.setParent(currNode);
						if (currNode != null)
							currNode.addChild(newNode);
						currNode = newNode;
//						state = NODE_START;	// These commented lines are just for completeness
					}
					else if (token.equals(")"))	// The node is a leaf node without label or distance, and no more siblings
					{
						TreeNode newNode = new TreeNode();
						newNode.setParent(currNode);
						if (currNode != null)
							currNode.addChild(newNode);
						else			// Multiple roots: not allowed
							reportFormatError(state, token);
//						currNode = currNode;
						state = NODE_END;
					}
					else if (token.equals(","))	// The node is a leaf node without label or distance, and a sibling is coming
					{
						TreeNode newNode = new TreeNode();
						newNode.setParent(currNode);
						if (currNode != null)
							currNode.addChild(newNode);
						else			// Multiple roots: not allowed
							reportFormatError(state, token);
//						currNode = currNode;
//						state = NODE_START;
					}
					else if (token.equals(":"))	// The node is a leaf node without label but with a distance
					{
						TreeNode newNode = new TreeNode();
						newNode.setParent(currNode);
						if (currNode != null)
							currNode.addChild(newNode);
						currNode = newNode;
						state = COLON;
					}
					else if (!isDelimiter(token))	// The node is a leaf node with a label (not known whether with a distance yet)
					{
						TreeNode newNode = new TreeNode();
						newNode.setParent(currNode);
						if (currNode != null)
							currNode.addChild(newNode);
						newNode.setLabel(token);
						currNode = newNode;
						state = LABEL;
					}
					else
						reportFormatError(state, token);
					break;
				case LABEL:		// Just read a node label
					if (token.equals(")"))		// Ending the parent node, which should be an internal node
					{
						currNode = currNode.getParent();
						if (currNode == null)	// Closing too many brackets
							reportFormatError(state, token);
						else
							state = NODE_END;
					}
					else if (token.equals(","))	// Going to start a sibling. Pass the control back to the parent first
					{
						currNode = currNode.getParent();
						if (currNode == null)	// Multiple roots: not allowed
							reportFormatError(state, token);
						else
							state = NODE_START;
					}
					else if (token.equals(":"))	// Going to receive the distance
						state = COLON;
					else if (token.equals(";"))	// No more nodes. Let the final check to determine if the tree is complete
						state = SEMI_COLON;
					else
						reportFormatError(state, token);
					break;
				case COLON:		// Just read a colon
					if (!isDelimiter(token))	// The distance: check if it is a valid number
					{
						try
						{
							double dist = Double.parseDouble(token);
							currNode.setDistToParent(dist);
							state = DIST;
						}
						catch (NumberFormatException nfe)
						{
							reportFormatError(state, token);
						}
					}
					else
						reportFormatError(state, token);
					break;
				case DIST:		// Just read the distance
					if (token.equals(")"))		// Ending the parent node, which should be an internal node
					{
						currNode = currNode.getParent();
						if (currNode == null)	// Closing too many brackets
							reportFormatError(state, token);
						else
							state = NODE_END;
					}
					else if (token.equals(","))
					{
						currNode = currNode.getParent();
						if (currNode == null)	// Multiple roots: not allowed
							reportFormatError(state, token);
						else
							state = NODE_START;
					}
					else if (token.equals(";"))
						state = SEMI_COLON;
					else
						reportFormatError(state, token);
					break;
				case NODE_END:		// Just read the close bracket of an internal node, may have the label and/or the distance coming
					if (token.equals(")"))	// Nothing comes, and ending the parent node
					{
						currNode = currNode.getParent();
						if (currNode == null)	// Closing too many brackets
							reportFormatError(state, token);
						else
							state = NODE_END;
					}
					else if (token.equals(","))	// Nothing comes, and is going to have a sibling node: just pass the control back to the parent
					{
						currNode = currNode.getParent();
						if (currNode == null)	// Multiple roots: not allowed
							reportFormatError(state, token);
						else
							state = NODE_START;
					}
					else if (token.equals(":"))	// No label, but expect a distance to come next
						state = COLON;
					else if (token.equals(";"))	// Nothing comes, and no more things to read
						state = SEMI_COLON;
					else if (!isDelimiter(token))	// A label comes
					{
						currNode.setLabel(token);
						state = LABEL;
					}
					else
						reportFormatError(state, token);
					break;
				default:		// Should never happen
					throw new RuntimeException("Unknown state.");
			}
		}

		if (currNode == null)
			throw new RuntimeException("More close brackets then open brackets");
		else if (currNode.getParent() != null)
			throw new RuntimeException("More open brackets then close brackets");
		return currNode;
	}


	/*----------------------------------------------------------------------
	 Protected methods
	----------------------------------------------------------------------*/

	/**
	 *	Check if the input token is a delimiter.
	 *	@param		token			The token
	 *	@return					True if the token is a delimiter,
	 *						false otherwise.
	 */
	public boolean isDelimiter(String token)
	{
		return token.equals("(") ||
		       token.equals(")") ||
		       token.equals(",") ||
		       token.equals(":") ||
		       token.equals(";");
	}

	/**
	 *	Report a format error.
	 *	@param		state			The current state
	 *	@param		token			The token being processed
	 */
	public void reportFormatError(char state, String token)
	{
		String stateStr = null;
		if (state == NODE_START)
			stateStr = "NODE_START";
		else if (state == LABEL)
			stateStr = "LABEL";
		else if (state == COLON)
			stateStr = "COLON";
		else if (state == DIST)
			stateStr = "DIST";
		else if (state == NODE_END)
			stateStr = "NODE_END";
		else if (state == SEMI_COLON)
			stateStr = "SEMI_COLON";

		throw new RuntimeException("Unexpected token [" + token + "] encountered in the " + stateStr + " state.");
	}
}

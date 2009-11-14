/*
 * Copyright [2009] [Marcin Rzeźnicki]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package mr.go.sgfilter.vis.app;

import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public enum NodeType {

	DATA {

		@Override
		public String toString() {
			return application.getString("DATA");
		}
	},
	DATA_FILTER {

		@Override
		public String toString() {
			return application.getString("DATA_FILTERS");
		}
	},
	FILTER {

		@Override
		public String toString() {
			return application.getString("SG_FILTER");
		}
	},
	PREPROCESSOR {

		@Override
		public String toString() {
			return application.getString("PREPROCESSORS");
		}
	};

	public int addThisNodeToTree(JTree tree, Object userObject) {
		DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode parentNode = findParentNodeOfThisType(tree);
		DefaultMutableTreeNode thisNode = new DefaultMutableTreeNode(
				userObject,
				false);
		parentNode.add(thisNode);
		treeModel.nodesWereInserted(parentNode, new int[]
		{ parentNode.getChildCount() - 1 });
		return findLeafIndexOfThisNode(tree, thisNode);
	}

	public boolean isThisNodeTheFirstLeaf(JTree tree, Object userObject) {
		DefaultMutableTreeNode parentNode = findParentNodeOfThisType(tree);
		DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) parentNode
				.getFirstChild();
		return firstChild.getUserObject().equals(userObject);
	}

	public boolean isThisNodeTheLastLeaf(JTree tree, Object userObject) {
		DefaultMutableTreeNode parentNode = findParentNodeOfThisType(tree);
		DefaultMutableTreeNode lastChild = (DefaultMutableTreeNode) parentNode
				.getLastChild();
		return lastChild.getUserObject().equals(userObject);
	}

	public DefaultMutableTreeNode moveThisNodeDown(JTree tree, Object userObject) {
		DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode parentNode = findParentNodeOfThisType(tree);
		int childIndex = -1;
		for (Enumeration<DefaultMutableTreeNode> e = parentNode.children(); e
				.hasMoreElements();) {
			final DefaultMutableTreeNode nextElement = e.nextElement();
			childIndex += 1;
			if (nextElement.getUserObject().equals(userObject)) {
				final Object tmp = nextElement.getUserObject();
				final DefaultMutableTreeNode followingElement = (DefaultMutableTreeNode) parentNode
						.getChildAt(childIndex + 1);
				nextElement.setUserObject(followingElement.getUserObject());
				followingElement.setUserObject(tmp);
				treeModel.nodesChanged(parentNode, new int[]
				{ childIndex, childIndex + 1 });
				return followingElement;
			}
		}
		return null;
	}

	public DefaultMutableTreeNode moveThisNodeUp(JTree tree, Object userObject) {
		DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode parentNode = findParentNodeOfThisType(tree);
		int childIndex = -1;
		for (Enumeration<DefaultMutableTreeNode> e = parentNode.children(); e
				.hasMoreElements();) {
			final DefaultMutableTreeNode nextElement = e.nextElement();
			childIndex += 1;
			if (nextElement.getUserObject().equals(userObject)) {
				final Object tmp = nextElement.getUserObject();
				final DefaultMutableTreeNode prevElement = (DefaultMutableTreeNode) parentNode
						.getChildAt(childIndex - 1);
				nextElement.setUserObject(prevElement.getUserObject());
				prevElement.setUserObject(tmp);
				treeModel.nodesChanged(parentNode, new int[]
				{ childIndex - 1, childIndex });
				return prevElement;
			}
		}
		return null;
	}

	public int removeThisNodeFromTree(JTree tree, Object userObject) {
		DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode parentNode = findParentNodeOfThisType(tree);
		int childIndex = -1;
		for (Enumeration<DefaultMutableTreeNode> e = parentNode.children(); e
				.hasMoreElements();) {
			final DefaultMutableTreeNode nextElement = e.nextElement();
			childIndex += 1;
			if (nextElement.getUserObject().equals(userObject)) {
				final int leafIndex = findLeafIndexOfThisNode(tree, nextElement);
				parentNode.remove(nextElement);
				treeModel.nodesWereRemoved(parentNode, new int[]
				{ childIndex }, new Object[]
				{ nextElement });
				return leafIndex;
			}
		}
		return -1;
	}

	private int findLeafIndexOfThisNode(JTree tree, DefaultMutableTreeNode node) {
		TreeModel treeModel = tree.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel
				.getRoot();
		int leafIndex = -1;
		for (Enumeration<DefaultMutableTreeNode> e = rootNode
				.depthFirstEnumeration(); e.hasMoreElements();) {
			final DefaultMutableTreeNode nextElement = e.nextElement();
			if (nextElement.isLeaf() && nextElement.getParent() != rootNode) {
				leafIndex += 1;
				if (nextElement == node) {
					return leafIndex;
				}
			}
		}
		return -1;
	}

	private DefaultMutableTreeNode findParentNodeOfThisType(JTree tree) {
		DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel
				.getRoot();
		for (Enumeration<DefaultMutableTreeNode> e = rootNode.children(); e
				.hasMoreElements();) {
			final DefaultMutableTreeNode nextElement = e.nextElement();
			final Object userObject = nextElement.getUserObject();
			if (userObject == this) {
				return nextElement;
			}
		}
		return null;
	}

	private static final ResourceBundle	application	= ResourceBundle
															.getBundle("mr/go/sgfilter/vis/app/resources/Application");
}

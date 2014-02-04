/*
 * Copyright 2012 - 2013 Manuel Laggner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tinymediamanager.ui.gamesets;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.tinymediamanager.core.game.GameSet;

/**
 * The Class GameSetRootTreeNode.
 * 
 * @author Manuel Laggner
 */
public class GameSetRootTreeNode extends DefaultMutableTreeNode {

  /** The Constant serialVersionUID. */
  private static final long    serialVersionUID = -1209627220507076339L;

  /** The node comparator. */
  private Comparator<TreeNode> nodeComparator;

  /**
   * Instantiates a new game set tree node.
   * 
   */
  public GameSetRootTreeNode() {
    super("GameSets");

    nodeComparator = new Comparator<TreeNode>() {
      @Override
      public int compare(TreeNode o1, TreeNode o2) {
        if (o1 instanceof GameTreeNode && o2 instanceof GameTreeNode) {
          GameSetTreeNode node1 = (GameSetTreeNode) o1;
          GameSet gameSet1 = (GameSet) node1.getUserObject();
          GameSetTreeNode node2 = (GameSetTreeNode) o2;
          GameSet gameSet2 = (GameSet) node2.getUserObject();
          return gameSet1.getTitleSortable().compareToIgnoreCase(gameSet2.getTitleSortable());
        }
        return o1.toString().compareToIgnoreCase(o2.toString());
      }
    };
  }

  /**
   * provides the right name of the node for display.
   * 
   * @return the string
   */
  @Override
  public String toString() {
    // return gameSet name
    if (getUserObject() instanceof GameSet) {
      GameSet gameSet = (GameSet) getUserObject();
      return gameSet.getTitle();
    }

    // fallback: call super
    return super.toString();
  }

  /**
   * Sort.
   */
  @SuppressWarnings("unchecked")
  public void sort() {
    if (this.children != null) {
      Collections.sort(this.children, nodeComparator);
    }
  }
}

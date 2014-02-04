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

import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameSet;

/**
 * The Class GameSetTreeNode.
 * 
 * @author Manuel Laggner
 */
public class GameSetTreeNode extends DefaultMutableTreeNode {

  /** The Constant serialVersionUID. */
  private static final long                   serialVersionUID = 1095499645850717752L;

  /** The Constant nodeComparator. */
  protected static final Comparator<TreeNode> nodeComparator;

  static {
    nodeComparator = new Comparator<TreeNode>() {
      @Override
      public int compare(TreeNode o1, TreeNode o2) {
        if (o1 instanceof GameTreeNode && o2 instanceof GameTreeNode) {
          GameTreeNode node1 = (GameTreeNode) o1;
          Game game1 = (Game) node1.getUserObject();
          GameTreeNode node2 = (GameTreeNode) o2;
          Game game2 = (Game) node2.getUserObject();

          if (game1 == null || game2 == null || game1.getGameSet() == null || game2.getGameSet() == null) {
            return 0;
          }
          int index1 = game1.getGameSet().getGameIndex(game1);
          int index2 = game2.getGameSet().getGameIndex(game2);
          return index1 - index2;
        }
        return o1.toString().compareToIgnoreCase(o2.toString());
      }
    };
  }

  /**
   * Instantiates a new game set tree node.
   * 
   * @param userObject
   *          the user object
   */
  public GameSetTreeNode(Object userObject) {
    super(userObject);
  }

  /**
   * provides the right name of the node for display
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
   * Sort the nodes
   */
  @SuppressWarnings("unchecked")
  public void sort() {
    if (this.children != null) {
      Collections.sort(this.children, nodeComparator);
    }
  }
}

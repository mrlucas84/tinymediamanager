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

import javax.swing.tree.DefaultMutableTreeNode;

import org.tinymediamanager.core.game.Game;

/**
 * The Class GameTreeNode.
 * 
 * @author Manuel Laggner
 */
public class GameTreeNode extends DefaultMutableTreeNode {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8311072585615953333L;

  /**
   * Instantiates a new game tree node.
   * 
   * @param userObject
   *          the user object
   */
  public GameTreeNode(Object userObject) {
    super(userObject);
  }

  /**
   * provides the right name of the node for display.
   * 
   * @return the string
   */
  @Override
  public String toString() {
    // return game name
    if (getUserObject() instanceof Game) {
      Game game = (Game) getUserObject();
      return game.getTitleSortable();
    }

    // fallback: call super
    return super.toString();
  }

}

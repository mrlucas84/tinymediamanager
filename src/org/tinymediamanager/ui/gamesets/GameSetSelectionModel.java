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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.tinymediamanager.core.AbstractModelObject;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameSet;

/**
 * The Class GameSelectionModel.
 * 
 * @author Manuel Laggner
 */
public class GameSetSelectionModel extends AbstractModelObject {
  private static final String    SELECTED_MOVIE_SET = "selectedGameSet";

  private GameSet                selectedGameSet;
  private GameSet                initalGameSet      = new GameSet("");
  private PropertyChangeListener propertyChangeListener;
  private JTree                  tree;

  /**
   * Instantiates a new game selection model. Usage in GameSetPanel
   */
  public GameSetSelectionModel(JTree tree) {
    selectedGameSet = initalGameSet;
    this.tree = tree;

    propertyChangeListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        firePropertyChange(evt);
      }
    };
  }

  /**
   * Sets the selected game set.
   * 
   * @param gameSet
   *          the new selected game set
   */
  public void setSelectedGameSet(GameSet gameSet) {
    GameSet oldValue = this.selectedGameSet;

    if (gameSet != null) {
      this.selectedGameSet = gameSet;
    }
    else {
      this.selectedGameSet = initalGameSet;
    }

    if (oldValue != null) {
      oldValue.removePropertyChangeListener(propertyChangeListener);
    }

    if (selectedGameSet != null) {
      selectedGameSet.addPropertyChangeListener(propertyChangeListener);
    }

    firePropertyChange(SELECTED_MOVIE_SET, oldValue, this.selectedGameSet);
  }

  /**
   * Gets the selected game set.
   * 
   * @return the selected game set
   */
  public GameSet getSelectedGameSet() {
    return selectedGameSet;
  }

  /**
   * Gets the selected game sets
   * 
   * @return the selected game sets
   */
  public List<GameSet> getSelectedGameSets() {
    List<GameSet> selectedGameSets = new ArrayList<GameSet>();
    TreePath[] paths = tree.getSelectionPaths();
    // tree.clearSelection();

    // filter out all game sets from the selection
    if (paths != null) {
      for (TreePath path : paths) {
        if (path.getPathCount() > 1) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
          if (node.getUserObject() instanceof GameSet) {
            GameSet gameSet = (GameSet) node.getUserObject();
            selectedGameSets.add(gameSet);
          }
        }
      }
    }

    return selectedGameSets;
  }

  /**
   * get all selected games. selected game sets will NOT return all their games
   * 
   * @return list of all selected games
   */
  public List<Game> getSelectedGames() {
    List<Game> selectedGames = new ArrayList<Game>();
    TreePath[] paths = tree.getSelectionPaths();

    // filter out all game sets from the selection
    if (paths != null) {
      for (TreePath path : paths) {
        if (path.getPathCount() > 1) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
          if (node.getUserObject() instanceof Game) {
            Game game = (Game) node.getUserObject();
            selectedGames.add(game);
          }
        }
      }
    }

    return selectedGames;
  }

  /**
   * get all selected games. selected game sets will return all their games
   * 
   * @return list of all selected games
   */
  public List<Game> getSelectedGamesRecursive() {
    List<Game> selectedGames = new ArrayList<Game>();

    TreePath[] paths = tree.getSelectionPaths();

    // filter out all game sets from the selection
    if (paths != null) {
      for (TreePath path : paths) {
        if (path.getPathCount() > 1) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
          if (node.getUserObject() instanceof GameSet) {
            GameSet gameSet = (GameSet) node.getUserObject();
            for (Game game : gameSet.getGames()) {
              if (!selectedGames.contains(game)) {
                selectedGames.add(game);
              }
            }
          }
          if (node.getUserObject() instanceof Game) {
            Game game = (Game) node.getUserObject();
            if (!selectedGames.contains(game)) {
              selectedGames.add(game);
            }
          }
        }
      }
    }

    return selectedGames;
  }
}

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.core.game.GameSet;

/**
 * The Class GameSetTreeModel.
 * 
 * @author Manuel Laggner
 */
public class GameSetTreeModel implements TreeModel {
  private GameSetRootTreeNode     root      = new GameSetRootTreeNode();
  private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
  private Map<Object, TreeNode>   nodeMap   = Collections.synchronizedMap(new HashMap<Object, TreeNode>());
  private PropertyChangeListener  propertyChangeListener;
  private GameList                gameList  = GameList.getInstance();

  /**
   * Instantiates a new game set tree model.
   * 
   * @param gameSets
   *          the game sets
   */
  public GameSetTreeModel(List<GameSet> gameSets) {
    // create the listener
    propertyChangeListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        // react on changes of games attached to this set
        if ("addedGame".equals(evt.getPropertyName())) {
          Game game = (Game) evt.getNewValue();
          GameSet gameSet = (GameSet) evt.getSource();
          addGame(gameSet, game);
        }
        if ("removedGame".equals(evt.getPropertyName())) {
          Game game = (Game) evt.getNewValue();
          GameSet gameSet = (GameSet) evt.getSource();
          removeGame(gameSet, game);
        }
        if ("removedAllGames".equals(evt.getPropertyName())) {
          @SuppressWarnings("unchecked")
          List<Game> removedGames = (List<Game>) evt.getOldValue();
          GameSet gameSet = (GameSet) evt.getSource();
          for (Game game : removedGames) {
            removeGame(gameSet, game);
          }
        }
        if ("addedGameSet".equals(evt.getPropertyName())) {
          GameSet gameSet = (GameSet) evt.getNewValue();
          addGameSet(gameSet);
        }
        if ("removedGameSet".equals(evt.getPropertyName())) {
          GameSet gameSet = (GameSet) evt.getNewValue();
          removeGameSet(gameSet);
        }
        if ("games".equals(evt.getPropertyName()) && evt.getSource() instanceof GameSet) {
          // sort order of games inside the gameset changed
          GameSet gameSet = (GameSet) evt.getSource();
          sortGamesInGameSet(gameSet);
        }

        // update on changes of a game
        if (evt.getSource() instanceof GameSet || evt.getSource() instanceof Game) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodeMap.get(evt.getSource());
          if (node != null) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            int index = parent.getIndex(node);
            TreeModelEvent event = new TreeModelEvent(this, parent.getPath(), new int[] { index }, new Object[] { node });
            for (TreeModelListener listener : listeners) {
              listener.treeNodesChanged(event);
            }
          }
        }
      }
    };

    // build initial tree
    for (GameSet gameSet : gameSets) {
      DefaultMutableTreeNode setNode = new GameSetTreeNode(gameSet);
      nodeMap.put(gameSet, setNode);
      for (Game game : gameSet.getGames()) {
        DefaultMutableTreeNode gameNode = new GameTreeNode(game);
        setNode.add(gameNode);
        nodeMap.put(game, gameNode);
      }
      root.add(setNode);

      // implement change listener
      gameSet.addPropertyChangeListener(propertyChangeListener);
    }

    gameList.addPropertyChangeListener(propertyChangeListener);

    root.sort();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
   */
  @Override
  public Object getChild(Object parent, int index) {
    return ((TreeNode) parent).getChildAt(index);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.tree.TreeModel#getRoot()
   */
  @Override
  public Object getRoot() {
    return root;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
   */
  public int getChildCount(Object parent) {
    return ((TreeNode) parent).getChildCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
   */
  public boolean isLeaf(Object node) {
    // root is never a leaf
    if (node == root) {
      return false;
    }

    if (node instanceof GameSetTreeNode) {
      GameSetTreeNode mstnode = (GameSetTreeNode) node;
      if (mstnode.getUserObject() instanceof GameSet) {
        return false;
      }
    }

    return getChildCount(node) == 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
   */
  public int getIndexOfChild(Object parent, Object child) {
    return ((TreeNode) parent).getIndex((TreeNode) child);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event. TreeModelListener)
   */
  public void addTreeModelListener(TreeModelListener listener) {
    listeners.add(listener);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event. TreeModelListener)
   */
  public void removeTreeModelListener(TreeModelListener listener) {
    listeners.remove(listener);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
   */
  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {
  }

  /**
   * Adds the game set.
   * 
   * @param gameSet
   *          the game set
   */
  public void addGameSet(GameSet gameSet) {
    synchronized (root) {
      GameSetTreeNode child = new GameSetTreeNode(gameSet);
      nodeMap.put(gameSet, child);
      // add the node
      root.add(child);
      root.sort();

      int index = root.getIndex(child);

      // inform listeners
      TreeModelEvent event = new TreeModelEvent(this, root.getPath(), new int[] { index }, new Object[] { child });

      for (TreeModelListener listener : listeners) {
        listener.treeNodesInserted(event);
      }
    }

    gameSet.addPropertyChangeListener(propertyChangeListener);
  }

  /**
   * Adds the game.
   * 
   * @param gameSet
   *          the game set
   * @param game
   *          the game
   */
  private void addGame(GameSet gameSet, Game game) {
    synchronized (root) {
      // get the game set node
      GameSetTreeNode parent = (GameSetTreeNode) nodeMap.get(gameSet);
      GameTreeNode child = new GameTreeNode(game);
      if (parent != null) {
        nodeMap.put(game, child);
        parent.add(child);
        int index = parent.getIndex(child);

        // inform listeners
        TreeModelEvent event = new TreeModelEvent(this, parent.getPath(), new int[] { index }, new Object[] { child });
        for (TreeModelListener listener : listeners) {
          listener.treeNodesInserted(event);
        }
      }
    }
  }

  /**
   * Removes the game.
   * 
   * @param gameSet
   *          the game set
   * @param game
   *          the game
   */
  private void removeGame(GameSet gameSet, Game game) {
    synchronized (root) {
      // get the game set node
      GameSetTreeNode parent = (GameSetTreeNode) nodeMap.get(gameSet);
      GameTreeNode child = (GameTreeNode) nodeMap.get(game);
      if (parent != null && child != null && parent.isNodeChild(child)) {
        int index = parent.getIndex(child);
        parent.remove(child);
        nodeMap.remove(game);

        // inform listeners
        TreeModelEvent event = new TreeModelEvent(this, parent.getPath(), new int[] { index }, new Object[] { child });
        for (TreeModelListener listener : listeners) {
          listener.treeNodesRemoved(event);
        }
      }
    }
  }

  /**
   * Removes the game set.
   * 
   * @param gameSet
   *          the game set
   */
  public void removeGameSet(GameSet gameSet) {
    synchronized (root) {
      GameSetTreeNode node = (GameSetTreeNode) nodeMap.get(gameSet);
      int index = root.getIndex(node);

      gameSet.removePropertyChangeListener(propertyChangeListener);

      nodeMap.remove(gameSet);
      for (Game game : gameSet.getGames()) {
        nodeMap.remove(game);
        game.removePropertyChangeListener(propertyChangeListener);
      }

      node.removeAllChildren();
      node.removeFromParent();

      // inform listeners
      TreeModelEvent event = new TreeModelEvent(this, root.getPath(), new int[] { index }, new Object[] { node });

      for (TreeModelListener listener : listeners) {
        listener.treeNodesRemoved(event);
      }
    }
  }

  /**
   * Removes the.
   * 
   * @param path
   *          the path
   */
  public void remove(TreePath path) {
    synchronized (root) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) path.getParentPath().getLastPathComponent();
      int index = parent.getIndex(node);

      // remove a gameset and all referenced games
      if (node.getUserObject() instanceof GameSet) {
        GameSet gameSet = (GameSet) node.getUserObject();
        for (Game game : gameSet.getGames()) {
          game.setGameSet(null);
          game.saveToDb();
          game.writeNFO();
          nodeMap.remove(game);
        }
        gameSet.removeAllGames();
        gameSet.removePropertyChangeListener(propertyChangeListener);
        gameList.removeGameSet(gameSet);
        nodeMap.remove(gameSet);

        node.removeAllChildren();
        node.removeFromParent();

        // inform listeners
        TreeModelEvent event = new TreeModelEvent(this, parent.getPath(), new int[] { index }, new Object[] { node });

        for (TreeModelListener listener : listeners) {
          listener.treeNodesRemoved(event);
        }
      }

      // remove a game
      if (node.getUserObject() instanceof Game) {
        Game game = (Game) node.getUserObject();
        GameSet gameSet = game.getGameSet();
        if (gameSet != null) {
          gameSet.removeGame(game);
        }

        nodeMap.remove(game);

        game.setGameSet(null);
        game.saveToDb();
        game.writeNFO();

        // here we do not need to inform listeners - is already done via
        // propertychangesupport (gameSet.removeGame)
      }
    }
  }

  /**
   * Sort games in game set.
   * 
   * @param gameSet
   *          the game set
   */
  public void sortGamesInGameSet(GameSet gameSet) {
    synchronized (root) {
      GameSetTreeNode node = (GameSetTreeNode) nodeMap.get(gameSet);
      node.sort();

      // inform listeners
      TreeModelEvent event = new TreeModelEvent(this, node.getPath());

      for (TreeModelListener listener : listeners) {
        listener.treeStructureChanged(event);
      }
    }
  }

  /**
   * Sort game sets.
   */
  public void sortGameSets() {
    synchronized (root) {
      root.sort();

      // inform listeners
      TreeModelEvent event = new TreeModelEvent(this, root.getPath());

      for (TreeModelListener listener : listeners) {
        listener.treeStructureChanged(event);
      }
    }
  }
}

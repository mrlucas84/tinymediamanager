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
package org.tinymediamanager.ui.games;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.tinymediamanager.core.AbstractModelObject;
import org.tinymediamanager.core.game.Game;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

/**
 * The Class GameSelectionModel.
 * 
 * @author Manuel Laggner
 */
public class GameSelectionModel extends AbstractModelObject implements ListSelectionListener {

  /** The Constant SELECTED_MOVIE. */
  private static final String              SELECTED_MOVIE = "selectedGame";

  /** The selected games. */
  private List<Game>                       selectedGames;

  /** The selected game. */
  private Game                             selectedGame;

  /** The initial game. */
  private Game                             initialGame    = new Game();

  /** The selection model. */
  private DefaultEventSelectionModel<Game> selectionModel;

  /** The matcher editor. */
  private GameMatcherEditor                matcherEditor;

  /** The table comparator chooser. */
  private TableComparatorChooser<Game>     tableComparatorChooser;

  /** The sorted list. */
  private SortedList<Game>                 sortedList;

  /** The property change listener. */
  private PropertyChangeListener           propertyChangeListener;

  /**
   * Instantiates a new game selection model. Usage in GamePanel
   * 
   * @param sortedList
   *          the sorted list
   * @param source
   *          the source
   * @param matcher
   *          the matcher
   */
  public GameSelectionModel(SortedList<Game> sortedList, EventList<Game> source, GameMatcherEditor matcher) {
    this.sortedList = sortedList;
    this.selectionModel = new DefaultEventSelectionModel<Game>(source);
    this.selectionModel.addListSelectionListener(this);
    this.matcherEditor = matcher;
    this.selectedGames = selectionModel.getSelected();

    propertyChangeListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == selectedGame) {
          firePropertyChange(evt);
        }
      }
    };
  }

  /**
   * Instantiates a new game selection model. Usage in GameSetPanel
   */
  public GameSelectionModel() {

  }

  /**
   * Sets the selected game.
   * 
   * @param game
   *          the new selected game
   */
  public void setSelectedGame(Game game) {
    Game oldValue = this.selectedGame;
    this.selectedGame = game;

    if (oldValue != null) {
      oldValue.removePropertyChangeListener(propertyChangeListener);
    }

    if (selectedGame != null) {
      selectedGame.addPropertyChangeListener(propertyChangeListener);
    }

    firePropertyChange(SELECTED_MOVIE, oldValue, game);
  }

  /**
   * Gets the matcher editor.
   * 
   * @return the matcher editor
   */
  public GameMatcherEditor getMatcherEditor() {
    return matcherEditor;
  }

  /**
   * Gets the selection model.
   * 
   * @return the selection model
   */
  public DefaultEventSelectionModel<Game> getSelectionModel() {
    return selectionModel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event. ListSelectionEvent)
   */
  /**
   * Value changed.
   * 
   * @param e
   *          the e
   */
  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }

    // display first selected game
    if (selectedGames.size() > 0 && selectedGame != selectedGames.get(0)) {
      Game oldValue = selectedGame;
      selectedGame = selectedGames.get(0);

      // unregister propertychangelistener
      if (oldValue != null && oldValue != initialGame) {
        oldValue.removePropertyChangeListener(propertyChangeListener);
      }
      if (selectedGame != null && selectedGame != initialGame) {
        selectedGame.addPropertyChangeListener(propertyChangeListener);
      }
      firePropertyChange(SELECTED_MOVIE, oldValue, selectedGame);
    }

    // display empty game (i.e. when all games are removed from the list)
    if (selectedGames.size() == 0) {
      Game oldValue = selectedGame;
      selectedGame = initialGame;
      // unregister propertychangelistener
      if (oldValue != null && oldValue != initialGame) {
        oldValue.removePropertyChangeListener(propertyChangeListener);
      }
      firePropertyChange(SELECTED_MOVIE, oldValue, selectedGame);
    }
  }

  /**
   * Gets the selected game.
   * 
   * @return the selected game
   */
  public Game getSelectedGame() {
    return selectedGame;
  }

  /**
   * Gets the selected games.
   * 
   * @return the selected games
   */
  public List<Game> getSelectedGames() {
    return selectedGames;
  }

  /**
   * Sets the selected games.
   * 
   * @param selectedGames
   *          the new selected games
   */
  public void setSelectedGames(List<Game> selectedGames) {
    this.selectedGames = selectedGames;
  }

  /**
   * Filter games.
   * 
   * @param filter
   *          the filter
   */
  public void filterGames(HashMap<GamesExtendedMatcher.SearchOptions, Object> filter) {
    matcherEditor.filterGames(filter);
  }

  /**
   * Gets the table comparator chooser.
   * 
   * @return the table comparator chooser
   */
  public TableComparatorChooser<Game> getTableComparatorChooser() {
    return tableComparatorChooser;
  }

  /**
   * Sets the table comparator chooser.
   * 
   * @param tableComparatorChooser
   *          the new table comparator chooser
   */
  public void setTableComparatorChooser(TableComparatorChooser<Game> tableComparatorChooser) {
    this.tableComparatorChooser = tableComparatorChooser;
  }

  /**
   * Sort games.
   * 
   * @param column
   *          the column
   * @param ascending
   *          the ascending
   */
  public void sortGames(GameExtendedComparator.SortColumn column, boolean ascending) {
    Comparator<Game> comparator = new GameExtendedComparator(column, ascending);
    sortedList.setComparator(comparator);
  }
}
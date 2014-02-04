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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.games.GameExtendedComparator.SortColumn;

/**
 * The Class GameTableModel.
 * 
 * @author Manuel Laggner
 */
public class GameTableModel extends AbstractTableModel {

  /** The Constant serialVersionUID. */
  private static final long           serialVersionUID = -1850397154387184169L;

  /** The Constant BUNDLE. */
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control());            //$NON-NLS-1$

  /** The Constant checkIcon. */
  private final static ImageIcon      checkIcon        = new ImageIcon(MainWindow.class.getResource("images/Checkmark.png"));

  /** The Constant crossIcon. */
  private final static ImageIcon      crossIcon        = new ImageIcon(MainWindow.class.getResource("images/Cross.png"));

  /** The game list. */
  private GameList                    gameList         = GameList.getInstance();

  /** The games. */
  private final List<Game>            games;

  /** The filtered games. */
  private final List<Game>            filteredGames;

  /** The comparator. */
  private Comparator<Game>            comparator;

  /**
   * Instantiates a new game table model.
   */
  public GameTableModel() {
    games = gameList.getGames();
    filteredGames = new ArrayList<Game>(games);
    comparator = new GameExtendedComparator(SortColumn.TITLE, true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  @Override
  public int getColumnCount() {
    return 6;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getRowCount()
   */
  @Override
  public int getRowCount() {
    return filteredGames.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.odell.glazedlists.gui.TableFormat#getColumnName(int)
   */
  @Override
  public String getColumnName(int column) {
    switch (column) {
      case 0:
        return BUNDLE.getString("metatag.title"); //$NON-NLS-1$

      case 1:
        return BUNDLE.getString("metatag.year"); //$NON-NLS-1$

      case 2:
        return BUNDLE.getString("metatag.nfo"); //$NON-NLS-1$

      case 3:
        return BUNDLE.getString("metatag.images"); //$NON-NLS-1$

      case 4:
        return BUNDLE.getString("metatag.trailer"); //$NON-NLS-1$

      case 5:
        return BUNDLE.getString("metatag.subtitles"); //$NON-NLS-1$
    }

    throw new IllegalStateException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  @Override
  public Object getValueAt(int rowIndex, int column) {
    Game game = filteredGames.get(rowIndex);
    switch (column) {
      case 0:
        return game.getTitleSortable();

      case 1:
        return game.getYear();

      case 2:
        if (game.getHasNfoFile()) {
          return checkIcon;
        }
        return crossIcon;

      case 3:
        if (game.getHasImages()) {
          return checkIcon;
        }
        return crossIcon;

      case 4:
        if (game.getHasTrailer()) {
          return checkIcon;
        }
        return crossIcon;

      case 5:
        if (game.hasSubtitles()) {
          return checkIcon;
        }
        return crossIcon;
    }

    throw new IllegalStateException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.odell.glazedlists.gui.AdvancedTableFormat#getColumnClass(int)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
      case 1:
        return String.class;

      case 2:
      case 3:
      case 4:
      case 5:
        return ImageIcon.class;
    }

    throw new IllegalStateException();
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
    comparator = new GameExtendedComparator(column, ascending);
    sort();
    fireTableChanged(new TableModelEvent(this));
  }

  /**
   * Sort.
   */
  private void sort() {
    Collections.sort(filteredGames, comparator);
  }

  /**
   * Gets the filtered games.
   * 
   * @return the filtered games
   */
  public List<Game> getFilteredGames() {
    return this.filteredGames;
  }

  /**
   * Filter games.
   * 
   * @param filter
   *          the filter
   */
  public void filterGames(HashMap<GameMatcher.SearchOptions, Object> filter) {
    GameMatcher matcher = new GameMatcher(filter);
    filteredGames.clear();
    for (int i = 0; i < games.size(); i++) {
      if (matcher.matches(games.get(i))) {
        filteredGames.add(games.get(i));
      }
    }
    sort();
    fireTableChanged(new TableModelEvent(this));
  }

}

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

import java.text.RuleBasedCollator;
import java.util.Comparator;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.ui.UTF8Control;

/**
 * The Class GameComparator is used to (initial) sort the games in the gamepanel.
 * 
 * @author Manuel Laggner
 */
public class GameExtendedComparator implements Comparator<Game> {
  private static final ResourceBundle BUNDLE         = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$
  private static final Logger         LOGGER         = LoggerFactory.getLogger(GameExtendedComparator.class);

  private SortColumn                  sortColumn;
  private boolean                     sortAscending;
  private RuleBasedCollator           stringCollator = (RuleBasedCollator) RuleBasedCollator.getInstance();

  /**
   * The Enum SortColumn.
   * 
   * @author Manuel Laggner
   */
  public enum SortColumn {
    /** The Title. */
    TITLE(BUNDLE.getString("metatag.title")), //$NON-NLS-1$,
    /** The Year. */
    YEAR(BUNDLE.getString("metatag.year")), //$NON-NLS-1$,
    /** The date added. */
    DATE_ADDED(BUNDLE.getString("metatag.dateadded")), //$NON-NLS-1$,
    /** The isFavorite. */
    WATCHED(BUNDLE.getString("metatag.isfavorite")), //$NON-NLS-1$,
    /** The rating. */
    RATING(BUNDLE.getString("metatag.rating")), //$NON-NLS-1$,
    /** The runtime. */
    RUNTIME(BUNDLE.getString("metatag.runtime")); //$NON-NLS-1$,

    /** The title. */
    private String title;

    /**
     * Instantiates a new sort column.
     * 
     * @param title
     *          the title
     */
    private SortColumn(String title) {
      this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Enum#toString()
     */
    public String toString() {
      return title;
    }
  }

  /**
   * The Enum IsFavoriteFlag.
   * 
   * @author Manuel Laggner
   */
  public enum IsFavoriteFlag {

    /** The isFavorite. */
    WATCHED(BUNDLE.getString("metatag.isfavorite")), //$NON-NLS-1$,
    /** The not isFavorite. */
    NOT_WATCHED(BUNDLE.getString("metatag.notisfavorite")); //$NON-NLS-1$,
    /** The title. */
    private String title;

    /**
     * Instantiates a new sort column.
     * 
     * @param title
     *          the title
     */
    private IsFavoriteFlag(String title) {
      this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Enum#toString()
     */
    public String toString() {
      return title;
    }
  }

  /**
   * The Enum SortOrder.
   * 
   * @author Manuel Laggner
   */
  public enum SortOrder {

    /** The ascending. */
    ASCENDING(BUNDLE.getString("sort.ascending")), //$NON-NLS-1$
    /** The descending. */
    DESCENDING(BUNDLE.getString("sort.descending")); //$NON-NLS-1$

    /** The title. */
    private String title;

    /**
     * Instantiates a new sort order.
     * 
     * @param title
     *          the title
     */
    private SortOrder(String title) {
      this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Enum#toString()
     */
    public String toString() {
      return title;
    }
  }

  /**
   * The Enum GameInGameSet.
   * 
   * @author Manuel Laggner
   */
  public enum GameInGameSet {

    /** The in gameset. */
    IN_MOVIESET(BUNDLE.getString("game.ingameset")), //$NON-NLS-1$
    /** The not in gameset. */
    NOT_IN_MOVIESET(BUNDLE.getString("game.notingameset")); //$NON-NLS-1$

    /** The title. */
    private String title;

    /**
     * Instantiates a new sort order.
     * 
     * @param title
     *          the title
     */
    private GameInGameSet(String title) {
      this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Enum#toString()
     */
    public String toString() {
      return title;
    }
  }

  /**
   * Instantiates a new game extended comparator.
   * 
   * @param sortColumn
   *          the sort column
   * @param sortAscending
   *          the sort ascending
   */
  public GameExtendedComparator(SortColumn sortColumn, boolean sortAscending) {
    this.sortColumn = sortColumn;
    this.sortAscending = sortAscending;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Game game1, Game game2) {
    int sortOrder = 0;

    try {
      // try to sort the chosen column
      switch (sortColumn) {
        case TITLE:
          sortOrder = stringCollator.compare(game1.getTitleSortable().toLowerCase(), game2.getTitleSortable().toLowerCase());
          break;

        case YEAR:
          sortOrder = stringCollator.compare(game1.getYear(), game2.getYear());
          break;

        case DATE_ADDED:
          sortOrder = game1.getDateAdded().compareTo(game2.getDateAdded());
          break;

        case WATCHED:
          Boolean isFavorite1 = Boolean.valueOf(game1.isIsFavorite());
          Boolean isFavorite2 = Boolean.valueOf(game2.isIsFavorite());
          sortOrder = isFavorite1.compareTo(isFavorite2);
          break;

        case RATING:
          sortOrder = Float.compare(game1.getRating(), game2.getRating());
          break;

        case RUNTIME:
          Integer runtime1 = Integer.valueOf(game1.getRuntime());
          Integer runtime2 = Integer.valueOf(game2.getRuntime());
          sortOrder = runtime1.compareTo(runtime2);
          break;
      }
    }
    catch (NullPointerException e) {
      // do nothing here. there could be
    }
    catch (Exception e) {
      LOGGER.warn(e.getMessage());
    }

    // sort ascending or descending
    if (sortAscending) {
      return sortOrder;
    }
    else {
      return sortOrder * -1;
    }
  }
}

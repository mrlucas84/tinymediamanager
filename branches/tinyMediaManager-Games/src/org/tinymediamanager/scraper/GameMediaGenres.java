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
package org.tinymediamanager.scraper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.tinymediamanager.core.Utils;
import org.tinymediamanager.ui.UTF8Control;

/**
 * The Class GameMediaGenres2.
 * 
 * @author Manuel Laggner
 */

public class GameMediaGenres extends DynaEnum<GameMediaGenres> {

  // List to be changed according public class GameMediaGenresScapers
  // ///////////

  public final static GameMediaGenres ACTION       = new GameMediaGenres("ACTION", 0, "Action");
  public final static GameMediaGenres ADVENTURE    = new GameMediaGenres("ADVENTURE", 1, "Adventure");
  public final static GameMediaGenres FIGHTING     = new GameMediaGenres("FIGHTING", 2, "Fighting");
  public final static GameMediaGenres PLATFORM     = new GameMediaGenres("PLATFORM", 3, "Platform");
  public final static GameMediaGenres PUZZLE       = new GameMediaGenres("PUZZLE", 4, "Puzzle");
  public final static GameMediaGenres RACING       = new GameMediaGenres("RACING", 5, "Racing");
  public final static GameMediaGenres ROLE_PLAYING = new GameMediaGenres("ROLE_PLAYING", 6, "Role-Playing");
  public final static GameMediaGenres SHOOTER      = new GameMediaGenres("SHOOTER", 7, "Shooter");
  public final static GameMediaGenres SIMULATION   = new GameMediaGenres("SIMULATION", 8, "Simulation");
  public final static GameMediaGenres SPORTS       = new GameMediaGenres("SPORTS", 9, "Sports");
  public final static GameMediaGenres STRATEGY     = new GameMediaGenres("STRATEGY", 10, "Strategy");
  public final static GameMediaGenres MISC         = new GameMediaGenres("MISC", 99, "Misc");

  /** The name. */
  private String                      name;

  /** The alternate names. */
  private String[]                    alternateNames;

  /**
   * Instantiates a new genres.
   * 
   * @param enumName
   *          the enum name
   * @param ordinal
   *          the ordinal
   * @param name
   *          the name
   * @param strings
   * @param alternateNames
   *          the alternate names
   */
  private GameMediaGenres(String enumName, int ordinal, String name) {
    super(enumName, ordinal);
    this.name = name;
    // System.out.println(enumName + " - " + name);
    this.alternateNames = loadAlternateNames(enumName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.core.DynaEnum#toString()
   */
  public String toString() {
    return this.getLocalizedName();
  }

  public String dump() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  /**
   * Iterates ofer all found languages ang gets the "alternative name" of specified property
   * 
   * @param propName
   *          the property
   * @return
   */
  public static String[] loadAlternateNames(String propName) {
    ArrayList<String> alt = new ArrayList<String>();
    for (Locale loc : Utils.getLanguages()) {
      if (loc.getLanguage().equals("en")) {
        // FIXME: all english is translated to german? wtf? but not needed, since EN has the name set...
        continue;
      }
      ResourceBundle b = ResourceBundle.getBundle("messages", loc, new UTF8Control()); //$NON-NLS-1$
      try {
        // System.out.println(" " + loc.getLanguage() + "-" + b.getString("Genres." + propName));
        alt.add(loc.getLanguage() + "-" + b.getString("Genres." + propName)); // just genres
      }
      catch (Exception e) {
        // not found or localized - ignore
      }
    }
    return alt.toArray(new String[alt.size()]);
  }

  /**
   * All the localized GameMediaGenres values, alphabetically sorted.
   * 
   * @return the media genres2[]
   */
  public static GameMediaGenres[] values() {
    Comparator<GameMediaGenres> comp = new GameMediaGenres.GameMediaGenresComparator();
    GameMediaGenres[] mg = values(GameMediaGenres.class);
    Arrays.sort(mg, comp);
    return mg;
  }

  /**
   * Gets the genre.
   * 
   * @param name
   *          the name
   * @return the genre
   */
  public static GameMediaGenres getGenre(String name) {
    for (GameMediaGenres genre : values()) {
      // check if the "enum" name matches
      if (genre.name().equals(name)) {
        return genre;
      }
      // check if the printable name matches
      if (genre.name.equalsIgnoreCase(name)) {
        return genre;
      }
      // check if one of the possible names matches
      for (String notation : genre.alternateNames) {
        if (notation.equalsIgnoreCase(name)) {
          return genre;
        }
        if (notation.length() > 3) {
          // first 3 chars are language like "de-"
          if (notation.substring(3).equalsIgnoreCase(name)) {
            return genre;
          }

          if (name.length() > 3) {
            // match both names without prefix
            if (notation.substring(3).equalsIgnoreCase(name.substring(3))) {
              return genre;
            }
          }
        }
      }
    }

    // dynamically create new one
    return new GameMediaGenres(name, values().length, name);
  }

  /**
   * Gets the genre name with default Locale<br>
   * or just name if not found<br>
   * eg: Name = "de-Abenteuer"
   * 
   * @return the localized genre
   */
  public String getLocalizedName() {
    String lang = Locale.getDefault().getLanguage() + "-";
    for (String notation : this.alternateNames) {
      if (notation.startsWith(lang)) {
        return notation.substring(3);
      }
    }
    return name;
  }

  /**
   * Comparator for sorting our GameMediaGenres in a localized fashion
   */
  public static class GameMediaGenresComparator implements Comparator<GameMediaGenres> {
    @Override
    public int compare(GameMediaGenres o1, GameMediaGenres o2) {
      // toString is localized name
      if (o1.toString() == null && o2.toString() == null) {
        return 0;
      }
      if (o1.toString() == null) {
        return 1;
      }
      if (o2.toString() == null) {
        return -1;
      }
      return o1.toString().compareTo(o2.toString());
    }
  }
}
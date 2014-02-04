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

import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameActor;
import org.tinymediamanager.scraper.GameMediaGenres;

/**
 * The Class GamesExtendedMatcher.
 * 
 * @author Manuel Laggner
 */
public class GameMatcher {

  /**
   * The Enum SearchOptions.
   * 
   * @author Manuel Laggner
   */
  public enum SearchOptions {
    DUPLICATES, WATCHED, GENRE, CAST, TAG, MOVIESET, VIDEO_FORMAT,
  }

  /** The search options. */
  private HashMap<SearchOptions, Object> searchOptions;

  /**
   * Instantiates a new games extended matcher.
   * 
   * @param searchOptions
   *          the search options
   */
  public GameMatcher(HashMap<SearchOptions, Object> searchOptions) {
    this.searchOptions = searchOptions;
  }

  /**
   * Matches.
   * 
   * @param game
   *          the game
   * @return true, if successful
   */
  public boolean matches(Game game) {
    // not null
    if (game == null) {
      return false;
    }

    // check duplicates
    if (searchOptions.containsKey(SearchOptions.DUPLICATES)) {
      if (!game.isDuplicate()) {
        return false;
      }
    }

    // check against isFavorite flag
    if (searchOptions.containsKey(SearchOptions.WATCHED)) {
      boolean isFavorite = (Boolean) searchOptions.get(SearchOptions.WATCHED);
      boolean result = !(game.isIsFavorite() ^ isFavorite);
      if (result == false) {
        return false;
      }
    }

    // check against genre
    if (searchOptions.containsKey(SearchOptions.GENRE)) {
      GameMediaGenres genre = (GameMediaGenres) searchOptions.get(SearchOptions.GENRE);
      if (!game.getGenres().contains(genre)) {
        return false;
      }
    }

    // check against cast member
    if (searchOptions.containsKey(SearchOptions.CAST)) {
      String castSearch = (String) searchOptions.get(SearchOptions.CAST);
      if (StringUtils.isNotEmpty(castSearch)) {
        Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(castSearch));
        java.util.regex.Matcher matcher = null;

        // director
        if (StringUtils.isNotEmpty(game.getDirector())) {
          matcher = pattern.matcher(game.getDirector());
          if (matcher.find()) {
            return true;
          }
        }

        // writer
        if (StringUtils.isNotEmpty(game.getWriter())) {
          matcher = pattern.matcher(game.getWriter());
          if (matcher.find()) {
            return true;
          }
        }

        // actors
        for (GameActor cast : game.getActors()) {
          if (StringUtils.isNotEmpty(cast.getName())) {
            matcher = pattern.matcher(cast.getName());
            if (matcher.find()) {
              return true;
            }
          }
        }
      }

      return false;
    }

    // check against tag
    if (searchOptions.containsKey(SearchOptions.TAG)) {
      String tag = (String) searchOptions.get(SearchOptions.TAG);

      for (String tagInGame : game.getTags()) {
        if (tagInGame.equals(tag)) {
          return true;
        }
      }

      return false;
    }

    // check against MOVIESET
    if (searchOptions.containsKey(SearchOptions.MOVIESET)) {
      Boolean isInSet = (Boolean) searchOptions.get(SearchOptions.MOVIESET);
      if ((game.getGameSet() != null) == isInSet) {
        return true;
      }

      return false;
    }

    // check against video format
    if (searchOptions.containsKey(SearchOptions.VIDEO_FORMAT)) {
      String videoFormat = (String) searchOptions.get(SearchOptions.VIDEO_FORMAT);
      if (videoFormat == game.getMediaInfoVideoFormat()) {
        return true;
      }
    }

    return true;
  }
}

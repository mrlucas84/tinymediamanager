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
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileAudioStream;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameActor;
import org.tinymediamanager.scraper.GameMediaGenres;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * The Class GamesExtendedMatcher.
 * 
 * @author Manuel Laggner
 */
public class GamesExtendedMatcher implements Matcher<Game> {

  /**
   * The Enum SearchOptions.
   * 
   * @author Manuel Laggner
   */
  public enum SearchOptions {
    DUPLICATES, WATCHED, GENRE, CAST, TAG, MOVIESET, PLATFORM, VIDEO_CODEC, AUDIO_CODEC
  }

  /** The search options. */
  private HashMap<SearchOptions, Object> searchOptions;

  /**
   * Instantiates a new games extended matcher.
   * 
   * @param searchOptions
   *          the search options
   */
  public GamesExtendedMatcher(HashMap<SearchOptions, Object> searchOptions) {
    this.searchOptions = searchOptions;
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.odell.glazedlists.matchers.Matcher#matches(java.lang.Object)
   */
  @Override
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

    // check against watched flag
    if (searchOptions.containsKey(SearchOptions.WATCHED)) {
      boolean watched = (Boolean) searchOptions.get(SearchOptions.WATCHED);
      boolean result = !(game.isIsFavorite() ^ watched);
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
      if (!containsCast(game, castSearch)) {
        return false;
      }
    }

    // check against tag
    if (searchOptions.containsKey(SearchOptions.TAG)) {
      String tag = (String) searchOptions.get(SearchOptions.TAG);
      if (!containsTag(game, tag)) {
        return false;
      }
    }

    // check against MOVIESET
    if (searchOptions.containsKey(SearchOptions.MOVIESET)) {
      Boolean isInSet = (Boolean) searchOptions.get(SearchOptions.MOVIESET);
      if ((game.getGameSet() != null) != isInSet) {
        return false;
      }
    }

    // check against platform
    if (searchOptions.containsKey(SearchOptions.PLATFORM)) {
      String platform = (String) searchOptions.get(SearchOptions.PLATFORM);
      if (!game.getPlatform().equalsIgnoreCase(platform)) {
        return false;
      }
    }

    // check against video codec
    if (searchOptions.containsKey(SearchOptions.VIDEO_CODEC)) {
      String videoCodec = (String) searchOptions.get(SearchOptions.VIDEO_CODEC);
      if (!videoCodec.equals(game.getMediaInfoVideoCodec())) {
        return false;
      }
    }

    // check against audio codec
    if (searchOptions.containsKey(SearchOptions.AUDIO_CODEC)) {
      String audioCodec = (String) searchOptions.get(SearchOptions.AUDIO_CODEC);
      if (!containsAudioCodec(game, audioCodec)) {
        return false;
      }
    }

    return true;
  }

  private boolean isVideoHD(String videoFormat) {
    if (videoFormat == MediaFile.VIDEO_FORMAT_720P) {
      return true;
    }
    if (videoFormat == MediaFile.VIDEO_FORMAT_1080P) {
      return true;
    }
    if (videoFormat == MediaFile.VIDEO_FORMAT_4K) {
      return true;
    }
    if (videoFormat == MediaFile.VIDEO_FORMAT_8K) {
      return true;
    }
    return false;
  }

  private boolean containsAudioCodec(Game game, String codec) {
    List<MediaFile> videoFiles = game.getMediaFiles(MediaFileType.GAME);

    if (videoFiles.size() == 0) {
      return false;
    }

    MediaFile mf = videoFiles.get(0);
    for (MediaFileAudioStream stream : mf.getAudioStreams()) {
      if (codec.equals(stream.getCodec())) {
        return true;
      }
    }

    return false;
  }

  private boolean containsTag(Game game, String tag) {
    for (String tagInGame : game.getTags()) {
      if (tagInGame.equals(tag)) {
        return true;
      }
    }

    return false;
  }

  private boolean containsCast(Game game, String name) {
    if (StringUtils.isNotEmpty(name)) {
      Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(name));
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
}

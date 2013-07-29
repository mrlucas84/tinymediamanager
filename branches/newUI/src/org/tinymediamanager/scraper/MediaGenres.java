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

/**
 * The Class MediaGenres2.
 * 
 * @author Manuel Laggner
 */
public class MediaGenres extends DynaEnum<MediaGenres> {
  /** The action. */
  public final static MediaGenres ACTION          = new MediaGenres("ACTION", 0, "Action", new String[] {});
  /** The adventure. */
  public final static MediaGenres ADVENTURE       = new MediaGenres("ADVENTURE", 1, "Adventure", new String[] { "Abenteuer" });
  /** The animation. */
  public final static MediaGenres ANIMATION       = new MediaGenres("ANIMATION", 2, "Animation", new String[] { "Anime", "Zeichentrick" });
  /** The animal. */
  public final static MediaGenres ANIMAL          = new MediaGenres("ANIMAL", 3, "Animal", new String[] { "Tierfilm" });
  /** The Biography. */
  public final static MediaGenres BIOGRAPHY       = new MediaGenres("BIOGRAPHY", 4, "Biography", new String[] { "Biographie" });
  /** The comedy. */
  public final static MediaGenres COMEDY          = new MediaGenres("COMEDY", 5, "Comedy", new String[] { "Komödie" });
  /** The crime. */
  public final static MediaGenres CRIME           = new MediaGenres("CRIME", 6, "Crime", new String[] { "Krimi" });
  /** The disaster. */
  public final static MediaGenres DISASTER        = new MediaGenres("DISASTER", 7, "Disaster", new String[] { "Katastrophen", "Katastrophenfilm" });
  /** The documentary. */
  public final static MediaGenres DOCUMENTARY     = new MediaGenres("DOCUMENTARY", 8, "Documentary", new String[] { "Dokumentation", "Mondo" });
  /** The drama. */
  public final static MediaGenres DRAMA           = new MediaGenres("DRAMA", 9, "Drama", new String[] {});
  /** The eastern. */
  public final static MediaGenres EASTERN         = new MediaGenres("EASTERN", 10, "Eastern", new String[] {});
  /** The erotic. */
  public final static MediaGenres EROTIC          = new MediaGenres("EROTIC", 11, "Erotic", new String[] { "Erotik", "Sex", "Adult" });
  /** The family. */
  public final static MediaGenres FAMILY          = new MediaGenres("FAMILY", 12, "Family", new String[] { "Kinder-/Familienfilm", "Familie",
      "Familienfilm"                             });
  /** The fan film. */
  public final static MediaGenres FAN_FILM        = new MediaGenres("FAN_FILM", 13, "Fan Film", new String[] { "Fan-Film" });
  /** The fantasy. */
  public final static MediaGenres FANTASY         = new MediaGenres("FANTASY", 14, "Fantasy", new String[] {});
  /** The film noir. */
  public final static MediaGenres FILM_NOIR       = new MediaGenres("FILM_NOIR", 15, "Film Noir", new String[] { "Film-Noir", "Neo-noir" });
  /** The foreign. */
  public final static MediaGenres FOREIGN         = new MediaGenres("FOREIGN", 16, "Foreign", new String[] {});
  /** The game show. */
  public final static MediaGenres GAME_SHOW       = new MediaGenres("GAME_SHOW", 17, "Gameshow", new String[] { "Game-Show" });
  /** The history. */
  public final static MediaGenres HISTORY         = new MediaGenres("HISTORY", 18, "History", new String[] { "Historienfilm", "Geschichte",
      "Historie"                                 });
  /** The holiday. */
  public final static MediaGenres HOLIDAY         = new MediaGenres("HOLIDAY", 19, "Holiday", new String[] {});
  /** The horror. */
  public final static MediaGenres HORROR          = new MediaGenres("HORROR", 20, "Horror", new String[] { "Splatter", "Grusel" });
  /** The indie. */
  public final static MediaGenres INDIE           = new MediaGenres("INDIE", 21, "Indie", new String[] { "Experimentalfilm", "Amateur" });
  /** The music. */
  public final static MediaGenres MUSIC           = new MediaGenres("MUSIC", 22, "Music", new String[] { "Musikfilm", "Musik" });
  /** The musical. */
  public final static MediaGenres MUSICAL         = new MediaGenres("MUSICAL", 23, "Musical", new String[] {});
  /** The mystery. */
  public final static MediaGenres MYSTERY         = new MediaGenres("MYSTERY", 24, "Mystery", new String[] {});
  /** The neo noir. */
  public final static MediaGenres NEO_NOIR        = new MediaGenres("NEO_NOIR", 25, "Neo Noir", new String[] {});
  /** The news. */
  public final static MediaGenres NEWS            = new MediaGenres("NEWS", 26, "News", new String[] { "Nachrichten" });
  /** The reality tv. */
  public final static MediaGenres REALITY_TV      = new MediaGenres("REALITY_TV", 27, "Reality TV", new String[] { "Reality-TV" });
  /** The road movie. */
  public final static MediaGenres ROAD_MOVIE      = new MediaGenres("ROAD_MOVIE", 28, "Road Movie", new String[] {});
  /** The romance. */
  public final static MediaGenres ROMANCE         = new MediaGenres("ROMANCE", 29, "Romance", new String[] { "Liebe/Romantik", "Romanze",
      "Lovestory", "Liebe", "Romantik"           });
  /** The science fiction. */
  public final static MediaGenres SCIENCE_FICTION = new MediaGenres("SCIENCE_FICTION", 30, "Science Fiction", new String[] { "Sci-Fi",
      "Science-Fiction"                          });
  /** The = new MediaGenres2(tv) series. */
  public final static MediaGenres SERIES          = new MediaGenres("SERIES", 31, "Series", new String[] { "Serie", "TV-Serie", "TV-Mini-Serie" });
  /** The short. */
  public final static MediaGenres SHORT           = new MediaGenres("SHORT", 32, "Short", new String[] { "Kurzfilm" });
  /** The silent ones. */
  public final static MediaGenres SILENT_MOVIE    = new MediaGenres("SILENT_MOVIE", 33, "Silent Movie", new String[] { "Stummfilm" });
  /** The sport. */
  public final static MediaGenres SPORT           = new MediaGenres("SPORT", 34, "Sport", new String[] { "Kampfsport" });
  /** The sporting event. */
  public final static MediaGenres SPORTING_EVENT  = new MediaGenres("SPORTING_EVENT", 35, "Sporting Event", new String[] { "Sportereignis" });
  /** The sports film. */
  public final static MediaGenres SPORTS_FILM     = new MediaGenres("SPORTS_FILM", 36, "Sports Film", new String[] { "Sportfilm", "Sport Film" });
  /** The suspense. */
  public final static MediaGenres SUSPENSE        = new MediaGenres("SUSPENSE", 37, "Suspense", new String[] {});
  /** The talk show. */
  public final static MediaGenres TALK_SHOW       = new MediaGenres("TALK_SHOW", 38, "Talk show", new String[] { "Talk-Show" });
  /** The tv movie. */
  public final static MediaGenres TV_MOVIE        = new MediaGenres("TV_MOVIE", 39, "TV Movie", new String[] { "TV-Film", "TV-Pilotfilm",
      "Heimatfilm"                               });
  /** The thriller. */
  public final static MediaGenres THRILLER        = new MediaGenres("THRILLER", 40, "Thriller", new String[] {});
  /** The war. */
  public final static MediaGenres WAR             = new MediaGenres("WAR", 41, "War", new String[] { "Krieg", "Kriegsfilm" });
  /** The western. */
  public final static MediaGenres WESTERN         = new MediaGenres("WESTERN", 42, "Western", new String[] {});

  /** The name. */
  private String                  name;

  /** The alternate names. */
  private String[]                alternateNames;

  /**
   * Instantiates a new genres.
   * 
   * @param enumName
   *          the enum name
   * @param ordinal
   *          the ordinal
   * @param name
   *          the name
   * @param alternateNames
   *          the alternate names
   */
  private MediaGenres(String enumName, int ordinal, String name, String[] alternateNames) {
    super(enumName, ordinal);
    this.name = name;
    this.alternateNames = alternateNames;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.core.DynaEnum#toString()
   */
  public String toString() {
    return this.name;
  }

  /**
   * Values.
   * 
   * @return the media genres2[]
   */
  public static MediaGenres[] values() {
    return values(MediaGenres.class);
  }

  /**
   * Gets the genre.
   * 
   * @param name
   *          the name
   * @return the genre
   */
  public static MediaGenres getGenre(String name) {
    for (MediaGenres genre : values()) {
      // check if the "enum" name matches
      if (genre.name().equals(name)) {
        return genre;
      }
      // check if the printable name matches
      if (genre.name.equals(name)) {
        return genre;
      }
      // check if one of the possible names matches
      for (String notation : genre.alternateNames) {
        if (notation.equalsIgnoreCase(name)) {
          return genre;
        }
      }
    }

    // dynamically create new one
    return new MediaGenres(name, values().length, name, new String[] {});
  }
}
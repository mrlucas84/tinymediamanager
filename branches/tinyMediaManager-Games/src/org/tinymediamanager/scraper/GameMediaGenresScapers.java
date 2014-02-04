/*
 * Copyright 2012 - 2013 Manuel Laggner
 *
 * Licensed under the Apache License, Version 2.0 (the "License"),
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
 * The Class GameMediaGenres2.
 * 
 * @author Manuel Laggner
 */
public class GameMediaGenresScapers {
  // @formatter:off
private static enum Genres {
   ACTION             ( true, "ADVENTURE",0, "Action", new String[] { "Action" }),
   ADVENTURE          ( true, "ADVENTURE", 1, "Adventure", new String[] { "Adventure" }),
   FIGHTING           ( true, "FIGHTING", 2, "Fighting", new String[] { "Fighting" }),
   PLATFORM           ( true, "PLATFORM", 3, "Platform", new String[] { "Platform" }),
   PUZZLE             ( true, "PUZZLE", 4, "Puzzle", new String[] { "Puzzle" }),
   RACING             ( true, "RACING", 5, "Racing", new String[] { "Racing" }),
   ROLE_PLAYING       ( true, "ROLE_PLAYING", 6, "Role-Playing", new String[] { "Role-Playing" }),
   SHOOTER            ( true, "SHOOTER", 7, "Shooter", new String[] { "Shooter" }),
   SIMULATION         ( true, "SIMULATION", 8, "Simulation", new String[] { "Simulation" }),
   SPORTS             ( true, "SPORTS", 9, "Sports", new String[] { "Sports" }),
   STRATEGY           ( true, "STRATEGY", 10, "Strategy", new String[] { "Strategy" }),
   MISC               ( true, "MISC", 99, "Misc", new String[] { "Misc" }),
   
   NOT_RATED          ( false, "NOT_RATED", 100, "Not Rated", new String[] { "Not rated" }),


   AVENTURE           ( false, "AVENTURE", 1, "Aventure", new String[] { "Aventure" }),
   ADRESSE            ( false,"ADRESSE", 99, "Adresse", new String[] { "Adresse" }),
   ARCADE             ( false,"ARCADE", 3, "Arcade", new String[] { "Arcade" }),
   BEATHEMALL         ( false,"BEATHEMALL", 0, "Beat'em all", new String[] { "Beat'em all" }),
   BLOCKER            ( false,"BLOCKER", 99, "Casse Briques", new String[] { "Casse Briques" }),
   COURSE             ( false,"COURSE", 9, "Course", new String[] { "Course" }),
   COACHING           ( false,"COACHING", 99, "Coaching", new String[] { "Coaching" }),
   COMPIL             ( false,"COMPIL", 99, "Compilation", new String[] { "Compilation" }),
   CREATE             ( false,"CREATE", 99, "Création", new String[] { "Création" }),
   DRAGUE             ( false,"DRAGUE", 99, "Drague", new String[] { "Drague" }),
   FLIPPER            ( false,"FLIPPER", 8, "Flipper", new String[] { "Flipper" }),
   FPS                ( false,"FPS", 7, "Flipper", new String[] { "FPS" }),
   GESTION            ( false,"GESTION", 99, "Gestion", new String[] { "Gestion" }),
   INFIL              ( false,"INFIL", 99, "Infiltration", new String[] { "Infiltration" }),
   CARTE              ( false,"CARTE", 99, "Jeu de cartes", new String[] { "Jeu de cartes" }),
   SOC                ( false,"SOC", 99, "Jeu de société", new String[] { "Jeu de société" }),
   JEUXDEROLE         ( false,"JEUXDEROLE", 6, "JeudeRôle", new String[] { "JeudeRôle" }),
   LUDO               ( false,"LUDO", 99, "Ludo-éducatif", new String[] { "Ludo-éducatif" }),
   MMO                ( false,"MMO", 1, "MMO", new String[] { "MMO" }),
   PLATEFORME         ( false,"PLATEFORME", 3, "Plates-formes", new String[] { "Plates-formes" }),
   PARTY              ( false,"PARTY", 99, "Party Game", new String[] { "Party Game" }),
   POINT              ( false,"POINT", 99, "Point&Click", new String[] { "Point&Click" }),
   REFLEXION          ( false,"REFLEXION", 99, "Point&Click", new String[] { "Réflexion" }),
   RYTHME             ( false,"RYTHME", 99, "Rythme", new String[] { "Rythme" }),
   SHOOTEMUP          ( false,"SHOOTEMUP", 7, "Shoot'em up", new String[] { "Shoot'em up" }),
   SIMUL              ( false,"SIMUL", 99, "Simulation", new String[] { "Simulation" }),
   SURVIVAL           ( false,"SURVIVAL", 99, "Survival-Horror", new String[] { "Survival-Horror" }),
   TACTIQUE           ( false,"TACTIQUE", 99, "Tactique", new String[] { "Tactique" }),
   WARGAME            ( false,"WARGAME", 10, "Wargame", new String[] { "Wargame" }),

  // Specific
   DRIVING_RACING     ( false,"DRIVING_RACING", 9, "Driving/Racing", new String[] { "Driving/Racing" }),
   EDUCATIONAL        ( false,"EDUCATIONAL", 99, "Educational", new String[] { "Educational" }),
   WRESTLING          ( false,"WRESTLING", 9, "Wrestling", new String[] { "Wrestling" }),
   REAL_TIME_STRATEGY ( false,"REAL_TIME_STRATEGY", 99, "Real-Time Strategy",
                                                             new String[] { "Real-Time Strategy" }),
   CARD_NAME          ( false,"CARD_NAME", 99, "Card Game", new String[] { "Card Game" }),
   TRIVIA             ( false,"TRIVIA", 99, "Trivia/Board Game",
                                                             new String[] { "Trivia/Board Game" }),
   COMPILATION        ( false,"COMPILATION", 99, "Compilation", new String[] { "Compilation" }),
   MMORPG             ( false,"MMORPG", 99, "MMORPG", new String[] { "MMORPG" }),
   MINI_GAME          ( false,"MINI_GAME", 99, "Minigame Collection",
                                                             new String[] { "Minigame Collection" }),
   MUSIC              ( false,"MUSIC", 99, "Music/Rhythm", new String[] { "Music/Rhythm" }),
   BOXING             ( false,"BOXING", 9, "Boxing", new String[] { "Boxing" }),
   FOOTBALL           ( false,"FOOTBALL", 9, "Football", new String[] { "Football" }),
   BASCKETBALL        ( false,"BASCKETBALL", 9, "Basketball", new String[] { "Basketball" }),

   FLIGHT             ( false,"FLIGHT", 9, "Flight Simulator", new String[] { "Flight Simulator" }),
   TENNIS             ( false,"TENNIS", 9, "Tennis", new String[] { "Tennis" }),
   BILLARDS           ( false,"BILLARDS", 9, "Billiards", new String[] { "Billiards" }),
   FISHING            ( false,"FISHING", 9, "Fishing", new String[] { "Fishing" }),
   GOLF               ( false,"GOLF", 9, "Golf", new String[] { "Golf" }),
   BOWLING            ( false,"BOWLING", 9, "Bowling", new String[] { "Bowling" }),
   PINBALL            ( false,"PINBALL", 9, "Pinball", new String[] { "Pinball" }),
   DUAL_SHOOTER       ( false,"DUAL_SHOOTER", 9, "Dual-Joystick Shooter", new String[] { "Dual-Joystick Shooter" }),
   FIRST_SHOOTER      ( false,"FIRST_SHOOTER", 7, "First-Person Shooter",  new String[] { "First-Person Shooter" }),
   SNOWBOARD          ( false,"SNOWBOARD", 9, "Snowboarding/Skiing", new String[] { "Snowboarding/Skiing" }),

   BASEBALL           ( false,"BASEBALL", 9, "Baseball", new String[] { "Baseball" }),
   LIGHTGUN           ( false,"LIGHTGUN", 99, "Light-Gun Shooter", new String[] { "Light-Gun Shooter" }),
   TEXT_ADVENTURE     ( false,"TEXT_ADVENTURE", 1, "Text Adventure",     new String[] { "Text Adventure" }),
   BRAWLER            ( false,"BRAWLER", 99, "Brawler", new String[] { "Brawler" }),
   VEHICULE_COMBAT    ( false,"VEHICULE_COMBAT", 99, "Vehicular Combat", new String[] { "Vehicular Combat" }),
   HOCKEY             ( false,"HOCKEY", 9, "Hockey", new String[] { "Hockey" }),
   SOCCER             ( false,"SOCCER", 9, "Soccer", new String[] { "Soccer" }),
   PLATFORMER         ( false,"PLATFORMER", 9, "Platformer", new String[] { "Platformer" }),
   TRACK              ( false,"TRACK", 9, "Track & Field", new String[] { "Track & Field" }),
   ACTION_ADVENTURE   ( false,"ACTION_ADVENTURE", 1, "Action-Adventure", new String[] { "Action-Adventure" }),
   FITNESS            ( false,"FITNESS", 99, "Fitness", new String[] { "Fitness" }),
   BLOCK_BREAKING     ( false,"BLOCK_BREAKING", 99, "Block-Breaking", new String[] { "Block-Breaking" }),
   CRICKET            ( false,"CRICKET", 9, "Cricket", new String[] { "Cricket" }),
   SURFING            ( false,"SURFING", 9, "Surfing", new String[] { "Surfing" }),
   STEALH              ( false,"STEALH", 1, "Stealth", new String[] { "Stealth" }),
   SHOOT              ( false,"SHOOT", 9, "Shoot 'Em Up", new String[] { "Shoot 'Em Up" }),
   GAMBLING           ( false,"GAMBLING", 99, "Gambling", new String[] { "Gambling" });

   private boolean                     isGameMediaGenres;
   
   private String                      name1;
   /** The id. */
   private Integer  id;

  private String                      name;

  /** The alternate names. */
  private String[]                    possibleNotations;
  
  private Genres(boolean isGameMediaGenres, String name1, Integer id, String name, String[] possibleNotations) {
    this.isGameMediaGenres = isGameMediaGenres;
    this.name1 = name1;
    this.name = name;
    this.id = id;
    this.possibleNotations = possibleNotations;
  }
}
//@formatter:on

  private static String getGameMediaGenres(Integer id) {
    for (Genres genre : Genres.values()) {
      if (genre.isGameMediaGenres && genre.id == id)
        return genre.name;
    }
    return "NOT_RATED";
  }

  public static GameMediaGenres getGenre(String str) {
    for (Genres genre : Genres.values()) {
      for (String v : genre.possibleNotations) {
        if (v.equalsIgnoreCase(str)) {
          String s = getGameMediaGenres(genre.id);
          return GameMediaGenres.getGenre(s);
        }
      }
    }
    return GameMediaGenres.MISC;
  }
}

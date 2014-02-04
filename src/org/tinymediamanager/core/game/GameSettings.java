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
package org.tinymediamanager.core.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jdesktop.observablecollections.ObservableCollections;
import org.tinymediamanager.core.AbstractModelObject;
import org.tinymediamanager.core.game.connector.GameConnectors;
import org.tinymediamanager.core.platform.Platforms;
import org.tinymediamanager.scraper.CountryCode;
import org.tinymediamanager.scraper.MediaArtwork.FanartSizes;
import org.tinymediamanager.scraper.MediaArtwork.PosterSizes;
import org.tinymediamanager.scraper.MediaLanguages;

/**
 * The Class GameSettings.
 */
@XmlRootElement(name = "GameSettings")
public class GameSettings extends AbstractModelObject {
  private final static String             PATH                                  = "path";
  private final static String             FILETYPE                              = "filetype";
  private final static String             FILENAME                              = "filename";
  private final static String             GAME_DATA_SOURCE                      = "gameDataSource";
  private final static String             IMAGE_POSTER_SIZE                     = "imagePosterSize";
  private final static String             IMAGE_FANART_SIZE                     = "imageFanartSize";
  private final static String             IMAGE_EXTRATHUMBS                     = "imageExtraThumbs";
  private final static String             IMAGE_EXTRATHUMBS_RESIZE              = "imageExtraThumbsResize";
  private final static String             IMAGE_EXTRATHUMBS_SIZE                = "imageExtraThumbsSize";
  private final static String             IMAGE_EXTRATHUMBS_COUNT               = "imageExtraThumbsCount";
  private final static String             IMAGE_EXTRAFANART                     = "imageExtraFanart";
  private final static String             IMAGE_EXTRAFANART_COUNT               = "imageExtraFanartCount";
  private final static String             ENABLE_GAMESET_ARTWORK_FOLDER         = "enableGameSetArtworkFolder";
  private final static String             GAMESET_ARTWORK_FOLDER                = "gameSetArtworkFolder";
  private final static String             GAME_CONNECTOR                        = "gameConnector";
  private final static String             GAME_NFO_FILENAME                     = "gameNfoFilename";
  private final static String             GAME_POSTER_FILENAME                  = "gamePosterFilename";
  private final static String             GAME_FANART_FILENAME                  = "gameFanartFilename";
  private final static String             GAME_RENAMER_PATHNAME                 = "gameRenamerPathname";
  private final static String             GAME_RENAMER_FILENAME                 = "gameRenamerFilename";
  private final static String             GAME_RENAMER_SPACE_SUBSTITUTION       = "gameRenamerSpaceSubstitution";
  private final static String             GAME_RENAMER_SPACE_REPLACEMENT        = "gameRenamerSpaceReplacement";
  private final static String             GAME_RENAMER_NFO_CLEANUP              = "gameRenamerNfoCleanup";
  private final static String             GAME_RENAMER_GAMESET_SINGLE_GAME      = "gameRenamerGamesetSingleGame";
  private final static String             GAME_SCRAPER                          = "gameScraper";
  private final static String             SCRAPE_BEST_IMAGE                     = "scrapeBestImage";
  private final static String             GAME_FILE_TYPES                       = "gameFileTypes";
  private final static String             GAME_PLATFORMS                        = "gamePlatforms";
  private final static String             PLATFORM                              = "platform";
  private final static String             IMAGE_SCRAPER_GIANTBOMB               = "imageScraperGiantBomb";
  private final static String             IMAGE_SCRAPER_GAMEDB                  = "imageScraperGameDB";
  private final static String             IMAGE_SCRAPER_JEUXVIDEO               = "imageScraperJeuxVideo";
  private final static String             TRAILER_SCRAPER_GIANTBOMB             = "trailerScraperGiantBomb";
  private final static String             TRAILER_SCRAPER_GAMEDB                = "trailerScraperGameDB";
  private final static String             TRAILER_SCRAPER_JEUXVIDEO             = "trailerScraperJeuxVideo";
  private final static String             WRITE_ACTOR_IMAGES                    = "writeActorImages";
  private final static String             IMDB_SCRAPE_FOREIGN_LANGU             = "imdbScrapeForeignLanguage";
  private final static String             SCRAPER_LANGU                         = "scraperLanguage";
  private final static String             CERTIFICATION_COUNTRY                 = "certificationCountry";
  private final static String             SCRAPER_THRESHOLD                     = "scraperThreshold";
  private final static String             DETECT_GAME_MULTI_DIR                 = "detectGameMultiDir";
  private final static String             BUILD_IMAGE_CACHE_ON_IMPORT           = "buildImageCacheOnImport";
  private final static String             ROM_COLLECTION                        = "romCollection";

  @XmlElementWrapper(name = GAME_DATA_SOURCE)
  @XmlElement(name = PATH)
  private final List<String>              gameDataSources                       = ObservableCollections.observableList(new ArrayList<String>());

  @XmlElementWrapper(name = GAME_FILE_TYPES)
  @XmlElement(name = FILETYPE)
  private final List<String>              gameFileTypes                         = ObservableCollections.observableList(new ArrayList<String>());

  @XmlElementWrapper(name = GAME_PLATFORMS)
  @XmlElement(name = PLATFORM)
  private final List<String>              gamePlatforms                         = ObservableCollections.observableList(new ArrayList<String>());

  @XmlElementWrapper(name = ROM_COLLECTION)
  private final List<RomCollectionConfig> romCollectionConfig                   = ObservableCollections
                                                                                    .observableList(new ArrayList<RomCollectionConfig>());
  @XmlElementWrapper(name = GAME_NFO_FILENAME)
  @XmlElement(name = FILENAME)
  private final List<GameNfoNaming>       gameNfoFilenames                      = new ArrayList<GameNfoNaming>();

  @XmlElementWrapper(name = GAME_POSTER_FILENAME)
  @XmlElement(name = FILENAME)
  private final List<GamePosterNaming>    gamePosterFilenames                   = new ArrayList<GamePosterNaming>();

  @XmlElementWrapper(name = GAME_FANART_FILENAME)
  @XmlElement(name = FILENAME)
  private final List<GameFanartNaming>    gameFanartFilenames                   = new ArrayList<GameFanartNaming>();

  private GameConnectors                  gameConnector                         = GameConnectors.XBMC;
  private String                          gameRenamerPathname                   = "$T ($Y)";
  private String                          gameRenamerFilename                   = "$T ($Y) $V $A";
  private boolean                         gameRenamerSpaceSubstitution          = false;
  private String                          gameRenamerSpaceReplacement           = "_";
  private boolean                         gameRenamerNfoCleanup                 = false;
  private boolean                         imdbScrapeForeignLanguage             = false;
  private GameScrapers                    gameScraper                           = GameScrapers.GIANTBOMB;
  private PosterSizes                     imagePosterSize                       = PosterSizes.BIG;
  private boolean                         imageScraperGiantBomb                 = true;
  private boolean                         imageScraperGameDB                    = false;
  private boolean                         imageScraperJeuxVideo                 = false;
  private FanartSizes                     imageFanartSize                       = FanartSizes.LARGE;
  private boolean                         imageExtraThumbs                      = false;
  private boolean                         imageExtraThumbsResize                = true;
  private int                             imageExtraThumbsSize                  = 300;
  private int                             imageExtraThumbsCount                 = 5;
  private boolean                         imageExtraFanart                      = false;
  private int                             imageExtraFanartCount                 = 5;
  private boolean                         enableGameSetArtworkFolder            = false;
  private String                          gameSetArtworkFolder                  = "GamesetArtwork";
  private boolean                         scrapeBestImage                       = true;
  private boolean                         trailerScraperGiantBomb               = true;
  private boolean                         trailerScraperGameDB                  = false;
  private boolean                         trailerScraperJeuxVideo               = false;
  private boolean                         writeActorImages                      = false;
  private MediaLanguages                  scraperLanguage                       = MediaLanguages.en;
  private CountryCode                     certificationCountry                  = CountryCode.US;
  private double                          scraperThreshold                      = 0.75;
  private boolean                         detectGameMultiDir                    = false;
  private boolean                         buildImageCacheOnImport               = false;
  private boolean                         gameRenamerCreateGamesetForSingleGame = false;

  public GameSettings() {
  }

  public void addGameDataSources(String path) {
    if (!gameDataSources.contains(path)) {
      gameDataSources.add(path);
      firePropertyChange(GAME_DATA_SOURCE, null, gameDataSources);
    }
  }

  public void removeGameDataSources(String path) {
    GameList gameList = GameList.getInstance();
    gameList.removeDatasource(path);
    gameDataSources.remove(path);
    firePropertyChange(GAME_DATA_SOURCE, null, gameDataSources);
  }

  public List<String> getGameDataSource() {
    return gameDataSources;
  }

  public void addGameNfoFilename(GameNfoNaming filename) {
    if (!gameNfoFilenames.contains(filename)) {
      gameNfoFilenames.add(filename);
      firePropertyChange(GAME_NFO_FILENAME, null, gameNfoFilenames);
    }
  }

  public void removeGameNfoFilename(GameNfoNaming filename) {
    if (gameNfoFilenames.contains(filename)) {
      gameNfoFilenames.remove(filename);
      firePropertyChange(GAME_NFO_FILENAME, null, gameNfoFilenames);
    }
  }

  public void clearGameNfoFilenames() {
    gameNfoFilenames.clear();
    firePropertyChange(GAME_NFO_FILENAME, null, gameNfoFilenames);
  }

  public List<GameNfoNaming> getGameNfoFilenames() {
    return this.gameNfoFilenames;
  }

  public void addGamePosterFilename(GamePosterNaming filename) {
    if (!gamePosterFilenames.contains(filename)) {
      gamePosterFilenames.add(filename);
      firePropertyChange(GAME_POSTER_FILENAME, null, gamePosterFilenames);
    }
  }

  public void removeGamePosterFilename(GamePosterNaming filename) {
    if (gamePosterFilenames.contains(filename)) {
      gamePosterFilenames.remove(filename);
      firePropertyChange(GAME_POSTER_FILENAME, null, gamePosterFilenames);
    }
  }

  public void clearGamePosterFilenames() {
    gamePosterFilenames.clear();
    firePropertyChange(GAME_POSTER_FILENAME, null, gamePosterFilenames);
  }

  public List<GamePosterNaming> getGamePosterFilenames() {
    return this.gamePosterFilenames;
  }

  public void addGameFanartFilename(GameFanartNaming filename) {
    if (!gameFanartFilenames.contains(filename)) {
      gameFanartFilenames.add(filename);
      firePropertyChange(GAME_FANART_FILENAME, null, gameFanartFilenames);
    }
  }

  public void removeGameFanartFilename(GameFanartNaming filename) {
    if (gameFanartFilenames.contains(filename)) {
      gameFanartFilenames.remove(filename);
      firePropertyChange(GAME_FANART_FILENAME, null, gameFanartFilenames);
    }
  }

  public void clearGameFanartFilenames() {
    gameFanartFilenames.clear();
    firePropertyChange(GAME_FANART_FILENAME, null, gameFanartFilenames);
  }

  public List<GameFanartNaming> getGameFanartFilenames() {
    return this.gameFanartFilenames;
  }

  @XmlElement(name = IMAGE_POSTER_SIZE)
  public PosterSizes getImagePosterSize() {
    return imagePosterSize;
  }

  public void setImagePosterSize(PosterSizes newValue) {
    PosterSizes oldValue = this.imagePosterSize;
    this.imagePosterSize = newValue;
    firePropertyChange(IMAGE_POSTER_SIZE, oldValue, newValue);
  }

  @XmlElement(name = IMAGE_FANART_SIZE)
  public FanartSizes getImageFanartSize() {
    return imageFanartSize;
  }

  public void setImageFanartSize(FanartSizes newValue) {
    FanartSizes oldValue = this.imageFanartSize;
    this.imageFanartSize = newValue;
    firePropertyChange(IMAGE_FANART_SIZE, oldValue, newValue);
  }

  public boolean isImageExtraThumbs() {
    return imageExtraThumbs;
  }

  public boolean isImageExtraThumbsResize() {
    return imageExtraThumbsResize;
  }

  public int getImageExtraThumbsSize() {
    return imageExtraThumbsSize;
  }

  public void setImageExtraThumbsResize(boolean newValue) {
    boolean oldValue = this.imageExtraThumbsResize;
    this.imageExtraThumbsResize = newValue;
    firePropertyChange(IMAGE_EXTRATHUMBS_RESIZE, oldValue, newValue);
  }

  public void setImageExtraThumbsSize(int newValue) {
    int oldValue = this.imageExtraThumbsSize;
    this.imageExtraThumbsSize = newValue;
    firePropertyChange(IMAGE_EXTRATHUMBS_SIZE, oldValue, newValue);
  }

  public int getImageExtraThumbsCount() {
    return imageExtraThumbsCount;
  }

  public void setImageExtraThumbsCount(int newValue) {
    int oldValue = this.imageExtraThumbsCount;
    this.imageExtraThumbsCount = newValue;
    firePropertyChange(IMAGE_EXTRATHUMBS_COUNT, oldValue, newValue);
  }

  public int getImageExtraFanartCount() {
    return imageExtraFanartCount;
  }

  public void setImageExtraFanartCount(int newValue) {
    int oldValue = this.imageExtraFanartCount;
    this.imageExtraFanartCount = newValue;
    firePropertyChange(IMAGE_EXTRAFANART_COUNT, oldValue, newValue);
  }

  public boolean isImageExtraFanart() {
    return imageExtraFanart;
  }

  public void setImageExtraThumbs(boolean newValue) {
    boolean oldValue = this.imageExtraThumbs;
    this.imageExtraThumbs = newValue;
    firePropertyChange(IMAGE_EXTRATHUMBS, oldValue, newValue);
  }

  public void setImageExtraFanart(boolean newValue) {
    boolean oldValue = this.imageExtraFanart;
    this.imageExtraFanart = newValue;
    firePropertyChange(IMAGE_EXTRAFANART, oldValue, newValue);
  }

  public boolean isEnableGameSetArtworkFolder() {
    return enableGameSetArtworkFolder;
  }

  public void setEnableGameSetArtworkFolder(boolean newValue) {
    boolean oldValue = this.enableGameSetArtworkFolder;
    this.enableGameSetArtworkFolder = newValue;
    firePropertyChange(ENABLE_GAMESET_ARTWORK_FOLDER, oldValue, newValue);
  }

  public String getGameSetArtworkFolder() {
    return gameSetArtworkFolder;
  }

  public void setGameSetArtworkFolder(String newValue) {
    String oldValue = this.gameSetArtworkFolder;
    this.gameSetArtworkFolder = newValue;
    firePropertyChange(GAMESET_ARTWORK_FOLDER, oldValue, newValue);
  }

  @XmlElement(name = GAME_CONNECTOR)
  public GameConnectors getGameConnector() {
    return gameConnector;
  }

  public void setGameConnector(GameConnectors newValue) {
    GameConnectors oldValue = this.gameConnector;
    this.gameConnector = newValue;
    firePropertyChange(GAME_CONNECTOR, oldValue, newValue);
  }

  @XmlElement(name = GAME_RENAMER_PATHNAME)
  public String getGameRenamerPathname() {
    return gameRenamerPathname;
  }

  public void setGameRenamerPathname(String newValue) {
    String oldValue = this.gameRenamerPathname;
    this.gameRenamerPathname = newValue;
    firePropertyChange(GAME_RENAMER_PATHNAME, oldValue, newValue);
  }

  @XmlElement(name = GAME_RENAMER_FILENAME)
  public String getGameRenamerFilename() {
    return gameRenamerFilename;
  }

  public void setGameRenamerFilename(String newValue) {
    String oldValue = this.gameRenamerFilename;
    this.gameRenamerFilename = newValue;
    firePropertyChange(GAME_RENAMER_FILENAME, oldValue, newValue);
  }

  @XmlElement(name = GAME_RENAMER_SPACE_SUBSTITUTION)
  public boolean isGameRenamerSpaceSubstitution() {
    return gameRenamerSpaceSubstitution;
  }

  public void setGameRenamerSpaceSubstitution(boolean gameRenamerSpaceSubstitution) {
    this.gameRenamerSpaceSubstitution = gameRenamerSpaceSubstitution;
  }

  @XmlElement(name = GAME_RENAMER_SPACE_REPLACEMENT)
  public String getGameRenamerSpaceReplacement() {
    return gameRenamerSpaceReplacement;
  }

  public void setGameRenamerSpaceReplacement(String gameRenamerSpaceReplacement) {
    this.gameRenamerSpaceReplacement = gameRenamerSpaceReplacement;
  }

  public GameScrapers getGameScraper() {
    if (gameScraper == null) {
      return GameScrapers.GIANTBOMB;
    }
    return gameScraper;
  }

  public void setGameScraper(GameScrapers newValue) {
    GameScrapers oldValue = this.gameScraper;
    this.gameScraper = newValue;
    firePropertyChange(GAME_SCRAPER, oldValue, newValue);
  }

  public boolean isImdbScrapeForeignLanguage() {
    return imdbScrapeForeignLanguage;
  }

  public void setImdbScrapeForeignLanguage(boolean newValue) {
    boolean oldValue = this.imdbScrapeForeignLanguage;
    this.imdbScrapeForeignLanguage = newValue;
    firePropertyChange(IMDB_SCRAPE_FOREIGN_LANGU, oldValue, newValue);
  }

  public boolean isScrapeBestImage() {
    return scrapeBestImage;
  }

  public void setScrapeBestImage(boolean newValue) {
    boolean oldValue = this.scrapeBestImage;
    this.scrapeBestImage = newValue;
    firePropertyChange(SCRAPE_BEST_IMAGE, oldValue, newValue);
  }

  @XmlElement(name = IMAGE_SCRAPER_GIANTBOMB)
  public boolean isImageScraperGiantBomb() {
    return imageScraperGiantBomb;
  }

  @XmlElement(name = IMAGE_SCRAPER_GAMEDB)
  public boolean isImageScraperGameDB() {
    return imageScraperGameDB;
  }

  @XmlElement(name = IMAGE_SCRAPER_JEUXVIDEO)
  public boolean isImageScraperJeuxVideo() {
    return imageScraperJeuxVideo;
  }

  @XmlElement(name = TRAILER_SCRAPER_GIANTBOMB)
  public boolean isTrailerScraperGiantBomb() {
    return trailerScraperGiantBomb;
  }

  @XmlElement(name = TRAILER_SCRAPER_GAMEDB)
  public boolean isTrailerScraperGameDB() {
    return trailerScraperGameDB;
  }

  @XmlElement(name = TRAILER_SCRAPER_JEUXVIDEO)
  public boolean isTrailerScraperJeuxVideo() {
    return trailerScraperJeuxVideo;
  }

  public void setTrailerScraperGiantBomb(boolean newValue) {
    boolean oldValue = this.trailerScraperGiantBomb;
    this.trailerScraperGiantBomb = newValue;
    firePropertyChange(TRAILER_SCRAPER_GIANTBOMB, oldValue, newValue);
  }

  public void setTrailerScraperGameDB(boolean newValue) {
    boolean oldValue = this.trailerScraperGameDB;
    this.trailerScraperGameDB = newValue;
    firePropertyChange(TRAILER_SCRAPER_GAMEDB, oldValue, newValue);
  }

  public void setTrailerScraperJeuxVideo(boolean newValue) {
    boolean oldValue = this.trailerScraperJeuxVideo;
    this.trailerScraperJeuxVideo = newValue;
    firePropertyChange(TRAILER_SCRAPER_JEUXVIDEO, oldValue, newValue);
  }

  public void setImageScraperGiantBomb(boolean newValue) {
    boolean oldValue = this.imageScraperGiantBomb;
    this.imageScraperGiantBomb = newValue;
    firePropertyChange(IMAGE_SCRAPER_GIANTBOMB, oldValue, newValue);
  }

  public void setImageScraperGameDB(boolean newValue) {
    boolean oldValue = this.imageScraperGameDB;
    this.imageScraperGameDB = newValue;
    firePropertyChange(IMAGE_SCRAPER_GAMEDB, oldValue, newValue);
  }

  public void setImageScraperJeuxVideo(boolean newValue) {
    boolean oldValue = this.imageScraperJeuxVideo;
    this.imageScraperJeuxVideo = newValue;
    firePropertyChange(IMAGE_SCRAPER_JEUXVIDEO, oldValue, newValue);
  }

  // Scraper End

  public boolean isWriteActorImages() {
    return writeActorImages;
  }

  public void setWriteActorImages(boolean newValue) {
    boolean oldValue = this.writeActorImages;
    this.writeActorImages = newValue;
    firePropertyChange(WRITE_ACTOR_IMAGES, oldValue, newValue);
  }

  @XmlElement(name = SCRAPER_LANGU)
  public MediaLanguages getScraperLanguage() {
    return scraperLanguage;
  }

  public void setScraperLanguage(MediaLanguages newValue) {
    MediaLanguages oldValue = this.scraperLanguage;
    this.scraperLanguage = newValue;
    firePropertyChange(SCRAPER_LANGU, oldValue, newValue);
  }

  @XmlElement(name = CERTIFICATION_COUNTRY)
  public CountryCode getCertificationCountry() {
    return certificationCountry;
  }

  public void setCertificationCountry(CountryCode newValue) {
    CountryCode oldValue = this.certificationCountry;
    certificationCountry = newValue;
    firePropertyChange(CERTIFICATION_COUNTRY, oldValue, newValue);
  }

  @XmlElement(name = SCRAPER_THRESHOLD)
  public double getScraperThreshold() {
    return scraperThreshold;
  }

  public void setScraperThreshold(double newValue) {
    double oldValue = this.scraperThreshold;
    scraperThreshold = newValue;
    firePropertyChange(SCRAPER_THRESHOLD, oldValue, newValue);
  }

  @XmlElement(name = GAME_RENAMER_NFO_CLEANUP)
  public boolean isGameRenamerNfoCleanup() {
    return gameRenamerNfoCleanup;
  }

  public void setGameRenamerNfoCleanup(boolean gameRenamerNfoCleanup) {
    this.gameRenamerNfoCleanup = gameRenamerNfoCleanup;
  }

  /**
   * Should we detect (and create) games from directories containing more than one game?
   * 
   * @return true/false
   */
  public boolean isDetectGameMultiDir() {
    return detectGameMultiDir;
  }

  /**
   * Should we detect (and create) games from directories containing more than one game?
   * 
   * @param newValue
   *          true/false
   */
  public void setDetectGameMultiDir(boolean newValue) {
    boolean oldValue = this.detectGameMultiDir;
    this.detectGameMultiDir = newValue;
    firePropertyChange(DETECT_GAME_MULTI_DIR, oldValue, newValue);
  }

  public boolean isBuildImageCacheOnImport() {
    return buildImageCacheOnImport;
  }

  public void setBuildImageCacheOnImport(boolean newValue) {
    boolean oldValue = this.buildImageCacheOnImport;
    this.buildImageCacheOnImport = newValue;
    firePropertyChange(BUILD_IMAGE_CACHE_ON_IMPORT, oldValue, newValue);
  }

  public boolean isGameRenamerCreateGamesetForSingleGame() {
    return gameRenamerCreateGamesetForSingleGame;
  }

  public void setGameRenamerCreateGamesetForSingleGame(boolean newValue) {
    boolean oldValue = this.gameRenamerCreateGamesetForSingleGame;
    this.gameRenamerCreateGamesetForSingleGame = newValue;
    firePropertyChange(GAME_RENAMER_GAMESET_SINGLE_GAME, oldValue, newValue);
  }

  public void addGameFileTypes(String type) {
    if (!gameFileTypes.contains(type)) {
      gameFileTypes.add(type);
      firePropertyChange(GAME_FILE_TYPES, null, gameFileTypes);
    }
  }

  public void removeGameFileType(String type) {
    gameFileTypes.remove(type);
    firePropertyChange(GAME_FILE_TYPES, null, gameFileTypes);
  }

  public List<String> getGameFileType() {
    return gameFileTypes;
  }

  public void addPlatformGame(String type) {
    if (!gamePlatforms.contains(type)) {
      gamePlatforms.add(type);
      Collections.sort(gamePlatforms);
      firePropertyChange(GAME_PLATFORMS, null, gamePlatforms);
    }
  }

  public void removePlatformGame(String type) {
    gamePlatforms.remove(type);
    firePropertyChange(GAME_PLATFORMS, null, gamePlatforms);
  }

  public List<String> getPlatformGame() {

    Collections.sort(gamePlatforms, String.CASE_INSENSITIVE_ORDER);
    return gamePlatforms;
  }

  /**
   * set rom Collection
   * 
   */

  public void removeallRomCollection() {
    romCollectionConfig.clear();
  }

  public void addRomCollectionConfig(RomCollectionConfig newCollection) {
    romCollectionConfig.add(newCollection);
    firePropertyChange(ROM_COLLECTION, null, romCollectionConfig.size());
  }

  public void removeRomCollection(RomCollectionConfig collection) {
    romCollectionConfig.remove(collection);
    firePropertyChange(ROM_COLLECTION, null, romCollectionConfig.size());
  }

  public List<RomCollectionConfig> getaddromCollection() {
    return romCollectionConfig;
  }

  public RomCollectionConfig getromCollectionbyShortName(String shortName) {
    for (RomCollectionConfig s : romCollectionConfig) {
      if (s.getShortName().equalsIgnoreCase(shortName)) {
        return s;
      }
    }
    return null;
  }

  public RomCollectionConfig getromCollectionbyLongName(String longName) {
    String n = Platforms.getInstance().getShortName(longName);

    if (n != null) {
      return getromCollectionbyShortName(n);
    }

    return null;
  }

  public void writeDefaultSettings() {
    // default game file types derived from http://www.file-extensions.org/filetype/extension/name/emulator-files
    addGameFileTypes(".$1"); // ZX Spectrum HOBETA format file
    addGameFileTypes(".000"); // Z80 Spectrum emulator file
    addGameFileTypes(".001"); // Snes9x slot 2 game saved file
    addGameFileTypes(".1"); // Super Nintendo split ROM image
    addGameFileTypes(".32x"); // Sega GENESIS ROM image file
    addGameFileTypes(".500"); // Nintendo Famicom Disk System file
    addGameFileTypes(".500"); // Pasofami file
    addGameFileTypes(".64b"); // Commodore C64 emulator file
    addGameFileTypes(".64c"); // Commodore C64 Emulator file
    addGameFileTypes(".64d"); // C64 PC64 Emulator file
    addGameFileTypes(".81"); // ZX Spectrum emulator file
    addGameFileTypes(".a26"); // Atari 2600 ROM image file
    addGameFileTypes(".a64"); // Nintendo 64 memory export file
    addGameFileTypes(".adf"); // Amiga disk file
    addGameFileTypes(".adx"); // Sega Dreamcast audio file
    addGameFileTypes(".adz"); // Amiga emulator compressed ADF file
    addGameFileTypes(".agn"); // Psion Series 3a Agenda file
    addGameFileTypes(".air"); // ZX Spectrum emulator input-recording format
    addGameFileTypes(".amf"); // Amiga metafile
    addGameFileTypes(".archimedes"); // Speccy ZX Spectrum emulator snapshot
    addGameFileTypes(".atr"); // Atari 8-bit disk image
    addGameFileTypes(".bin"); // Atari 2600 game ROM file
    addGameFileTypes(".bin"); // Nintendo DS binary file
    addGameFileTypes(".blk"); // ZX Spectrum emulator file
    addGameFileTypes(".boxer"); // Boxer for Mac game archive file
    addGameFileTypes(".brm"); // Kega Fusion backup RAM file
    addGameFileTypes(".c64"); // Commodore 64 ROM image
    addGameFileTypes(".cdmedia"); // Boxer for Mac cd-rom file
    addGameFileTypes(".cgb"); // Gameboy color file
    addGameFileTypes(".chd"); // MAME compressed hard disk file
    addGameFileTypes(".crg"); // Atari Jaguar Cinepak Chunky-format 16-bit RGB film file
    addGameFileTypes(".cxarchive"); // CrossOver bottle file
    addGameFileTypes(".d64"); // Commodore emulator file
    addGameFileTypes(".d71"); // Commodore emulator file
    addGameFileTypes(".d80"); // Commodore CBM-8050 diskette emulator image
    addGameFileTypes(".d81"); // Commodore VC-1581 diskette emulator image
    addGameFileTypes(".d82"); // Commodore emulator file
    addGameFileTypes(".d88"); // Toshiba Pasopia 7 disk file
    addGameFileTypes(".dat"); // Game data file
    addGameFileTypes(".dck"); // Warajevo ZX Spectrum emulation file
    addGameFileTypes(".dess"); // Microsoft Device Emulator saved state file
    addGameFileTypes(".dgn"); // Dragon 32 emulator file
    addGameFileTypes(".dhf"); // AMIGA emulator disk image ROM file
    addGameFileTypes(".dms"); // Amiga compressed archive
    addGameFileTypes(".dsk"); // ZX Spectrum emulator file
    addGameFileTypes(".dsk"); // SHARP MZ-series emulator file
    addGameFileTypes(".dsv"); // DeSmuME saved game file
    addGameFileTypes(".epr"); // Z88 Disk ROM Image files
    addGameFileTypes(".fam"); // Nintendo Entertainment System FamicomS emulator ROM image
    addGameFileTypes(".fdd"); // ZX Spectrum emulator file
    addGameFileTypes(".fdi"); // Floppy disk image
    addGameFileTypes(".fds"); // Nintendo Famicom (NES) disk system file
    addGameFileTypes(".fig"); // Super Nintendo game-console ROM image
    addGameFileTypes(".fmv"); // Famtasia movie capture
    addGameFileTypes(".frz"); // Snes9x saved state file
    addGameFileTypes(".fxm"); // Fuxoft AY music chip language file
    addGameFileTypes(".g64"); // C64 emulator disk image file
    addGameFileTypes(".gb"); // Nintendo Gameboy ROM image
    addGameFileTypes(".gba"); // Nintendo Game Boy Advance ROM image
    addGameFileTypes(".gbc"); // Nintendo GameBoy Colour emulator ROM image file
    addGameFileTypes(".gci"); // Nintendo GameCube saved game file
    addGameFileTypes(".gcz"); // Dolphin emulator archive
    addGameFileTypes(".gg"); // Sega GameGear game ROM image file
    addGameFileTypes(".ggs"); // Gameboy emulator file
    addGameFileTypes(".gme"); // Interact DexDrive Sony Playstation memory card save
    addGameFileTypes(".gmv"); // Gens movie capture
    addGameFileTypes(".gnm"); // GNM Output music file
    addGameFileTypes(".gs0"); // Sega Genesis (Megadrive) emulator quick slot 0 save file
    addGameFileTypes(".gs4"); // Genecyst saved state slot 4
    addGameFileTypes(".gs6"); // Genecyst save state 6 file
    addGameFileTypes(".gsx"); // Gens Sega emulator save state file
    addGameFileTypes(".harddisk"); // Boxer for Mac harddisk file
    addGameFileTypes(".hdf"); // Amiga hard disk image
    addGameFileTypes(".hdz"); // Amiga hard disk image file
    addGameFileTypes(".hpf"); // HP9100A program file
    addGameFileTypes(".hun"); // Personal Paint Hungarian language user interface file
    addGameFileTypes(".iie"); // SimIIe emulator file
    addGameFileTypes(".img"); // ZX Spectrum emulator disk image file
    addGameFileTypes(".ipf"); // Interchangeable preservation file
    addGameFileTypes(".iso"); // ePSXe Sony Playstation game image file
    addGameFileTypes(".itm"); // ZX Spectrum emulator tape file
    addGameFileTypes(".j64"); // Virtual Jaguar ROM file
    addGameFileTypes(".jma"); // NSRT Compressed Game file
    addGameFileTypes(".jmv"); // Jnes record file
    addGameFileTypes(".lha"); // LH ARC compressed archive
    addGameFileTypes(".lhz"); // LHA Compressed file archive
    addGameFileTypes(".lnx"); // Atari Lynx ROM image file
    addGameFileTypes(".ltp"); // ZX Spectrum emulator file
    addGameFileTypes(".lzx"); // Amiga LZX archive file
    addGameFileTypes(".m64"); // Mupen64 movie capture
    addGameFileTypes(".mbz"); // Pogoshell NES emulator plugin file
    addGameFileTypes(".mc"); // PlaySaver and PSEmuPro memory card save and single game save file
    addGameFileTypes(".mcc"); // Amiga MUI external class library
    addGameFileTypes(".mcd"); // Bleem! memory card save file
    addGameFileTypes(".mcr"); // ePSXe memory card file
    addGameFileTypes(".mdb"); // ZX Spectrum emulator MB-02 disk format
    addGameFileTypes(".mdr"); // ZX Spectrum microdrive emulator file
    addGameFileTypes(".mdv"); // QLAY file
    addGameFileTypes(".mgt"); // ZX Spectrum emulator disk image
    addGameFileTypes(".mix"); // Don Knuth's MMIX emulator file
    addGameFileTypes(".mpk"); // Project64 memory pack file
    addGameFileTypes(".mzt"); // SHARP MZ-series emulator file
    addGameFileTypes(".n64"); // Nintendo 64 Emulation ROM image file
    addGameFileTypes(".nd5"); // Nintendo DS game ROM file
    addGameFileTypes(".nds"); // Nintendo DS game ROM image file
    addGameFileTypes(".nes"); // Nintendo Entertainment System ROM image
    addGameFileTypes(".nez"); // NES ROM emulator image file
    addGameFileTypes(".ngp"); // Neo Geo Pocket ROM image file
    addGameFileTypes(".opd"); // ZX Spectrum emulator file
    addGameFileTypes(".out"); // ZX Spectrum emulator file
    addGameFileTypes(".p"); // ZX81 image file
    addGameFileTypes(".p00"); // C64 emulator file
    addGameFileTypes(".pal"); // ZX SPECTRUM emulator file
    addGameFileTypes(".pcsxstate"); // PCSX emulator saved game file
    addGameFileTypes(".pdb"); // C64 emulator file
    addGameFileTypes(".pie"); // Glove programmable input emulator file
    addGameFileTypes(".pj"); // Project64 game progress file
    addGameFileTypes(".pok"); // ZX Spectrum emulator poke file
    addGameFileTypes(".pol"); // Personal Paint polish language user interface file
    addGameFileTypes(".poo"); // Commodore C64 machine code file
    addGameFileTypes(".pp"); // Amiga compressed archive
    addGameFileTypes(".prg"); // SHARP MZ-series emulator file
    addGameFileTypes(".prg"); // ZX Spectrum SpecEm snapshot file
    addGameFileTypes(".pro"); // APE Atari disk image file
    addGameFileTypes(".ptp"); // Primo Computer Emulator tape file
    addGameFileTypes(".pvr"); // Sega Dreamcast VR texture file
    addGameFileTypes(".qcf"); // Q-emuLator file
    addGameFileTypes(".qd"); // SHARP MZ-series emulator file
    addGameFileTypes(".raw"); // ZX Spectrum memory dump file
    addGameFileTypes(".rdb"); // N64 Nintendo 64 ROM database file
    addGameFileTypes(".rom"); // Read Only Memory image (emulators/AONs)
    addGameFileTypes(".rp2"); // RetroPlatform Player (Amiga Forever) file
    addGameFileTypes(".rp9"); // RetroPlatform file
    addGameFileTypes(".rzx"); // ZX Spectrum emulator input-recording format
    addGameFileTypes(".s19"); // Motorola EEPROM programming file
    addGameFileTypes(".s28"); // Genesis or Sega32X ROM Image file
    addGameFileTypes(".sa1"); // Nintendo Game Boy emulator save file
    addGameFileTypes(".san"); // ZX Spectrum emulator file
    addGameFileTypes(".sc"); // Sega SC-3000 image file
    addGameFileTypes(".scl"); // ZX Spectrum emulator disk image
    addGameFileTypes(".scr"); // ZX Spectrum standard screen
    addGameFileTypes(".sem"); // ZX Spectrum-Emulator snapshot file
    addGameFileTypes(".sf7"); // Sega SF-7000 ROM file
    addGameFileTypes(".sfc"); // Nintendo SNES9x ROM file
    addGameFileTypes(".sgb"); // Super Gameboy image file
    addGameFileTypes(".sit"); // Sinclair ZX Spectrum emulator snapshot file
    addGameFileTypes(".slt"); // ZX Spectrum emulator Super Level Loader snapshot file
    addGameFileTypes(".smc"); // Super Nintendo game-console ROM image
    addGameFileTypes(".smd"); // Sega Genesis ROM emulator file
    addGameFileTypes(".smv"); // Snes9x movie capture ile
    addGameFileTypes(".sna"); // ZX80 Spectrum Mirage emulator snapshot file
    addGameFileTypes(".snap"); // ZX Spectrum emulator Mirage Microdrive snapshot file
    addGameFileTypes(".snapshot"); // ZX Spectrum emulator Mirage Microdrive snapshot file
    addGameFileTypes(".sp"); // ZX Spectrum emulator source profile file
    addGameFileTypes(".spc"); // J. Swiatek SP ZX Spectrum emulator tape file
    addGameFileTypes(".srg"); // Atari Jaguar Cinepak Smooth-format 16-bit RGB film file
    addGameFileTypes(".srm"); // Super Nintendo ROM saved game emulator file
    addGameFileTypes(".srm"); // Kega Fusion game RAM file
    addGameFileTypes(".ss0"); // GameBoid first saving slot file
    addGameFileTypes(".st"); // Atari disk image file
    addGameFileTypes(".svs"); // gpSP Game Boy Advance emulator saved state file
    addGameFileTypes(".szx"); // ZX Spectrum emulator ZX-state snapshot file
    addGameFileTypes(".t64"); // Commodore 64 emulator tape file
    addGameFileTypes(".tap"); // ZX Spectrum tape file
    addGameFileTypes(".trd"); // ZX Spectrum floppy disk image file
    addGameFileTypes(".ttp"); // Atari Falcon application
    addGameFileTypes(".tzx"); // ZX Spectrum tape image poke file
    addGameFileTypes(".u00"); // Commodore C64 universal file
    addGameFileTypes(".uss"); // WinUAE saved state file
    addGameFileTypes(".v64"); // Nintento 64 emulation ROM image file
    addGameFileTypes(".vb"); // Virtual Boy image file
    addGameFileTypes(".vbm"); // Visual Boy Advance movie capture format
    addGameFileTypes(".vgs"); // Virtual Game Station memory card save
    addGameFileTypes(".vms"); // Dreamcast VMU save file
    addGameFileTypes(".vmv"); // VirtualNES or VisualBoyAdvance file
    addGameFileTypes(".wdf"); // Wiimm Nintendo Wii disc file
    addGameFileTypes(".wdr"); // ZX Spectrum emulator file
    addGameFileTypes(".whd"); // WinUAEX Amiga game ROM file
    addGameFileTypes(".x64"); // Commodore 64 emulator disk image
    addGameFileTypes(".xadml"); // Amiga Forever XADML file
    addGameFileTypes(".xdf"); // X68000 image file
    addGameFileTypes(".z80"); // ZX Spectrum Emulator memory snapshot file
    addGameFileTypes(".zm1"); // ZSNES movie #1 file
    addGameFileTypes(".zm2"); // ZSNES movie #2 file
    addGameFileTypes(".zm3"); // ZSNES Movie #3 file
    addGameFileTypes(".zm4"); // ZSNES Movie #4 file
    addGameFileTypes(".zm6"); // ZSNES Movie #6 file
    addGameFileTypes(".zm7"); // ZSNES Movie #7 file
    addGameFileTypes(".zm8"); // ZSNES Movie #8 file
    addGameFileTypes(".zm9"); // ZSNES Movie #9 file
    addGameFileTypes(".zm?"); // ZSNES movie file
    addGameFileTypes(".zs0"); // ZSNES slot 0 saved state file
    addGameFileTypes(".zs1"); // ZSNES slot 1 saved state file
    addGameFileTypes(".zs2"); // ZSNES slot 2 saved state file
    addGameFileTypes(".zs3"); // ZSNES slot 3 saved state file
    addGameFileTypes(".zs4"); // ZSNES slot 4 save state file
    addGameFileTypes(".zs5"); // ZSNES slot 5 saved state file
    addGameFileTypes(".zs6"); // ZSNES slot 6 saved state file
    addGameFileTypes(".zs7"); // ZSNES slot 7 saved state file
    addGameFileTypes(".zs8"); // ZSNES slot 8 saved state file
    addGameFileTypes(".zs9"); // ZSNES slot 9 saved state file
    addGameFileTypes(".zsg"); // GENS zipped Sega Genesis file
    addGameFileTypes(".zx"); // KGB ZX Spectrum emulator snapshot file
    addGameFileTypes(".zx82"); // ZX Spectrum emulator Speculator file
    addGameFileTypes(".zxs"); // ZX Spectrum emulator ZX32 snapshot file
    addGameFileTypes(".zxt"); // ZX Spectrum emulator file
    Collections.sort(gameFileTypes, String.CASE_INSENSITIVE_ORDER);

    // most common used rom
    // http://www.rom-world.com/toproms.php
    addPlatformGame("Super Nintendo"); //
    addPlatformGame("Neo Geo"); //
    addPlatformGame("Nintendo 64"); //
    addPlatformGame("Game Boy Advance"); //
    addPlatformGame("Playstation 1"); // //
    addPlatformGame("Super Nintendo"); //
    addPlatformGame("Nintendo DS");
    addPlatformGame("Nintendo 3DS");
    addPlatformGame("PlayStation Portable");
    addPlatformGame("PlayStation 2");
    addPlatformGame("PlayStation 3"); //
    addPlatformGame("PlayStation 4");
    addPlatformGame("PC");
    addPlatformGame("Wii");
    addPlatformGame("Wii U");
    addPlatformGame("Xbox 360");
    addPlatformGame("Xbox One");
    addPlatformGame("iPhone");
    Collections.sort(gamePlatforms, String.CASE_INSENSITIVE_ORDER);

    addGameNfoFilename(GameNfoNaming.GAME_NFO);
    addGamePosterFilename(GamePosterNaming.POSTER_JPG);
    addGamePosterFilename(GamePosterNaming.POSTER_PNG);
    addGameFanartFilename(GameFanartNaming.FANART_JPG);
    addGameFanartFilename(GameFanartNaming.FANART_PNG);
  }
}

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

import static org.tinymediamanager.core.Constants.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.observablecollections.ObservableCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.AbstractModelObject;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileAudioStream;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.Message.MessageLevel;
import org.tinymediamanager.core.MessageManager;
import org.tinymediamanager.core.platform.Platforms;
import org.tinymediamanager.scraper.IMediaArtworkProvider;
import org.tinymediamanager.scraper.IMediaMetadataProvider;
import org.tinymediamanager.scraper.IMediaTrailerProvider;
import org.tinymediamanager.scraper.MediaSearchOptions;
import org.tinymediamanager.scraper.MediaSearchOptions.SearchParam;
import org.tinymediamanager.scraper.MediaSearchResult;
import org.tinymediamanager.scraper.MediaType;
import org.tinymediamanager.scraper.giantbomb.giantbombMetadataProvider;
import org.tinymediamanager.scraper.thegamesdb.TheGamesDbMetadataProvider;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;

/**
 * The Class GameList.
 * 
 * @author Manuel Laggner
 */
public class GameList extends AbstractModelObject {
  private static final Logger         LOGGER                = LoggerFactory.getLogger(GameList.class);
  private static GameList             instance;

  private ObservableElementList<Game> gameList;
  private List<GameSet>               gameSetList;
  private PropertyChangeListener      tagListener;
  private List<String>                tagsObservable        = ObservableCollections.observableList(Collections
                                                                .synchronizedList(new ArrayList<String>()));
  private List<String>                platformObservable    = ObservableCollections.observableList(Collections
                                                                .synchronizedList(new ArrayList<String>()));

  private List<String>                videoCodecsObservable = ObservableCollections.observableList(Collections
                                                                .synchronizedList(new ArrayList<String>()));

  private List<String>                audioCodecsObservable = ObservableCollections.observableList(Collections
                                                                .synchronizedList(new ArrayList<String>()));

  /**
   * Instantiates a new game list.
   */
  private GameList() {
    // the tag listener: its used to always have a full list of all tags used in tmm
    tagListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        // listen to changes of tags
        if ("tag".equals(evt.getPropertyName())) {
          Game game = (Game) evt.getSource();
          updateTags(game);
        }
        if (MEDIA_FILES.equals(evt.getPropertyName()) || MEDIA_INFORMATION.equals(evt.getPropertyName())) {
          Game game = (Game) evt.getSource();
          updateMediaInformationLists(game);
        }
      }
    };
  }

  /**
   * Gets the single instance of GameList.
   * 
   * @return single instance of GameList
   */
  public static GameList getInstance() {
    if (GameList.instance == null) {
      GameList.instance = new GameList();
    }
    return GameList.instance;
  }

  /**
   * Adds the game.
   * 
   * @param game
   *          the game
   */
  public void addGame(Game game) {
    if (!gameList.contains(game)) {
      int oldValue = gameList.size();
      gameList.add(game);

      updateTags(game);
      game.addPropertyChangeListener(tagListener);
      firePropertyChange("games", null, gameList);
      firePropertyChange("gameCount", oldValue, gameList.size());
    }
  }

  /**
   * Removes the datasource.
   * 
   * @param path
   *          the path
   */
  public void removeDatasource(String path) {
    if (StringUtils.isEmpty(path)) {
      return;
    }

    for (int i = gameList.size() - 1; i >= 0; i--) {
      Game game = gameList.get(i);
      if (new File(path).equals(new File(game.getDataSource()))) {
        removeGame(game);
      }
    }
  }

  /**
   * Gets the unscraped games.
   * 
   * @return the unscraped games
   */
  public List<Game> getUnscrapedGames() {
    List<Game> unscrapedGames = new ArrayList<Game>();
    for (Game game : gameList) {
      if (!game.isScraped()) {
        unscrapedGames.add(game);
      }
    }
    return unscrapedGames;
  }

  /**
   * Gets the new games or games with new files
   * 
   * @return the new games
   */
  public List<Game> getNewGames() {
    List<Game> newGames = new ArrayList<Game>();
    for (Game game : gameList) {
      if (game.isNewlyAdded()) {
        newGames.add(game);
      }
    }
    return newGames;
  }

  /**
   * Removes the game.
   * 
   * @param game
   *          the game
   */
  public void removeGame(Game game) {
    int oldValue = gameList.size();
    gameList.remove(game);

    // remove game also from gamesets
    if (game.getGameSet() != null) {
      game.getGameSet().removeGame(game);
      game.setGameSet(null);
    }

    Globals.entityManager.getTransaction().begin();
    Globals.entityManager.remove(game);
    Globals.entityManager.getTransaction().commit();
    firePropertyChange("games", null, gameList);
    firePropertyChange("gameCount", oldValue, gameList.size());
  }

  /**
   * Remove all games.
   * 
   */
  public void removeGames() {
    for (int i = gameList.size() - 1; i >= 0; i--) {
      Game game = gameList.get(i);
      removeGame(game);
    }
  }

  /**
   * Gets the games.
   * 
   * @return the games
   */
  public ObservableElementList<Game> getGames() {
    if (gameList == null) {
      gameList = new ObservableElementList<Game>(GlazedLists.threadSafeList(new BasicEventList<Game>()), GlazedLists.beanConnector(Game.class));
    }
    return gameList;
  }

  /**
   * Load games from database.
   */
  public void loadGamesFromDatabase() {
    List<Game> games = null;
    List<GameSet> gameSets = null;
    try {
      // load games
      TypedQuery<Game> query = Globals.entityManager.createQuery("SELECT game FROM Game game", Game.class);
      games = query.getResultList();
      if (games != null) {
        LOGGER.info("found " + games.size() + " games in database");
        gameList = new ObservableElementList<Game>(GlazedLists.threadSafeList(new BasicEventList<Game>(games.size())),
            GlazedLists.beanConnector(Game.class));

        for (Object obj : games) {
          if (obj instanceof Game) {
            Game game = (Game) obj;
            try {
              // game.setObservables();
              game.initializeAfterLoading();

              // for performance reasons we add games directly
              // addGame(game);
              gameList.add(game);
              updateTags(game);
              updateMediaInformationLists(game);
              game.addPropertyChangeListener(tagListener);
            }
            catch (Exception e) {
              LOGGER.error("error loading game/dropping it: " + e.getMessage());
              try {
                removeGame(game);
              }
              catch (Exception e1) {
              }
            }
          }
          else {
            LOGGER.error("retrieved no game: " + obj);
          }
        }

      }
      else {
        LOGGER.debug("found no games in database");
      }

      // load game sets
      TypedQuery<GameSet> querySets = Globals.entityManager.createQuery("SELECT gameSet FROM GameSet gameSet", GameSet.class);
      gameSets = querySets.getResultList();
      if (gameSets != null) {
        LOGGER.info("found " + gameSets.size() + " gameSets in database");
        gameSetList = ObservableCollections.observableList(Collections.synchronizedList(new ArrayList<GameSet>(gameSets.size())));

        // load game sets
        for (Object obj : gameSets) {
          if (obj instanceof GameSet) {
            GameSet gameSet = (GameSet) obj;
            gameSet.setObservables();

            // for performance reasons we add gamesets directly
            // addGameSet(gameSet);
            this.gameSetList.add(gameSet);
          }
        }
      }
      else {
        LOGGER.debug("found no gameSets in database");
      }

      // cross check games and gamesets if linking is "stable"
      checkAndCleanupGameSets();
    }
    catch (Exception e) {
      LOGGER.error("loadGamesFromDatabase", e);
      MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, "", "message.database.loadgames"));
    }
  }

  /**
   * Gets the game by path.
   * 
   * @param path
   *          the path
   * @return the game by path
   */
  public synchronized Game getGameByPath(File path) {

    for (Game game : gameList) {
      if (new File(game.getPath()).compareTo(path) == 0) {
        return game;
      }
    }

    return null;
  }

  /**
   * Gets a list of games by same path.
   * 
   * @param path
   *          the path
   * @return the game list
   */
  public synchronized List<Game> getGamesByPath(File path) {
    ArrayList<Game> games = new ArrayList<Game>();
    for (Game game : gameList) {
      if (new File(game.getPath()).compareTo(path) == 0) {
        games.add(game);
      }
    }
    return games;
  }

  /**
   * Search game.
   * 
   * @param searchTerm
   *          the search term
   * @param game
   *          the game
   * @param metadataProvider
   *          the metadata provider
   * @return the list
   */
  public List<MediaSearchResult> searchGame(boolean forceSearchTerm, String searchTerm, Game game, String machine,
      IMediaMetadataProvider metadataProvider) {
    List<MediaSearchResult> sr = null;
    String IdProvider = null;

    try {
      IMediaMetadataProvider provider = metadataProvider;
      // get a new metadataprovider if nothing is set
      if (provider == null) {
        provider = getMetadataProvider();
      }

      IdProvider = provider.getProviderInfo().getId();

      boolean idFound = false;
      // set what we have, so the provider could chose from all :)
      MediaSearchOptions options = new MediaSearchOptions(MediaType.GAME);
      options.set(SearchParam.PLATFORM, machine);
      options.set(SearchParam.LANGUAGE, Globals.settings.getGameSettings().getScraperLanguage().name());

      if (game != null && Platforms.getInstance().isPlatformName(game.getPlatform()) && !forceSearchTerm) {
        if (game.getId(IdProvider) != null && game.getPlatform().equalsIgnoreCase(machine)) {
          options.set(SearchParam.GAMEID, game.getId(IdProvider).toString());
          options.set(SearchParam.PLATFORM, machine);
          idFound = true;
        }
        else {
          options.set(SearchParam.PLATFORM, machine);
        }
        options.set(SearchParam.TITLE, game.getTitle());
        if (!game.getYear().isEmpty()) {
          options.set(SearchParam.YEAR, game.getYear());
        }
      }

      if (!searchTerm.isEmpty()) {
        if (idFound) {
          // id found, so search for it
          // except when searchTerm differs from game title (we entered something to search for)
          if (!searchTerm.equals(game.getTitle())) {
            options.set(SearchParam.QUERY, searchTerm);
          }
        }
        else {
          options.set(SearchParam.QUERY, searchTerm);
        }
      }

      sr = provider.search(options);
    }
    catch (Exception e) {
      LOGGER.error("searchGame", e);
      MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, game, "message.game.searcherror", new String[] { ":",
          e.getLocalizedMessage() }));
    }

    return sr;
  }

  // /**
  // * Search game.
  // *
  // * @param searchTerm
  // * the search term
  // * @param ImdbId
  // * the imdb id
  // * @param metadataProvider
  // * the metadata provider
  // * @return the list
  // */
  // @Deprecated
  // public List<MediaSearchResult> searchGame(String searchTerm, String year, String ImdbId, IMediaMetadataProvider metadataProvider) {
  // List<MediaSearchResult> sr = null;
  // if (ImdbId != null && !ImdbId.isEmpty()) {
  // sr = searchGameByImdbId(ImdbId, metadataProvider);
  // }
  // if (sr == null || sr.size() == 0) {
  // sr = searchGame(searchTerm, year, metadataProvider);
  // }
  //
  // return sr;
  // }

  // /**
  // * Search game.
  // *
  // * @param searchTerm
  // * the search term
  // * @param metadataProvider
  // * the metadata provider
  // * @return the list
  // */
  // @Deprecated
  // private List<MediaSearchResult> searchGame(String searchTerm, String year, IMediaMetadataProvider metadataProvider) {
  // // format searchstring
  // // searchTerm = MetadataUtil.removeNonSearchCharacters(searchTerm);
  //
  // List<MediaSearchResult> searchResult = null;
  // try {
  // IMediaMetadataProvider provider = metadataProvider;
  // // get a new metadataprovider if nothing is set
  // if (provider == null) {
  // provider = getMetadataProvider();
  // }
  // MediaSearchOptions options = new MediaSearchOptions(MediaType.MOVIE, MediaSearchOptions.SearchParam.QUERY, searchTerm);
  // options.set(MediaSearchOptions.SearchParam.YEAR, year);
  // searchResult = provider.search(options);
  // }
  // catch (Exception e) {
  // LOGGER.error("searchGame", e);
  // MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, "", "message.game.searcherror", new String[] { ":",
  // e.getLocalizedMessage() }));
  // }
  //
  // return searchResult;
  // }

  // /**
  // * Search game.
  // *
  // * @param imdbId
  // * the imdb id
  // * @param metadataProvider
  // * the metadata provider
  // * @return the list
  // */
  // @Deprecated
  // private List<MediaSearchResult> searchGameByImdbId(String imdbId, IMediaMetadataProvider metadataProvider) {
  //
  // List<MediaSearchResult> searchResult = null;
  // MediaSearchOptions options = new MediaSearchOptions(MediaType.MOVIE);
  // options.setMediaType(MediaType.MOVIE);
  // options.set(SearchParam.IMDBID, imdbId);
  //
  // try {
  // IMediaMetadataProvider provider = metadataProvider;
  // // get a new metadataProvider if no one is set
  // if (provider == null) {
  // provider = getMetadataProvider();
  // }
  // searchResult = provider.search(options);
  // }
  // catch (Exception e) {
  // LOGGER.warn("failed to search game with imdbid", e);
  // searchResult = new ArrayList<MediaSearchResult>();
  // }
  //
  // return searchResult;
  // }

  /**
   * Gets the metadata provider.
   * 
   * @return the metadata provider
   */
  public IMediaMetadataProvider getMetadataProvider() {
    GameScrapers scraper = Globals.settings.getGameSettings().getGameScraper();
    return getMetadataProvider(scraper);
  }

  /**
   * Gets the metadata provider.
   * 
   * @param scraper
   *          the scraper
   * @return the metadata provider
   */
  public IMediaMetadataProvider getMetadataProvider(GameScrapers scraper) {
    IMediaMetadataProvider metadataProvider = null;
    switch (scraper) {

    // case JEUXVIDEO:
    // LOGGER.debug("get instance of jeuxvideoMetadataProvider");
    // metadataProvider = new jeuxvideoMetadataProvider();
    // break;

      case GAMEDB:
        LOGGER.debug("get instance of gameDBMetadataProvider");
        metadataProvider = new TheGamesDbMetadataProvider();
        break;

      case GIANTBOMB:
      default:
        LOGGER.debug("get instance of giantbombMetadataProvider");
        try {
          metadataProvider = new giantbombMetadataProvider();
        }
        catch (Exception e) {
          LOGGER.warn("failed to get instance of giantbombMetadataProvider", e);
        }
    }

    //
    // try {
    // metadataProvider = new XbmcMetadataProvider(new
    // XbmcScraperParser().parseScraper(new
    // File("xbmc_scraper/metadata.imdb.com/imdb.xml")));
    // metadataProvider = new XbmcMetadataProvider(new
    // XbmcScraperParser().parseScraper(new
    // File("xbmc_scraper/metadata.imdb.de/imdb_de.xml")));
    // } catch (Exception e) {
    // LOGGER.error("tried to get xmbc scraper", e);
    // }

    // }

    return metadataProvider;
  }

  /**
   * Gets the artwork provider.
   * 
   * @return the artwork provider
   */
  public List<IMediaArtworkProvider> getArtworkProviders() {
    List<GameArtworkScrapers> scrapers = new ArrayList<GameArtworkScrapers>();
    if (Globals.settings.getGameSettings().isImageScraperGiantBomb()) {
      scrapers.add(GameArtworkScrapers.GIANTBOMB);
    }

    if (Globals.settings.getGameSettings().isImageScraperGameDB()) {
      scrapers.add(GameArtworkScrapers.GAMEDB);
    }

    if (Globals.settings.getGameSettings().isImageScraperJeuxVideo()) {
      scrapers.add(GameArtworkScrapers.JEUXVIDEO);
    }

    return getArtworkProviders(scrapers);
  }

  /**
   * Gets the artwork providers.
   * 
   * @param scrapers
   *          the scrapers
   * @return the artwork providers
   */
  public List<IMediaArtworkProvider> getArtworkProviders(List<GameArtworkScrapers> scrapers) {
    List<IMediaArtworkProvider> artworkProviders = new ArrayList<IMediaArtworkProvider>();

    IMediaArtworkProvider artworkProvider = null;

    // giantbomb
    if (scrapers.contains(GameArtworkScrapers.GIANTBOMB)) {
      try {
        if (Globals.settings.getGameSettings().isImageScraperGiantBomb()) {
          LOGGER.debug("get instance of giantbombMetadataProvider");
          artworkProvider = new giantbombMetadataProvider();
          artworkProviders.add(artworkProvider);
        }
      }
      catch (Exception e) {
        LOGGER.warn("failed to get instance of giantbombMetadataProvider", e);
      }
    }

    // gamedb
    if (scrapers.contains(GameArtworkScrapers.GAMEDB)) {
      try {
        if (Globals.settings.getGameSettings().isImageScraperGameDB()) {
          LOGGER.debug("get instance of gameDBMetadataProvider");
          artworkProvider = new TheGamesDbMetadataProvider();
          artworkProviders.add(artworkProvider);
        }
      }
      catch (Exception e) {
        LOGGER.warn("failed to get instance of gameDBMetadataProvider", e);
      }
    }

    // // gamedb
    // if (scrapers.contains(GameArtworkScrapers.JEUXVIDEO)) {
    // try {
    // if (Globals.settings.getGameSettings().isImageScraperJeuxVideo()) {
    // LOGGER.debug("get instance of jeuxvideoMetadataProvider");
    // artworkProvider = new jeuxvideoMetadataProvider();
    // artworkProviders.add(artworkProvider);
    // }
    // }
    // catch (Exception e) {
    // LOGGER.warn("failed to get instance of jeuxvideoMetadataProvider", e);
    // }
    // }

    return artworkProviders;
  }

  /**
   * Gets the trailer providers.
   * 
   * @return the trailer providers
   */
  public List<IMediaTrailerProvider> getTrailerProviders() {
    List<GameTrailerScrapers> scrapers = new ArrayList<GameTrailerScrapers>();

    if (Globals.settings.getGameSettings().isTrailerScraperGiantBomb()) {
      scrapers.add(GameTrailerScrapers.GIANTBOMB);
    }

    if (Globals.settings.getGameSettings().isTrailerScraperGameDB()) {
      scrapers.add(GameTrailerScrapers.GAMEDB);
    }

    if (Globals.settings.getGameSettings().isTrailerScraperJeuxVideo()) {
      scrapers.add(GameTrailerScrapers.JEUXVIDEO);
    }

    return getTrailerProviders(scrapers);
  }

  /**
   * Gets the trailer providers.
   * 
   * @param scrapers
   *          the scrapers
   * @return the trailer providers
   */
  public List<IMediaTrailerProvider> getTrailerProviders(List<GameTrailerScrapers> scrapers) {
    List<IMediaTrailerProvider> trailerProviders = new ArrayList<IMediaTrailerProvider>();

    // tmdb
    if (scrapers.contains(GameTrailerScrapers.GIANTBOMB)) {
      try {
        IMediaTrailerProvider trailerProvider = new giantbombMetadataProvider();
        trailerProviders.add(trailerProvider);
      }
      catch (Exception e) {
        LOGGER.warn("failed to get instance of giantbombMetadataProvider", e);
      }
    }

    // hd-trailer.net
    if (scrapers.contains(GameTrailerScrapers.GAMEDB)) {
      IMediaTrailerProvider trailerProvider = new TheGamesDbMetadataProvider();
      trailerProviders.add(trailerProvider);
    }

    // // ofdb.de
    // if (scrapers.contains(GameTrailerScrapers.JEUXVIDEO)) {
    // IMediaTrailerProvider trailerProvider = new jeuxvideoMetadataProvider();
    // trailerProviders.add(trailerProvider);
    // }

    return trailerProviders;
  }

  /**
   * Gets the game count.
   * 
   * @return the game count
   */
  public int getGameCount() {
    int size = gameList.size();
    return size;
  }

  /**
   * Gets the game set count.
   * 
   * @return the game set count
   */
  public int getGameSetCount() {
    int size = gameSetList.size();
    return size;
  }

  /**
   * Gets the tags in games.
   * 
   * @return the tags in games
   */
  public List<String> getTagsInGames() {
    return tagsObservable;
  }

  /**
   * Gets the platform in games.
   * 
   * @return the platforms in games
   */
  public List<String> getPlatformInGames() {
    return platformObservable;
  }

  /**
   * Update tags used in games.
   * 
   * @param game
   *          the game
   */
  private void updateTags(Game game) {
    for (String tagInGame : game.getTags()) {
      boolean tagFound = false;
      for (String tag : tagsObservable) {
        if (tagInGame.equals(tag)) {
          tagFound = true;
          break;
        }
      }
      if (!tagFound) {
        addTag(tagInGame);
      }
    }
  }

  /**
   * Update media information used in games.
   * 
   * @param game
   *          the game
   */
  private void updateMediaInformationLists(Game game) {
    // video codec
    for (MediaFile mf : game.getMediaFiles(MediaFileType.GAME)) {
      String codec = mf.getVideoCodec();
      boolean codecFound = false;

      for (String mfCodec : videoCodecsObservable) {
        if (mfCodec.equals(codec)) {
          codecFound = true;
          break;
        }
      }

      if (!codecFound) {
        addVideoCodec(codec);
      }
    }

    // audio codec
    for (MediaFile mf : game.getMediaFiles(MediaFileType.GAME)) {
      for (MediaFileAudioStream audio : mf.getAudioStreams()) {
        String codec = audio.getCodec();
        boolean codecFound = false;
        for (String mfCodec : audioCodecsObservable) {
          if (mfCodec.equals(codec)) {
            codecFound = true;
            break;
          }
        }

        if (!codecFound) {
          addAudioCodec(codec);
        }
      }
    }

  }

  public List<String> getVideoCodecsInGames() {
    return videoCodecsObservable;
  }

  public List<String> getAudioCodecsInGames() {
    return audioCodecsObservable;
  }

  /**
   * Adds the tag.
   * 
   * @param newTag
   *          the new tag
   */
  private void addTag(String newTag) {
    if (StringUtils.isBlank(newTag)) {
      return;
    }

    for (String tag : tagsObservable) {
      if (tag.equals(newTag)) {
        return;
      }
    }

    tagsObservable.add(newTag);
    firePropertyChange("tag", null, tagsObservable);
  }

  private void addVideoCodec(String newCodec) {
    if (StringUtils.isBlank(newCodec)) {
      return;
    }

    for (String codec : videoCodecsObservable) {
      if (codec.equals(newCodec)) {
        return;
      }
    }

    videoCodecsObservable.add(newCodec);
    firePropertyChange("videoCodec", null, videoCodecsObservable);
  }

  private void addAudioCodec(String newCodec) {
    if (StringUtils.isBlank(newCodec)) {
      return;
    }

    for (String codec : audioCodecsObservable) {
      if (codec.equals(newCodec)) {
        return;
      }
    }

    audioCodecsObservable.add(newCodec);
    firePropertyChange("audioCodec", null, audioCodecsObservable);
  }

  /**
   * Search duplicates.
   */
  public void searchDuplicates() {
    Map<String, Game> imdbDuplicates = new HashMap<String, Game>();

    for (Game game : gameList) {
      game.clearDuplicate();

      // imdb duplicate search only works with given imdbid
      if (StringUtils.isNotEmpty(game.getId("default").toString())) {
        // is there a game with this imdbid sotred?
        if (imdbDuplicates.containsKey(game.getId("default"))) {
          // yes - set duplicate flag on both games
          game.setDuplicate();
          Game game2 = imdbDuplicates.get(game.getId("default"));
          game2.setDuplicate();
        }
        else {
          // no, store game
          imdbDuplicates.put(game.getId("default").toString(), game);
        }
      }
    }
  }

  /**
   * Gets the game set list.
   * 
   * @return the gameSetList
   */
  public List<GameSet> getGameSetList() {
    if (gameSetList == null) {
      gameSetList = ObservableCollections.observableList(Collections.synchronizedList(new ArrayList<GameSet>()));
    }
    return gameSetList;
  }

  /**
   * Sets the game set list.
   * 
   * @param gameSetList
   *          the gameSetList to set
   */
  public void setGameSetList(ObservableElementList<GameSet> gameSetList) {
    this.gameSetList = gameSetList;
  }

  /**
   * Adds the game set.
   * 
   * @param gameSet
   *          the game set
   */
  public void addGameSet(GameSet gameSet) {
    int oldValue = gameSetList.size();
    this.gameSetList.add(gameSet);
    firePropertyChange("addedGameSet", null, gameSet);
    firePropertyChange("gameSetCount", oldValue, gameSetList.size());
  }

  /**
   * Removes the game set.
   * 
   * @param gameSet
   *          the game set
   */
  public void removeGameSet(GameSet gameSet) {
    int oldValue = gameSetList.size();
    gameSet.removeAllGames();

    gameSetList.remove(gameSet);
    Globals.entityManager.getTransaction().begin();
    Globals.entityManager.remove(gameSet);
    Globals.entityManager.getTransaction().commit();
    firePropertyChange("removedGameSet", null, gameSet);
    firePropertyChange("gameSetCount", oldValue, gameSetList.size());
  }

  private GameSet findGameSet(String title, int tmdbId) {
    // first search by tmdbId
    if (tmdbId > 0) {
      for (GameSet gameSet : gameSetList) {
        if (gameSet.getTmdbId() == tmdbId) {
          return gameSet;
        }
      }
    }

    // search for the gameset by name
    for (GameSet gameSet : gameSetList) {
      if (gameSet.getTitle().equals(title)) {
        return gameSet;
      }
    }

    return null;
  }

  public synchronized GameSet getGameSet(String title, int tmdbId) {
    GameSet gameSet = findGameSet(title, tmdbId);

    if (gameSet == null) {
      gameSet = new GameSet(title);
      gameSet.saveToDb();
      addGameSet(gameSet);
    }

    return gameSet;
  }

  /**
   * Sort games in game set.
   * 
   * @param gameSet
   *          the game set
   */
  public void sortGamesInGameSet(GameSet gameSet) {
    if (gameSet.getGames().size() > 1) {
      gameSet.sortGames();
    }
    firePropertyChange("sortedGameSets", null, gameSetList);
  }

  /**
   * invalidate the title sortable upon changes to the sortable prefixes
   */
  public void invalidateTitleSortable() {
    for (Game game : new ArrayList<Game>(gameList)) {
      game.clearTitleSortable();
    }
  }

  /**
   * cross check the linking between games and gamesets and clean it
   */
  private void checkAndCleanupGameSets() {
    for (Game game : gameList) {
      // first check if this game is in the given gameset
      if (game.getGameSet() != null && !game.getGameSet().getGames().contains(game)) {
        // add it
        game.getGameSet().addGame(game);
        game.getGameSet().saveToDb();
      }
      // and check if this game is in other gamesets
      for (GameSet gameSet : gameSetList) {
        if (gameSet != game.getGameSet() && gameSet.getGames().contains(game)) {
          gameSet.removeGame(game);
          gameSet.saveToDb();
        }
      }
    }
  }
}

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.observablecollections.ObservableCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.ImageCache;
import org.tinymediamanager.core.MediaEntity;
import org.tinymediamanager.core.Utils;
import org.tinymediamanager.scraper.MediaArtwork.MediaArtworkType;
import org.tinymediamanager.scraper.util.Url;

/**
 * The Class GameSet.
 * 
 * @author Manuel Laggner
 */
@Entity
public class GameSet extends MediaEntity {

  private static final Logger           LOGGER               = LoggerFactory.getLogger(GameSet.class);
  private static final Comparator<Game> MOVIE_SET_COMPARATOR = new GameInGameSetComparator();

  private List<Game>                    games                = new ArrayList<Game>();
  @Transient
  private List<Game>                    gamesObservable      = ObservableCollections.observableList(games);
  @Transient
  private String                        titleSortable        = "";

  static {
    mediaFileComparator = new GameMediaFileComparator();
  }

  /**
   * Instantiates a new gameset. To initialize the propertychangesupport after loading
   */
  public GameSet() {
  }

  @Override
  public void setTitle(String newValue) {
    super.setTitle(newValue);

    for (Game game : gamesObservable) {
      game.gameSetTitleChanged();
    }
  }

  /**
   * Returns the sortable variant of title<br>
   * eg "The Terminator Collection" -> "Terminator Collection, The".
   * 
   * @return the title in its sortable format
   */
  public String getTitleSortable() {
    if (StringUtils.isEmpty(titleSortable)) {
      titleSortable = Utils.getSortableName(this.getTitle());
    }
    return titleSortable;
  }

  /**
   * Instantiates a new game set.
   * 
   * @param title
   *          the title
   */
  public GameSet(String title) {
    setTitle(title);
  }

  /**
   * Sets the observable cast list.
   */
  public void setObservables() {
    gamesObservable = ObservableCollections.observableList(games);
  }

  /**
   * Gets the tmdb id.
   * 
   * @return the tmdb id
   */
  public int getTmdbId() {
    int id = 0;
    try {
      id = (Integer) ids.get("tmdbId");
    }
    catch (Exception e) {
      return 0;
    }
    return id;
  }

  /**
   * Sets the tmdb id.
   * 
   * @param newValue
   *          the new tmdb id
   */
  public void setTmdbId(int newValue) {
    int oldValue = getTmdbId();
    ids.put("tmdbId", newValue);
    firePropertyChange(TMDBID, oldValue, newValue);
  }

  /**
   * Sets the poster url.
   * 
   * @param newValue
   *          the new poster url
   */
  @Override
  public void setPosterUrl(String newValue) {
    super.setPosterUrl(newValue);
    boolean written = false;
    String posterFilename = "gameset-poster.jpg";

    // write new poster
    writeImageToGameFolder(gamesObservable, posterFilename, posterUrl);
    if (gamesObservable.size() > 0) {
      written = true;
    }

    // write to artwork folder
    if (Globals.settings.getGameSettings().isEnableGameSetArtworkFolder()
        && StringUtils.isNotBlank(Globals.settings.getGameSettings().getGameSetArtworkFolder())) {
      writeImagesToArtworkFolder(true, false);
      written = true;
    }

    if (written) {
      firePropertyChange(POSTER, false, true);
    }
    else {
      // at least cache it
      if (StringUtils.isNotEmpty(posterUrl) && gamesObservable.size() == 0) {
        ImageFetcher task = new ImageFetcher("poster", posterUrl);
        Globals.executor.execute(task);
      }
    }

  }

  /**
   * Sets the fanart url.
   * 
   * @param newValue
   *          the new fanart url
   */
  @Override
  public void setFanartUrl(String newValue) {
    super.setFanartUrl(newValue);
    boolean written = false;
    String fanartFilename = "gameset-fanart.jpg";

    // write new fanart
    writeImageToGameFolder(gamesObservable, fanartFilename, fanartUrl);
    if (gamesObservable.size() > 0) {
      written = true;
    }

    // write to artwork folder
    if (Globals.settings.getGameSettings().isEnableGameSetArtworkFolder()
        && StringUtils.isNotBlank(Globals.settings.getGameSettings().getGameSetArtworkFolder())) {
      writeImagesToArtworkFolder(false, true);
      written = true;
    }

    if (written) {
      firePropertyChange(FANART, false, true);
    }
    else {
      // at least cache it
      if (StringUtils.isNotEmpty(fanartUrl) && gamesObservable.size() == 0) {
        ImageFetcher task = new ImageFetcher("fanart", fanartUrl);
        Globals.executor.execute(task);
      }
    }
  }

  /**
   * Gets the fanart.
   * 
   * @return the fanart
   */
  @Override
  public String getFanart() {
    String fanart = "";

    // try to get from the artwork folder if enabled
    if (Globals.settings.getGameSettings().isEnableGameSetArtworkFolder()) {
      String filename = Globals.settings.getGameSettings().getGameSetArtworkFolder() + File.separator + getTitle() + "-fanart.jpg";
      File fanartFile = new File(filename);
      if (fanartFile.exists()) {
        return filename;
      }
    }

    // try to get a fanart from one game
    for (Game game : gamesObservable) {
      String filename = game.getPath() + File.separator + "gameset-fanart.jpg";
      File fanartFile = new File(filename);
      if (fanartFile.exists()) {
        return filename;
      }
    }

    // we did not find an image from a game - get the cached file from the url
    File cachedFile = new File(ImageCache.getCacheDir() + File.separator + ImageCache.getCachedFileName(fanartUrl) + ".jpg");
    if (cachedFile.exists()) {
      return cachedFile.getPath();
    }

    return fanart;
  }

  /**
   * Gets the poster.
   * 
   * @return the poster
   */
  @Override
  public String getPoster() {
    String poster = "";

    // try to get from the artwork folder if enabled
    if (Globals.settings.getGameSettings().isEnableGameSetArtworkFolder()) {
      String filename = Globals.settings.getGameSettings().getGameSetArtworkFolder() + File.separator + getTitle() + "-poster.jpg";
      File fanartFile = new File(filename);
      if (fanartFile.exists()) {
        return filename;
      }
    }

    // try to get a fanart from one game
    List<Game> games = new ArrayList<Game>(gamesObservable);
    for (Game game : games) {
      String filename = game.getPath() + File.separator + "gameset-poster.jpg";
      File posterFile = new File(filename);
      if (posterFile.exists()) {
        return filename;
      }
    }

    // we did not find an image from a game - get the cached file from the url
    File cachedFile = new File(ImageCache.getCacheDir() + File.separator + ImageCache.getCachedFileName(posterUrl) + ".jpg");
    if (cachedFile.exists()) {
      return cachedFile.getPath();
    }

    return poster;
  }

  /**
   * Adds the game to the end of the list
   * 
   * @param game
   *          the game
   */
  public void addGame(Game game) {
    if (gamesObservable.contains(game)) {
      return;
    }
    gamesObservable.add(game);
    saveToDb();

    // // look for an tmdbid if no one available
    // if (tmdbId == 0) {
    // searchTmdbId();
    // }

    // write images
    List<Game> games = new ArrayList<Game>(1);
    games.add(game);
    writeImageToGameFolder(games, "gameset-fanart.jpg", fanartUrl);
    writeImageToGameFolder(games, "gameset-poster.jpg", posterUrl);

    firePropertyChange("addedGame", null, game);
    firePropertyChange("games", null, gamesObservable);
  }

  /**
   * Inserts the game into the right position of the list
   * 
   * @param game
   */
  public void insertGame(Game game) {
    if (gamesObservable.contains(game)) {
      return;
    }

    int index = Collections.binarySearch(gamesObservable, game, MOVIE_SET_COMPARATOR);
    if (index < 0) {
      gamesObservable.add(-index - 1, game);
    }
    else if (index >= 0) {
      gamesObservable.add(index, game);
    }

    saveToDb();

    // // look for an tmdbid if no one available
    // if (tmdbId == 0) {
    // searchTmdbId();
    // }

    // write images
    List<Game> games = new ArrayList<Game>(1);
    games.add(game);
    writeImageToGameFolder(games, "gameset-fanart.jpg", fanartUrl);
    writeImageToGameFolder(games, "gameset-poster.jpg", posterUrl);

    firePropertyChange("addedGame", null, game);
    firePropertyChange("games", null, gamesObservable);
  }

  /**
   * Removes the game.
   * 
   * @param game
   *          the game
   */
  public void removeGame(Game game) {
    // remove images from game folder
    File imageFile = new File(game.getPath() + File.separator + "gameset-fanart.jpg");
    if (imageFile.exists()) {
      imageFile.delete();
    }
    imageFile = new File(game.getPath() + File.separator + "gameset-poster.jpg");
    if (imageFile.exists()) {
      imageFile.delete();
    }
    if (game.getGameSet() != null) {
      game.setGameSet(null);
      game.saveToDb();
    }

    gamesObservable.remove(game);
    saveToDb();

    firePropertyChange("games", null, gamesObservable);
    firePropertyChange("removedGame", null, game);
  }

  /**
   * Gets the games.
   * 
   * @return the games
   */
  public List<Game> getGames() {
    return gamesObservable;
  }

  /**
   * Sort games.
   */
  public void sortGames() {
    Collections.sort(gamesObservable, MOVIE_SET_COMPARATOR);
    firePropertyChange("games", null, gamesObservable);
  }

  /**
   * Removes the all games.
   */
  public void removeAllGames() {
    // remove images from game folder
    for (Game game : gamesObservable) {
      File imageFile = new File(game.getPath() + File.separator + "gameset-fanart.jpg");
      if (imageFile.exists()) {
        imageFile.delete();
      }
      imageFile = new File(game.getPath() + File.separator + "gameset-poster.jpg");
      if (imageFile.exists()) {
        imageFile.delete();
      }

      if (game.getGameSet() != null) {
        game.setGameSet(null);
        game.saveToDb();
      }
    }

    // store all old games to remove the nodes in the tree
    List<Game> oldValue = new ArrayList<Game>(gamesObservable.size());
    oldValue.addAll(gamesObservable);
    gamesObservable.clear();
    saveToDb();

    firePropertyChange("games", null, gamesObservable);
    firePropertyChange("removedAllGames", oldValue, gamesObservable);
  }

  /**
   * Save to db.
   */
  public synchronized void saveToDb() {
    // update DB
    synchronized (Globals.entityManager) {
      Globals.entityManager.getTransaction().begin();
      Globals.entityManager.persist(this);
      Globals.entityManager.getTransaction().commit();
    }
  }

  /**
   * toString. used for JComboBox in game editor
   * 
   * @return the string
   */
  @Override
  public String toString() {
    return getTitle();
  }

  /**
   * Gets the game index.
   * 
   * @param game
   *          the game
   * @return the game index
   */
  public int getGameIndex(Game game) {
    return games.indexOf(game);
  }

  /**
   * Write image to game folder.
   * 
   * @param games
   *          the games
   * @param filename
   *          the filename
   * @param url
   *          the url
   */
  private void writeImageToGameFolder(List<Game> games, String filename, String url) {
    // check for empty strings or games
    if (games == null || games.size() == 0 || StringUtils.isEmpty(filename) || StringUtils.isEmpty(url)) {
      return;
    }

    // write image for all games
    for (Game game : games) {
      try {
        if (!game.isMultiGameDir()) {
          writeImage(url, game.getPath() + File.separator + filename);
        }
      }
      catch (IOException e) {
        LOGGER.warn("could not write files", e);
      }
    }
  }

  /**
   * Rewrite all images.
   */
  public void rewriteAllImages() {
    writeImageToGameFolder(gamesObservable, "gameset-fanart.jpg", fanartUrl);
    writeImageToGameFolder(gamesObservable, "gameset-poster.jpg", posterUrl);

    // write to artwork folder
    if (Globals.settings.getGameSettings().isEnableGameSetArtworkFolder()
        && StringUtils.isNotBlank(Globals.settings.getGameSettings().getGameSetArtworkFolder())) {
      writeImagesToArtworkFolder(true, true);
    }
  }

  /**
   * Write images to artwork folder.
   * 
   * @param poster
   *          the poster
   * @param fanart
   *          the fanart
   */
  private void writeImagesToArtworkFolder(boolean poster, boolean fanart) {
    // write images to artwork folder
    File artworkFolder = new File(Globals.settings.getGameSettings().getGameSetArtworkFolder());

    // check if folder exists
    if (!artworkFolder.exists()) {
      artworkFolder.mkdirs();
    }

    // write files
    try {
      // poster
      if (poster && StringUtils.isNotBlank(posterUrl)) {
        String providedFiletype = FilenameUtils.getExtension(posterUrl);
        writeImage(posterUrl, artworkFolder.getPath() + File.separator + getTitle() + "-folder." + providedFiletype);
      }
    }
    catch (IOException e) {
      LOGGER.warn("could not write files", e);
    }

    try {
      // fanart
      if (fanart && StringUtils.isNotBlank(fanartUrl)) {
        String providedFiletype = FilenameUtils.getExtension(fanartUrl);
        writeImage(fanartUrl, artworkFolder.getPath() + File.separator + getTitle() + "-fanart." + providedFiletype);
      }
    }
    catch (IOException e) {
      LOGGER.warn("could not write files", e);
    }
  }

  /**
   * Write image.
   * 
   * @param url
   *          the url
   * @param pathAndFilename
   *          the path and filename
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void writeImage(String url, String pathAndFilename) throws IOException {
    Url url1 = new Url(url);
    FileOutputStream outputStream = new FileOutputStream(pathAndFilename);
    InputStream is = url1.getInputStream();
    IOUtils.copy(is, outputStream);
    outputStream.flush();
    try {
      outputStream.getFD().sync(); // wait until file has been completely written
    }
    catch (Exception e) {
      // empty here -> just not let the thread crash
    }
    outputStream.close();
    is.close();

    ImageCache.invalidateCachedImage(pathAndFilename);
  }

  // /**
  // * Search tmdb id for this gameset.
  // */
  // public void searchTmdbId() {
  // try {
  // TmdbMetadataProvider tmdb = new TmdbMetadataProvider();
  // for (Game game : gamesObservable) {
  // MediaScrapeOptions options = new MediaScrapeOptions();
  // if (Utils.isValidImdbId(game.getImdbId()) || game.getTmdbId() > 0) {
  // options.setTmdbId(game.getTmdbId());
  // options.setImdbId(game.getImdbId());
  // MediaMetadata md = tmdb.getMetadata(options);
  // if (md.getTmdbIdSet() > 0) {
  // setTmdbId(md.getTmdbIdSet());
  // saveToDb();
  // break;
  // }
  // }
  // }
  // }
  // catch (Exception e) {
  // LOGGER.warn(e);
  // }
  // }

  /**
   * The Class ImageFetcher.
   * 
   * @author Manuel Laggner
   */
  private class ImageFetcher implements Runnable {

    /** The property name. */
    private String propertyName = "";

    /** The image url. */
    private String imageUrl     = "";

    /**
     * Instantiates a new image fetcher.
     * 
     * @param propertyName
     *          the property name
     * @param url
     *          the url
     */
    public ImageFetcher(String propertyName, String url) {
      this.propertyName = propertyName;
      this.imageUrl = url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
      String filename = ImageCache.getCachedFileName(imageUrl);
      File outputFile = new File(ImageCache.getCacheDir(), filename + ".jpg");

      try {
        Url url = new Url(imageUrl);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        InputStream is = url.getInputStream();
        IOUtils.copy(is, outputStream);
        outputStream.close();
        outputStream.flush();
        try {
          outputStream.getFD().sync(); // wait until file has been completely written
        }
        catch (Exception e) {
          // empty here -> just not let the thread crash
        }
        is.close();

        firePropertyChange(propertyName, "", outputFile);
      }
      catch (IOException e) {
        LOGGER.warn("error in image fetcher", e);
      }
    }
  }

  /**
   * The Class GameInGameSetComparator.
   * 
   * @author Manuel Laggner
   */
  private static class GameInGameSetComparator implements Comparator<Game> {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Game o1, Game o2) {
      Collator collator = null;

      if (o1 == null || o2 == null) {
        return 0;
      }

      // sort with sorttitle if available
      if (StringUtils.isNotBlank(o1.getSortTitle()) && StringUtils.isNotBlank(o2.getSortTitle())) {
        collator = Collator.getInstance();
        return collator.compare(o1.getSortTitle(), o2.getSortTitle());
      }

      // sort with release date if available
      if (o1.getReleaseDate() != null && o2.getReleaseDate() != null) {
        return o1.getReleaseDate().compareTo(o2.getReleaseDate());
      }

      // sort with year if available
      if (StringUtils.isNotBlank(o1.getYear()) && StringUtils.isNotBlank(o2.getYear())) {
        try {
          int year1 = Integer.parseInt(o1.getYear());
          int year2 = Integer.parseInt(o2.getYear());
          return year1 - year2;
        }
        catch (Exception e) {
        }
      }

      // fallback
      return 0;
    }

  }

  /**
   * Gets the checks for images.
   * 
   * @return the checks for images
   */
  public Boolean getHasImages() {
    if (!StringUtils.isEmpty(getPoster()) && !StringUtils.isEmpty(getFanart())) {
      return true;
    }
    return false;
  }

  /**
   * Gets the images to cache.
   * 
   * @return the images to cache
   */
  public List<File> getImagesToCache() {
    // get files to cache
    List<File> filesToCache = new ArrayList<File>();

    if (StringUtils.isNotBlank(getPoster())) {
      filesToCache.add(new File(getPoster()));
    }

    if (StringUtils.isNotBlank(getFanart())) {
      filesToCache.add(new File(getFanart()));
    }

    return filesToCache;
  }

  @Override
  public synchronized void callbackForWrittenArtwork(MediaArtworkType type) {
  }

  /**
   * recalculate all game sorttitles
   */
  public void updateGameSorttitle() {
    for (Game game : new ArrayList<Game>(gamesObservable)) {
      game.setSortTitleFromGameSet();
      game.saveToDb();
      game.writeNFO();
    }
  }

  public void setGiantbombId(Object giantbombId) {
    // TODO Auto-generated method stub

  }
}

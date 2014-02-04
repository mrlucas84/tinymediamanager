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
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.ImageCache;
import org.tinymediamanager.core.MediaEntity;
import org.tinymediamanager.core.MediaEntityImageFetcherTask;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.Utils;
import org.tinymediamanager.core.game.connector.GameConnectors;
import org.tinymediamanager.core.game.connector.GameToXbmcNfoConnector;
import org.tinymediamanager.core.game.tasks.GameActorImageFetcher;
import org.tinymediamanager.core.game.tasks.GameExtraImageFetcher;
import org.tinymediamanager.scraper.Certification;
import org.tinymediamanager.scraper.GameMediaGenres;
import org.tinymediamanager.scraper.MediaArtwork;
import org.tinymediamanager.scraper.MediaArtwork.MediaArtworkType;
import org.tinymediamanager.scraper.MediaCastMember;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaTrailer;
import org.tinymediamanager.scraper.util.Url;
import org.tinymediamanager.scraper.util.UrlUtil;

/**
 * The main class for games.
 * 
 * @author masterlilou
 */
@Entity
@Inheritance(strategy = javax.persistence.InheritanceType.JOINED)
public class Game extends MediaEntity {
  @XmlTransient
  private static final Logger   LOGGER          = LoggerFactory.getLogger(Game.class);

  private String                sortTitle       = "";
  private String                tagline         = "";
  private int                   votes           = 0;
  private int                   runtime         = 0;
  private String                director        = "";
  private String                writer          = "";
  private String                dataSource      = "";
  private boolean               IsFavorite      = false;
  private GameSet               gameSet;
  private boolean               isDisc          = false;
  private boolean               subtitles       = false;
  private String                platform        = "";
  private Date                  releaseDate     = null;
  protected String              publisher       = "";
  private boolean               multiGameDir    = false;                              // we detected more games in same folder

  private List<String>          genres          = new ArrayList<String>();
  private List<String>          tags            = new ArrayList<String>();
  private List<String>          extraThumbs     = new ArrayList<String>();
  private List<String>          extraFanarts    = new ArrayList<String>();

  @Enumerated(EnumType.STRING)
  private Certification         certification   = Certification.NOT_RATED;

  @OneToMany(cascade = CascadeType.ALL)
  private List<GameActor>       actors          = new ArrayList<GameActor>();

  @OneToMany(cascade = CascadeType.ALL)
  private List<MediaTrailer>    trailer         = new ArrayList<MediaTrailer>();

  @Transient
  private String                titleSortable   = "";

  @Transient
  private boolean               newlyAdded      = false;

  @Transient
  private List<GameMediaGenres> genresForAccess = new ArrayList<GameMediaGenres>();

  static {
    mediaFileComparator = new GameMediaFileComparator();
  }

  /**
   * Instantiates a new game. To initialize the propertychangesupport after loading
   */
  public Game() {
  }

  /**
   * checks if this game has been scraped.<br>
   * On a fresh DB, just reading local files, everything is again "unscraped". <br>
   * detect minimum of filled values as "scraped"
   * 
   * @return isScraped
   */
  @Override
  public boolean isScraped() {
    if (!scraped) {
      if (!plot.isEmpty() && !(year.isEmpty() || year.equals("0")) && !(genres == null || genres.size() == 0)
          && !(actors == null || actors.size() == 0) && (ids == null || ids.size() == 0)) {
        setScraped(true);
      }
    }
    return scraped;
  }

  public String getSortTitle() {
    return sortTitle;
  }

  public void setSortTitle(String newValue) {
    String oldValue = this.sortTitle;
    this.sortTitle = newValue;
    firePropertyChange(SORT_TITLE, oldValue, newValue);
  }

  public void setSortTitleFromGameSet() {
    if (gameSet != null) {
      int index = gameSet.getGameIndex(this) + 1;
      setSortTitle(gameSet.getTitle() + String.format("%02d", index));
    }
  }

  /**
   * Returns the sortable variant of title<br>
   * eg "The Bourne Legacy" -> "Bourne Legacy, The".
   */
  public String getTitleSortable() {
    if (StringUtils.isEmpty(titleSortable)) {
      titleSortable = Utils.getSortableName(this.getTitle());
    }
    return titleSortable;
  }

  public void clearTitleSortable() {
    titleSortable = "";
  }

  public Boolean getHasNfoFile() {
    List<MediaFile> mf = getMediaFiles(MediaFileType.NFO);
    if (mf != null && mf.size() > 0) {
      return true;
    }

    return false;
  }

  public Boolean getHasImages() {
    if (!StringUtils.isEmpty(getPoster()) && !StringUtils.isEmpty(getFanart())) {
      return true;
    }
    return false;
  }

  public Boolean getHasTrailer() {
    if (trailer != null && trailer.size() > 0) {
      return true;
    }
    return false;
  }

  public String getTitleForUi() {
    StringBuffer titleForUi = new StringBuffer(title);
    if (year != null && !year.isEmpty()) {
      titleForUi.append(" (");
      titleForUi.append(year);
      titleForUi.append(")");
    }
    return titleForUi.toString();
  }

  /**
   * Initialize after loading.
   */
  @Override
  public void initializeAfterLoading() {
    super.initializeAfterLoading();

    // remove empty tag and null values
    Utils.removeEmptyStringsFromList(tags);
    Utils.removeEmptyStringsFromList(genres);

    // load genres
    for (String genre : new ArrayList<String>(genres)) {
      addGenre(GameMediaGenres.getGenre(genre));
    }
  }

  public void addActor(GameActor obj) {
    this.actors.add(obj);
    firePropertyChange(ACTORS, null, this.getActors());
  }

  public List<MediaTrailer> getTrailers() {
    return this.trailer;
  }

  public void addTrailer(MediaTrailer obj) {
    this.trailer.add(obj);
    firePropertyChange(TRAILER, null, this.trailer);
  }

  public void removeAllTrailers() {
    this.trailer.clear();
    firePropertyChange(TRAILER, null, this.trailer);
  }

  /**
   * Downloads trailer to game folder (get from NFO), naming <code>&lt;game&gt;-trailer.ext</code><br>
   * Downloads to .tmp file first and renames after successful download.
   * 
   * @param trailerToDownload
   *          the MediaTrailer object to download
   * @return true/false if successful
   * @author Myron Boyle
   */
  public Boolean downladTtrailer(MediaTrailer trailerToDownload) {
    try {
      // get trailer filename from first mediafile
      String tfile = GameRenamer.createDestinationForFilename(Globals.settings.getGameSettings().getGameRenamerFilename(), this) + "-trailer.";
      String ext = UrlUtil.getFileExtension(trailerToDownload.getUrl());
      if (ext.isEmpty()) {
        ext = "unknown";
      }
      // TODO: push to threadpool
      // download to temp first
      trailerToDownload.downloadTo(tfile + ext + ".tmp");
      LOGGER.info("Trailer download successfully");
      // TODO: maybe check if there are other trailerfiles (with other
      // extension) and remove
      File trailer = new File(tfile + ext);
      FileUtils.deleteQuietly(trailer);
      boolean ok = Utils.moveFileSafe(new File(tfile + ext + ".tmp"), trailer);
    }
    catch (IOException e) {
      LOGGER.error("Error downloading trailer", e);
      return false;
    }
    catch (URISyntaxException e) {
      LOGGER.error("Error downloading trailer; url invalid", e);
      return false;
    }
    catch (Exception e) {
      LOGGER.error("Error downloading trailer; rename failed", e);
    }
    return true;
  }

  public void addToTags(String newTag) {
    if (StringUtils.isBlank(newTag)) {
      return;
    }

    for (String tag : this.tags) {
      if (tag.equals(newTag)) {
        return;
      }
    }

    this.tags.add(newTag);
    firePropertyChange(TAG, null, this.tags);
    firePropertyChange(TAGS_AS_STRING, null, newTag);
  }

  public void removeFromTags(String removeTag) {
    this.tags.remove(removeTag);
    firePropertyChange(TAG, null, this.tags);
    firePropertyChange(TAGS_AS_STRING, null, removeTag);
  }

  public void setTags(List<String> newTags) {
    // two way sync of tags

    // first, add new ones
    for (String tag : newTags) {
      if (!this.tags.contains(tag)) {
        this.tags.add(tag);
      }
    }

    // second remove old ones
    for (int i = this.tags.size() - 1; i >= 0; i--) {
      String tag = this.tags.get(i);
      if (!newTags.contains(tag)) {
        this.tags.remove(tag);
      }
    }

    Utils.removeEmptyStringsFromList(this.tags);

    firePropertyChange(TAG, null, this.tags);
    firePropertyChange(TAGS_AS_STRING, null, this.tags);
  }

  public String getTagsAsString() {
    StringBuilder sb = new StringBuilder();
    for (String tag : tags) {
      if (!StringUtils.isEmpty(sb)) {
        sb.append(", ");
      }
      sb.append(tag);
    }
    return sb.toString();
  }

  public List<String> getTags() {
    return this.tags;
  }

  public String getDataSource() {
    return dataSource;
  }

  public void setDataSource(String newValue) {
    String oldValue = this.dataSource;
    this.dataSource = newValue;
    firePropertyChange(DATA_SOURCE, oldValue, newValue);
  }

  /** has game local (or any mediafile inline) subtitles? */
  public boolean hasSubtitles() {
    if (this.subtitles) {
      return true; // local ones found
    }

    for (MediaFile mf : getMediaFiles(MediaFileType.GAME)) {
      if (mf.hasSubtitles()) {
        return true;
      }
    }

    return false;
  }

  public void setSubtitles(boolean sub) {
    this.subtitles = sub;
  }

  public List<GameActor> getActors() {
    return this.actors;
  }

  public String getDirector() {
    return director;
  }

  public int getVotes() {
    return votes;
  }

  public void setVotes(int newValue) {
    int oldValue = this.votes;
    this.votes = newValue;
    firePropertyChange(VOTES, oldValue, newValue);
  }

  public int getRuntime() {
    return runtime == 0 ? getRuntimeFromMediaFilesInMinutes() : runtime;
  }

  public String getTagline() {
    return tagline;
  }

  public String getWriter() {
    return writer;
  }

  public boolean hasFile(String filename) {
    if (StringUtils.isEmpty(filename)) {
      return false;
    }

    for (MediaFile file : new ArrayList<MediaFile>(getMediaFiles())) {
      if (filename.compareTo(file.getFilename()) == 0) {
        return true;
      }
    }

    return false;
  }

  public void removeActor(GameActor obj) {
    this.actors.remove(obj);
    firePropertyChange(ACTORS, null, this.getActors());
  }

  public List<String> getExtraThumbs() {
    return extraThumbs;
  }

  public void setExtraThumbs(List<String> extraThumbs) {
    this.extraThumbs = extraThumbs;
  }

  public void downloadExtraThumbs(List<String> thumbs) {
    // init/delete old thumbs
    extraThumbs.clear();

    // do not create extrathumbs folder, if no extrathumbs are selected
    if (thumbs.size() == 0) {
      return;
    }

    try {
      String path = getPath() + File.separator + "extrathumbs";
      File folder = new File(path);
      if (folder.exists()) {
        FileUtils.deleteDirectory(folder);
        removeAllMediaFiles(MediaFileType.THUMB);
      }

      folder.mkdirs();

      // fetch and store images
      for (int i = 0; i < thumbs.size(); i++) {
        String url = thumbs.get(i);
        String providedFiletype = FilenameUtils.getExtension(url);

        FileOutputStream outputStream = null;
        InputStream is = null;
        File file = null;
        if (Globals.settings.getGameSettings().isImageExtraThumbsResize() && Globals.settings.getGameSettings().getImageExtraThumbsSize() > 0) {
          file = new File(path, "thumb" + (i + 1) + ".jpg");
          outputStream = new FileOutputStream(file);
          try {
            is = ImageCache.scaleImage(url, Globals.settings.getGameSettings().getImageExtraThumbsSize());
          }
          catch (Exception e) {
            LOGGER.warn("problem with rescaling: " + e.getMessage());
            continue;
          }
        }
        else {
          file = new File(path, "thumb" + (i + 1) + "." + providedFiletype);
          outputStream = new FileOutputStream(file);
          Url url1 = new Url(url);
          is = url1.getInputStream();
        }

        IOUtils.copy(is, outputStream);
        outputStream.flush();
        try {
          outputStream.getFD().sync();
        }
        catch (Exception e) {
          // empty here -> just not let the thread crash
        }
        outputStream.close();
        is.close();

        MediaFile mf = new MediaFile(file, MediaFileType.THUMB);
        mf.gatherMediaInformation();
        addToMediaFiles(mf);
      }
    }
    catch (IOException e) {
      LOGGER.warn("download extrathumbs", e);
    }
    catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
  }

  public List<String> getExtraFanarts() {
    return extraFanarts;
  }

  public void setExtraFanarts(List<String> extraFanarts) {
    this.extraFanarts = extraFanarts;
  }

  public void downloadExtraFanarts(List<String> fanarts) {
    // init/delete old fanarts
    extraFanarts.clear();

    // do not create extrafanarts folder, if no extrafanarts are selected
    if (fanarts.size() == 0) {
      return;
    }

    try {
      String path = getPath() + File.separator + "extrafanart";
      File folder = new File(path);
      if (folder.exists()) {
        FileUtils.deleteDirectory(folder);
        removeAllMediaFiles(MediaFileType.EXTRAFANART);
      }

      folder.mkdirs();

      // fetch and store images
      for (int i = 0; i < fanarts.size(); i++) {
        String urlAsString = fanarts.get(i);
        String providedFiletype = FilenameUtils.getExtension(urlAsString);
        Url url = new Url(urlAsString);
        File file = new File(path, "fanart" + (i + 1) + "." + providedFiletype);
        FileOutputStream outputStream = new FileOutputStream(file);
        InputStream is = url.getInputStream();
        IOUtils.copy(is, outputStream);
        outputStream.flush();
        try {
          outputStream.getFD().sync();
        }
        catch (Exception e) {
          // empty here -> just not let the thread crash
        }
        outputStream.close();

        is.close();
        MediaFile mf = new MediaFile(file, MediaFileType.EXTRAFANART);
        mf.gatherMediaInformation();
        addToMediaFiles(mf);
      }
    }
    catch (IOException e) {
      LOGGER.warn("download extrafanarts", e);
    }
  }

  public void writeExtraImages(boolean extrathumbs, boolean extrafanart) {
    // get images in thread
    GameExtraImageFetcher task = new GameExtraImageFetcher(this, extrafanart, extrathumbs);
    Globals.executor.execute(task);
  }

  public void setMetadata(MediaMetadata metadata, GameScraperMetadataConfig config) {
    // check if metadata has at least a name
    if (StringUtils.isEmpty(metadata.getStringValue(MediaMetadata.TITLE))) {
      LOGGER.warn("wanted to save empty metadata for " + getTitle());
      return;
    }

    setIds(metadata.getIds());

    // set chosen metadata
    if (config.isTitle()) {
      setTitle(metadata.getStringValue(MediaMetadata.TITLE));
    }

    if (config.isOriginalTitle()) {
      setOriginalTitle(metadata.getStringValue(MediaMetadata.ORIGINAL_TITLE));
    }

    if (config.isTagline()) {
      setTagline(metadata.getStringValue(MediaMetadata.TAGLINE));
    }

    if (config.isPlot()) {
      setPlot(metadata.getStringValue(MediaMetadata.PLOT));
    }

    if (config.isYear()) {
      setYear(metadata.getStringValue(MediaMetadata.YEAR));
      try {
        setReleaseDate(metadata.getStringValue(MediaMetadata.RELEASE_DATE));
      }
      catch (ParseException e) {
        LOGGER.warn(e.getMessage());
      }
    }

    if (config.isRating()) {
      setRating(metadata.getFloatValue(MediaMetadata.RATING));
      setVotes(metadata.getIntegerValue(MediaMetadata.VOTE_COUNT));
    }

    if (config.isPublisher()) {
      setPublisher(metadata.getStringValue(MediaMetadata.PUBLISHER));
    }

    setPlatform(metadata.getStringValue(MediaMetadata.PLATFORM));

    // certifications
    if (config.isCertification()) {
      if (metadata.getCertifications() != null && metadata.getCertifications().size() > 0) {
        setCertification(metadata.getCertifications().get(0));
      }
    }

    // cast
    if (config.isCast()) {
      setProductionCompany(metadata.getStringValue(MediaMetadata.PRODUCTION_COMPANY));
      List<GameActor> actors = new ArrayList<GameActor>();
      String director = "";
      String writer = "";
      for (MediaCastMember member : metadata.getCastMembers()) {
        switch (member.getType()) {
          case ACTOR:
            GameActor actor = new GameActor();
            actor.setName(member.getName());
            actor.setCharacter(member.getCharacter());
            actor.setThumb(member.getImageUrl());
            actors.add(actor);
            break;

          case DIRECTOR:
            if (!StringUtils.isEmpty(director)) {
              director += ", ";
            }
            director += member.getName();
            break;

          case WRITER:
            if (!StringUtils.isEmpty(writer)) {
              writer += ", ";
            }
            writer += member.getName();
            break;

          default:
            break;
        }
      }
      setActors(actors);
      setDirector(director);
      setWriter(writer);

      writeActorImages();
    }

    // genres
    if (config.isGenres()) {
      setGenres(metadata.getGameGenres());
    }

    // set scraped
    setScraped(true);

    // update DB
    saveToDb();

    // create GameSet
    if (config.isCollection()) {
      // FIXME
      // int col = metadata.getIntegerValue(MediaMetadata.TMDBID_SET);
      // if (col != 0) {
      // GameSet gameSet = GameList.getInstance().getGameSet(metadata.getStringValue(MediaMetadata.COLLECTION_NAME),
      // metadata.getIntegerValue(MediaMetadata.TMDBID));
      // if (gameSet.getTmdbId() == 0) {
      // gameSet.setTmdbId(col);
      // // get gameset metadata
      // try {
      // giantbombMetadataProvider mp = new giantbombMetadataProvider();
      // MediaScrapeOptions options = new MediaScrapeOptions();
      // options.setTmdbId(col);
      // options.setLanguage(Globals.settings.getGameSettings().getScraperLanguage());
      // options.setCountry(Globals.settings.getGameSettings().getCertificationCountry());
      // }
      // catch (Exception e) {
      // }
      // }
      //
      // // add game to gameset
      // if (gameSet != null) {
      // // first remove from "old" gameset
      // setGameSet(null);
      //
      // // add to new gameset
      // // gameSet.addGame(this);
      // setGameSet(gameSet);
      // gameSet.insertGame(this);
      // gameSet.updateGameSorttitle();
      // // saveToDb();
      // }
      // }
    }

    // write NFO
    writeNFO();

  }

  public void setTrailers(List<MediaTrailer> trailers) {
    removeAllTrailers();
    for (MediaTrailer trailer : trailers) {
      if (this.trailer.size() == 0 && !trailer.getUrl().startsWith("file")) {
        trailer.setInNfo(Boolean.TRUE);
      }
      addTrailer(trailer);
    }

    // persist
    saveToDb();
  }

  public MediaMetadata getMetadata() {
    MediaMetadata md = new MediaMetadata("");

    for (Entry<String, Object> entry : ids.entrySet()) {
      md.setId(entry.getKey(), entry.getValue());
    }

    md.storeMetadata(MediaMetadata.TITLE, title);
    md.storeMetadata(MediaMetadata.ORIGINAL_TITLE, originalTitle);
    md.storeMetadata(MediaMetadata.TAGLINE, tagline);
    md.storeMetadata(MediaMetadata.PLOT, plot);
    md.storeMetadata(MediaMetadata.YEAR, year);
    md.storeMetadata(MediaMetadata.RATING, rating);
    md.storeMetadata(MediaMetadata.VOTE_COUNT, votes);
    md.storeMetadata(MediaMetadata.RUNTIME, runtime);
    md.storeMetadata(MediaMetadata.PLATFORM, platform);
    md.addCertification(certification);

    return md;
  }

  /**
   * Sets the artwork.
   * 
   * @param md
   *          the md
   * @param config
   *          the config
   */
  public void setArtwork(MediaMetadata md, GameScraperMetadataConfig config) {
    setArtwork(md.getMediaArt(MediaArtworkType.ALL), config);
  }

  /**
   * Sets the artwork.
   * 
   * @param artwork
   *          the artwork
   * @param config
   *          the config
   * @param list
   */
  public void setArtwork(List<MediaArtwork> artwork, GameScraperMetadataConfig config) {
    if (config.isArtwork()) {
      // poster
      boolean posterFound = false;
      for (MediaArtwork art : artwork) {
        // only get artwork in desired resolution
        if (art.getType() == MediaArtworkType.POSTER && art.getSizeOrder() == Globals.settings.getGameSettings().getImagePosterSize().getOrder()) {
          setPosterUrl(art.getDefaultUrl());

          LOGGER.debug(art.getSmallestArtwork().toString());
          LOGGER.debug(art.getBiggestArtwork().toString());

          posterFound = true;
          break;
        }
      }
      // if there has nothing been found, do a fallback
      if (!posterFound) {
        for (MediaArtwork art : artwork) {
          if (art.getType() == MediaArtworkType.POSTER) {
            setPosterUrl(art.getDefaultUrl());

            LOGGER.debug(art.getSmallestArtwork().toString());
            LOGGER.debug(art.getBiggestArtwork().toString());

            break;
          }
        }
      }

      // fanart
      boolean fanartFound = false;
      for (MediaArtwork art : artwork) {
        // only get artwork in desired resolution
        if (art.getType() == MediaArtworkType.BACKGROUND && art.getSizeOrder() == Globals.settings.getGameSettings().getImageFanartSize().getOrder()) {
          setFanartUrl(art.getDefaultUrl());

          LOGGER.debug(art.getSmallestArtwork().toString());
          LOGGER.debug(art.getBiggestArtwork().toString());

          fanartFound = true;
          break;
        }
      }

      // no fanart has been found - do a fallback
      if (!fanartFound) {
        for (MediaArtwork art : artwork) {
          // only get artwork in desired resolution
          if (art.getType() == MediaArtworkType.BACKGROUND) {
            setFanartUrl(art.getDefaultUrl());

            LOGGER.debug(art.getSmallestArtwork().toString());
            LOGGER.debug(art.getBiggestArtwork().toString());

            break;
          }
        }
      }

      if (!isMultiGameDir()) {
        // extrathumbs
        List<String> extrathumbs = new ArrayList<String>();
        if (Globals.settings.getGameSettings().isImageExtraThumbs() && Globals.settings.getGameSettings().getImageExtraThumbsCount() > 0) {
          for (MediaArtwork art : artwork) {
            // only get artwork in desired resolution
            if (art.getType() == MediaArtworkType.BACKGROUND
                && art.getSizeOrder() == Globals.settings.getGameSettings().getImageFanartSize().getOrder()) {
              extrathumbs.add(art.getDefaultUrl());
              if (extrathumbs.size() >= Globals.settings.getGameSettings().getImageExtraThumbsCount()) {
                break;
              }
            }
          }
          setExtraThumbs(extrathumbs);
        }

        // extrafanarts
        List<String> extrafanarts = new ArrayList<String>();
        if (Globals.settings.getGameSettings().isImageExtraFanart() && Globals.settings.getGameSettings().getImageExtraFanartCount() > 0) {
          for (MediaArtwork art : artwork) {
            // only get artwork in desired resolution
            if (art.getType() == MediaArtworkType.BACKGROUND
                && art.getSizeOrder() == Globals.settings.getGameSettings().getImageFanartSize().getOrder()) {
              extrafanarts.add(art.getDefaultUrl());
              if (extrafanarts.size() >= Globals.settings.getGameSettings().getImageExtraFanartCount()) {
                break;
              }
            }
          }
          setExtraFanarts(extrafanarts);
        }

        // download extra images
        if (extrathumbs.size() > 0 || extrafanarts.size() > 0) {
          writeExtraImages(true, true);
        }
      }

      // download images
      writeImages(true, true);
      // update DB
      saveToDb();
    }
  }

  /**
   * Sets the actors.
   * 
   * @param newActors
   *          the new actors
   */
  public void setActors(List<GameActor> newActors) {
    // two way sync of actors

    // first add the new ones
    for (GameActor actor : newActors) {
      if (!this.actors.contains(actor)) {
        this.actors.add(actor);
      }
    }

    // second remove unused
    for (int i = this.actors.size() - 1; i >= 0; i--) {
      GameActor actor = this.actors.get(i);
      if (!newActors.contains(actor)) {
        this.actors.remove(actor);
      }
    }

    firePropertyChange(ACTORS, null, this.getActors());
  }

  @Override
  public void setTitle(String newValue) {
    String oldValue = this.title;
    super.setTitle(newValue);

    firePropertyChange(TITLE_FOR_UI, oldValue, newValue);

    oldValue = this.titleSortable;
    titleSortable = "";
    firePropertyChange(TITLE_SORTABLE, oldValue, titleSortable);
  }

  public void setRuntime(int newValue) {
    int oldValue = this.runtime;
    this.runtime = newValue;
    firePropertyChange(RUNTIME, oldValue, newValue);
  }

  public void setTagline(String newValue) {
    String oldValue = this.tagline;
    this.tagline = newValue;
    firePropertyChange("tagline", oldValue, newValue);
  }

  /**
   * Sets the year.
   * 
   * @param newValue
   *          the new year
   */
  @Override
  public void setYear(String newValue) {
    String oldValue = year;
    super.setYear(newValue);

    firePropertyChange(TITLE_FOR_UI, oldValue, newValue);
  }

  /**
   * all XBMC supported poster names. (without path!)
   * 
   * @param poster
   *          the poster
   * @return the poster filename
   */
  public String getPosterFilename(GamePosterNaming poster) {
    List<MediaFile> mfs = getMediaFiles(MediaFileType.GAME);
    if (mfs != null && mfs.size() > 0) {
      return getPosterFilename(poster, mfs.get(0).getFilename());
    }
    else {
      return getPosterFilename(poster, ""); // no video files
    }
  }

  public String getPosterFilename(GamePosterNaming poster, String newGameFilename) {
    String filename = "";
    String mediafile = Utils.cleanStackingMarkers(FilenameUtils.getBaseName(newGameFilename));

    switch (poster) {
      case GAMENAME_POSTER_PNG:
        filename += getTitle() + ".png";
        break;
      case GAMENAME_POSTER_JPG:
        filename += getTitle() + ".jpg";
        break;
      case FILENAME_POSTER_PNG:
        filename += mediafile.isEmpty() ? "" : mediafile + "-poster.png";
        break;
      case FILENAME_POSTER_JPG:
        filename += mediafile.isEmpty() ? "" : mediafile + "-poster.jpg";
        break;
      case GAMENAME_PNG:
        filename += mediafile.isEmpty() ? "" : mediafile + ".png";
        break;
      case GAMENAME_JPG:
        filename += mediafile.isEmpty() ? "" : mediafile + ".jpg";
        break;
      case GAME_PNG:
        filename += "game.png";
        break;
      case GAME_JPG:
        filename += "game.jpg";
        break;
      case POSTER_PNG:
        filename += "poster.png";
        break;
      case POSTER_JPG:
        filename += "poster.jpg";
        break;
      case FOLDER_PNG:
        filename += "folder.png";
        break;
      case FOLDER_JPG:
        filename += "folder.jpg";
        break;
      default:
        filename = "";
        break;
    }
    return filename;
  }

  /**
   * all XBMC supported fanart names. (without path!)
   * 
   * @param fanart
   *          the fanart
   * @return the fanart filename
   */
  public String getFanartFilename(GameFanartNaming fanart) {
    List<MediaFile> mfs = getMediaFiles(MediaFileType.GAME);
    if (mfs != null && mfs.size() > 0) {
      return getFanartFilename(fanart, mfs.get(0).getFilename());
    }
    else {
      return getFanartFilename(fanart, ""); // no video files
    }
  }

  public String getFanartFilename(GameFanartNaming fanart, String newGameFilename) {
    String filename = "";
    String mediafile = Utils.cleanStackingMarkers(FilenameUtils.getBaseName(newGameFilename));

    switch (fanart) {
      case FANART_PNG:
        filename += "fanart.png";
        break;
      case FANART_JPG:
        filename += "fanart.jpg";
        break;
      case FILENAME_FANART_PNG:
        filename += mediafile.isEmpty() ? "" : mediafile + "-fanart.png";
        break;
      case FILENAME_FANART_JPG:
        filename += mediafile.isEmpty() ? "" : mediafile + "-fanart.jpg";
        break;
      case FILENAME_FANART2_PNG:
        filename += mediafile.isEmpty() ? "" : mediafile + ".fanart.png";
        break;
      case FILENAME_FANART2_JPG:
        filename += mediafile.isEmpty() ? "" : mediafile + ".fanart.jpg";
        break;
      case MOVIENAME_FANART_PNG:
        filename += getTitle() + "-fanart.png";
        break;
      case MOVIENAME_FANART_JPG:
        filename += getTitle() + "-fanart.jpg";
        break;
      default:
        filename = "";
        break;
    }
    return filename;
  }

  /**
   * all XBMC supported NFO names. (without path!)
   * 
   * @param nfo
   *          the nfo
   * @return the nfo filename
   */
  public String getNfoFilename(GameNfoNaming nfo) {
    List<MediaFile> mfs = getMediaFiles(MediaFileType.GAME);
    if (mfs != null && mfs.size() > 0) {
      return getNfoFilename(nfo, mfs.get(0).getFilename());
    }
    else {
      return getNfoFilename(nfo, ""); // no video files
    }
  }

  /**
   * all XBMC supported NFO names. (without path!)
   * 
   * @param nfo
   *          the nfo filenaming
   * @param newGameFilename
   *          the new/desired game filename
   * @return the nfo filename
   */
  public String getNfoFilename(GameNfoNaming nfo, String newGameFilename) {
    String filename = "";

    switch (nfo) {
      case FILENAME_NFO:
        String gameFilename = FilenameUtils.getBaseName(newGameFilename);
        filename += gameFilename.isEmpty() ? "" : Utils.cleanStackingMarkers(gameFilename) + ".nfo"; // w/o stacking information
        break;
      case GAME_NFO:
        filename += "game.nfo";
        break;
      default:
        filename = "";
        break;
    }
    return filename;
  }

  /**
   * Write images.
   * 
   * @param poster
   *          the poster
   * @param fanart
   *          the fanart
   */
  public void writeImages(boolean poster, boolean fanart) {
    String filename = null;

    // poster
    if (poster && !StringUtils.isEmpty(getPosterUrl())) {
      // try {
      int i = 0;
      List<GamePosterNaming> posternames = new ArrayList<GamePosterNaming>();
      if (isMultiGameDir()) {
        // Fixate the name regardless of setting
        posternames.add(GamePosterNaming.FILENAME_POSTER_JPG);
        posternames.add(GamePosterNaming.FILENAME_POSTER_PNG);
      }
      else {
        posternames = Globals.settings.getGameSettings().getGamePosterFilenames();
      }
      for (GamePosterNaming name : posternames) {
        boolean firstImage = false;
        filename = getPosterFilename(name);

        // only store .png as png and .jpg as jpg
        String generatedFiletype = FilenameUtils.getExtension(filename);
        String providedFiletype = FilenameUtils.getExtension(getPosterUrl());
        if (!generatedFiletype.equals(providedFiletype)) {
          continue;
        }

        if (++i == 1) {
          firstImage = true;
        }

        // get image in thread
        MediaEntityImageFetcherTask task = new MediaEntityImageFetcherTask(this, getPosterUrl(), MediaArtworkType.POSTER, filename, firstImage);
        Globals.executor.execute(task);
      }
    }

    // fanart
    if (fanart && !StringUtils.isEmpty(getFanartUrl())) {
      int i = 0;
      List<GameFanartNaming> fanartnames = new ArrayList<GameFanartNaming>();
      if (isMultiGameDir()) {
        // Fixate the name regardless of setting
        fanartnames.add(GameFanartNaming.FILENAME_FANART_JPG);
        fanartnames.add(GameFanartNaming.FILENAME_FANART_PNG);
      }
      else {
        fanartnames = Globals.settings.getGameSettings().getGameFanartFilenames();
      }
      for (GameFanartNaming name : fanartnames) {
        boolean firstImage = false;
        filename = getFanartFilename(name);

        // only store .png as png and .jpg as jpg
        String generatedFiletype = FilenameUtils.getExtension(filename);
        String providedFiletype = FilenameUtils.getExtension(getFanartUrl());
        if (!generatedFiletype.equals(providedFiletype)) {
          continue;
        }

        if (++i == 1) {
          firstImage = true;
        }

        // get image in thread
        MediaEntityImageFetcherTask task = new MediaEntityImageFetcherTask(this, getFanartUrl(), MediaArtworkType.BACKGROUND, filename, firstImage);
        Globals.executor.execute(task);
      }
    }
  }

  /**
   * Write actor images.
   */
  public void writeActorImages() {
    // check if actor images shall be written
    if (!Globals.settings.getGameSettings().isWriteActorImages() || isMultiGameDir()) {
      return;
    }

    GameActorImageFetcher task = new GameActorImageFetcher(this);
    Globals.executor.execute(task);
  }

  /**
   * Write nfo.
   */
  public void writeNFO() {
    if (Globals.settings.getGameSettings().getGameConnector() == GameConnectors.XBMC) {
      GameToXbmcNfoConnector.setData(this);
    }
    firePropertyChange(HAS_NFO_FILE, false, true);
  }

  /**
   * Sets the director.
   * 
   * @param newValue
   *          the new director
   */
  public void setDirector(String newValue) {
    String oldValue = this.director;
    this.director = newValue;
    firePropertyChange(DIRECTOR, oldValue, newValue);
  }

  /**
   * Sets the writer.
   * 
   * @param newValue
   *          the new writer
   */
  public void setWriter(String newValue) {
    String oldValue = this.writer;
    this.writer = newValue;
    firePropertyChange(WRITER, oldValue, newValue);
  }

  /**
   * Gets the genres.
   * 
   * @return the genres
   */
  public List<GameMediaGenres> getGenres() {
    return genresForAccess;
  }

  /**
   * Adds the genre.
   * 
   * @param newValue
   *          the new value
   */
  public void addGenre(GameMediaGenres newValue) {
    if (!genresForAccess.contains(newValue)) {
      genresForAccess.add(newValue);
      if (!genres.contains(newValue.name())) {
        genres.add(newValue.name());
      }
      firePropertyChange(GENRE, null, newValue);
      firePropertyChange(GENRES_AS_STRING, null, newValue);
    }
  }

  /**
   * Sets the genres.
   * 
   * @param genres
   *          the new genres
   */
  public void setGenres(List<GameMediaGenres> genres) {
    // two way sync of genres

    // first, add new ones
    for (GameMediaGenres genre : genres) {
      if (!this.genresForAccess.contains(genre)) {
        this.genresForAccess.add(genre);
        if (!genres.contains(genre.name())) {
          this.genres.add(genre.name());
        }
      }
    }

    // second remove old ones
    for (int i = this.genresForAccess.size() - 1; i >= 0; i--) {
      GameMediaGenres genre = this.genresForAccess.get(i);
      if (!genres.contains(genre)) {
        this.genresForAccess.remove(genre);
        this.genres.remove(genre.name());
      }
    }

    firePropertyChange(GENRE, null, genres);
    firePropertyChange(GENRES_AS_STRING, null, genres);
  }

  /**
   * Removes the genre.
   * 
   * @param genre
   *          the genre
   */
  public void removeGenre(GameMediaGenres genre) {
    if (genresForAccess.contains(genre)) {
      genresForAccess.remove(genre);
      genres.remove(genre.name());
      firePropertyChange(GENRE, null, genre);
      firePropertyChange(GENRES_AS_STRING, null, genre);
    }
  }

  /**
   * Gets the certifications.
   * 
   * @return the certifications
   */
  public Certification getCertification() {
    return certification;
  }

  /**
   * Sets the certifications.
   * 
   * @param newValue
   *          the new certifications
   */
  public void setCertification(Certification newValue) {
    this.certification = newValue;
    firePropertyChange(CERTIFICATION, null, newValue);
  }

  /**
   * Gets the checks for rating.
   * 
   * @return the checks for rating
   */
  public boolean getHasRating() {
    if (rating > 0 || scraped) {
      return true;
    }
    return false;
  }

  /**
   * Gets the genres as string.
   * 
   * @return the genres as string
   */
  public String getGenresAsString() {
    StringBuilder sb = new StringBuilder();
    for (GameMediaGenres genre : genresForAccess) {
      if (!StringUtils.isEmpty(sb)) {
        sb.append(", ");
      }
      sb.append(genre != null ? genre.getLocalizedName() : "null");
    }
    return sb.toString();
  }

  /**
   * Checks if is IsFavorite.
   * 
   * @return true, if is IsFavorite
   */
  public boolean isIsFavorite() {
    return IsFavorite;
  }

  /**
   * Sets the IsFavorite.
   * 
   * @param newValue
   *          the new IsFavorite
   */
  public void setIsFavorite(boolean newValue) {
    boolean oldValue = this.IsFavorite;
    this.IsFavorite = newValue;
    firePropertyChange(IS_FAVORITE, oldValue, newValue);
  }

  /**
   * Checks if this game is in a folder with other games and not in an own folder<br>
   * so disable everything except renaming
   * 
   * @return true, if in datasource root
   */
  public boolean isMultiGameDir() {
    return multiGameDir;
  }

  /**
   * Sets the flag, that the game is not in an own folder<br>
   * so disable everything except renaming
   * 
   * @param multiDir
   *          true/false
   */
  public void setMultiGameDir(boolean multiDir) {
    this.multiGameDir = multiDir;
  }

  /**
   * <p>
   * Uses <code>ReflectionToStringBuilder</code> to generate a <code>toString</code> for the specified object.
   * </p>
   * 
   * @return the String result
   * @see ReflectionToStringBuilder#toString(Object)
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  /**
   * Gets the game set.
   * 
   * @return the gameset
   */
  public GameSet getGameSet() {
    return gameSet;
  }

  /**
   * Sets the game set.
   * 
   * @param newValue
   *          the new game set
   */
  public void setGameSet(GameSet newValue) {
    GameSet oldValue = this.gameSet;
    this.gameSet = newValue;

    // remove gameset-sorttitle
    if (oldValue != null && newValue == null) {
      setSortTitle("");
    }

    firePropertyChange(MOVIESET, oldValue, newValue);
    firePropertyChange(MOVIESET_TITLE, oldValue, newValue);
  }

  public void gameSetTitleChanged() {
    firePropertyChange(MOVIESET_TITLE, null, "");
  }

  public String getGameSetTitle() {
    if (gameSet != null) {
      return gameSet.getTitle();
    }
    return "";
  }

  /**
   * Removes the from game set.
   */
  public void removeFromGameSet() {
    if (gameSet != null) {
      gameSet.removeGame(this);
    }
    setGameSet(null);
  }

  /**
   * is this a disc game folder (video_ts / bdmv)?.
   * 
   * @return true, if is disc
   */
  public boolean isDisc() {
    return isDisc;
  }

  /**
   * is this a disc game folder (video_ts / bdmv)?.
   * 
   * @param isDisc
   *          the new disc
   */
  public void setDisc(boolean isDisc) {
    this.isDisc = isDisc;
  }

  /**
   * has this game been newlay added in our list?!
   * 
   * @return true/false
   */
  public boolean isNewlyAdded() {
    return this.newlyAdded;
  }

  /**
   * has this game been newlay added in our list?!
   * 
   * @param newlyAdded
   *          true/false
   */
  public void setNewlyAdded(boolean newlyAdded) {
    this.newlyAdded = newlyAdded;
  }

  /**
   * Gets the media info video format (i.e. 720p).
   * 
   * @return the media info video format
   */
  public String getMediaInfoVideoFormat() {
    List<MediaFile> videos = getMediaFiles(MediaFileType.GAME);
    if (videos.size() > 0) {
      MediaFile mediaFile = videos.get(0);
      return mediaFile.getVideoFormat();
    }

    return "";
  }

  /**
   * Gets the media info video codec (i.e. divx)
   * 
   * @return the media info video codec
   */
  public String getMediaInfoVideoCodec() {
    List<MediaFile> videos = getMediaFiles(MediaFileType.GAME);
    if (videos.size() > 0) {
      MediaFile mediaFile = videos.get(0);
      return mediaFile.getVideoCodec();
    }

    return "";
  }

  /**
   * Gets the media info audio codec (i.e mp3) and channels (i.e. 6 at 5.1 sound)
   * 
   * @return the media info audio codec
   */
  public String getMediaInfoAudioCodecAndChannels() {
    List<MediaFile> videos = getMediaFiles(MediaFileType.GAME);
    if (videos.size() > 0) {
      MediaFile mediaFile = videos.get(0);
      return mediaFile.getAudioCodec() + "_" + mediaFile.getAudioChannels();
    }

    return "";
  }

  public String getMediaInfoVideoResolution() {

    return "";
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String newValue) {
    String oldValue = this.platform;
    this.platform = newValue;
    firePropertyChange(PLATFORM, oldValue, newValue);
  }

  /**
   * Gets the images to cache.
   * 
   * @return the images to cache
   */
  public List<File> getImagesToCache() {
    // get files to cache
    List<File> filesToCache = new ArrayList<File>();
    for (MediaFile mf : new ArrayList<MediaFile>(getMediaFiles())) {
      if (mf.isGraphic()) {
        filesToCache.add(mf.getFile());
      }
    }

    return filesToCache;
  }

  public List<MediaFile> getMediaFilesContainingAudioStreams() {
    List<MediaFile> mediaFilesWithAudioStreams = new ArrayList<MediaFile>(1);

    // get the audio streams from the first video file
    List<MediaFile> videoFiles = getMediaFiles(MediaFileType.GAME);
    if (videoFiles.size() > 0) {
      MediaFile videoFile = videoFiles.get(0);
      mediaFilesWithAudioStreams.add(videoFile);
    }

    // get all extra audio streams
    for (MediaFile audioFile : getMediaFiles(MediaFileType.AUDIO)) {
      mediaFilesWithAudioStreams.add(audioFile);
    }

    return mediaFilesWithAudioStreams;
  }

  public List<MediaFile> getMediaFilesContainingSubtitles() {
    List<MediaFile> mediaFilesWithSubtitles = new ArrayList<MediaFile>(1);

    // look in the first media file if it has subtitles
    List<MediaFile> videoFiles = getMediaFiles(MediaFileType.GAME);
    if (videoFiles.size() > 0) {
      MediaFile videoFile = videoFiles.get(0);
      if (videoFile.hasSubtitles()) {
        mediaFilesWithSubtitles.add(videoFile);
      }
    }

    // look for all other types
    for (MediaFile mediaFile : getMediaFiles(MediaFileType.SUBTITLE)) {
      if (mediaFile.hasSubtitles()) {
        mediaFilesWithSubtitles.add(mediaFile);
      }
    }

    return mediaFilesWithSubtitles;
  }

  public int getRuntimeFromMediaFiles() {
    int runtime = 0;
    for (MediaFile mf : getMediaFiles(MediaFileType.GAME)) {
      runtime += mf.getDuration();
    }
    return runtime;
  }

  public int getRuntimeFromMediaFilesInMinutes() {
    return getRuntimeFromMediaFiles() / 60;
  }

  public Date getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(Date newValue) {
    Date oldValue = this.releaseDate;
    this.releaseDate = newValue;
    firePropertyChange(RELEASE_DATE, oldValue, newValue);
    firePropertyChange(RELEASE_DATE_AS_STRING, oldValue, newValue);
  }

  /**
   * release date as yyyy-mm-dd<br>
   * https://xkcd.com/1179/ :P
   */
  public String getReleaseDateFormatted() {
    if (this.releaseDate == null) {
      return "";
    }
    return new SimpleDateFormat("yyyy-MM-dd").format(this.releaseDate);
  }

  /**
   * Gets the first aired as a string, formatted in the system locale.
   */
  public String getReleaseDateAsString() {
    if (this.releaseDate == null) {
      return "";
    }
    return SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(releaseDate);
  }

  /**
   * convenient method to set the release date (parsed from string).
   */
  public void setReleaseDate(String dateAsString) throws ParseException {
    setReleaseDate(org.tinymediamanager.scraper.util.StrgUtils.parseDate(dateAsString));
  }

  @Override
  public synchronized void callbackForWrittenArtwork(MediaArtworkType type) {
    if (Globals.settings.getGameSettings().getGameConnector() != GameConnectors.XBMC) {
      writeNFO();
    }
  }

  public List<MediaFile> getVideoFiles() {
    return getMediaFiles(MediaFileType.GAME);
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String newValue) {
    String oldValue = this.publisher;
    this.publisher = newValue;
    firePropertyChange(PUBLISHER, oldValue, newValue);
  }
}

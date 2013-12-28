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
package org.tinymediamanager.core.movie;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.tinymediamanager.core.movie.connector.MovieConnectors;
import org.tinymediamanager.core.movie.connector.MovieToMpNfoConnector;
import org.tinymediamanager.core.movie.connector.MovieToXbmcNfoConnector;
import org.tinymediamanager.core.movie.tasks.MovieActorImageFetcher;
import org.tinymediamanager.core.movie.tasks.MovieExtraImageFetcher;
import org.tinymediamanager.scraper.Certification;
import org.tinymediamanager.scraper.MediaArtwork;
import org.tinymediamanager.scraper.MediaArtwork.MediaArtworkType;
import org.tinymediamanager.scraper.MediaCastMember;
import org.tinymediamanager.scraper.MediaGenres;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaScrapeOptions;
import org.tinymediamanager.scraper.MediaTrailer;
import org.tinymediamanager.scraper.tmdb.TmdbMetadataProvider;
import org.tinymediamanager.scraper.util.Url;
import org.tinymediamanager.scraper.util.UrlUtil;

import com.omertron.themoviedbapi.model.CollectionInfo;

/**
 * The main class for movies.
 * 
 * @author Manuel Laggner / Myron Boyle
 */
@Entity
@Inheritance(strategy = javax.persistence.InheritanceType.JOINED)
public class Movie extends MediaEntity {
  @XmlTransient
  private static final Logger LOGGER          = LoggerFactory.getLogger(Movie.class);

  private String              sortTitle       = "";
  private String              tagline         = "";
  private int                 votes           = 0;
  private int                 runtime         = 0;
  private String              director        = "";
  private String              writer          = "";
  private String              dataSource      = "";
  private boolean             watched         = false;
  private MovieSet            movieSet;
  private boolean             isDisc          = false;
  private String              spokenLanguages = "";
  private boolean             subtitles       = false;
  private String              country         = "";
  private Date                releaseDate     = null;
  private boolean             multiMovieDir   = false;                               // we detected more movies in same folder
  private int                 top250          = 0;

  private List<String>        genres          = new ArrayList<String>(0);
  private List<String>        tags            = new ArrayList<String>(0);
  private List<String>        extraThumbs     = new ArrayList<String>(0);
  private List<String>        extraFanarts    = new ArrayList<String>(0);

  @Enumerated(EnumType.STRING)
  private Certification       certification   = Certification.NOT_RATED;

  @OneToMany(cascade = CascadeType.ALL)
  private List<MovieActor>    actors          = new ArrayList<MovieActor>(0);

  @OneToMany(cascade = CascadeType.ALL)
  private List<MovieProducer> producers       = new ArrayList<MovieProducer>(0);

  @OneToMany(cascade = CascadeType.ALL)
  private List<MediaTrailer>  trailer         = new ArrayList<MediaTrailer>(0);

  @Transient
  private String              titleSortable   = "";

  @Transient
  private boolean             newlyAdded      = false;

  @Transient
  private List<MediaGenres>   genresForAccess = new ArrayList<MediaGenres>(0);

  static {
    mediaFileComparator = new MovieMediaFileComparator();
  }

  /**
   * Instantiates a new movie. To initialize the propertychangesupport after loading
   */
  public Movie() {
  }

  /**
   * checks if this movie has been scraped.<br>
   * On a fresh DB, just reading local files, everything is again "unscraped". <br>
   * detect minimum of filled values as "scraped"
   * 
   * @return isScraped
   */
  @Override
  public boolean isScraped() {
    if (!scraped) {
      if (!plot.isEmpty() && !(year.isEmpty() || year.equals("0")) && !(genres == null || genres.size() == 0)
          && !(actors == null || actors.size() == 0)) {
        return true;
      }
    }
    return scraped;
  }

  /**
   * Gets the sort title.
   * 
   * @return the sort title
   */
  public String getSortTitle() {
    return sortTitle;
  }

  /**
   * Sets the sort title.
   * 
   * @param newValue
   *          the new sort title
   */
  public void setSortTitle(String newValue) {
    String oldValue = this.sortTitle;
    this.sortTitle = newValue;
    firePropertyChange(SORT_TITLE, oldValue, newValue);
  }

  /**
   * Sets the sort title from movie set.
   */
  public void setSortTitleFromMovieSet() {
    if (movieSet != null) {
      int index = movieSet.getMovieIndex(this) + 1;
      setSortTitle(movieSet.getTitle() + String.format("%02d", index));
    }
  }

  /**
   * Returns the sortable variant of title<br>
   * eg "The Bourne Legacy" -> "Bourne Legacy, The".
   * 
   * @return the title in its sortable format
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

  /**
   * Gets the checks for nfo file.
   * 
   * @return the checks for nfo file
   */
  public Boolean getHasNfoFile() {
    List<MediaFile> mf = getMediaFiles(MediaFileType.NFO);
    if (mf != null && mf.size() > 0) {
      return true;
    }

    return false;
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
   * Gets the checks for trailer.
   * 
   * @return the checks for trailer
   */
  public Boolean getHasTrailer() {
    if (trailer != null && trailer.size() > 0) {
      return true;
    }
    return false;
  }

  /**
   * Gets the title for ui.
   * 
   * @return the title for ui
   */
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
  public void initializeAfterLoading() {
    super.initializeAfterLoading();

    // remove empty tag and null values
    Utils.removeEmptyStringsFromList(tags);
    Utils.removeEmptyStringsFromList(genres);

    // load genres
    for (String genre : new ArrayList<String>(genres)) {
      addGenre(MediaGenres.getGenre(genre));
    }
  }

  /**
   * Adds the actor.
   * 
   * @param obj
   *          the obj
   */
  public void addActor(MovieActor obj) {
    actors.add(obj);
    firePropertyChange(ACTORS, null, this.getActors());
  }

  /**
   * Gets the trailers.
   * 
   * @return the trailers
   */
  public List<MediaTrailer> getTrailers() {
    return this.trailer;
  }

  /**
   * Adds the trailer.
   * 
   * @param obj
   *          the obj
   */
  public void addTrailer(MediaTrailer obj) {
    trailer.add(obj);
    firePropertyChange(TRAILER, null, trailer);
  }

  /**
   * Removes the all trailers.
   */
  public void removeAllTrailers() {
    trailer.clear();
    firePropertyChange(TRAILER, null, trailer);
  }

  /**
   * Downloads trailer to movie folder (get from NFO), naming <code>&lt;movie&gt;-trailer.ext</code><br>
   * Downloads to .tmp file first and renames after successful download.
   * 
   * @param trailerToDownload
   *          the MediaTrailer object to download
   * @return true/false if successful
   */
  public Boolean downloadTtrailer(MediaTrailer trailerToDownload) {
    try {
      // get trailer filename from first mediafile
      String tfile = MovieRenamer.createDestinationForFilename(Globals.settings.getMovieSettings().getMovieRenamerFilename(), this) + "-trailer.";
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

  /**
   * Adds the to tags.
   * 
   * @param newTag
   *          the new tag
   */
  public void addToTags(String newTag) {
    if (StringUtils.isBlank(newTag)) {
      return;
    }

    for (String tag : tags) {
      if (tag.equals(newTag)) {
        return;
      }
    }

    tags.add(newTag);
    firePropertyChange(TAG, null, tags);
    firePropertyChange(TAGS_AS_STRING, null, newTag);
  }

  /**
   * Removes the from tags.
   * 
   * @param removeTag
   *          the remove tag
   */
  public void removeFromTags(String removeTag) {
    tags.remove(removeTag);
    firePropertyChange(TAG, null, tags);
    firePropertyChange(TAGS_AS_STRING, null, removeTag);
  }

  /**
   * Sets the tags.
   * 
   * @param newTags
   *          the new tags
   */
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

    Utils.removeEmptyStringsFromList(tags);

    firePropertyChange(TAG, null, tags);
    firePropertyChange(TAGS_AS_STRING, null, tags);
  }

  /**
   * Gets the tag as string.
   * 
   * @return the tag as string
   */
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

  /**
   * Gets the tags.
   * 
   * @return the tags
   */
  public List<String> getTags() {
    return this.tags;
  }

  /**
   * Gets the data source.
   * 
   * @return the data source
   */
  public String getDataSource() {
    return dataSource;
  }

  /**
   * Sets the data source.
   * 
   * @param newValue
   *          the new data source
   */
  public void setDataSource(String newValue) {
    String oldValue = this.dataSource;
    this.dataSource = newValue;
    firePropertyChange(DATA_SOURCE, oldValue, newValue);
  }

  /** has movie local (or any mediafile inline) subtitles? */
  public boolean hasSubtitles() {
    if (this.subtitles) {
      return true; // local ones found
    }

    for (MediaFile mf : getMediaFiles(MediaFileType.VIDEO)) {
      if (mf.hasSubtitles()) {
        return true;
      }
    }

    return false;
  }

  /** set subtitles */
  public void setSubtitles(boolean sub) {
    this.subtitles = sub;
  }

  /**
   * Find actor images.
   */
  public void findActorImages() {
    if (Globals.settings.getMovieSettings().isWriteActorImages()) {
      String actorsDirPath = getPath() + File.separator + MovieActor.ACTOR_DIR;
      // get all files from the actors path
      File[] actorImages = new File(actorsDirPath).listFiles();
      if (actorImages != null && actorImages.length > 0) {
        // search all local actor images
        for (MovieActor actor : getActors()) {
          if (StringUtils.isNotBlank(actor.getThumbPath())) {
            continue;
          }

          String actorName = actor.getName().replace(" ", "_");

          Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(actorName) + "\\.(tbn|jpg|png)");
          for (File file : actorImages) {
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.matches()) {
              actor.setThumbPath(file.getAbsolutePath());
            }
          }
        }
      }
    }
  }

  /**
   * Gets the actors.
   * 
   * @return the actors
   */
  public List<MovieActor> getActors() {
    return this.actors;
  }

  /**
   * Gets the director.
   * 
   * @return the director
   */
  public String getDirector() {
    return director;
  }

  /**
   * Gets the imdb id.
   * 
   * @return the imdb id
   */
  public String getImdbId() {
    Object obj = ids.get("imdbId");
    if (obj == null || !Utils.isValidImdbId(obj.toString())) {
      return "";
    }
    return obj.toString();
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
   * Gets the votes.
   * 
   * @return the votes
   */
  public int getVotes() {
    return votes;
  }

  /**
   * Sets the votes.
   * 
   * @param newValue
   *          the new votes
   */
  public void setVotes(int newValue) {
    int oldValue = this.votes;
    this.votes = newValue;
    firePropertyChange(VOTES, oldValue, newValue);
  }

  /**
   * Gets the runtime in minutes
   * 
   * @return the runtime
   */
  public int getRuntime() {
    if (Globals.settings.getMovieSettings().isRuntimeFromMediaInfo()) {
      return getRuntimeFromMediaFilesInMinutes();
    }
    return runtime == 0 ? getRuntimeFromMediaFilesInMinutes() : runtime;
  }

  /**
   * Gets the tagline.
   * 
   * @return the tagline
   */
  public String getTagline() {
    return tagline;
  }

  /**
   * Gets the writer.
   * 
   * @return the writer
   */
  public String getWriter() {
    return writer;
  }

  /**
   * Checks for file.
   * 
   * @param filename
   *          the filename
   * @return true, if successful
   */
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

  /**
   * Removes the actor.
   * 
   * @param obj
   *          the obj
   */
  public void removeActor(MovieActor obj) {
    actors.remove(obj);
    firePropertyChange(ACTORS, null, this.getActors());
  }

  /**
   * Gets the extra thumbs.
   * 
   * @return the extra thumbs
   */
  public List<String> getExtraThumbs() {
    return extraThumbs;
  }

  /**
   * Sets the extra thumbs.
   * 
   * @param extraThumbs
   *          the new extra thumbs
   */
  public void setExtraThumbs(List<String> extraThumbs) {
    this.extraThumbs = extraThumbs;
  }

  /**
   * Download extra thumbs.
   * 
   * @param thumbs
   *          the thumbs
   */
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
        if (Globals.settings.getMovieSettings().isImageExtraThumbsResize() && Globals.settings.getMovieSettings().getImageExtraThumbsSize() > 0) {
          file = new File(path, "thumb" + (i + 1) + ".jpg");
          outputStream = new FileOutputStream(file);
          try {
            is = ImageCache.scaleImage(url, Globals.settings.getMovieSettings().getImageExtraThumbsSize());
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

  /**
   * Gets the extra fanarts.
   * 
   * @return the extra fanarts
   */
  public List<String> getExtraFanarts() {
    return extraFanarts;
  }

  /**
   * Sets the extra fanarts.
   * 
   * @param extraFanarts
   *          the new extra fanarts
   */
  public void setExtraFanarts(List<String> extraFanarts) {
    this.extraFanarts = extraFanarts;
  }

  /**
   * Download extra thumbs.
   * 
   * @param fanarts
   *          the fanarts
   */
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

  /**
   * Write extra images.
   * 
   * @param extrathumbs
   *          the extrathumbs
   * @param extrafanart
   *          the extrafanart
   */
  public void writeExtraImages(boolean extrathumbs, boolean extrafanart) {
    // get images in thread
    MovieExtraImageFetcher task = new MovieExtraImageFetcher(this, extrafanart, extrathumbs);
    Globals.executor.execute(task);
  }

  /**
   * Sets the imdb id.
   * 
   * @param newValue
   *          the new imdb id
   */
  public void setImdbId(String newValue) {
    if (!Utils.isValidImdbId(newValue)) {
      newValue = "";
    }
    String oldValue = getImdbId();
    ids.put("imdbId", newValue);
    firePropertyChange("imdbId", oldValue, newValue);
  }

  /**
   * Sets the metadata.
   * 
   * @param metadata
   *          the new metadata
   * @param config
   *          the config
   */
  /**
   * @param metadata
   */
  public void setMetadata(MediaMetadata metadata, MovieScraperMetadataConfig config) {
    // check if metadata has at least a name
    if (StringUtils.isEmpty(metadata.getStringValue(MediaMetadata.TITLE))) {
      LOGGER.warn("wanted to save empty metadata for " + getTitle());
      return;
    }

    if (StringUtils.isNotBlank(metadata.getStringValue(MediaMetadata.IMDBID))) {
      setImdbId(metadata.getStringValue(MediaMetadata.IMDBID));
    }
    if (metadata.getIntegerValue(MediaMetadata.TMDBID) > 0) {
      setTmdbId(metadata.getIntegerValue(MediaMetadata.TMDBID));
    }

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
      setTop250(metadata.getIntegerValue(MediaMetadata.TOP_250));
    }

    if (config.isRuntime()) {
      setRuntime(metadata.getIntegerValue(MediaMetadata.RUNTIME));
    }

    setSpokenLanguages(metadata.getStringValue(MediaMetadata.SPOKEN_LANGUAGES));
    setCountry(metadata.getStringValue(MediaMetadata.COUNTRY));

    // certifications
    if (config.isCertification()) {
      if (metadata.getCertifications() != null && metadata.getCertifications().size() > 0) {
        setCertification(metadata.getCertifications().get(0));
      }
    }

    // cast
    if (config.isCast()) {
      setProductionCompany(metadata.getStringValue(MediaMetadata.PRODUCTION_COMPANY));
      List<MovieActor> actors = new ArrayList<MovieActor>();
      List<MovieProducer> producers = new ArrayList<MovieProducer>();
      String director = "";
      String writer = "";
      for (MediaCastMember member : metadata.getCastMembers()) {
        switch (member.getType()) {
          case ACTOR:
            MovieActor actor = new MovieActor();
            actor.setName(member.getName());
            actor.setCharacter(member.getCharacter());
            actor.setThumbUrl(member.getImageUrl());
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

          case PRODUCER:
            MovieProducer producer = new MovieProducer();
            producer.setName(member.getName());
            producer.setRole(member.getPart());
            producer.setThumbUrl(member.getImageUrl());
            producers.add(producer);
            break;

          default:
            break;
        }
      }
      setActors(actors);
      setDirector(director);
      setWriter(writer);
      setProducers(producers);
      writeActorImages();
    }

    // genres
    if (config.isGenres()) {
      setGenres(metadata.getGenres());
    }

    // set scraped
    setScraped(true);

    // update DB
    saveToDb();

    // create MovieSet
    if (config.isCollection()) {
      int col = metadata.getIntegerValue(MediaMetadata.TMDBID_SET);
      if (col != 0) {
        MovieSet movieSet = MovieList.getInstance().getMovieSet(metadata.getStringValue(MediaMetadata.COLLECTION_NAME),
            metadata.getIntegerValue(MediaMetadata.TMDBID));
        if (movieSet.getTmdbId() == 0) {
          movieSet.setTmdbId(col);
          // get movieset metadata
          try {
            TmdbMetadataProvider mp = new TmdbMetadataProvider();
            MediaScrapeOptions options = new MediaScrapeOptions();
            options.setTmdbId(col);
            options.setLanguage(Globals.settings.getMovieSettings().getScraperLanguage());
            options.setCountry(Globals.settings.getMovieSettings().getCertificationCountry());

            CollectionInfo info = mp.getMovieSetMetadata(options);
            if (info != null) {
              movieSet.setTitle(info.getName());
              movieSet.setPlot(info.getOverview());
              movieSet.setPosterUrl(info.getPosterPath());
              movieSet.setFanartUrl(info.getBackdropPath());
            }
          }
          catch (Exception e) {
          }
        }

        // add movie to movieset
        if (movieSet != null) {
          // first remove from "old" movieset
          setMovieSet(null);

          // add to new movieset
          // movieSet.addMovie(this);
          setMovieSet(movieSet);
          movieSet.insertMovie(this);
          movieSet.updateMovieSorttitle();
          // saveToDb();
        }
      }
    }

    // write NFO
    writeNFO();

  }

  /**
   * Sets the trailers; first one is "inNFO" if not a local one.
   * 
   * @param trailers
   *          the new trailers
   */
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

  /**
   * Gets the metadata.
   * 
   * @return the metadata
   */
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
  public void setArtwork(MediaMetadata md, MovieScraperMetadataConfig config) {
    setArtwork(md.getMediaArt(MediaArtworkType.ALL), config);
  }

  /**
   * Sets the artwork.
   * 
   * @param artwork
   *          the artwork
   * @param config
   *          the config
   */
  public void setArtwork(List<MediaArtwork> artwork, MovieScraperMetadataConfig config) {
    if (config.isArtwork()) {
      // poster
      boolean posterFound = false;
      for (MediaArtwork art : artwork) {
        // only get artwork in desired resolution
        if (art.getType() == MediaArtworkType.POSTER && art.getSizeOrder() == Globals.settings.getMovieSettings().getImagePosterSize().getOrder()) {
          setPosterUrl(art.getDefaultUrl());

          LOGGER.debug(art.getSmallestArtwork().toString());
          LOGGER.debug(art.getBiggestArtwork().toString());

          // did we get the tmdbid from artwork?
          if (getTmdbId() == 0 && art.getTmdbId() > 0) {
            setTmdbId(art.getTmdbId());
          }
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

            // did we get the tmdbid from artwork?
            if (getTmdbId() == 0 && art.getTmdbId() > 0) {
              setTmdbId(art.getTmdbId());
            }
            break;
          }
        }
      }

      // fanart
      boolean fanartFound = false;
      for (MediaArtwork art : artwork) {
        // only get artwork in desired resolution
        if (art.getType() == MediaArtworkType.BACKGROUND && art.getSizeOrder() == Globals.settings.getMovieSettings().getImageFanartSize().getOrder()) {
          setFanartUrl(art.getDefaultUrl());

          LOGGER.debug(art.getSmallestArtwork().toString());
          LOGGER.debug(art.getBiggestArtwork().toString());

          // did we get the tmdbid from artwork?
          if (getTmdbId() == 0 && art.getTmdbId() > 0) {
            setTmdbId(art.getTmdbId());
          }
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

            // did we get the tmdbid from artwork?
            if (getTmdbId() == 0 && art.getTmdbId() > 0) {
              setTmdbId(art.getTmdbId());
            }
            break;
          }
        }
      }

      if (!isMultiMovieDir()) {
        // extrathumbs
        List<String> extrathumbs = new ArrayList<String>();
        if (Globals.settings.getMovieSettings().isImageExtraThumbs() && Globals.settings.getMovieSettings().getImageExtraThumbsCount() > 0) {
          for (MediaArtwork art : artwork) {
            // only get artwork in desired resolution
            if (art.getType() == MediaArtworkType.BACKGROUND
                && art.getSizeOrder() == Globals.settings.getMovieSettings().getImageFanartSize().getOrder()) {
              extrathumbs.add(art.getDefaultUrl());
              if (extrathumbs.size() >= Globals.settings.getMovieSettings().getImageExtraThumbsCount()) {
                break;
              }
            }
          }
          setExtraThumbs(extrathumbs);
        }

        // extrafanarts
        List<String> extrafanarts = new ArrayList<String>();
        if (Globals.settings.getMovieSettings().isImageExtraFanart() && Globals.settings.getMovieSettings().getImageExtraFanartCount() > 0) {
          for (MediaArtwork art : artwork) {
            // only get artwork in desired resolution
            if (art.getType() == MediaArtworkType.BACKGROUND
                && art.getSizeOrder() == Globals.settings.getMovieSettings().getImageFanartSize().getOrder()) {
              extrafanarts.add(art.getDefaultUrl());
              if (extrafanarts.size() >= Globals.settings.getMovieSettings().getImageExtraFanartCount()) {
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
  public void setActors(List<MovieActor> newActors) {
    // two way sync of actors

    // first remove unused
    for (int i = actors.size() - 1; i >= 0; i--) {
      MovieActor actor = actors.get(i);
      if (!newActors.contains(actor)) {
        actors.remove(actor);
      }
    }

    // second add the new ones
    for (int i = 0; i < newActors.size(); i++) {
      MovieActor actor = newActors.get(i);
      if (!actors.contains(actor)) {
        actors.add(i, actor);
      }
      else {
        int indexOldList = actors.indexOf(actor);
        if (i != indexOldList) {
          MovieActor oldActor = actors.remove(indexOldList);
          actors.add(i, oldActor);
        }
      }
    }

    firePropertyChange(ACTORS, null, this.getActors());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.core.MediaEntity#setTitle(java.lang.String)
   */
  @Override
  public void setTitle(String newValue) {
    String oldValue = this.title;
    super.setTitle(newValue);

    firePropertyChange(TITLE_FOR_UI, oldValue, newValue);

    oldValue = this.titleSortable;
    titleSortable = "";
    firePropertyChange(TITLE_SORTABLE, oldValue, titleSortable);
  }

  /**
   * Sets the runtime in minutes
   * 
   * @param newValue
   *          the new runtime
   */
  public void setRuntime(int newValue) {
    int oldValue = this.runtime;
    this.runtime = newValue;
    firePropertyChange(RUNTIME, oldValue, newValue);
  }

  /**
   * Sets the tagline.
   * 
   * @param newValue
   *          the new tagline
   */
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
  public String getPosterFilename(MoviePosterNaming poster) {
    List<MediaFile> mfs = getMediaFiles(MediaFileType.VIDEO);
    if (mfs != null && mfs.size() > 0) {
      return getPosterFilename(poster, mfs.get(0).getFilename());
    }
    else {
      return getPosterFilename(poster, ""); // no video files
    }
  }

  public String getPosterFilename(MoviePosterNaming poster, String newMovieFilename) {
    String filename = "";
    String mediafile = Utils.cleanStackingMarkers(FilenameUtils.getBaseName(newMovieFilename));

    switch (poster) {
      case MOVIENAME_POSTER_PNG:
        filename += getTitle() + ".png";
        break;
      case MOVIENAME_POSTER_JPG:
        filename += getTitle() + ".jpg";
        break;
      case MOVIENAME_POSTER_TBN:
        filename += getTitle() + ".tbn";
        break;
      case FILENAME_POSTER_PNG:
        filename += mediafile.isEmpty() ? "" : mediafile + "-poster.png";
        break;
      case FILENAME_POSTER_JPG:
        filename += mediafile.isEmpty() ? "" : mediafile + "-poster.jpg";
        break;
      case FILENAME_POSTER_TBN:
        filename += mediafile.isEmpty() ? "" : mediafile + "-poster.tbn";
        break;
      case FILENAME_PNG:
        filename += mediafile.isEmpty() ? "" : mediafile + ".png";
        break;
      case FILENAME_JPG:
        filename += mediafile.isEmpty() ? "" : mediafile + ".jpg";
        break;
      case FILENAME_TBN:
        filename += mediafile.isEmpty() ? "" : mediafile + ".tbn";
        break;
      case MOVIE_PNG:
        filename += "movie.png";
        break;
      case MOVIE_JPG:
        filename += "movie.jpg";
        break;
      case MOVIE_TBN:
        filename += "movie.tbn";
        break;
      case POSTER_PNG:
        filename += "poster.png";
        break;
      case POSTER_JPG:
        filename += "poster.jpg";
        break;
      case POSTER_TBN:
        filename += "poster.tbn";
        break;
      case FOLDER_PNG:
        filename += "folder.png";
        break;
      case FOLDER_JPG:
        filename += "folder.jpg";
        break;
      case FOLDER_TBN:
        filename += "folder.tbn";
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
  public String getFanartFilename(MovieFanartNaming fanart) {
    List<MediaFile> mfs = getMediaFiles(MediaFileType.VIDEO);
    if (mfs != null && mfs.size() > 0) {
      return getFanartFilename(fanart, mfs.get(0).getFilename());
    }
    else {
      return getFanartFilename(fanart, ""); // no video files
    }
  }

  public String getFanartFilename(MovieFanartNaming fanart, String newMovieFilename) {
    String filename = "";
    String mediafile = Utils.cleanStackingMarkers(FilenameUtils.getBaseName(newMovieFilename));

    switch (fanart) {
      case FANART_PNG:
        filename += "fanart.png";
        break;
      case FANART_JPG:
        filename += "fanart.jpg";
        break;
      case FANART_TBN:
        filename += "fanart.tbn";
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
      case FILENAME_FANART_TBN:
        filename += mediafile.isEmpty() ? "" : mediafile + "-fanart.tbn";
        break;
      case MOVIENAME_FANART_PNG:
        filename += getTitle() + "-fanart.png";
        break;
      case MOVIENAME_FANART_JPG:
        filename += getTitle() + "-fanart.jpg";
        break;
      case MOVIENAME_FANART_TBN:
        filename += getTitle() + "-fanart.tbn";
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
  public String getNfoFilename(MovieNfoNaming nfo) {
    List<MediaFile> mfs = getMediaFiles(MediaFileType.VIDEO);
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
   * @param newMovieFilename
   *          the new/desired movie filename
   * @return the nfo filename
   */
  public String getNfoFilename(MovieNfoNaming nfo, String newMovieFilename) {
    if (isDisc) {
      // detect if this directory is a DVD or BR directory
      File dir = new File(path, "VIDEO_TS");
      if (dir.exists()) {
        // we need to pass the subdir in this case
        // info for that file naming: http://wiki.xbmc.org/index.php?title=NFO_files/movies
        return "VIDEO_TS" + File.separator + "VIDEO_TS.nfo";
      }

      dir = new File(path, "BDMV");
      if (dir.exists()) {
        // we need to pass the subdir in this case
        // info for that file naming: http://forum.xbmc.org/showthread.php?tid=155523
        return "BDMV" + File.separator + "index.nfo";
      }
    }

    String filename = "";
    switch (nfo) {
      case FILENAME_NFO:
        String movieFilename = FilenameUtils.getBaseName(newMovieFilename);
        filename += movieFilename.isEmpty() ? "" : Utils.cleanStackingMarkers(movieFilename) + ".nfo"; // w/o stacking information
        break;
      case MOVIE_NFO:
        filename += "movie.nfo";
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
      List<MoviePosterNaming> posternames = new ArrayList<MoviePosterNaming>();
      if (isMultiMovieDir()) {
        // Fixate the name regardless of setting
        posternames.add(MoviePosterNaming.FILENAME_POSTER_JPG);
        posternames.add(MoviePosterNaming.FILENAME_POSTER_PNG);
      }
      else if (isDisc()) {
        // override poster naming for disc files
        posternames.add(MoviePosterNaming.POSTER_JPG);
        posternames.add(MoviePosterNaming.POSTER_PNG);
      }
      else {
        posternames = Globals.settings.getMovieSettings().getMoviePosterFilenames();
      }
      for (MoviePosterNaming name : posternames) {
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
      List<MovieFanartNaming> fanartnames = new ArrayList<MovieFanartNaming>();
      if (isMultiMovieDir()) {
        // Fixate the name regardless of setting
        fanartnames.add(MovieFanartNaming.FILENAME_FANART_JPG);
        fanartnames.add(MovieFanartNaming.FILENAME_FANART_PNG);
      }
      else if (isDisc()) {
        // override fanart naming for disc files
        fanartnames.add(MovieFanartNaming.FANART_JPG);
        fanartnames.add(MovieFanartNaming.FANART_PNG);
      }
      else {
        fanartnames = Globals.settings.getMovieSettings().getMovieFanartFilenames();
      }
      for (MovieFanartNaming name : fanartnames) {
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
    if (!Globals.settings.getMovieSettings().isWriteActorImages() || isMultiMovieDir()) {
      return;
    }

    MovieActorImageFetcher task = new MovieActorImageFetcher(this);
    Globals.executor.execute(task);
  }

  /**
   * Write nfo.
   */
  public void writeNFO() {
    if (Globals.settings.getMovieSettings().getMovieConnector() == MovieConnectors.MP) {
      MovieToMpNfoConnector.setData(this);
    }
    else {
      MovieToXbmcNfoConnector.setData(this);
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
  public List<MediaGenres> getGenres() {
    return genresForAccess;
  }

  /**
   * Adds the genre.
   * 
   * @param newValue
   *          the new value
   */
  public void addGenre(MediaGenres newValue) {
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
  public void setGenres(List<MediaGenres> genres) {
    // two way sync of genres

    // first, add new ones
    for (MediaGenres genre : genres) {
      if (!this.genresForAccess.contains(genre)) {
        this.genresForAccess.add(genre);
        if (!genres.contains(genre.name())) {
          this.genres.add(genre.name());
        }
      }
    }

    // second remove old ones
    for (int i = this.genresForAccess.size() - 1; i >= 0; i--) {
      MediaGenres genre = this.genresForAccess.get(i);
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
  public void removeGenre(MediaGenres genre) {
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
    for (MediaGenres genre : genresForAccess) {
      if (!StringUtils.isEmpty(sb)) {
        sb.append(", ");
      }
      sb.append(genre != null ? genre.getLocalizedName() : "null");
    }
    return sb.toString();
  }

  /**
   * Checks if is watched.
   * 
   * @return true, if is watched
   */
  public boolean isWatched() {
    return watched;
  }

  /**
   * Sets the watched.
   * 
   * @param newValue
   *          the new watched
   */
  public void setWatched(boolean newValue) {
    boolean oldValue = this.watched;
    this.watched = newValue;
    firePropertyChange(WATCHED, oldValue, newValue);
  }

  /**
   * Checks if this movie is in a folder with other movies and not in an own folder<br>
   * so disable everything except renaming
   * 
   * @return true, if in datasource root
   */
  public boolean isMultiMovieDir() {
    return multiMovieDir;
  }

  /**
   * Sets the flag, that the movie is not in an own folder<br>
   * so disable everything except renaming
   * 
   * @param multiDir
   *          true/false
   */
  public void setMultiMovieDir(boolean multiDir) {
    this.multiMovieDir = multiDir;
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
   * Gets the movie set.
   * 
   * @return the movieset
   */
  public MovieSet getMovieSet() {
    return movieSet;
  }

  /**
   * Sets the movie set.
   * 
   * @param newValue
   *          the new movie set
   */
  public void setMovieSet(MovieSet newValue) {
    MovieSet oldValue = this.movieSet;
    this.movieSet = newValue;

    // remove movieset-sorttitle
    if (oldValue != null && newValue == null) {
      setSortTitle("");
    }

    firePropertyChange(MOVIESET, oldValue, newValue);
    firePropertyChange(MOVIESET_TITLE, oldValue, newValue);
  }

  public void movieSetTitleChanged() {
    firePropertyChange(MOVIESET_TITLE, null, "");
  }

  public String getMovieSetTitle() {
    if (movieSet != null) {
      return movieSet.getTitle();
    }
    return "";
  }

  /**
   * Removes the from movie set.
   */
  public void removeFromMovieSet() {
    if (movieSet != null) {
      movieSet.removeMovie(this);
    }
    setMovieSet(null);
  }

  /**
   * is this a disc movie folder (video_ts / bdmv)?.
   * 
   * @return true, if is disc
   */
  public boolean isDisc() {
    return isDisc;
  }

  /**
   * is this a disc movie folder (video_ts / bdmv)?.
   * 
   * @param isDisc
   *          the new disc
   */
  public void setDisc(boolean isDisc) {
    this.isDisc = isDisc;
  }

  /**
   * has this movie been newlay added in our list?!
   * 
   * @return true/false
   */
  public boolean isNewlyAdded() {
    return this.newlyAdded;
  }

  /**
   * has this movie been newlay added in our list?!
   * 
   * @param newlyAdded
   *          true/false
   */
  public void setNewlyAdded(boolean newlyAdded) {
    this.newlyAdded = newlyAdded;
  }

  /**
   * Gets the media info video format (i.e. 720p).
   */
  public String getMediaInfoVideoFormat() {
    List<MediaFile> videos = getMediaFiles(MediaFileType.VIDEO);
    if (videos.size() > 0) {
      MediaFile mediaFile = videos.get(0);
      return mediaFile.getVideoFormat();
    }

    return "";
  }

  /**
   * Gets the media info video codec (i.e. divx)
   */
  public String getMediaInfoVideoCodec() {
    List<MediaFile> videos = getMediaFiles(MediaFileType.VIDEO);
    if (videos.size() > 0) {
      MediaFile mediaFile = videos.get(0);
      return mediaFile.getVideoCodec();
    }

    return "";
  }

  /**
   * Gets the media info audio codec (i.e mp3) and channels (i.e. 6 at 5.1 sound)
   */
  public String getMediaInfoAudioCodecAndChannels() {
    List<MediaFile> videos = getMediaFiles(MediaFileType.VIDEO);
    if (videos.size() > 0) {
      MediaFile mediaFile = videos.get(0);
      return mediaFile.getAudioCodec() + "_" + mediaFile.getAudioChannels();
    }

    return "";
  }

  public String getMediaInfoVideoResolution() {

    return "";
  }

  public void setSpokenLanguages(String newValue) {
    String oldValue = this.spokenLanguages;
    this.spokenLanguages = newValue;
    firePropertyChange(SPOKEN_LANGUAGES, oldValue, newValue);
  }

  public String getSpokenLanguages() {
    return this.spokenLanguages;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String newValue) {
    String oldValue = this.country;
    this.country = newValue;
    firePropertyChange(COUNTRY, oldValue, newValue);
  }

  /**
   * Gets the images to cache.
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
    List<MediaFile> videoFiles = getMediaFiles(MediaFileType.VIDEO);
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
    List<MediaFile> videoFiles = getMediaFiles(MediaFileType.VIDEO);
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
    for (MediaFile mf : getMediaFiles(MediaFileType.VIDEO)) {
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
    if (Globals.settings.getMovieSettings().getMovieConnector() == MovieConnectors.MP) {
      writeNFO();
    }
  }

  public List<MediaFile> getVideoFiles() {
    return getMediaFiles(MediaFileType.VIDEO);
  }

  public int getTop250() {
    return top250;
  }

  public void setTop250(int newValue) {
    int oldValue = this.top250;
    this.top250 = newValue;
    firePropertyChange(TOP250, oldValue, newValue);
  }

  public void addProducer(MovieProducer obj) {
    producers.add(obj);
    firePropertyChange(PRODUCERS, null, producers);

  }

  public void removeProducer(MovieProducer obj) {
    producers.remove(obj);
    firePropertyChange(PRODUCERS, null, producers);
  }

  public void setProducers(List<MovieProducer> newProducers) {
    // two way sync of producers

    // first remove unused
    for (int i = producers.size() - 1; i >= 0; i--) {
      MovieProducer producer = producers.get(i);
      if (!newProducers.contains(producer)) {
        producers.remove(producer);
      }
    }

    // second add the new ones
    for (int i = 0; i < newProducers.size(); i++) {
      MovieProducer producer = newProducers.get(i);
      if (!producers.contains(producer)) {
        // new producer
        producers.add(i, producer);
      }
      else {
        int indexOldList = producers.indexOf(producer);
        if (i != indexOldList) {
          MovieProducer oldProducer = producers.remove(indexOldList);
          producers.add(i, oldProducer);
        }
      }
    }

    firePropertyChange(PRODUCERS, null, producers);
  }

  public List<MovieProducer> getProducers() {
    return this.producers;
  }
}
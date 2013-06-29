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
package org.tinymediamanager.core.tvshow;

import static org.tinymediamanager.core.Constants.*;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.observablecollections.ObservableCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.MediaEntity;
import org.tinymediamanager.core.MediaEntityImageFetcherTask;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.tvshow.connector.TvShowEpisodeToXbmcNfoConnector;
import org.tinymediamanager.scraper.MediaArtwork;
import org.tinymediamanager.scraper.MediaArtwork.MediaArtworkType;
import org.tinymediamanager.scraper.MediaCastMember;
import org.tinymediamanager.scraper.MediaMetadata;

/**
 * The Class TvShowEpisode.
 * 
 * @author Manuel Laggner
 */
@Entity
@Inheritance(strategy = javax.persistence.InheritanceType.JOINED)
public class TvShowEpisode extends MediaEntity implements Comparable<TvShowEpisode> {

  /** The Constant LOGGER. */
  private static final Logger LOGGER            = LoggerFactory.getLogger(TvShowEpisode.class);

  /** The tv show. */
  private TvShow              tvShow            = null;

  /** The episode. */
  private int                 episode           = 0;

  /** The season. */
  private int                 season            = -1;

  /** the first aired date. */
  private Date                firstAired        = null;

  /** The director. */
  private String              director          = "";

  /** The writer. */
  private String              writer            = "";

  /** is this episode in a disc folder structure?. */
  private boolean             disc              = false;

  /** The nfo filename. */
  private String              nfoFilename       = "";

  /** The watched. */
  private boolean             watched           = false;

  /** The votes. */
  private int                 votes             = 0;

  private boolean             subtitles         = false;

  /** The actors. */
  @OneToMany(cascade = CascadeType.ALL)
  private List<TvShowActor>   actors            = new ArrayList<TvShowActor>();

  /** The actors observables. */
  @Transient
  private List<TvShowActor>   actorsObservables = ObservableCollections.observableList(actors);

  /**
   * Instantiates a new tv show episode. To initialize the propertychangesupport after loading
   */
  public TvShowEpisode() {
  }

  /**
   * first aired date.
   * 
   * @return the date
   */
  public Date getFirstAired() {
    return firstAired;
  }

  /**
   * sets the first aired date.
   * 
   * @param newValue
   *          the new first aired
   */
  public void setFirstAired(Date newValue) {
    Date oldValue = this.firstAired;
    this.firstAired = newValue;
    firePropertyChange(FIRST_AIRED, oldValue, newValue);
    firePropertyChange(FIRST_AIRED_AS_STRING, oldValue, newValue);
  }

  /**
   * Gets the tv show season.
   * 
   * @return the tv show season
   */
  public TvShowSeason getTvShowSeason() {
    if (tvShow == null) {
      return null;
    }
    return tvShow.getSeasonForEpisode(this);
  }

  /**
   * Sets the tv show season.
   */
  public void setTvShowSeason() {
    // dummy for beansbinding
  }

  /**
   * Is this episode in a disc folder structure?.
   * 
   * @return true/false
   */
  public boolean isDisc() {
    return disc;
  }

  /**
   * This episode is in a disc folder structure.
   * 
   * @param disc
   *          true/false
   */
  public void setDisc(boolean disc) {
    this.disc = disc;
  }

  /**
   * first aired date as yyyy-mm-dd<br>
   * https://xkcd.com/1179/ :P
   * 
   * @return the date or empty string
   */
  public String getFirstAiredFormatted() {
    if (this.firstAired == null) {
      return "";
    }
    return new SimpleDateFormat("yyyy-MM-dd").format(this.firstAired);
  }

  /**
   * Gets the first aired as a string, formatted in the system locale.
   * 
   * @return the first aired as string
   */
  public String getFirstAiredAsString() {
    if (this.firstAired == null) {
      return "";
    }
    return SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(firstAired);
  }

  /**
   * convenient method to set the first aired date (parsed from string).
   * 
   * @param aired
   *          the new first aired
   * @throws ParseException
   *           if string cannot be parsed!
   */
  public void setFirstAired(String aired) throws ParseException {
    Pattern date = Pattern.compile("([0-9]{2})[_\\.-]([0-9]{2})[_\\.-]([0-9]{4})");
    Matcher m = date.matcher(aired);
    if (m.find()) {
      this.firstAired = new SimpleDateFormat("dd-MM-yyyy").parse(m.group(1) + "-" + m.group(2) + "-" + m.group(3));
    }
    else {
      date = Pattern.compile("([0-9]{4})[_\\.-]([0-9]{2})[_\\.-]([0-9]{2})");
      m = date.matcher(aired);
      if (m.find()) {
        this.firstAired = new SimpleDateFormat("yyyy-MM-dd").parse(m.group(1) + "-" + m.group(2) + "-" + m.group(3));
      }
      else {
        throw new ParseException("could not parse date from: " + aired, 0);
      }
    }
  }

  /**
   * Gets the tv show.
   * 
   * @return the tv show
   */
  public TvShow getTvShow() {
    return tvShow;
  }

  /**
   * Sets the tv show.
   * 
   * @param newValue
   *          the new tv show
   */
  public void setTvShow(TvShow newValue) {
    TvShow oldValue = this.tvShow;
    this.tvShow = newValue;
    firePropertyChange(TV_SHOW, oldValue, newValue);
  }

  /**
   * Gets the episode.
   * 
   * @return the episode
   */
  public int getEpisode() {
    return episode;
  }

  /**
   * Gets the season.
   * 
   * @return the season
   */
  public int getSeason() {
    return season;
  }

  /**
   * Sets the episode.
   * 
   * @param newValue
   *          the new episode
   */
  public void setEpisode(int newValue) {
    int oldValue = this.episode;
    this.episode = newValue;
    firePropertyChange(EPISODE, oldValue, newValue);
    firePropertyChange(TITLE_FOR_UI, "", newValue);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.core.MediaEntity#setTitle(java.lang.String)
   */
  @Override
  public void setTitle(String newValue) {
    super.setTitle(newValue);
    firePropertyChange(TITLE_FOR_UI, "", newValue);
  }

  /**
   * Sets the season.
   * 
   * @param newValue
   *          the new season
   */
  public void setSeason(int newValue) {
    int oldValue = this.season;
    this.season = newValue;
    firePropertyChange(SEASON, oldValue, newValue);
    firePropertyChange(TITLE_FOR_UI, "", newValue);
  }

  /**
   * Gets the title for ui.
   * 
   * @return the title for ui
   */
  public String getTitleForUi() {
    StringBuffer titleForUi = new StringBuffer();
    if (episode > 0 && season > 0) {
      titleForUi.append(String.format("S%02dE%02d - ", season, episode));
    }
    titleForUi.append(title);
    return titleForUi.toString();
  }

  /**
   * Initialize after loading.
   */
  public void initializeAfterLoading() {
    super.initializeAfterLoading();

    actorsObservables = ObservableCollections.observableList(actors);
  }

  /**
   * Write thumb image.
   */
  public void writeThumbImage() {
    if (StringUtils.isNotEmpty(getThumbUrl())) {
      boolean firstImage = true;
      // create correct filename
      MediaFile mf = getMediaFiles().get(0);
      String filename = FilenameUtils.getBaseName(mf.getFilename()) + "-thumb." + FilenameUtils.getExtension(getThumbUrl());
      // get image in thread
      MediaEntityImageFetcherTask task = new MediaEntityImageFetcherTask(this, getThumbUrl(), MediaArtworkType.THUMB, filename, firstImage);
      Globals.executor.execute(task);
    }
  }

  /**
   * Sets the metadata.
   * 
   * @param metadata
   *          the new metadata
   */
  public void setMetadata(MediaMetadata metadata) {

    setTitle(metadata.getTitle());
    setPlot(metadata.getPlot());
    setIds(metadata.getIds());

    try {
      setFirstAired(metadata.getFirstAired());
    }
    catch (ParseException e) {
      LOGGER.warn(e.getMessage());
    }

    setRating((float) metadata.getRating());

    List<TvShowActor> actors = new ArrayList<TvShowActor>();
    String director = "";
    String writer = "";
    for (MediaCastMember member : metadata.getCastMembers()) {
      switch (member.getType()) {
        case ACTOR:
          TvShowActor actor = new TvShowActor();
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

    for (MediaArtwork ma : metadata.getFanart()) {
      if (ma.getType() == MediaArtworkType.THUMB) {
        setThumbUrl(ma.getDefaultUrl());
        writeThumbImage();
        break;
      }
    }

    // write NFO
    writeNFO();

    // update DB
    saveToDb();
  }

  /**
   * Write nfo.
   */
  public void writeNFO() {
    List<TvShowEpisode> episodesInNfo = new ArrayList<TvShowEpisode>(1);

    // worst case: multi episode in multiple files
    // e.g. warehouse13.s01e01e02.Part1.avi/warehouse13.s01e01e02.Part2.avi
    for (MediaFile mf : getMediaFiles(MediaFileType.VIDEO)) {
      episodesInNfo.addAll(TvShowList.getInstance().getTvEpisodesByFile(mf.getFile()));
    }

    String nfoFilename = TvShowEpisodeToXbmcNfoConnector.setData(episodesInNfo);
    for (TvShowEpisode episode : episodesInNfo) {
      episode.setNfoFilename(nfoFilename);
      episode.saveToDb();
    }
  }

  /**
   * Gets the checks for nfo file.
   * 
   * @return the checks for nfo file
   */
  public Boolean getHasNfoFile() {
    if (!StringUtils.isEmpty(nfoFilename)) {
      return true;
    }
    return false;
  }

  /**
   * Sets the nfo filename.
   * 
   * @param newValue
   *          the new nfo filename
   */
  public void setNfoFilename(String newValue) {
    String oldValue = this.nfoFilename;
    this.nfoFilename = newValue;
    setNFO(new File(path, nfoFilename));
    firePropertyChange(NFO_FILENAME, oldValue, newValue);
    firePropertyChange(HAS_NFO_FILE, false, true);
  }

  private void setNFO(File file) {
    List<MediaFile> nfos = getMediaFiles(MediaFileType.NFO);
    MediaFile mediaFile = null;
    if (nfos.size() > 0) {
      mediaFile = nfos.get(0);
      mediaFile.setFile(file);
    }
    else {
      mediaFile = new MediaFile(file, MediaFileType.NFO);
      addToMediaFiles(mediaFile);
    }
  }

  /**
   * Gets the checks for images.
   * 
   * @return the checks for images
   */
  public Boolean getHasImages() {
    if (StringUtils.isNotEmpty(getThumb())) {
      return true;
    }
    return false;
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
   * Gets the director.
   * 
   * @return the director
   */
  public String getDirector() {
    return director;
  }

  /**
   * Adds the actor.
   * 
   * @param obj
   *          the obj
   */
  public void addActor(TvShowActor obj) {
    actorsObservables.add(obj);
    firePropertyChange(ACTORS, null, this.getActors());
  }

  /**
   * Gets the actors.
   * 
   * @return the actors
   */
  public List<TvShowActor> getActors() {
    List<TvShowActor> allActors = new ArrayList<TvShowActor>();
    if (tvShow != null) {
      allActors.addAll(tvShow.getActors());
    }
    allActors.addAll(actorsObservables);
    return allActors;
  }

  /**
   * Removes the actor.
   * 
   * @param obj
   *          the obj
   */
  public void removeActor(TvShowActor obj) {
    actorsObservables.remove(obj);
    firePropertyChange(ACTORS, null, this.getActors());
  }

  /**
   * Sets the actors.
   * 
   * @param newActors
   *          the new actors
   */
  public void setActors(List<TvShowActor> newActors) {
    // two way sync of actors

    // first add the new ones
    for (TvShowActor actor : newActors) {
      if (!actorsObservables.contains(actor)) {
        actorsObservables.add(actor);
      }
    }

    // second remove unused
    for (int i = actorsObservables.size() - 1; i >= 0; i--) {
      TvShowActor actor = actorsObservables.get(i);
      if (!newActors.contains(actor)) {
        actorsObservables.remove(actor);
      }
    }

    firePropertyChange(ACTORS, null, this.getActors());
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
   * Parses the nfo.
   * 
   * @param episodeFile
   *          the episode file
   * @return the list
   */
  public static List<TvShowEpisode> parseNFO(File episodeFile) {
    List<TvShowEpisode> episodes = new ArrayList<TvShowEpisode>(1);

    String filename = episodeFile.getParent() + File.separator + FilenameUtils.getBaseName(episodeFile.getName()) + ".nfo";
    episodes.addAll(TvShowEpisodeToXbmcNfoConnector.getData(filename));

    return episodes;
  }

  /**
   * Find images.
   */
  public void findImages() {
    // find thumb
    findThumb();
  }

  /**
   * Find thumb.
   */
  private void findThumb() {
    boolean found = false;
    // there are 2 possible filenames for thumbs

    // a) episodename-thumb.jpg/png (as described in the xbmc wiki http://wiki.xbmc.org/index.php?title=Frodo_FAQ#Local_images)
    Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(getTitle()) + "-thumb\\..{2,4}");
    File[] files = new File(path).listFiles();
    for (File file : files) {
      Matcher matcher = pattern.matcher(file.getName());
      if (matcher.matches()) {
        setFanart(file);
        LOGGER.debug("found thumb " + file.getPath());
        found = true;
        break;
      }
    }

    // b) filename-thumb/fanart.jpg/png
    if (!found) {
      String mediafile = "";
      try {
        mediafile = FilenameUtils.getBaseName(getMediaFiles(MediaFileType.VIDEO).get(0).getFilename());
      }
      catch (Exception e) {
        System.out.println(path);
      }
      pattern = Pattern.compile("(?i)" + Pattern.quote(mediafile) + "-(thumb|fanart)\\..{2,4}");
      for (File file : files) {
        Matcher matcher = pattern.matcher(file.getName());
        if (matcher.matches()) {
          setFanart(file);
          LOGGER.debug("found thumb " + file.getPath());
          found = true;
          break;
        }
      }
    }

    // if we did not find anything, try to download it
    if (!found && StringUtils.isNotEmpty(thumbUrl)) {
      writeThumbImage();
      found = true;
      LOGGER.debug("got thumb url: " + thumbUrl + " ; try to download this");
    }

    if (!found) {
      LOGGER.debug("Sorry, could not find a thumb.");
    }
  }

  /**
   * Gets the media info video format (i.e. 720p).
   * 
   * @return the media info video format
   */
  public String getMediaInfoVideoFormat() {
    if (mediaFiles.size() > 0) {
      MediaFile mediaFile = mediaFiles.get(0);
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
    if (mediaFiles.size() > 0) {
      MediaFile mediaFile = mediaFiles.get(0);
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
    if (mediaFiles.size() > 0) {
      MediaFile mediaFile = mediaFiles.get(0);
      return mediaFile.getAudioCodec() + "_" + mediaFile.getAudioChannels();
    }

    return "";
  }

  /**
   * Gets the vote count.
   * 
   * @return the vote count
   */
  public int getVoteCount() {
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
   * Gets the images to cache.
   * 
   * @return the images to cache
   */
  public List<File> getImagesToCache() {
    // get files to cache
    List<File> filesToCache = new ArrayList<File>();

    if (StringUtils.isNotBlank(getFanart())) {
      filesToCache.add(new File(getFanart()));
    }

    return filesToCache;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(TvShowEpisode otherTvShowEpisode) {
    if (getTvShow() != otherTvShowEpisode.getTvShow()) {
      return getTvShow().getTitle().compareTo(otherTvShowEpisode.getTvShow().getTitle());
    }

    if (getSeason() != otherTvShowEpisode.getSeason()) {
      return getSeason() - otherTvShowEpisode.getSeason();
    }

    return getEpisode() - otherTvShowEpisode.getEpisode();
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

  public void setSubtitles(boolean sub) {
    this.subtitles = sub;
  }
}
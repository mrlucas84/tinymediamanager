/*
 * Copyright 2012 Manuel Laggner
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
package org.tinymediamanager.core;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Scanner;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.tinymediamanager.Globals;
import org.tinymediamanager.thirdparty.MediaInfo;
import org.tinymediamanager.thirdparty.MediaInfo.StreamKind;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaFile.
 * 
 * @author manuel
 */
@Entity
public class MediaFile extends AbstractModelObject {

  /** The Constant LOGGER. */
  private static final Logger LOGGER           = Logger.getLogger(MediaFile.class);

  /** The Constant PATH. */
  private static final String PATH             = "path";

  /** The Constant FILENAME. */
  private static final String FILENAME         = "filename";

  /** The Constant FILESIZE. */
  private static final String FILESIZE         = "filesize";

  /** The Constant FILESIZE_IN_MB. */
  private static final String FILESIZE_IN_MB   = "filesizeInMegabytes";

  /** The path. */
  private String              path;

  /** The filename. */
  private String              filename;

  /** The filesize. */
  private long                filesize;

  /** The video codec. */
  private String              videoCodec       = "";

  /** The audio codec. */
  private String              audioCodec       = "";

  /** The audio channels. */
  private String              audioChannels    = "";

  /** The container format. */
  private String              containerFormat  = "";

  /** The video format. */
  private String              videoFormat      = "";

  /** The exact video format. */
  private String              exactVideoFormat = "";

  /** The video width. */
  private int                 videoWidth       = 0;

  /** The video height. */
  private int                 videoHeight      = 0;

  /** the mediainfo object. */
  @Transient
  private MediaInfo           mediaInfo;

  /**
   * Instantiates a new media file.
   */
  public MediaFile() {
    this.path = "";
    this.filename = "";
  }

  /**
   * Instantiates a new media file.
   * 
   * @param path
   *          the path
   * @param filename
   *          the filename
   */
  public MediaFile(String path, String filename) {
    this.path = path;
    this.filename = filename;

    File file = new File(this.path, this.filename);
    if (file.exists()) {
      setFilesize(FileUtils.sizeOf(file));
    }
  }

  /**
   * Instantiates a new media file.
   * 
   * @param pathAndFilename
   *          the path and filename
   */
  public MediaFile(String pathAndFilename) {
    this.path = FilenameUtils.getPath(pathAndFilename);
    this.filename = FilenameUtils.getName(pathAndFilename);

    File file = new File(this.path, this.filename);
    if (file.exists()) {
      setFilesize(FileUtils.sizeOf(file));
    }
  }

  /**
   * gets the file handle.
   * 
   * @return the file handle or NULL if file does not exits
   */
  public File getFile() {
    File f = new File(this.path, this.filename);
    if (f.exists()) {
      return f;
    }
    else {
      return null;
    }
  }

  /**
   * Gets the path.
   * 
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * Sets the path.
   * 
   * @param newValue
   *          the new path
   */
  public void setPath(String newValue) {
    String oldValue = this.path;
    this.path = newValue;
    firePropertyChange(PATH, oldValue, newValue);
  }

  /**
   * Gets the filename.
   * 
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }

  /**
   * Sets the filename.
   * 
   * @param newValue
   *          the new filename
   */
  public void setFilename(String newValue) {
    String oldValue = this.filename;
    this.filename = newValue;
    firePropertyChange(FILENAME, oldValue, newValue);
  }

  /**
   * Gets the filesize.
   * 
   * @return the filesize
   */
  public long getFilesize() {
    return filesize;
  }

  /**
   * Sets the filesize.
   * 
   * @param newValue
   *          the new filesize
   */
  public void setFilesize(long newValue) {
    long oldValue = this.filesize;
    this.filesize = newValue;
    firePropertyChange(FILESIZE, oldValue, newValue);
    firePropertyChange(FILESIZE_IN_MB, oldValue, newValue);
  }

  /**
   * Gets the filesize in megabytes.
   * 
   * @return the filesize in megabytes
   */
  public String getFilesizeInMegabytes() {
    DecimalFormat df = new DecimalFormat("#0.00");
    return df.format(filesize / (1024.0 * 1024.0)) + " M";
  }

  /**
   * <p>
   * Uses <code>ReflectionToStringBuilder</code> to generate a
   * <code>toString</code> for the specified object.
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
   * instantiates and gets new mediainfo object.
   * 
   * @return MediaInfo object
   */
  private synchronized MediaInfo getMediaInfo() {
    if (mediaInfo == null) {
      mediaInfo = new MediaInfo();
      if (!mediaInfo.open(this.getFile())) {
        LOGGER.error("Mediainfo could not open file: " + this.getFilename());
      }
    }
    return mediaInfo;
  }

  /**
   * Gets the real mediainfo values.
   * 
   * @param streamKind
   *          MediaInfo.StreamKind.(General|Video|Audio|Text|Chapters|Image|Menu
   *          )
   * @param streamNumber
   *          the stream number (0 for first)
   * @param keys
   *          the information you want to fetch
   * @return the media information you asked<br>
   *         <b>OR AN EMPTY STRING IF MEDIAINFO COULD NOT BE LOADED</b> (never
   *         NULL)
   */
  private String getMediaInfo(StreamKind streamKind, int streamNumber, String... keys) {
    for (String key : keys) {
      String value = getMediaInfo().get(streamKind, streamNumber, key);
      if (value.length() > 0) {
        return value;
      }
    }
    return "";
  }

  /**
   * Checks if is empty value.
   * 
   * @param object
   *          the object
   * @return true, if is empty value
   */
  public static boolean isEmptyValue(Object object) {
    return object == null || object.toString().length() == 0;
  }

  /**
   * gets the first token of video codec<br>
   * (e.g. DivX 5 => DivX)
   * 
   * @return the video codec
   */
  public String getVideoCodec() {
    return videoCodec;
  }

  /**
   * Sets the video codec.
   * 
   * @param newValue
   *          the new video codec
   */
  public void setVideoCodec(String newValue) {
    String oldValue = this.videoCodec;
    this.videoCodec = newValue;
    firePropertyChange("videoCodec", oldValue, newValue);
  }

  /**
   * gets the audio codec<br>
   * (w/o punctuation; eg AC-3 => AC3).
   * 
   * @return the audio codec
   */
  public String getAudioCodec() {
    return audioCodec;
  }

  /**
   * Sets the audio codec.
   * 
   * @param newValue
   *          the new audio codec
   */
  public void setAudioCodec(String newValue) {
    String oldValue = this.audioCodec;
    this.audioCodec = newValue;
    firePropertyChange("audioCodec", oldValue, newValue);
  }

  /**
   * returns the container format extensions (e.g. avi, mkv mka mks, OGG, etc.)
   * 
   * @return the container format
   */
  public String getContainerFormat() {
    return this.containerFormat;
  }

  /**
   * Sets the container format.
   * 
   * @param newValue
   *          the new container format
   */
  public void setContainerFormat(String newValue) {
    String oldValue = this.containerFormat;
    this.containerFormat = newValue;
    firePropertyChange("containerFormat", oldValue, newValue);
  }

  /**
   * gets the common video format<br>
   * .
   * 
   * @return 1080p 720p 480p... or null if too small
   */
  public String getVideoFormat() {
    return this.videoFormat;
  }

  /**
   * Sets the video format.
   * 
   * @param newValue
   *          the new video format
   */
  public void setVideoFormat(String newValue) {
    String oldValue = this.videoFormat;
    this.videoFormat = newValue;
    firePropertyChange("videoFormat", oldValue, newValue);
  }

  /**
   * gets the exact video format (height + scantype p/i).
   * 
   * @return the exact video format
   */
  public String getExactVideoFormat() {
    return this.exactVideoFormat;
  }

  /**
   * Sets the exact video format.
   * 
   * @param newValue
   *          the new exact video format
   */
  public void setExactVideoFormat(String newValue) {
    String oldValue = this.exactVideoFormat;
    this.exactVideoFormat = newValue;
    firePropertyChange("exactVideoFormat", oldValue, newValue);
  }

  /**
   * returns the amount of audio channels.
   * 
   * @return the amount of audio channels (eg. 6ch)
   */
  public String getAudioChannels() {
    return this.audioChannels;
  }

  /**
   * Sets the audio channels.
   * 
   * @param newValue
   *          the new audio channels
   */
  public void setAudioChannels(String newValue) {
    String oldValue = this.audioChannels;
    this.audioChannels = newValue;
    firePropertyChange("audioChannels", oldValue, newValue);
  }

  /**
   * returns the exact video resolution.
   * 
   * @return eg 1280x720
   */
  public String getVideoResolution() {
    if (this.videoWidth == 0 || this.videoHeight == 0) {
      return "";
    }
    return this.videoWidth + "x" + this.videoHeight;
  }

  /**
   * Gets the video width.
   * 
   * @return the video width
   */
  public int getVideoWidth() {
    return videoWidth;
  }

  /**
   * Gets the video height.
   * 
   * @return the video height
   */
  public int getVideoHeight() {
    return videoHeight;
  }

  /**
   * Sets the video width.
   * 
   * @param newValue
   *          the new video width
   */
  public void setVideoWidth(int newValue) {
    int oldValue = this.videoWidth;
    this.videoWidth = newValue;
    firePropertyChange("videoWidth", oldValue, newValue);
  }

  /**
   * Sets the video height.
   * 
   * @param newValue
   *          the new video height
   */
  public void setVideoHeight(int newValue) {
    int oldValue = this.videoHeight;
    this.videoHeight = newValue;
    firePropertyChange("videoHeight", oldValue, newValue);
  }

  /**
   * if width-to-height aspect ratio greater than 1.37:1
   * 
   * @return true/false if widescreen
   */
  public Boolean isWidescreen() {
    if (this.videoWidth == 0 || this.videoHeight == 0) {
      return false;
    }
    return ((float) this.videoWidth) / ((float) this.videoHeight) > 1.37f ? true : false;
  }

  /**
   * returns the aspect ratio.
   * 
   * @return the aspect ratio
   */
  public Float getAspectRatio() {
    if (this.videoWidth == 0 || this.videoHeight == 0) {
      return 0F;
    }
    return (float) (Math.round((float) this.videoWidth / (float) this.videoHeight * 100) / 100.0);
  }

  /**
   * SD (less than 720 lines) or HD (more than 720 lines).
   * 
   * @return SD or HD
   */
  public String getVideoDefinitionCategory() {
    if (this.videoWidth == 0 || this.videoHeight == 0) {
      return "";
    }
    return this.videoWidth >= 1280 || this.videoHeight >= 720 ? "HD" : "SD";
  }

  /**
   * Gathers the media information via the native mediainfo lib.
   */
  public void gatherMediaInformation() {
    // video codec
    // e.g. XviD, x264, DivX 5, MPEG-4 Visual, AVC, etc.
    String videoCodec = getMediaInfo(StreamKind.Video, 0, "Encoded_Library/Name", "CodecID/Hint", "Format");
    // get first token (e.g. DivX 5 => DivX)
    setVideoCodec(StringUtils.isEmpty(videoCodec) ? "" : new Scanner(videoCodec).next());

    // audio codec
    // e.g. AC-3, DTS, AAC, Vorbis, MP3, etc.
    String audioCodec = getMediaInfo(StreamKind.Audio, 0, "CodecID/Hint", "Format");
    // remove punctuation (e.g. AC-3 => AC3)
    setAudioCodec(audioCodec.replaceAll("\\p{Punct}", ""));

    // audio channels
    String channels = getMediaInfo(StreamKind.Audio, 0, "Channel(s)");
    setAudioChannels(StringUtils.isEmpty(channels) ? "" : channels + "ch");

    // container format
    String extensions = getMediaInfo(StreamKind.General, 0, "Codec/Extensions", "Format");
    // get first extension
    setContainerFormat(StringUtils.isEmpty(extensions) ? "" : new Scanner(extensions).next().toLowerCase());

    // video format
    setVideoFormat("");
    String v = getMediaInfo(StreamKind.Video, 0, "Height");
    if (!v.isEmpty()) {
      int height = Integer.parseInt(v);

      int ns = 0;
      int[] hs = new int[] { 1080, 720, 576, 540, 480, 360, 240, 120 };
      for (int i = 0; i < hs.length - 1; i++) {
        if (height > hs[i + 1]) {
          ns = hs[i];
          break;
        }
      }

      if (ns > 0) {
        // e.g. 720p, nobody actually wants files to be tagged as interlaced,
        // e.g. 720i
        setVideoFormat(String.format("%dp", ns));
      }
    }

    // exact video format
    String height = getMediaInfo(StreamKind.Video, 0, "Height");
    String scanType = getMediaInfo(StreamKind.Video, 0, "ScanType");

    if (height.isEmpty() || scanType.isEmpty()) {
      setExactVideoFormat("");
    }
    else {
      setExactVideoFormat(height + Character.toLowerCase(scanType.charAt(0)));
    }

    // video dimension
    String width = getMediaInfo(StreamKind.Video, 0, "Width");
    if (!width.isEmpty()) {
      setVideoWidth(Integer.parseInt(width));
    }
    if (!height.isEmpty()) {
      setVideoHeight(Integer.parseInt(height));
    }
  }

  /**
   * Save to db.
   */
  public synchronized void saveToDb() {
    // update DB
    Globals.entityManager.getTransaction().begin();
    Globals.entityManager.persist(this);
    Globals.entityManager.getTransaction().commit();
  }

}

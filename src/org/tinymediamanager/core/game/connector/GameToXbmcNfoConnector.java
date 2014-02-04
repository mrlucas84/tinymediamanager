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
package org.tinymediamanager.core.game.connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileAudioStream;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.Message.MessageLevel;
import org.tinymediamanager.core.MessageManager;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameActor;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.core.game.GameNfoNaming;
import org.tinymediamanager.core.game.GameSet;
import org.tinymediamanager.core.game.connector.GameToXbmcNfoConnector.Actor;
import org.tinymediamanager.core.platform.Platforms;
import org.tinymediamanager.scraper.Certification;
import org.tinymediamanager.scraper.GameMediaGenres;
import org.tinymediamanager.scraper.MediaTrailer;

/**
 * The Class GameToXbmcNfoConnector. isFavorite
 * 
 * @author Manuel Laggner
 */
@XmlRootElement(name = "game")
@XmlSeeAlso(Actor.class)
@XmlType(propOrder = { "title", "originaltitle", "gameset", "sorttitle", "rating", "year", "votes", "plot", "thumb", "fanart", "mpaa",
    "certifications", "developer", "trailer", "platform", "tinyplatform", "fileinfo", "isFavorite", "playcount", "genres", "publisher", "tags",
    "scrapers", "actors", "detailUrl", "region", "media", "perspective", "controller", "version" })
public class GameToXbmcNfoConnector {
  private static final Logger LOGGER         = LoggerFactory.getLogger(GameToXbmcNfoConnector.class);
  private static JAXBContext  context        = initContext();

  @XmlElement
  private String              title          = "";

  @XmlElement
  private String              originaltitle  = "";

  @XmlElement
  private String              year           = "";

  @XmlElement
  private String              plot           = "";

  @XmlElement
  private String              thumb          = "";

  @XmlElement
  private String              fanart         = "";

  @XmlElement
  private String              publisher      = "";

  @XmlElement
  private String              developer      = "";

  @XmlElement
  private String              platform       = "";

  @XmlElement
  private String              tinyplatform   = "";

  @XmlElement
  private String              mpaa           = "";

  @XmlElement(name = "certification")
  private String              certifications = "";

  @XmlElement
  private String              trailer        = "";

  @XmlElement
  private String              gameset        = "";

  @XmlElement
  private String              sorttitle      = "";

  @XmlElement
  private int                 isFavorite     = 0;

  @XmlElement
  private float               rating         = 0;

  @XmlElement
  private int                 votes          = 0;

  @XmlElement
  private int                 playcount      = 0;

  @XmlElement
  private Fileinfo            fileinfo;

  @XmlAnyElement(lax = true)
  private List<Object>        actors;

  @XmlElement(name = "genre")
  private List<String>        genres;

  @XmlElement(name = "tag")
  private List<String>        tags;

  @XmlElement(name = "scrapers")
  private List<ScraperId>     scrapers;

  /** not supported tags, but used to retrain in NFO. */

  @XmlElement
  private Object              detailUrl;
  @XmlElement
  private Object              region;
  @XmlElement
  private Object              media;
  @XmlElement
  private Object              perspective;
  @XmlElement
  private Object              controller;
  @XmlElement
  private Object              version;

  /*
   * inits the context for faster marshalling/unmarshalling
   */
  private static JAXBContext initContext() {
    try {
      return JAXBContext.newInstance(GameToXbmcNfoConnector.class, Actor.class);
    }
    catch (JAXBException e) {
      LOGGER.error("Error instantiating JaxB", e);
    }
    return null;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public GameToXbmcNfoConnector() {
    actors = new ArrayList();
    genres = new ArrayList<String>();
    tags = new ArrayList<String>();
    scrapers = new ArrayList<ScraperId>();
  }

  /**
   * Sets the data.
   * 
   * @param game
   *          the game
   */
  public static void setData(Game game) {
    if (context == null) {
      MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, game, "message.nfo.writeerror", new String[] { ":", "Context is null" }));
      return;
    }

    GameToXbmcNfoConnector xbmc = null;

    // load existing NFO if possible
    for (MediaFile mf : game.getMediaFiles(MediaFileType.NFO)) {
      File file = mf.getFile();
      if (file.exists()) {
        try {
          xbmc = parseNFO(file);
        }
        catch (Exception e) {
          LOGGER.error("failed to parse " + mf.getFilename(), e);
        }
      }
      if (xbmc != null) {
        break;
      }
    }

    // create new
    if (xbmc == null) {
      xbmc = new GameToXbmcNfoConnector();
    }

    // set data
    xbmc.title = game.getTitle();
    xbmc.originaltitle = game.getOriginalTitle();
    xbmc.rating = game.getRating();
    xbmc.votes = game.getVotes();
    xbmc.year = game.getYear();
    xbmc.plot = game.getTagline();

    // outline is only the first 200 characters of the plot
    // int spaceIndex = 0;
    // if (!StringUtils.isEmpty(xbmc.plot) && xbmc.plot.length() > 200) {
    // spaceIndex = xbmc.plot.indexOf(" ", 200);
    // if (spaceIndex > 0) {
    // xbmc.outline = xbmc.plot.substring(0, spaceIndex);
    // }
    // else {
    // xbmc.outline = xbmc.plot;
    // }
    // }
    // else if (!StringUtils.isEmpty(xbmc.plot)) {
    // spaceIndex = xbmc.plot.length();
    // xbmc.outline = xbmc.plot.substring(0, spaceIndex);
    // }

    xbmc.thumb = game.getPosterUrl();
    xbmc.fanart = game.getFanartUrl();

    if (StringUtils.isNotEmpty(game.getPublisher())) {
      xbmc.publisher = game.getPublisher();
    }

    xbmc.developer = game.getProductionCompany();
    xbmc.tinyplatform = game.getPlatform();
    xbmc.platform = Platforms.getInstance().getScraperPlatformNamebyMachineName("romcollection", game.getPlatform());
    xbmc.isFavorite = game.isIsFavorite() ? 1 : 0;
    if (xbmc.isFavorite == 1) {
      xbmc.playcount = 1;
    }

    // certifications
    if (game.getCertification() != null) {
      xbmc.mpaa = Certification.generateCertificationStringWithAlternateNames(game.getCertification());
      xbmc.certifications = Certification.generateCertificationStringWithAlternateNames(game.getCertification());
    }

    // // filename and path
    // if (game.getMediaFiles().size() > 0) {
    // xbmc.setFilenameandpath(game.getPath() + File.separator +
    // game.getMediaFiles().get(0).getFilename());
    // }

    xbmc.actors.clear();
    for (GameActor cast : game.getActors()) {
      xbmc.addActor(cast.getName(), cast.getCharacter(), cast.getThumb());
    }

    xbmc.genres.clear();
    for (GameMediaGenres genre : game.getGenres()) {
      xbmc.genres.add(genre.toString());
    }

    for (MediaTrailer trailer : game.getTrailers()) {
      if (trailer.getInNfo() && !trailer.getUrl().startsWith("file")) {
        // parse internet trailer url for nfo (do not add local one)
        xbmc.trailer = prepareTrailerForXbmc(trailer);
        break;
      }
    }
    // keep trailer already in NFO, remove tag only when empty
    if (xbmc.trailer.isEmpty()) {
      xbmc.trailer = null;
    }

    xbmc.tags.clear();
    for (String tag : game.getTags()) {
      xbmc.tags.add(tag);
    }

    xbmc.scrapers.clear();
    for (Entry<String, Object> entry : game.getIds().entrySet()) {
      ScraperId scraperId = new ScraperId();
      scraperId.gameId = entry.getValue().toString();
      scraperId.scaperName = entry.getKey();
      xbmc.scrapers.add(scraperId);
    }

    // game set
    if (game.getGameSet() != null) {
      GameSet gameSet = game.getGameSet();
      xbmc.gameset = gameSet.getTitle();
    }
    else {
      xbmc.gameset = "";
    }

    xbmc.sorttitle = game.getSortTitle();

    // fileinfo
    for (MediaFile mediaFile : game.getMediaFiles(MediaFileType.GAME)) {
      if (StringUtils.isEmpty(mediaFile.getVideoCodec())) {
        break;
      }

      // if (xbmc.fileinfo == null) {
      Fileinfo info = new Fileinfo();
      info.streamdetails.video.codec = mediaFile.getVideoCodec();
      info.streamdetails.video.aspect = String.valueOf(mediaFile.getAspectRatio());
      info.streamdetails.video.width = mediaFile.getVideoWidth();
      info.streamdetails.video.height = mediaFile.getVideoHeight();

      for (MediaFileAudioStream as : mediaFile.getAudioStreams()) {
        Audio audio = new Audio();
        audio.codec = as.getCodec();
        audio.language = as.getLanguage();
        audio.channels = String.valueOf(as.getChannelsAsInt());
        info.streamdetails.audio.add(audio);
      }
      xbmc.fileinfo = info;
      // }
    }

    // and marshall it
    String nfoFilename = "";
    List<MediaFile> newNfos = new ArrayList<MediaFile>(1);

    List<GameNfoNaming> nfonames = new ArrayList<GameNfoNaming>();
    if (game.isMultiGameDir()) {
      // Fixate the name regardless of setting
      nfonames.add(GameNfoNaming.FILENAME_NFO);
    }
    else {
      nfonames = Globals.settings.getGameSettings().getGameNfoFilenames();
    }
    for (GameNfoNaming name : nfonames) {
      try {
        nfoFilename = game.getNfoFilename(name);

        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // w = new FileWriter(nfoFilename);
        Writer w = new StringWriter();
        m.marshal(xbmc, w);
        StringBuilder sb = new StringBuilder(w.toString());
        w.close();

        // on windows make windows conform linebreaks
        if (SystemUtils.IS_OS_WINDOWS) {
          sb = new StringBuilder(sb.toString().replaceAll("(?<!\r)\n", "\r\n"));
        }
        File f = new File(game.getPath(), nfoFilename);
        FileUtils.write(f, sb, "UTF-8");
        newNfos.add(new MediaFile(f));
      }
      catch (Exception e) {
        LOGGER.error("setData", e.getMessage());
        MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, game, "message.nfo.writeerror", new String[] { ":",
            e.getLocalizedMessage() }));
      }
    }

    if (newNfos.size() > 0) {
      game.removeAllMediaFiles(MediaFileType.NFO);
      game.addToMediaFiles(newNfos);
    }
  }

  /**
   * Gets the data.
   * 
   * @param nfoFile
   *          the nfo filename
   * @return the data
   */
  public static Game getData(File nfoFile) {
    if (context == null) {
      MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, nfoFile, "message.nfo.readerror"));
      return null;
    }

    // try to parse XML
    Game game = null;
    try {
      GameToXbmcNfoConnector xbmc = parseNFO(nfoFile);
      game = new Game();
      game.setTitle(xbmc.title);
      game.setOriginalTitle(xbmc.originaltitle);
      game.setRating(xbmc.rating);
      game.setVotes(xbmc.votes);
      game.setYear(xbmc.year);
      game.setTagline(xbmc.plot);

      if (StringUtils.isNotBlank(xbmc.thumb)) {
        if (xbmc.thumb.contains("http://")) {
          game.setPosterUrl(xbmc.thumb);
        }
      }

      if (StringUtils.isNotBlank(xbmc.fanart)) {
        if (xbmc.fanart.contains("http://")) {
          game.setFanartUrl(xbmc.fanart);
        }
      }

      game.setPublisher(xbmc.publisher);
      game.setProductionCompany(xbmc.developer);
      game.setPlatform(xbmc.tinyplatform);
      if (!StringUtils.isEmpty(xbmc.certifications)) {
        game.setCertification(Certification.parseCertificationStringForGameSetupCountry(xbmc.certifications));
      }
      if (!StringUtils.isEmpty(xbmc.mpaa) && game.getCertification() == Certification.NOT_RATED) {
        game.setCertification(Certification.parseCertificationStringForGameSetupCountry(xbmc.mpaa));
      }
      game.setIsFavorite(xbmc.isFavorite == 0 ? false : true);

      // gameset
      if (StringUtils.isNotEmpty(xbmc.gameset)) {
        // search for that gameset
        GameList gameList = GameList.getInstance();
        GameSet gameSet = gameList.getGameSet(xbmc.gameset, 0);

        // add game to gameset
        if (gameSet != null) {
          game.setGameSet(gameSet);
        }
      }

      // be aware of the sorttitle - set an empty string if nothing has been
      // found
      if (StringUtils.isEmpty(xbmc.sorttitle)) {
        game.setSortTitle("");
      }
      else {
        game.setSortTitle(xbmc.sorttitle);
      }

      for (Actor actor : xbmc.getActors()) {
        GameActor cast = new GameActor(actor.name, actor.role);
        cast.setThumb(actor.thumb);
        game.addActor(cast);
      }

      for (String genre : xbmc.genres) {
        GameMediaGenres genreFound = GameMediaGenres.getGenre(genre);
        if (genreFound != null) {
          game.addGenre(genreFound);
        }
      }

      if (StringUtils.isNotEmpty(xbmc.trailer)) {
        String urlFromNfo = parseTrailerUrl(xbmc.trailer);
        if (!urlFromNfo.startsWith("file")) {
          // only add new MT when not a local file
          MediaTrailer trailer = new MediaTrailer();
          trailer.setName("fromNFO");
          trailer.setProvider("from NFO");
          trailer.setQuality("unknown");
          trailer.setUrl(urlFromNfo);
          trailer.setInNfo(true);
          game.addTrailer(trailer);
        }
      }

      for (String tag : xbmc.tags) {
        game.addToTags(tag);
      }

      for (ScraperId scraper : xbmc.scrapers) {
        game.setId(scraper.scaperName, scraper.gameId);
      }

    }
    catch (UnmarshalException e) {
      LOGGER.error("getData " + e.getMessage());
      return null;
    }
    catch (Exception e) {
      LOGGER.error("getData", e);
      return null;
    }

    // only return if a game name has been found
    if (StringUtils.isEmpty(game.getTitle())) {
      return null;
    }

    return game;
  }

  private static GameToXbmcNfoConnector parseNFO(File nfoFile) throws Exception {
    Unmarshaller um = context.createUnmarshaller();
    if (um == null) {
      MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, nfoFile, "message.nfo.readerror"));
      throw new Exception("could not create unmarshaller");
    }

    try {
      Reader in = new InputStreamReader(new FileInputStream(nfoFile), "UTF-8");
      return (GameToXbmcNfoConnector) um.unmarshal(in);
    }
    catch (UnmarshalException e) {
      LOGGER.error("tried to unmarshal; now trying to clean xml stream");
    }

    // now trying to parse it via string
    String completeNFO = FileUtils.readFileToString(nfoFile, "UTF-8").trim().replaceFirst("^([\\W]+)<", "<");
    Reader in = new StringReader(completeNFO);
    return (GameToXbmcNfoConnector) um.unmarshal(in);
  }

  private void addActor(String name, String role, String thumb) {
    Actor actor = new Actor(name, role, thumb);
    actors.add(actor);
  }

  public List<Actor> getActors() {
    // @XmlAnyElement(lax = true) causes all unsupported tags to be in actors;
    // filter Actors out
    List<Actor> pureActors = new ArrayList<Actor>();
    for (Object obj : actors) {
      if (obj instanceof Actor) {
        Actor actor = (Actor) obj;
        pureActors.add(actor);
      }
    }
    return pureActors;
  }

  private static String prepareTrailerForXbmc(MediaTrailer trailer) {
    // youtube trailer are stored in a special notation: plugin://plugin.video.youtube/?action=play_video&videoid=<ID>
    // parse out the ID from the url and store it in the right notation
    Pattern pattern = Pattern.compile("https{0,1}://.*youtube..*/watch\\?v=(.*)$");
    Matcher matcher = pattern.matcher(trailer.getUrl());
    if (matcher.matches()) {
      return "plugin://plugin.video.youtube/?action=play_video&videoid=" + matcher.group(1);
    }

    // other urls are handled by the hd-trailers.net plugin
    pattern = Pattern.compile("https{0,1}://.*(apple.com|yahoo-redir|yahoo.com|youtube.com|gamefone.com|ign.com|hd-trailers.net|aol.com).*");
    matcher = pattern.matcher(trailer.getUrl());
    if (matcher.matches()) {
      try {
        return "plugin://plugin.video.hdtrailers_net/video/" + matcher.group(1) + "/" + URLEncoder.encode(trailer.getUrl(), "UTF-8");
      }
      catch (Exception e) {
        LOGGER.error("failed to escape " + trailer.getUrl());
      }
    }
    // everything else is stored directly
    return trailer.getUrl();
  }

  private static String parseTrailerUrl(String nfoTrailerUrl) {
    // try to parse out youtube trailer plugin
    Pattern pattern = Pattern.compile("plugin://plugin.video.youtube/?action=play_video&videoid=(.*)$");
    Matcher matcher = pattern.matcher(nfoTrailerUrl);
    if (matcher.matches()) {
      return "http://www.youtube.com/watch?v=" + matcher.group(1);
    }

    pattern = Pattern.compile("plugin://plugin.video.hdtrailers_net/video/.*?/(.*)$");
    matcher = pattern.matcher(nfoTrailerUrl);
    if (matcher.matches()) {
      try {
        return URLDecoder.decode(matcher.group(1), "UTF-8");
      }
      catch (UnsupportedEncodingException e) {
        LOGGER.error("failed to unescape " + nfoTrailerUrl);
      }
    }

    return nfoTrailerUrl;
  }

  /*
   * inner class actor to represent actors
   */
  @XmlRootElement(name = "actor")
  public static class Actor {

    @XmlElement
    private String name;

    @XmlElement
    private String role;

    @XmlElement
    private String thumb;

    public Actor() {
    }

    public Actor(String name, String role, String thumb) {
      this.name = name;
      this.role = role;
      this.thumb = thumb;
    }
  }

  /*
   * inner class holding file informations
   */
  static class Fileinfo {
    @XmlElement
    Streamdetails streamdetails;

    public Fileinfo() {
      streamdetails = new Streamdetails();
    }
  }

  /*
   * inner class holding details of audio and video stream
   */
  static class Streamdetails {
    @XmlElement
    private Video       video;

    @XmlElement
    private List<Audio> audio;

    public Streamdetails() {
      video = new Video();
      audio = new ArrayList<Audio>();
    }
  }

  /*
   * inner class holding details of the video stream
   */
  static class Video {
    @XmlElement
    private String codec;

    @XmlElement
    private String aspect;

    @XmlElement
    private int    width;

    @XmlElement
    private int    height;

    @XmlElement
    private int    durationinseconds;
  }

  /*
   * inner class holding details of the audio stream
   */
  static class Audio {
    @XmlElement
    private String codec;

    @XmlElement
    private String language;

    @XmlElement
    private String channels;
  }

  static class ScraperId {
    @XmlElement
    String scaperName;

    @XmlElement
    String gameId;

    @XmlElement
    String url = "";
  }
}

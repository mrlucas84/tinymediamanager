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
package org.tinymediamanager.scraper.thegamesdb;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.platform.Platforms;
import org.tinymediamanager.scraper.Certification;
import org.tinymediamanager.scraper.CountryCode;
import org.tinymediamanager.scraper.GameMediaGenres;
import org.tinymediamanager.scraper.GameMediaGenresScapers;
import org.tinymediamanager.scraper.IMediaArtworkProvider;
import org.tinymediamanager.scraper.IMediaMetadataProvider;
import org.tinymediamanager.scraper.IMediaTrailerProvider;
import org.tinymediamanager.scraper.MediaArtwork;
import org.tinymediamanager.scraper.MediaArtwork.MediaArtworkType;
import org.tinymediamanager.scraper.MediaArtwork.PosterSizes;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaProviderInfo;
import org.tinymediamanager.scraper.MediaScrapeOptions;
import org.tinymediamanager.scraper.MediaSearchOptions;
import org.tinymediamanager.scraper.MediaSearchResult;
import org.tinymediamanager.scraper.MediaTrailer;
import org.tinymediamanager.scraper.MediaType;
import org.tinymediamanager.scraper.MetadataUtil;
import org.tinymediamanager.scraper.util.CachedUrl;
import org.tinymediamanager.scraper.util.Similarity;
import org.tinymediamanager.scraper.util.Url;

/**
 * The Class TheGamesDbMetadataProvider
 * 
 * @author masterlilou, Manuel Laggner
 */
public class TheGamesDbMetadataProvider implements IMediaMetadataProvider, IMediaArtworkProvider, IMediaTrailerProvider {
  private static final Logger            LOGGER       = LoggerFactory.getLogger(TheGamesDbMetadataProvider.class);
  private static final String            BASE_URL     = "http://thegamesdb.net/api/";
  private static final MediaProviderInfo providerInfo = new MediaProviderInfo("thegamesdb", "TheGamesDB",
                                                          "Scraper for TheGamesDB which is able to scrape game metadata");

  public TheGamesDbMetadataProvider() {
  }

  @Override
  public MediaProviderInfo getProviderInfo() {
    return providerInfo;
  }

  @Override
  public MediaMetadata getMetadata(MediaScrapeOptions options) throws Exception {
    LOGGER.debug("TheGamesDB: getMetadata() " + options.toString());
    MediaMetadata md = new MediaMetadata(providerInfo.getId());

    String searchString = "";
    String gamesDbId = "";

    // id from result
    if (options.getResult() != null) {
      gamesDbId = options.getResult().getId();
    }

    // id from scrape options
    if (StringUtils.isBlank(gamesDbId)) {
      gamesDbId = options.getId(providerInfo.getId());
    }

    if (StringUtils.isBlank(gamesDbId)) {
      LOGGER.warn("not possible to scrape from TheGamesDB - no id found");
      return md;
    }

    searchString = BASE_URL + "GetGame.php?id=" + gamesDbId;
    LOGGER.debug("get metadata with TheGamesDB: " + searchString);

    try {
      Url url = new CachedUrl(searchString);
      InputStream in = url.getInputStream();
      Document doc = Jsoup.parse(in, "UTF-8", "");
      in.close();

      // id
      Element elem = doc.select("id").first();
      if (elem != null) {
        md.setId(providerInfo.getId(), elem.text());
      }

      // title
      elem = doc.select("GameTitle").first();
      if (elem != null) {
        md.storeMetadata(MediaMetadata.TITLE, elem.text());
      }

      // overview
      elem = doc.select("Overview").first();
      if (elem != null) {
        md.storeMetadata(MediaMetadata.PLOT, elem.text());

      }

      // release date & year
      elem = doc.select("ReleaseDate").first();
      if (elem != null) {
        String text = elem.text();
        Pattern pattern = Pattern.compile("[0-9]{4}");
        Matcher matcher = pattern.matcher(text);
        String year = "";
        if (matcher.find()) {
          year = matcher.group().substring(0);
          md.storeMetadata(MediaMetadata.YEAR, year);
        }

        try {
          if (text.contains("/")) {
            String[] parts = text.split("/");
            // results
            md.storeMetadata(MediaMetadata.RELEASE_DATE, parts[0] + "-" + parts[1] + "-" + parts[2]);
          }
        }
        catch (Exception e) {
          md.storeMetadata(MediaMetadata.RELEASE_DATE, "01-01-" + year);
        }
      }

      // platform
      elem = doc.select("PlatformId").first();
      if (elem != null) {
        Integer platformId = Integer.decode(elem.text());
        String platform = Platforms.getInstance().getPlatformNamebyMachineId(providerInfo.getId(), platformId);
        md.storeMetadata(MediaMetadata.PLATFORM, platform);
      }

      // publisher
      elem = doc.select("Publisher").first();
      if (elem != null) {
        md.storeMetadata(MediaMetadata.PUBLISHER, elem.text());
      }

      // developer
      elem = doc.select("Developer").first();
      if (elem != null) {
        md.storeMetadata(MediaMetadata.PRODUCTION_COMPANY, elem.text());
      }

      // genre
      Elements genres = doc.select("genre");
      for (Element genre : genres) {
        if (genre != null) {
          GameMediaGenres g = GameMediaGenresScapers.getGenre(genre.text());
          md.addGameGenre(g);
        }
      }

      // rating
      elem = doc.select("Rating").first();
      if (elem != null) {
        md.storeMetadata(MediaMetadata.RATING, (Float.parseFloat(elem.text())));
      }

      // certification
      elem = doc.select("ESRB").first();
      if (elem != null) {
        String text = elem.text();
        for (Certification cert : Certification.getCertificationsforCountry(CountryCode.US)) {
          for (String val : cert.getPossibleNotations()) {
            if (text.contentEquals(val)) {
              md.addCertification(cert);
            }
          }
        }
      }

      // poster
      String imageBaseUrl = doc.select("baseImgUrl").first().text();
      Elements boxarts = doc.select("boxart");
      if (boxarts != null) {
        for (Element boxart : boxarts) {
          String side = boxart.attr("side");
          if (side.contentEquals("front")) {
            String poster = boxart.attr("thumb");
            md.storeMetadata(MediaMetadata.POSTER_URL, imageBaseUrl + poster);
            break;
          }
        }
      }

    }
    catch (Exception e) {
      LOGGER.error("Error getting metadata from TheGamesDB: " + e.getMessage());
    }

    return md;
  }

  @Override
  public List<MediaArtwork> getArtwork(MediaScrapeOptions options) throws Exception {
    LOGGER.debug("TheGamesDB: getArtwork() " + options.toString());
    List<MediaArtwork> artworks = new ArrayList<MediaArtwork>();

    // id from scrape options
    String gamesDbId = options.getId(providerInfo.getId());

    if (StringUtils.isBlank(gamesDbId)) {
      LOGGER.warn("not possible to scrape from TheGamesDB - no id found");
      return artworks;
    }

    // we also get the data via the game api -> in most cases the calls are already cached
    String searchString = BASE_URL + "GetGame.php?id=" + gamesDbId;
    LOGGER.debug("get artwork with TheGamesDB: " + searchString);
    try {
      Url url = new CachedUrl(searchString);
      InputStream in = url.getInputStream();
      Document doc = Jsoup.parse(in, "UTF-8", "");
      in.close();

      String url_base = doc.select("baseImgUrl").first().text();

      // fanart resists in <fanart> and <screenshot> tags
      if (options.getArtworkType() == MediaArtworkType.ALL || options.getArtworkType() == MediaArtworkType.BACKGROUND) {
        Elements fanarts = doc.select("fanart");
        fanarts.addAll(doc.select("screenshot"));
        for (Element fanart : fanarts) {
          try {
            Element original = fanart.select("original").first();
            String fanartUrl = url_base + original.text();
            int width = Integer.parseInt(original.attr("width"));
            int height = Integer.parseInt(original.attr("height"));

            String thumbUrl = "";
            Element thumb = fanart.select("thumb").first();
            if (thumb != null) {
              thumbUrl = url_base + thumb.text();
            }

            MediaArtwork artwork = new MediaArtwork();
            artwork.setType(MediaArtworkType.BACKGROUND);
            artwork.addImageSize(width, height, fanartUrl);
            artwork.setSizeOrder(PosterSizes.LARGE.getOrder());
            artwork.setDefaultUrl(fanartUrl);
            artwork.setPreviewUrl(thumbUrl);
            artworks.add(artwork);
          }
          catch (Exception e) {
            LOGGER.warn("error parsing fanart: " + e.getMessage());
          }
        }
      }

      // posters are in <boxart> tags; we only take the front
      if (options.getArtworkType() == MediaArtworkType.ALL || options.getArtworkType() == MediaArtworkType.POSTER) {
        for (Element boxart : doc.select("boxart")) {
          String side = boxart.attr("side");
          if ("front".equals(side)) {
            try {
              String boxartUrl = url_base + boxart.text();
              String thumbUrl = url_base + boxart.attr("thumb");
              int width = Integer.parseInt(boxart.attr("width"));
              int height = Integer.parseInt(boxart.attr("height"));

              MediaArtwork artwork = new MediaArtwork();
              artwork.setType(MediaArtworkType.POSTER);
              artwork.addImageSize(width, height, boxartUrl);
              artwork.setSizeOrder(PosterSizes.BIG.getOrder());
              artwork.setDefaultUrl(boxartUrl);
              artwork.setPreviewUrl(thumbUrl);
              artworks.add(artwork);
            }
            catch (Exception e) {
              LOGGER.warn("error parsing boxart: " + e.getMessage());
            }
          }
        }
      }
    }
    catch (Exception e) {
      LOGGER.error("error parsing artwork: " + e.getMessage());
    }

    // buffer the artwork
    MediaMetadata md = options.getMetadata();
    if (md != null) {
      md.addMediaArt(artworks);
    }

    return artworks;
  }

  @Override
  public List<MediaSearchResult> search(MediaSearchOptions options) throws Exception {
    LOGGER.debug("search() " + options.toString());
    List<MediaSearchResult> resultList = new ArrayList<MediaSearchResult>();
    String searchString = "";
    String imdb = "";
    Elements filme = null;
    List<String> IdList = null;

    // 1. search with Id
    if (StringUtils.isNotEmpty(options.get(MediaSearchOptions.SearchParam.GAMEID))) {
      imdb = options.get(MediaSearchOptions.SearchParam.GAMEID);
      searchString = BASE_URL + "GetGame.php?id=" + imdb;
      LOGGER.debug("search with GameDB: " + imdb);

      Url url = new CachedUrl(searchString);
      LOGGER.debug("search with GameDB on : " + searchString);
      InputStream in = url.getInputStream();
      MediaSearchResult sr = updateMedia(in, providerInfo.getId(), imdb, null);
      Document doc = Jsoup.parse(in, "UTF-8", "");
      in.close();
      if (sr.getScore() != 0) {
        resultList.add(sr);
        LOGGER.debug("found ");
      }
    }

    // 1. search for search string
    if (StringUtils.isNotEmpty(options.get(MediaSearchOptions.SearchParam.QUERY)) && (filme == null || filme.isEmpty())) {
      String query = options.get(MediaSearchOptions.SearchParam.QUERY);
      query = MetadataUtil.removeNonSearchCharacters(query);

      if (StringUtils.isNotEmpty(options.get(MediaSearchOptions.SearchParam.PLATFORM))) {
        String platform = options.get(MediaSearchOptions.SearchParam.PLATFORM);
        // Example http://thegamesdb.net/api/GetGamesList.php?name=tomb%20raider&platform=Sony%20Playstation
        // http://thegamesdb.net/api/GetGamesList.php?name=Velvet+Assassin&platform=Microsoft%20Xbox%20360

        if (platform.equalsIgnoreCase("All")) {
          searchString = BASE_URL + "GetGamesList.php?name=" + URLEncoder.encode(query, "UTF-8");
        }
        else {
          String ma = Platforms.getInstance().getScraperPlatformNamebyMachineName(providerInfo.getId(), platform);
          searchString = BASE_URL + "GetGamesList.php?" + "&platform=" + URLEncoder.encode(ma, "UTF-8") + "&name="
              + URLEncoder.encode(query, "UTF-8");
        }
      }
      else {
        searchString = BASE_URL + "GetGamesList.php?name=" + URLEncoder.encode(query, "UTF-8");
      }
      LOGGER.debug("search for everything: " + query);

      {
        Url url = new CachedUrl(searchString);
        LOGGER.debug("search with GameDB on : " + searchString);
        InputStream in = url.getInputStream();
        IdList = searchIdList(in);
        in.close();
        // only look for game links
        LOGGER.debug("found " + IdList.size() + " search results");
      }

      for (String a : IdList) {
        try {
          imdb = a;
          searchString = BASE_URL + "GetGame.php?id=" + imdb;
          LOGGER.debug("search with GameDB: " + imdb);

          Url url = new CachedUrl(searchString);
          LOGGER.debug("search with GameDB on : " + searchString);
          InputStream in = url.getInputStream();
          MediaSearchResult sr = updateMedia(in, providerInfo.getId(), imdb, query);
          in.close();

          if (sr.getScore() != 0) {
            resultList.add(sr);
            LOGGER.debug("found ");
          }
        }
        catch (Exception e) {
          LOGGER.warn("error parsing game result: " + e.getMessage());
        }
      }
      Collections.sort(resultList);
      Collections.reverse(resultList);
    }

    return resultList;
  }

  @Override
  public List<MediaTrailer> getTrailers(MediaScrapeOptions options) throws Exception {
    LOGGER.debug("getTrailers() " + options.toString());
    List<MediaTrailer> trailers = new ArrayList<MediaTrailer>();

    {
      String imdb = options.getImdbId();
      String url1 = BASE_URL + "/GetGame.php?id=" + imdb;
      LOGGER.debug("search trailer on GameDB machine " + url1);

      Url url2;
      Integer Id = 0;
      try {
        url2 = new CachedUrl(url1);
        InputStream in = url2.getInputStream();

        String StringFromInputStream = IOUtils.toString(in, "UTF-8");
        Document doc = Jsoup.parse(StringFromInputStream, "");
        in.close();

        Elements localvideos = doc.select("Youtube");

        for (Element localvideo : localvideos) {
          {
            if (localvideo != null) {

              MediaTrailer trailer = new MediaTrailer();

              String text = localvideo.text();
              trailer.setName(text);
              trailer.setQuality(0 + " (" + 0 + ")");
              trailer.setProvider("gameDB");
              trailer.setUrl(text);
              LOGGER.debug(trailer.toString());
              trailers.add(trailer);
            }
          }
        }
      }
      catch (IOException e) {
        LOGGER.error("Error parsing ");
      }
    }

    return trailers;
  }

  public List<String> searchIdList(InputStream in) throws Exception {
    List<String> resultList = new ArrayList<String>();
    LOGGER.debug("get list of id");

    try {
      String StringFromInputStream = IOUtils.toString(in, "UTF-8");

      Document doc = Jsoup.parse(StringFromInputStream, "");

      Elements games = doc.select("Game");

      for (Element game : games) {
        {
          Element elem = game.select("id").first();
          if (elem != null) {
            String text = elem.text();
            resultList.add(text);
          }
        }
      }
    }
    catch (IOException e) {
      LOGGER.error("Error parsing ");
      throw e;
    }

    return resultList;
  }

  public MediaSearchResult updateMedia(InputStream in, String provider, String Id, String query) throws Exception {
    MediaSearchResult sr = new MediaSearchResult(provider);
    LOGGER.debug("get from id " + Id);

    sr.setMediaType(MediaType.MOVIE);
    sr.setId(Id);
    sr.setIMDBId(Id);
    sr.setScore(0);
    sr.setYear("?");

    try {
      String StringFromInputStream = IOUtils.toString(in, "UTF-8");
      Document doc = Jsoup.parse(StringFromInputStream, "");

      {
        Element elem = doc.select("GameTitle").first();
        String text = elem.text();
        sr.setTitle(text);
        LOGGER.debug("found title videogame " + sr.getTitle());
      }

      if (query == null) {
        sr.setScore(1);
      }
      else {
        sr.setScore(Similarity.compareStrings(sr.getTitle(), query));
      }
      LOGGER.debug("score  " + sr.getScore());

      {
        Element elem = doc.select("Overview").first();
        if (elem != null) {
          String text = elem.text();
          LOGGER.debug("found title videogame " + sr.getTitle());
        }
      }

      {
        Element elem = doc.select("ReleaseDate").first();
        if (elem != null) {
          String text = elem.text();
          Pattern pattern = Pattern.compile("[0-9]{4}");
          Matcher matcher = pattern.matcher(text);
          if (matcher.find()) {
            text = matcher.group().substring(0);
            sr.setYear(text);
            LOGGER.debug("found Year " + sr.getYear());
          }
        }
      }

      {
        Element elem = doc.select("PlatformId").first();
        if (elem != null) {
          String text = elem.text();
          Integer Idmachine = Integer.decode(text);
          String ma = Platforms.getInstance().getPlatformNamebyMachineId(providerInfo.getId(), Idmachine);
          sr.setTitle(sr.getTitle() + " (" + ma + ")");
          LOGGER.debug("found id_machine " + sr.getTitle());
        }
      }

      {
        Element elem = doc.select("Rating").first();
        if (elem != null) {
          String text = elem.text();
          // sr.se(Float.parseFloat(text));
        }
      }

      {
        String url_base = doc.select("baseImgUrl").first().text();
        Elements boxarts = doc.select("boxart");

        for (Element boxart : boxarts) {
          if (boxarts != null) {
            String side = boxart.attr("side");
            if (side.contentEquals("front")) {
              String poster = boxart.attr("thumb");
              sr.setPosterUrl(url_base + poster);
            }
          }
        }
      }

    }
    catch (Exception e) {
      LOGGER.error("Error parsing ", e);
      // throw e;
    }
    return sr;
  }
}

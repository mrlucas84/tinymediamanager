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
package org.tinymediamanager.scraper.giantbomb;

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
import org.tinymediamanager.Globals;
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
import org.tinymediamanager.scraper.util.Url;

/**
 * The Class GiantbombMetadataProvider
 * 
 * @author masterlilou, Manuel Laggner
 */
public class giantbombMetadataProvider implements IMediaMetadataProvider, IMediaArtworkProvider, IMediaTrailerProvider {
  private static final Logger            LOGGER       = LoggerFactory.getLogger(giantbombMetadataProvider.class);
  private static final String            BASE_URL     = "http://www.giantbomb.com/api";
  private static final String            KEY          = "?api_key=0a204c5b48872c635a858970050ec807c381b13e";
  private static final String            FORMAT_XML   = "&format=xml";
  private static final MediaProviderInfo providerInfo = new MediaProviderInfo("giantbomb", "giantbomb",
                                                          "Scraper for french giantbomb.com which is able to scrape game metadata");

  public giantbombMetadataProvider() {
  }

  @Override
  public MediaProviderInfo getProviderInfo() {
    return providerInfo;
  }

  @Override
  public MediaMetadata getMetadata(MediaScrapeOptions options) throws Exception {
    LOGGER.debug("Giantbomb: getMetadata() " + options.toString());

    MediaMetadata md = new MediaMetadata(providerInfo.getId());

    String giantbombId = "";

    // id from result
    if (options.getResult() != null) {
      giantbombId = options.getResult().getId();
    }

    // id from scrape options
    if (StringUtils.isBlank(giantbombId)) {
      giantbombId = options.getId(providerInfo.getId());
    }

    if (StringUtils.isBlank(giantbombId)) {
      LOGGER.warn("not possible to scrape from Giantbomb - no id found");
      return md;
    }

    String searchString = BASE_URL + "/game/" + giantbombId + "/" + KEY;
    LOGGER.debug("get metadata with giantbomb: " + giantbombId);

    try {
      Url url = new CachedUrl(searchString);
      InputStream in = url.getInputStream();
      Document doc = Jsoup.parse(in, "UTF-8", "");
      in.close();

      // title
      Element elem = doc.select("name").first();
      if (elem != null) {
        md.storeMetadata(MediaMetadata.TITLE, elem.text());
      }

      // description
      elem = doc.select("description").first();
      if (elem != null) {
        md.storeMetadata(MediaMetadata.PLOT, elem.text());
      }

      // deck
      elem = doc.select("deck").first();
      if (elem != null) {
        md.storeMetadata(MediaMetadata.TAGLINE, elem.text());
      }

      // release date
      elem = doc.select("original_release_date").first();
      if (elem != null) {
        Pattern pattern = Pattern.compile("[0-9]{4}");
        Matcher matcher = pattern.matcher(elem.text());
        String year = "";
        if (matcher.find()) {
          year = matcher.group().substring(0);
          md.storeMetadata(MediaMetadata.YEAR, year);
        }
        try {
          String releaseDate = "01-01-" + year;
          if (elem.text().contains(" ")) {
            String[] parts = elem.text().split(" ");
            String date = parts[0];
            if (date.contains("-")) {
              String[] part2s = date.split("-");
              releaseDate = part2s[0] + "-" + part2s[1] + "-" + part2s[2];
            }
          }

          md.storeMetadata(MediaMetadata.RELEASE_DATE, releaseDate);
        }
        catch (Exception e) {
          md.storeMetadata(MediaMetadata.RELEASE_DATE, "01-01-" + year);
        }
      }

      // rating
      elem = doc.select("original_game_rating").first();
      if (elem != null) {
        try {
          md.storeMetadata(MediaMetadata.RATING, Float.parseFloat(elem.text()));
        }
        catch (Exception e) {
          LOGGER.warn("problem parsing rating: " + e.getMessage());
        }

        // platform
        // FIXME
        // String ma = Platforms.getPlatformNamebyMachineId(providerInfo.getId(), options.getResult().getPlatform());
        // md.setPlatform(ma);

        // publisher

        StringBuilder publisher = new StringBuilder();
        for (Element pub : doc.select("publisher")) {
          Element name = pub.select("name").first();
          if (name != null) {
            if (StringUtils.isNotBlank(publisher)) {
              publisher.append(", ");
            }
            publisher.append(name.text());
          }
        }
        md.storeMetadata(MediaMetadata.PUBLISHER, publisher.toString());

        // developer
        StringBuilder developer = new StringBuilder();
        for (Element pub : doc.select("developer")) {
          Element name = pub.select("name").first();
          if (name != null) {
            if (StringUtils.isNotBlank(developer)) {
              developer.append(", ");
            }
            developer.append(name.text());
          }
        }
        md.storeMetadata(MediaMetadata.PRODUCTION_COMPANY, developer.toString());

        // genres
        Elements genres = doc.select("genre");
        for (Element genre : genres) {
          if (genre != null) {
            Elements names = genres.select("name");
            for (Element name : names) {
              if (name != null) {
                String text = name.text();
                GameMediaGenres g = GameMediaGenresScapers.getGenre(text);
                md.addGameGenre(g);
              }
            }
          }
        }

        // rating
        Elements game_ratings = doc.select("game_rating");
        for (Element game_rating : game_ratings) {

          if (game_rating != null) {
            Element id = game_rating.select("id").first();
            if (id != null) {
              if (id.text().equalsIgnoreCase("16")) { // ESRB
                Element name = game_rating.select("name").first();
                if (name != null) {
                  String text = name.text();
                  for (Certification cert : Certification.getCertificationsforCountry(CountryCode.US)) {
                    for (String val : cert.getPossibleNotations()) {
                      if (text.contentEquals(val)) {
                        md.addCertification(cert);
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // poster url
        Element image = doc.select("medium_url").first();
        if (image != null) {
          md.storeMetadata(MediaMetadata.POSTER_URL, image.text());
        }
      }

      // TODO single developers?

    }
    catch (Exception e) {
      LOGGER.error("Error parsing ", e);
    }

    return md;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.scraper.IMediaArtworkProvider#getArtwork(org. tinymediamanager.scraper.MediaScrapeOptions)
   */
  @Override
  public List<MediaArtwork> getArtwork(MediaScrapeOptions options) throws Exception {
    LOGGER.debug("getArtwork() " + options.toString());
    MediaArtworkType artworkType = options.getArtworkType();

    List<MediaArtwork> artworks = new ArrayList<MediaArtwork>();

    List<MediaArtworkType> types = new ArrayList<MediaArtworkType>();

    if (options.getArtworkType() == MediaArtworkType.ALL) {
      types.add(MediaArtworkType.POSTER);
      types.add(MediaArtworkType.BACKGROUND);
      types.add(MediaArtworkType.THUMB);
    }
    else if (options.getArtworkType() == MediaArtworkType.BACKGROUND) {
      types.add(MediaArtworkType.BACKGROUND);
      types.add(MediaArtworkType.THUMB);
    }
    else
      types.add(options.getArtworkType());

    for (MediaArtworkType type : types) {
      switch (type) {
        case ALL:
          break;

        case BACKGROUND: {
          String imdb = options.getImdbId();
          String searchString = BASE_URL + "/game/" + imdb + "/" + KEY + "&field_list=images";

          LOGGER.debug("search with giantbomb: " + imdb);

          Url url1 = new CachedUrl(searchString);
          LOGGER.debug("search with giantbomb on : " + searchString);
          InputStream in = url1.getInputStream();

          try {
            String StringFromInputStream = IOUtils.toString(in, "UTF-8");
            Document doc = Jsoup.parse(StringFromInputStream, "");
            in.close();

            {
              Elements screen_urls = doc.select("super_url"); // BUG ???? with images ???
              String large_screen = "";

              for (Element image_url : screen_urls) {
                if (image_url != null) {
                  large_screen = image_url.text();

                  MediaArtwork artwork = new MediaArtwork();
                  artwork.setType(MediaArtworkType.BACKGROUND);
                  artwork.addImageSize(100, 100, large_screen);
                  artwork.setSizeOrder(PosterSizes.LARGE.getOrder());
                  artwork.setDefaultUrl(large_screen);
                  artwork.setPreviewUrl(large_screen);
                  artworks.add(artwork);
                }
              }
            }
          }
          catch (IOException e) {
            LOGGER.error("Error parsing ");
            throw e;
          }
        }
          break;
        case POSTER: {
          String imdb = options.getImdbId();
          String searchString = BASE_URL + "/game/" + imdb + "/" + KEY + "&field_list=image";
          LOGGER.debug("search with giantbomb: " + imdb);

          Url url1 = new CachedUrl(searchString);
          LOGGER.debug("search with giantbomb on : " + searchString);
          InputStream in = url1.getInputStream();

          try {
            String StringFromInputStream = IOUtils.toString(in, "UTF-8");
            Document doc = Jsoup.parse(StringFromInputStream, "");
            in.close();

            {
              Elements screen_urls = doc.select("super_url");
              for (Element screen_url : screen_urls) {
                if (screen_url != null) {
                  MediaArtwork artwork = new MediaArtwork();
                  String url_boxart = screen_url.text();
                  artwork.setType(MediaArtworkType.POSTER);
                  artwork.addImageSize(100, 100, url_boxart);
                  artwork.setSizeOrder(PosterSizes.BIG.getOrder());
                  artwork.setDefaultUrl(url_boxart);
                  artwork.setPreviewUrl(url_boxart);
                  artworks.add(artwork);
                }
              }
            }
          }
          catch (IOException e) {
            LOGGER.error("Error parsing ");
            throw e;
          }
        }

          options.setArtworkType(MediaArtworkType.POSTER);
          break;

        case ACTOR:
          options.setArtworkType(MediaArtworkType.BACKGROUND);
          break;

        case BANNER:
          options.setArtworkType(MediaArtworkType.BANNER);
          break;

        case THUMB:
          options.setArtworkType(MediaArtworkType.BACKGROUND);
          break;

        default:

      }
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

    // 1. search with Id
    String gameId = options.get(MediaSearchOptions.SearchParam.GAMEID);
    if (StringUtils.isNotBlank(gameId)) {
      MediaScrapeOptions scrapeOptions = new MediaScrapeOptions();
      scrapeOptions.setLanguage(Globals.settings.getGameSettings().getScraperLanguage());
      scrapeOptions.setCountry(Globals.settings.getGameSettings().getCertificationCountry());
      scrapeOptions.setId(providerInfo.getId(), gameId);

      MediaMetadata metadata = getMetadata(scrapeOptions);

      MediaSearchResult sr = new MediaSearchResult(providerInfo.getId());
      sr.setMediaType(MediaType.GAME);
      sr.setMetadata(metadata);
      sr.setTitle(metadata.getStringValue(MediaMetadata.TITLE));
      sr.setPosterUrl(metadata.getStringValue(MediaMetadata.POSTER_URL));
      sr.setScore(1f);
      sr.setPlatform(options.get(MediaSearchOptions.SearchParam.PLATFORM));

      resultList.add(sr);
      return resultList;
    }

    // 2. search for search string
    String query = options.get(MediaSearchOptions.SearchParam.QUERY);
    if (StringUtils.isBlank(query)) {
      return resultList;
    }

    query = MetadataUtil.removeNonSearchCharacters(query);

    String platform = "All";
    if (StringUtils.isNotBlank(options.get(MediaSearchOptions.SearchParam.PLATFORM))) {
      platform = options.get(MediaSearchOptions.SearchParam.PLATFORM);
    }

    String searchString = BASE_URL + "/search/" + KEY + "&field_list=id,platforms,image,name,original_release_date" + FORMAT_XML + "&query="
        + URLEncoder.encode("\"" + query + "\"", "UTF-8");
    LOGGER.debug("search for everything: " + query);

    try {
      Url url = new CachedUrl(searchString);
      Document doc = Jsoup.parse(url.getInputStream(), "UTF-8", "");

      // parse games from response
      Elements games = doc.select("game");
      for (Element game : games) {
        MediaSearchResult result = new MediaSearchResult(providerInfo.getId());
        // id
        Element elem = game.select("id").first();
        if (elem != null) {
          result.setId(elem.text());
        }

        // name
        elem = game.select("name").first();
        if (elem != null) {
          result.setTitle(elem.text());
        }

        // poster url
        elem = game.select("small_url").first();
        if (elem != null) {
          result.setPosterUrl(elem.text());
        }

        // year
        elem = game.select("original_release_date").first();
        if (elem != null) {
          Pattern pattern = Pattern.compile("[0-9]{4}");
          Matcher matcher = pattern.matcher(elem.text());
          if (matcher.find()) {
            result.setYear(matcher.group().substring(0));
          }
          else {
            result.setYear("");
          }
        }

        // add a result for every platform
        Elements platforms = game.select("platform");
        if (platforms != null) {
          for (Element pl : platforms) {
            Element id = pl.select("id").first();
            if (id != null) {
              String ma = Platforms.getInstance().getPlatformNamebyMachineId(providerInfo.getId(), Integer.decode(id.text()));
              if (!"All".equalsIgnoreCase(platform) && !ma.equalsIgnoreCase(platform)) {
                continue;
              }
              else {
                result.setPlatform(ma);
                resultList.add(result);
              }
            }
          }
        }
        else {
          resultList.add(result);
        }
      }
    }
    catch (Exception e) {
      LOGGER.error("error getting result: " + e.getMessage());
    }

    Collections.sort(resultList);
    Collections.reverse(resultList);

    return resultList;
  }

  @Override
  public List<MediaTrailer> getTrailers(MediaScrapeOptions options) throws Exception {
    LOGGER.debug("getTrailers() " + options.toString());
    List<MediaTrailer> trailers = new ArrayList<MediaTrailer>();

    {
      String imdb = options.getImdbId();
      String url1 = BASE_URL + "/game/" + imdb + "/" + KEY + "&field_list=videos";
      LOGGER.debug("search trailer on giantbomb machine " + url1);

      Url url2;
      try {
        url2 = new CachedUrl(url1);
        InputStream in = url2.getInputStream();

        String StringFromInputStream = IOUtils.toString(in, "UTF-8");
        Document doc = Jsoup.parse(StringFromInputStream, "");
        in.close();

        Elements localvideos = doc.select("video");

        for (Element localvideo : localvideos) {
          {
            if (localvideo != null) {
              Element url = localvideo.select("video").first();

              if (url != null) {

                Element title = localvideo.select("title").first();
                String ltitle = "";
                if (title != null) {
                  Document doc2 = Jsoup.parse(title.text(), "");
                  Element dtitle = doc2.select("body").first();
                  if (dtitle != null) {
                    ltitle = dtitle.text();
                  }
                }
                Element id = localvideo.select("id").first();
                if (id != null) {

                  String video_url = BASE_URL + "/video/" + id.text() + "/" + KEY + "&field_list=hd_url,high_url,low_url,video_type";
                  LOGGER.debug("search trailer on giantbomb machine " + video_url);

                  try {
                    Url video_url2 = new CachedUrl(video_url);
                    InputStream in2 = video_url2.getInputStream();

                    String StringFromVideoInputStream = IOUtils.toString(in2, "UTF-8");
                    Document videodoc = Jsoup.parse(StringFromVideoInputStream, "");
                    in2.close();

                    {
                      Element hivideo = videodoc.select("high_url").first();
                      if (hivideo != null) {
                        MediaTrailer trailer = new MediaTrailer();

                        String text = hivideo.text();
                        trailer.setName(ltitle);
                        trailer.setQuality(640 + " (" + 360 + ")");
                        trailer.setProvider("giantbomb");
                        trailer.setUrl(text);
                        LOGGER.debug(trailer.toString());
                        trailers.add(trailer);
                      }
                    }
                    {
                      Element lowvideo = videodoc.select("low_url").first();
                      if (lowvideo != null) {
                        MediaTrailer trailer = new MediaTrailer();

                        String text = lowvideo.text();
                        trailer.setName(ltitle);
                        trailer.setQuality(320 + " (" + 180 + ")");
                        trailer.setProvider("giantbomb");
                        trailer.setUrl(text);
                        LOGGER.debug(trailer.toString());
                        trailers.add(trailer);
                      }
                    }
                  }
                  catch (IOException e) {
                    LOGGER.error("Error parsing ");
                  }
                }

              }
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
}
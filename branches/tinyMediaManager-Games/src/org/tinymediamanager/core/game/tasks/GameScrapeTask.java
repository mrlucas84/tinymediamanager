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
package org.tinymediamanager.core.game.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.TmmThreadPool;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.Message.MessageLevel;
import org.tinymediamanager.core.MessageManager;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.core.game.GameScraperMetadataConfig;
import org.tinymediamanager.core.game.GameSearchAndScrapeOptions;
import org.tinymediamanager.scraper.IMediaArtworkProvider;
import org.tinymediamanager.scraper.IMediaMetadataProvider;
import org.tinymediamanager.scraper.IMediaTrailerProvider;
import org.tinymediamanager.scraper.MediaArtwork;
import org.tinymediamanager.scraper.MediaArtwork.MediaArtworkType;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaProviderInfo;
import org.tinymediamanager.scraper.MediaScrapeOptions;
import org.tinymediamanager.scraper.MediaSearchResult;
import org.tinymediamanager.scraper.MediaTrailer;

/**
 * The Class GameScrapeTask.
 * 
 * @author Manuel Laggner
 */
public class GameScrapeTask extends TmmThreadPool {
  private final static Logger        LOGGER = LoggerFactory.getLogger(GameScrapeTask.class);

  private List<Game>                 gamesToScrape;
  private boolean                    doSearch;
  private boolean                    force;
  private GameSearchAndScrapeOptions options;

  /**
   * Instantiates a new game scrape task.
   * 
   * @param gamesToScrape
   *          the games to scrape
   * @param doSearch
   *          the do search
   * @param options
   *          the options
   */
  public GameScrapeTask(List<Game> gamesToScrape, boolean doSearch, GameSearchAndScrapeOptions options) {
    this.gamesToScrape = gamesToScrape;
    this.doSearch = doSearch;
    this.options = options;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @Override
  protected Void doInBackground() throws Exception {
    initThreadPool(3, "scrape");
    startProgressBar("scraping games", 0);

    for (int i = 0; i < gamesToScrape.size(); i++) {
      Game game = gamesToScrape.get(i);
      submitTask(new Worker(game));
    }
    waitForCompletionOrCancel();
    if (cancel) {
      cancel(false);// swing cancel
    }
    LOGGER.info("Done scraping games)");

    return null;
  }

  /**
   * Cancel.
   */
  public void cancel() {
    cancel = true;
    // cancel(false);
    // gamesToScrape.clear();
  }

  @Override
  public void done() {
    stopProgressBar();
  }

  /**
   * The Class Worker.
   */
  private class Worker implements Runnable {

    private GameList gameList;
    private Game     game;

    /**
     * Instantiates a new worker.
     */
    public Worker(Game game) {
      this.game = game;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
      try {
        gameList = GameList.getInstance();
        // set up scrapers
        GameScraperMetadataConfig scraperMetadataConfig = options.getScraperMetadataConfig();
        IMediaMetadataProvider mediaMetadataProvider = gameList.getMetadataProvider(options.getMetadataScraper());
        List<IMediaArtworkProvider> artworkProviders = gameList.getArtworkProviders(options.getArtworkScrapers());
        List<IMediaTrailerProvider> trailerProviders = gameList.getTrailerProviders(options.getTrailerScrapers());

        // search game
        MediaSearchResult result1 = null;
        if (doSearch) {
          List<MediaSearchResult> results = gameList.searchGame(true, game.getTitle(), game, options.getPlatform(), mediaMetadataProvider);
          if (results != null && !results.isEmpty()) {
            result1 = results.get(0);
            // check if there is an other result with 100% score
            if (results.size() > 1) {
              MediaSearchResult result2 = results.get(1);
              // if both results have 100% score - do not take any result
              if (result1.getScore() == 1 && result2.getScore() == 1) {
                LOGGER.info("two 100% results, can't decide whitch to take - ignore result");
                MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, game, "game.scrape.toosimilar"));
                return;
              }
            }

            // if there is only one result - we assume it is THE right game
            // else: get treshold from settings (default 0.75) - to minimize false positives
            if (results.size() == 1) {
              final double scraperTreshold = Globals.settings.getGameSettings().getScraperThreshold();
              LOGGER.info("using treshold from settings of {}", scraperTreshold);
              if (result1.getScore() < scraperTreshold) {
                LOGGER.info("score is lower than " + scraperTreshold + " (" + result1.getScore() + ") - ignore result");
                MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, game, "game.scrape.toolowscore"));
                return;
              }
            }
          }
          else {
            LOGGER.info("no result found for " + game.getTitle());
            MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, game, "game.scrape.nomatchfound"));
          }
        }

        // get metadata, artwork and trailers
        if ((doSearch && result1 != null) || !doSearch) {
          try {
            MediaScrapeOptions options = new MediaScrapeOptions();
            options.setResult(result1);
            options.setLanguage(Globals.settings.getGameSettings().getScraperLanguage());
            options.setCountry(Globals.settings.getGameSettings().getCertificationCountry());

            // we didn't do a search - pass imdbid and tmdbid from game
            // object
            if (!doSearch) {
              for (Entry<String, Object> entry : game.getIds().entrySet()) {
                options.setId(entry.getKey(), entry.getValue().toString());
              }
            }

            // scrape metadata if wanted
            MediaMetadata md = null;

            if (scraperMetadataConfig.isCast() || scraperMetadataConfig.isCertification() || scraperMetadataConfig.isGenres()
                || scraperMetadataConfig.isOriginalTitle() || scraperMetadataConfig.isPlot() || scraperMetadataConfig.isRating()
                || scraperMetadataConfig.isPublisher() || scraperMetadataConfig.isTagline() || scraperMetadataConfig.isTitle()
                || scraperMetadataConfig.isYear()) {

              MediaProviderInfo v = mediaMetadataProvider.getProviderInfo();

              if (result1 != null) {
                md = mediaMetadataProvider.getMetadata(options);
                game.setMetadata(md, scraperMetadataConfig);
              }
              else if (StringUtils.isNotBlank(game.getId(v.getId()).toString())) {
                MediaSearchResult result2 = new MediaSearchResult(v.getId());
                result2.setId(game.getId(v.getId()).toString());
                options.setResult(result2);

                md = mediaMetadataProvider.getMetadata(options);
                game.setMetadata(md, scraperMetadataConfig);
              }
            }

            // scrape artwork if wanted
            if (scraperMetadataConfig.isArtwork()) {

              MediaProviderInfo v = mediaMetadataProvider.getProviderInfo();

              if (result1 != null) {
                game.setArtwork(getArtwork(game, md, artworkProviders), scraperMetadataConfig);
              }
              else if (StringUtils.isNotBlank(game.getId(v.getId()).toString())) {
                game.setArtwork(getArtwork(game, md, artworkProviders), scraperMetadataConfig);
              }
            }

            // scrape trailer if wanted
            if (scraperMetadataConfig.isTrailer()) {

              MediaProviderInfo v = mediaMetadataProvider.getProviderInfo();

              if (result1 != null) {
                game.setTrailers(getTrailers(game, md, trailerProviders));
              }
              else if (StringUtils.isNotBlank(game.getId(v.getId()).toString())) {
                game.setTrailers(getTrailers(game, md, trailerProviders));
              }
            }
            game.writeNFO();
          }
          catch (Exception e) {
            LOGGER.error("game.setMetadata", e);
            MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, game, "message.scrape.metadatagamefailed"));
          }
        }
      }
      catch (Exception e) {
        LOGGER.error("Thread crashed", e);
        MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, "GameScraper", "message.scrape.threadcrashed"));
      }
    }

    /**
     * Gets the artwork.
     * 
     * @param game
     *          the game
     * @param metadata
     *          the metadata
     * @param artworkProviders
     *          the artwork providers
     * @return the artwork
     */
    public List<MediaArtwork> getArtwork(Game game, MediaMetadata metadata, List<IMediaArtworkProvider> artworkProviders) {
      List<MediaArtwork> artwork = null;

      MediaScrapeOptions options = new MediaScrapeOptions();
      options.setArtworkType(MediaArtworkType.ALL);
      options.setMetadata(metadata);
      options.setIds(game.getIds());
      options.setLanguage(Globals.settings.getGameSettings().getScraperLanguage());
      options.setCountry(Globals.settings.getGameSettings().getCertificationCountry());

      // scrape providers till one artwork has been found
      for (IMediaArtworkProvider artworkProvider : artworkProviders) {
        try {
          MediaProviderInfo v = artworkProvider.getProviderInfo();
          if (metadata.getProviderId().equalsIgnoreCase(v.getId())) {
            artwork = artworkProvider.getArtwork(options);
          }

        }
        catch (Exception e) {
          LOGGER.error("getArtwork", e);
          MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, game, "message.scrape.gameartworkfailed"));
          artwork = new ArrayList<MediaArtwork>();
        }
        // check if at least one artwork has been found
        if (artwork != null && artwork.size() > 0) {
          break;
        }
      }

      // initialize if null
      if (artwork == null) {
        artwork = new ArrayList<MediaArtwork>();
      }

      return artwork;
    }

    /**
     * Gets the trailers.
     * 
     * @param game
     *          the game
     * @param metadata
     *          the metadata
     * @param trailerProviders
     *          the trailer providers
     * @return the trailers
     */
    private List<MediaTrailer> getTrailers(Game game, MediaMetadata metadata, List<IMediaTrailerProvider> trailerProviders) {
      List<MediaTrailer> trailers = new ArrayList<MediaTrailer>();

      // add local trailers!
      for (MediaFile mf : game.getMediaFiles(MediaFileType.TRAILER)) {
        LOGGER.debug("adding local trailer " + mf.getFilename());
        MediaTrailer mt = new MediaTrailer();
        mt.setName(mf.getFilename());
        mt.setProvider("downloaded");
        mt.setQuality(mf.getVideoFormat());
        mt.setInNfo(false);
        mt.setUrl(mf.getFile().toURI().toString());
        trailers.add(mt);
      }

      MediaScrapeOptions options = new MediaScrapeOptions();
      options.setMetadata(metadata);
      options.setIds(game.getIds());
      options.setLanguage(Globals.settings.getGameSettings().getScraperLanguage());
      options.setCountry(Globals.settings.getGameSettings().getCertificationCountry());

      // scrape trailers
      for (IMediaTrailerProvider trailerProvider : trailerProviders) {
        try {
          MediaProviderInfo v = trailerProvider.getProviderInfo();
          if (metadata.getProviderId().equalsIgnoreCase(v.getId())) {
            List<MediaTrailer> foundTrailers = trailerProvider.getTrailers(options);
            trailers.addAll(foundTrailers);
          }
        }
        catch (Exception e) {
          LOGGER.error("getTrailers", e);
          MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, game, "message.scrape.gametrailerfailed"));
        }
      }

      return trailers;
    }
  }

  @Override
  public void callback(Object obj) {
    startProgressBar((String) obj, getTaskcount(), getTaskdone());
  }
}

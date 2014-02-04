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

import java.util.ArrayList;
import java.util.List;

import org.tinymediamanager.Globals;

/**
 * The Class GameSearchAndScrapeOptions.
 * 
 * @author masterlilou
 */
public class GameSearchAndScrapeOptions {
  private GameScraperMetadataConfig scraperMetadataConfig;
  private GameScrapers              metadataScraper;
  private List<GameArtworkScrapers> artworkScrapers = new ArrayList<GameArtworkScrapers>();
  private List<GameTrailerScrapers> trailerScrapers = new ArrayList<GameTrailerScrapers>();
  private String                    platform;

  /**
   * Instantiates a new game search and scrape config.
   */
  public GameSearchAndScrapeOptions() {
  }

  /**
   * Load default Settings.
   */
  public void loadDefaults() {
    scraperMetadataConfig = Globals.settings.getGameScraperMetadataConfig();
    // metadata
    metadataScraper = Globals.settings.getGameSettings().getGameScraper();

    // artwork
    if (Globals.settings.getGameSettings().isImageScraperGiantBomb()) {
      artworkScrapers.add(GameArtworkScrapers.GIANTBOMB);
    }

    if (Globals.settings.getGameSettings().isImageScraperGameDB()) {
      artworkScrapers.add(GameArtworkScrapers.GAMEDB);
    }

    if (Globals.settings.getGameSettings().isImageScraperJeuxVideo()) {
      artworkScrapers.add(GameArtworkScrapers.JEUXVIDEO);
    }

    // trailer
    if (Globals.settings.getGameSettings().isTrailerScraperGiantBomb()) {
      trailerScrapers.add(GameTrailerScrapers.GIANTBOMB);
    }

    if (Globals.settings.getGameSettings().isTrailerScraperGameDB()) {
      trailerScrapers.add(GameTrailerScrapers.GAMEDB);
    }

    if (Globals.settings.getGameSettings().isTrailerScraperJeuxVideo()) {
      trailerScrapers.add(GameTrailerScrapers.JEUXVIDEO);
    }
  }

  /**
   * Gets the scraper metadata config.
   * 
   * @return the scraper metadata config
   */
  public GameScraperMetadataConfig getScraperMetadataConfig() {
    return scraperMetadataConfig;
  }

  /**
   * Gets the metadata scraper.
   * 
   * @return the metadata scraper
   */
  public GameScrapers getMetadataScraper() {
    return metadataScraper;
  }

  /**
   * Gets the artwork scrapers.
   * 
   * @return the artwork scrapers
   */
  public List<GameArtworkScrapers> getArtworkScrapers() {
    return artworkScrapers;
  }

  /**
   * Gets the trailer scrapers.
   * 
   * @return the trailer scrapers
   */
  public List<GameTrailerScrapers> getTrailerScrapers() {
    return trailerScrapers;
  }

  /**
   * Sets the scraper metadata config.
   * 
   * @param scraperMetadataConfig
   *          the new scraper metadata config
   */
  public void setScraperMetadataConfig(GameScraperMetadataConfig scraperMetadataConfig) {
    this.scraperMetadataConfig = scraperMetadataConfig;
  }

  /**
   * Sets the metadata scraper.
   * 
   * @param metadataScraper
   *          the new metadata scraper
   */
  public void setMetadataScraper(GameScrapers metadataScraper) {
    this.metadataScraper = metadataScraper;
  }

  /**
   * Adds the artwork scraper.
   * 
   * @param artworkScraper
   *          the artwork scraper
   */
  public void addArtworkScraper(GameArtworkScrapers artworkScraper) {
    this.artworkScrapers.add(artworkScraper);
  }

  /**
   * Adds the trailer scraper.
   * 
   * @param trailerScraper
   *          the trailer scraper
   */
  public void addTrailerScraper(GameTrailerScrapers trailerScraper) {
    this.trailerScrapers.add(trailerScraper);
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

}

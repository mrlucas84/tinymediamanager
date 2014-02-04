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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.tinymediamanager.scraper.GameMediaGenres;
import org.tinymediamanager.scraper.IMediaArtworkProvider;
import org.tinymediamanager.scraper.IMediaMetadataProvider;
import org.tinymediamanager.scraper.MediaArtwork;
import org.tinymediamanager.scraper.MediaArtwork.MediaArtworkType;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaScrapeOptions;
import org.tinymediamanager.scraper.MediaType;

/**
 * @author Manuel Laggner
 * 
 */
public class TheGamesDbMetadataProviderTest {

  @Test
  public void testScrapeMetadata() {
    try {
      IMediaMetadataProvider mp = new TheGamesDbMetadataProvider();

      // Assassin's Creed: http://thegamesdb.net/api/GetGame.php?id=12
      MediaScrapeOptions options = new MediaScrapeOptions();
      options.setType(MediaType.GAME);
      options.setId("thegamesdb", "12");

      MediaMetadata md = mp.getMetadata(options);

      assertEquals("Assassin's Creed", md.getStringValue(MediaMetadata.TITLE));
      assertEquals("PC", md.getStringValue(MediaMetadata.PLATFORM));
      assertEquals("11-14-2007", md.getStringValue(MediaMetadata.RELEASE_DATE));
      assertEquals("2007", md.getStringValue(MediaMetadata.YEAR));
      assertEquals(
          "The game centers on the use of a machine named the \"Animus\", which allows the viewing of the protagonist's genetic memories of his ancestors. Through this plot device, details emerge of a struggle between two factions, the Knights Templar and the Assassins (Hashshashin), over an artifact known as a \"Piece of Eden\" and the game primarily takes place during the Third Crusade in the Holy Land in 1191.",
          md.getStringValue(MediaMetadata.PLOT));
      assertEquals("Ubisoft", md.getStringValue(MediaMetadata.PUBLISHER));
      assertEquals("Ubisoft Montreal", md.getStringValue(MediaMetadata.PRODUCTION_COMPANY));
      assertEquals(new Float(7.6f), md.getFloatValue(MediaMetadata.RATING));

      GameMediaGenres genre = md.getGameGenres().get(0);
      assertEquals(genre, GameMediaGenres.ACTION);
      genre = md.getGameGenres().get(1);
      assertEquals(genre, GameMediaGenres.ADVENTURE);
      // sandbox missing?
    }
    catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testScrapeArtwork() {
    try {
      IMediaArtworkProvider mp = new TheGamesDbMetadataProvider();

      // Assassin's Creed: http://thegamesdb.net/api/GetGame.php?id=12
      MediaScrapeOptions options = new MediaScrapeOptions();
      options.setType(MediaType.GAME);
      options.setId("thegamesdb", "12");
      options.setArtworkType(MediaArtworkType.ALL);

      List<MediaArtwork> artworks = mp.getArtwork(options);

      MediaArtwork artwork = artworks.get(0);
      assertEquals("http://thegamesdb.net/banners/fanart/original/12-1.jpg", artwork.getDefaultUrl());
      assertEquals("http://thegamesdb.net/banners/fanart/thumb/12-1.jpg", artwork.getPreviewUrl());
      assertEquals(1920, artwork.getImageSizes().get(0).getWidth());
      assertEquals(1080, artwork.getImageSizes().get(0).getHeight());

      artwork = artworks.get(6);
      assertEquals("http://thegamesdb.net/banners/boxart/original/front/12-1.jpg", artwork.getDefaultUrl());
      assertEquals("http://thegamesdb.net/banners/boxart/thumb/original/front/12-1.jpg", artwork.getPreviewUrl());
      assertEquals(1532, artwork.getImageSizes().get(0).getWidth());
      assertEquals(2176, artwork.getImageSizes().get(0).getHeight());

    }
    catch (Exception e) {
      fail();
    }
  }
}

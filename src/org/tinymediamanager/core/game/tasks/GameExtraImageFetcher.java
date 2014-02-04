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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.Message.MessageLevel;
import org.tinymediamanager.core.MessageManager;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.scraper.MediaArtwork.MediaArtworkType;

/**
 * The Class GameExtraImageFetcher.
 * 
 * @author Manuel Laggner
 */
public class GameExtraImageFetcher implements Runnable {

  /** The Constant LOGGER. */
  private final static Logger LOGGER = LoggerFactory.getLogger(GameExtraImageFetcher.class);

  /** The game. */
  private Game                game;

  /** The extrafanart. */
  private boolean             extrafanart;

  /** The extrathumbs. */
  private boolean             extrathumbs;

  /**
   * Instantiates a new game extra image fetcher.
   * 
   * @param game
   *          the game
   * @param extrafanart
   *          the extrafanart
   * @param extrathumbs
   *          the extrathumbs
   */
  public GameExtraImageFetcher(Game game, boolean extrafanart, boolean extrathumbs) {
    this.game = game;
    this.extrafanart = extrafanart;
    this.extrathumbs = extrathumbs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    // try/catch block in the root of the thread to log crashes
    try {
      if (!game.isMultiGameDir()) {
        // download extrathumbs
        if (extrathumbs) {
          game.downloadExtraThumbs(new ArrayList<String>(game.getExtraThumbs()));
        }

        // download extrafanart
        if (extrafanart) {
          game.downloadExtraFanarts(new ArrayList<String>(game.getExtraFanarts()));
        }

        game.saveToDb();
        game.callbackForWrittenArtwork(MediaArtworkType.ALL);
      }
    }
    catch (Exception e) {
      LOGGER.error("Thread crashed: ", e);
      MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, game, "message.extraimage.threadcrashed"));
    }
  }
}

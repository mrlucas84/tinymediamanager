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
package org.tinymediamanager.ui.games;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.scraper.GameMediaGenres;

/**
 * The Class GameGenresPanel.
 * 
 * @author Manuel Laggner
 */
public class GameGenresPanel extends JPanel {

  /** The Constant serialVersionUID. */
  private static final long   serialVersionUID = -6585642654072040266L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER           = LoggerFactory.getLogger(GameGenresPanel.class);

  /** The model. */
  private GameSelectionModel  gameSelectionModel;

  /**
   * Instantiates a new game genres panel.
   * 
   * @param model
   *          the model
   */
  public GameGenresPanel(GameSelectionModel model) {
    this.gameSelectionModel = model;
    setOpaque(false);

    // install the propertychangelistener
    PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String property = propertyChangeEvent.getPropertyName();
        Object source = propertyChangeEvent.getSource();

        // react on selection of a game or change of genres
        if ((source.getClass() == GameSelectionModel.class && "selectedGame".equals(property))
            || (source.getClass() == Game.class && "genre".equals(property))) {
          buildImages();
        }
      }
    };

    gameSelectionModel.addPropertyChangeListener(propertyChangeListener);
  }

  /**
   * Builds the images.
   */
  private void buildImages() {
    removeAll();
    List<GameMediaGenres> genres = gameSelectionModel.getSelectedGame().getGenres();
    for (GameMediaGenres genre : genres) {
      try {
        StringBuilder sb = new StringBuilder("/images/gamegenres/");
        sb.append(genre.name().toLowerCase());
        sb.append(".png");
        Icon image = new ImageIcon(GameGenresPanel.class.getResource(sb.toString()));
        JLabel lblImage = new JLabel(image);
        add(lblImage);
      }
      catch (NullPointerException e) {
        LOGGER.warn("genre image for genre " + genre.name() + " not available");
      }
      catch (Exception e) {
        LOGGER.warn(e.getMessage());
      }
    }
    // add unknown if there is no genre
    if (genres == null || genres.size() == 0) {
      try {
        Icon image = new ImageIcon(GameGenresPanel.class.getResource("/images/gamegenres/unknown.png"));
        JLabel lblImage = new JLabel(image);
        add(lblImage);
      }
      catch (Exception e) {
        LOGGER.warn(e.getMessage());
      }
    }
  }

}

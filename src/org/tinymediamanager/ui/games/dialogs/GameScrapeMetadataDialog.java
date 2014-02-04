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
package org.tinymediamanager.ui.games.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.tinymediamanager.Globals;
import org.tinymediamanager.core.game.GameArtworkScrapers;
import org.tinymediamanager.core.game.GameScraperMetadataConfig;
import org.tinymediamanager.core.game.GameScrapers;
import org.tinymediamanager.core.game.GameSearchAndScrapeOptions;
import org.tinymediamanager.core.game.GameTrailerScrapers;
import org.tinymediamanager.ui.EqualsLayout;
import org.tinymediamanager.ui.TmmWindowSaver;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.games.GameScraperMetadataPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameScrapeMetadataDialog.
 * 
 * @author Manuel Laggner
 */
public class GameScrapeMetadataDialog extends JDialog {

  /** The Constant BUNDLE. */
  private static final ResourceBundle BUNDLE                    = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  /** The Constant serialVersionUID. */
  private static final long           serialVersionUID          = 3826984454317979241L;

  /** The game search and scrape config. */
  private GameSearchAndScrapeOptions  gameSearchAndScrapeConfig = new GameSearchAndScrapeOptions();

  /** The cb metadata scraper. */
  private JComboBox                   cbMetadataScraper;

  /** The chckbx the GiantBomb */
  private JCheckBox                   chckbxscGiantBomb;

  /** The chckbx GameDB. */
  private JCheckBox                   chckbxscGameDB;

  /** The chckbx ofdbde. */
  private JCheckBox                   chckbxscJeuxVideo;

  /** The chckbx the GiantBomb */
  private JCheckBox                   chckbxtGiantBomb;

  /** The chckbx GameDB. */
  private JCheckBox                   chckbxtGameDB;

  /** The chckbx ofdbde. */
  private JCheckBox                   chckbxtJeuxVideo;

  private JComboBox                   cbMachine;

  /** The start scrape. */
  private boolean                     startScrape               = false;

  /**
   * Instantiates a new game scrape metadata.
   * 
   * @param title
   *          the title
   */
  public GameScrapeMetadataDialog(String title) {
    setTitle(title);
    setName("updateMetadata");
    setBounds(5, 5, 550, 280);
    setMinimumSize(new Dimension(getWidth(), getHeight()));
    TmmWindowSaver.loadSettings(this);
    setIconImage(Globals.logo);
    setModal(true);

    // copy the values
    GameScraperMetadataConfig settings = Globals.settings.getGameScraperMetadataConfig();

    GameScraperMetadataConfig scraperMetadataConfig = new GameScraperMetadataConfig();
    scraperMetadataConfig.setTitle(settings.isTitle());
    scraperMetadataConfig.setOriginalTitle(settings.isOriginalTitle());
    scraperMetadataConfig.setTagline(settings.isTagline());
    scraperMetadataConfig.setPlot(settings.isPlot());
    scraperMetadataConfig.setRating(settings.isRating());
    scraperMetadataConfig.setPublisher(settings.isPublisher());
    scraperMetadataConfig.setYear(settings.isYear());
    scraperMetadataConfig.setCertification(settings.isCertification());
    scraperMetadataConfig.setCast(settings.isCast());
    scraperMetadataConfig.setGenres(settings.isGenres());
    scraperMetadataConfig.setArtwork(settings.isArtwork());
    scraperMetadataConfig.setTrailer(settings.isTrailer());

    gameSearchAndScrapeConfig.setScraperMetadataConfig(scraperMetadataConfig);

    JPanel panelContent = new JPanel();
    getContentPane().add(panelContent, BorderLayout.CENTER);
    panelContent.setLayout(new BorderLayout(0, 0));

    JPanel panelScraper = new JPanel();
    panelContent.add(panelScraper, BorderLayout.NORTH);
    panelScraper.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC,
        FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    JLabel lblMetadataScraperT = new JLabel(BUNDLE.getString("scraper.metadata")); //$NON-NLS-1$
    panelScraper.add(lblMetadataScraperT, "2, 1, right, default");

    cbMetadataScraper = new JComboBox(GameScrapers.values());
    panelScraper.add(cbMetadataScraper, "4, 1, 3, 1, fill, default");

    {
      JLabel lblScraper = new JLabel(BUNDLE.getString("platform")); //$NON-NLS-1$
      panelScraper.add(lblScraper, "6, 1, right, default");
    }
    {
      cbMachine = new JComboBox();
      cbMachine.addItem("All");
      for (String p : Globals.settings.getGameSettings().getPlatformGame()) {
        cbMachine.addItem((String) p);
      }
      cbMachine.setSelectedItem(0);
      panelScraper.add(cbMachine, "8, 1, fill, default");
    }

    JLabel lblArtworkScraper = new JLabel(BUNDLE.getString("scraper.artwork")); //$NON-NLS-1$
    panelScraper.add(lblArtworkScraper, "2, 3, right, default");

    chckbxscGiantBomb = new JCheckBox("GiantBomb");
    panelScraper.add(chckbxscGiantBomb, "4, 3");

    chckbxscGameDB = new JCheckBox("TheGameDB");
    panelScraper.add(chckbxscGameDB, "6, 3");

    chckbxscJeuxVideo = new JCheckBox("jeuxvideo.com");
    panelScraper.add(chckbxscJeuxVideo, "8, 3");

    JLabel lblTrailerScraper = new JLabel(BUNDLE.getString("scraper.trailer")); //$NON-NLS-1$
    panelScraper.add(lblTrailerScraper, "2, 5, right, default");

    chckbxtGiantBomb = new JCheckBox("GiantBomb");
    panelScraper.add(chckbxtGiantBomb, "4, 5");

    chckbxtGameDB = new JCheckBox("TheGameDB");
    panelScraper.add(chckbxtGameDB, "6, 5");

    chckbxtJeuxVideo = new JCheckBox("jeuxvideo.com");
    panelScraper.add(chckbxtJeuxVideo, "8, 5");

    {
      JPanel panelCenter = new JPanel();
      panelContent.add(panelCenter, BorderLayout.CENTER);
      panelCenter.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"),
          FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"),
          FormFactory.RELATED_GAP_ROWSPEC, }));

      JPanel panelScraperMetadataSetting = new GameScraperMetadataPanel(this.gameSearchAndScrapeConfig.getScraperMetadataConfig());
      panelScraperMetadataSetting.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), BUNDLE.getString("scraper.metadata.select"),
          TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$,
      panelCenter.add(panelScraperMetadataSetting, "2, 2");
    }

    JPanel panelButtons = new JPanel();
    panelButtons.setLayout(new EqualsLayout(5));
    panelContent.add(panelButtons, BorderLayout.SOUTH);

    JButton btnStart = new JButton(BUNDLE.getString("scraper.start")); //$NON-NLS-1$
    btnStart.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        startScrape = true;
        setVisible(false);
      }
    });
    panelButtons.add(btnStart);

    JButton btnCancel = new JButton(BUNDLE.getString("Button.cancel")); //$NON-NLS-1$
    btnCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        startScrape = false;
        setVisible(false);
      }
    });
    panelButtons.add(btnCancel);

    // set data

    // metadataprovider
    GameScrapers defaultScraper = Globals.settings.getGameSettings().getGameScraper();

    cbMetadataScraper.setSelectedItem(defaultScraper);

    // artwork provider
    if (Globals.settings.getGameSettings().isImageScraperGiantBomb()) {
      chckbxscGiantBomb.setSelected(true);
    }

    if (Globals.settings.getGameSettings().isImageScraperGameDB()) {
      chckbxscGameDB.setSelected(true);
    }

    if (Globals.settings.getGameSettings().isImageScraperJeuxVideo()) {
      chckbxscJeuxVideo.setSelected(true);
    }

    // trailer provider
    if (Globals.settings.getGameSettings().isTrailerScraperGiantBomb()) {
      chckbxtGiantBomb.setSelected(true);
    }

    if (Globals.settings.getGameSettings().isTrailerScraperGameDB()) {
      chckbxtGameDB.setSelected(true);
    }

    if (Globals.settings.getGameSettings().isTrailerScraperJeuxVideo()) {
      chckbxtJeuxVideo.setSelected(true);
    }
  }

  /**
   * Pass the game search and scrape config to the caller.
   * 
   * @return the game search and scrape config
   */
  public GameSearchAndScrapeOptions getGameSearchAndScrapeConfig() {
    // metadata provider
    gameSearchAndScrapeConfig.setMetadataScraper((GameScrapers) cbMetadataScraper.getSelectedItem());

    gameSearchAndScrapeConfig.setPlatform((String) cbMachine.getSelectedItem().toString());
    // artwork provider
    if (chckbxscGiantBomb.isSelected()) {
      gameSearchAndScrapeConfig.addArtworkScraper(GameArtworkScrapers.GIANTBOMB);
    }

    if (chckbxscGameDB.isSelected()) {
      gameSearchAndScrapeConfig.addArtworkScraper(GameArtworkScrapers.GAMEDB);
    }

    if (chckbxscJeuxVideo.isSelected()) {
      gameSearchAndScrapeConfig.addArtworkScraper(GameArtworkScrapers.JEUXVIDEO);
    }

    // tailer provider
    if (chckbxtGiantBomb.isSelected()) {
      gameSearchAndScrapeConfig.addTrailerScraper(GameTrailerScrapers.GIANTBOMB);
    }

    if (chckbxtGameDB.isSelected()) {
      gameSearchAndScrapeConfig.addTrailerScraper(GameTrailerScrapers.GAMEDB);
    }

    if (chckbxtJeuxVideo.isSelected()) {
      gameSearchAndScrapeConfig.addTrailerScraper(GameTrailerScrapers.JEUXVIDEO);
    }

    return gameSearchAndScrapeConfig;
  }

  /**
   * Should start scrape.
   * 
   * @return true, if successful
   */
  public boolean shouldStartScrape() {
    return startScrape;
  }
}

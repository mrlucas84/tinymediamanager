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
package org.tinymediamanager.ui.games.settings;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.tinymediamanager.core.Settings;
import org.tinymediamanager.core.game.GameFanartNaming;
import org.tinymediamanager.core.game.GamePosterNaming;
import org.tinymediamanager.scraper.MediaArtwork.FanartSizes;
import org.tinymediamanager.scraper.MediaArtwork.PosterSizes;
import org.tinymediamanager.ui.TmmUIHelper;
import org.tinymediamanager.ui.UTF8Control;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameImageSettingsPanel.
 * 
 * @author Manuel Laggner
 */
public class GameImageSettingsPanel extends JPanel {

  /** The Constant serialVersionUID. */
  private static final long           serialVersionUID = 7312645402037806284L;

  /** The Constant BUNDLE. */
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  /** The settings. */
  private Settings                    settings         = Settings.getInstance();

  /** The cb image tmdb poster size. */
  private JComboBox                   cbImagePosterSize;

  /** The cb image tmdb fanart size. */
  private JComboBox                   cbImageFanartSize;

  /** The cb game poster filename2. */
  private JCheckBox                   cbGamePosterFilename2;

  /** The cb game poster filename4. */
  private JCheckBox                   cbGamePosterFilename4;

  /** The cb game poster filename6. */
  private JCheckBox                   cbGamePosterFilename6;

  /** The cb game poster filename7. */
  private JCheckBox                   cbGamePosterFilename7;

  /** The cb game fanart filename1. */
  private JCheckBox                   cbGameFanartFilename1;

  /** The cb game fanart filename2. */
  private JCheckBox                   cbGameFanartFilename2;

  /** The cb game poster filename8. */
  private JCheckBox                   cbGamePosterFilename8;

  /** The chckbx fanarttv. */
  private JCheckBox                   chckbxGameDB;

  /** The chckbx the game database. */
  private JCheckBox                   chckbxGiantBomb;

  /** The chckbx the game database. */
  private JCheckBox                   chckbxJeuxVideo;

  /** The lbl attention fanart tv. */
  private JLabel                      lblAttentionFanartTv;

  /** The panel actor thumbs. */
  private JPanel                      panelActorThumbs;

  /** The cb actor images. */
  private JCheckBox                   cbActorImages;

  /** The tp file naming hint. */
  private JTextPane                   tpFileNamingHint;

  /** The chckbx enable extrathumbs. */
  private JCheckBox                   chckbxEnableExtrathumbs;

  /** The chckbx enable extrafanart. */
  private JCheckBox                   chckbxEnableExtrafanart;

  /** The separator. */
  private JSeparator                  separator;

  /** The separator_1. */
  private JSeparator                  separator_1;

  /** The chckbx resize extrathumbs to. */
  private JCheckBox                   chckbxResizeExtrathumbsTo;

  /** The sp extrathumb width. */
  private JSpinner                    spExtrathumbWidth;

  /** The lbl download. */
  private JLabel                      lblDownload;

  /** The sp download count extrathumbs. */
  private JSpinner                    spDownloadCountExtrathumbs;

  /** The lbl download count. */
  private JLabel                      lblDownloadCount;

  /** The sp download count extrafanart. */
  private JSpinner                    spDownloadCountExtrafanart;

  /** The panel. */
  private JPanel                      panel;

  /** The chckbx store gameset artwork. */
  private JCheckBox                   chckbxStoreGamesetArtwork;

  /** The tf game set artwork folder. */
  private JTextField                  tfGameSetArtworkFolder;

  /** The lbl foldername. */
  private JLabel                      lblFoldername;

  /** The btn select folder. */
  private JButton                     btnSelectFolder;

  /** The separator_2. */
  private JSeparator                  separator_2;
  private JCheckBox                   cbGameFanartFilename3;

  /**
   * Instantiates a new game image settings panel.
   */
  public GameImageSettingsPanel() {
    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    JPanel panelGameImages = new JPanel();
    panelGameImages.setBorder(new TitledBorder(null, BUNDLE.getString("Settings.poster"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
    add(panelGameImages, "2, 2, left, top");
    panelGameImages.setLayout(new FormLayout(new ColumnSpec[] { //
        FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, //
            FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, //
            FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, //
        new RowSpec[] {
            FormFactory.RELATED_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC, //
            FormFactory.NARROW_LINE_GAP_ROWSPEC, //
            FormFactory.DEFAULT_ROWSPEC, //
            FormFactory.NARROW_LINE_GAP_ROWSPEC, //
            FormFactory.DEFAULT_ROWSPEC, //
            FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.UNRELATED_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    JLabel lblSource = new JLabel(BUNDLE.getString("Settings.source")); //$NON-NLS-1$
    panelGameImages.add(lblSource, "2, 2");

    chckbxGiantBomb = new JCheckBox("GiantBomb");
    chckbxGiantBomb.setSelected(true);
    panelGameImages.add(chckbxGiantBomb, "4, 2");

    chckbxGameDB = new JCheckBox("TheGameDB");
    chckbxGameDB.setSelected(true);
    panelGameImages.add(chckbxGameDB, "4, 4");

    chckbxJeuxVideo = new JCheckBox("jeuxvideo.com");
    chckbxJeuxVideo.setSelected(true);
    panelGameImages.add(chckbxJeuxVideo, "4, 6");

    lblAttentionFanartTv = new JLabel(BUNDLE.getString("Settings.GiantBomb.alert")); //$NON-NLS-1$
    lblAttentionFanartTv.setFont(new Font("Dialog", Font.PLAIN, 10));
    panelGameImages.add(lblAttentionFanartTv, "4, 7, 3, 1");

    separator = new JSeparator();
    panelGameImages.add(separator, "1, 9, 6, 1");

    JLabel lblImageTmdbPosterSize = new JLabel(BUNDLE.getString("image.poster.size"));
    panelGameImages.add(lblImageTmdbPosterSize, "2, 11");

    cbImagePosterSize = new JComboBox(PosterSizes.values());
    panelGameImages.add(cbImagePosterSize, "4, 11");

    JLabel lblImageTmdbFanartSize = new JLabel(BUNDLE.getString("image.fanart.size"));
    panelGameImages.add(lblImageTmdbFanartSize, "2, 13");

    cbImageFanartSize = new JComboBox(FanartSizes.values());
    panelGameImages.add(cbImageFanartSize, "4, 13");

    separator_2 = new JSeparator();
    panelGameImages.add(separator_2, "1, 15, 6, 1");

    JLabel lblPosterFilename = new JLabel(BUNDLE.getString("image.poster.naming")); //$NON-NLS-1$
    panelGameImages.add(lblPosterFilename, "2, 17");

    cbGamePosterFilename7 = new JCheckBox("<dynamic>.ext"); //$NON-NLS-1$
    panelGameImages.add(cbGamePosterFilename7, "4, 17");

    cbGamePosterFilename4 = new JCheckBox("poster.ext");
    panelGameImages.add(cbGamePosterFilename4, "6, 17");

    cbGamePosterFilename8 = new JCheckBox("<dynamic>-poster.ext"); //$NON-NLS-1$
    panelGameImages.add(cbGamePosterFilename8, "4, 18");

    cbGamePosterFilename6 = new JCheckBox("folder.ext");
    panelGameImages.add(cbGamePosterFilename6, "6, 18");

    cbGamePosterFilename2 = new JCheckBox("game.ext");
    panelGameImages.add(cbGamePosterFilename2, "4, 19");

    JLabel lblFanartFileNaming = new JLabel(BUNDLE.getString("image.fanart.naming")); //$NON-NLS-1$
    panelGameImages.add(lblFanartFileNaming, "2, 21");

    cbGameFanartFilename1 = new JCheckBox("<dynamic>-fanart.ext"); //$NON-NLS-1$
    panelGameImages.add(cbGameFanartFilename1, "4, 21");

    cbGameFanartFilename3 = new JCheckBox("<dynamic>.fanart.ext");//$NON-NLS-1$
    panelGameImages.add(cbGameFanartFilename3, "6, 21");

    cbGameFanartFilename2 = new JCheckBox("fanart.ext");
    panelGameImages.add(cbGameFanartFilename2, "4, 22");

    tpFileNamingHint = new JTextPane();
    tpFileNamingHint.setText(BUNDLE.getString("Settings.naming.info")); //$NON-NLS-1$
    tpFileNamingHint.setBackground(UIManager.getColor("Panel.background"));
    tpFileNamingHint.setFont(new Font("Dialog", Font.PLAIN, 10));
    panelGameImages.add(tpFileNamingHint, "2, 24, 5, 1, fill, fill");

    separator_1 = new JSeparator();
    panelGameImages.add(separator_1, "1, 26, 6, 1");

    chckbxEnableExtrathumbs = new JCheckBox(BUNDLE.getString("Settings.enable.extrathumbs")); //$NON-NLS-1$
    panelGameImages.add(chckbxEnableExtrathumbs, "2, 28");

    chckbxResizeExtrathumbsTo = new JCheckBox(BUNDLE.getString("Settings.resize.extrathumbs")); //$NON-NLS-1$
    panelGameImages.add(chckbxResizeExtrathumbsTo, "4, 28");

    spExtrathumbWidth = new JSpinner();
    spExtrathumbWidth.setPreferredSize(new Dimension(49, 20));
    panelGameImages.add(spExtrathumbWidth, "6, 28, left, default");

    lblDownload = new JLabel(BUNDLE.getString("Settings.amount.autodownload")); //$NON-NLS-1$
    panelGameImages.add(lblDownload, "2, 29, 3, 1, right, default");

    spDownloadCountExtrathumbs = new JSpinner();
    spDownloadCountExtrathumbs.setPreferredSize(new Dimension(49, 20));
    panelGameImages.add(spDownloadCountExtrathumbs, "6, 29, left, default");

    chckbxEnableExtrafanart = new JCheckBox(BUNDLE.getString("Settings.enable.extrafanart")); //$NON-NLS-1$
    panelGameImages.add(chckbxEnableExtrafanart, "2, 31");

    lblDownloadCount = new JLabel(BUNDLE.getString("Settings.amount.autodownload")); //$NON-NLS-1$
    panelGameImages.add(lblDownloadCount, "2, 32, 3, 1, right, default");

    spDownloadCountExtrafanart = new JSpinner();
    spDownloadCountExtrafanart.setPreferredSize(new Dimension(49, 20));
    panelGameImages.add(spDownloadCountExtrafanart, "6, 32, left, default");

    panelActorThumbs = new JPanel();
    panelActorThumbs.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), BUNDLE.getString("Settings.actor"), TitledBorder.LEADING,
        TitledBorder.TOP, null, null)); //$NON-NLS-1$
    add(panelActorThumbs, "2, 4");
    panelActorThumbs.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    cbActorImages = new JCheckBox(BUNDLE.getString("Settings.actor.download")); //$NON-NLS-1$
    panelActorThumbs.add(cbActorImages, "2, 2");

    panel = new JPanel();
    panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), BUNDLE.getString("Settings.gameset"), TitledBorder.LEADING,
        TitledBorder.TOP, null, null)); //$NON-NLS-1$
    add(panel, "2, 6, fill, fill");
    panel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    chckbxStoreGamesetArtwork = new JCheckBox(BUNDLE.getString("Settings.gameset.store")); //$NON-NLS-1$
    panel.add(chckbxStoreGamesetArtwork, "2, 2, 3, 1");

    lblFoldername = new JLabel(BUNDLE.getString("Settings.gameset.foldername")); //$NON-NLS-1$
    panel.add(lblFoldername, "2, 4, right, default");

    tfGameSetArtworkFolder = new JTextField();
    panel.add(tfGameSetArtworkFolder, "4, 4, fill, default");
    tfGameSetArtworkFolder.setColumns(10);

    btnSelectFolder = new JButton(BUNDLE.getString("Settings.gameset.buttonselect")); //$NON-NLS-1$
    btnSelectFolder.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        File file = TmmUIHelper.selectDirectory(BUNDLE.getString("Settings.gameset.folderchooser")); //$NON-NLS-1$
        if (file != null && file.exists() && file.isDirectory()) {
          tfGameSetArtworkFolder.setText(file.getAbsolutePath());
        }
      }
    });
    panel.add(btnSelectFolder, "6, 4");

    initDataBindings();

    // poster filenames
    List<GamePosterNaming> gamePosterFilenames = settings.getGameSettings().getGamePosterFilenames();
    if (gamePosterFilenames.contains(GamePosterNaming.GAME_JPG)) {
      cbGamePosterFilename2.setSelected(true);
    }
    if (gamePosterFilenames.contains(GamePosterNaming.POSTER_JPG)) {
      cbGamePosterFilename4.setSelected(true);
    }
    if (gamePosterFilenames.contains(GamePosterNaming.FOLDER_JPG)) {
      cbGamePosterFilename6.setSelected(true);
    }
    if (gamePosterFilenames.contains(GamePosterNaming.GAMENAME_JPG)) {
      cbGamePosterFilename7.setSelected(true);
    }
    if (gamePosterFilenames.contains(GamePosterNaming.FILENAME_POSTER_JPG)) {
      cbGamePosterFilename8.setSelected(true);
    }

    // fanart filenames
    List<GameFanartNaming> gameFanartFilenames = settings.getGameSettings().getGameFanartFilenames();
    if (gameFanartFilenames.contains(GameFanartNaming.FILENAME_FANART_JPG)) {
      cbGameFanartFilename1.setSelected(true);
    }
    if (gameFanartFilenames.contains(GameFanartNaming.FANART_JPG)) {
      cbGameFanartFilename2.setSelected(true);
    }
    if (gameFanartFilenames.contains(GameFanartNaming.FILENAME_FANART2_JPG)) {
      cbGameFanartFilename3.setSelected(true);
    }

    // listen to changes of the checkboxes
    ItemListener listener = new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        checkChanges();
      }
    };
    cbGamePosterFilename2.addItemListener(listener);
    cbGamePosterFilename4.addItemListener(listener);
    cbGamePosterFilename6.addItemListener(listener);
    cbGamePosterFilename7.addItemListener(listener);
    cbGamePosterFilename8.addItemListener(listener);

    cbGameFanartFilename1.addItemListener(listener);
    cbGameFanartFilename2.addItemListener(listener);
    cbGameFanartFilename3.addItemListener(listener);
  }

  /**
   * Inits the data bindings.
   */
  protected void initDataBindings() {
    BeanProperty<Settings, PosterSizes> settingsBeanProperty_5 = BeanProperty.create("gameSettings.imagePosterSize");
    BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
    AutoBinding<Settings, PosterSizes, JComboBox, Object> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_5, cbImagePosterSize, jComboBoxBeanProperty);
    autoBinding_4.bind();
    //
    BeanProperty<Settings, FanartSizes> settingsBeanProperty_6 = BeanProperty.create("gameSettings.imageFanartSize");
    AutoBinding<Settings, FanartSizes, JComboBox, Object> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_6, cbImageFanartSize, jComboBoxBeanProperty);
    autoBinding_5.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty = BeanProperty.create("gameSettings.imageScraperGiantBomb");
    BeanProperty<JCheckBox, Boolean> jCheckBoxBeanProperty = BeanProperty.create("selected");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty, chckbxGiantBomb, jCheckBoxBeanProperty);
    autoBinding.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_1 = BeanProperty.create("gameSettings.imageScraperGameDB");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_1, chckbxGameDB, jCheckBoxBeanProperty);
    autoBinding_1.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_21 = BeanProperty.create("gameSettings.imageScraperJeuxVideo");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_21 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_21, chckbxJeuxVideo, jCheckBoxBeanProperty);
    autoBinding_21.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_2 = BeanProperty.create("gameSettings.writeActorImages");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_2, cbActorImages, jCheckBoxBeanProperty);
    autoBinding_2.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_3 = BeanProperty.create("gameSettings.imageExtraFanart");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_3, chckbxEnableExtrafanart, jCheckBoxBeanProperty);
    autoBinding_3.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_4 = BeanProperty.create("gameSettings.imageExtraThumbs");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_4, chckbxEnableExtrathumbs, jCheckBoxBeanProperty);
    autoBinding_7.bind();
    //
    BeanProperty<Settings, Integer> settingsBeanProperty_8 = BeanProperty.create("gameSettings.imageExtraThumbsSize");
    BeanProperty<JSpinner, Object> jSpinnerBeanProperty_1 = BeanProperty.create("value");
    AutoBinding<Settings, Integer, JSpinner, Object> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_8, spExtrathumbWidth, jSpinnerBeanProperty_1);
    autoBinding_10.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_9 = BeanProperty.create("gameSettings.imageExtraThumbsResize");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_11 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_9, chckbxResizeExtrathumbsTo, jCheckBoxBeanProperty);
    autoBinding_11.bind();
    //
    BeanProperty<Settings, Integer> settingsBeanProperty_10 = BeanProperty.create("gameSettings.imageExtraThumbsCount");
    AutoBinding<Settings, Integer, JSpinner, Object> autoBinding_12 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_10, spDownloadCountExtrathumbs, jSpinnerBeanProperty_1);
    autoBinding_12.bind();
    //
    BeanProperty<Settings, Integer> settingsBeanProperty_11 = BeanProperty.create("gameSettings.imageExtraFanartCount");
    AutoBinding<Settings, Integer, JSpinner, Object> autoBinding_13 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_11, spDownloadCountExtrafanart, jSpinnerBeanProperty_1);
    autoBinding_13.bind();
    //
    BeanProperty<JSpinner, Boolean> jSpinnerBeanProperty = BeanProperty.create("enabled");
    AutoBinding<JCheckBox, Boolean, JSpinner, Boolean> autoBinding_14 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
        chckbxEnableExtrafanart, jCheckBoxBeanProperty, spDownloadCountExtrafanart, jSpinnerBeanProperty);
    autoBinding_14.bind();
    //
    AutoBinding<JCheckBox, Boolean, JSpinner, Boolean> autoBinding_15 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
        chckbxEnableExtrathumbs, jCheckBoxBeanProperty, spDownloadCountExtrathumbs, jSpinnerBeanProperty);
    autoBinding_15.bind();
    //
    BeanProperty<Settings, String> settingsBeanProperty_12 = BeanProperty.create("gameSettings.gameSetArtworkFolder");
    BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
    AutoBinding<Settings, String, JTextField, String> autoBinding_16 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_12, tfGameSetArtworkFolder, jTextFieldBeanProperty);
    autoBinding_16.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_13 = BeanProperty.create("gameSettings.enableGameSetArtworkFolder");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_17 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_13, chckbxStoreGamesetArtwork, jCheckBoxBeanProperty);
    autoBinding_17.bind();
    //
    BeanProperty<JCheckBox, Boolean> jCheckBoxBeanProperty_1 = BeanProperty.create("enabled");
    AutoBinding<JCheckBox, Boolean, JCheckBox, Boolean> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
        chckbxEnableExtrathumbs, jCheckBoxBeanProperty, chckbxResizeExtrathumbsTo, jCheckBoxBeanProperty_1);
    autoBinding_8.bind();
    //
    AutoBinding<JCheckBox, Boolean, JSpinner, Boolean> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, chckbxEnableExtrathumbs,
        jCheckBoxBeanProperty, spExtrathumbWidth, jSpinnerBeanProperty);
    autoBinding_9.bind();
  }

  /**
   * Check changes.
   */
  public void checkChanges() {
    // set poster filenames
    settings.getGameSettings().clearGamePosterFilenames();

    if (cbGamePosterFilename2.isSelected()) {
      settings.getGameSettings().addGamePosterFilename(GamePosterNaming.GAME_JPG);
      settings.getGameSettings().addGamePosterFilename(GamePosterNaming.GAME_PNG);
    }
    if (cbGamePosterFilename4.isSelected()) {
      settings.getGameSettings().addGamePosterFilename(GamePosterNaming.POSTER_JPG);
      settings.getGameSettings().addGamePosterFilename(GamePosterNaming.POSTER_PNG);
    }
    if (cbGamePosterFilename6.isSelected()) {
      settings.getGameSettings().addGamePosterFilename(GamePosterNaming.FOLDER_JPG);
      settings.getGameSettings().addGamePosterFilename(GamePosterNaming.FOLDER_PNG);
    }
    if (cbGamePosterFilename7.isSelected()) {
      settings.getGameSettings().addGamePosterFilename(GamePosterNaming.GAMENAME_JPG);
      settings.getGameSettings().addGamePosterFilename(GamePosterNaming.GAMENAME_PNG);
    }
    if (cbGamePosterFilename8.isSelected()) {
      settings.getGameSettings().addGamePosterFilename(GamePosterNaming.FILENAME_POSTER_JPG);
      settings.getGameSettings().addGamePosterFilename(GamePosterNaming.FILENAME_POSTER_PNG);
    }

    // set fanart filenames
    settings.getGameSettings().clearGameFanartFilenames();
    if (cbGameFanartFilename1.isSelected()) {
      settings.getGameSettings().addGameFanartFilename(GameFanartNaming.FILENAME_FANART_JPG);
      settings.getGameSettings().addGameFanartFilename(GameFanartNaming.FILENAME_FANART_PNG);
    }
    if (cbGameFanartFilename2.isSelected()) {
      settings.getGameSettings().addGameFanartFilename(GameFanartNaming.FANART_JPG);
      settings.getGameSettings().addGameFanartFilename(GameFanartNaming.FANART_PNG);
    }
    if (cbGameFanartFilename3.isSelected()) {
      settings.getGameSettings().addGameFanartFilename(GameFanartNaming.FILENAME_FANART2_JPG);
      settings.getGameSettings().addGameFanartFilename(GameFanartNaming.FILENAME_FANART2_PNG);
    }
  }
}

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.Message.MessageLevel;
import org.tinymediamanager.core.MessageManager;
import org.tinymediamanager.core.game.RomCollectionConfig;
import org.tinymediamanager.ui.TmmUIHelper;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.LinkLabel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

/**
 * The Class GameDetailsPanel. country votes
 * 
 * @author Manuel Laggner
 */
public class GameDetailsPanel extends JPanel {

  /** The Constant BUNDLE. */
  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  /** The Constant serialVersionUID. */
  // private static final long serialVersionUID = 6273970118830324299L;

  /** The logger. */
  private final static Logger         LOGGER = LoggerFactory.getLogger(GameDetailsPanel.class);

  /** The game selection model. */
  private GameSelectionModel          gameSelectionModel;

  /** The lbl original title t. */
  private JLabel                      lblOriginalTitleT;

  /** The lbl original title. */
  private JLabel                      lblOriginalTitle;

  /** The lbl production t. */
  private JLabel                      lblDeveloperT;

  /** The lbl production. */
  private JLabel                      lblDeveloper;

  /** The lbl production t. */
  private JLabel                      lblPublisherT;

  /** The lbl production. */
  private JLabel                      lblPublisher;

  /** The lbl genres t. */
  private JLabel                      lblGenresT;

  /** The lbl genres. */
  private JLabel                      lblGenres;

  /** The lbl certification t. */
  private JLabel                      lblCertificationT;

  /** The lbl certification. */
  private JLabel                      lblCertification;

  /** The lbl imdb id t. */
  private JLabel                      lblImdbIdT;

  /** The lbl tmdb id t. */
  private JLabel                      lblTmdbIdT;

  /** The lbl imdb id. */
  private LinkLabel                   lblImdbId;

  /** The lbl tmdb id. */
  private LinkLabel                   lblTmdbId;

  /** The lbl tags t. */
  private JLabel                      lblTagsT;

  /** The lbl tags. */
  private JLabel                      lblTags;

  /** The lbl game path t. */
  private JLabel                      lblGamePathT;

  /** The lbl game path. */
  private LinkLabel                   lblGamePath;

  /** The lbl gameset t. */
  private JLabel                      lblGamesetT;

  /** The lbl game set. */
  private JLabel                      lblGameSet;

  /** The lbl spoken languages. */
  private JButton                     btnPlay;
  private JLabel                      lblPlatformT;
  private JLabel                      lblPlatform;
  private JLabel                      lblReleaseDateT;
  private JLabel                      lblReleaseDate;

  /** Favorite game */
  private JCheckBox                   chckbxIsFavorite;

  /**
   * Instantiates a new game details panel.
   * 
   * @param model
   *          the model
   */
  public GameDetailsPanel(GameSelectionModel model) {
    this.gameSelectionModel = model;

    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.UNRELATED_GAP_COLSPEC,
        ColumnSpec.decode("25px"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC,
        FormFactory.DEFAULT_COLSPEC, FormFactory.UNRELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("55px"), FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC,
        new RowSpec(RowSpec.CENTER, Sizes.bounded(Sizes.MINIMUM, Sizes.constant("15px", false), Sizes.constant("50px", false)), 0),
        FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    lblOriginalTitleT = new JLabel(BUNDLE.getString("metatag.originaltitle")); //$NON-NLS-1$
    add(lblOriginalTitleT, "2, 2");

    lblOriginalTitle = new JLabel("");
    add(lblOriginalTitle, "4, 2, 7, 1");

    btnPlay = new JButton("");
    btnPlay.setIcon(new ImageIcon(GameDetailsPanel.class.getResource("/org/tinymediamanager/ui/images/Play.png")));
    btnPlay.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        MediaFile mf = gameSelectionModel.getSelectedGame().getMediaFiles(MediaFileType.GAME).get(0);
        RomCollectionConfig exe = Globals.settings.getGameSettings().getromCollectionbyLongName(gameSelectionModel.getSelectedGame().getPlatform());
        if (exe != null) {
          try {
            TmmUIHelper.executeFile(exe.getEmulatorExecutable(), mf.getFile());
          }
          catch (Exception e) {
            MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, mf, "message.erroropenfile", new String[] { ":",
                e.getLocalizedMessage() }));
          }
        }
      }
    });
    add(btnPlay, "12, 2, 1, 5");

    lblGenresT = new JLabel(BUNDLE.getString("metatag.genre")); //$NON-NLS-1$
    add(lblGenresT, "2, 4");
    lblGenresT.setLabelFor(lblGenres);

    lblGenres = new JLabel("");
    add(lblGenres, "4, 4, 7, 1");

    lblReleaseDateT = new JLabel(BUNDLE.getString("metatag.releasedate")); //$NON-NLS-1$
    add(lblReleaseDateT, "2, 6"); // 2

    lblReleaseDate = new JLabel("");
    add(lblReleaseDate, "4, 6 , 7 , 1"); // 4

    lblCertificationT = new JLabel(BUNDLE.getString("metatag.certification")); //$NON-NLS-1$
    add(lblCertificationT, "2, 8");
    lblCertificationT.setLabelFor(lblCertification);

    lblCertification = new JLabel("");
    add(lblCertification, "4, 8, 7, 1");

    lblDeveloperT = new JLabel(BUNDLE.getString("metatag.developer")); //$NON-NLS-1$
    add(lblDeveloperT, "2, 10, default, top");
    lblDeveloperT.setLabelFor(lblDeveloper);

    lblDeveloper = new JLabel();
    add(lblDeveloper, "4, 10, 9, 1");

    lblPublisherT = new JLabel(BUNDLE.getString("metatag.publisher")); //$NON-NLS-1$
    add(lblPublisherT, "8, 10, default, top"); // //////////////
    lblPublisherT.setLabelFor(lblPublisher);

    lblPublisher = new JLabel();
    add(lblPublisher, "10, 10");

    lblPlatformT = new JLabel(BUNDLE.getString("metatag.platform")); //$NON-NLS-1$
    add(lblPlatformT, "2, 12");

    lblPlatform = new JLabel("");
    add(lblPlatform, "4, 12, 3, 1");

    lblGamesetT = new JLabel(BUNDLE.getString("metatag.gameset")); //$NON-NLS-1$
    add(lblGamesetT, "2, 14");

    lblGameSet = new JLabel("");
    add(lblGameSet, "4, 14, 9, 1");

    lblTagsT = new JLabel(BUNDLE.getString("metatag.tags")); //$NON-NLS-1$
    add(lblTagsT, "2, 16");

    // lblTags = new JLabel("");
    // add(lblTags, "4, 16, 9, 1");
    //
    //    lblImdbIdT = new JLabel(BUNDLE.getString("metatag.imdbempty")); //$NON-NLS-1$
    // add(lblImdbIdT, "2, 18");

    // lblImdbId = new LinkLabel("");
    // lblImdbId.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent arg0) {
    // String url = "http://www.imdb.com/title/" + lblImdbId.getNormalText();
    // try {
    // TmmUIHelper.browseUrl(url);
    // }
    // catch (Exception e) {
    // LOGGER.error("browse to imdbid", e);
    // MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, url, "message.erroropenurl", new String[] { ":",
    // e.getLocalizedMessage() }));
    // }
    // }
    // });

    // add(lblImdbId, "4, 18, 3, 1, left, default");
    // lblImdbIdT.setLabelFor(lblImdbId);
    //
    //    lblTmdbIdT = new JLabel(BUNDLE.getString("metatag.tmdbempty")); //$NON-NLS-1$
    // add(lblTmdbIdT, "8, 18");
    //
    // lblTmdbId = new LinkLabel("");
    // lblTmdbId.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent arg0) {
    // String url = "http://www.thegamedb.org/game/" + lblTmdbId.getNormalText();
    // try {
    // TmmUIHelper.browseUrl(url);
    // }
    // catch (Exception e) {
    // LOGGER.error("browse to tmdbid", e);
    // MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, url, "message.erroropenurl", new String[] { ":",
    // e.getLocalizedMessage() }));
    // }
    // }
    // });
    // add(lblTmdbId, "10, 18, 3, 1, left, default");
    // lblTmdbIdT.setLabelFor(lblTmdbId);

    JLabel lblIsFavoriteT = new JLabel(BUNDLE.getString("metatag.isfavorite")); //$NON-NLS-1$
    add(lblIsFavoriteT, "8, 4");

    chckbxIsFavorite = new JCheckBox("");
    chckbxIsFavorite.setEnabled(false);
    add(chckbxIsFavorite, "10, 4");

    // chckbxIsFavorite.setSelected(gameSelectionModel.getSelectedGame().isIsFavorite());

    lblGamePathT = new JLabel(BUNDLE.getString("metatag.path")); //$NON-NLS-1$
    add(lblGamePathT, "2, 20");

    lblGamePath = new LinkLabel("");
    lblGamePath.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (!StringUtils.isEmpty(lblGamePath.getNormalText())) {
          // get the location from the label
          File path = new File(lblGamePath.getNormalText());
          try {
            // check whether this location exists
            if (path.exists()) {
              TmmUIHelper.openFile(path);
            }
          }
          catch (Exception ex) {
            LOGGER.error("open filemanager", ex);
            MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, path, "message.erroropenfolder", new String[] { ":",
                ex.getLocalizedMessage() }));
          }
        }
      }
    });
    lblGamePathT.setLabelFor(lblGamePath);
    lblGamePathT.setLabelFor(lblGamePath);
    add(lblGamePath, "4, 20, 9, 1");

    initDataBindings();
  }

  protected void initDataBindings() {
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_6 = BeanProperty.create("selectedGame.originalTitle");
    BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_6, lblOriginalTitle, jLabelBeanProperty);
    autoBinding_7.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_7 = BeanProperty.create("selectedGame.genresAsString");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_7, lblGenres, jLabelBeanProperty);
    autoBinding_8.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_11 = BeanProperty.create("selectedGame.imdbId");
    BeanProperty<LinkLabel, String> linkLabelBeanProperty = BeanProperty.create("text");
    AutoBinding<GameSelectionModel, String, LinkLabel, String> autoBinding_12 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_11, lblImdbId, linkLabelBeanProperty);
    autoBinding_12.bind();
    //
    BeanProperty<GameSelectionModel, Integer> gameSelectionModelBeanProperty_12 = BeanProperty.create("selectedGame.tmdbId");
    AutoBinding<GameSelectionModel, Integer, LinkLabel, String> autoBinding_13 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_12, lblTmdbId, linkLabelBeanProperty);
    autoBinding_13.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_9 = BeanProperty.create("selectedGame.certification.name");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_9, lblCertification, jLabelBeanProperty);
    autoBinding_10.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_4 = BeanProperty.create("selectedGame.tagsAsString");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_4, lblTags, jLabelBeanProperty);
    autoBinding_4.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_5 = BeanProperty.create("selectedGame.developer");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_5, lblDeveloper, jLabelBeanProperty);
    autoBinding_5.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_61 = BeanProperty.create("selectedGame.publisher");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_61, lblPublisher, jLabelBeanProperty);
    autoBinding_6.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty = BeanProperty.create("selectedGame.path");
    AutoBinding<GameSelectionModel, String, LinkLabel, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty, lblGamePath, linkLabelBeanProperty);
    autoBinding.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_1 = BeanProperty.create("selectedGame.gameSetTitle");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_1, lblGameSet, jLabelBeanProperty);
    autoBinding_1.bind();

    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_3 = BeanProperty.create("selectedGame.platform");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_3, lblPlatform, jLabelBeanProperty);
    autoBinding_3.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_10 = BeanProperty.create("selectedGame.releaseDateAsString");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding_71 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_10, lblReleaseDate, jLabelBeanProperty);
    autoBinding_71.bind();
    //
    BeanProperty<GameSelectionModel, Boolean> gameSelectionModelBeanProperty_13 = BeanProperty.create("selectedGame.isFavorite");
    BeanProperty<JCheckBox, Boolean> jCheckBoxBeanProperty = BeanProperty.create("selected");
    AutoBinding<GameSelectionModel, Boolean, JCheckBox, Boolean> autoBinding_72 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_13, chckbxIsFavorite, jCheckBoxBeanProperty);
    autoBinding_72.bind();

  }
}

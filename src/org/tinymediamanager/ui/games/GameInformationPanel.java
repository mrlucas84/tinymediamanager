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

import static org.tinymediamanager.core.Constants.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.scraper.Certification;
import org.tinymediamanager.ui.CertificationImageConverter;
import org.tinymediamanager.ui.IsFavoriteIconConverter;
import org.tinymediamanager.ui.MediaInfoAudioCodecConverter;
import org.tinymediamanager.ui.MediaInfoVideoCodecConverter;
import org.tinymediamanager.ui.MediaInfoVideoFormatConverter;
import org.tinymediamanager.ui.PlatformImageConverter;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.VoteCountConverter;
import org.tinymediamanager.ui.components.ImageLabel;
import org.tinymediamanager.ui.components.ImagePanel;
import org.tinymediamanager.ui.components.StarRater;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameInformationPanel.
 * 
 * @author Manuel Laggner
 */
public class GameInformationPanel extends JPanel {
  /** The Constant BUNDLE. */
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  /** The Constant serialVersionUID. */
  private static final long           serialVersionUID = -8527284262749511617L;

  /** The split pane vertical. */
  private JSplitPane                  splitPaneVertical;

  /** The panel top. */
  private JPanel                      panelTop;

  /** The panel isFavorite image. */
  private JPanel                      panelIsFavoriteImage;

  /** The panel game logos. */
  private JPanel                      panelGameLogos;

  /** The panel rating. */
  private StarRater                   panelRatingStars;

  /** The lbl isFavorite image. */
  private JLabel                      lblIsFavoriteImage;

  /** The lbl isFavorite image. */
  private JLabel                      lblPlatformImage;

  /** The lbl game name. */
  private JLabel                      lblGameName;

  /** The label rating. */
  private JLabel                      lblRating;

  /** The lbl vote count. */
  private JLabel                      lblVoteCount;

  /** The lbl original name. */
  private JLabel                      lblTagline;

  /** The lbl certification image. */
  private JLabel                      lblCertificationImage;

  /** The lbl game background. */
  private ImageLabel                  lblGameBackground;

  /** The lbl game poster. */
  private ImageLabel                  lblGamePoster;

  // /** The table cast. */
  // private JTable tableCast;

  /** The tabbed pane game details. */
  private JTabbedPane                 tabbedPaneGameDetails;

  /** The panel overview. */
  private JPanel                      panelOverview;

  /** The panel details. */
  private JPanel                      panelDetails;
  //
  // /** The lbl director t. */
  // private JLabel lblDirectorT;
  //
  // /** The lbl director. */
  // private JLabel lblDirector;
  //
  // /** The lbl writer t. */
  // private JLabel lblWriterT;
  //
  // /** The lbl writer. */
  // private JLabel lblWriter;
  //
  // /** The lbl actors. */
  // private JLabel lblActors;

  /** The text pane. */
  private JTextPane                   tpOverview;

  /** The text pane. */
  private JTextPane                   tpTag;

  /** The panel media information. */
  private JPanel                      panelMediaInformation;

  /** The panel media files. */
  private JPanel                      panelMediaFiles;

  /** The panel scramper. */
  private JPanel                      panelScramper;

  // /** The lbl actor thumb. */
  // private ActorImageLabel lblActorThumb;

  /** The panel game trailer. */
  private GameTrailerPanel            panelGameTrailer;

  /** The game selection model. */
  private GameSelectionModel          gameSelectionModel;

  /** The lbl media logo resolution. */
  private JLabel                      lblMediaLogoResolution;

  /** The lbl media logo video codec. */
  private JLabel                      lblMediaLogoVideoCodec;

  /** The lbl media logo audio. */
  private JLabel                      lblMediaLogoAudio;

  /**
   * Instantiates a new game information panel.
   * 
   * @param gameSelectionModel
   *          the game selection model
   */
  public GameInformationPanel(GameSelectionModel gameSelectionModel) {
    this.gameSelectionModel = gameSelectionModel;

    setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("650px:grow"), }, new RowSpec[] { RowSpec.decode("180px:grow"), }));

    splitPaneVertical = new JSplitPane();
    splitPaneVertical.setBorder(null);
    splitPaneVertical.setResizeWeight(0.9);
    splitPaneVertical.setContinuousLayout(true);
    splitPaneVertical.setOneTouchExpandable(true);
    splitPaneVertical.setOrientation(JSplitPane.VERTICAL_SPLIT);
    add(splitPaneVertical, "1, 1, fill, fill");

    panelTop = new JPanel();
    panelTop.setBorder(null);
    splitPaneVertical.setTopComponent(panelTop);
    panelTop.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("300px:grow"),
        FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, //
        new RowSpec[] { RowSpec.decode("fill:default"), RowSpec.decode("fill:default"), RowSpec.decode("top:pref:grow"), }));

    JPanel panelGameHeader = new JPanel();
    panelTop.add(panelGameHeader, "2, 1, 3, 1, fill, top");
    panelGameHeader.setBorder(null);
    panelGameHeader.setLayout(new BorderLayout(0, 0));

    JPanel panelGameTitle = new JPanel();
    panelGameHeader.add(panelGameTitle, BorderLayout.NORTH);
    panelGameTitle.setLayout(new BorderLayout(0, 0));
    lblGameName = new JLabel("");
    // panelGameHeader.add(lblGameName, BorderLayout.NORTH);
    panelGameTitle.add(lblGameName);
    lblGameName.setFont(new Font("Dialog", Font.BOLD, 16));

    //
    panelIsFavoriteImage = new JPanel();
    panelGameTitle.add(panelIsFavoriteImage, BorderLayout.EAST);

    lblIsFavoriteImage = new JLabel("");
    panelIsFavoriteImage.add(lblIsFavoriteImage);

    JPanel panelRatingTagline = new JPanel();
    panelGameHeader.add(panelRatingTagline, BorderLayout.CENTER);
    panelRatingTagline.setLayout(new FormLayout( //
        new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("default:grow"), }, //
        new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("24px") }));

    lblRating = new JLabel("");
    panelRatingTagline.add(lblRating, "2, 2, left, center");

    lblVoteCount = new JLabel("");
    panelRatingTagline.add(lblVoteCount, "3, 2, left, center");

    panelRatingStars = new StarRater(5, 2);
    panelRatingTagline.add(panelRatingStars, "1, 2, left, top");
    panelRatingStars.setEnabled(false);

    panelGameLogos = new JPanel();
    panelGameHeader.add(panelGameLogos, BorderLayout.EAST);

    lblCertificationImage = new JLabel();
    panelGameLogos.add(lblCertificationImage);

    // /////

    JPanel panelTag = new JPanel();
    panelTag.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.PREF_COLSPEC }, //
        new RowSpec[] { FormFactory.PREF_ROWSPEC }));

    JScrollPane scrollTag = new JScrollPane();
    scrollTag.setPreferredSize(new Dimension(730, 70));

    tpTag = new JTextPane();
    tpTag.setOpaque(false);
    tpTag.setEditable(false);
    tpTag.setContentType("text");
    scrollTag.setViewportView(tpTag);

    panelTag.add(scrollTag, "1, 1, left, top");
    panelTop.add(panelTag, "1, 2, 4, 1, fill, fill");

    //

    JLayeredPane layeredPaneImages = new JLayeredPane();
    panelTop.add(layeredPaneImages, "1, 3, 4, 1, fill, fill");
    layeredPaneImages.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("max(10px;default)"), ColumnSpec.decode("left:120px:grow"),
        ColumnSpec.decode("default:grow(10)"), }, new RowSpec[] { RowSpec.decode("max(10px;default)"), RowSpec.decode("top:180px:grow"),
        RowSpec.decode("fill:80px:grow(3)"), }));

    lblGameBackground = new ImageLabel(false, true);
    lblGameBackground.setAlternativeText(BUNDLE.getString("image.notfound.fanart")); //$NON-NLS-1$
    layeredPaneImages.add(lblGameBackground, "1, 1, 3, 3, fill, fill");

    lblGamePoster = new ImageLabel();
    lblGamePoster.setAlternativeText(BUNDLE.getString("image.notfound.poster")); //$NON-NLS-1$
    layeredPaneImages.setLayer(lblGamePoster, 1);
    layeredPaneImages.add(lblGamePoster, "2, 2, fill, fill");

    JPanel panelGenres = new GameGenresPanel(gameSelectionModel);
    layeredPaneImages.setLayer(panelGenres, 2);
    layeredPaneImages.add(panelGenres, "2, 2, 2, 2, right, bottom");

    JPanel panelLogos = new JPanel();
    panelLogos.setOpaque(false);
    layeredPaneImages.setLayer(panelLogos, 2);
    layeredPaneImages.add(panelLogos, "2, 2, 2, 2, right, top");
    panelLogos.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

    lblMediaLogoResolution = new JLabel("");
    panelLogos.add(lblMediaLogoResolution);

    lblMediaLogoVideoCodec = new JLabel("");
    panelLogos.add(lblMediaLogoVideoCodec);

    lblMediaLogoAudio = new JLabel("");
    panelLogos.add(lblMediaLogoAudio);

    lblPlatformImage = new JLabel("");
    panelLogos.add(lblPlatformImage);

    JPanel panelBottom = new JPanel();
    panelBottom.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("496px:grow"), }, new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC,
        RowSpec.decode("fill:min:grow"), }));

    tabbedPaneGameDetails = new JTabbedPane(JTabbedPane.TOP);
    panelBottom.add(tabbedPaneGameDetails, "1, 2, fill, fill");
    splitPaneVertical.setBottomComponent(panelBottom);

    panelDetails = new GameDetailsPanel(gameSelectionModel);
    tabbedPaneGameDetails.addTab(BUNDLE.getString("metatag.details"), null, panelDetails, null); //$NON-NLS-1$

    panelOverview = new JPanel();
    tabbedPaneGameDetails.addTab(BUNDLE.getString("metatag.plot"), null, panelOverview, null); //$NON-NLS-1$
    panelOverview.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("241px:grow"), }, new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC,
        RowSpec.decode("fill:default:grow"), }));
    // panelGameDetails.add(tabbedPaneGameDetails, "2, 3, fill, fill");

    JScrollPane scrollPaneOverview = new JScrollPane();
    scrollPaneOverview.setBorder(null);
    panelOverview.add(scrollPaneOverview, "1, 2, fill, fill");

    tpOverview = new JTextPane();
    tpOverview.setOpaque(false);
    tpOverview.setEditable(false);
    tpOverview.setContentType("text/html");
    scrollPaneOverview.setViewportView(tpOverview);

    panelMediaInformation = new GameMediaInformationPanel(gameSelectionModel);
    tabbedPaneGameDetails.addTab(BUNDLE.getString("metatag.mediainformation"), null, panelMediaInformation, null); //$NON-NLS-1$

    panelMediaFiles = new GameMediaFilesPanel(gameSelectionModel);
    tabbedPaneGameDetails.addTab(BUNDLE.getString("metatag.mediafiles"), null, panelMediaFiles, null); //$NON-NLS-1$

    final List<MediaFile> mediaFiles = new ArrayList<MediaFile>();
    final ImagePanel panelArtwork = new ImagePanel(mediaFiles);
    tabbedPaneGameDetails.addTab(BUNDLE.getString("metatag.artwork"), null, panelArtwork, null); //$NON-NLS-1$

    panelGameTrailer = new GameTrailerPanel(gameSelectionModel);
    tabbedPaneGameDetails.addTab(BUNDLE.getString("metatag.trailer"), null, panelGameTrailer, null); //$NON-NLS-1$

    // beansbinding init
    initDataBindings();

    // manual coded binding
    PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String property = propertyChangeEvent.getPropertyName();
        Object source = propertyChangeEvent.getSource();
        // react on selection of a game and change of a game
        if (source instanceof GameSelectionModel || (source instanceof Game && MEDIA_FILES.equals(property))) {
          Game game = null;
          if (source instanceof GameSelectionModel) {
            game = ((GameSelectionModel) source).getSelectedGame();
          }
          if (source instanceof Game) {
            game = (Game) source;
          }

          if (game != null) {
            lblGameBackground.setImagePath(game.getFanart());
            lblGamePoster.setImagePath(game.getPoster());

            synchronized (mediaFiles) {
              mediaFiles.clear();
              for (MediaFile mediafile : game.getMediaFiles(MediaFileType.POSTER)) {
                mediaFiles.add(mediafile);
              }
              for (MediaFile mediafile : game.getMediaFiles(MediaFileType.FANART)) {
                mediaFiles.add(mediafile);
              }
              for (MediaFile mediafile : game.getMediaFiles(MediaFileType.BANNER)) {
                mediaFiles.add(mediafile);
              }
              for (MediaFile mediafile : game.getMediaFiles(MediaFileType.THUMB)) {
                mediaFiles.add(mediafile);
              }
              for (MediaFile mediafile : game.getMediaFiles(MediaFileType.EXTRAFANART)) {
                mediaFiles.add(mediafile);
              }
              panelArtwork.rebuildPanel();
            }
          }
        }
        if ((source.getClass() == Game.class && FANART.equals(property))) {
          Game game = (Game) source;
          lblGameBackground.clearImage();
          lblGameBackground.setImagePath(game.getFanart());
        }
        if ((source.getClass() == Game.class && POSTER.equals(property))) {
          Game game = (Game) source;
          lblGamePoster.clearImage();
          lblGamePoster.setImagePath(game.getPoster());
        }
      }
    };

    gameSelectionModel.addPropertyChangeListener(propertyChangeListener);
  }

  /**
   * Gets the split pane vertical.
   * 
   * @return the split pane vertical
   */
  public JSplitPane getSplitPaneVertical() {
    return splitPaneVertical;
  }

  protected void initDataBindings() {
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty = BeanProperty.create("selectedGame.titleForUi");
    BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty, lblGameName, jLabelBeanProperty);
    autoBinding.bind();
    //
    BeanProperty<GameSelectionModel, Float> gameSelectionModelBeanProperty_1 = BeanProperty.create("selectedGame.rating");
    AutoBinding<GameSelectionModel, Float, JLabel, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_1, lblRating, jLabelBeanProperty);
    autoBinding_1.bind();
    //
    BeanProperty<StarRater, Float> starRaterBeanProperty = BeanProperty.create("rating");
    AutoBinding<GameSelectionModel, Float, StarRater, Float> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_1, panelRatingStars, starRaterBeanProperty);
    autoBinding_3.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_14 = BeanProperty.create("selectedGame.plot");
    BeanProperty<JTextPane, String> jTextPaneBeanProperty = BeanProperty.create("text");
    AutoBinding<GameSelectionModel, String, JTextPane, String> autoBinding_15 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_14, tpOverview, jTextPaneBeanProperty);
    autoBinding_15.bind();
    //
    BeanProperty<GameSelectionModel, Integer> gameSelectionModelBeanProperty_2 = BeanProperty.create("selectedGame.votes");
    AutoBinding<GameSelectionModel, Integer, JLabel, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_2, lblVoteCount, jLabelBeanProperty);
    autoBinding_2.setConverter(new VoteCountConverter());
    autoBinding_2.bind();
    //
    BeanProperty<GameSelectionModel, Certification> gameSelectionModelBeanProperty_6 = BeanProperty.create("selectedGame.certification");
    BeanProperty<JLabel, Icon> jLabelBeanProperty_2 = BeanProperty.create("icon");
    AutoBinding<GameSelectionModel, Certification, JLabel, Icon> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_6, lblCertificationImage, jLabelBeanProperty_2);
    autoBinding_7.setConverter(new CertificationImageConverter());
    autoBinding_7.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_3 = BeanProperty.create("selectedGame.tagline");
    AutoBinding<GameSelectionModel, String, JTextPane, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_3, tpTag, jTextPaneBeanProperty);
    autoBinding_4.bind();
    //
    BeanProperty<GameSelectionModel, Boolean> gameSelectionModelBeanProperty_7 = BeanProperty.create("selectedGame.isFavorite");
    AutoBinding<GameSelectionModel, Boolean, JLabel, Icon> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_7, lblIsFavoriteImage, jLabelBeanProperty_2);
    autoBinding_8.setConverter(new IsFavoriteIconConverter());
    autoBinding_8.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_9 = BeanProperty.create("selectedGame.mediaInfoVideoFormat");
    AutoBinding<GameSelectionModel, String, JLabel, Icon> autoBinding_11 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_9, lblMediaLogoResolution, jLabelBeanProperty_2);
    autoBinding_11.setConverter(new MediaInfoVideoFormatConverter());
    autoBinding_11.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_10 = BeanProperty.create("selectedGame.mediaInfoVideoCodec");
    AutoBinding<GameSelectionModel, String, JLabel, Icon> autoBinding_12 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_10, lblMediaLogoVideoCodec, jLabelBeanProperty_2);
    autoBinding_12.setConverter(new MediaInfoVideoCodecConverter());
    autoBinding_12.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_11 = BeanProperty.create("selectedGame.mediaInfoAudioCodecAndChannels");
    AutoBinding<GameSelectionModel, String, JLabel, Icon> autoBinding_13 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_11, lblMediaLogoAudio, jLabelBeanProperty_2);
    autoBinding_13.setConverter(new MediaInfoAudioCodecConverter());
    autoBinding_13.bind();

    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_12 = BeanProperty.create("selectedGame.country");
    AutoBinding<GameSelectionModel, String, JLabel, Icon> autoBinding_14 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_12, lblPlatformImage, jLabelBeanProperty_2);
    autoBinding_14.setConverter(new PlatformImageConverter());
    autoBinding_14.bind();

  }
}

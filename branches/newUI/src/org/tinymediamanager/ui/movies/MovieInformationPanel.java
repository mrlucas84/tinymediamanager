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
package org.tinymediamanager.ui.movies;

import static org.tinymediamanager.core.Constants.*;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.movie.Movie;
import org.tinymediamanager.scraper.Certification;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.ImageLabel;
import org.tinymediamanager.ui.components.ImagePanel;
import org.tinymediamanager.ui.components.StarRater;
import org.tinymediamanager.ui.converter.CertificationImageConverter;
import org.tinymediamanager.ui.converter.MediaInfoAudioCodecConverter;
import org.tinymediamanager.ui.converter.MediaInfoVideoCodecConverter;
import org.tinymediamanager.ui.converter.MediaInfoVideoFormatConverter;
import org.tinymediamanager.ui.converter.VoteCountConverter;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class MovieInformationPanel.
 * 
 * @author Manuel Laggner
 */
public class MovieInformationPanel extends JPanel {

  private static final long           serialVersionUID = -8527284262749511617L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  private MovieSelectionModel         movieSelectionModel;
  private final ImageIcon             imageEmtpy       = new ImageIcon();
  private ImageIcon                   imageUnwatched;

  private StarRater                   panelRatingStars;
  private JLabel                      lblMovieName;
  private JLabel                      lblRating;
  private JLabel                      lblVoteCount;
  private JLabel                      lblTagline;
  private JLabel                      lblCertificationImage;
  private ImageLabel                  lblMovieBackground;
  private ImageLabel                  lblMoviePoster;
  private JTabbedPane                 tabbedPaneMovieDetails;
  private JPanel                      panelMovieCast;
  private JPanel                      panelDetails;
  private JTextPane                   tpOverview;
  private JPanel                      panelMediaInformation;
  private JPanel                      panelMediaFiles;
  private MovieTrailerPanel           panelMovieTrailer;
  private JLabel                      lblMediaLogoResolution;
  private JLabel                      lblMediaLogoVideoCodec;
  private JLabel                      lblMediaLogoAudio;
  private JPanel                      panelTopRight;
  private JSeparator                  separator;
  private JLabel                      lblYearT;
  private JLabel                      lblYear;
  private JLabel                      lblImdbIdT;
  private JLabel                      lblImdbid;
  private JLabel                      lblRunningTimeT;
  private JLabel                      lblRunningTime;
  private JLabel                      lblTmdbIdT;
  private JLabel                      lblTmdbid;
  private JLabel                      lblGenresT;
  private JLabel                      lblGenres;
  private JSeparator                  separator_1;
  private JSeparator                  separator_2;
  private JLabel                      lblTaglineT;
  private JSeparator                  separator_3;
  private JLabel                      lblPlotT;
  private JTextArea                   taPlot;
  private JPanel                      panelTop1;
  private JScrollPane                 scrollPane;
  private JPanel                      panelBottomRight;
  private JLayeredPane                panelTopLeft;
  private JPanel                      panelBottomLeft;
  private JLabel                      lblUnwatched;
  private JLabel                      lblTagline_1;

  /**
   * Instantiates a new movie information panel.
   * 
   * @param movieSelectionModel
   *          the movie selection model
   */
  public MovieInformationPanel(MovieSelectionModel movieSelectionModel) {
    this.movieSelectionModel = movieSelectionModel;

    try {
      imageUnwatched = new ImageIcon(MoviePanel.class.getResource("/org/tinymediamanager/ui/images/unwatched.png"));
    }
    catch (Exception e) {
      imageUnwatched = imageEmtpy;
    }

    putClientProperty("class", "roundedPanel");
    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("200px:grow"),
        ColumnSpec.decode("500px:grow(3)"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
        FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("200px:grow(2)"), FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("fill:200px:grow"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    panelTopLeft = new JLayeredPane();
    add(panelTopLeft, "3, 3, fill, fill");
    panelTopLeft.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("200px:grow"), }, new RowSpec[] { RowSpec.decode("default:grow"), }));

    lblMoviePoster = new ImageLabel(false, false, true);
    panelTopLeft.add(lblMoviePoster, "1, 1, fill, fill");
    panelTopLeft.setLayer(lblMoviePoster, 1);
    lblMoviePoster.setAlternativeText(BUNDLE.getString("image.notfound.poster")); //$NON-NLS-1$

    lblUnwatched = new JLabel(imageEmtpy);
    panelTopLeft.add(lblUnwatched, "1, 1, left, top");
    panelTopLeft.setLayer(lblUnwatched, 2);

    Font bold = lblMoviePoster.getFont().deriveFont(Font.BOLD);

    panelTopRight = new JPanel();
    add(panelTopRight, "4, 3, fill, fill");
    panelTopRight.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.UNRELATED_GAP_COLSPEC, FormFactory.MIN_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
        RowSpec.decode("fill:default"), FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, RowSpec.decode("default:grow"), }));
    lblMovieName = new JLabel("");
    panelTopRight.add(lblMovieName, "2, 1, 3, 1, fill, fill");
    lblMovieName.setFont(new Font("Dialog", Font.BOLD, 16));

    separator = new JSeparator();
    panelTopRight.add(separator, "2, 3, 3, 1");

    panelTop1 = new JPanel();
    panelTopRight.add(panelTop1, "2, 5, 3, 1, fill, fill");
    panelTop1.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("max(100px;min):grow"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("max(100px;min):grow"), }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    lblYearT = new JLabel(BUNDLE.getString("metatag.year")); //$NON-NLS-1$
    lblYearT.setFont(bold);
    panelTop1.add(lblYearT, "1, 1");

    lblYear = new JLabel("");
    panelTop1.add(lblYear, "3, 1");

    lblImdbIdT = new JLabel(BUNDLE.getString("metatag.imdb")); //$NON-NLS-1$
    lblImdbIdT.setFont(bold);
    panelTop1.add(lblImdbIdT, "5, 1");

    lblImdbid = new JLabel("");
    panelTop1.add(lblImdbid, "7, 1");

    lblRunningTimeT = new JLabel(BUNDLE.getString("metatag.runtime")); //$NON-NLS-1$
    lblRunningTimeT.setFont(bold);
    panelTop1.add(lblRunningTimeT, "1, 3");

    lblRunningTime = new JLabel("");
    panelTop1.add(lblRunningTime, "3, 3");

    lblTmdbIdT = new JLabel(BUNDLE.getString("metatag.tmdb")); //$NON-NLS-1$
    lblTmdbIdT.setFont(bold);
    panelTop1.add(lblTmdbIdT, "5, 3");

    lblTmdbid = new JLabel("");
    panelTop1.add(lblTmdbid, "7, 3");

    lblGenresT = new JLabel(BUNDLE.getString("metatag.genre")); //$NON-NLS-1$
    lblGenresT.setFont(bold);
    panelTop1.add(lblGenresT, "1, 5");

    lblGenres = new JLabel("");
    panelTop1.add(lblGenres, "3, 5, 5, 1");

    separator_1 = new JSeparator();
    panelTopRight.add(separator_1, "2, 7, 3, 1");

    JPanel panelRatingTagline = new JPanel();
    panelTopRight.add(panelRatingTagline, "2, 9, 3, 1");
    panelRatingTagline
        .setLayout(new FormLayout(new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
            FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] { RowSpec.decode("24px"), }));

    lblRating = new JLabel("");
    panelRatingTagline.add(lblRating, "3, 1, left, center");

    lblVoteCount = new JLabel("");
    panelRatingTagline.add(lblVoteCount, "5, 1, left, center");

    panelRatingStars = new StarRater(10, 1);
    panelRatingTagline.add(panelRatingStars, "1, 1, left, center");
    panelRatingStars.setEnabled(false);

    separator_2 = new JSeparator();
    panelTopRight.add(separator_2, "2, 11, 3, 1");

    separator_3 = new JSeparator();
    panelTopRight.add(separator_3, "2, 13, 3, 1");

    lblTagline_1 = new JLabel("Tagline");
    panelTopRight.add(lblTagline_1, "2, 15");

    lblTaglineT = new JLabel(BUNDLE.getString("metatag.tagline")); //$NON-NLS-1$
    lblTaglineT.setFont(bold);
    panelTopRight.add(lblTaglineT, "2, 17");

    lblTagline = new JLabel();
    panelTopRight.add(lblTagline, "2, 19, 3, 1");

    lblPlotT = new JLabel(BUNDLE.getString("metatag.plot")); //$NON-NLS-1$
    lblPlotT.setFont(bold);
    panelTopRight.add(lblPlotT, "2, 21");

    scrollPane = new JScrollPane();
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBorder(null);
    panelTopRight.add(scrollPane, "2, 23, 3, 1, fill, fill");

    taPlot = new JTextArea();
    scrollPane.setViewportView(taPlot);
    taPlot.setLineWrap(true);
    taPlot.setWrapStyleWord(true);
    taPlot.setOpaque(false);

    final List<MediaFile> mediaFiles = new ArrayList<MediaFile>();

    panelBottomLeft = new JPanel();
    add(panelBottomLeft, "3, 6, fill, fill");
    panelBottomLeft.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("200px:grow"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("250px:grow"), }));

    lblMovieBackground = new ImageLabel(false, false, true);
    panelBottomLeft.add(lblMovieBackground, "1, 4, fill, fill");

    panelBottomRight = new JPanel();
    add(panelBottomRight, "4, 6, fill, fill");
    panelBottomRight.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("450px:grow"), }, new RowSpec[] {
        FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), }));

    tabbedPaneMovieDetails = new JTabbedPane(JTabbedPane.TOP);
    panelBottomRight.add(tabbedPaneMovieDetails, "2, 2, fill, fill");

    panelDetails = new MovieDetailsPanel(movieSelectionModel);
    tabbedPaneMovieDetails.addTab(BUNDLE.getString("metatag.details"), null, panelDetails, null); //$NON-NLS-1$

    panelMovieCast = new MovieCastPanel(movieSelectionModel);
    tabbedPaneMovieDetails.addTab(BUNDLE.getString("metatag.cast"), null, panelMovieCast, null); //$NON-NLS-1$

    panelMediaInformation = new MovieMediaInformationPanel(movieSelectionModel);
    tabbedPaneMovieDetails.addTab(BUNDLE.getString("metatag.media"), null, panelMediaInformation, null); //$NON-NLS-1$

    panelMediaFiles = new MovieMediaFilesPanel(movieSelectionModel);
    tabbedPaneMovieDetails.addTab(BUNDLE.getString("metatag.files"), null, panelMediaFiles, null); //$NON-NLS-1$
    final ImagePanel panelArtwork = new ImagePanel(mediaFiles);
    tabbedPaneMovieDetails.addTab(BUNDLE.getString("metatag.artwork"), null, panelArtwork, null); //$NON-NLS-1$

    panelMovieTrailer = new MovieTrailerPanel(movieSelectionModel);
    tabbedPaneMovieDetails.addTab(BUNDLE.getString("metatag.trailer"), null, panelMovieTrailer, null); //$NON-NLS-1$

    // beansbinding init
    initDataBindings();

    // manual coded binding
    PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String property = propertyChangeEvent.getPropertyName();
        Object source = propertyChangeEvent.getSource();
        // react on selection of a movie and change of a movie
        if (source instanceof MovieSelectionModel || (source instanceof Movie && MEDIA_FILES.equals(property))) {
          Movie movie = null;
          if (source instanceof MovieSelectionModel) {
            movie = ((MovieSelectionModel) source).getSelectedMovie();
          }
          if (source instanceof Movie) {
            movie = (Movie) source;
          }

          if (movie != null) {
            lblMovieBackground.setImagePath(movie.getFanart());
            lblMoviePoster.setImagePath(movie.getPoster());

            if (movie.isWatched()) {
              lblUnwatched.setIcon(imageEmtpy);
            }
            else {
              lblUnwatched.setIcon(imageUnwatched);
            }

            synchronized (mediaFiles) {
              mediaFiles.clear();
              for (MediaFile mediafile : movie.getMediaFiles(MediaFileType.POSTER)) {
                mediaFiles.add(mediafile);
              }
              for (MediaFile mediafile : movie.getMediaFiles(MediaFileType.FANART)) {
                mediaFiles.add(mediafile);
              }
              for (MediaFile mediafile : movie.getMediaFiles(MediaFileType.BANNER)) {
                mediaFiles.add(mediafile);
              }
              for (MediaFile mediafile : movie.getMediaFiles(MediaFileType.THUMB)) {
                mediaFiles.add(mediafile);
              }
              for (MediaFile mediafile : movie.getMediaFiles(MediaFileType.EXTRAFANART)) {
                mediaFiles.add(mediafile);
              }
              panelArtwork.rebuildPanel();
            }
          }
        }
        if ((source.getClass() == Movie.class && FANART.equals(property))) {
          Movie movie = (Movie) source;
          lblMovieBackground.clearImage();
          lblMovieBackground.setImagePath(movie.getFanart());
        }
        if ((source.getClass() == Movie.class && POSTER.equals(property))) {
          Movie movie = (Movie) source;
          lblMoviePoster.clearImage();
          lblMoviePoster.setImagePath(movie.getPoster());
        }
      }
    };

    movieSelectionModel.addPropertyChangeListener(propertyChangeListener);
  }

  protected void initDataBindings() {
    BeanProperty<MovieSelectionModel, Float> movieSelectionModelBeanProperty_1 = BeanProperty.create("selectedMovie.rating");
    BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
    AutoBinding<MovieSelectionModel, Float, JLabel, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_1, lblRating, jLabelBeanProperty);
    autoBinding_1.bind();
    //
    BeanProperty<StarRater, Float> starRaterBeanProperty = BeanProperty.create("rating");
    AutoBinding<MovieSelectionModel, Float, StarRater, Float> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_1, panelRatingStars, starRaterBeanProperty);
    autoBinding_3.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_14 = BeanProperty.create("selectedMovie.plot");
    BeanProperty<JTextPane, String> jTextPaneBeanProperty = BeanProperty.create("text");
    AutoBinding<MovieSelectionModel, String, JTextPane, String> autoBinding_15 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_14, tpOverview, jTextPaneBeanProperty);
    autoBinding_15.bind();
    //
    BeanProperty<MovieSelectionModel, Integer> movieSelectionModelBeanProperty_2 = BeanProperty.create("selectedMovie.votes");
    AutoBinding<MovieSelectionModel, Integer, JLabel, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_2, lblVoteCount, jLabelBeanProperty);
    autoBinding_2.setConverter(new VoteCountConverter());
    autoBinding_2.bind();
    //
    BeanProperty<MovieSelectionModel, Certification> movieSelectionModelBeanProperty_6 = BeanProperty.create("selectedMovie.certification");
    BeanProperty<JLabel, Icon> jLabelBeanProperty_2 = BeanProperty.create("icon");
    AutoBinding<MovieSelectionModel, Certification, JLabel, Icon> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ,
        movieSelectionModel, movieSelectionModelBeanProperty_6, lblCertificationImage, jLabelBeanProperty_2);
    autoBinding_7.setConverter(new CertificationImageConverter());
    autoBinding_7.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_9 = BeanProperty.create("selectedMovie.mediaInfoVideoFormat");
    AutoBinding<MovieSelectionModel, String, JLabel, Icon> autoBinding_11 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_9, lblMediaLogoResolution, jLabelBeanProperty_2);
    autoBinding_11.setConverter(new MediaInfoVideoFormatConverter());
    autoBinding_11.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_10 = BeanProperty.create("selectedMovie.mediaInfoVideoCodec");
    AutoBinding<MovieSelectionModel, String, JLabel, Icon> autoBinding_12 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_10, lblMediaLogoVideoCodec, jLabelBeanProperty_2);
    autoBinding_12.setConverter(new MediaInfoVideoCodecConverter());
    autoBinding_12.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_11 = BeanProperty
        .create("selectedMovie.mediaInfoAudioCodecAndChannels");
    AutoBinding<MovieSelectionModel, String, JLabel, Icon> autoBinding_13 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_11, lblMediaLogoAudio, jLabelBeanProperty_2);
    autoBinding_13.setConverter(new MediaInfoAudioCodecConverter());
    autoBinding_13.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_8 = BeanProperty.create("selectedMovie.year");
    AutoBinding<MovieSelectionModel, String, JLabel, String> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_8, lblYear, jLabelBeanProperty);
    autoBinding_9.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_12 = BeanProperty.create("selectedMovie.imdbId");
    AutoBinding<MovieSelectionModel, String, JLabel, String> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_12, lblImdbid, jLabelBeanProperty);
    autoBinding_10.bind();
    //
    BeanProperty<MovieSelectionModel, Integer> movieSelectionModelBeanProperty_13 = BeanProperty.create("selectedMovie.runtime");
    AutoBinding<MovieSelectionModel, Integer, JLabel, String> autoBinding_14 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_13, lblRunningTime, jLabelBeanProperty);
    autoBinding_14.bind();
    //
    BeanProperty<MovieSelectionModel, Integer> movieSelectionModelBeanProperty_15 = BeanProperty.create("selectedMovie.tmdbId");
    AutoBinding<MovieSelectionModel, Integer, JLabel, String> autoBinding_16 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_15, lblTmdbid, jLabelBeanProperty);
    autoBinding_16.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_16 = BeanProperty.create("selectedMovie.genresAsString");
    AutoBinding<MovieSelectionModel, String, JLabel, String> autoBinding_17 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_16, lblGenres, jLabelBeanProperty);
    autoBinding_17.bind();
    //
    BeanProperty<JTextArea, String> jTextAreaBeanProperty = BeanProperty.create("text");
    AutoBinding<MovieSelectionModel, String, JTextArea, String> autoBinding_18 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_14, taPlot, jTextAreaBeanProperty);
    autoBinding_18.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_3 = BeanProperty.create("selectedMovie.tagline");
    AutoBinding<MovieSelectionModel, String, JLabel, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_3, lblTagline, jLabelBeanProperty);
    autoBinding_4.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_4 = BeanProperty.create("selectedMovie.title");
    AutoBinding<MovieSelectionModel, String, JLabel, String> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_4, lblMovieName, jLabelBeanProperty);
    autoBinding_5.bind();
  }
}

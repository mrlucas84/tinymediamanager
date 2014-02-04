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
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameActor;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.core.game.GameSet;
import org.tinymediamanager.scraper.Certification;
import org.tinymediamanager.scraper.GameMediaGenres;
import org.tinymediamanager.scraper.MediaTrailer;
import org.tinymediamanager.scraper.MediaType;
import org.tinymediamanager.ui.EqualsLayout;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.TmmWindowSaver;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.AutocompleteComboBox;
import org.tinymediamanager.ui.components.ImageLabel;
import org.tinymediamanager.ui.dialogs.ImageChooserDialog;
import org.tinymediamanager.ui.dialogs.ImageChooserDialog.ImageType;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameEditor. genres/ Platform
 * 
 * @author Manuel Laggner
 */
public class GameEditorDialog extends JDialog {
  private static final long           serialVersionUID    = -286251957529920347L;
  private static final ResourceBundle BUNDLE              = ResourceBundle.getBundle("messages", new UTF8Control());               //$NON-NLS-1$
  private static final Date           INITIAL_DATE        = new Date(0);

  private Game                        gameToEdit;
  private GameList                    gameList            = GameList.getInstance();
  private List<GameActor>             cast                = ObservableCollections.observableList(new ArrayList<GameActor>());
  private List<GameMediaGenres>       genres              = ObservableCollections.observableList(new ArrayList<GameMediaGenres>());
  private List<MediaTrailer>          trailers            = ObservableCollections.observableList(new ArrayList<MediaTrailer>());
  private List<String>                tags                = ObservableCollections.observableList(new ArrayList<String>());
  private List<String>                extrathumbs         = new ArrayList<String>();
  private List<String>                extrafanarts        = new ArrayList<String>();
  private boolean                     continueQueue       = true;

  private final JPanel                details1Panel       = new JPanel();
  private final JPanel                details2Panel       = new JPanel();
  private JTextField                  tfTitle;
  private JTextField                  tfOriginalTitle;
  private JSpinner                    spYear;
  private JEditorPane                 tpPlot;
  // private JTextField tfDirector;
  private JTable                      tableActors;
  private JLabel                      lblGamePath;
  private ImageLabel                  lblPoster;
  private ImageLabel                  lblFanart;
  private JTextField                  tfPublisher;
  private JSpinner                    spRuntime;
  private JTextPane                   tfDeveloper;
  private JList                       listGenres;
  private JComboBox                   cbGenres;
  private JSpinner                    spRating;
  private JComboBox                   cbCertification;
  // private JTextField tfImdbId;
  // private JTextField tfTmdbId;
  private JLabel                      lblImdbId;
  private JLabel                      lblTmdbId;
  private JLabel                      lblIsFavorite;
  private JCheckBox                   cbIsFavorite;
  private JTextPane                   tpTagline;
  private JTable                      tableTrailer;
  private JTable                      tableScramper;
  private JComboBox                   cbTags;
  private JList                       listTags;
  private JSpinner                    spDateAdded;
  private JComboBox                   cbGameSet;
  private JTextField                  tfSorttitle;
  // private JTextField tfSpokenLanguages;
  private JTextField                  tfPlatform;
  private JSpinner                    spReleaseDate;

  private final Action                actionOK            = new SwingAction();
  private final Action                actionCancel        = new SwingAction_1();
  private final Action                actionAddActor      = new SwingAction_4();
  private final Action                actionRemoveActor   = new SwingAction_5();
  private final Action                actionAddGenre      = new SwingAction_2();
  private final Action                actionRemoveGenre   = new SwingAction_3();
  private final Action                action              = new SwingAction_trailer_add();
  private final Action                action_1            = new SwingAction_trailer_remove();
  private final Action                action_2            = new SwingAction_trailer_remove_bis();
  private final Action                action_3            = new SwingAction_9();
  private final Action                actionToggleGameSet = new ToggleGameSetAction();
  private final Action                abortAction         = new SwingAction_10();

  private final Action                action_11           = new SwingAction_scraper_add();
  private final Action                action_12           = new SwingAction_scraper_remove();

  /**
   * Create the dialog.
   * 
   * @param game
   *          the game
   * @param inQueue
   *          the in queue
   */
  public GameEditorDialog(Game game, boolean inQueue) {
    setModal(true);
    setIconImage(Globals.logo);
    setTitle(BUNDLE.getString("game.edit")); //$NON-NLS-1$
    setName("gameEditor");
    setBounds(5, 5, 950, 700);
    TmmWindowSaver.loadSettings(this);

    gameToEdit = game;

    getContentPane().setLayout(new BorderLayout());
    {
      JPanel panelPath = new JPanel();
      getContentPane().add(panelPath, BorderLayout.NORTH);
      panelPath.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
          FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC,
          RowSpec.decode("15px"), FormFactory.RELATED_GAP_ROWSPEC, }));

      JLabel lblGamePathT = new JLabel(BUNDLE.getString("metatag.path")); //$NON-NLS-1$
      panelPath.add(lblGamePathT, "2, 2, left, top");

      lblGamePath = new JLabel("");
      lblGamePath.setFont(new Font("Dialog", Font.BOLD, 14));
      panelPath.add(lblGamePath, "5, 2, left, top");
    }

    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.NORTH);
    tabbedPane.addTab(BUNDLE.getString("metatag.details"), details1Panel); //$NON-NLS-1$
    getContentPane().add(tabbedPane, BorderLayout.CENTER);

    details1Panel.setBorder(new EmptyBorder(5, 5, 5, 5));
    details1Panel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(40dlu;default)"),
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("50px:grow"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("50px"),
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("100px:grow"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"),
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("50px"), FormFactory.UNRELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("250px:grow"), FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("50px"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("top:max(75px;default)"), FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("75px:grow"),
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("50px"), FormFactory.NARROW_LINE_GAP_ROWSPEC, RowSpec.decode("fill:30px:grow(2)"), }));

    {
      JLabel lblTitle = new JLabel(BUNDLE.getString("metatag.title")); //$NON-NLS-1$
      details1Panel.add(lblTitle, "2, 4, right, default");
    }
    {
      tfTitle = new JTextField();
      details1Panel.add(tfTitle, "4, 4, 9, 1, fill, default");
      tfTitle.setColumns(10);
    }
    {
      // JLabel lblPoster = new JLabel("");
      lblPoster = new ImageLabel();
      lblPoster.setAlternativeText(BUNDLE.getString("image.notfound.poster")); //$NON-NLS-1$
      lblPoster.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {

          ImageChooserDialog dialog = new ImageChooserDialog(gameToEdit.getIds(), ImageType.POSTER, gameList.getArtworkProviders(), lblPoster, null,
              null, MediaType.GAME);
          dialog.setLocationRelativeTo(MainWindow.getActiveInstance());
          dialog.setVisible(true);
        }
      });
      lblPoster.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      details1Panel.add(lblPoster, "14, 4, 3, 23, fill, fill");
    }
    {
      JLabel lblOriginalTitle = new JLabel(BUNDLE.getString("metatag.originaltitle")); //$NON-NLS-1$
      details1Panel.add(lblOriginalTitle, "2, 6, right, default");
    }
    {
      tfOriginalTitle = new JTextField();
      details1Panel.add(tfOriginalTitle, "4, 6, 9, 1, fill, top");
      tfOriginalTitle.setColumns(10);
    }
    {
      JLabel lblSorttitle = new JLabel(BUNDLE.getString("metatag.sorttitle")); //$NON-NLS-1$
      details1Panel.add(lblSorttitle, "2, 8, right, default");
    }
    {
      tfSorttitle = new JTextField();
      details1Panel.add(tfSorttitle, "4, 8, 9, 1, fill, default");
      tfSorttitle.setColumns(10);
    }
    {
      JLabel lblTagline = new JLabel(BUNDLE.getString("metatag.tagline")); //$NON-NLS-1$
      details1Panel.add(lblTagline, "2, 10, right, top");
    }
    {
      JScrollPane scrollPaneTagline = new JScrollPane();
      tpTagline = new JTextPane();
      scrollPaneTagline.setViewportView(tpTagline);
      details1Panel.add(scrollPaneTagline, "4, 10, 9, 1, fill, fill");
    }
    {
      JLabel lblYear = new JLabel(BUNDLE.getString("metatag.year")); //$NON-NLS-1$
      details1Panel.add(lblYear, "2, 12, right, default");
    }
    {
      spYear = new JSpinner();
      details1Panel.add(spYear, "4, 12, fill, top");
    }
    {
      JLabel lblRuntime = new JLabel(BUNDLE.getString("metatag.runtime")); //$NON-NLS-1$
      details1Panel.add(lblRuntime, "8, 12, right, default");
    }
    {
      spRuntime = new JSpinner();
      details1Panel.add(spRuntime, "10, 12, fill, default");
    }
    {
      JLabel lblMin = new JLabel(BUNDLE.getString("metatag.minutes")); //$NON-NLS-1$
      details1Panel.add(lblMin, "12, 12");
    }
    {
      JLabel lblReleaseDate = new JLabel(BUNDLE.getString("metatag.releasedate")); //$NON-NLS-1$
      details1Panel.add(lblReleaseDate, "2, 14, right, default");
    }
    {
      spReleaseDate = new JSpinner(new SpinnerDateModel());
      details1Panel.add(spReleaseDate, "4, 14");
    }
    {
      JLabel lblRating = new JLabel(BUNDLE.getString("metatag.rating")); //$NON-NLS-1$
      details1Panel.add(lblRating, "2, 16, right, default");
    }
    {
      spRating = new JSpinner();
      details1Panel.add(spRating, "4, 16");
    }
    {
      JLabel lblCertification = new JLabel(BUNDLE.getString("metatag.certification")); //$NON-NLS-1$
      details1Panel.add(lblCertification, "8, 16, right, default");
    }
    {
      cbCertification = new JComboBox();
      for (Certification cert : Certification.getCertificationsforCountry(Globals.settings.getGameSettings().getCertificationCountry())) {
        cbCertification.addItem(cert);
      }
      details1Panel.add(cbCertification, "10, 16, 3, 1, fill, default");
    }
    {
      JLabel lblGameSet = new JLabel(BUNDLE.getString("metatag.gameset")); //$NON-NLS-1$"Game set");
      details1Panel.add(lblGameSet, "2, 18, right, default");
    }
    {
      cbGameSet = new JComboBox();
      cbGameSet.setAction(actionToggleGameSet);
      details1Panel.add(cbGameSet, "4, 18, 9, 1, fill, default");
    }
    {
      lblImdbId = new JLabel(BUNDLE.getString("metatag.imdbputempty")); //$NON-NLS-1$
      details1Panel.add(lblImdbId, "2, 20, right, default");
    }
    // {
    // tfImdbId = new JTextField();
    // lblImdbId.setLabelFor(tfImdbId);
    // details1Panel.add(tfImdbId, "4, 20, 3, 1, fill, default");
    // tfImdbId.setColumns(10);
    // }
    // {
    //      lblTmdbId = new JLabel(BUNDLE.getString("metatag.tmdbempty")); //$NON-NLS-1$
    // details1Panel.add(lblTmdbId, "8, 20, right, default");
    // }
    // {
    // tfTmdbId = new JTextField();
    // lblTmdbId.setLabelFor(tfTmdbId);
    // details1Panel.add(tfTmdbId, "10, 20, 3, 1, fill, default");
    // tfTmdbId.setColumns(10);
    // }
    {
      lblIsFavorite = new JLabel(BUNDLE.getString("metatag.isFavorite")); //$NON-NLS-1$
      details1Panel.add(lblIsFavorite, "2, 22, right, default");
    }
    {
      cbIsFavorite = new JCheckBox("");
      lblIsFavorite.setLabelFor(cbIsFavorite);
      details1Panel.add(cbIsFavorite, "4, 22");
    }
    {
      JLabel lblDateAdded = new JLabel(BUNDLE.getString("metatag.dateadded")); //$NON-NLS-1$
      details1Panel.add(lblDateAdded, "8, 22, right, default");
    }
    {
      spDateAdded = new JSpinner(new SpinnerDateModel());
      // JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spDateAdded,
      // "dd.MM.yyyy HH:mm:ss");
      // spDateAdded.setEditor(timeEditor);
      details1Panel.add(spDateAdded, "10, 22, 3, 1");
    }
    // {
    //      JLabel lblSpokenLanguages = new JLabel(BUNDLE.getString("metatag.spokenlanguages")); //$NON-NLS-1$
    // details1Panel.add(lblSpokenLanguages, "2, 24, right, default");
    // }
    // {
    // tfSpokenLanguages = new JTextField();
    // details1Panel.add(tfSpokenLanguages, "4, 24, 3, 1, fill, default");
    // tfSpokenLanguages.setColumns(10);
    // }
    {
      JLabel lbl = new JLabel(BUNDLE.getString("metatag.platform")); //$NON-NLS-1$
      details1Panel.add(lbl, "8, 24, right, default");
    }
    {
      tfPlatform = new JTextField();
      details1Panel.add(tfPlatform, "10, 24, 3, 1, fill, default");
      tfPlatform.setColumns(10);
    }
    {
      JLabel lblPlot = new JLabel(BUNDLE.getString("metatag.plot")); //$NON-NLS-1$
      details1Panel.add(lblPlot, "2, 26, right, top");
    }
    {
      JScrollPane scrollPanePlot = new JScrollPane();
      details1Panel.add(scrollPanePlot, "4, 26, 9, 3, fill, fill");
      {
        tpPlot = new JEditorPane();
        scrollPanePlot.setViewportView(tpPlot);
      }
    }
    // {
    //      JLabel lblDirector = new JLabel(BUNDLE.getString("metatag.director")); //$NON-NLS-1$
    // details1Panel.add(lblDirector, "2, 30, right, default");
    // }
    // {
    // tfDirector = new JTextField();
    // details1Panel.add(tfDirector, "4, 30, 9, 1, fill, top");
    // tfDirector.setColumns(10);
    // }
    {
      lblFanart = new ImageLabel();
      lblFanart.setAlternativeText(BUNDLE.getString("image.notfound.fanart")); //$NON-NLS-1$
      lblFanart.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      lblFanart.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          ImageChooserDialog dialog = new ImageChooserDialog(gameToEdit.getIds(), ImageType.FANART, gameList.getArtworkProviders(), lblFanart,
              extrathumbs, extrafanarts, MediaType.GAME);
          dialog.setLocationRelativeTo(MainWindow.getActiveInstance());
          dialog.setVisible(true);
        }
      });
      details1Panel.add(lblFanart, "14, 28, 3, 7, fill, fill");
    }
    lblFanart.setImagePath(game.getFanart());
    {
      JLabel lblPublisher = new JLabel(BUNDLE.getString("metatag.publisher")); //$NON-NLS-1$
      details1Panel.add(lblPublisher, "2, 32, right, default");
    }
    {
      tfPublisher = new JTextField();
      details1Panel.add(tfPublisher, "4, 32, 9, 1, fill, top");
      tfPublisher.setColumns(10);
    }
    {
      JLabel lblCompany = new JLabel(BUNDLE.getString("metatag.production")); //$NON-NLS-1$
      details1Panel.add(lblCompany, "2, 34, right, top");
    }
    {
      JScrollPane scrollPaneProduction = new JScrollPane();
      details1Panel.add(scrollPaneProduction, "4, 34, 9, 1, fill, fill");
      tfDeveloper = new JTextPane();
      scrollPaneProduction.setViewportView(tfDeveloper);
    }

    /**
     * DetailsPanel 2
     */
    tabbedPane.addTab(BUNDLE.getString("metatag.details2"), details2Panel); //$NON-NLS-1$
    details2Panel.setBorder(new EmptyBorder(5, 5, 5, 5));
    details2Panel.setLayout(new FormLayout(new ColumnSpec[] { //
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(40dlu;default)"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("50px:grow"),
            FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("100px:grow"),
            FormFactory.RELATED_GAP_COLSPEC, }, //
        new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("fill:30px:grow"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"),
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow(2)"), }));
    {
      JLabel lblActors = new JLabel(BUNDLE.getString("metatag.actors")); //$NON-NLS-1$
      details2Panel.add(lblActors, "2, 2, right, default");
    }
    {
      JScrollPane scrollPane = new JScrollPane();
      details2Panel.add(scrollPane, "4, 2, 1, 7");
      {
        tableActors = new JTable();
        scrollPane.setViewportView(tableActors);
      }
    }
    {
      JLabel lblGenres = new JLabel(BUNDLE.getString("metatag.genre")); //$NON-NLS-1$
      details2Panel.add(lblGenres, "6, 2");
    }
    {
      JButton btnAddActor = new JButton(BUNDLE.getString("cast.actor.add")); //$NON-NLS-1$
      btnAddActor.setMargin(new Insets(2, 2, 2, 2));
      btnAddActor.setAction(actionAddActor);
      btnAddActor.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Add.png")));
      details2Panel.add(btnAddActor, "2, 4, right, top");
    }
    {
      JScrollPane scrollPaneGenres = new JScrollPane();
      details2Panel.add(scrollPaneGenres, "8, 2, 1, 5");
      {
        listGenres = new JList();
        scrollPaneGenres.setViewportView(listGenres);
      }
    }
    {
      JButton btnAddGenre = new JButton("");
      btnAddGenre.setAction(actionAddGenre);
      btnAddGenre.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Add.png")));
      btnAddGenre.setMargin(new Insets(2, 2, 2, 2));
      details2Panel.add(btnAddGenre, "6, 4, right, top");
    }
    {
      JButton btnRemoveActor = new JButton(BUNDLE.getString("cast.actor.remove")); //$NON-NLS-1$
      btnRemoveActor.setMargin(new Insets(2, 2, 2, 2));
      btnRemoveActor.setAction(actionRemoveActor);
      btnRemoveActor.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Remove.png")));
      details2Panel.add(btnRemoveActor, "2,6, right, top");
    }

    {
      JButton btnRemoveGenre = new JButton("");
      btnRemoveGenre.setAction(actionRemoveGenre);
      btnRemoveGenre.setMargin(new Insets(2, 2, 2, 2));
      btnRemoveGenre.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Remove.png")));
      details2Panel.add(btnRemoveGenre, "6, 6, right, top");
    }
    {
      // cbGenres = new JComboBox(GameMediaGenres2.values());
      cbGenres = new AutocompleteComboBox(GameMediaGenres.values());
      cbGenres.setEditable(true);
      details2Panel.add(cbGenres, "8,8");
    }

    {
      JLabel lblTrailer = new JLabel(BUNDLE.getString("metatag.trailer")); //$NON-NLS-1$
      details2Panel.add(lblTrailer, "2, 10, right, default");
    }
    {
      JScrollPane scrollPaneTrailer = new JScrollPane();
      details2Panel.add(scrollPaneTrailer, "4, 10, 5, 5");
      tableTrailer = new JTable();
      scrollPaneTrailer.setViewportView(tableTrailer);
    }
    {
      JButton btnAddTrailer = new JButton("");
      btnAddTrailer.setAction(action);
      btnAddTrailer.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Add.png")));
      btnAddTrailer.setMargin(new Insets(2, 2, 2, 2));
      details2Panel.add(btnAddTrailer, "2, 12, right, top");
    }
    {
      JButton btnRemoveTrailer = new JButton("");
      btnRemoveTrailer.setAction(action_1);
      btnRemoveTrailer.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Remove.png")));
      btnRemoveTrailer.setMargin(new Insets(2, 2, 2, 2));
      details2Panel.add(btnRemoveTrailer, "2, 14, right, top");
    }
    {
      JLabel lblTags = new JLabel(BUNDLE.getString("metatag.tags")); //$NON-NLS-1$
      details2Panel.add(lblTags, "2, 16, right, default");
    }
    {
      JScrollPane scrollPaneTags = new JScrollPane();
      details2Panel.add(scrollPaneTags, "4, 16, 1, 5");
      listTags = new JList();
      scrollPaneTags.setViewportView(listTags);
    }
    {
      JButton btnAddTag = new JButton("");
      btnAddTag.setAction(action_2);
      btnAddTag.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Add.png")));
      btnAddTag.setMargin(new Insets(2, 2, 2, 2));
      details2Panel.add(btnAddTag, "2, 18, right, top");
    }
    {
      JButton btnRemoveTag = new JButton("");
      btnRemoveTag.setAction(action_3);
      btnRemoveTag.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Remove.png")));
      btnRemoveTag.setMargin(new Insets(2, 2, 2, 2));
      details2Panel.add(btnRemoveTag, "2, 20, right, top");
    }
    // Start

    {
      JLabel lblScramper = new JLabel(BUNDLE.getString("metatag.scraper")); //$NON-NLS-1$
      details2Panel.add(lblScramper, "6, 16, right, default");
    }
    {
      JScrollPane scrollPaneTrailer = new JScrollPane();
      details2Panel.add(scrollPaneTrailer, "8, 16, 1, 5");
      tableScramper = new JTable();
      scrollPaneTrailer.setViewportView(tableScramper);
    }
    {
      JButton btnAddScramper = new JButton("");
      btnAddScramper.setAction(action_11);
      btnAddScramper.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Add.png")));
      btnAddScramper.setMargin(new Insets(2, 2, 2, 2));
      details2Panel.add(btnAddScramper, "6, 18, right, top");
    }
    {
      JButton btnRemoveScramper = new JButton("");
      btnRemoveScramper.setAction(action_12);
      btnRemoveScramper.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Remove.png")));
      btnRemoveScramper.setMargin(new Insets(2, 2, 2, 2));
      details2Panel.add(btnRemoveScramper, "6, 20, right, top");
    }
    // end
    {
      cbTags = new AutocompleteComboBox(gameList.getTagsInGames().toArray());
      cbTags.setEditable(true);
      details2Panel.add(cbTags, "4, 22");
    }

    /**
     * Button pane
     */
    {
      JPanel bottomPane = new JPanel();
      getContentPane().add(bottomPane, BorderLayout.SOUTH);
      bottomPane.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("371px:grow"), FormFactory.DEFAULT_COLSPEC,
          FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("25px"),
          FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

      JPanel buttonPane = new JPanel();
      bottomPane.add(buttonPane, "2, 2, left, top");
      EqualsLayout layout = new EqualsLayout(5);
      layout.setMinWidth(100);
      buttonPane.setLayout(layout);
      {
        JButton okButton = new JButton(BUNDLE.getString("Button.ok")); //$NON-NLS-1$
        buttonPane.add(okButton, "2, 1, fill, top");
        okButton.setAction(actionOK);
        okButton.setActionCommand("OK");
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton(BUNDLE.getString("Button.cancel")); //$NON-NLS-1$
        buttonPane.add(cancelButton, "4, 1, fill, top");
        cancelButton.setAction(actionCancel);
        cancelButton.setActionCommand("Cancel");
      }
      if (inQueue) {
        JButton btnAbort = new JButton(BUNDLE.getString("Button.abortqueue")); //$NON-NLS-1$
        btnAbort.setAction(abortAction);
        buttonPane.add(btnAbort, "6, 1, fill, top");
      }

    }
    initDataBindings();

    {
      lblGamePath.setText(game.getPath());
      tfTitle.setText(game.getTitle());
      tfOriginalTitle.setText(game.getOriginalTitle());
      tfSorttitle.setText(game.getSortTitle());
      tpTagline.setText(game.getTagline());
      // tfImdbId.setText(game.getGameId("default"));
      // tfTmdbId.setText("0");

      tpPlot.setContentType("text/html");
      tpPlot.setText(game.getPlot());

      // tfDirector.setText(game.getDirector());
      tfPublisher.setText(game.getPublisher());
      lblPoster.setImagePath(game.getPoster());
      tfDeveloper.setText(game.getProductionCompany());
      spRuntime.setValue(Integer.valueOf(game.getRuntime()));
      cbIsFavorite.setSelected(game.isIsFavorite());
      spDateAdded.setValue(game.getDateAdded());
      if (game.getReleaseDate() != null) {
        spReleaseDate.setValue(game.getReleaseDate());
      }
      else {
        spReleaseDate.setValue(INITIAL_DATE);
      }
      // tfSpokenLanguages.setText(game.getSpokenLanguages());
      tfPlatform.setText(game.getPlatform());

      int year = 0;
      try {
        year = Integer.valueOf(game.getYear());
      }
      catch (Exception e) {
      }
      spYear.setModel(new SpinnerNumberModel(year, 0, 3000, 1));
      spYear.setEditor(new JSpinner.NumberEditor(spYear, "#"));

      spRating.setModel(new SpinnerNumberModel(game.getRating(), 0.0, 10.0, 0.1));
      SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM);
      spReleaseDate.setEditor(new JSpinner.DateEditor(spReleaseDate, dateFormat.toPattern()));

      for (GameActor origCast : game.getActors()) {
        GameActor actor = new GameActor();
        actor.setName(origCast.getName());
        actor.setCharacter(origCast.getCharacter());
        actor.setThumb(origCast.getThumb());
        cast.add(actor);
      }

      for (GameMediaGenres genre : game.getGenres()) {
        genres.add(genre);
      }

      for (MediaTrailer trailer : game.getTrailers()) {
        trailers.add(trailer);
      }

      for (String tag : gameToEdit.getTags()) {
        if (StringUtils.isNotBlank(tag)) {
          tags.add(tag);
        }
      }

      extrathumbs.addAll(gameToEdit.getExtraThumbs());
      extrafanarts.addAll(gameToEdit.getExtraFanarts());

      cbCertification.setSelectedItem(game.getCertification());

      cbGameSet.addItem("");
      for (GameSet gameSet : gameList.getGameSetList()) {
        cbGameSet.addItem(gameSet);
        if (gameToEdit.getGameSet() == gameSet) {
          cbGameSet.setSelectedItem(gameSet);
        }
      }

      toggleSorttitle();
    }
    // adjust columnn titles - we have to do it this way - thx to windowbuilder pro
    tableActors.getColumnModel().getColumn(0).setHeaderValue(BUNDLE.getString("metatag.name")); //$NON-NLS-1$
    tableActors.getColumnModel().getColumn(1).setHeaderValue(BUNDLE.getString("metatag.role")); //$NON-NLS-1$

    tableTrailer.getColumnModel().getColumn(0).setHeaderValue(BUNDLE.getString("metatag.nfo")); //$NON-NLS-1$
    tableTrailer.getColumnModel().getColumn(1).setHeaderValue(BUNDLE.getString("metatag.name")); //$NON-NLS-1$
    tableTrailer.getColumnModel().getColumn(2).setHeaderValue(BUNDLE.getString("metatag.source")); //$NON-NLS-1$
    tableTrailer.getColumnModel().getColumn(3).setHeaderValue(BUNDLE.getString("metatag.quality")); //$NON-NLS-1$
    tableTrailer.getColumnModel().getColumn(4).setHeaderValue(BUNDLE.getString("metatag.url")); //$NON-NLS-1$

    // adjust table columns
    tableTrailer.getColumnModel().getColumn(0).setMaxWidth(55);

    // implement listener to simulate button group
    tableTrailer.getModel().addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent arg0) {
        // click on the checkbox
        if (arg0.getColumn() == 0) {
          int row = arg0.getFirstRow();
          MediaTrailer changedTrailer = trailers.get(row);
          // if flag inNFO was changed, change all other trailers flags
          if (changedTrailer.getInNfo()) {
            for (MediaTrailer trailer : trailers) {
              if (trailer != changedTrailer) {
                trailer.setInNfo(Boolean.FALSE);
              }
            }
          }
        }
      }
    });

    tableScramper.getColumnModel().getColumn(0).setHeaderValue(BUNDLE.getString("metatag.scrampername")); //$NON-NLS-1$
    tableScramper.getColumnModel().getColumn(1).setHeaderValue(BUNDLE.getString("metatag.scramperid")); //$NON-NLS-1$
    tableScramper.getColumnModel().getColumn(2).setHeaderValue(BUNDLE.getString("metatag.scramperurl")); //$NON-NLS-1$

    // implement listener to simulate button group
    tableScramper.getModel().addTableModelListener(new TableModelListener() {

      @Override
      public void tableChanged(TableModelEvent e) {
        // TODO Auto-generated method stub

      }
    });

  }

  /**
   * Toggle sorttitle.
   */
  private void toggleSorttitle() {
    Object obj = cbGameSet.getSelectedItem();
    if (obj instanceof String) {
      tfSorttitle.setEnabled(true);
    }
    else {
      tfSorttitle.setEnabled(false);
    }
  }

  /**
   * The Class SwingAction.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new swing action.
     */
    public SwingAction() {
      putValue(NAME, BUNDLE.getString("Button.ok")); //$NON-NLS-1$
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("game.change")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      gameToEdit.setTitle(tfTitle.getText());
      gameToEdit.setOriginalTitle(tfOriginalTitle.getText());
      gameToEdit.setTagline(tpTagline.getText());
      gameToEdit.setPlot(tpPlot.getText());
      gameToEdit.setYear(spYear.getValue().equals(0) ? "" : String.valueOf(spYear.getValue())); // set empty on 0

      Date releaseDate = (Date) spReleaseDate.getValue();
      if (!releaseDate.equals(INITIAL_DATE)) {
        gameToEdit.setReleaseDate(releaseDate);
      }
      gameToEdit.setRuntime((Integer) spRuntime.getValue());
      // gameToEdit.setGameId(tfImdbId.getText(), "default");
      gameToEdit.setIsFavorite(cbIsFavorite.isSelected());
      // gameToEdit.setSpokenLanguages(tfSpokenLanguages.getText());
      gameToEdit.setPlatform(tfPlatform.getText());

      Object certification = cbCertification.getSelectedItem();
      if (certification instanceof Certification) {
        gameToEdit.setCertification((Certification) certification);
      }

      if (!StringUtils.isEmpty(lblPoster.getImageUrl()) && !lblPoster.getImageUrl().equals(gameToEdit.getPosterUrl())) {
        gameToEdit.setPosterUrl(lblPoster.getImageUrl());
        gameToEdit.writeImages(true, false);
      }

      if (!StringUtils.isEmpty(lblFanart.getImageUrl()) && !lblFanart.getImageUrl().equals(gameToEdit.getFanartUrl())) {
        gameToEdit.setFanartUrl(lblFanart.getImageUrl());
        gameToEdit.writeImages(false, true);
      }

      // set extrathumbs
      if (extrathumbs.size() != gameToEdit.getExtraThumbs().size() || !extrathumbs.containsAll(gameToEdit.getExtraThumbs())
          || !gameToEdit.getExtraThumbs().containsAll(extrathumbs)) {
        // gameToEdit.downloadExtraThumbs(extrathumbs);
        gameToEdit.setExtraThumbs(extrathumbs);
        gameToEdit.writeExtraImages(true, false);
      }

      // set extrafanarts
      if (extrafanarts.size() != gameToEdit.getExtraFanarts().size() || !extrafanarts.containsAll(gameToEdit.getExtraFanarts())
          || !gameToEdit.getExtraFanarts().containsAll(extrafanarts)) {
        // gameToEdit.downloadExtraFanarts(extrafanarts);
        gameToEdit.setExtraFanarts(extrafanarts);
        gameToEdit.writeExtraImages(false, true);
      }

      // gameToEdit.setDirector(tfDirector.getText());
      gameToEdit.setPublisher(tfPublisher.getText());
      gameToEdit.setProductionCompany(tfDeveloper.getText());
      gameToEdit.setActors(cast);
      gameToEdit.setGenres(genres);

      gameToEdit.removeAllTrailers();
      for (MediaTrailer trailer : trailers) {
        gameToEdit.addTrailer(trailer);
      }

      gameToEdit.setTags(tags);
      gameToEdit.setDateAdded((Date) spDateAdded.getValue());

      // game set
      Object obj = cbGameSet.getSelectedItem();
      if (obj instanceof String) {
        gameToEdit.removeFromGameSet();
        gameToEdit.setSortTitle(tfSorttitle.getText());
      }
      if (obj instanceof GameSet) {
        GameSet gameSet = (GameSet) obj;

        if (gameToEdit.getGameSet() != gameSet) {
          gameToEdit.removeFromGameSet();
          gameToEdit.setGameSet(gameSet);
          // gameSet.addGame(gameToEdit);
          gameSet.insertGame(gameToEdit);
        }

        // gameToEdit.setSortTitleFromGameSet();
        gameSet.updateGameSorttitle();
      }

      double tempRating = (Double) spRating.getValue();
      float rating = (float) tempRating;
      if (gameToEdit.getRating() != rating) {
        gameToEdit.setRating(rating);
        gameToEdit.setVotes(1);
      }

      gameToEdit.saveToDb();
      gameToEdit.writeNFO();
      setVisible(false);
      dispose();
    }
  }

  /**
   * The Class SwingAction_1.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_1 extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new swing action_1.
     */
    public SwingAction_1() {
      putValue(NAME, BUNDLE.getString("Button.cancel")); //$NON-NLS-1$
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("edit.discard")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      setVisible(false);
      dispose();
    }
  }

  /**
   * The Class SwingAction_4.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_4 extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new swing action_4.
     */
    public SwingAction_4() {
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("cast.actor.add")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      GameActor actor = new GameActor(BUNDLE.getString("cast.actor.unknown"), BUNDLE.getString("cast.role.unknown")); //$NON-NLS-1$
      cast.add(0, actor);
    }
  }

  /**
   * The Class SwingAction_5.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_5 extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new swing action_5.
     */
    public SwingAction_5() {
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("cast.actor.remove")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      int row = tableActors.getSelectedRow();
      if (row > -1) {
        row = tableActors.convertRowIndexToModel(row);
        cast.remove(row);
      }
    }
  }

  /**
   * The Class SwingAction_2.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_2 extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new swing action_2.
     */
    public SwingAction_2() {
      // putValue(NAME, "SwingAction_2");
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("genre.add")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      GameMediaGenres newGenre = null;
      Object item = cbGenres.getSelectedItem();

      // genre
      if (item instanceof GameMediaGenres) {
        newGenre = (GameMediaGenres) item;
      }

      // newly created genre?
      if (item instanceof String) {
        newGenre = GameMediaGenres.getGenre((String) item);
      }

      // add genre if it is not already in the list
      if (newGenre != null && !genres.contains(newGenre)) {
        genres.add(newGenre);
      }
    }
  }

  /**
   * The Class SwingAction_3.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_3 extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new swing action_3.
     */
    public SwingAction_3() {
      // putValue(NAME, "SwingAction_3");
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("genre.remove")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      GameMediaGenres newGenre = (GameMediaGenres) listGenres.getSelectedValue();
      // remove genre
      if (newGenre != null) {
        genres.remove(newGenre);
      }
    }
  }

  /**
   * The Class SwingAction_trailer_add.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_trailer_add extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new swing SwingAction_trailer_add.
     */
    public SwingAction_trailer_add() {
      // putValue(NAME, "SwingAction_trailer_add");
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("trailer.add")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      MediaTrailer trailer = new MediaTrailer();
      trailer.setName("unknown");
      trailer.setProvider("unknown");
      trailer.setQuality("unknown");
      trailer.setUrl("http://");
      trailers.add(0, trailer);
    }
  }

  /**
   * The Class SwingAction_trailer_remove.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_trailer_remove extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6956921050689930101L;

    /**
     * Instantiates a new swing SwingAction_trailer_remove.
     */
    public void SwingAction_trailer_remove() {
      // putValue(NAME, "SwingAction_trailer_remove");
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("trailer.remove")); //$NON-NLS-1$
    }

    /**
     * Instantiates a new swing action_8.
     * 
     * @return
     */
    public void SwingAction_trailer_remove_bis() {
      // putValue(NAME, "SwingAction_trailer_remove_bis");
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("trailer.remove")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      int row = tableTrailer.getSelectedRow();
      if (row > -1) {
        row = tableTrailer.convertRowIndexToModel(row);
        trailers.remove(row);
      }
    }
  }

  /**
   * The Class SwingAction_trailer_add.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_scraper_add extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new swing SwingAction_trailer_add.
     * 
     * @return
     */
    public void SwingAction_scraper__add() {
      // putValue(NAME, "SwingAction_trailer_add");
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("trailer.add")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      // ScraperId scraper = new ScraperId();
      // scraper.setScaperName("unknown");
      // scraper.setGameId("unknown");
      // scraper.setUrl("unknown");
      // scraperIds.add(0, scraper);
    }
  }

  /**
   * The Class SwingAction_trailer_remove.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_scraper_remove extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6956921050689930101L;

    /**
     * Instantiates a new swing SwingAction_trailer_remove.
     */
    public void SwingAction_scraper_remove() {
      // putValue(NAME, "SwingAction_trailer_remove");
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("trailer.remove")); //$NON-NLS-1$
    }

    /**
     * Instantiates a new swing action_8.
     * 
     * @return
     */
    public void SwingAction_scraper_remove_bis() {
      // putValue(NAME, "SwingAction_trailer_remove_bis");
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("trailer.remove")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      int row = tableScramper.getSelectedRow();
      if (row > -1) {
        row = tableScramper.convertRowIndexToModel(row);
        // scraperIds.remove(row);
      }
    }
  }

  /**
   * Inits the data bindings.
   */
  protected void initDataBindings() {
    JTableBinding<GameActor, List<GameActor>, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ, cast, tableActors);
    //
    BeanProperty<GameActor, String> gameCastBeanProperty = BeanProperty.create("name");
    jTableBinding.addColumnBinding(gameCastBeanProperty);
    //
    BeanProperty<GameActor, String> gameCastBeanProperty_1 = BeanProperty.create("character");
    jTableBinding.addColumnBinding(gameCastBeanProperty_1);
    //
    jTableBinding.bind();
    //
    JListBinding<GameMediaGenres, List<GameMediaGenres>, JList> jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ, genres,
        listGenres);
    jListBinding.bind();
    //
    JTableBinding<MediaTrailer, List<MediaTrailer>, JTable> jTableBinding_1 = SwingBindings.createJTableBinding(UpdateStrategy.READ, trailers,
        tableTrailer);
    //
    BeanProperty<MediaTrailer, Boolean> trailerBeanProperty = BeanProperty.create("inNfo");
    jTableBinding_1.addColumnBinding(trailerBeanProperty).setColumnClass(Boolean.class).setEditable(true);
    //
    BeanProperty<MediaTrailer, String> trailerBeanProperty_1 = BeanProperty.create("name");
    jTableBinding_1.addColumnBinding(trailerBeanProperty_1);
    //
    BeanProperty<MediaTrailer, String> trailerBeanProperty_2 = BeanProperty.create("provider");
    jTableBinding_1.addColumnBinding(trailerBeanProperty_2);
    //
    BeanProperty<MediaTrailer, String> trailerBeanProperty_3 = BeanProperty.create("quality");
    jTableBinding_1.addColumnBinding(trailerBeanProperty_3);
    //
    BeanProperty<MediaTrailer, String> trailerBeanProperty_4 = BeanProperty.create("url");
    jTableBinding_1.addColumnBinding(trailerBeanProperty_4);
    //
    jTableBinding_1.bind();
    //
    //
    // JTableBinding<ScraperId, List<ScraperId>, JTable> jTableBinding_11 = SwingBindings.createJTableBinding(UpdateStrategy.READ, scraperIds,
    // tableScramper);
    // //
    // BeanProperty<ScraperId, String> trailerBeanProperty_10 = BeanProperty.create("scaperName");
    // jTableBinding_11.addColumnBinding(trailerBeanProperty_10);
    // //
    // BeanProperty<ScraperId, String> trailerBeanProperty_11 = BeanProperty.create("gameId");
    // jTableBinding_11.addColumnBinding(trailerBeanProperty_11);
    // //
    // BeanProperty<ScraperId, String> trailerBeanProperty_12 = BeanProperty.create("url");
    // jTableBinding_11.addColumnBinding(trailerBeanProperty_12);
    // //
    // jTableBinding_11.bind();
    //
    BeanProperty<GameList, List<String>> gameListBeanProperty = BeanProperty.create("tagsInGames");
    JComboBoxBinding<String, GameList, JComboBox> jComboBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ, gameList,
        gameListBeanProperty, cbTags);
    jComboBinding.bind();
    //
    JListBinding<String, List<String>, JList> jListBinding_1 = SwingBindings.createJListBinding(UpdateStrategy.READ, tags, listTags);
    jListBinding_1.bind();
  }

  /**
   * Shows the dialog and returns whether the work on the queue should be continued.
   * 
   * @return true, if successful
   */
  public boolean showDialog() {
    setLocationRelativeTo(MainWindow.getActiveInstance());
    setVisible(true);
    return continueQueue;
  }

  /**
   * The Class SwingAction_trailer_remove_bis.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_trailer_remove_bis extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9160043031922897785L;

    /**
     * Instantiates a new swing SwingAction_trailer_remove_bis.
     */
    public SwingAction_trailer_remove_bis() {
      // putValue(NAME, "SwingAction_trailer_remove_bis");
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("tag.add")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      String newTag = (String) cbTags.getSelectedItem();
      boolean tagFound = false;

      // do not continue with empty tags
      if (StringUtils.isBlank(newTag)) {
        return;
      }

      // search if this tag already has been added
      for (String tag : tags) {
        if (tag.equals(newTag)) {
          tagFound = true;
          break;
        }
      }

      // add tag
      if (!tagFound) {
        tags.add(newTag);
      }
    }
  }

  /**
   * The Class SwingAction_9.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_9 extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1580945350962234235L;

    /**
     * Instantiates a new swing action_9.
     */
    public SwingAction_9() {
      // putValue(NAME, "SwingAction_9");
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("tag.remove")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      String tag = (String) listTags.getSelectedValue();
      tags.remove(tag);
    }
  }

  /**
   * The Class ToggleGameSetAction.
   * 
   * @author Manuel Laggner
   */
  private class ToggleGameSetAction extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5666621763248388091L;

    /**
     * Instantiates a new toggle game set action.
     */
    public ToggleGameSetAction() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      toggleSorttitle();
    }
  }

  /**
   * The Class SwingAction_10.
   * 
   * @author Manuel Laggner
   */
  private class SwingAction_10 extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7652218354710642510L;

    /**
     * Instantiates a new swing action_10.
     */
    public SwingAction_10() {
      putValue(NAME, BUNDLE.getString("Button.abortqueue")); //$NON-NLS-1$
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("game.edit.abortqueue.desc")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      continueQueue = false;
      setVisible(false);
      dispose();
    }
  }
}

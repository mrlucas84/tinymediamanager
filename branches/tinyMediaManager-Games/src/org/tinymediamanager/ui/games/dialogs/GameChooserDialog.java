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
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.core.game.GameScraperMetadataConfig;
import org.tinymediamanager.core.game.GameScrapers;
import org.tinymediamanager.core.platform.Platforms;
import org.tinymediamanager.scraper.IMediaArtworkProvider;
import org.tinymediamanager.scraper.IMediaMetadataProvider;
import org.tinymediamanager.scraper.IMediaTrailerProvider;
import org.tinymediamanager.scraper.MediaArtwork;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaSearchResult;
import org.tinymediamanager.scraper.MediaTrailer;
import org.tinymediamanager.scraper.MediaType;
import org.tinymediamanager.ui.EqualsLayout;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.TmmWindowSaver;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.ImageLabel;
import org.tinymediamanager.ui.dialogs.ImageChooserDialog;
import org.tinymediamanager.ui.dialogs.ImageChooserDialog.ImageType;
import org.tinymediamanager.ui.games.GameChooserModel;
import org.tinymediamanager.ui.games.GameScraperMetadataPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameChooser.
 * 
 * @author Manuel Laggner
 */
public class GameChooserDialog extends JDialog implements ActionListener {

  private static final long                                               serialVersionUID      = -3104541519073924724L;
  private static final ResourceBundle                                     BUNDLE                = ResourceBundle.getBundle(
                                                                                                    "messages", new UTF8Control());                    //$NON-NLS-1$
  private static final Logger                                             LOGGER                = LoggerFactory.getLogger(GameChooserDialog.class);

  private GameList                                                        gameList              = GameList.getInstance();
  private Game                                                            gameToScrape;
  private List<GameChooserModel>                                          gamesFound            = ObservableCollections
                                                                                                    .observableList(new ArrayList<GameChooserModel>());
  private GameScraperMetadataConfig                                       scraperMetadataConfig = new GameScraperMetadataConfig();
  private IMediaMetadataProvider                                          metadataProvider;
  private List<IMediaArtworkProvider>                                     artworkProviders;
  private List<IMediaTrailerProvider>                                     trailerProviders;
  private boolean                                                         continueQueue         = true;
  private SearchTask                                                      activeSearchTask      = null;

  private final JPanel                                                    contentPanel          = new JPanel();
  private JTextField                                                      textFieldSearchString;
  private JComboBox                                                       cbScraper;
  private JComboBox                                                       cbMachine;
  private JTable                                                          table;
  private JTextArea                                                       lblGameName;
  private JTextPane                                                       tpGameDescription;
  private ImageLabel                                                      lblGamePoster;
  private JLabel                                                          lblProgressAction;
  private JProgressBar                                                    progressBar;
  private JTextArea                                                       lblTagline;
  private JButton                                                         okButton;
  private JLabel                                                          lblPath;

  private JTableBinding<GameChooserModel, List<GameChooserModel>, JTable> jTableBinding;
  private AutoBinding<JTable, String, JTextPane, String>                  autoBinding_1;
  private AutoBinding<JTable, String, ImageLabel, String>                 autoBinding_2;
  private AutoBinding<JTable, String, JTextArea, String>                  autoBinding_3;

  /**
   * Create the dialog.
   * 
   * @param game
   *          the game
   * @param inQueue
   *          the in queue
   */
  public GameChooserDialog(Game game, boolean inQueue) {
    setTitle(BUNDLE.getString("gamechooser.search")); //$NON-NLS-1$
    setName("gameChooser");
    setBounds(5, 5, 800, 500);
    TmmWindowSaver.loadSettings(this);
    setIconImage(Globals.logo);
    setModal(true);

    // copy the values
    GameScraperMetadataConfig settings = Globals.settings.getGameScraperMetadataConfig();
    metadataProvider = gameList.getMetadataProvider();
    artworkProviders = gameList.getArtworkProviders();
    trailerProviders = gameList.getTrailerProviders();

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
    scraperMetadataConfig.setCollection(settings.isCollection());

    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("800px:grow"),
        FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, RowSpec.decode("fill:300px:grow"),
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, RowSpec.decode("default:grow"), FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, }));
    {
      lblPath = new JLabel("");
      contentPanel.add(lblPath, "2, 2");
    }
    {
      JPanel panelSearchField = new JPanel();
      contentPanel.add(panelSearchField, "2, 4, fill, fill");
      panelSearchField.setLayout(new FormLayout(new ColumnSpec[] // column
          { FormFactory.LABEL_COMPONENT_GAP_COLSPEC, //
              FormFactory.DEFAULT_COLSPEC, // scraper text
              FormFactory.RELATED_GAP_COLSPEC, //
              FormFactory.DEFAULT_COLSPEC, // scrape name
              FormFactory.UNRELATED_GAP_COLSPEC, //
              FormFactory.DEFAULT_COLSPEC, // machine text
              FormFactory.UNRELATED_GAP_COLSPEC, //
              FormFactory.DEFAULT_COLSPEC, // machine
              FormFactory.UNRELATED_GAP_COLSPEC, //
              ColumnSpec.decode("default:grow"), // text
              FormFactory.RELATED_GAP_COLSPEC, //
              ColumnSpec.decode("right:max(100px;default)"), // Button

          }

          , new RowSpec[] // row
          { FormFactory.DEFAULT_ROWSPEC, }));
      {
        JLabel lblScraper = new JLabel(BUNDLE.getString("scraper")); //$NON-NLS-1$
        panelSearchField.add(lblScraper, "2, 1, right, default");
      }
      {
        cbScraper = new JComboBox(GameScrapers.values());
        GameScrapers defaultScraper = Globals.settings.getGameSettings().getGameScraper();
        cbScraper.setSelectedItem(defaultScraper);
        cbScraper.setAction(new ChangeScraperAction());
        panelSearchField.add(cbScraper, "4, 1, fill, default");
      }
      {
        JLabel lblScraper = new JLabel(BUNDLE.getString("platform")); //$NON-NLS-1$
        panelSearchField.add(lblScraper, "6, 1, right, default");
      }
      {
        cbMachine = new JComboBox();
        cbMachine.addItem("All");
        for (String p : Globals.settings.getGameSettings().getPlatformGame()) {
          cbMachine.addItem((String) p);
        }
        cbMachine.setSelectedItem(0);
        cbMachine.setAction(new ChangeScraperAction());
        panelSearchField.add(cbMachine, "8, 1, fill, default");
      }

      {
        textFieldSearchString = new JTextField();
        panelSearchField.add(textFieldSearchString, "10, 1, fill, default");
        textFieldSearchString.setColumns(10);
      }

      {
        JButton btnSearch = new JButton(BUNDLE.getString("Button.search")); //$NON-NLS-1$
        panelSearchField.add(btnSearch, "12, 1, fill, default");
        btnSearch.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            searchGame(textFieldSearchString.getText(), null, (String) cbMachine.getSelectedItem());
          }
        });
        getRootPane().setDefaultButton(btnSearch);
      }
    }
    {
      JSplitPane splitPane = new JSplitPane();
      splitPane.setResizeWeight(0.5);
      splitPane.setContinuousLayout(true);
      contentPanel.add(splitPane, "2, 6, fill, fill");
      {
        JPanel panelSearchResults = new JPanel();
        splitPane.setLeftComponent(panelSearchResults);
        panelSearchResults.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("200px:grow"), },
            new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("fill:150px:grow"), }));
        {
          {
            JScrollPane scrollPane = new JScrollPane();
            panelSearchResults.add(scrollPane, "2, 2, fill, fill");
            table = new JTable();
            scrollPane.setViewportView(table);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setBorder(new LineBorder(new Color(0, 0, 0)));
            ListSelectionModel rowSM = table.getSelectionModel();
            rowSM.addListSelectionListener(new ListSelectionListener() {
              public void valueChanged(ListSelectionEvent e) {
                // Ignore extra messages.
                if (e.getValueIsAdjusting())
                  return;

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()) {
                  int selectedRow = lsm.getMinSelectionIndex();
                  selectedRow = table.convertRowIndexToModel(selectedRow);
                  try {
                    GameChooserModel model = gamesFound.get(selectedRow);
                    if (model != GameChooserModel.emptyResult && !model.isScraped()) {
                      ScrapeTask task = new ScrapeTask(model);
                      task.execute();
                    }
                  }
                  catch (Exception ex) {
                    LOGGER.warn(ex.getMessage());
                  }
                }
              }
            });
          }
        }
      }
      {
        JPanel panelSearchDetail = new JPanel();
        splitPane.setRightComponent(panelSearchDetail);
        panelSearchDetail.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("350px:grow"),
            FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.PARAGRAPH_GAP_ROWSPEC, RowSpec.decode("fill:150px:grow"), }));
        {
          lblGameName = new JTextArea("");
          lblGameName.setLineWrap(true);
          lblGameName.setOpaque(false);
          lblGameName.setWrapStyleWord(true);
          lblGameName.setFont(new Font("Dialog", Font.BOLD, 14));
          panelSearchDetail.add(lblGameName, "2, 1, default, top");
        }
        // {
        // lblTagline = new JTextArea("");
        // lblTagline.setLineWrap(true);
        // lblTagline.setOpaque(false);
        // lblTagline.setWrapStyleWord(true);
        // lblTagline.setEditable(false);
        // panelSearchDetail.add(lblTagline, "2, 2, default, top");
        // }
        {
          JPanel panel = new JPanel();
          panelSearchDetail.add(panel, "2, 4, fill, fill");
          panel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("150px"), FormFactory.RELATED_GAP_COLSPEC,
              ColumnSpec.decode("200px:grow"), }, new RowSpec[] { RowSpec.decode("240px"), }));
          {
            lblGamePoster = new ImageLabel(false);
            panel.add(lblGamePoster, "1, 1, fill, fill");
            lblGamePoster.setAlternativeText("");
          }
          {
            JScrollPane scrollPane = new JScrollPane();
            panel.add(scrollPane, "3, 1, fill, fill");
            scrollPane.setBorder(null);
            {
              tpGameDescription = new JTextPane();
              tpGameDescription.setOpaque(false);
              tpGameDescription.setEditable(false);
              tpGameDescription.setContentType("text/html");
              scrollPane.setViewportView(tpGameDescription);
            }
          }
        }
      }
    }
    {
      JLabel lblScrapeFollowingItems = new JLabel(BUNDLE.getString("chooser.scrape")); //$NON-NLS-1$
      contentPanel.add(lblScrapeFollowingItems, "2, 8");
    }
    {
      JPanel panelScraperMetadataSetting = new GameScraperMetadataPanel(scraperMetadataConfig);
      contentPanel.add(panelScraperMetadataSetting, "2, 9, fill, fill");
    }

    {
      JPanel bottomPane = new JPanel();
      contentPanel.add(bottomPane, "2, 11");
      {
        bottomPane.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("max(82dlu;default)"),
            FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
            FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("25px"), }));
        {
          progressBar = new JProgressBar();
          bottomPane.add(progressBar, "2, 2");
        }
        {
          lblProgressAction = new JLabel("");
          bottomPane.add(lblProgressAction, "4, 2");
        }
        {
          JPanel buttonPane = new JPanel();
          bottomPane.add(buttonPane, "5, 2, fill, fill");
          EqualsLayout layout = new EqualsLayout(5);
          layout.setMinWidth(100);
          buttonPane.setLayout(layout);
          okButton = new JButton(BUNDLE.getString("Button.ok")); //$NON-NLS-1$
          buttonPane.add(okButton);
          okButton.setActionCommand("OK");
          okButton.addActionListener(this);

          JButton cancelButton = new JButton(BUNDLE.getString("Button.cancel")); //$NON-NLS-1$
          buttonPane.add(cancelButton);
          cancelButton.setActionCommand("Cancel");
          cancelButton.addActionListener(this);

          if (inQueue) {
            JButton abortButton = new JButton(BUNDLE.getString("Button.abortqueue")); //$NON-NLS-1$
            buttonPane.add(abortButton);
            abortButton.setActionCommand("Abort");
            abortButton.addActionListener(this);
          }
        }
      }
    }

    {
      gameToScrape = game;
      progressBar.setVisible(false);
      initDataBindings();

      //
      String lman = game.getPlatform();

      boolean f = false;
      if (Platforms.getInstance().isPlatformName(lman)) {

        for (String m : Globals.settings.getGameSettings().getPlatformGame()) {
          if (m.equalsIgnoreCase(lman)) {
            cbMachine.setSelectedItem(lman);
            f = true;
            break;
          }
        }
        if (!f) {
          cbMachine.setSelectedItem("All");
        }
      }
      else {
        lman = "All";
        cbMachine.setSelectedItem("All");
      }

      // adjust column name
      table.getColumnModel().getColumn(0).setHeaderValue(BUNDLE.getString("chooser.searchresult"));
      lblPath.setText(gameToScrape.getPath());
      textFieldSearchString.setText(gameToScrape.getTitle());
      startProgressBar(BUNDLE.getString("chooser.scrapeing") + " " + gameToScrape.getTitle()); //$NON-NLS-1$
      searchGame(textFieldSearchString.getText(), gameToScrape, lman);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  /**
   * Action performed.
   * 
   * @param e
   *          the e
   */
  public void actionPerformed(ActionEvent e) {
    if ("OK".equals(e.getActionCommand())) {
      int row = table.getSelectedRow();
      if (row >= 0) {
        GameChooserModel model = gamesFound.get(row);
        if (model != GameChooserModel.emptyResult) {
          MediaMetadata md = model.getMetadata();

          // did the user want to choose the images?
          if (!Globals.settings.getGameSettings().isScrapeBestImage()) {
            md.clearMediaArt();
          }

          // set scraped metadata
          gameToScrape.setMetadata(md, scraperMetadataConfig);

          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

          // get images?
          if (scraperMetadataConfig.isArtwork()) {
            // let the user choose the images
            if (!Globals.settings.getGameSettings().isScrapeBestImage()) {
              // poster
              {
                ImageLabel lblImage = new ImageLabel();
                ImageType itype = ImageChooserDialog.ImageType.POSTER;
                ImageChooserDialog dialog = new ImageChooserDialog(gameToScrape.getIds(), itype, artworkProviders, lblImage, null, null,
                    MediaType.GAME);
                dialog.setLocationRelativeTo(MainWindow.getActiveInstance());
                dialog.setVisible(true);
                gameToScrape.setPosterUrl(lblImage.getImageUrl());
                gameToScrape.writeImages(true, false);
              }

              // fanart
              {
                ImageLabel lblImage = new ImageLabel();
                List<String> extrathumbs = new ArrayList<String>();
                List<String> extrafanarts = new ArrayList<String>();
                ImageChooserDialog dialog = new ImageChooserDialog(gameToScrape.getIds(), ImageType.FANART, artworkProviders, lblImage, extrathumbs,
                    extrafanarts, MediaType.GAME);
                dialog.setVisible(true);
                gameToScrape.setFanartUrl(lblImage.getImageUrl());
                gameToScrape.writeImages(false, true);

                // set extrathumbs and extrafanarts
                gameToScrape.setExtraThumbs(extrathumbs);
                gameToScrape.setExtraFanarts(extrafanarts);
                if (extrafanarts.size() > 0 || extrathumbs.size() > 0) {
                  gameToScrape.writeExtraImages(true, true);
                }
              }
            }
            else {
              // get artwork directly from provider
              //
              List<MediaArtwork> artwork = model.getArtwork();

              gameToScrape.setArtwork(artwork, scraperMetadataConfig);
            }
          }

          // get trailers?
          if (scraperMetadataConfig.isTrailer()) {
            List<MediaTrailer> trailers = model.getTrailers();
            // add local trailers!
            for (MediaFile mf : gameToScrape.getMediaFiles(MediaFileType.TRAILER)) {
              LOGGER.debug("adding local trailer " + mf.getFilename());
              MediaTrailer mt = new MediaTrailer();
              mt.setName(mf.getFilename());
              mt.setProvider("downloaded");
              mt.setQuality(mf.getVideoFormat());
              mt.setInNfo(false);
              mt.setUrl(mf.getFile().toURI().toString());
              trailers.add(0, mt); // add as first
            }
            gameToScrape.setTrailers(trailers);
          }

          // rewrite the complete NFO
          gameToScrape.writeNFO();

          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

          this.setVisible(false);
          dispose();
        }
      }
    }

    // cancel
    if ("Cancel".equals(e.getActionCommand())) {
      this.setVisible(false);
      dispose();
    }

    // Abort queue
    if ("Abort".equals(e.getActionCommand())) {
      continueQueue = false;
      this.setVisible(false);
      dispose();
    }

  }

  private void searchGame(String searchTerm, Game game, String machine) {
    if (activeSearchTask != null && !activeSearchTask.isDone()) {
      activeSearchTask.cancel();
    }
    activeSearchTask = new SearchTask(searchTerm, game, machine, false);
    activeSearchTask.execute();
  }

  private void startProgressBar(final String description) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        lblProgressAction.setText(description);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
      }
    });
  }

  private void stopProgressBar() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        lblProgressAction.setText("");
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
      }
    });
  }

  /**
   * Inits the data bindings.
   */
  protected void initDataBindings() {
    jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ, gamesFound, table);
    //
    BeanProperty<GameChooserModel, String> gameChooserModelBeanProperty = BeanProperty.create("combinedName");
    jTableBinding.addColumnBinding(gameChooserModelBeanProperty).setEditable(false); //$NON-NLS-1$
    //
    jTableBinding.bind();
    //
    BeanProperty<JTable, String> jTableBeanProperty_1 = BeanProperty.create("selectedElement.tagline");
    BeanProperty<JTextPane, String> jTextPaneBeanProperty = BeanProperty.create("text");
    autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ, table, jTableBeanProperty_1, tpGameDescription, jTextPaneBeanProperty);
    autoBinding_1.bind();
    //
    BeanProperty<JTable, String> jTableBeanProperty_2 = BeanProperty.create("selectedElement.posterUrl");
    BeanProperty<ImageLabel, String> imageLabelBeanProperty = BeanProperty.create("imageUrl");
    autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, table, jTableBeanProperty_2, lblGamePoster, imageLabelBeanProperty);
    autoBinding_2.bind();
    //
    // BeanProperty<JTable, String> jTableBeanProperty = BeanProperty.create("selectedElement.tagline");
    // BeanProperty<JTextArea, String> jTextAreaBeanProperty = BeanProperty.create("text");
    // AutoBinding<JTable, String, JTextArea, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, table, jTableBeanProperty,
    // lblTagline, jTextAreaBeanProperty);
    // autoBinding.bind();
    //
    BeanProperty<JTable, String> jTableBeanProperty_3 = BeanProperty.create("selectedElement.combinedName");
    BeanProperty<JTextArea, String> jTextAreaBeanProperty_1 = BeanProperty.create("text");
    autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, table, jTableBeanProperty_3, lblGameName, jTextAreaBeanProperty_1);
    autoBinding_3.bind();
  }

  /**
   * Shows the dialog and returns whether the work on the queue should be continued.
   * 
   * @return true, if successful
   */
  public boolean showDialog() {
    pack();
    setLocationRelativeTo(MainWindow.getActiveInstance());
    setVisible(true);
    return continueQueue;
  }

  @Override
  public void dispose() {
    super.dispose();
    jTableBinding.unbind();
    autoBinding_1.unbind();
    autoBinding_2.unbind();
    autoBinding_3.unbind();
  }

  /**********************************************************************************
   * helper classes
   **********************************************************************************/
  private class SearchTask extends SwingWorker<Void, Void> {
    private String                  searchTerm;
    private Game                    game;
    private String                  machine;
    private List<MediaSearchResult> searchResult;
    boolean                         cancel = false;

    public SearchTask(String searchTerm, Game game, String machine, boolean force) {
      this.searchTerm = searchTerm;
      this.game = game;
      this.machine = machine;
    }

    @Override
    public Void doInBackground() {
      startProgressBar(BUNDLE.getString("chooser.searchingfor") + " " + searchTerm); //$NON-NLS-1$
      searchResult = gameList.searchGame(false, searchTerm, game, machine, metadataProvider);
      return null;
    }

    public void cancel() {
      cancel = true;
    }

    @Override
    public void done() {
      if (!cancel) {
        gamesFound.clear();
        if (searchResult.size() == 0) {
          // display empty result
          gamesFound.add(GameChooserModel.emptyResult);
        }
        else {
          for (MediaSearchResult result : searchResult) {
            gamesFound.add(new GameChooserModel(metadataProvider, artworkProviders, trailerProviders, game, result));
          }
        }
        if (gamesFound.size() == 1) { // only one result
          table.setRowSelectionInterval(0, 0); // select first row
        }
      }
      stopProgressBar();
    }
  }

  private class ScrapeTask extends SwingWorker<Void, Void> {
    private GameChooserModel model;

    public ScrapeTask(GameChooserModel model) {
      this.model = model;
    }

    @Override
    public Void doInBackground() {
      startProgressBar(BUNDLE.getString("chooser.scrapeing") + " " + model.getName()); //$NON-NLS-1$

      // disable button as long as its scraping
      okButton.setEnabled(false);
      model.scrapeMetaData();
      okButton.setEnabled(true);
      return null;
    }

    @Override
    public void done() {
      stopProgressBar();
    }
  }

  private class ChangeScraperAction extends AbstractAction {
    private static final long serialVersionUID = -4365761222995534769L;

    @Override
    public void actionPerformed(ActionEvent e) {

      GameScrapers selectedScraper = (GameScrapers) cbScraper.getSelectedItem();
      metadataProvider = GameList.getInstance().getMetadataProvider(selectedScraper);
      searchGame(textFieldSearchString.getText(), gameToScrape, (String) cbMachine.getSelectedItem());
    }
  }
}

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
package org.tinymediamanager.ui.gamesets.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringUtils;
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
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameSet;
import org.tinymediamanager.scraper.giantbomb.giantbombMetadataProvider;
import org.tinymediamanager.ui.EqualsLayout;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.TmmWindowSaver;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.ImageLabel;
import org.tinymediamanager.ui.gamesets.GameSetChooserModel;
import org.tinymediamanager.ui.gamesets.GameSetChooserModel.GameInSet;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameSetChooserPanel.
 * 
 * @author Manuel Laggner
 */
public class GameSetChooserDialog extends JDialog implements ActionListener {

  private static final long           serialVersionUID = -1023959850452480592L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control());                   //$NON-NLS-1$
  private static final Logger         LOGGER           = LoggerFactory.getLogger(GameSetChooserDialog.class);

  private GameSet                     gameSetToScrape;
  private List<GameSetChooserModel>   gameSetsFound    = ObservableCollections.observableList(new ArrayList<GameSetChooserModel>());
  private final Action                actionSearch     = new SearchAction();
  private boolean                     continueQueue    = true;

  private JLabel                      lblProgressAction;
  private JProgressBar                progressBar;
  private JTextField                  tfGameSetName;
  private JTable                      tableGameSets;
  private JTextArea                   lblGameSetName;
  private ImageLabel                  lblGameSetPoster;
  private JTable                      tableGames;
  private JCheckBox                   cbAssignGames;
  private JButton                     btnOk;

  /**
   * Instantiates a new game set chooser panel.
   * 
   * @param gameSet
   *          the game set
   */
  public GameSetChooserDialog(GameSet gameSet, boolean inQueue) {
    setTitle(BUNDLE.getString("gameset.search")); //$NON-NLS-1$
    setName("gameSetChooser");
    setBounds(5, 5, 865, 578);
    TmmWindowSaver.loadSettings(this);
    setIconImage(Globals.logo);
    setModal(true);

    gameSetToScrape = gameSet;

    getContentPane().setLayout(new BorderLayout(0, 0));

    JPanel panelHeader = new JPanel();
    getContentPane().add(panelHeader, BorderLayout.NORTH);
    panelHeader.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("114px:grow"),
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("100px"), ColumnSpec.decode("2dlu"), }, new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC,
        RowSpec.decode("25px"), FormFactory.RELATED_GAP_ROWSPEC, }));
    {
      tfGameSetName = new JTextField();
      panelHeader.add(tfGameSetName, "2, 2, fill, fill");
      tfGameSetName.setColumns(10);
    }
    {
      JButton btnSearch = new JButton("");
      btnSearch.setAction(actionSearch);
      panelHeader.add(btnSearch, "4, 2, fill, top");
    }
    {
      JSplitPane splitPane = new JSplitPane();
      splitPane.setContinuousLayout(true);
      splitPane.setResizeWeight(0.5);
      getContentPane().add(splitPane, BorderLayout.CENTER);
      {
        JPanel panelResults = new JPanel();
        panelResults.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("300px:grow"), }, new RowSpec[] {
            FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("fill:403px:grow"), }));
        JScrollPane panelSearchResults = new JScrollPane();
        panelResults.add(panelSearchResults, "2, 2, fill, fill");
        splitPane.setLeftComponent(panelResults);
        {
          tableGameSets = new JTable();
          panelSearchResults.setViewportView(tableGameSets);
          tableGameSets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          tableGameSets.setBorder(new LineBorder(new Color(0, 0, 0)));
          ListSelectionModel rowSM = tableGameSets.getSelectionModel();
          rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
              // Ignore extra messages.
              if (e.getValueIsAdjusting())
                return;

              ListSelectionModel lsm = (ListSelectionModel) e.getSource();
              if (!lsm.isSelectionEmpty()) {
                int selectedRow = lsm.getMinSelectionIndex();
                selectedRow = tableGameSets.convertRowIndexToModel(selectedRow);
                try {
                  GameSetChooserModel model = gameSetsFound.get(selectedRow);
                  if (model != GameSetChooserModel.emptyResult && !model.isScraped()) {
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
      {
        JPanel panelSearchDetail = new JPanel();
        splitPane.setRightComponent(panelSearchDetail);
        panelSearchDetail.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("left:150px"),
            FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(300px;default):grow"), FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
            FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("250px"), FormFactory.PARAGRAPH_GAP_ROWSPEC,
            RowSpec.decode("top:default:grow"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));
        {
          lblGameSetName = new JTextArea("");
          lblGameSetName.setLineWrap(true);
          lblGameSetName.setOpaque(false);
          lblGameSetName.setWrapStyleWord(true);
          lblGameSetName.setFont(new Font("Dialog", Font.BOLD, 14));
          panelSearchDetail.add(lblGameSetName, "2, 1, 3, 1, fill, top");
        }
        {
          lblGameSetPoster = new ImageLabel();
          lblGameSetPoster.setAlternativeText(BUNDLE.getString("image.notfound.poster")); //$NON-NLS-1$
          panelSearchDetail.add(lblGameSetPoster, "2, 3, fill, fill");
        }
        {
          JPanel panel = new JPanel();
          panelSearchDetail.add(panel, "4, 3, fill, fill");
          panel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
              FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
              FormFactory.DEFAULT_ROWSPEC, }));
        }
        {

          JScrollPane scrollPane = new JScrollPane();
          panelSearchDetail.add(scrollPane, "2, 5, 3, 1, fill, fill");
          {
            tableGames = new JTable();
            scrollPane.setViewportView(tableGames);
          }

        }
        {
          cbAssignGames = new JCheckBox(BUNDLE.getString("gameset.game.assign")); //$NON-NLS-1$
          cbAssignGames.setSelected(true);
          panelSearchDetail.add(cbAssignGames, "2, 7, 3, 1");
        }
      }
    }

    {
      JPanel bottomPane = new JPanel();
      getContentPane().add(bottomPane, BorderLayout.SOUTH);
      {
        bottomPane.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("2dlu"), ColumnSpec.decode("185px"),
            FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("18px:grow"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
            ColumnSpec.decode("2dlu"), }, new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("25px"), }));
        {
          progressBar = new JProgressBar();
          bottomPane.add(progressBar, "2, 2, fill, center");
        }
        {
          lblProgressAction = new JLabel("");
          bottomPane.add(lblProgressAction, "4, 2, fill, center");
        }
        {
          JPanel buttonPane = new JPanel();
          bottomPane.add(buttonPane, "6, 2, fill, fill");
          EqualsLayout layout = new EqualsLayout(5);
          layout.setMinWidth(100);
          buttonPane.setLayout(layout);

          btnOk = new JButton(BUNDLE.getString("Button.ok")); //$NON-NLS-1$
          btnOk.setActionCommand("Save");
          btnOk.setToolTipText(BUNDLE.getString("Button.ok")); //$NON-NLS-1$
          btnOk.addActionListener(this);
          buttonPane.add(btnOk);

          JButton btnCancel = new JButton(BUNDLE.getString("Button.cancel")); //$NON-NLS-1$
          btnCancel.setActionCommand("Cancel");
          btnCancel.setToolTipText(BUNDLE.getString("Button.cancel")); //$NON-NLS-1$
          btnCancel.addActionListener(this);
          buttonPane.add(btnCancel);

          if (inQueue) {
            JButton btnAbort = new JButton(BUNDLE.getString("Button.abortqueue")); //$NON-NLS-1$
            btnAbort.setActionCommand("Abort");
            btnAbort.setToolTipText(BUNDLE.getString("Button.abortqueue")); //$NON-NLS-1$
            btnCancel.addActionListener(this);
            buttonPane.add(btnAbort, "6, 1, fill, top");
          }
        }
      }
    }
    initDataBindings();

    // adjust table columns
    tableGames.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableGames.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    tableGameSets.getColumnModel().getColumn(0).setHeaderValue(BUNDLE.getString("chooser.searchresult"));
    {
      tfGameSetName.setText(gameSet.getTitle());
      searchGame();
    }

  }

  /**
   * Search game.
   * 
   */
  private void searchGame() {
    SearchTask task = new SearchTask(tfGameSetName.getText());
    task.execute();
  }

  /**
   * The Class SearchTask.
   * 
   * @author Manuel Laggner
   */
  private class SearchTask extends SwingWorker<Void, Void> {

    /** The search term. */
    private String searchTerm;

    /**
     * Instantiates a new search task.
     * 
     * @param searchTerm
     *          the search term
     */
    public SearchTask(String searchTerm) {
      this.searchTerm = searchTerm;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    public Void doInBackground() {
      startProgressBar(BUNDLE.getString("chooser.searchingfor") + " " + searchTerm); //$NON-NLS-1$
      giantbombMetadataProvider mp;
      // try {
      // mp = new giantbombMetadataProvider();
      // List<Collection> gameSets = mp.searchGameSets(searchTerm);
      // gameSetsFound.clear();
      // if (gameSets.size() == 0) {
      // gameSetsFound.add(GameSetChooserModel.emptyResult);
      // }
      // else {
      // for (Collection collection : gameSets) {
      // GameSetChooserModel model = new GameSetChooserModel(collection);
      // gameSetsFound.add(model);
      // }
      // }
      // }
      // catch (Exception e1) {
      // LOGGER.warn("SearchTask", e1);
      // }

      return null;
    }

    /*
     * Executed in event dispatching thread
     */
    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    public void done() {
      stopProgressBar();
    }
  }

  /**
   * The Class SearchAction.
   * 
   * @author Manuel Laggner
   */
  private class SearchAction extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new search action.
     */
    public SearchAction() {
      putValue(NAME, BUNDLE.getString("Button.search")); //$NON-NLS-1$
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("gameset.search")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      searchGame();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent arg0) {
    if ("Cancel".equals(arg0.getActionCommand())) {
      // cancel
      setVisible(false);
      dispose();
    }

    if ("Save".equals(arg0.getActionCommand())) {
      // save it
      int row = tableGameSets.getSelectedRow();
      if (row >= 0) {
        GameSetChooserModel model = gameSetsFound.get(row);
        if (model != GameSetChooserModel.emptyResult) {
          gameSetToScrape.setTitle(model.getName());

          if (StringUtils.isNotBlank(model.getInfo().getOverview())) {
            gameSetToScrape.setPlot(model.getInfo().getOverview());
          }
          else {
            gameSetToScrape.setPlot("");
          }
          gameSetToScrape.setPosterUrl(model.getPosterUrl());
          gameSetToScrape.setFanartUrl(model.getFanartUrl());
          gameSetToScrape.setGiantbombId(model.getGiantbombId());
          gameSetToScrape.saveToDb();

          // assign games
          if (cbAssignGames.isSelected()) {
            gameSetToScrape.removeAllGames();
            for (int i = 0; i < model.getGames().size(); i++) {
              GameInSet gameInSet = model.getGames().get(i);
              Game game = gameInSet.getGame();
              if (game == null) {
                continue;
              }

              // check if the found game contains a matching set
              if (game.getGameSet() != null) {
                // unassign game from set
                GameSet mSet = game.getGameSet();
                mSet.removeGame(game);
              }

              game.setGameSet(gameSetToScrape);
              game.setSortTitle(gameSetToScrape.getTitle() + String.format("%02d", i + 1));
              game.saveToDb();
              gameSetToScrape.addGame(game);

              game.writeNFO();
            }

            // and finally save assignments
            gameSetToScrape.saveToDb();
          }

        }
        setVisible(false);
        dispose();
      }
    }

    // Abort queue
    if ("Abort".equals(arg0.getActionCommand())) {
      continueQueue = false;
      this.setVisible(false);
      dispose();
    }
  }

  /**
   * The Class ScrapeTask.
   * 
   * @author Manuel Laggner
   */
  private class ScrapeTask extends SwingWorker<Void, Void> {

    /** The model. */
    private GameSetChooserModel model;

    /**
     * Instantiates a new scrape task.
     * 
     * @param model
     *          the model
     */
    public ScrapeTask(GameSetChooserModel model) {
      this.model = model;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    public Void doInBackground() {
      startProgressBar(BUNDLE.getString("chooser.scrapeing") + " " + model.getName()); //$NON-NLS-1$

      // disable ok button as long as its scraping
      btnOk.setEnabled(false);
      model.scrapeMetadata();
      btnOk.setEnabled(true);

      return null;
    }

    /*
     * Executed in event dispatching thread
     */
    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    public void done() {
      stopProgressBar();
    }
  }

  /**
   * Start progress bar.
   * 
   * @param description
   *          the description
   */
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

  /**
   * Stop progress bar.
   */
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
    JTableBinding<GameSetChooserModel, List<GameSetChooserModel>, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ,
        gameSetsFound, tableGameSets);
    //
    BeanProperty<GameSetChooserModel, String> gameSetChooserModelBeanProperty = BeanProperty.create("name");
    jTableBinding.addColumnBinding(gameSetChooserModelBeanProperty).setEditable(false); //$NON-NLS-1$
    //
    jTableBinding.bind();
    //
    BeanProperty<JTable, List<GameInSet>> jTableBeanProperty = BeanProperty.create("selectedElement.games");
    JTableBinding<GameInSet, JTable, JTable> jTableBinding_1 = SwingBindings.createJTableBinding(UpdateStrategy.READ, tableGameSets,
        jTableBeanProperty, tableGames);
    //
    BeanProperty<GameInSet, String> gameInSetBeanProperty = BeanProperty.create("name");
    jTableBinding_1.addColumnBinding(gameInSetBeanProperty).setColumnName(BUNDLE.getString("tmm.game")).setEditable(false); //$NON-NLS-1$
    //
    BeanProperty<GameInSet, String> gameInSetBeanProperty_2 = BeanProperty.create("game.title");
    jTableBinding_1.addColumnBinding(gameInSetBeanProperty_2).setColumnName(BUNDLE.getString("gameset.game.matched")).setEditable(false); //$NON-NLS-1$
    //
    jTableBinding_1.bind();
    //
    BeanProperty<JTable, String> jTableBeanProperty_1 = BeanProperty.create("selectedElement.name");
    BeanProperty<JTextArea, String> jTextAreaBeanProperty = BeanProperty.create("text");
    AutoBinding<JTable, String, JTextArea, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, tableGameSets, jTableBeanProperty_1,
        lblGameSetName, jTextAreaBeanProperty);
    autoBinding.bind();
    //
    BeanProperty<JTable, String> jTableBeanProperty_2 = BeanProperty.create("selectedElement.posterUrl");
    BeanProperty<ImageLabel, String> imageLabelBeanProperty = BeanProperty.create("imageUrl");
    AutoBinding<JTable, String, ImageLabel, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ, tableGameSets,
        jTableBeanProperty_2, lblGameSetPoster, imageLabelBeanProperty);
    autoBinding_1.bind();
  }

  /**
   * Shows the dialog and returns whether the work on the queue should be continued.
   * 
   * @return true, if successful
   */
  public boolean showDialog() {
    // pack();
    setLocationRelativeTo(MainWindow.getActiveInstance());
    setVisible(true);
    return continueQueue;
  }
}

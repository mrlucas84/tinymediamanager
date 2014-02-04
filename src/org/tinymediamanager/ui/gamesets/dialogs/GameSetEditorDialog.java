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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.core.game.GameSet;
import org.tinymediamanager.scraper.IMediaArtworkProvider;
import org.tinymediamanager.scraper.MediaScrapeOptions;
import org.tinymediamanager.scraper.tmdb.TmdbMetadataProvider;
import org.tinymediamanager.ui.EqualsLayout;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.TmmWindowSaver;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.ImageLabel;
import org.tinymediamanager.ui.dialogs.ImageChooserDialog.ImageType;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameSetEditor. Plot
 * 
 * @author Manuel Laggner
 */
public class GameSetEditorDialog extends JDialog {

  /** The Constant LOGGER. */
  private static final Logger         LOGGER             = LoggerFactory.getLogger(GameSetEditorDialog.class);

  /** The Constant BUNDLE. */
  private static final ResourceBundle BUNDLE             = ResourceBundle.getBundle("messages", new UTF8Control());    //$NON-NLS-1$

  /** The Constant serialVersionUID. */
  private static final long           serialVersionUID   = -4446433759280691976L;

  /** The game set to edit. */
  private GameSet                     gameSetToEdit;

  /** The tf name. */
  private JTextField                  tfName;

  /** The table games. */
  private JTable                      tableGames;

  /** The lbl poster. */
  private ImageLabel                  lblPoster;

  /** The lbl fanart. */
  private ImageLabel                  lblFanart;

  /** The tp overview. */
  private JTextPane                   tpOverview;

  /** The games in set. */
  private List<Game>                  gamesInSet         = ObservableCollections.observableList(new ArrayList<Game>());

  /** The removed games. */
  private List<Game>                  removedGames       = new ArrayList<Game>();

  /** The action remove game. */
  private final Action                actionRemoveGame   = new RemoveGameAction();

  /** The action move game up. */
  private final Action                actionMoveGameUp   = new MoveUpAction();

  /** The action move game down. */
  private final Action                actionMoveGameDown = new MoveDownAction();

  /** The action ok. */
  private final Action                actionOk           = new OkAction();

  /** The action cancel. */
  private final Action                actionCancel       = new CancelAction();

  /** The action abort. */
  private final Action                actionAbort        = new AbortAction();

  /** The tf tmdb id. */
  private JTextField                  tfTmdbId;

  /** The action search tmdb id. */
  private final Action                actionSearchTmdbId = new SwingAction();

  /** The artwork providers. */
  private List<IMediaArtworkProvider> artworkProviders   = new ArrayList<IMediaArtworkProvider>();

  /** The continue queue. */
  private boolean                     continueQueue      = true;

  /**
   * Instantiates a new game set editor.
   * 
   * @param gameSet
   *          the game set
   * @param inQueue
   *          the in queue
   */
  public GameSetEditorDialog(GameSet gameSet, boolean inQueue) {
    setModal(true);
    setIconImage(Globals.logo);
    setTitle(BUNDLE.getString("gameset.edit")); //$NON-NLS-1$
    setName("gameSetEditor");
    setBounds(5, 5, 800, 500);
    TmmWindowSaver.loadSettings(this);

    gameSetToEdit = gameSet;
    try {
      artworkProviders.add(new TmdbMetadataProvider());
    }
    catch (Exception e2) {
      LOGGER.warn("error getting IMediaArtworkProvider " + e2.getMessage());
    }

    getContentPane().setLayout(new BorderLayout());

    JPanel panelContent = new JPanel();
    panelContent.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("2dlu"), FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("100px"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("300px:grow"), FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("250px:grow"), ColumnSpec.decode("2dlu"), }, new RowSpec[] { FormFactory.NARROW_LINE_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
        RowSpec.decode("75px:grow"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormFactory.NARROW_LINE_GAP_ROWSPEC, }));
    getContentPane().add(panelContent, BorderLayout.CENTER);

    JLabel lblName = new JLabel(BUNDLE.getString("gameset.title")); //$NON-NLS-1$
    panelContent.add(lblName, "2, 2, right, default");

    tfName = new JTextField();
    panelContent.add(tfName, "4, 2, 3, 1, fill, default");
    tfName.setColumns(10);

    lblPoster = new ImageLabel();
    lblPoster.setAlternativeText(BUNDLE.getString("image.notfound.poster")); //$NON-NLS-1$
    lblPoster.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int tmdbId = 0;
        try {
          tmdbId = Integer.parseInt(tfTmdbId.getText());
        }
        catch (Exception e1) {
        }
        HashMap<String, Object> ids = new HashMap<String, Object>(gameSetToEdit.getIds());
        ids.put("tmdbId", tmdbId);

        // /// ADE ..........
        // GameSetImageChooserDialog dialog = new GameSetImageChooserDialog(tmdbId, ImageType.POSTER, lblPoster);
        // GameImageChooserDialog dialog = new GameImageChooserDialog(, ImageType.POSTER, artworkProviders, lblPoster, null, null);
        // dialog.setLocationRelativeTo(MainWindow.getActiveInstance());
        // dialog.setVisible(true);
      }
    });
    panelContent.add(lblPoster, "8, 2, 1, 9, fill, fill");

    JLabel lblTmdbid = new JLabel(BUNDLE.getString("metatag.tmdb")); //$NON-NLS-1$
    panelContent.add(lblTmdbid, "2, 4, right, default");

    tfTmdbId = new JTextField();
    panelContent.add(tfTmdbId, "4, 4, fill, default");
    tfTmdbId.setColumns(10);

    JButton btnSearchTmdbId = new JButton("");
    btnSearchTmdbId.setAction(actionSearchTmdbId);
    panelContent.add(btnSearchTmdbId, "6, 4, left, default");

    JLabel lblOverview = new JLabel(BUNDLE.getString("metatag.plot")); //$NON-NLS-1$
    panelContent.add(lblOverview, "2, 6, right, top");

    JScrollPane scrollPaneOverview = new JScrollPane();
    panelContent.add(scrollPaneOverview, "4, 6, 3, 1, fill, fill");

    tpOverview = new JTextPane();
    scrollPaneOverview.setViewportView(tpOverview);

    JLabel lblGames = new JLabel(BUNDLE.getString("tmm.games")); //$NON-NLS-1$
    panelContent.add(lblGames, "2, 8, right, top");

    JScrollPane scrollPaneGames = new JScrollPane();
    panelContent.add(scrollPaneGames, "4, 8, 3, 9, fill, fill");

    tableGames = new JTable();
    scrollPaneGames.setViewportView(tableGames);

    JButton btnRemoveGame = new JButton("");
    btnRemoveGame.setAction(actionRemoveGame);
    panelContent.add(btnRemoveGame, "2, 10, right, top");

    JButton btnMoveGameUp = new JButton("");
    btnMoveGameUp.setAction(actionMoveGameUp);
    panelContent.add(btnMoveGameUp, "2, 12, right, top");

    lblFanart = new ImageLabel();
    lblFanart.setAlternativeText(BUNDLE.getString("image.notfound.fanart")); //$NON-NLS-1$
    lblFanart.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int tmdbId = 0;
        try {
          tmdbId = Integer.parseInt(tfTmdbId.getText());
        }
        catch (Exception e1) {
        }
        HashMap<String, Object> ids = new HashMap<String, Object>(gameSetToEdit.getIds());
        ids.put("tmdbId", tmdbId);

        List<String> a = new ArrayList<String>();
        ImageType itype = ImageType.FANART;

        // / ADEE
        // GameImageChooserDialog dialog = new GameImageChooserDialog(ids, ImageType.FANART, artworkProviders, lblFanart, null, null);
        // dialog.setLocationRelativeTo(MainWindow.getActiveInstance());
        // dialog.setVisible(true);
      }
    });
    panelContent.add(lblFanart, "8, 12, 1, 5, fill, fill");

    JButton btnMoveGameDown = new JButton("");
    btnMoveGameDown.setAction(actionMoveGameDown);
    panelContent.add(btnMoveGameDown, "2, 14, right, top");

    /**
     * Button pane
     */
    {
      JPanel buttonPane = new JPanel();
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      EqualsLayout layout = new EqualsLayout(5);
      layout.setMinWidth(100);
      buttonPane.setLayout(layout);
      {
        JButton btnOk = new JButton(BUNDLE.getString("Button.ok")); //$NON-NLS-1$
        btnOk.setAction(actionOk);
        buttonPane.add(btnOk);
        getRootPane().setDefaultButton(btnOk);

        JButton btnCancel = new JButton(BUNDLE.getString("Button.cancel")); //$NON-NLS-1$
        btnCancel.setAction(actionCancel);
        buttonPane.add(btnCancel);

        if (inQueue) {
          JButton abortButton = new JButton(BUNDLE.getString("Button.abortqueue")); //$NON-NLS-1$
          buttonPane.add(abortButton);
          abortButton.setAction(actionAbort);
        }
      }

    }

    {
      tfName.setText(gameSetToEdit.getTitle());
      tfTmdbId.setText(String.valueOf(gameSetToEdit.getTmdbId()));
      tpOverview.setText(gameSetToEdit.getPlot());
      lblPoster.setImageUrl(gameSetToEdit.getPosterUrl());
      gamesInSet.addAll(gameSetToEdit.getGames());
      lblPoster.setImageUrl(gameSetToEdit.getPosterUrl());
      lblFanart.setImageUrl(gameSetToEdit.getFanartUrl());
    }

    initDataBindings();

    // adjust table columns
    // name column
    tableGames.getTableHeader().getColumnModel().getColumn(0).setHeaderValue(BUNDLE.getString("metatag.name"));

    // year column
    tableGames.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(35);
    tableGames.getTableHeader().getColumnModel().getColumn(1).setMinWidth(35);
    tableGames.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(50);
    tableGames.getTableHeader().getColumnModel().getColumn(1).setHeaderValue(BUNDLE.getString("metatag.year"));

    // isFavorite column
    tableGames.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(70);
    tableGames.getTableHeader().getColumnModel().getColumn(2).setMinWidth(70);
    tableGames.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(85);
    tableGames.getTableHeader().getColumnModel().getColumn(2).setHeaderValue(BUNDLE.getString("metatag.isFavorite"));
  }

  /**
   * The Class RemoveGameAction.
   * 
   * @author Manuel Laggner
   */
  private class RemoveGameAction extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new removes the game action.
     */
    public RemoveGameAction() {
      putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/tinymediamanager/ui/images/Remove.png")));
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("gameset.game.remove")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      int row = tableGames.getSelectedRow();
      if (row > -1) {
        Game game = gamesInSet.get(row);
        gamesInSet.remove(row);
        removedGames.add(game);
      }
    }
  }

  /**
   * The Class MoveUpAction.
   * 
   * @author Manuel Laggner
   */
  private class MoveUpAction extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new move up action.
     */
    public MoveUpAction() {
      putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/tinymediamanager/ui/images/Button_Up.png")));
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("gameset.game.moveup")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      int row = tableGames.getSelectedRow();
      if (row > 0) {
        Collections.rotate(gamesInSet.subList(row - 1, row + 1), 1);
        tableGames.getSelectionModel().setSelectionInterval(row - 1, row - 1);
      }
    }
  }

  /**
   * The Class MoveDownAction.
   * 
   * @author Manuel Laggner
   */
  private class MoveDownAction extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new move down action.
     */
    public MoveDownAction() {
      putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/tinymediamanager/ui/images/Button_Down.png")));
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("gameset.game.movedown")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      int row = tableGames.getSelectedRow();
      if (row < gamesInSet.size() - 1) {
        Collections.rotate(gamesInSet.subList(row, row + 2), -1);
        tableGames.getSelectionModel().setSelectionInterval(row + 1, row + 1);
      }
    }
  }

  /**
   * The Class OkAction.
   * 
   * @author Manuel Laggner
   */
  private class OkAction extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new ok action.
     */
    public OkAction() {
      putValue(NAME, BUNDLE.getString("Button.save")); //$NON-NLS-1$);
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("Button.save")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      gameSetToEdit.setTitle(tfName.getText());
      gameSetToEdit.setPlot(tpOverview.getText());

      // image changes
      if (StringUtils.isNotEmpty(lblPoster.getImageUrl()) && !lblPoster.getImageUrl().equals(gameSetToEdit.getPosterUrl())) {
        gameSetToEdit.setPosterUrl(lblPoster.getImageUrl());
      }
      if (StringUtils.isNotEmpty(lblFanart.getImageUrl()) && !lblFanart.getImageUrl().equals(gameSetToEdit.getFanartUrl())) {
        gameSetToEdit.setFanartUrl(lblFanart.getImageUrl());
      }

      // delete games
      for (int i = gameSetToEdit.getGames().size() - 1; i >= 0; i--) {
        Game game = gameSetToEdit.getGames().get(i);
        if (!gamesInSet.contains(game)) {
          game.setGameSet(null);
          game.saveToDb();
          gameSetToEdit.removeGame(game);
          game.writeNFO();
        }
      }

      // sort games in the right order
      for (int i = 0; i < gamesInSet.size(); i++) {
        Game game = gamesInSet.get(i);
        game.setSortTitle(gameSetToEdit.getTitle() + String.format("%02d", i + 1));
        game.saveToDb();
      }

      // remove removed games
      for (Game game : removedGames) {
        game.removeFromGameSet();
        game.saveToDb();
        gameSetToEdit.removeGame(game);
      }

      GameList.getInstance().sortGamesInGameSet(gameSetToEdit);

      // and rewrite NFO
      for (Game game : gamesInSet) {
        game.writeNFO();
      }

      int tmdbId = 0;
      try {

        tmdbId = Integer.parseInt(tfTmdbId.getText());
      }
      catch (Exception e1) {
      }
      gameSetToEdit.setTmdbId(tmdbId);
      gameSetToEdit.saveToDb();

      setVisible(false);
      dispose();
    }
  }

  /**
   * The Class CancelAction.
   * 
   * @author Manuel Laggner
   */
  private class CancelAction extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new cancel action.
     */
    public CancelAction() {
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
   * The Class AbortAction.
   * 
   * @author Manuel Laggner
   */
  private class AbortAction extends AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new cancel action.
     */
    public AbortAction() {
      putValue(NAME, BUNDLE.getString("Button.abortqueue")); //$NON-NLS-1$
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("Button.abortqueue")); //$NON-NLS-1$
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

  /**
   * Inits the data bindings.
   */
  protected void initDataBindings() {
    JTableBinding<Game, List<Game>, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, gamesInSet, tableGames);
    //
    BeanProperty<Game, String> gameBeanProperty = BeanProperty.create("title");
    jTableBinding.addColumnBinding(gameBeanProperty).setEditable(false); //$NON-NLS-1$
    //
    BeanProperty<Game, String> gameBeanProperty_1 = BeanProperty.create("year");
    jTableBinding.addColumnBinding(gameBeanProperty_1).setEditable(false); //$NON-NLS-1$
    //
    BeanProperty<Game, Boolean> gameBeanProperty_2 = BeanProperty.create("isFavorite");
    jTableBinding.addColumnBinding(gameBeanProperty_2).setEditable(false).setColumnClass(Boolean.class); //$NON-NLS-1$
    //
    jTableBinding.setEditable(false);
    jTableBinding.bind();
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
      putValue(NAME, BUNDLE.getString("gameset.tmdb.find")); //$NON-NLS-1$
      putValue(SHORT_DESCRIPTION, BUNDLE.getString("gameset.tmdb.desc")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      // search for a tmdbId
      try {
        TmdbMetadataProvider tmdb = new TmdbMetadataProvider();
        for (Game game : gamesInSet) {
          MediaScrapeOptions options = new MediaScrapeOptions();
          // FIXME
          // if (Utils.isValidImdbId(game.getGameId(tmdb.getProviderInfo().getId()))) {
          // options.setImdbId(game.getGameId(tmdb.getProviderInfo().getId()));
          // options.setLanguage(Globals.settings.getGameSettings().getScraperLanguage());
          // options.setCountry(Globals.settings.getGameSettings().getCertificationCountry());
          // MediaMetadata md = tmdb.getMetadata(options);
          // if (md.getTmdbIdSet() > 0) {
          // tfTmdbId.setText(String.valueOf(md.getTmdbIdSet()));
          // break;
          // }
          // }
        }
      }
      catch (Exception e1) {
        JOptionPane.showMessageDialog(null, BUNDLE.getString("gameset.tmdb.error")); //$NON-NLS-1$
      }

    }
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
}

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
package org.tinymediamanager.ui.gamesets;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.core.game.GameSet;
import org.tinymediamanager.ui.PopupListener;
import org.tinymediamanager.ui.TreeUI;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.ZebraJTree;
import org.tinymediamanager.ui.games.GameInformationPanel;
import org.tinymediamanager.ui.games.GameSelectionModel;
import org.tinymediamanager.ui.games.GameSetTreeCellRenderer;
import org.tinymediamanager.ui.gamesets.actions.GameEditAction;
import org.tinymediamanager.ui.gamesets.actions.GameSetAddAction;
import org.tinymediamanager.ui.gamesets.actions.GameSetEditAction;
import org.tinymediamanager.ui.gamesets.actions.GameSetRemoveAction;
import org.tinymediamanager.ui.gamesets.actions.GameSetRenameAction;
import org.tinymediamanager.ui.gamesets.actions.GameSetSearchAction;
import org.tinymediamanager.ui.tvshows.TvShowPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * The Class GameSetTreePanel.
 * 
 * @author Manuel Laggner
 */
public class GameSetPanel extends JPanel {
  private static final long           serialVersionUID    = -7095093579735941697L;
  private static final ResourceBundle BUNDLE              = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  GameSelectionModel                  gameSelectionModel;
  GameSetSelectionModel               gameSetSelectionModel;
  private GameList                    gameList            = GameList.getInstance();
  private GameSetTreeModel            treeModel;
  private int                         width               = 0;

  /**
   * UI elements
   */
  private JSplitPane                  splitPaneHorizontal;
  private JTree                       tree;
  private JLabel                      lblGameSetCount;

  private final Action                actionAddGameSet    = new GameSetAddAction(false);
  private final Action                actionRemoveGameSet = new GameSetRemoveAction(false);
  private final Action                actionSearchGameSet = new GameSetSearchAction(false);
  private final Action                actionEditGameSet   = new GameSetEditAction(false);

  /**
   * Instantiates a new game set panel.
   */
  public GameSetPanel() {
    super();

    gameSelectionModel = new GameSelectionModel();
    treeModel = new GameSetTreeModel(gameList.getGameSetList());

    setLayout(new FormLayout(
        new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
            FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("fill:default:grow"), FormFactory.DEFAULT_ROWSPEC, }));

    splitPaneHorizontal = new JSplitPane();
    splitPaneHorizontal.setContinuousLayout(true);
    add(splitPaneHorizontal, "2, 2, fill, fill");

    JPanel panelGameSetList = new JPanel();
    splitPaneHorizontal.setLeftComponent(panelGameSetList);
    panelGameSetList.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("250px:grow"), },
        new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
            RowSpec.decode("fill:322px:grow"), }));

    JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);
    toolBar.setFloatable(false);
    toolBar.setOpaque(false);
    panelGameSetList.add(toolBar, "2, 2");

    JButton btnAddGameSet = new JButton("");
    btnAddGameSet.setAction(actionAddGameSet);
    toolBar.add(btnAddGameSet);

    JButton btnRemoveGameSet = new JButton("");
    btnRemoveGameSet.setAction(actionRemoveGameSet);
    toolBar.add(btnRemoveGameSet);

    JButton btnSearchGameSet = new JButton("");
    btnSearchGameSet.setAction(actionSearchGameSet);
    toolBar.add(btnSearchGameSet);

    JButton btnEditGameSet = new JButton("");
    btnEditGameSet.setAction(actionEditGameSet);
    toolBar.add(btnEditGameSet);

    JScrollPane scrollPane = new JScrollPane();
    panelGameSetList.add(scrollPane, "2, 4, fill, fill");

    // tree = new JTree(treeModel);
    tree = new ZebraJTree(treeModel) {
      private static final long serialVersionUID = 1L;

      @Override
      public void paintComponent(Graphics g) {
        width = this.getWidth();
        super.paintComponent(g);
      }
    };
    gameSetSelectionModel = new GameSetSelectionModel(tree);

    TreeUI ui = new TreeUI() {
      @Override
      protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded,
          boolean hasBeenExpanded, boolean isLeaf) {
        bounds.width = width - bounds.x;
        super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
      }

    };
    tree.setUI(ui);

    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree.setCellRenderer(new GameSetTreeCellRenderer());
    scrollPane.setViewportView(tree);

    JPanel panelHeader = new JPanel() {
      private static final long serialVersionUID = -6646766582759138262L;

      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getColHeaderColors(), 0, 0, getWidth(), getHeight());
      }
    };
    scrollPane.setColumnHeaderView(panelHeader);
    panelHeader.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"),
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("center:20px"), ColumnSpec.decode("center:20px"), },
        new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, }));

    JLabel lblGameSetColumn = new JLabel("Game set");
    lblGameSetColumn.setHorizontalAlignment(JLabel.CENTER);
    panelHeader.add(lblGameSetColumn, "2, 1");

    JLabel lblNfoColumn = new JLabel("");
    lblNfoColumn.setHorizontalAlignment(JLabel.CENTER);
    lblNfoColumn.setIcon(new ImageIcon(TvShowPanel.class.getResource("/org/tinymediamanager/ui/images/Info.png")));
    panelHeader.add(lblNfoColumn, "4, 1");

    JLabel lblImageColumn = new JLabel("");
    lblImageColumn.setHorizontalAlignment(JLabel.CENTER);
    lblImageColumn.setIcon(new ImageIcon(TvShowPanel.class.getResource("/org/tinymediamanager/ui/images/Image.png")));
    panelHeader.add(lblImageColumn, "5, 1");

    final JPanel panelRight = new JPanel();
    splitPaneHorizontal.setRightComponent(panelRight);
    panelRight.setLayout(new CardLayout(0, 0));

    JPanel panelSet = new GameSetInformationPanel(gameSetSelectionModel);
    panelRight.add(panelSet, "gameSet");

    JPanel panelGame = new GameInformationPanel(gameSelectionModel);
    panelRight.add(panelGame, "game");

    JPanel panelGameSetCount = new JPanel();
    add(panelGameSetCount, "2, 3, left, fill");

    JLabel lblGameSets = new JLabel("Gamesets:");
    panelGameSetCount.add(lblGameSets);

    lblGameSetCount = new JLabel("0");
    panelGameSetCount.add(lblGameSetCount);

    tree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
          if (node.getUserObject() instanceof GameSet) {
            GameSet gameSet = (GameSet) node.getUserObject();
            gameSetSelectionModel.setSelectedGameSet(gameSet);
            CardLayout cl = (CardLayout) (panelRight.getLayout());
            cl.show(panelRight, "gameSet");
          }
          if (node.getUserObject() instanceof Game) {
            Game game = (Game) node.getUserObject();
            gameSelectionModel.setSelectedGame(game);
            CardLayout cl = (CardLayout) (panelRight.getLayout());
            cl.show(panelRight, "game");
          }
        }
        else {
          gameSetSelectionModel.setSelectedGameSet(null);
        }
      }
    });
    // further initializations
    init();
    initDataBindings();
  }

  private void init() {
    // build menu
    buildMenu();

  }

  private void buildMenu() {
    // TODO popup menu for gamesets
    // popup menu
    JPopupMenu popupMenu = new JPopupMenu();

    // gameset actions
    Action actionAddGameSet = new GameSetAddAction(true);
    popupMenu.add(actionAddGameSet);
    Action actionRemoveGameSet = new GameSetRemoveAction(true);
    popupMenu.add(actionRemoveGameSet);
    Action actionEditGameSet = new GameSetEditAction(true);
    popupMenu.add(actionEditGameSet);
    Action actionSearchGameSet = new GameSetSearchAction(true);
    popupMenu.add(actionSearchGameSet);

    // game actions
    popupMenu.addSeparator();
    Action actionEditGame = new GameEditAction(true);
    popupMenu.add(actionEditGame);

    // actions for both of them
    popupMenu.addSeparator();
    Action actionRenameGames = new GameSetRenameAction();
    popupMenu.add(actionRenameGames);

    MouseListener popupListener = new PopupListener(popupMenu, tree);
    tree.addMouseListener(popupListener);
  }

  /**
   * Inits the data bindings.
   */
  protected void initDataBindings() {
    BeanProperty<GameList, Integer> gameListBeanProperty = BeanProperty.create("gameSetCount");
    BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
    AutoBinding<GameList, Integer, JLabel, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, gameList, gameListBeanProperty,
        lblGameSetCount, jLabelBeanProperty);
    autoBinding.bind();
  }
}

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

import static org.tinymediamanager.core.Constants.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameSet;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.ImageLabel;
import org.tinymediamanager.ui.components.ZebraJTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameSetInformationPanel.
 * 
 * @author Manuel Laggner
 */
public class GameSetInformationPanel extends JPanel {

  /** The Constant BUNDLE. */
  private static final ResourceBundle  BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  /** The Constant serialVersionUID. */
  private static final long            serialVersionUID = -8166784589262658147L;

  /** The selection model. */
  private GameSetSelectionModel        selectionModel;

  /** The lbl game set name. */
  private JLabel                       lblGameSetName;

  /** The table assigned games. */
  private JTable                       tableAssignedGames;

  /** The lbl game set poster. */
  private ImageLabel                   lblGameSetPoster;

  /** The panel. */
  private JPanel                       panel;

  /** The layered pane. */
  private JLayeredPane                 layeredPane;

  /** The lbl game set fanart. */
  private ImageLabel                   lblGameSetFanart;

  /** The panel south. */
  private JSplitPane                   panelSouth;

  /** The scroll pane overview. */
  private JScrollPane                  scrollPaneOverview;

  /** The tp overview. */
  private JTextPane                    tpOverview;

  /** The panel overview. */
  private JPanel                       panelOverview;

  /** The lbl overview. */
  private JLabel                       lblOverview;

  /** The media file event list. */
  private EventList<Game>              gameEventList;

  /** The media file table model. */
  private DefaultEventTableModel<Game> gameTableModel   = null;

  /**
   * Instantiates a new game set information panel.
   * 
   * @param model
   *          the model
   */
  public GameSetInformationPanel(GameSetSelectionModel model) {
    this.selectionModel = model;
    gameEventList = new ObservableElementList<Game>(GlazedLists.threadSafeList(new BasicEventList<Game>()), GlazedLists.beanConnector(Game.class));

    setLayout(new BorderLayout(0, 0));

    panel = new JPanel();
    add(panel, BorderLayout.CENTER);
    panel
        .setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("180px:grow"), ColumnSpec.decode("1px"), },
            new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("pref:grow"),
                RowSpec.decode("bottom:default"), }));

    lblGameSetName = new JLabel("");
    lblGameSetName.setFont(new Font("Dialog", Font.BOLD, 18));
    panel.add(lblGameSetName, "2,1, fill, fill");

    layeredPane = new JLayeredPane();
    panel.add(layeredPane, "1, 3, 2, 1, fill, fill");
    layeredPane.setLayout(new FormLayout(
        new ColumnSpec[] { ColumnSpec.decode("10px"), ColumnSpec.decode("120px"), ColumnSpec.decode("default:grow"), }, new RowSpec[] {
            RowSpec.decode("10px"), RowSpec.decode("180px"), RowSpec.decode("default:grow"), }));

    lblGameSetPoster = new ImageLabel();
    lblGameSetPoster.setAlternativeText(BUNDLE.getString("image.notfound.poster")); //$NON-NLS-1$
    layeredPane.setLayer(lblGameSetPoster, 1);
    layeredPane.add(lblGameSetPoster, "2, 2, fill, fill");

    lblGameSetFanart = new ImageLabel(false, true);
    lblGameSetFanart.setAlternativeText(BUNDLE.getString("image.notfound.fanart")); //$NON-NLS-1$
    layeredPane.add(lblGameSetFanart, "1, 1, 3, 3, fill, fill");

    panelSouth = new JSplitPane();
    panelSouth.setContinuousLayout(true);
    panelSouth.setResizeWeight(0.5);
    add(panelSouth, BorderLayout.SOUTH);

    panelOverview = new JPanel();
    panelSouth.setLeftComponent(panelOverview);
    panelOverview.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("250px:grow"), }, new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("24px:grow"), }));

    lblOverview = new JLabel(BUNDLE.getString("metatag.plot")); //$NON-NLS-1$
    panelOverview.add(lblOverview, "1, 2");

    scrollPaneOverview = new JScrollPane();
    panelOverview.add(scrollPaneOverview, "1, 4, fill, fill");

    tpOverview = new JTextPane();
    tpOverview.setEditable(false);
    scrollPaneOverview.setViewportView(tpOverview);

    JPanel panelGames = new JPanel();
    panelSouth.setRightComponent(panelGames);
    panelGames.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("453px:grow"), }, new RowSpec[] { FormFactory.LINE_GAP_ROWSPEC,
        RowSpec.decode("203px:grow"), }));

    gameTableModel = new DefaultEventTableModel<Game>(GlazedListsSwing.swingThreadProxyList(gameEventList), new GameInGameSetTableFormat());
    // tableAssignedGames = new JTable(gameTableModel);
    tableAssignedGames = new ZebraJTable(gameTableModel);
    // JScrollPane scrollPaneGames = new JScrollPane();
    JScrollPane scrollPaneGames = ZebraJTable.createStripedJScrollPane(tableAssignedGames);
    panelGames.add(scrollPaneGames, "1, 2, fill, fill");

    tableAssignedGames.setPreferredScrollableViewportSize(new Dimension(450, 200));
    scrollPaneGames.setViewportView(tableAssignedGames);

    initDataBindings();

    // adjust table columns
    // year column
    tableAssignedGames.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(35);
    tableAssignedGames.getTableHeader().getColumnModel().getColumn(1).setMinWidth(35);
    tableAssignedGames.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(50);

    // isFavorite column
    tableAssignedGames.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(70);
    tableAssignedGames.getTableHeader().getColumnModel().getColumn(2).setMinWidth(70);
    tableAssignedGames.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(85);

    // install the propertychangelistener
    PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String property = propertyChangeEvent.getPropertyName();
        Object source = propertyChangeEvent.getSource();
        // react on selection of a game and change of media files
        if ((source.getClass() == GameSetSelectionModel.class && "selectedGameSet".equals(property))
            || (source.getClass() == GameSet.class && "games".equals(property))) {
          gameEventList.clear();
          gameEventList.addAll(selectionModel.getSelectedGameSet().getGames());
          lblGameSetFanart.setImagePath(selectionModel.getSelectedGameSet().getFanart());
          lblGameSetPoster.setImagePath(selectionModel.getSelectedGameSet().getPoster());
        }

        // react on changes of the images
        if ((source.getClass() == GameSet.class && FANART.equals(property))) {
          GameSet gameSet = (GameSet) source;
          lblGameSetFanart.clearImage();
          lblGameSetFanart.setImagePath(gameSet.getFanart());
        }
        if ((source.getClass() == GameSet.class && POSTER.equals(property))) {
          GameSet gameSet = (GameSet) source;
          lblGameSetPoster.clearImage();
          lblGameSetPoster.setImagePath(gameSet.getPoster());
        }
      }
    };

    selectionModel.addPropertyChangeListener(propertyChangeListener);
  }

  /**
   * The Class MediaTableFormat.
   * 
   * @author Manuel Laggner
   */
  private static class GameInGameSetTableFormat implements AdvancedTableFormat<Game> {

    /*
     * (non-Javadoc)
     * 
     * @see ca.odell.glazedlists.gui.TableFormat#getColumnCount()
     */
    @Override
    public int getColumnCount() {
      return 3;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ca.odell.glazedlists.gui.TableFormat#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
      switch (column) {
        case 0:
          return BUNDLE.getString("gameset.parts"); //$NON-NLS-1$

        case 1:
          return BUNDLE.getString("metatag.year"); //$NON-NLS-1$

        case 2:
          return BUNDLE.getString("metatag.isfavorite"); //$NON-NLS-1$
      }

      throw new IllegalStateException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ca.odell.glazedlists.gui.TableFormat#getColumnValue(java.lang.Object, int)
     */
    @Override
    public Object getColumnValue(Game game, int column) {
      switch (column) {
        case 0:
          return game.getTitle();

        case 1:
          return game.getYear();

        case 2:
          return game.isIsFavorite();
      }
      throw new IllegalStateException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ca.odell.glazedlists.gui.AdvancedTableFormat#getColumnClass(int)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Class getColumnClass(int column) {
      switch (column) {
        case 0:
        case 1:
          return String.class;

        case 2:
          return Boolean.class;
      }
      throw new IllegalStateException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ca.odell.glazedlists.gui.AdvancedTableFormat#getColumnComparator(int)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Comparator getColumnComparator(int arg0) {
      return null;
    }

  }

  /**
   * Inits the data bindings.
   */
  protected void initDataBindings() {
    BeanProperty<GameSetSelectionModel, String> gameSetSelectionModelBeanProperty = BeanProperty.create("selectedGameSet.title");
    BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
    AutoBinding<GameSetSelectionModel, String, JLabel, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, selectionModel,
        gameSetSelectionModelBeanProperty, lblGameSetName, jLabelBeanProperty);
    autoBinding.bind();
    //
    BeanProperty<GameSetSelectionModel, String> gameSetSelectionModelBeanProperty_4 = BeanProperty.create("selectedGameSet.plot");
    BeanProperty<JTextPane, String> jTextPaneBeanProperty = BeanProperty.create("text");
    AutoBinding<GameSetSelectionModel, String, JTextPane, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, selectionModel,
        gameSetSelectionModelBeanProperty_4, tpOverview, jTextPaneBeanProperty);
    autoBinding_3.bind();
    //
    // BeanProperty<GameSetSelectionModel, String> gameSetSelectionModelBeanProperty_1 = BeanProperty.create("selectedGameSet.fanart");
    // BeanProperty<ImageLabel, String> imageLabelBeanProperty_1 = BeanProperty.create("imagePath");
    // AutoBinding<GameSetSelectionModel, String, ImageLabel, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, selectionModel,
    // gameSetSelectionModelBeanProperty_1, lblGameSetFanart, imageLabelBeanProperty_1);
    // autoBinding_2.bind();
    // //
    // BeanProperty<GameSetSelectionModel, String> gameSetSelectionModelBeanProperty_2 = BeanProperty.create("selectedGameSet.poster");
    // AutoBinding<GameSetSelectionModel, String, ImageLabel, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ, selectionModel,
    // gameSetSelectionModelBeanProperty_2, lblGameSetPoster, imageLabelBeanProperty_1);
    // autoBinding_1.bind();
  }
}

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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameSet;
import org.tinymediamanager.ui.ImageIconConverter;
import org.tinymediamanager.ui.gamesets.GameSetTreeNode;
import org.tinymediamanager.ui.gamesets.GameTreeNode;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameSetTreeCellRenderer.
 * 
 * @author Manuel Laggner
 */
public class GameSetTreeCellRenderer implements TreeCellRenderer {

  /** The game set panel. */
  private JPanel                  gameSetPanel      = new JPanel();

  /** The game panel. */
  private JPanel                  gamePanel         = new JPanel();

  /** The game set title. */
  private JLabel                  gameSetTitle      = new JLabel();

  /** The game title. */
  private JLabel                  gameTitle         = new JLabel();

  /** The game set info. */
  private JLabel                  gameSetInfo       = new JLabel();

  /** The game set image label. */
  private JLabel                  gameSetImageLabel = new JLabel();

  /** The game nfo label. */
  private JLabel                  gameNfoLabel      = new JLabel();

  /** The game image label. */
  private JLabel                  gameImageLabel    = new JLabel();

  /** The default renderer. */
  private DefaultTreeCellRenderer defaultRenderer   = new DefaultTreeCellRenderer();

  /** The Constant EVEN_ROW_COLOR. */
  private static final Color      EVEN_ROW_COLOR    = new Color(241, 245, 250);

  /**
   * Instantiates a new game set tree cell renderer.
   */
  public GameSetTreeCellRenderer() {
    gameSetPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("min:grow"), FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
        ColumnSpec.decode("center:20px"), ColumnSpec.decode("center:20px") }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, }));

    gameSetTitle.setFont(new Font("Dialog", Font.BOLD, 12));
    gameSetTitle.setHorizontalAlignment(JLabel.LEFT);
    gameSetTitle.setMinimumSize(new Dimension(0, 0));
    gameSetPanel.add(gameSetTitle, "1, 1");

    gameSetPanel.add(gameSetImageLabel, "4, 1, 1, 2");

    gameSetInfo.setFont(new Font("Dialog", Font.PLAIN, 10));
    gameSetInfo.setHorizontalAlignment(JLabel.LEFT);
    gameSetInfo.setMinimumSize(new Dimension(0, 0));
    gameSetPanel.add(gameSetInfo, "1, 2");

    gamePanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("min:grow"), FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
        ColumnSpec.decode("center:20px"), ColumnSpec.decode("center:20px") }, new RowSpec[] { FormFactory.DEFAULT_ROWSPEC }));
    gameTitle.setMinimumSize(new Dimension(0, 0));
    gamePanel.add(gameTitle, "1, 1");
    gamePanel.add(gameNfoLabel, "3, 1");
    gamePanel.add(gameImageLabel, "4, 1");
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
   */
  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    Component returnValue = null;

    // paint game set node
    if (value != null && value instanceof GameSetTreeNode) {
      Object userObject = ((GameSetTreeNode) value).getUserObject();
      if (userObject instanceof GameSet) {
        GameSet gameSet = (GameSet) userObject;

        gameSetTitle.setText(gameSet.getTitle());
        gameSetInfo.setText(gameSet.getGames().size() + " Games");
        gameSetImageLabel.setIcon(gameSet.getHasImages() ? ImageIconConverter.checkIcon : ImageIconConverter.crossIcon);

        gameSetPanel.setEnabled(tree.isEnabled());
        returnValue = gameSetPanel;
      }
    }

    // paint game node
    if (value != null && value instanceof GameTreeNode) {
      Object userObject = ((GameTreeNode) value).getUserObject();
      if (userObject instanceof Game) {
        Game game = (Game) userObject;

        gameTitle.setText(game.getTitle());
        gameNfoLabel.setIcon(game.getHasNfoFile() ? ImageIconConverter.checkIcon : ImageIconConverter.crossIcon);
        gameImageLabel.setIcon(game.getHasImages() ? ImageIconConverter.checkIcon : ImageIconConverter.crossIcon);

        gamePanel.setEnabled(tree.isEnabled());
        returnValue = gamePanel;
      }
    }

    if (returnValue == null) {
      returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }

    // paint background
    if (selected) {
      returnValue.setBackground(defaultRenderer.getBackgroundSelectionColor());
    }
    else {
      returnValue.setBackground(row % 2 == 0 ? EVEN_ROW_COLOR : Color.WHITE);
      // rendererPanel.setBackground(defaultRenderer.getBackgroundNonSelectionColor());
    }

    return returnValue;
  }

}

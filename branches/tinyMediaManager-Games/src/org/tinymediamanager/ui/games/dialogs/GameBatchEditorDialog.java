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
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.tinymediamanager.Globals;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.core.game.GameSet;
import org.tinymediamanager.scraper.GameMediaGenres;
import org.tinymediamanager.ui.TmmWindowSaver;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.AutocompleteComboBox;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameBatchEditor.
 * 
 * @author Manuel Laggner
 */
public class GameBatchEditorDialog extends JDialog {
  private static final long           serialVersionUID = -8515248604267310279L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  private GameList                    gameList         = GameList.getInstance();
  private List<Game>                  gamesToEdit;
  private boolean                     changed          = false;

  private JComboBox                   cbGenres;
  private JComboBox                   cbTags;
  private JComboBox                   cbGameSet;
  private JCheckBox                   chckbxIsFavorite;

  /**
   * Instantiates a new game batch editor.
   * 
   * @param games
   *          the games
   */
  public GameBatchEditorDialog(final List<Game> games) {
    setModal(true);
    setIconImage(Globals.logo);
    setTitle(BUNDLE.getString("game.edit")); //$NON-NLS-1$
    setName("gameBatchEditor");
    setBounds(5, 5, 350, 230);
    TmmWindowSaver.loadSettings(this);
    getContentPane().setLayout(new BorderLayout(0, 0));

    {
      JPanel panelContent = new JPanel();
      getContentPane().add(panelContent, BorderLayout.CENTER);
      panelContent.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
          FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
          FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
          FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
          FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

      JLabel lblGenres = new JLabel(BUNDLE.getString("metatag.genre")); //$NON-NLS-1$
      panelContent.add(lblGenres, "2, 2, right, default");

      // cbGenres = new JComboBox(GameMediaGenres2.values());
      cbGenres = new AutocompleteComboBox(GameMediaGenres.values());
      cbGenres.setEditable(true);
      panelContent.add(cbGenres, "4, 2, fill, default");

      JButton btnAddGenre = new JButton("");
      btnAddGenre.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Add.png")));
      btnAddGenre.setMargin(new Insets(2, 2, 2, 2));
      btnAddGenre.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          changed = true;
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          GameMediaGenres genre = null;
          Object item = cbGenres.getSelectedItem();

          // genre
          if (item instanceof GameMediaGenres) {
            genre = (GameMediaGenres) item;
          }

          // newly created genre?
          if (item instanceof String) {
            genre = GameMediaGenres.getGenre((String) item);
          }
          // GameMediaGenres2 genre = (GameMediaGenres2) cbGenres.getSelectedItem();
          if (genre != null) {
            for (Game game : gamesToEdit) {
              game.addGenre(genre);
            }
          }
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      });
      panelContent.add(btnAddGenre, "6, 2");

      JButton btnRemoveGenre = new JButton("");
      btnRemoveGenre.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Remove.png")));
      btnRemoveGenre.setMargin(new Insets(2, 2, 2, 2));
      btnRemoveGenre.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          changed = true;
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          GameMediaGenres genre = (GameMediaGenres) cbGenres.getSelectedItem();
          for (Game game : gamesToEdit) {
            game.removeGenre(genre);
          }
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      });
      panelContent.add(btnRemoveGenre, "8, 2");

      JLabel lblTags = new JLabel("Tag");
      panelContent.add(lblTags, "2, 4, right, default");

      cbTags = new AutocompleteComboBox(gameList.getTagsInGames().toArray());
      cbTags.setEditable(true);
      panelContent.add(cbTags, "4, 4, fill, default");

      JButton btnAddTag = new JButton("");
      btnAddTag.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Add.png")));
      btnAddTag.setMargin(new Insets(2, 2, 2, 2));
      btnAddTag.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          changed = true;
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          String tag = (String) cbTags.getSelectedItem();
          for (Game game : gamesToEdit) {
            game.addToTags(tag);
          }
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      });
      panelContent.add(btnAddTag, "6, 4");

      JButton btnRemoveTag = new JButton("");
      btnRemoveTag.setIcon(new ImageIcon(GameEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Remove.png")));
      btnRemoveTag.setMargin(new Insets(2, 2, 2, 2));
      btnRemoveTag.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          changed = true;
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          String tag = (String) cbTags.getSelectedItem();
          for (Game game : gamesToEdit) {
            game.removeFromTags(tag);
          }
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      });
      panelContent.add(btnRemoveTag, "8, 4");

      JLabel lblGameSet = new JLabel(BUNDLE.getString("metatag.gameset")); //$NON-NLS-1$
      panelContent.add(lblGameSet, "2, 6, right, default");

      cbGameSet = new JComboBox();
      panelContent.add(cbGameSet, "4, 6, fill, default");

      JButton btnSetGameSet = new JButton("");
      btnSetGameSet.setMargin(new Insets(2, 2, 2, 2));
      btnSetGameSet.setIcon(new ImageIcon(GameBatchEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Checkmark_big.png")));
      btnSetGameSet.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          changed = true;
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          // game set
          Object obj = cbGameSet.getSelectedItem();
          for (Game game : gamesToEdit) {
            if (obj instanceof String) {
              game.removeFromGameSet();
              game.setSortTitle("");
            }
            if (obj instanceof GameSet) {
              GameSet gameSet = (GameSet) obj;

              if (game.getGameSet() != gameSet) {
                game.removeFromGameSet();
                game.setGameSet(gameSet);
                // gameSet.addGame(game);
                gameSet.insertGame(game);
              }

              // game.setSortTitleFromGameSet();
              // game.saveToDb();
              gameSet.updateGameSorttitle();
            }
          }
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      });
      panelContent.add(btnSetGameSet, "6, 6");

      JLabel lblIsFavorite = new JLabel(BUNDLE.getString("metatag.isFavorite")); //$NON-NLS-1$
      panelContent.add(lblIsFavorite, "2, 8, right, default");

      chckbxIsFavorite = new JCheckBox("");
      panelContent.add(chckbxIsFavorite, "4, 8");

      JButton btnIsFavorite = new JButton("");
      btnIsFavorite.setMargin(new Insets(2, 2, 2, 2));
      btnIsFavorite.setIcon(new ImageIcon(GameBatchEditorDialog.class.getResource("/org/tinymediamanager/ui/images/Checkmark_big.png")));
      btnIsFavorite.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          changed = true;
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          for (Game game : gamesToEdit) {
            game.setIsFavorite(chckbxIsFavorite.isSelected());
          }
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      });
      panelContent.add(btnIsFavorite, "6, 8");
    }

    {
      JPanel panelButtons = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panelButtons.getLayout();
      flowLayout.setAlignment(FlowLayout.RIGHT);
      getContentPane().add(panelButtons, BorderLayout.SOUTH);

      JButton btnClose = new JButton(BUNDLE.getString("Button.close")); //$NON-NLS-1$
      btnClose.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          // rewrite games, if anything changed
          if (changed) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            for (Game game : gamesToEdit) {
              game.saveToDb();
              game.writeNFO();
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
          setVisible(false);
          dispose();
        }
      });
      panelButtons.add(btnClose);

      // add window listener to write changes (if the window close button "X" is
      // pressed)
      addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          // rewrite games, if anything changed
          if (changed) {
            for (Game game : gamesToEdit) {
              game.saveToDb();
              game.writeNFO();
            }
          }
        }
      });
    }

    {
      cbGameSet.addItem("");

      for (GameSet gameSet : gameList.getGameSetList()) {
        cbGameSet.addItem(gameSet);
      }

      gamesToEdit = games;
    }
  }
}

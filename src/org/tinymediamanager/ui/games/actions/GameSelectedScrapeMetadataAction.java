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
package org.tinymediamanager.ui.games.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameSearchAndScrapeOptions;
import org.tinymediamanager.core.game.tasks.GameScrapeTask;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.TmmSwingWorker;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.games.GameUIModule;
import org.tinymediamanager.ui.games.dialogs.GameScrapeMetadataDialog;

/**
 * The GameSelectedScrapeMetadataAction - to rescrape metadata of selected games
 * 
 * @author Manuel Laggner
 */
public class GameSelectedScrapeMetadataAction extends AbstractAction {
  private static final long           serialVersionUID = -4417368111497702010L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  public GameSelectedScrapeMetadataAction() {
    putValue(NAME, BUNDLE.getString("game.scrape.metadata")); //$NON-NLS-1$
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("game.scrape.metadata.desc")); //$NON-NLS-1$
    putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/org/tinymediamanager/ui/images/Search.png")));
    putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/tinymediamanager/ui/images/Search.png")));
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    List<Game> selectedGames = new ArrayList<Game>(GameUIModule.getInstance().getSelectionModel().getSelectedGames());

    if (selectedGames.size() > 0) {
      GameScrapeMetadataDialog dialog = new GameScrapeMetadataDialog(BUNDLE.getString("game.scrape.metadata")); //$NON-NLS-1$
      dialog.setLocationRelativeTo(MainWindow.getActiveInstance());
      dialog.setVisible(true);
      // get options from dialog
      GameSearchAndScrapeOptions options = dialog.getGameSearchAndScrapeConfig();
      // do we want to scrape?
      if (dialog.shouldStartScrape()) {
        // scrape
        TmmSwingWorker scrapeTask = new GameScrapeTask(selectedGames, false, options);
        if (!MainWindow.executeMainTask(scrapeTask)) {
          JOptionPane.showMessageDialog(null, BUNDLE.getString("onlyoneoperation")); //$NON-NLS-1$
        }
      }
      dialog.dispose();
    }
  }
}

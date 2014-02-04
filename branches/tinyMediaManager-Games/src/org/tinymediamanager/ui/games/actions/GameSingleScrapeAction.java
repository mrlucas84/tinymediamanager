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

import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.games.GameUIModule;
import org.tinymediamanager.ui.games.dialogs.GameChooserDialog;

/**
 * GameSingleScrapeAction - does a single scrape for a game including gamechooser popup
 * 
 * @author Manuel Laggner
 */
public class GameSingleScrapeAction extends AbstractAction {
  private static final long           serialVersionUID = 3066746719177708420L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  /**
   * Instantiates a new SingleScrapeAction.
   * 
   * @param withTitle
   *          the with title
   */
  public GameSingleScrapeAction(boolean withTitle) {
    if (withTitle) {
      putValue(NAME, BUNDLE.getString(Messages.getString("GameSingleScrapeAction.3"))); //$NON-NLS-1$
    }
    putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/org/tinymediamanager/ui/images/Search.png"))); 
    putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/tinymediamanager/ui/images/Search.png"))); 
    putValue(SHORT_DESCRIPTION, BUNDLE.getString(Messages.getString("GameSingleScrapeAction.2"))); //$NON-NLS-1$
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    List<Game> selectedGames = new ArrayList<Game>(GameUIModule.getInstance().getSelectionModel().getSelectedGames());

    for (Game game : selectedGames) {
      GameChooserDialog dialogGameChooser = new GameChooserDialog(game, selectedGames.size() > 1 ? true : false);
      if (!dialogGameChooser.showDialog()) {
        break;
      }
    }
  }
}

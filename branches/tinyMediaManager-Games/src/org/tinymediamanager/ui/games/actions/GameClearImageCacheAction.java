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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;

import org.tinymediamanager.core.ImageCache;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.games.GameUIModule;

/**
 * GameClearImageCacheAction - clear image cache for selected games
 * 
 * @author Manuel Laggner
 */
public class GameClearImageCacheAction extends AbstractAction {
  private static final long           serialVersionUID = -5089957097690621345L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  public GameClearImageCacheAction() {
    putValue(NAME, BUNDLE.getString("game.clearimagecache")); //$NON-NLS-1$
    putValue(LARGE_ICON_KEY, "");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    List<Game> selectedGames = new ArrayList<Game>(GameUIModule.getInstance().getSelectionModel().getSelectedGames());

    // get data of all files within all selected games
    MainWindow.getActiveInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    for (Game game : selectedGames) {
      ImageCache.clearImageCacheForMediaEntity(game);
    }
    MainWindow.getActiveInstance().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }
}

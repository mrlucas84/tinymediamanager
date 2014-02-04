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

import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.games.GameUIModule;
import org.tinymediamanager.ui.games.dialogs.GameExporterDialog;

/**
 * The GameExportAction - to export all selected games via a template
 * 
 * @author Manuel Laggner
 */
public class GameExportAction extends AbstractAction {
  private static final long           serialVersionUID = -6731682301579049379L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  public GameExportAction() {
    putValue(LARGE_ICON_KEY, "");
    putValue(NAME, BUNDLE.getString("game.export")); //$NON-NLS-1$
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    List<Game> games = new ArrayList<Game>(GameUIModule.getInstance().getSelectionModel().getSelectedGames());

    // export selected games
    if (games.size() > 0) {
      GameExporterDialog dialog = new GameExporterDialog(games);
      dialog.setLocationRelativeTo(MainWindow.getActiveInstance());
      dialog.setVisible(true);
    }
  }
}

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
package org.tinymediamanager.ui.gamesets.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.core.game.GameSet;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.gamesets.GameSetUIModule;

/**
 * @author Manuel Laggner
 * 
 */
public class GameSetRemoveAction extends AbstractAction {
  private static final long           serialVersionUID = -9030996266835702009L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  /**
   * Instantiates a new removes the game set action.
   */
  public GameSetRemoveAction(boolean withTitle) {
    if (withTitle) {
      putValue(NAME, BUNDLE.getString("gameset.remove.desc")); //$NON-NLS-1$
    }
    putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/org/tinymediamanager/ui/images/Remove.png")));
    putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/org/tinymediamanager/ui/images/Remove.png")));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("gameset.remove.desc")); //$NON-NLS-1$
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    List<GameSet> selectedGameSets = GameSetUIModule.getInstance().getSelectionModel().getSelectedGameSets();

    for (int i = 0; i < selectedGameSets.size(); i++) {
      GameSet gameSet = selectedGameSets.get(i);
      GameList.getInstance().removeGameSet(gameSet);
    }

  }
}

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
package org.tinymediamanager.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.tinymediamanager.Globals;
import org.tinymediamanager.ui.EqualsLayout;
import org.tinymediamanager.ui.TmmWindowSaver;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.settings.SettingsPanel;

/**
 * The SettingsDialog to display the settings
 * 
 * @author Manuel Laggner
 */
public class SettingsDialog extends JDialog {
  private static final long           serialVersionUID = 2435834806519338339L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  public SettingsDialog() {
    setTitle(BUNDLE.getString("tmm.settings")); //$NON-NLS-1$
    setName("settings");
    setBounds(5, 5, 1111, 643);
    TmmWindowSaver.loadSettings(this);
    setIconImage(Globals.logo);
    setModal(true);

    getContentPane().setLayout(new BorderLayout(0, 0));

    JPanel settingsPanel = new SettingsPanel();
    getContentPane().add(settingsPanel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    EqualsLayout layout = new EqualsLayout(5);
    layout.setMinWidth(100);
    buttonPanel.setLayout(layout);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    JButton btnClose = new JButton("Close");
    btnClose.setAction(new CloseAction());
    buttonPanel.add(btnClose);
  }

  private class CloseAction extends AbstractAction {
    public CloseAction() {
      putValue(NAME, "Close");
      putValue(SHORT_DESCRIPTION, "Close the settings window");
    }

    public void actionPerformed(ActionEvent e) {
      setVisible(false);
      dispose();
    }
  }
}

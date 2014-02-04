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
package org.tinymediamanager.ui.games.settings;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.tinymediamanager.ui.UTF8Control;

/**
 * The class GameSettingsContainerPanel. For holding all sub panels for game settings
 * 
 * @author Manuel Laggner
 */
public class GameSettingsContainerPanel extends JPanel {
  private static final long           serialVersionUID = -8828272794092580902L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  public GameSettingsContainerPanel() {
    setLayout(new BorderLayout(0, 0));
    {
      JTabbedPane tabbedPanePages = new JTabbedPane(JTabbedPane.TOP);
      add(tabbedPanePages, BorderLayout.CENTER);
      {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(new GameSettingsPanel());
        tabbedPanePages.addTab(BUNDLE.getString("Settings.general"), null, scrollPane, null); //$NON-NLS-1$
      }
      {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(new GameGeneralSettingsPanel());
        tabbedPanePages.addTab(BUNDLE.getString("Settings.general"), null, scrollPane, null); //$NON-NLS-1$
      }
      {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(new GameScraperSettingsPanel());
        tabbedPanePages.addTab(BUNDLE.getString("Settings.scraper"), null, scrollPane, null); //$NON-NLS-1$
      }
      {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(new GameImageSettingsPanel());
        tabbedPanePages.addTab(BUNDLE.getString("Settings.images"), null, scrollPane, null); //$NON-NLS-1$
      }
    }
  }
}
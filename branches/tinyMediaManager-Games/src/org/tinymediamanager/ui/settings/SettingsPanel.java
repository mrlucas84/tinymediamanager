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
package org.tinymediamanager.ui.settings;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.tinymediamanager.core.Settings;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.games.settings.GameSettingsContainerPanel;
import org.tinymediamanager.ui.movies.settings.MovieSettingsContainerPanel;
import org.tinymediamanager.ui.tvshows.settings.TvShowSettingsContainerPanel;

/**
 * The Class SettingsPanel.
 * 
 * @author Manuel Laggner
 */
public class SettingsPanel extends JPanel {
  private static final long           serialVersionUID = -3509434882626534578L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  private Settings                    settings         = Settings.getInstance();

  /**
   * UI components
   */
  private JTabbedPane                 tabbedPaneModules;
  private JPanel                      panelGeneralSettings;
  private JPanel                      panelMovieSettings;
  private JPanel                      panelTvShowSettings;
  private JPanel                      panelGameSettings;

  /**
   * Create the panel.
   */
  public SettingsPanel() {
    tabbedPaneModules = new JTabbedPane();
    setLayout(new BorderLayout());
    add("Center", tabbedPaneModules);

    /*
     * General settings
     */
    panelGeneralSettings = new GeneralSettingsContainerPanel();
    tabbedPaneModules
        .addTab(
            BUNDLE.getString("Settings.general"), new ImageIcon(SettingsPanel.class.getResource("/org/tinymediamanager/ui/images/Action-configure-icon.png")), panelGeneralSettings); //$NON-NLS-1$

    /*
     * Movie settings
     */
    panelMovieSettings = new MovieSettingsContainerPanel();
    tabbedPaneModules
        .addTab(
            BUNDLE.getString("Settings.movies"), new ImageIcon(SettingsPanel.class.getResource("/org/tinymediamanager/ui/images/show_reel.png")), panelMovieSettings); //$NON-NLS-1$

    /*
     * TV show settings
     */
    panelTvShowSettings = new TvShowSettingsContainerPanel();
    tabbedPaneModules
        .addTab(
            BUNDLE.getString("Settings.tvshow"), new ImageIcon(SettingsPanel.class.getResource("/org/tinymediamanager/ui/images/tv_show.png")), panelTvShowSettings); //$NON-NLS-1$

    /*
     * Game settings
     */
    panelGameSettings = new GameSettingsContainerPanel();
    tabbedPaneModules
        .addTab(
            BUNDLE.getString("Settings.games"), new ImageIcon(SettingsPanel.class.getResource("/org/tinymediamanager/ui/images/show_reel.png")), panelGameSettings); //$NON-NLS-1$

    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentHidden(ComponentEvent e) {
        settings.saveSettings();
      }
    });
  }
  // /**
  // * Show.
  // *
  // * @param component
  // * the component
  // */
  // private void show(Component component) {
  // if (currentComponent != null) {
  // scrollPane.remove(currentComponent);
  // }
  // scrollPane.setViewportView(component);
  //
  // // scroll to top upon changing the panel
  // SwingUtilities.invokeLater(new Runnable() {
  // public void run() {
  // scrollPane.getVerticalScrollBar().setValue(0);
  // }
  // });
  // }
}

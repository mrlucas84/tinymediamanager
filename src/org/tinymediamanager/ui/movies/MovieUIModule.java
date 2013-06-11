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
package org.tinymediamanager.ui.movies;

import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPanel;

import org.tinymediamanager.ui.ITmmUIModule;
import org.tinymediamanager.ui.UTF8Control;

/**
 * @author Manuel Laggner
 * 
 */
public class MovieUIModule implements ITmmUIModule {
  private final static ResourceBundle BUNDLE   = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$
  private final static String         ID       = "movies";
  private static MovieUIModule        instance = null;

  private MovieListPanel              listPanel;
  private JPanel                      detailPanel;

  private final MovieSelectionModel   selectionModel;

  private MovieUIModule() {
    listPanel = new MovieListPanel();
    selectionModel = listPanel.selectionModel;
    detailPanel = new MovieInformationPanel(selectionModel);
  }

  public static MovieUIModule getInstance() {
    if (instance == null) {
      instance = new MovieUIModule();
    }
    return instance;
  }

  public MovieSelectionModel getSelectionModel() {
    return selectionModel;
  }

  @Override
  public String getModuleId() {
    return ID;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getTabPanel()
   */
  @Override
  public JPanel getTabPanel() {
    return listPanel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getTabTitle()
   */
  @Override
  public String getTabTitle() {
    return BUNDLE.getString("tmm.movies"); //$NON-NLS-1$)
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getDetailPanel()
   */
  @Override
  public JPanel getDetailPanel() {
    return detailPanel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getSearchAction()
   */
  @Override
  public Action getSearchAction() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getSearchMenu()
   */
  @Override
  public JMenu getSearchMenu() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getEditAction()
   */
  @Override
  public Action getEditAction() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getEditMenu()
   */
  @Override
  public JMenu getEditMenu() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getUpdateAction()
   */
  @Override
  public Action getUpdateAction() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getUpdateMenu()
   */
  @Override
  public JMenu getUpdateMenu() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getExportAction()
   */
  @Override
  public Action getExportAction() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getExportMenu()
   */
  @Override
  public JMenu getExportMenu() {
    // TODO Auto-generated method stub
    return null;
  }

}

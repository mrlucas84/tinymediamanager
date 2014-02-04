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
package org.tinymediamanager.ui.games;

import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.tinymediamanager.ui.ITmmUIModule;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.games.actions.GameEditAction;
import org.tinymediamanager.ui.games.actions.GameSingleScrapeAction;
import org.tinymediamanager.ui.games.actions.GameUpdateDatasourceAction;

/**
 * @author Manuel Laggner
 * 
 */
public class GameUIModule implements ITmmUIModule {
  private final static ResourceBundle BUNDLE   = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$
  private final static String         ID       = "games";
  private static GameUIModule         instance = null;

  private GamePanel                   listPanel;
  private JPanel                      detailPanel;

  private final GameSelectionModel    selectionModel;

  private Action                      searchAction;
  private Action                      editAction;
  private Action                      updateAction;

  private JPopupMenu                  searchPopupMenu;
  private JPopupMenu                  editPopupMenu;

  private GameUIModule() {
    // this will be used in v3
    // listPanel = new GamePanel();
    // selectionModel = listPanel.gameSelectionModel;
    // detailPanel = new GameInformationPanel(selectionModel);

    // createActions();
    // createPopupMenu();

    selectionModel = MainWindow.getActiveInstance().getGamePanel().gameSelectionModel;
  }

  public static GameUIModule getInstance() {
    if (instance == null) {
      instance = new GameUIModule();
    }
    return instance;
  }

  public GameSelectionModel getSelectionModel() {
    return selectionModel;
  }

  private void createActions() {
    searchAction = new GameSingleScrapeAction(false);
    editAction = new GameEditAction(false);
    updateAction = new GameUpdateDatasourceAction(false);
  }

  private void createPopupMenu() {
    // search popup menu
    searchPopupMenu = new JPopupMenu();
    searchPopupMenu.add(new GameSingleScrapeAction(true));
    // popupMenu.add(actionScrapeSelected);
    // popupMenu.add(actionScrapeMetadataSelected);
    // popupMenu.addSeparator();
    // popupMenu.add(actionEditGame2);
    // popupMenu.add(actionBatchEdit);
    // popupMenu.add(actionRename2);
    // popupMenu.add(actionMediaInformation2);
    // popupMenu.add(actionExport);
    // popupMenu.addSeparator();
    // popupMenu.add(actionRemove2);

    // edit popup menu
    editPopupMenu = new JPopupMenu();
    editPopupMenu.add(new GameEditAction(true));

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
    return BUNDLE.getString("tmm.games"); //$NON-NLS-1$)
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
    return searchAction;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getSearchMenu()
   */
  @Override
  public JPopupMenu getSearchMenu() {
    return searchPopupMenu;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getEditAction()
   */
  @Override
  public Action getEditAction() {
    return editAction;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getEditMenu()
   */
  @Override
  public JPopupMenu getEditMenu() {
    return editPopupMenu;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getUpdateAction()
   */
  @Override
  public Action getUpdateAction() {
    return updateAction;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getUpdateMenu()
   */
  @Override
  public JPopupMenu getUpdateMenu() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getExportAction()
   */
  @Override
  public Action getExportAction() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.ITmmUIModule#getExportMenu()
   */
  @Override
  public JPopupMenu getExportMenu() {
    return null;
  }

}

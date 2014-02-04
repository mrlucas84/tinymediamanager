package org.tinymediamanager.ui.gamesets;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.tinymediamanager.ui.ITmmUIModule;
import org.tinymediamanager.ui.MainWindow;

public class GameSetUIModule implements ITmmUIModule {
  private final static String         ID       = "gameSets";
  private static GameSetUIModule      instance = null;

  private final GameSetSelectionModel selectionModel;

  private GameSetUIModule() {
    // this will be used in v3
    // listPanel = new GamePanel();
    // selectionModel = listPanel.gameSelectionModel;
    // detailPanel = new GameInformationPanel(selectionModel);

    // createActions();
    // createPopupMenu();

    selectionModel = MainWindow.getActiveInstance().getGameSetPanel().gameSetSelectionModel;
  }

  public static GameSetUIModule getInstance() {
    if (instance == null) {
      instance = new GameSetUIModule();
    }
    return instance;
  }

  @Override
  public String getModuleId() {
    return ID;
  }

  @Override
  public JPanel getTabPanel() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getTabTitle() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public JPanel getDetailPanel() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Action getSearchAction() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public JPopupMenu getSearchMenu() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Action getEditAction() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public JPopupMenu getEditMenu() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Action getUpdateAction() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public JPopupMenu getUpdateMenu() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Action getExportAction() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public JPopupMenu getExportMenu() {
    // TODO Auto-generated method stub
    return null;
  }

  public GameSetSelectionModel getSelectionModel() {
    return selectionModel;
  }
}

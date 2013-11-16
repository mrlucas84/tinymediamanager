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
package org.tinymediamanager.ui.plaf.light;

import javax.swing.Icon;

import com.jtattoo.plaf.AbstractIconFactory;

/**
 * @author Manuel Laggner
 */
public class TmmLightIconFactory implements AbstractIconFactory {

  private static TmmLightIconFactory instance = null;

  private TmmLightIconFactory() {
  }

  public static synchronized TmmLightIconFactory getInstance() {
    if (instance == null) {
      instance = new TmmLightIconFactory();
    }
    return instance;
  }

  public Icon getOptionPaneErrorIcon() {
    return TmmLightIcons.getOptionPaneErrorIcon();
  }

  public Icon getOptionPaneWarningIcon() {
    return TmmLightIcons.getOptionPaneWarningIcon();
  }

  public Icon getOptionPaneInformationIcon() {
    return TmmLightIcons.getOptionPaneInformationIcon();
  }

  public Icon getOptionPaneQuestionIcon() {
    return TmmLightIcons.getOptionPaneQuestionIcon();
  }

  public Icon getFileChooserDetailViewIcon() {
    return TmmLightIcons.getFileChooserDetailViewIcon();
  }

  public Icon getFileChooserHomeFolderIcon() {
    return TmmLightIcons.getFileChooserHomeFolderIcon();
  }

  public Icon getFileChooserListViewIcon() {
    return TmmLightIcons.getFileChooserListViewIcon();
  }

  public Icon getFileChooserNewFolderIcon() {
    return TmmLightIcons.getFileChooserNewFolderIcon();
  }

  public Icon getFileChooserUpFolderIcon() {
    return TmmLightIcons.getFileChooserUpFolderIcon();
  }

  public Icon getMenuIcon() {
    return TmmLightIcons.getMenuIcon();
  }

  public Icon getIconIcon() {
    return TmmLightIcons.getIconIcon();
  }

  public Icon getMaxIcon() {
    return TmmLightIcons.getMaxIcon();
  }

  public Icon getMinIcon() {
    return TmmLightIcons.getMinIcon();
  }

  public Icon getCloseIcon() {
    return TmmLightIcons.getCloseIcon();
  }

  public Icon getPaletteCloseIcon() {
    return TmmLightIcons.getPaletteCloseIcon();
  }

  public Icon getRadioButtonIcon() {
    return TmmLightIcons.getRadioButtonIcon();
  }

  public Icon getCheckBoxIcon() {
    return TmmLightIcons.getCheckBoxIcon();
  }

  public Icon getComboBoxIcon() {
    return TmmLightIcons.getComboBoxIcon();
  }

  public Icon getTreeComputerIcon() {
    return TmmLightIcons.getTreeComputerIcon();
  }

  public Icon getTreeFloppyDriveIcon() {
    return TmmLightIcons.getTreeFloppyDriveIcon();
  }

  public Icon getTreeHardDriveIcon() {
    return TmmLightIcons.getTreeHardDriveIcon();
  }

  public Icon getTreeFolderIcon() {
    return TmmLightIcons.getTreeFolderIcon();
  }

  public Icon getTreeLeafIcon() {
    return TmmLightIcons.getTreeLeafIcon();
  }

  public Icon getTreeCollapsedIcon() {
    return TmmLightIcons.getTreeControlIcon(true);
  }

  public Icon getTreeExpandedIcon() {
    return TmmLightIcons.getTreeControlIcon(false);
  }

  public Icon getMenuArrowIcon() {
    return TmmLightIcons.getMenuArrowIcon();
  }

  public Icon getMenuCheckBoxIcon() {
    return TmmLightIcons.getMenuCheckBoxIcon();
  }

  public Icon getMenuRadioButtonIcon() {
    return TmmLightIcons.getMenuRadioButtonIcon();
  }

  public Icon getUpArrowIcon() {
    return TmmLightIcons.getUpArrowIcon();
  }

  public Icon getDownArrowIcon() {
    return TmmLightIcons.getDownArrowIcon();
  }

  public Icon getLeftArrowIcon() {
    return TmmLightIcons.getLeftArrowIcon();
  }

  public Icon getRightArrowIcon() {
    return TmmLightIcons.getRightArrowIcon();
  }

  public Icon getSplitterDownArrowIcon() {
    return TmmLightIcons.getSplitterDownArrowIcon();
  }

  public Icon getSplitterHorBumpIcon() {
    return TmmLightIcons.getSplitterHorBumpIcon();
  }

  public Icon getSplitterLeftArrowIcon() {
    return TmmLightIcons.getSplitterLeftArrowIcon();
  }

  public Icon getSplitterRightArrowIcon() {
    return TmmLightIcons.getSplitterRightArrowIcon();
  }

  public Icon getSplitterUpArrowIcon() {
    return TmmLightIcons.getSplitterUpArrowIcon();
  }

  public Icon getSplitterVerBumpIcon() {
    return TmmLightIcons.getSplitterVerBumpIcon();
  }

  public Icon getThumbHorIcon() {
    return TmmLightIcons.getThumbHorIcon();
  }

  public Icon getThumbVerIcon() {
    return TmmLightIcons.getThumbVerIcon();
  }

  public Icon getThumbHorIconRollover() {
    return TmmLightIcons.getThumbHorIconRollover();
  }

  public Icon getThumbVerIconRollover() {
    return TmmLightIcons.getThumbVerIconRollover();
  }
}

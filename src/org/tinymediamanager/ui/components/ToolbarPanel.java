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
package org.tinymediamanager.ui.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.tinymediamanager.ui.ITmmUIModule;
import org.tinymediamanager.ui.actions.AboutAction;
import org.tinymediamanager.ui.actions.BugReportAction;
import org.tinymediamanager.ui.actions.ClearImageCacheAction;
import org.tinymediamanager.ui.actions.ClearUrlCacheAction;
import org.tinymediamanager.ui.actions.DonateAction;
import org.tinymediamanager.ui.actions.FeedbackAction;
import org.tinymediamanager.ui.actions.RebuildImageCacheAction;
import org.tinymediamanager.ui.actions.SettingsAction;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class ToolbarPanel.
 * 
 * @author Manuel Laggner
 */
public class ToolbarPanel extends JPanel {
  private static final long      serialVersionUID    = 7969400170662870244L;

  private static final ImageIcon SCRAPE_ICON         = new ImageIcon(
                                                         ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Search_Scrape_Icon.png"));
  private static final ImageIcon SCRAPE_ICON_HOVER   = new ImageIcon(
                                                         ToolbarPanel.class
                                                             .getResource("/org/tinymediamanager/ui/images/Search_Scrape_Icon_Hover.png"));
  private static final ImageIcon EDIT_ICON           = new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Edit_Icon.png"));
  private static final ImageIcon EDIT_ICON_HOVER     = new ImageIcon(
                                                         ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Edit_Icon_Hover.png"));
  private static final ImageIcon REFRESH_ICON        = new ImageIcon(
                                                         ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Refresh_Database_Icon.png"));
  private static final ImageIcon REFRESH_ICON_HOVER  = new ImageIcon(
                                                         ToolbarPanel.class
                                                             .getResource("/org/tinymediamanager/ui/images/Refresh_Database_Icon_Hover.png"));

  private static final ImageIcon EXPORT_ICON         = new ImageIcon(
                                                         ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Export_Icon.png"));
  private static final ImageIcon EXPORT_ICON_HOVER   = new ImageIcon(
                                                         ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Export_Icon_Hover.png"));

  private static final ImageIcon TOOLS_ICON          = new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Tools_Icon.png"));
  private static final ImageIcon TOOLS_ICON_HOVER    = new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Tools_Icon.png"));
  private static final ImageIcon SETTINGS_ICON       = new ImageIcon(
                                                         ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Settings_Icon.png"));
  private static final ImageIcon SETTINGS_ICON_HOVER = new ImageIcon(
                                                         ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Settings_Icon.png"));
  private static final ImageIcon ABOUT_ICON          = new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/About_Icon.png"));
  private static final ImageIcon ABOUT_ICON_HOVER    = new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/About_Icon.png"));
  private static final ImageIcon DONATE_ICON         = new ImageIcon(
                                                         ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Donate_Icon.png"));
  private static final ImageIcon DONATE_ICON_HOVER   = new ImageIcon(
                                                         ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Donate_Icon.png"));

  private JTextField             textField;
  private JButton                btnSearch;
  private JButton                btnEdit;
  private JButton                btnUpdate;
  private JButton                btnExport;
  private JButton                btnTools;
  private JButton                btnSettings;
  private JButton                btnAbout;
  private JButton                btnDonate;

  private JLabel                 lblSearch;
  private JLabel                 lblEdit;
  private JLabel                 lblUpdate;
  private JLabel                 lblExport;
  private JLabel                 lblTools;
  private JLabel                 lblSettings;
  private JLabel                 lblAbout;
  private JLabel                 lblDonate;

  private Action                 searchAction;
  private Action                 editAction;
  private Action                 updateAction;
  private Action                 exportAction;
  private Action                 settingsAction      = new SettingsAction();
  private Action                 aboutAction         = new AboutAction();
  private Action                 donateAction        = new DonateAction();

  private JPopupMenu             searchPopupMenu;
  private JPopupMenu             editPopupMenu;
  private JPopupMenu             exportPopupMenu;
  private JPopupMenu             toolsPopupMenu      = buildToolsMenu();

  private int                    arrowSize           = 10;
  private Color                  arrowColor          = Color.GRAY;
  private Color                  arrowColorHover     = Color.WHITE;
  private ImageIcon              menuImage;
  private ImageIcon              menuImageHover;

  public ToolbarPanel() {
    putClientProperty("class", "toolbarPanel");
    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), ColumnSpec.decode("default:grow"), ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { RowSpec.decode("top:50px"), FormFactory.DEFAULT_ROWSPEC,
        FormFactory.UNRELATED_GAP_ROWSPEC, }));

    btnSearch = createButton("", SCRAPE_ICON, SCRAPE_ICON_HOVER);
    add(btnSearch, "2,1, fill, fill");

    btnEdit = createButton("", EDIT_ICON, EDIT_ICON_HOVER);
    add(btnEdit, "4, 1, center, default");

    btnUpdate = createButton("", REFRESH_ICON, REFRESH_ICON_HOVER);
    add(btnUpdate, "6, 1, center, default");

    btnExport = createButton("", EXPORT_ICON, EXPORT_ICON_HOVER);
    add(btnExport, "8, 1, center, default");

    textField = new JTextField();
    add(textField, "10, 1, fill, center");
    textField.setColumns(10);

    btnTools = createButton("", TOOLS_ICON, TOOLS_ICON_HOVER);
    add(btnTools, "12, 1, center, center");

    btnSettings = createButton("", SETTINGS_ICON, SETTINGS_ICON_HOVER);
    add(btnSettings, "14, 1, center, center");

    btnAbout = createButton("", ABOUT_ICON, ABOUT_ICON_HOVER);
    add(btnAbout, "16, 1, center, center");

    btnDonate = createButton("", DONATE_ICON, DONATE_ICON_HOVER);
    add(btnDonate, "18, 1, center, center");

    lblSearch = createMenu("Search & Scrape");
    add(lblSearch, "2, 2, center, default");

    lblEdit = createMenu("Edit");
    add(lblEdit, "4, 2, center, default");

    lblUpdate = new JLabel("Refresh database");
    lblUpdate.setForeground(arrowColor);
    add(lblUpdate, "6, 2, center, default");

    lblExport = createMenu("Export");
    add(lblExport, "8, 2, center, default");

    lblTools = createMenu("Tools");
    add(lblTools, "12, 2, center, default");

    lblSettings = new JLabel("Settings");
    lblSettings.setForeground(arrowColor);
    add(lblSettings, "14, 2, center, default");

    lblAbout = new JLabel("About");
    lblAbout.setForeground(arrowColor);
    add(lblAbout, "16, 2, center, default");

    lblDonate = new JLabel("Donate");
    lblDonate.setForeground(arrowColor);
    add(lblDonate, "18, 2, center, default");
  }

  public void setUIModule(ITmmUIModule module) {
    searchAction = module.getSearchAction();
    setTooltipFromAction(btnSearch, searchAction);
    searchPopupMenu = module.getSearchMenu();

    editAction = module.getEditAction();
    setTooltipFromAction(btnEdit, editAction);
    editPopupMenu = module.getEditMenu();

    updateAction = module.getUpdateAction();
    setTooltipFromAction(btnEdit, updateAction);
  }

  private void setTooltipFromAction(JButton button, Action action) {
    Object shortDescription = action.getValue(Action.SHORT_DESCRIPTION);
    if (shortDescription != null) {
      button.setToolTipText(shortDescription.toString());
    }
    else {
      button.setToolTipText("");
    }
  }

  /**
   * create the buttons (for main actions)
   */
  private JButton createButton(String text, final Icon icon, final Icon hoverIcon) {
    final JButton button = new JButton(text, icon);

    button.setVerticalTextPosition(SwingConstants.BOTTOM);
    button.setHorizontalTextPosition(SwingConstants.CENTER);
    button.setOpaque(false);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.putClientProperty("flatButton", Boolean.TRUE);
    button.updateUI();
    button.addMouseListener(new MouseListener() {
      @Override
      public void mouseReleased(MouseEvent arg0) {
      }

      @Override
      public void mousePressed(MouseEvent arg0) {
      }

      @Override
      public void mouseExited(MouseEvent arg0) {
        button.setIcon(icon);
      }

      @Override
      public void mouseEntered(MouseEvent arg0) {
        button.setIcon(hoverIcon);
      }

      @Override
      public void mouseClicked(MouseEvent arg0) {
        buttonCallback(arg0.getSource());
      }
    });

    return button;
  }

  /**
   * create the texts with a menu in it
   */
  private JLabel createMenu(String text) {
    final JLabel label = new JLabel(text, getMenuIndicatorImage(), SwingConstants.CENTER);
    label.setHorizontalTextPosition(SwingConstants.LEFT);
    label.setVerticalTextPosition(SwingConstants.BOTTOM);
    label.setOpaque(false);
    label.setForeground(arrowColor);
    label.addMouseListener(new MouseListener() {
      @Override
      public void mouseReleased(MouseEvent arg0) {
      }

      @Override
      public void mousePressed(MouseEvent arg0) {
      }

      @Override
      public void mouseExited(MouseEvent arg0) {
        label.setForeground(arrowColor);
        label.setIcon(getMenuIndicatorImage());
      }

      @Override
      public void mouseEntered(MouseEvent arg0) {
        label.setForeground(arrowColorHover);
        label.setIcon(getMenuIndicatorHoverImage());
      }

      @Override
      public void mouseClicked(MouseEvent arg0) {
        menuCallback(arg0.getSource());
      }
    });

    return label;
  }

  /**
   * callback method for button clicks (to call the right action)
   */
  private void buttonCallback(Object sender) {
    if (sender == btnSearch) {
      if (searchAction != null) {
        searchAction.actionPerformed(null);
      }
    }
    else if (sender == btnEdit) {
      if (editAction != null) {
        editAction.actionPerformed(null);
      }
    }
    else if (sender == btnUpdate) {
      if (updateAction != null) {
        updateAction.actionPerformed(null);
      }
    }
    else if (sender == btnSettings) {
      settingsAction.actionPerformed(null);
    }
    else if (sender == btnAbout) {
      aboutAction.actionPerformed(null);
    }
    else if (sender == btnDonate) {
      donateAction.actionPerformed(null);
    }
  }

  /**
   * callback method for menu label clicks (to call the right menu)
   */
  private void menuCallback(Object sender) {
    if (sender == lblSearch) {
      if (searchPopupMenu != null) {
        showPopupMenu(lblSearch, searchPopupMenu);

      }
    }
    else if (sender == lblEdit) {
      if (editPopupMenu != null) {
        showPopupMenu(lblEdit, editPopupMenu);
      }
    }
    else if (sender == lblTools) {
      showPopupMenu(lblTools, toolsPopupMenu);
    }
  }

  private JPopupMenu buildToolsMenu() {
    JPopupMenu menu = new JPopupMenu();

    menu.add(new ClearUrlCacheAction());
    menu.add(new ClearImageCacheAction());
    menu.add(new RebuildImageCacheAction());

    menu.addSeparator();
    menu.add(new BugReportAction());
    menu.add(new FeedbackAction());
    return menu;
  }

  private void showPopupMenu(JLabel label, JPopupMenu popupMenu) {
    popupMenu.show(label, label.getWidth() - (int) popupMenu.getPreferredSize().getWidth(), label.getHeight());
  }

  private ImageIcon getMenuIndicatorImage() {
    if (menuImage != null) {
      return menuImage;
    }

    menuImage = new ImageIcon(paintMenuImage(false));
    return menuImage;
  }

  private ImageIcon getMenuIndicatorHoverImage() {
    if (menuImageHover != null) {
      return menuImageHover;
    }

    menuImageHover = new ImageIcon(paintMenuImage(true));
    return menuImageHover;
  }

  private Image paintMenuImage(boolean hover) {
    Graphics2D g = null;
    BufferedImage img = new BufferedImage(arrowSize, arrowSize, BufferedImage.TYPE_INT_RGB);
    g = (Graphics2D) img.createGraphics();
    g.setColor(hover ? arrowColor : arrowColorHover);
    g.fillRect(0, 0, img.getWidth(), img.getHeight());
    g.setColor(hover ? arrowColorHover : arrowColor);
    // this creates a triangle facing right >
    g.fillPolygon(new int[] { 0, 0, arrowSize / 2 }, new int[] { 0, arrowSize, arrowSize / 2 }, 3);
    g.dispose();
    // rotate it to face downwards
    img = rotate(img, 90);
    BufferedImage dimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    g = (Graphics2D) dimg.createGraphics();
    g.setComposite(AlphaComposite.Src);
    g.drawImage(img, null, 0, 0);
    g.dispose();

    // paint transparent background
    for (int i = 0; i < dimg.getHeight(); i++) {
      for (int j = 0; j < dimg.getWidth(); j++) {
        if (dimg.getRGB(j, i) == (hover ? arrowColor.getRGB() : arrowColorHover.getRGB())) {
          dimg.setRGB(j, i, 0x8F1C1C);
        }
      }
    }

    return Toolkit.getDefaultToolkit().createImage(dimg.getSource());
  }

  private BufferedImage rotate(BufferedImage img, int angle) {
    int w = img.getWidth();
    int h = img.getHeight();
    BufferedImage dimg = new BufferedImage(w, h, img.getType());
    Graphics2D g = dimg.createGraphics();
    g.rotate(Math.toRadians(angle), w / 2, h / 2);
    g.drawImage(img, null, 0, 0);
    return dimg;
  }
}

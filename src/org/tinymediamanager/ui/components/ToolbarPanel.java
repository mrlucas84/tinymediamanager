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
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.Message.MessageLevel;
import org.tinymediamanager.core.MessageManager;
import org.tinymediamanager.ui.ITmmUIModule;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.TmmUIHelper;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.actions.AboutAction;
import org.tinymediamanager.ui.actions.BugReportAction;
import org.tinymediamanager.ui.actions.ClearImageCacheAction;
import org.tinymediamanager.ui.actions.ClearUrlCacheAction;
import org.tinymediamanager.ui.actions.DonateAction;
import org.tinymediamanager.ui.actions.FeedbackAction;
import org.tinymediamanager.ui.actions.RebuildImageCacheAction;
import org.tinymediamanager.ui.actions.SettingsAction;
import org.tinymediamanager.ui.dialogs.LogDialog;

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
  private static final long           serialVersionUID    = 7969400170662870244L;
  private static final ResourceBundle BUNDLE              = ResourceBundle.getBundle("messages", new UTF8Control());
  private final static Logger         LOGGER              = LoggerFactory.getLogger(ToolbarPanel.class);                                               //$NON-NLS-1$

  private static final ImageIcon      SCRAPE_ICON         = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_search.png"));
  private static final ImageIcon      SCRAPE_ICON_HOVER   = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_search_hover.png"));
  private static final ImageIcon      EDIT_ICON           = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_edit.png"));
  private static final ImageIcon      EDIT_ICON_HOVER     = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_edit_hover.png"));
  private static final ImageIcon      REFRESH_ICON        = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_refresh.png"));
  private static final ImageIcon      REFRESH_ICON_HOVER  = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_refresh_hover.png"));

  private static final ImageIcon      DOWNLOAD_ICON       = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_download.png"));
  private static final ImageIcon      DOWNLOAD_ICON_HOVER = new ImageIcon(
                                                              ToolbarPanel.class
                                                                  .getResource("/org/tinymediamanager/ui/images/icn_download_hover.png"));

  private static final ImageIcon      EXPORT_ICON         = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_export.png"));
  private static final ImageIcon      EXPORT_ICON_HOVER   = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_export_hover.png"));
  private static final ImageIcon      TOOLS_ICON          = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_tools.png"));
  private static final ImageIcon      TOOLS_ICON_HOVER    = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_tools_hover.png"));
  private static final ImageIcon      SETTINGS_ICON       = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_settings.png"));
  private static final ImageIcon      SETTINGS_ICON_HOVER = new ImageIcon(
                                                              ToolbarPanel.class
                                                                  .getResource("/org/tinymediamanager/ui/images/icn_settings_hover.png"));
  private static final ImageIcon      ABOUT_ICON          = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_about.png"));
  private static final ImageIcon      ABOUT_ICON_HOVER    = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_about_hover.png"));
  private static final ImageIcon      DONATE_ICON         = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_donate.png"));
  private static final ImageIcon      DONATE_ICON_HOVER   = new ImageIcon(
                                                              ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/icn_donate_hover.png"));
  private JButton                     btnSearch;
  private JButton                     btnEdit;
  private JButton                     btnUpdate;
  private JButton                     btnDownload;
  private JButton                     btnExport;
  private JButton                     btnTools;
  private JButton                     btnSettings;
  private JButton                     btnAbout;
  private JButton                     btnDonate;

  private JLabel                      lblSearch;
  private JLabel                      lblEdit;
  private JLabel                      lblUpdate;
  private JLabel                      lblDownload;
  private JLabel                      lblExport;
  private JLabel                      lblTools;
  private JLabel                      lblSettings;
  private JLabel                      lblAbout;
  private JLabel                      lblDonate;

  private Action                      searchAction;
  private Action                      editAction;
  private Action                      updateAction;
  private Action                      exportAction;
  private Action                      settingsAction      = new SettingsAction();
  private Action                      aboutAction         = new AboutAction();
  private Action                      donateAction        = new DonateAction();

  private JPopupMenu                  searchPopupMenu;
  private JPopupMenu                  editPopupMenu;
  private JPopupMenu                  toolsPopupMenu      = buildToolsMenu();

  private int                         arrowSize           = 10;
  private Color                       arrowColor          = Color.GRAY;
  private Color                       arrowColorHover     = Color.WHITE;
  private ImageIcon                   menuImage;
  private ImageIcon                   menuImageHover;

  public ToolbarPanel() {
    putClientProperty("class", "toolbarPanel");
    setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("10dlu"), FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), ColumnSpec.decode("default:grow"), ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { RowSpec.decode("top:40px"), FormFactory.DEFAULT_ROWSPEC,
        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

    add(new JLabel(new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/logo_toolbar.png"))), "2,1,1,2,center,center");

    btnUpdate = createButton("", REFRESH_ICON, REFRESH_ICON_HOVER);
    add(btnUpdate, "4, 1, center, default");

    btnSearch = createButton("", SCRAPE_ICON, SCRAPE_ICON_HOVER);
    add(btnSearch, "6,1, center, default");

    btnEdit = createButton("", EDIT_ICON, EDIT_ICON_HOVER);
    add(btnEdit, "8, 1, center, default");

    btnExport = createButton("", EXPORT_ICON, EXPORT_ICON_HOVER);
    add(btnExport, "12, 1, center, bottom");

    btnTools = createButton("", TOOLS_ICON, TOOLS_ICON_HOVER);
    add(btnTools, "14, 1, center, bottom");

    btnSettings = createButton("", SETTINGS_ICON, SETTINGS_ICON_HOVER);
    add(btnSettings, "16, 1, center, bottom");

    btnDownload = createButton("", DOWNLOAD_ICON, DOWNLOAD_ICON_HOVER);
    add(btnDownload, "18, 1, center, bottom");

    btnAbout = createButton("", ABOUT_ICON, ABOUT_ICON_HOVER);
    add(btnAbout, "20, 1, center, bottom");

    btnDonate = createButton("", DONATE_ICON, DONATE_ICON_HOVER);
    add(btnDonate, "22, 1, center, bottom");

    lblUpdate = createMenu("Refresh source");
    add(lblUpdate, "4, 2, center, default");

    lblSearch = createMenu("Search & Scrape");
    add(lblSearch, "6, 2, center, default");

    lblEdit = createMenu("Edit");
    add(lblEdit, "8, 2, center, default");

    lblExport = new JLabel("Export");
    lblExport.setFont(lblExport.getFont().deriveFont(11f));
    lblExport.setForeground(arrowColor);
    add(lblExport, "12, 2, center, default");

    lblTools = createMenu("Tools");
    add(lblTools, "14, 2, center, default");

    lblSettings = new JLabel("Settings");
    lblSettings.setForeground(arrowColor);
    add(lblSettings, "16, 2, center, default");

    lblDownload = new JLabel("Progress");
    lblDownload.setForeground(arrowColor);
    add(lblDownload, "18, 2, center, default");

    lblAbout = new JLabel("About");
    lblAbout.setForeground(arrowColor);
    add(lblAbout, "20, 2, center, default");

    lblDonate = new JLabel("Donate");
    lblDonate.setForeground(arrowColor);
    add(lblDonate, "22, 2, center, default");
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

    // debug menu
    JMenu debug = new JMenu(BUNDLE.getString("tmm.debug")); //$NON-NLS-1$
    JMenuItem clearDatabase = new JMenuItem(BUNDLE.getString("tmm.cleardatabase")); //$NON-NLS-1$
    debug.add(clearDatabase);
    clearDatabase.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // delete the database
        try {
          Globals.shutdownDatabase();
          File db = new File("tmm.odb");
          if (db.exists()) {
            db.delete();
          }
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          JOptionPane.showMessageDialog(null, BUNDLE.getString("tmm.cleardatabase.info")); //$NON-NLS-1$
        }
        catch (Exception e) {
          JOptionPane.showMessageDialog(null, BUNDLE.getString("tmm.cleardatabase.error")); //$NON-NLS-1$
          // open the tmm folder
          File path = new File(".");
          try {
            // check whether this location exists
            if (path.exists()) {
              TmmUIHelper.openFile(path);
            }
          }
          catch (Exception ex) {
            LOGGER.warn(ex.getMessage());
            MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, path, "message.erroropenfolder", new String[] { ":",
                ex.getLocalizedMessage() }));
          }
        }
        System.exit(0);
      }
    });

    JMenuItem tmmFolder = new JMenuItem(BUNDLE.getString("tmm.gotoinstalldir")); //$NON-NLS-1$
    debug.add(tmmFolder);
    tmmFolder.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        File path = new File(System.getProperty("user.dir"));
        try {
          // check whether this location exists
          if (path.exists()) {
            TmmUIHelper.openFile(path);
          }
        }
        catch (Exception ex) {
          LOGGER.error("open filemanager", ex);
          MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, path, "message.erroropenfolder", new String[] { ":",
              ex.getLocalizedMessage() }));
        }
      }
    });

    JMenuItem tmmLogs = new JMenuItem(BUNDLE.getString("tmm.errorlogs")); //$NON-NLS-1$
    debug.add(tmmLogs);
    tmmLogs.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        JDialog logDialog = new LogDialog();
        logDialog.setLocationRelativeTo(MainWindow.getActiveInstance());
        logDialog.setVisible(true);
      }
    });

    menu.add(debug);

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

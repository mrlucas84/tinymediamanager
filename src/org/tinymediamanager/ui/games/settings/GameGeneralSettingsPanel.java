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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.Settings;
import org.tinymediamanager.core.game.GameSettings;
import org.tinymediamanager.core.game.RomCollectionConfig;
import org.tinymediamanager.core.platform.Platform;
import org.tinymediamanager.core.platform.Platforms;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.games.settings.GameGeneralSettingsPanel.PlatformUI.MyTableModel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GeneralSettingsPanel.Action
 * 
 * @author Manuel Laggner
 */
public class GameGeneralSettingsPanel extends JPanel {

  private static final long           serialVersionUID = 500841588272296493L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  private Settings                    settings         = Settings.getInstance();

  private JPanel                      panelGameFiletypes;
  private JPanel                      panelPlatform;
  private JPanel                      panelRom;
  private JTextField                  tfGameFiletype;
  private JList                       listGameFiletypes;
  private JList                       listPlatform;
  private JComboBox                   tfPlatform;

  /**
   * Instantiates a new general settings panel.
   */
  public GameGeneralSettingsPanel() {
    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("left:max(200px;min)"),
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(200px;default)"), FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("max(200px;default)"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("400px:grow"), }));
    {
      panelGameFiletypes = new JPanel();
      panelGameFiletypes
          .setBorder(new TitledBorder(
              UIManager.getBorder("TitledBorder.border"), BUNDLE.getString("Settings.gamefiletypes"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
      panelGameFiletypes.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"),
          FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
          RowSpec.decode("default:grow"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

      JScrollPane scrollPaneGameFiletypes = new JScrollPane();
      panelGameFiletypes.add(scrollPaneGameFiletypes, "2, 2, fill, fill");

      listGameFiletypes = new JList();
      scrollPaneGameFiletypes.setViewportView(listGameFiletypes);

      JButton btnRemoveGameFiletype = new JButton(BUNDLE.getString("Button.remove")); //$NON-NLS-1$
      btnRemoveGameFiletype.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          int row = listGameFiletypes.getSelectedIndex();
          if (row != -1) {
            String prefix = Globals.settings.getGameSettings().getGameFileType().get(row);
            Globals.settings.getGameSettings().removeGameFileType(prefix);
          }
        }
      });
      panelGameFiletypes.add(btnRemoveGameFiletype, "4, 2, default, bottom");

      tfGameFiletype = new JTextField();
      panelGameFiletypes.add(tfGameFiletype, "2, 4, fill, default");
      tfGameFiletype.setColumns(10);
      JButton btnAddPlatform = new JButton(BUNDLE.getString("Button.add")); //$NON-NLS-1$
      btnAddPlatform.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (StringUtils.isNotEmpty(tfGameFiletype.getText())) {
            Globals.settings.getGameSettings().addGameFileTypes(tfGameFiletype.getText());
            tfGameFiletype.setText("");
          }
        }
      });
      panelGameFiletypes.add(btnAddPlatform, "4, 4");
    }

    add(panelGameFiletypes, "2, 4, fill, fill");

    {
      panelPlatform = new JPanel();
      panelPlatform.setBorder(new TitledBorder(
          UIManager.getBorder("TitledBorder.border"), BUNDLE.getString("Settings.platform"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
      panelPlatform.setLayout(new FormLayout(//
          new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC,
              FormFactory.DEFAULT_COLSPEC, }, //
          new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormFactory.RELATED_GAP_ROWSPEC,
              FormFactory.DEFAULT_ROWSPEC, }));

      JScrollPane scrollPlatform = new JScrollPane();
      panelPlatform.add(scrollPlatform, "2, 2, fill, fill");

      listPlatform = new JList();
      scrollPlatform.setViewportView(listPlatform);

      JButton scrollPanePlatform = new JButton(BUNDLE.getString("Button.remove")); //$NON-NLS-1$
      scrollPanePlatform.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          int row = listPlatform.getSelectedIndex();
          if (row != -1) {
            String prefix = Globals.settings.getGameSettings().getPlatformGame().get(row);
            Globals.settings.getGameSettings().removeGameFileType(prefix);
          }
        }
      });
      panelPlatform.add(scrollPanePlatform, "4, 2, default, bottom");

      List<String> platformGameCombox = new ArrayList<String>();

      for (Platform p : Platforms.getInstance().getPlatformList()) {
        String pc = p.getLongName();
        platformGameCombox.add(pc);
      }
      Collections.sort(platformGameCombox, String.CASE_INSENSITIVE_ORDER);
      tfPlatform = new JComboBox();

      for (String p : platformGameCombox) {
        tfPlatform.addItem((String) p);
      }

      panelPlatform.add(tfPlatform, "2, 4, fill, default");
      // tfPlatform.setColumns(10);

      JButton btnAddPlatform = new JButton(BUNDLE.getString("Button.add")); //$NON-NLS-1$
      btnAddPlatform.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          Globals.settings.getGameSettings().addPlatformGame((String) tfPlatform.getSelectedItem());
        }
      });

      panelPlatform.add(btnAddPlatform, "4, 4");
    }

    add(panelPlatform, "4, 4, fill, fill");

    {
      panelRom = new JPanel();

      panelRom
          .setBorder(new TitledBorder(
              UIManager.getBorder("TitledBorder.border"), BUNDLE.getString("Settings.gamefiletypes"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
      panelRom.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"),
          FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
          RowSpec.decode("default:grow"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

      PlatformUI newContentPane = new PlatformUI();

      panelRom.add(newContentPane, "2, 2, fill, fill");

      JButton btnApplyRomCollection = new JButton(BUNDLE.getString("Button.save")); //$NON-NLS-1$
      btnApplyRomCollection.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          MyTableModel.saveCollection();
        }
      });
      panelRom.add(btnApplyRomCollection, "4, 4");

    }

    add(panelRom, "6, 4, fill, fill");

    initDataBindings();
  }

  public static final class PlatformUI extends JPanel {
    /**
     * 
     */
    private static final long            serialVersionUID = 1L;
    private TableRowSorter<MyTableModel> sorter;

    public PlatformUI() {
      super(new GridLayout(1, 0));

      Integer large = 1200;
      Integer height = 500;
      Integer pathlength = 200;

      JTable table = new JTable(new MyTableModel()) {

        protected String[] columnToolTips = { "chose platform", "name", "Used ?", "Path to rom Emulator",
                                              "Emulator param (%ROM%) is used for your rom file", "Path to roms",
                                              "File mask (comma-separated) e.g *.iso, *.smc", "Path to Artwork" };

        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
          Component comp = super.prepareRenderer(renderer, row, column);
          JComponent jc = (JComponent) comp;// for Custom JComponent
          if (isCellEditable(row, column)) {
            comp.setBackground(Color.white);
          }
          else {
            comp.setBackground(Color.LIGHT_GRAY);
          }
          return comp;
        }

        @Override
        public String getToolTipText(MouseEvent e) {
          String tip = null;
          java.awt.Point p = e.getPoint();
          int rowIndex = rowAtPoint(p);
          int colIndex = columnAtPoint(p);
          int realColumnIndex = convertColumnIndexToModel(colIndex);

          if (realColumnIndex == 2) { // isCollection column
            TableModel model = getModel();
            String platform = (String) model.getValueAt(rowIndex, 0);
            tip = "Is " + platform + " a collection ? " + getValueAt(rowIndex, colIndex);
          }
          else {
            TableModel model = getModel();
            String platform = (String) model.getValueAt(rowIndex, 0);
            boolean isCollection = (Boolean) model.getValueAt(rowIndex, 2);
            if (Boolean.FALSE.equals(isCollection)) {
              tip = "Enable " + platform + " to declare available roms";
            }
            else {
              switch (realColumnIndex) {
                case 3: // EmulatorExecutable
                  tip = "Path to " + platform + " rom Emulator";
                  break;
                case 4: // EmulatorParam
                  tip = platform + " Emulator param (%ROM%) is used for your rom file";
                  break;
                case 5: // RomsCollectionPath
                  tip = "Path to " + platform + " roms";
                  break;
                case 6: // RomFileMask
                  tip = platform + "File mask (comma-separated) e.g *.iso, *.smc";
                  break;
                case 7: // RomsCollectionAntworkPath
                  tip = "Path to " + platform + " Artwork";
                  break;
              }
            }
            return tip;
          }
          return tip;
        }

        // Implement table header tool tips.
        protected JTableHeader createDefaultTableHeader() {
          return new JTableHeader(columnModel) {
            public String getToolTipText(MouseEvent e) {
              String tip = null;
              java.awt.Point p = e.getPoint();
              int index = columnModel.getColumnIndexAtX(p.x);
              int realIndex = columnModel.getColumn(index).getModelIndex();
              return columnToolTips[realIndex];
            }
          };
        }
      };

      table.setPreferredScrollableViewportSize(new Dimension(large, height));
      table.setFillsViewportHeight(true);
      table.setAutoCreateRowSorter(true);

      // Create the scroll pane and add the table to it.
      JScrollPane scrollPane = new JScrollPane(table);

      // Add the scroll pane to this panel.
      add(scrollPane);
      table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      TableColumn column = null;
      for (int columnIndex = 0; columnIndex <= 7; columnIndex++) {
        column = table.getColumnModel().getColumn(columnIndex);
        switch (columnIndex) {
          case 0: // LongName
            column.setPreferredWidth(200);
            break;
          case 1: // ShortName
            column.setPreferredWidth(100);
            break;
          case 2: // Iscollection
            column.setPreferredWidth(100);
            break;
          // case 3:
          // column.setPreferredWidth(large - 200 - 100 - 50);
          // column.setCellRenderer(new MyCellRenderer());
          // break;
          case 3: // EmulatorExecutable
            column.setPreferredWidth(pathlength);
            column.setCellEditor(new FileChooserCellEditor());
            break;
          case 4: // EmulatorParam
            column.setPreferredWidth(100);
            break;
          case 5: // RomsCollectionPath
            column.setPreferredWidth(pathlength);
            column.setCellEditor(new FolderChooserCellEditor());
            break;
          case 6: // RomFileMask
            column.setPreferredWidth(100);
            break;
          case 7: // RomsCollectionAntworkPath
            column.setPreferredWidth(pathlength);
            column.setCellEditor(new FolderChooserCellEditor());
            break;

          default:
            column.setPreferredWidth(50);
            break;
        }
      }

    }

    public static class MyTableModel extends AbstractTableModel {

      public Object[] longValues;

      private class SimplePlatform {

        private String  shortName                 = "";
        private String  longName                  = "";
        private String  deck                      = "";
        private boolean iscollection              = false;
        private String  emulatorExecutable        = "";
        private String  emulatorParam             = "";
        private String  romsCollectionPath        = "";
        private String  romFileMask               = "";
        private String  romsCollectionAntworkPath = "";

        public String getRomFileMask() {
          return romFileMask;
        }

        public void setRomFileMask(String romFileMask) {
          this.romFileMask = romFileMask;
        }

        public String getRomsCollectionAntworkPath() {
          return romsCollectionAntworkPath;
        }

        public void setRomsCollectionAntworkPath(String romsCollectionAntworkPath) {
          this.romsCollectionAntworkPath = romsCollectionAntworkPath;
        }

        public boolean isIscollection() {
          return iscollection;
        }

        public void setIscollection(boolean iscollection) {
          this.iscollection = iscollection;
        }

        public String getEmulatorExecutable() {
          return emulatorExecutable;
        }

        public void setEmulatorExecutable(String emulatorExecutable) {
          this.emulatorExecutable = emulatorExecutable;
        }

        public String getEmulatorParam() {
          return emulatorParam;
        }

        public void setEmulatorParam(String emulatorParam) {
          this.emulatorParam = emulatorParam;
        }

        public String getRomsCollectionPath() {
          return romsCollectionPath;
        }

        public void setRomsCollectionPath(String romsCollectionPath) {
          this.romsCollectionPath = romsCollectionPath;
        }

        public String getShortName() {
          return shortName;
        }

        public void setShortName(String shortName) {
          this.shortName = shortName;
        }

        public String getLongName() {
          return longName;
        }

        public void setLongName(String longName) {
          this.longName = longName;
        }

        public String getDeck() {
          return deck;
        }

        public void setDeck(String deck) {
          this.deck = deck;
        }

      }

      private static final ArrayList<SimplePlatform> simplePlatformList = new ArrayList<SimplePlatform>();

      private String[]                               columnNames        = { "LongName", "ShortName", "isCollection?", "Emulator Path",
                                                                            "Emulator param", "Roms Collection Path", "File mask", "Rom Antwork Path" };

      public MyTableModel() {
        super();
        for (Platform p : Platforms.getInstance().getPlatformList()) {
          SimplePlatform j = new SimplePlatform();
          j.setDeck(p.getDeck());
          j.setLongName(p.getLongName());
          j.setShortName(p.getShortName());
          j.setIscollection(false);
          RomCollectionConfig c = Globals.settings.getGameSettings().getromCollectionbyShortName(p.getShortName());
          if (c != null) {
            j.setEmulatorExecutable(c.getEmulatorExecutable());
            j.setEmulatorParam(c.getEmulatorParam());
            j.setIscollection(c.isIscollection());
            j.setRomFileMask(c.getRomFileMask());
            j.setRomsCollectionAntworkPath(c.getRomsCollectionAntworkPath());
            j.setRomsCollectionPath(c.getRomsCollectionPath());
          }

          simplePlatformList.add(j);
        }
      }

      public static void saveCollection() {
        Globals.settings.getGameSettings().removeallRomCollection();
        for (SimplePlatform s : simplePlatformList) {
          if (s.isIscollection() || !s.getEmulatorExecutable().equalsIgnoreCase("") || !s.getEmulatorParam().equalsIgnoreCase("")
              || !s.getRomFileMask().equalsIgnoreCase("") || !s.getRomsCollectionAntworkPath().equalsIgnoreCase("")
              || !s.getRomsCollectionPath().equalsIgnoreCase("")) {
            RomCollectionConfig c = new RomCollectionConfig();
            c.setEmulatorExecutable(s.getEmulatorExecutable());
            c.setEmulatorParam(s.getEmulatorParam());
            c.setIscollection(s.isIscollection());
            c.setRomFileMask(s.getRomFileMask());
            c.setRomsCollectionAntworkPath(s.getRomsCollectionAntworkPath());
            c.setRomsCollectionPath(s.getRomsCollectionPath());
            c.setShortName(s.getShortName());
            Globals.settings.getGameSettings().addRomCollectionConfig(c);
          }
        }
      }

      @Override
      public int getRowCount() {
        return simplePlatformList.size();
      }

      @Override
      public int getColumnCount() {
        return columnNames.length;
      }

      @Override
      public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
          case 0:
            return simplePlatformList.get(rowIndex).getLongName();
          case 1:
            return simplePlatformList.get(rowIndex).getShortName();
          case 2:
            return simplePlatformList.get(rowIndex).isIscollection();
            // case 3:
            // return simplePlatformList.get(rowIndex).getDeck();
          case 3:
            return simplePlatformList.get(rowIndex).getEmulatorExecutable();
          case 4:
            return simplePlatformList.get(rowIndex).getEmulatorParam();
          case 5:
            return simplePlatformList.get(rowIndex).getRomsCollectionPath();
          case 6:
            return simplePlatformList.get(rowIndex).getRomFileMask();
          case 7:
            return simplePlatformList.get(rowIndex).getRomsCollectionAntworkPath();
          default:
            return null;
        }
      }

      @Override
      public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex >= 2 && columnIndex <= 7)
          return true;
        else
          return false;
      }

      @Override
      public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (aValue != null) {
          SimplePlatform simplePlatform = simplePlatformList.get(rowIndex);

          switch (columnIndex) {
            case 2:
              simplePlatformList.get(rowIndex).setIscollection((Boolean) aValue);
              break;
            case 3:
              simplePlatformList.get(rowIndex).setEmulatorExecutable((String) aValue);
              break;
            case 4:
              simplePlatformList.get(rowIndex).setEmulatorParam((String) aValue);
              break;
            case 5:
              simplePlatformList.get(rowIndex).setRomsCollectionPath((String) aValue);
              break;
            case 6:
              simplePlatformList.get(rowIndex).setRomFileMask((String) aValue);
              break;
            case 7:
              simplePlatformList.get(rowIndex).setRomsCollectionAntworkPath((String) aValue);
              break;

            default:
              ;
          }
        }
      }

      @Override
      public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
          case 2:
            return Boolean.class;
          default:
            return Object.class;
        }
      }

    }

    public class MyCellRenderer extends JTextArea implements TableCellRenderer {
      public MyCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
      }

      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((String) value);
        setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
        if (table.getRowHeight(row) != getPreferredSize().height) {
          table.setRowHeight(row, getPreferredSize().height);
        }
        return this;
      }
    }

    public class FileChooserCellEditor extends DefaultCellEditor implements TableCellEditor {
      /** Number of clicks to start editing */
      private static final int CLICK_COUNT_TO_START = 2;
      /** Editor component */
      private JButton          button;
      /** File chooser */
      private JFileChooser     fileChooser;
      /** Selected file */
      private String           file                 = "";

      /**
       * Constructor.
       */
      public FileChooserCellEditor() {
        super(new JTextField());
        setClickCountToStart(CLICK_COUNT_TO_START);

        // Using a JButton as the editor component
        button = new JButton();
        button.setBackground(Color.white);
        button.setFont(button.getFont().deriveFont(Font.PLAIN));
        button.setBorder(null);

        // Dialog which will do the actual editing
        fileChooser = new JFileChooser();
      }

      @Override
      public Object getCellEditorValue() {
        return file;
      }

      @Override
      public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        file = value.toString();
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            fileChooser.setCurrentDirectory(new java.io.File("."));
            fileChooser.setDialogTitle("selectFile");
            fileChooser.setSelectedFile(new File(file));
            if (fileChooser.showOpenDialog(button) == JFileChooser.APPROVE_OPTION) {
              file = fileChooser.getSelectedFile().getAbsolutePath();
            }
            fireEditingStopped();
          }
        });
        button.setText(file);
        return button;
      }
    }

    public class FolderChooserCellEditor extends DefaultCellEditor implements TableCellEditor {
      /** Number of clicks to start editing */
      private static final int CLICK_COUNT_TO_START = 2;
      /** Editor component */
      private JButton          button;
      /** File chooser */
      private JFileChooser     folderChooser;
      /** Selected file */
      private String           file                 = "";

      /**
       * Constructor.
       */
      public FolderChooserCellEditor() {
        super(new JTextField());
        setClickCountToStart(CLICK_COUNT_TO_START);

        // Using a JButton as the editor component
        button = new JButton();
        button.setBackground(Color.white);
        button.setFont(button.getFont().deriveFont(Font.PLAIN));
        button.setBorder(null);

        // Dialog which will do the actual editing
        folderChooser = new JFileChooser();
      }

      @Override
      public Object getCellEditorValue() {
        return file;
      }

      @Override
      public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        file = value.toString();
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            folderChooser.setCurrentDirectory(new java.io.File("."));
            folderChooser.setDialogTitle("selectFolder");
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            folderChooser.setAcceptAllFileFilterUsed(false);
            folderChooser.setSelectedFile(new File(file));
            if (folderChooser.showOpenDialog(button) == JFileChooser.APPROVE_OPTION) {
              file = folderChooser.getSelectedFile().getAbsolutePath();
            }
            fireEditingStopped();
          }
        });
        button.setText(file);
        return button;
      }
    }
  }

  protected void initDataBindings() {

    //
    BeanProperty<GameSettings, List<String>> settingsBeanProperty_5 = BeanProperty.create("gameFileType");
    JListBinding<String, GameSettings, JList> jListBinding_1 = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE,
        settings.getGameSettings(), settingsBeanProperty_5, listGameFiletypes);
    jListBinding_1.bind();
    //
    BeanProperty<GameSettings, List<String>> settingsBeanProperty_6 = BeanProperty.create("gamefileType");
    JListBinding<String, GameSettings, JList> jListBinding_2 = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE,
        settings.getGameSettings(), settingsBeanProperty_6, listGameFiletypes);
    jListBinding_2.bind();
    //
    BeanProperty<GameSettings, List<String>> settingsBeanProperty_7 = BeanProperty.create("platformGame");
    JListBinding<String, GameSettings, JList> jListBinding_3 = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE,
        settings.getGameSettings(), settingsBeanProperty_7, listPlatform);
    jListBinding_3.bind();
  }
}

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

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ObjectProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.Settings;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.core.game.GameNfoNaming;
import org.tinymediamanager.core.game.GameRenamer;
import org.tinymediamanager.core.game.connector.GameConnectors;
import org.tinymediamanager.ui.TmmUIHelper;
import org.tinymediamanager.ui.UTF8Control;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameSettingsPanel.
 * 
 * @author Manuel Laggner
 */
public class GameSettingsPanel extends JPanel implements HierarchyListener {
  private static final long           serialVersionUID           = -7580437046944123496L;
  private static final ResourceBundle BUNDLE                     = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  private Settings                    settings                   = Settings.getInstance();
  private List<String>                separators                 = new ArrayList<String>(Arrays.asList("_", ".", "-"));

  /**
   * UI elements
   */
  private JTable                      tableGameSources;
  private JComboBox                   cbNfoFormat;
  private JCheckBox                   cbGameNfoFilename1;
  private JCheckBox                   cbGameNfoFilename2;
  private JTextField                  tfGamePath;
  private JTextField                  tfGameFilename;
  private JLabel                      lblExample;
  private JCheckBox                   chckbxSpaceSubstitution;
  private JComboBox                   cbSeparator;
  private JComboBox                   cbGameForPreview;
  private JCheckBox                   chckbxRemoveOtherNfos;
  private JCheckBox                   chckbxMultipleGamesPerFolder;
  private JCheckBox                   chckbxImageCache;
  private JCheckBox                   chckbxGamesetSingleGame;

  private ActionListener              actionCreateRenamerExample = new ActionListener() {
                                                                   @Override
                                                                   public void actionPerformed(ActionEvent e) {
                                                                     createRenamerExample();
                                                                   }
                                                                 };

  /**
   * Instantiates a new game settings panel.
   */
  public GameSettingsPanel() {
    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
        FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("fill:default"),
        FormFactory.LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    JPanel panelGameDataSources = new JPanel();

    panelGameDataSources.setBorder(new TitledBorder(null,
        BUNDLE.getString("Settings.game.datasource"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
    add(panelGameDataSources, "2, 2, fill, fill");
    panelGameDataSources.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(66dlu;default)"), FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("max(72dlu;default):grow"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("100px"), FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
        RowSpec.decode("100px:grow"), FormFactory.NARROW_LINE_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    JScrollPane scrollPane = new JScrollPane();
    panelGameDataSources.add(scrollPane, "2, 2, 5, 1, fill, fill");

    tableGameSources = new JTable();
    scrollPane.setViewportView(tableGameSources);

    JPanel panelGameSourcesButtons = new JPanel();
    panelGameDataSources.add(panelGameSourcesButtons, "8, 2, fill, top");
    panelGameSourcesButtons.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, },
        new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    JButton btnAdd = new JButton(BUNDLE.getString("Button.add")); //$NON-NLS-1$
    btnAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        File file = TmmUIHelper.selectDirectory(BUNDLE.getString("Settings.datasource.folderchooser")); //$NON-NLS-1$
        if (file != null && file.exists() && file.isDirectory()) {
          settings.getGameSettings().addGameDataSources(file.getAbsolutePath());
        }
      }
    });

    panelGameSourcesButtons.add(btnAdd, "2, 1, fill, top");

    JButton btnRemove = new JButton(BUNDLE.getString("Button.remove")); //$NON-NLS-1$
    btnRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        int row = tableGameSources.convertRowIndexToModel(tableGameSources.getSelectedRow());
        if (row != -1) { // nothing selected
          String path = Globals.settings.getGameSettings().getGameDataSource().get(row);
          String[] choices = { BUNDLE.getString("Button.continue"), BUNDLE.getString("Button.abort") }; //$NON-NLS-1$
          int decision = JOptionPane.showOptionDialog(null, String.format(BUNDLE.getString("Settings.game.datasource.remove.info"), path),
              BUNDLE.getString("Settings.datasource.remove"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices,
              BUNDLE.getString("Button.abort")); //$NON-NLS-1$
          if (decision == 0) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Globals.settings.getGameSettings().removeGameDataSources(path);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
        }
      }
    });
    panelGameSourcesButtons.add(btnRemove, "2, 3, fill, top");

    JLabel lblAllowMultipleGamesPerFolder = new JLabel(BUNDLE.getString("Settings.multipleGames")); //$NON-NLS-1$
    panelGameDataSources.add(lblAllowMultipleGamesPerFolder, "2, 4, right, default");

    chckbxMultipleGamesPerFolder = new JCheckBox("");
    panelGameDataSources.add(chckbxMultipleGamesPerFolder, "4, 4");

    JTextPane tpMultipleGamesHint = new JTextPane();
    tpMultipleGamesHint.setFont(new Font("Dialog", Font.PLAIN, 10));
    tpMultipleGamesHint.setBackground(UIManager.getColor("Panel.background"));
    tpMultipleGamesHint.setText(BUNDLE.getString("Settings.multipleGames.hint")); //$NON-NLS-1$
    tpMultipleGamesHint.setEditable(false);
    panelGameDataSources.add(tpMultipleGamesHint, "6, 4, 5, 1, fill, fill");

    JSeparator separator_1 = new JSeparator();
    panelGameDataSources.add(separator_1, "2, 6, 9, 1");

    JLabel lblNfoFormat = new JLabel(BUNDLE.getString("Settings.nfoFormat")); //$NON-NLS-1$
    panelGameDataSources.add(lblNfoFormat, "2, 8, right, default");

    cbNfoFormat = new JComboBox(GameConnectors.values());
    panelGameDataSources.add(cbNfoFormat, "4, 8, fill, default");

    JLabel lblNfoFileNaming = new JLabel(BUNDLE.getString("Settings.nofFileNaming")); //$NON-NLS-1$
    panelGameDataSources.add(lblNfoFileNaming, "2, 10, right, default");

    cbGameNfoFilename1 = new JCheckBox(BUNDLE.getString("Settings.gamefilename") + ".nfo"); //$NON-NLS-1$
    panelGameDataSources.add(cbGameNfoFilename1, "4, 10");

    cbGameNfoFilename2 = new JCheckBox("game.nfo");
    panelGameDataSources.add(cbGameNfoFilename2, "4, 11");

    JSeparator separator_2 = new JSeparator();
    panelGameDataSources.add(separator_2, "2, 13, 9, 1");

    JLabel lblImageCache = new JLabel(BUNDLE.getString("Settings.imagecacheimport")); //$NON-NLS-1$
    panelGameDataSources.add(lblImageCache, "2, 15");

    chckbxImageCache = new JCheckBox("");
    panelGameDataSources.add(chckbxImageCache, "4, 15");

    JLabel lblImageCacheHint = new JLabel(BUNDLE.getString("Settings.imagecacheimporthint")); //$NON-NLS-1$
    lblImageCacheHint.setFont(new Font("Dialog", Font.PLAIN, 10));
    panelGameDataSources.add(lblImageCacheHint, "6, 15, 5, 1");

    // the panel renamer
    JPanel panelRenamer = new JPanel();
    panelRenamer.setBorder(new TitledBorder(null, BUNDLE.getString("Settings.renamer"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
    add(panelRenamer, "2, 4, fill, fill");
    panelRenamer.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("50px"),
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, },
        new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("fill:default:grow"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    JLabel lblGamePath = new JLabel(BUNDLE.getString("Settings.renamer.folder")); //$NON-NLS-1$
    panelRenamer.add(lblGamePath, "2, 2, right, default");

    tfGamePath = new JTextField();
    tfGamePath.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent arg0) {
        createRenamerExample();
      }

      @Override
      public void insertUpdate(DocumentEvent arg0) {
        createRenamerExample();
      }

      @Override
      public void changedUpdate(DocumentEvent arg0) {
        createRenamerExample();
      }
    });
    panelRenamer.add(tfGamePath, "4, 2, 3, 1, fill, default");
    tfGamePath.setColumns(10);

    JTextPane txtpntTitle = new JTextPane();
    txtpntTitle.setFont(new Font("Dialog", Font.PLAIN, 10));
    txtpntTitle.setBackground(UIManager.getColor("Panel.background"));
    txtpntTitle.setText(BUNDLE.getString("Settings.game.renamer.info")); //$NON-NLS-1$
    txtpntTitle.setEditable(false);
    panelRenamer.add(txtpntTitle, "10, 2, 1, 12, fill, fill");

    JLabel lblGameFilename = new JLabel(BUNDLE.getString("Settings.renamer.file")); //$NON-NLS-1$
    panelRenamer.add(lblGameFilename, "2, 4, right, fill");

    tfGameFilename = new JTextField();
    tfGameFilename.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent arg0) {
        createRenamerExample();
      }

      @Override
      public void insertUpdate(DocumentEvent arg0) {
        createRenamerExample();
      }

      @Override
      public void changedUpdate(DocumentEvent arg0) {
        createRenamerExample();
      }
    });
    lblGameFilename.setLabelFor(tfGameFilename);
    panelRenamer.add(tfGameFilename, "4, 4, 3, 1, fill, default");
    tfGameFilename.setColumns(10);

    chckbxSpaceSubstitution = new JCheckBox(BUNDLE.getString("Settings.game.renamer.spacesubstitution")); //$NON-NLS-1$
    chckbxSpaceSubstitution.addActionListener(actionCreateRenamerExample);
    panelRenamer.add(chckbxSpaceSubstitution, "4, 6");

    cbSeparator = new JComboBox(separators.toArray());
    panelRenamer.add(cbSeparator, "6, 6, fill, default");
    cbSeparator.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        checkChanges();
        createRenamerExample();
      }
    });

    chckbxGamesetSingleGame = new JCheckBox(BUNDLE.getString("Settings.renamer.gamesetsinglegame")); //$NON-NLS-1$
    chckbxGamesetSingleGame.addActionListener(actionCreateRenamerExample);
    panelRenamer.add(chckbxGamesetSingleGame, "4, 8, 5, 1, fill, default");

    JTextPane txtrChooseAFolder = new JTextPane();
    txtrChooseAFolder.setFont(new Font("Dialog", Font.PLAIN, 10));
    txtrChooseAFolder.setText(BUNDLE.getString("Settings.game.renamer.example")); //$NON-NLS-1$
    txtrChooseAFolder.setBackground(UIManager.getColor("Panel.background"));
    panelRenamer.add(txtrChooseAFolder, "2, 10, 3, 1, fill, bottom");

    JLabel lblExampleT = new JLabel(BUNDLE.getString("Settings.example")); //$NON-NLS-1$
    panelRenamer.add(lblExampleT, "2, 12");

    cbGameForPreview = new JComboBox();
    cbGameForPreview.addActionListener(actionCreateRenamerExample);
    panelRenamer.add(cbGameForPreview, "4, 12, 5, 1, fill, default");

    lblExample = new JLabel("");
    lblExample.setFont(lblExample.getFont().deriveFont(11f));
    panelRenamer.add(lblExample, "2, 14, 9, 1");

    JSeparator separator = new JSeparator();
    panelRenamer.add(separator, "1, 16, 10, 1");

    JLabel lblCleanupOptions = new JLabel(BUNDLE.getString("Settings.cleanupoptions")); //$NON-NLS-1$
    panelRenamer.add(lblCleanupOptions, "2, 18, 3, 1");

    chckbxRemoveOtherNfos = new JCheckBox("");
    panelRenamer.add(chckbxRemoveOtherNfos, "2, 20, right, default");

    JLabel lblRemoveAllNon = new JLabel(BUNDLE.getString("Settings.renamer.removenfo")); //$NON-NLS-1$
    panelRenamer.add(lblRemoveAllNon, "4, 20, 5, 1");

    initDataBindings();

    // NFO filenames
    List<GameNfoNaming> gameNfoFilenames = settings.getGameSettings().getGameNfoFilenames();
    if (gameNfoFilenames.contains(GameNfoNaming.FILENAME_NFO)) {
      cbGameNfoFilename1.setSelected(true);
    }

    if (gameNfoFilenames.contains(GameNfoNaming.GAME_NFO)) {
      cbGameNfoFilename2.setSelected(true);
    }

    // item listener
    cbGameNfoFilename1.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        checkChanges();
      }
    });
    cbGameNfoFilename2.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        checkChanges();
      }
    });

    // space separator
    String replacement = settings.getGameSettings().getGameRenamerSpaceReplacement();
    int index = separators.indexOf(replacement);
    if (index >= 0) {
      cbSeparator.setSelectedIndex(index);
    }
  }

  private void createRenamerExample() {
    Game game = null;
    if (cbGameForPreview.getSelectedItem() instanceof GamePreviewContainer) {
      GamePreviewContainer container = (GamePreviewContainer) cbGameForPreview.getSelectedItem();
      game = container.game;
    }

    if (game != null) {
      String path = "";
      String filename = "";
      if (StringUtils.isNotBlank(tfGamePath.getText())) {
        path = GameRenamer.createDestinationForFoldername(tfGamePath.getText(), game);
      }
      else {
        path = game.getPath();
      }

      if (StringUtils.isNotBlank(tfGameFilename.getText())) {
        List<MediaFile> mediaFiles = game.getMediaFiles(MediaFileType.GAME);
        if (mediaFiles.size() > 0) {
          String extension = FilenameUtils.getExtension(mediaFiles.get(0).getFilename());
          filename = GameRenamer.createDestinationForFilename(tfGameFilename.getText(), game) + "." + extension;
        }
      }
      else {
        filename = game.getMediaFiles(MediaFileType.GAME).get(0).getFilename();
      }

      lblExample.setText(game.getDataSource() + File.separator + path + File.separator + filename);
    }
    else {
      lblExample.setText(BUNDLE.getString("Settings.game.renamer.nogame")); //$NON-NLS-1$
    }
  }

  /**
   * check changes of checkboxes
   */
  private void checkChanges() {
    // set NFO filenames
    settings.getGameSettings().clearGameNfoFilenames();
    if (cbGameNfoFilename1.isSelected()) {
      settings.getGameSettings().addGameNfoFilename(GameNfoNaming.FILENAME_NFO);
    }
    if (cbGameNfoFilename2.isSelected()) {
      settings.getGameSettings().addGameNfoFilename(GameNfoNaming.GAME_NFO);
    }
    // separator
    String separator = (String) cbSeparator.getSelectedItem();
    settings.getGameSettings().setGameRenamerSpaceReplacement(separator);
  }

  private void buildAndInstallGameArray() {
    cbGameForPreview.removeAllItems();
    List<Game> allGames = new ArrayList<Game>(GameList.getInstance().getGames());
    Collections.sort(allGames, new GameComparator());
    for (Game game : allGames) {
      GamePreviewContainer container = new GamePreviewContainer();
      container.game = game;
      cbGameForPreview.addItem(container);
    }
  }

  private class GamePreviewContainer {
    Game game;

    public String toString() {
      return game.getTitle();
    }
  }

  private class GameComparator implements Comparator<Game> {
    @Override
    public int compare(Game arg0, Game arg1) {
      return arg0.getTitle().compareTo(arg1.getTitle());
    }
  }

  @Override
  public void hierarchyChanged(HierarchyEvent arg0) {
    if (isShowing()) {
      buildAndInstallGameArray();
    }
  }

  @Override
  public void addNotify() {
    super.addNotify();
    addHierarchyListener(this);
  }

  @Override
  public void removeNotify() {
    removeHierarchyListener(this);
    super.removeNotify();
  }

  protected void initDataBindings() {
    BeanProperty<Settings, List<String>> settingsBeanProperty_4 = BeanProperty.create("gameSettings.gameDataSource");
    JTableBinding<String, Settings, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ, settings, settingsBeanProperty_4,
        tableGameSources);
    //
    ObjectProperty<String> stringObjectProperty = ObjectProperty.create();
    jTableBinding.addColumnBinding(stringObjectProperty).setColumnName("Source");
    //
    jTableBinding.bind();
    //
    BeanProperty<Settings, GameConnectors> settingsBeanProperty_10 = BeanProperty.create("gameSettings.gameConnector");
    BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
    AutoBinding<Settings, GameConnectors, JComboBox, Object> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_10, cbNfoFormat, jComboBoxBeanProperty);
    autoBinding_9.bind();
    //
    BeanProperty<Settings, String> settingsBeanProperty_11 = BeanProperty.create("gameSettings.gameRenamerPathname");
    BeanProperty<JTextField, String> jTextFieldBeanProperty_3 = BeanProperty.create("text");
    AutoBinding<Settings, String, JTextField, String> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_11, tfGamePath, jTextFieldBeanProperty_3);
    autoBinding_10.bind();
    //
    BeanProperty<Settings, String> settingsBeanProperty_12 = BeanProperty.create("gameSettings.gameRenamerFilename");
    BeanProperty<JTextField, String> jTextFieldBeanProperty_4 = BeanProperty.create("text");
    AutoBinding<Settings, String, JTextField, String> autoBinding_11 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_12, tfGameFilename, jTextFieldBeanProperty_4);
    autoBinding_11.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty = BeanProperty.create("gameSettings.gameRenamerSpaceSubstitution");
    BeanProperty<JCheckBox, Boolean> jCheckBoxBeanProperty = BeanProperty.create("selected");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty, chckbxSpaceSubstitution, jCheckBoxBeanProperty);
    autoBinding.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_1 = BeanProperty.create("gameSettings.gameRenamerNfoCleanup");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_1, chckbxRemoveOtherNfos, jCheckBoxBeanProperty);
    autoBinding_1.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_2 = BeanProperty.create("gameSettings.detectGameMultiDir");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_2, chckbxMultipleGamesPerFolder, jCheckBoxBeanProperty);
    autoBinding_2.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_3 = BeanProperty.create("gameSettings.buildImageCacheOnImport");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_3, chckbxImageCache, jCheckBoxBeanProperty);
    autoBinding_3.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_5 = BeanProperty.create("gameSettings.gameRenamerCreateGamesetForSingleGame");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_5, chckbxGamesetSingleGame, jCheckBoxBeanProperty);
    autoBinding_4.bind();
  }
}

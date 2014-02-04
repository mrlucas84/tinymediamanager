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
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.tinymediamanager.core.Settings;
import org.tinymediamanager.core.game.GameScrapers;
import org.tinymediamanager.scraper.CountryCode;
import org.tinymediamanager.scraper.MediaLanguages;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.games.GameScraperMetadataPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameScraperSettingsPanel.
 * 
 * @author Manuel Laggner
 */
public class GameScraperSettingsPanel extends JPanel {
  private static final long           serialVersionUID = -299825914193235308L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  private Settings                    settings         = Settings.getInstance();

  /**
   * UI Elements
   */
  private ButtonGroup                 buttonGroupScraper;
  private JComboBox                   cbScraperLanguage;
  private JComboBox                   cbCertificationCountry;
  private JCheckBox                   cbImdbTranslateableContent;
  private JCheckBox                   cbScrapergamedb;
  private JCheckBox                   chckbxAutomaticallyScrapeImages;
  private JPanel                      panelScraperMetadata;
  private JPanel                      panelScraperMetadataContainer;
  private JCheckBox                   cbScrapergiamtbomb;
  private JCheckBox                   cbScraperjeuxvideo;
  private JPanel                      panel;
  private JCheckBox                   cbgiamtbomb;
  private JCheckBox                   cbgameDB;
  private JCheckBox                   cbjeuxvideo;
  private JTextPane                   lblScraperThresholdHint;
  private JPanel                      panelAutomaticScraper;
  private JSlider                     sliderThreshold;

  /**
   * Instantiates a new game scraper settings panel.
   */
  public GameScraperSettingsPanel() {
    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("default:grow"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), }));
    JPanel panelGameScrapers = new JPanel();
    panelGameScrapers.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), BUNDLE.getString("scraper.metadata.defaults"),
        TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
    add(panelGameScrapers, "2, 2, fill, top");
    panelGameScrapers.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("default:grow"), }, new RowSpec[] { FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    cbScrapergiamtbomb = new JCheckBox("Giant Bomb");
    buttonGroupScraper = new ButtonGroup();
    buttonGroupScraper.add(cbScrapergiamtbomb);
    cbScrapergiamtbomb.setSelected(true);
    panelGameScrapers.add(cbScrapergiamtbomb, "1, 2");

    cbScrapergamedb = new JCheckBox("TheGameDB");
    buttonGroupScraper.add(cbScrapergamedb);
    panelGameScrapers.add(cbScrapergamedb, "1, 4");

    //    cbImdbTranslateableContent = new JCheckBox(BUNDLE.getString("Settings.getfromTMDB")); //$NON-NLS-1$
    // panelGameScrapers.add(cbImdbTranslateableContent, "3, 4");

    cbScraperjeuxvideo = new JCheckBox("T jeuxvideo.com");
    buttonGroupScraper.add(cbScraperjeuxvideo);
    panelGameScrapers.add(cbScraperjeuxvideo, "1, 6");

    JSeparator separator = new JSeparator();
    panelGameScrapers.add(separator, "1, 11, 3, 1");

    JLabel lblScraperLanguage = new JLabel(BUNDLE.getString("Settings.preferredLanguage")); //$NON-NLS-1$
    panelGameScrapers.add(lblScraperLanguage, "1, 12, right, default");

    cbScraperLanguage = new JComboBox(MediaLanguages.values());
    panelGameScrapers.add(cbScraperLanguage, "3, 12");

    JLabel lblCountry = new JLabel(BUNDLE.getString("Settings.certificationCountry")); //$NON-NLS-1$
    panelGameScrapers.add(lblCountry, "1, 14, right, default");

    cbCertificationCountry = new JComboBox(CountryCode.values());
    panelGameScrapers.add(cbCertificationCountry, "3, 14, fill, default");

    panelScraperMetadataContainer = new JPanel();
    panelScraperMetadataContainer.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), BUNDLE
        .getString("scraper.metadata.defaults"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51))); //$NON-NLS-1$
    add(panelScraperMetadataContainer, "4, 2, fill, top");
    panelScraperMetadataContainer.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), },
        new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    panelScraperMetadata = new GameScraperMetadataPanel(settings.getGameScraperMetadataConfig());
    panelScraperMetadataContainer.add(panelScraperMetadata, "1, 1, 2, 1, fill, default");

    chckbxAutomaticallyScrapeImages = new JCheckBox(BUNDLE.getString("Settings.default.autoscrape")); //$NON-NLS-1$
    panelScraperMetadataContainer.add(chckbxAutomaticallyScrapeImages, "2, 3");

    panel = new JPanel();
    panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), BUNDLE.getString("scraper.trailer"), TitledBorder.LEADING,
        TitledBorder.TOP, null, null)); //$NON-NLS-1$
    add(panel, "2, 4, fill, fill");
    panel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, }));

    cbgiamtbomb = new JCheckBox("Giant Bomb");
    panel.add(cbgiamtbomb, "1, 2");

    cbgameDB = new JCheckBox("The GameDB");
    panel.add(cbgameDB, "1, 4");

    cbjeuxvideo = new JCheckBox("jeuxvideo.com");
    panel.add(cbjeuxvideo, "1, 6");

    panelAutomaticScraper = new JPanel();
    panelAutomaticScraper.setBorder(new TitledBorder(null, "Automatic scraper", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    add(panelAutomaticScraper, "4, 4, fill, fill");
    panelAutomaticScraper.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("default:grow"), }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    JLabel lblScraperTreshold = new JLabel(BUNDLE.getString("Settings.scraperTreshold"));
    panelAutomaticScraper.add(lblScraperTreshold, "1, 2, default, top");

    sliderThreshold = new JSlider();
    sliderThreshold.setMinorTickSpacing(5);
    sliderThreshold.setMajorTickSpacing(10);
    sliderThreshold.setPaintTicks(true);
    sliderThreshold.setPaintLabels(true);
    sliderThreshold.setValue((int) (settings.getGameSettings().getScraperThreshold() * 100));
    java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<Integer, JLabel>();
    labelTable.put(new Integer(100), new JLabel("1.0"));
    labelTable.put(new Integer(75), new JLabel("0.75"));
    labelTable.put(new Integer(50), new JLabel("0.50"));
    labelTable.put(new Integer(25), new JLabel("0.25"));
    labelTable.put(new Integer(0), new JLabel("0.0"));
    sliderThreshold.setLabelTable(labelTable);
    sliderThreshold.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent arg0) {
        settings.getGameSettings().setScraperThreshold(sliderThreshold.getValue() / 100.0);
      }
    });
    panelAutomaticScraper.add(sliderThreshold, "3, 2");

    lblScraperThresholdHint = new JTextPane();
    panelAutomaticScraper.add(lblScraperThresholdHint, "1, 6, 3, 1");
    lblScraperThresholdHint.setOpaque(false);
    lblScraperThresholdHint.setFont(new Font("Dialog", Font.PLAIN, 10));
    lblScraperThresholdHint.setText(BUNDLE.getString("Settings.scraperTreshold.hint"));

    initDataBindings();

    // set game Scrapers
    GameScrapers gameScraper = settings.getGameSettings().getGameScraper();
    switch (gameScraper) {
      case GAMEDB:
        cbScrapergamedb.setSelected(true);
        break;

      case JEUXVIDEO:
        cbScraperjeuxvideo.setSelected(true);
        break;

      case GIANTBOMB:
      default:
        cbScrapergiamtbomb.setSelected(true);
    }

    cbgiamtbomb.setSelected(settings.getGameSettings().isTrailerScraperGiantBomb());
    cbjeuxvideo.setSelected(settings.getGameSettings().isTrailerScraperJeuxVideo());
    cbgameDB.setSelected(settings.getGameSettings().isTrailerScraperGameDB());

    cbScrapergamedb.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        checkChanges();
      }
    });
    cbScrapergiamtbomb.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        checkChanges();
      }
    });
    cbScraperjeuxvideo.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        checkChanges();
      }
    });
  }

  /**
   * Check changes.
   */
  public void checkChanges() {
    // save scraper
    if (cbScrapergamedb.isSelected()) {
      settings.getGameSettings().setGameScraper(GameScrapers.GAMEDB);
    }
    if (cbScrapergiamtbomb.isSelected()) {
      settings.getGameSettings().setGameScraper(GameScrapers.GIANTBOMB);
    }
    if (cbScraperjeuxvideo.isSelected()) {
      settings.getGameSettings().setGameScraper(GameScrapers.JEUXVIDEO);
    }
  }

  protected void initDataBindings() {
    BeanProperty<Settings, MediaLanguages> settingsBeanProperty_8 = BeanProperty.create("gameSettings.scraperLanguage");
    BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
    AutoBinding<Settings, MediaLanguages, JComboBox, Object> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_8, cbScraperLanguage, jComboBoxBeanProperty);
    autoBinding_7.bind();
    //
    BeanProperty<Settings, CountryCode> settingsBeanProperty_9 = BeanProperty.create("gameSettings.certificationCountry");
    AutoBinding<Settings, CountryCode, JComboBox, Object> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_9, cbCertificationCountry, jComboBoxBeanProperty);
    autoBinding_8.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_13 = BeanProperty.create("gameSettings.imdbScrapeForeignLanguage");
    BeanProperty<JCheckBox, Boolean> jCheckBoxBeanProperty = BeanProperty.create("selected");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_12 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_13, cbImdbTranslateableContent, jCheckBoxBeanProperty);
    autoBinding_12.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty = BeanProperty.create("gameSettings.scrapeBestImage");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty, chckbxAutomaticallyScrapeImages, jCheckBoxBeanProperty);
    autoBinding.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_2 = BeanProperty.create("gameSettings.trailerScraperGiantBomb");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_2, cbgiamtbomb, jCheckBoxBeanProperty);
    autoBinding_2.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_3 = BeanProperty.create("gameSettings.trailerScraperGameDB");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_3, cbgameDB, jCheckBoxBeanProperty);
    autoBinding_3.bind();
    //
    BeanProperty<Settings, Boolean> settingsBeanProperty_4 = BeanProperty.create("gameSettings.trailerScraperJeuxVideo");
    AutoBinding<Settings, Boolean, JCheckBox, Boolean> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, settings,
        settingsBeanProperty_4, cbjeuxvideo, jCheckBoxBeanProperty);
    autoBinding_4.bind();
  }
}

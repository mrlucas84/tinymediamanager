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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.scraper.GameMediaGenres;
import org.tinymediamanager.ui.SmallCheckBoxUI;
import org.tinymediamanager.ui.SmallTextFieldBorder;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.CollapsiblePanel;
import org.tinymediamanager.ui.components.SmallComboBox;
import org.tinymediamanager.ui.games.GameExtendedComparator.GameInGameSet;
import org.tinymediamanager.ui.games.GameExtendedComparator.IsFavoriteFlag;
import org.tinymediamanager.ui.games.GameExtendedComparator.SortColumn;
import org.tinymediamanager.ui.games.GameExtendedComparator.SortOrder;
import org.tinymediamanager.ui.games.GamesExtendedMatcher.SearchOptions;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameExtendedSearchPanel. played
 * 
 * @author Manuel Laggner
 */
public class GameExtendedSearchPanel extends CollapsiblePanel {
  private static final long            serialVersionUID = -4170930017190753789L;
  private static final ResourceBundle  BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$
  private static final float           FONT_SIZE        = 11f;
  private static final SmallCheckBoxUI CHECKBOX_UI      = new SmallCheckBoxUI();

  private GameList                     gameList         = GameList.getInstance();
  private GameSelectionModel           gameSelectionModel;

  /**
   * UI Elements
   */
  private JCheckBox                    cbFilterIsFavorite;
  private JLabel                       lblGenre;
  private JComboBox                    cbGenre;
  private JComboBox                    cbSortColumn;
  private JComboBox                    cbSortOrder;
  private JLabel                       lblFilterBy;
  private JLabel                       lblIsFavoriteFlag;
  private JComboBox                    cbIsFavorite;
  private JCheckBox                    cbFilterGenre;
  private JLabel                       lblSortBy;
  private JCheckBox                    cbFilterCast;
  private JLabel                       lblCastMember;
  private JTextField                   tfCastMember;
  private JCheckBox                    cbFilterTag;
  private JLabel                       lblTag;
  private JComboBox                    cbTag;
  private JCheckBox                    cbFilterDuplicates;
  private JLabel                       lblShowDuplicates;
  private JCheckBox                    cbFilterGameset;
  private JLabel                       lblGamesInGameset;
  private JComboBox                    cbGameset;
  private JCheckBox                    cbFilterPlatform;
  private JLabel                       lblPlatform;
  private JComboBox                    cbPlatform;
  private JCheckBox                    cbFilterVideoCodec;
  private JLabel                       lblVideoCodec;
  private JComboBox                    cbVideoCodec;
  private JCheckBox                    cbFilterAudioCodec;
  private JLabel                       lblAudioCodec;
  private JComboBox                    cbAudioCodec;

  private final Action                 actionSort       = new SortAction();
  private final Action                 actionFilter     = new FilterAction();

  /**
   * Instantiates a new game extended search panel.
   * 
   * @param model
   *          the model
   */
  public GameExtendedSearchPanel(GameSelectionModel model) {
    super(BUNDLE.getString("gameextendedsearch.options")); //$NON-NLS-1$

    this.gameSelectionModel = model;

    // JPanel panel = new JPanel();
    panel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] {
        FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, }));

    //    lblFilterBy = new JLabel(BUNDLE.getString("gameextendedsearch.filterby")); //$NON-NLS-1$
    // setComponentFont(lblFilterBy);
    // panel.add(lblFilterBy, "2, 1, 3, 1");

    cbFilterDuplicates = new JCheckBox("");
    cbFilterDuplicates.setUI(CHECKBOX_UI);
    cbFilterDuplicates.setAction(actionFilter);
    panel.add(cbFilterDuplicates, "2, 3");

    lblShowDuplicates = new JLabel(BUNDLE.getString("gameextendedsearch.duplicates")); //$NON-NLS-1$
    setComponentFont(lblShowDuplicates);
    panel.add(lblShowDuplicates, "4, 3, right, default");

    cbFilterIsFavorite = new JCheckBox("");
    cbFilterIsFavorite.setUI(CHECKBOX_UI);
    cbFilterIsFavorite.setAction(actionFilter);
    panel.add(cbFilterIsFavorite, "2, 4");

    lblIsFavoriteFlag = new JLabel(BUNDLE.getString("gameextendedsearch.isfavorite")); //$NON-NLS-1$
    setComponentFont(lblIsFavoriteFlag);
    panel.add(lblIsFavoriteFlag, "4, 4, right, default");

    cbIsFavorite = new SmallComboBox(IsFavoriteFlag.values());
    setComponentFont(cbIsFavorite);
    cbIsFavorite.setAction(actionFilter);
    panel.add(cbIsFavorite, "6, 4, fill, default");

    cbFilterGenre = new JCheckBox("");
    cbFilterGenre.setUI(CHECKBOX_UI);
    cbFilterGenre.setAction(actionFilter);
    panel.add(cbFilterGenre, "2, 5");

    lblGenre = new JLabel(BUNDLE.getString("gameextendedsearch.genre")); //$NON-NLS-1$
    setComponentFont(lblGenre);
    panel.add(lblGenre, "4, 5, right, default");

    cbGenre = new SmallComboBox(GameMediaGenres.values());
    setComponentFont(cbGenre);
    cbGenre.setAction(actionFilter);
    panel.add(cbGenre, "6, 5, fill, default");

    cbFilterCast = new JCheckBox("");
    cbFilterCast.setUI(CHECKBOX_UI);
    cbFilterCast.setAction(actionFilter);
    panel.add(cbFilterCast, "2, 6");

    lblCastMember = new JLabel(BUNDLE.getString("gameextendedsearch.cast")); //$NON-NLS-1$
    setComponentFont(lblCastMember);
    panel.add(lblCastMember, "4, 6, right, default");

    tfCastMember = new JTextField();
    setComponentFont(tfCastMember);
    tfCastMember.setBorder(new SmallTextFieldBorder());
    panel.add(tfCastMember, "6, 6, fill, default");
    tfCastMember.setColumns(10);
    tfCastMember.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        actionFilter.actionPerformed(null);
      }

      public void insertUpdate(DocumentEvent e) {
        actionFilter.actionPerformed(null);
      }

      public void removeUpdate(DocumentEvent e) {
        actionFilter.actionPerformed(null);
      }
    });

    cbFilterTag = new JCheckBox("");
    cbFilterTag.setUI(CHECKBOX_UI);
    cbFilterTag.setAction(actionFilter);
    panel.add(cbFilterTag, "2, 7");

    lblTag = new JLabel(BUNDLE.getString("gameextendedsearch.tag")); //$NON-NLS-1$
    setComponentFont(lblTag);
    panel.add(lblTag, "4, 7, right, default");

    cbTag = new SmallComboBox();
    setComponentFont(cbTag);
    cbTag.setAction(actionFilter);

    panel.add(cbTag, "6, 7, fill, default");

    cbFilterGameset = new JCheckBox("");
    cbFilterGameset.setUI(CHECKBOX_UI);
    cbFilterGameset.setAction(actionFilter);
    panel.add(cbFilterGameset, "2, 8");

    lblGamesInGameset = new JLabel(BUNDLE.getString("gameextendedsearch.gameset")); //$NON-NLS-1$
    setComponentFont(lblGamesInGameset);
    panel.add(lblGamesInGameset, "4, 8, right, default");

    cbGameset = new SmallComboBox(GameInGameSet.values());
    setComponentFont(cbGameset);
    cbGameset.setAction(actionFilter);
    panel.add(cbGameset, "6, 8, fill, default");

    cbFilterPlatform = new JCheckBox("");
    cbFilterPlatform.setUI(CHECKBOX_UI);
    cbFilterPlatform.setAction(actionFilter);
    panel.add(cbFilterPlatform, "2, 9");

    lblPlatform = new JLabel(BUNDLE.getString("metatag.platform")); //$NON-NLS-1$
    setComponentFont(lblPlatform);
    panel.add(lblPlatform, "4, 9, right, default");

    cbPlatform = new SmallComboBox();
    for (String p : Globals.settings.getGameSettings().getPlatformGame()) {
      cbPlatform.addItem(p);
    }
    setComponentFont(cbPlatform);
    cbPlatform.setAction(actionFilter);
    panel.add(cbPlatform, "6, 9, fill, default");

    cbFilterVideoCodec = new JCheckBox("");
    cbFilterVideoCodec.setUI(CHECKBOX_UI);
    cbFilterVideoCodec.setAction(actionFilter);
    panel.add(cbFilterVideoCodec, "2, 10");

    lblVideoCodec = new JLabel(BUNDLE.getString("metatag.videocodec")); //$NON-NLS-1$
    setComponentFont(lblVideoCodec);
    panel.add(lblVideoCodec, "4, 10, right, default");

    cbVideoCodec = new SmallComboBox();
    setComponentFont(cbVideoCodec);
    cbVideoCodec.setAction(actionFilter);
    panel.add(cbVideoCodec, "6, 10, fill, default");

    cbFilterAudioCodec = new JCheckBox("");
    cbFilterAudioCodec.setUI(CHECKBOX_UI);
    cbFilterAudioCodec.setAction(actionFilter);
    panel.add(cbFilterAudioCodec, "2, 11");

    lblAudioCodec = new JLabel(BUNDLE.getString("metatag.audiocodec")); //$NON-NLS-1$
    setComponentFont(lblAudioCodec);
    panel.add(lblAudioCodec, "4, 11, right, default");

    cbAudioCodec = new SmallComboBox();
    setComponentFont(cbAudioCodec);
    cbAudioCodec.setAction(actionFilter);
    panel.add(cbAudioCodec, "6, 11, fill, default");

    JSeparator separator = new JSeparator();
    panel.add(separator, "2, 13, 5, 1");

    lblSortBy = new JLabel(BUNDLE.getString("gameextendedsearch.sortby")); //$NON-NLS-1$
    setComponentFont(lblSortBy);
    // panel.add(lblSortBy, "2, 11, 3, 1");
    panel.add(lblSortBy, "2, 15");

    cbSortColumn = new SmallComboBox(SortColumn.values());
    setComponentFont(cbSortColumn);
    cbSortColumn.setAction(actionSort);
    panel.add(cbSortColumn, "4, 15, fill, default");

    cbSortOrder = new SmallComboBox(SortOrder.values());
    setComponentFont(cbSortOrder);
    cbSortOrder.setAction(actionSort);
    panel.add(cbSortOrder, "6, 15, fill, default");

    add(panel);
    setCollapsed(true);

    PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof GameList && "tag".equals(evt.getPropertyName())) {
          buildAndInstallTagsArray();
        }
        if (evt.getSource() instanceof GameList && "videoCodec".equals(evt.getPropertyName())) {
          buildAndInstallCodecArray();
        }
        if (evt.getSource() instanceof GameList && "audioCodec".equals(evt.getPropertyName())) {
          buildAndInstallCodecArray();
        }
      }
    };
    gameList.addPropertyChangeListener(propertyChangeListener);
    buildAndInstallTagsArray();
    buildAndInstallCodecArray();
  }

  private void buildAndInstallTagsArray() {
    cbTag.removeAllItems();
    List<String> tags = new ArrayList<String>(gameList.getTagsInGames());
    Collections.sort(tags);
    for (String tag : tags) {
      cbTag.addItem(tag);
    }
  }

  private void buildAndInstallCodecArray() {
    cbVideoCodec.removeAllItems();
    List<String> codecs = new ArrayList<String>(gameList.getVideoCodecsInGames());
    Collections.sort(codecs);
    for (String codec : codecs) {
      cbVideoCodec.addItem(codec);
    }

    cbAudioCodec.removeAllItems();
    codecs = new ArrayList<String>(gameList.getAudioCodecsInGames());
    Collections.sort(codecs);
    for (String codec : codecs) {
      cbAudioCodec.addItem(codec);
    }
  }

  private class SortAction extends AbstractAction {
    private static final long serialVersionUID = -4057379119252539003L;

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      SortColumn column = (SortColumn) cbSortColumn.getSelectedItem();
      SortOrder order = (SortOrder) cbSortOrder.getSelectedItem();
      boolean ascending = order == SortOrder.ASCENDING ? true : false;

      // sort
      gameSelectionModel.sortGames(column, ascending);
    }
  }

  private class FilterAction extends AbstractAction {
    private static final long serialVersionUID = 7488733475791640009L;

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      HashMap<SearchOptions, Object> searchOptions = new HashMap<SearchOptions, Object>();

      // filter duplicates
      if (cbFilterDuplicates.isSelected()) {
        gameList.searchDuplicates();
        searchOptions.put(SearchOptions.DUPLICATES, null);
      }

      // filter for isFavorite flag
      if (cbFilterIsFavorite.isSelected()) {
        if (cbIsFavorite.getSelectedItem() == IsFavoriteFlag.WATCHED) {
          searchOptions.put(SearchOptions.WATCHED, true);
        }
        else {
          searchOptions.put(SearchOptions.WATCHED, false);
        }
      }

      // filter by genre
      if (cbFilterGenre.isSelected()) {
        GameMediaGenres genre = (GameMediaGenres) cbGenre.getSelectedItem();
        if (genre != null) {
          searchOptions.put(SearchOptions.GENRE, genre);
        }
      }

      // filter by cast
      if (cbFilterCast.isSelected()) {
        if (StringUtils.isNotBlank(tfCastMember.getText())) {
          searchOptions.put(SearchOptions.CAST, tfCastMember.getText());
        }
      }

      // filter by tag
      if (cbFilterTag.isSelected()) {
        String tag = (String) cbTag.getSelectedItem();
        if (StringUtils.isNotBlank(tag)) {
          searchOptions.put(SearchOptions.TAG, tag);
        }
      }

      // filter by game in gameset
      if (cbFilterGameset.isSelected()) {
        if (cbGameset.getSelectedItem() == GameInGameSet.IN_MOVIESET) {
          searchOptions.put(SearchOptions.MOVIESET, true);
        }
        else {
          searchOptions.put(SearchOptions.MOVIESET, false);
        }
      }

      // filter by platform
      if (cbFilterPlatform.isSelected()) {
        String platform = (String) cbPlatform.getSelectedItem();
        searchOptions.put(SearchOptions.PLATFORM, platform);
      }

      // filter by video codec
      if (cbFilterVideoCodec.isSelected()) {
        String videoCodec = (String) cbVideoCodec.getSelectedItem();
        if (StringUtils.isNotBlank(videoCodec)) {
          searchOptions.put(SearchOptions.VIDEO_CODEC, videoCodec);
        }
      }

      // filter by audio codec
      if (cbFilterAudioCodec.isSelected()) {
        String audioCodec = (String) cbAudioCodec.getSelectedItem();
        if (StringUtils.isNotBlank(audioCodec)) {
          searchOptions.put(SearchOptions.AUDIO_CODEC, audioCodec);
        }
      }

      // apply the filter
      gameSelectionModel.filterGames(searchOptions);
    }
  }

  private void setComponentFont(JComponent comp) {
    comp.setFont(comp.getFont().deriveFont(FONT_SIZE));
  }
}
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

import static org.tinymediamanager.core.Constants.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.Message.MessageLevel;
import org.tinymediamanager.core.MessageManager;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.ui.TmmUIHelper;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.LinkLabel;
import org.tinymediamanager.ui.components.MediaFilesPanel;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class GameMediaInformationPanel.
 * 
 * @author Manuel Laggner
 */
public class GameMediaFilesPanel extends JPanel {
  private static final long           serialVersionUID = 3181909355114738346L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$
  private final static Logger         LOGGER           = LoggerFactory.getLogger(GameMediaFilesPanel.class);

  private GameSelectionModel          gameSelectionModel;

  private JLabel                      lblFilesT;
  private LinkLabel                   lblGamePath;
  private JLabel                      lblDateAddedT;
  private JLabel                      lblDateAdded;
  private JLabel                      lblGamePathT;
  // private JButton btnPlay;

  /** The media file event list. */
  private EventList<MediaFile>        mediaFileEventList;
  private MediaFilesPanel             panelMediaFiles;

  /**
   * Instantiates a new game media information panel.
   * 
   * @param model
   *          the model
   */
  public GameMediaFilesPanel(GameSelectionModel model) {
    this.gameSelectionModel = model;
    mediaFileEventList = new ObservableElementList<MediaFile>(GlazedLists.threadSafeList(new BasicEventList<MediaFile>()),
        GlazedLists.beanConnector(MediaFile.class));

    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
        ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
        FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.NARROW_LINE_GAP_ROWSPEC,
        RowSpec.decode("default:grow"), FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

    lblDateAddedT = new JLabel(BUNDLE.getString("metatag.dateadded")); //$NON-NLS-1$
    add(lblDateAddedT, "2, 2");

    lblDateAdded = new JLabel("");
    add(lblDateAdded, "4, 2");

    lblGamePathT = new JLabel(BUNDLE.getString("metatag.path")); //$NON-NLS-1$
    add(lblGamePathT, "2, 4");

    lblGamePath = new LinkLabel("");
    lblGamePath.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (!StringUtils.isEmpty(lblGamePath.getNormalText())) {
          File path = new File(lblGamePath.getNormalText());
          try {
            // get the location from the label
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
      }
    });
    lblGamePathT.setLabelFor(lblGamePath);
    lblGamePathT.setLabelFor(lblGamePath);
    add(lblGamePath, "4, 4");

    lblFilesT = new JLabel(BUNDLE.getString("metatag.files")); //$NON-NLS-1$
    add(lblFilesT, "2, 6, default, top");

    panelMediaFiles = new MediaFilesPanel(mediaFileEventList);
    add(panelMediaFiles, "4, 6, 1, 1, fill, fill");

    initDataBindings();

    // install the propertychangelistener
    PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String property = propertyChangeEvent.getPropertyName();
        Object source = propertyChangeEvent.getSource();
        // react on selection of a game and change of media files
        if ((source.getClass() == GameSelectionModel.class && "selectedGame".equals(property))
            || (source.getClass() == Game.class && MEDIA_FILES.equals(property))) {
          // this does sometimes not work. simply wrap it
          try {
            mediaFileEventList.getReadWriteLock().writeLock().lock();
            mediaFileEventList.clear();
            mediaFileEventList.addAll(gameSelectionModel.getSelectedGame().getMediaFiles());
          }
          catch (Exception e) {
          }
          finally {
            mediaFileEventList.getReadWriteLock().writeLock().unlock();
          }
          try {
            panelMediaFiles.adjustColumns();
          }
          catch (Exception e) {
          }
        }
      }
    };

    gameSelectionModel.addPropertyChangeListener(propertyChangeListener);
  }

  protected void initDataBindings() {
    BeanProperty<GameSelectionModel, Integer> gameSelectionModelBeanProperty = BeanProperty.create("selectedGame.dateAdded.date");
    BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
    AutoBinding<GameSelectionModel, Integer, JLabel, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty, lblDateAdded, jLabelBeanProperty);
    autoBinding.bind();
    //
    BeanProperty<GameSelectionModel, Integer> gameSelectionModelBeanProperty_2 = BeanProperty.create("selectedGame.dateAdded.day");
    AutoBinding<GameSelectionModel, Integer, JLabel, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_2, lblDateAdded, jLabelBeanProperty);
    autoBinding_2.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_3 = BeanProperty.create("selectedGame.dateAddedAsString");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_3, lblDateAdded, jLabelBeanProperty);
    autoBinding_3.bind();
    //
    BeanProperty<GameSelectionModel, String> gameSelectionModelBeanProperty_13 = BeanProperty.create("selectedGame.path");
    AutoBinding<GameSelectionModel, String, JLabel, String> autoBinding_19 = Bindings.createAutoBinding(UpdateStrategy.READ, gameSelectionModel,
        gameSelectionModelBeanProperty_13, lblGamePath, jLabelBeanProperty);
    autoBinding_19.bind();
  }
}

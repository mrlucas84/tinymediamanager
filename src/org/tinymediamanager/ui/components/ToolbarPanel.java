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

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
  private static final long serialVersionUID = 7969400170662870244L;
  private JTextField        textField;

  public ToolbarPanel() {
    putClientProperty("class", "toolbarPanel");
    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), ColumnSpec.decode("default:grow"), ColumnSpec.decode("10dlu"),
        FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("10dlu"), FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
        FormFactory.UNRELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.UNRELATED_GAP_ROWSPEC, }));

    JLabel btnNewButton = new JLabel("");
    btnNewButton.setOpaque(false);
    btnNewButton.setIcon(new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Search_Scrape_Icon.png")));
    add(btnNewButton, "2, 2, center, default");

    JLabel btnNewButton_1 = new JLabel("");
    btnNewButton_1.setIcon(new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Edit_Icon.png")));
    add(btnNewButton_1, "4, 2, center, default");

    JLabel btnNewButton_2 = new JLabel("");
    btnNewButton_2.setIcon(new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Refresh_Database_Icon.png")));
    add(btnNewButton_2, "6, 2, center, default");

    JLabel btnNewButton_3 = new JLabel("");
    btnNewButton_3.setIcon(new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Export_Icon.png")));
    add(btnNewButton_3, "8, 2, center, default");

    textField = new JTextField();
    add(textField, "10, 2, fill, default");
    textField.setColumns(10);

    JLabel btnNewButton_4 = new JLabel("");
    btnNewButton_4.setIcon(new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Tools_Icon.png")));
    add(btnNewButton_4, "12, 2, center, default");

    JLabel btnNewButton_5 = new JLabel("");
    btnNewButton_5.setIcon(new ImageIcon(ToolbarPanel.class.getResource("/org/tinymediamanager/ui/images/Settings_Icon.png")));
    add(btnNewButton_5, "14, 2, center, default");

    JLabel lblNewLabel = new JLabel("Search & Scrape");
    lblNewLabel.setForeground(Color.LIGHT_GRAY);
    add(lblNewLabel, "2, 3, center, default");

    JLabel lblEdit = new JLabel("Edit");
    lblEdit.setForeground(Color.LIGHT_GRAY);
    add(lblEdit, "4, 3, center, default");

    JLabel lblRefreshDatabase = new JLabel("Refresh database");
    lblRefreshDatabase.setForeground(Color.LIGHT_GRAY);
    add(lblRefreshDatabase, "6, 3, center, default");

    JLabel lblExport = new JLabel("Export");
    lblExport.setForeground(Color.LIGHT_GRAY);
    add(lblExport, "8, 3, center, default");

    JLabel lblTools = new JLabel("Tools");
    lblTools.setForeground(Color.LIGHT_GRAY);
    add(lblTools, "12, 3, center, default");

    JLabel lblSettings = new JLabel("Settings");
    lblSettings.setForeground(Color.LIGHT_GRAY);
    add(lblSettings, "14, 3, center, default");
  }
}

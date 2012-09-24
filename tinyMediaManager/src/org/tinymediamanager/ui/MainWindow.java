package org.tinymediamanager.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MainWindow {

  private JFrame frame;

  /**
   * Create the application.
   */
  public MainWindow() {
    initialize();
    frame.setVisible(true);
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 1091, 720);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(
        new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] {
            FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("fill:default:grow"), }));

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setTabPlacement(JTabbedPane.LEFT);
    frame.getContentPane().add(tabbedPane, "2, 2, fill, fill");

    JPanel panelMovies = new MoviePanel();// new JPanel();
    tabbedPane.addTab("Movies", null, panelMovies, null);

    JPanel panelSettings = new SettingsPanel();// JPanel();
    tabbedPane.addTab("Settings", null, panelSettings, null);
  }

}

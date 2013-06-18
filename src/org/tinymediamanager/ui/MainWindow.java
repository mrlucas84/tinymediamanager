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
package org.tinymediamanager.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.ui.components.MainTabbedPane;
import org.tinymediamanager.ui.components.ToolbarPanel;
import org.tinymediamanager.ui.movies.MoviePanel;
import org.tinymediamanager.ui.movies.MovieUIModule;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class MainWindow.
 * 
 * @author Manuel Laggner
 */
public class MainWindow extends JFrame {

  private static final long           serialVersionUID = -6342448589302283399L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$
  private final static Logger         LOGGER           = LoggerFactory.getLogger(MainWindow.class);
  private static MainWindow           instance;

  private JPanel                      panelMovies;
  private JPanel                      panelStatusBar;
  private JLabel                      lblLoadingImg;
  private JLabel                      lblProgressAction;
  private JProgressBar                progressBar;
  private JButton                     btnCancelTask;
  private JPanel                      rootPanel;
  private JTabbedPane                 tabbedPane;
  private JPanel                      detailPanel;
  private ToolbarPanel                toolbarPanel;

  private TmmSwingWorker              activeTask;
  private StatusbarThread             statusTask       = new StatusbarThread();

  /**
   * Create the application.
   * 
   * @param name
   *          the name
   */
  public MainWindow(String name) {
    super(name);
    setName("mainWindow");
    setMinimumSize(new Dimension(1100, 700));

    instance = this;

    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    initialize();

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
          try {
            File path = new File(".");
            // check whether this location exists
            if (path.exists()) {
              Desktop.getDesktop().open(path);
            }
          }
          catch (Exception ex) {
            LOGGER.warn(ex.getMessage());
          }
        }
        System.exit(0);
      }
    });

    JMenu cache = new JMenu(BUNDLE.getString("tmm.cache")); //$NON-NLS-1$
    debug.add(cache);

    JMenuItem tmmFolder = new JMenuItem(BUNDLE.getString("tmm.gotoinstalldir")); //$NON-NLS-1$
    debug.add(tmmFolder);
    tmmFolder.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        try {
          // get the location from the label
          File path = new File(System.getProperty("user.dir"));
          // check whether this location exists
          if (path.exists()) {
            Desktop.getDesktop().open(path);
          }
        }
        catch (Exception ex) {
          LOGGER.error("open filemanager", ex);
        }
      }
    });

    menuBar.add(debug);

    // Globals.executor.execute(new MyStatusbarThread());
    // use a Future to be able to cancel it
    statusTask.execute();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    // set the logo
    setIconImage(Globals.logo);
    setBounds(5, 5, 1100, 727);
    // do nothing, we have our own windowClosing() listener
    // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    toolbarPanel = new ToolbarPanel();
    getContentPane().add(toolbarPanel, BorderLayout.NORTH);

    rootPanel = new JPanel();
    rootPanel.putClientProperty("class", "rootPanel");
    getContentPane().add(rootPanel);

    rootPanel.setLayout(new FormLayout(
        new ColumnSpec[] { ColumnSpec.decode("15dlu"), ColumnSpec.decode("default:grow"), ColumnSpec.decode("15dlu"), }, new RowSpec[] {
            RowSpec.decode("10dlu"), RowSpec.decode("fill:max(500px;default):grow"), RowSpec.decode("10dlu"), FormFactory.DEFAULT_ROWSPEC, }));

    JSplitPane splitPane = new JSplitPane();
    splitPane.setContinuousLayout(true);
    splitPane.setOpaque(false);
    splitPane.putClientProperty("flatMode", true);
    rootPanel.add(splitPane, "2, 2, fill, fill");

    JPanel leftPanel = new JPanel();
    leftPanel.setOpaque(false);
    leftPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, },
        new RowSpec[] { RowSpec.decode("fill:default:grow"), }));
    tabbedPane = new MainTabbedPane();
    leftPanel.add(tabbedPane, "1, 1, fill, fill");
    splitPane.setLeftComponent(leftPanel);

    JPanel rightPanel = new JPanel();
    rightPanel.setOpaque(false);
    rightPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] {
        RowSpec.decode("10dlu"), FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("fill:default:grow"), }));
    detailPanel = new JPanel();
    detailPanel.setLayout(new CardLayout(0, 0));
    rightPanel.add(detailPanel, "2, 3, fill, fill");
    splitPane.setRightComponent(rightPanel);

    // JTabbedPane tabbedPane = VerticalTextIcon.createTabbedPane(JTabbedPane.LEFT);
    // UIManager.put("TabbedPane.contentOpaque", false);
    // tabbedPane.setTabPlacement(JTabbedPane.LEFT);
    // rootPanel.add(tabbedPane, "1, 2, fill, fill");
    //
    buildStatusbar();
    //
    // panelMovies = new MoviePanel();
    //    VerticalTextIcon.addTab(tabbedPane, BUNDLE.getString("tmm.movies"), panelMovies); //$NON-NLS-1$
    //
    // JPanel panelMovieSets = new MovieSetPanel();
    //    VerticalTextIcon.addTab(tabbedPane, BUNDLE.getString("tmm.moviesets"), panelMovieSets); //$NON-NLS-1$
    //
    // JPanel panelTvShows = new TvShowPanel();
    //    VerticalTextIcon.addTab(tabbedPane, BUNDLE.getString("tmm.tvshows"), panelTvShows); //$NON-NLS-1$
    //
    // JPanel panelSettings = new SettingsPanel();
    //    VerticalTextIcon.addTab(tabbedPane, BUNDLE.getString("tmm.settings"), panelSettings); //$NON-NLS-1$

    // shutdown listener - to clean database connections safely
    createShutdownListener();

    addModule(MovieUIModule.getInstance());

    // FIXME move to a dynamic place
    toolbarPanel.setSearchAction(MovieUIModule.getInstance().getSearchAction());
    toolbarPanel.setSearchPopupMenu(MovieUIModule.getInstance().getSearchMenu());
    toolbarPanel.setEditAction(MovieUIModule.getInstance().getEditAction());

    // FIXME
    tabbedPane.addTab("TV SHOWS", new JPanel());

  }

  private void buildStatusbar() {
    panelStatusBar = new JPanel();
    rootPanel.add(panelStatusBar, "2, 4");
    panelStatusBar.setLayout(new FormLayout(
        new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.DEFAULT_COLSPEC,
            FormFactory.LABEL_COMPONENT_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            FormFactory.DEFAULT_COLSPEC, FormFactory.LABEL_COMPONENT_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
            FormFactory.LABEL_COMPONENT_GAP_COLSPEC, }, new RowSpec[] { RowSpec.decode("20px"), }));

    lblProgressAction = new JLabel("");
    panelStatusBar.add(lblProgressAction, "3, 1, default, default");

    progressBar = new JProgressBar();
    panelStatusBar.add(progressBar, "5, 1");

    btnCancelTask = new JButton("");
    panelStatusBar.add(btnCancelTask, "7, 1");
    btnCancelTask.setVisible(false);
    btnCancelTask.setContentAreaFilled(false);
    btnCancelTask.setBorderPainted(false);
    btnCancelTask.setBorder(null);
    btnCancelTask.setMargin(new Insets(0, 0, 0, 0));
    btnCancelTask.setIcon(new ImageIcon(MoviePanel.class.getResource("/org/tinymediamanager/ui/images/Button_Stop.png")));
    btnCancelTask.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (activeTask != null && !activeTask.isDone()) {
          activeTask.cancel();
        }
      }
    });
    progressBar.setVisible(false);

    lblLoadingImg = new JLabel("");
    panelStatusBar.add(lblLoadingImg, "9, 1");
  }

  private void createShutdownListener() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        closeTmm();
      }
    });
  }

  private void closeTmm() {
    int confirm = 0;
    // if there are some threads running, display exit confirmation
    if (Globals.poolRunning()) {
      confirm = JOptionPane.showOptionDialog(null, BUNDLE.getString("tmm.exit.runningtasks"), BUNDLE.getString("tmm.exit.confirmation"),
          JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null); //$NON-NLS-1$
    }
    if (confirm == JOptionPane.YES_OPTION) {
      LOGGER.info("bye bye");
      try {
        // send shutdown signal
        Globals.executor.shutdown();
        // cancel our status task (send interrupt())
        statusTask.cancel(true);
        // save unsaved settings
        Globals.settings.saveSettings();
        // close database connection
        Globals.shutdownDatabase();
        // clear cache directory
        if (Globals.settings.isClearCacheShutdown()) {
          File cache = new File("cache");
          if (cache.exists()) {
            FileUtils.deleteDirectory(cache);
          }
        }
      }
      catch (Exception ex) {
        LOGGER.warn(ex.getMessage());
      }
      dispose();
      try {
        // wait a bit for threads to finish (if any)
        Globals.executor.awaitTermination(2, TimeUnit.SECONDS);
        // hard kill
        Globals.executor.shutdownNow();
      }
      catch (InterruptedException e1) {
        LOGGER.debug("Global thread shutdown");
      }
      System.exit(0); // calling the method is a must
    }
  }

  private void addModule(ITmmUIModule module) {
    tabbedPane.addTab(module.getTabTitle(), module.getTabPanel());
    detailPanel.add(module.getDetailPanel(), module.getModuleId());
  }

  /**
   * Gets the active instance.
   * 
   * @return the active instance
   */
  public static MainWindow getActiveInstance() {
    return instance;
  }

  // status bar thread
  /**
   * The Class StatusbarThread.
   * 
   * @author Manuel Laggner
   */
  private class StatusbarThread extends SwingWorker<Void, Void> {

    /** The loading. */
    private final ImageIcon    loading;

    /** The ex. */
    private ThreadPoolExecutor ex = Globals.executor;

    /**
     * Instantiates a new statusbar thread.
     */
    public StatusbarThread() {
      loading = new ImageIcon(MainWindow.class.getResource("/org/tinymediamanager/ui/images/loading.gif"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    public Void doInBackground() {
      Thread.currentThread().setName("statusBar thread");
      try {
        while (!Thread.interrupted()) {
          if (Globals.poolRunning() || (activeTask != null && !activeTask.isDone())) {
            if (lblLoadingImg.getIcon() != loading) {
              lblLoadingImg.setIcon(loading);
            }
          }
          else {
            if (lblLoadingImg.getIcon() == loading) {
              lblLoadingImg.setIcon(null);
            }
          }
          String text = String.format(
              "<html><body>" + BUNDLE.getString("status.activethreads") + " [%d/%d]<br>" + BUNDLE.getString("status.queuesize")
                  + " %d </body></html>", this.ex.getActiveCount(), this.ex.getMaximumPoolSize(), this.ex.getQueue().size()); //$NON-NLS-1$
          // LOGGER.debug(text);
          lblLoadingImg.setToolTipText(text);
          Thread.sleep(2000);
        }
      }
      catch (InterruptedException e) {
        // called on cancel(), so don't log it
        // LOGGER.debug("statusBar thread shutdown");
      }
      return null;
    }
  }

  /**
   * Gets the movie panel.
   * 
   * @return the movie panel
   */
  public MoviePanel getMoviePanel() {
    return (MoviePanel) panelMovies;
  }

  /**
   * Executes a "main" task. A "main" task is a task which can't be parallelized
   * 
   * @param task
   *          the task
   * @return true, if successful
   */
  public static boolean executeMainTask(TmmSwingWorker task) {
    if (instance == null) {
      return false;
    }
    if (instance.activeTask == null || instance.activeTask.isDone()) {
      instance.activeTask = task;
      instance.activeTask.setUIElements(instance.lblProgressAction, instance.progressBar, instance.btnCancelTask);
      instance.activeTask.execute();
      return true;
    }

    return false;
  }

  /**
   * Gets the frame.
   * 
   * @return the frame
   */
  public static JFrame getFrame() {
    return instance;
  }
}

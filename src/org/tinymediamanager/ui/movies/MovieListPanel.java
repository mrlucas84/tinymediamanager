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
package org.tinymediamanager.ui.movies;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.gpl.JSplitButton.JSplitButton;
import org.gpl.JSplitButton.action.SplitButtonActionListener;
import org.tinymediamanager.core.movie.Movie;
import org.tinymediamanager.core.movie.MovieList;
import org.tinymediamanager.ui.BorderCellRenderer;
import org.tinymediamanager.ui.IconRenderer;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.components.JSearchTextField;
import org.tinymediamanager.ui.components.ZebraJTable;
import org.tinymediamanager.ui.movies.actions.MovieEditAction;
import org.tinymediamanager.ui.movies.actions.MovieMediaInformationAction;
import org.tinymediamanager.ui.movies.actions.MovieRenameAction;
import org.tinymediamanager.ui.movies.actions.MovieSingleScrapeAction;
import org.tinymediamanager.ui.movies.actions.MovieUpdateDatasourceAction;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * @author Manuel Laggner
 * 
 */
public class MovieListPanel extends JPanel {

  private static final long serialVersionUID   = -1681460428331929420L;

  MovieSelectionModel       selectionModel;

  private JTextField        searchField;
  private JTable            movieTable;
  private JLabel            lblMovieCountFiltered;

  private Action            actionSingleScrape = new MovieSingleScrapeAction(false);

  public MovieListPanel() {
    putClientProperty("class", "roundedPanel");

    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("200px:grow"), ColumnSpec.decode("150px:grow"), },
        new RowSpec[] { RowSpec.decode("26px"), FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("fill:max(200px;default):grow"),
            FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    buildToolbar();
    buildTable();
    buildSearchPanel();
    buildStatusPanel();
  }

  private void buildToolbar() {
    JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);
    toolBar.setFloatable(false);
    toolBar.setOpaque(false);
    add(toolBar, "2, 1, left, fill");

    toolBar.add(new MovieUpdateDatasourceAction(false));
    JSplitButton buttonScrape = new JSplitButton(new ImageIcon(getClass().getResource("/org/tinymediamanager/ui/images/Search.png")));
    // temp fix for size of the button
    buttonScrape.setText("   ");
    buttonScrape.setHorizontalAlignment(JButton.LEFT);
    // buttonScrape.setMargin(new Insets(2, 2, 2, 24));
    buttonScrape.setSplitWidth(18);

    // register for listener
    buttonScrape.addSplitButtonActionListener(new SplitButtonActionListener() {
      public void buttonClicked(ActionEvent e) {
        actionSingleScrape.actionPerformed(e);
      }

      public void splitButtonClicked(ActionEvent e) {
      }
    });

    // JPopupMenu popup = new JPopupMenu("popup");
    // JMenuItem item = new JMenuItem(actionScrape2);
    // popup.add(item);
    // item = new JMenuItem(actionScrapeUnscraped);
    // popup.add(item);
    // item = new JMenuItem(actionScrapeSelected);
    // popup.add(item);
    // buttonScrape.setPopupMenu(popup);
    toolBar.add(buttonScrape);

    toolBar.add(new MovieEditAction(false));
    toolBar.add(new MovieRenameAction(false));
    toolBar.add(new MovieMediaInformationAction(false));

    // textField = new JTextField();
    searchField = new JSearchTextField();
    add(searchField, "3, 1, right, bottom");
    searchField.setColumns(10);
  }

  private void buildTable() {
    // build the list (wrap it with all necessary glazedlists types), build the tablemodel and the selectionmodel
    MovieList movieList = MovieList.getInstance();
    SortedList<Movie> sortedMovies = new SortedList<Movie>(GlazedListsSwing.swingThreadProxyList(movieList.getMovies()), new MovieComparator());
    sortedMovies.setMode(SortedList.AVOID_MOVING_ELEMENTS);

    MatcherEditor<Movie> textMatcherEditor = new TextComponentMatcherEditor<Movie>(searchField, new MovieFilterator());
    MovieMatcherEditor movieMatcherEditor = new MovieMatcherEditor();
    FilterList<Movie> extendedFilteredMovies = new FilterList<Movie>(sortedMovies, movieMatcherEditor);
    FilterList<Movie> textFilteredMovies = new FilterList<Movie>(extendedFilteredMovies, textMatcherEditor);
    selectionModel = new MovieSelectionModel(sortedMovies, textFilteredMovies, movieMatcherEditor);
    final DefaultEventTableModel<Movie> movieTableModel = new DefaultEventTableModel<Movie>(textFilteredMovies, new MovieTableFormat());

    // build the table
    movieTable = new ZebraJTable(movieTableModel);

    movieTableModel.addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent arg0) {
        lblMovieCountFiltered.setText(String.valueOf(movieTableModel.getRowCount()));
        // select first movie if nothing is selected
        ListSelectionModel selectionModel = movieTable.getSelectionModel();
        if (selectionModel.isSelectionEmpty() && movieTableModel.getRowCount() > 0) {
          selectionModel.setSelectionInterval(0, 0);
        }
      }
    });

    // install and save the comparator on the Table
    selectionModel.setTableComparatorChooser(TableComparatorChooser.install(movieTable, sortedMovies, TableComparatorChooser.SINGLE_COLUMN));

    // moviename column
    movieTable.getColumnModel().getColumn(0).setCellRenderer(new BorderCellRenderer());

    // year column
    movieTable.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(35);
    movieTable.getTableHeader().getColumnModel().getColumn(1).setMinWidth(35);
    movieTable.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(50);

    // NFO column
    movieTable.getTableHeader().getColumnModel().getColumn(2).setHeaderRenderer(new IconRenderer("NFO"));
    movieTable.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(20);
    URL imageURL = MainWindow.class.getResource("images/File.png");
    if (imageURL != null) {
      movieTable.getColumnModel().getColumn(2).setHeaderValue(new ImageIcon(imageURL));
    }

    // Images column
    movieTable.getTableHeader().getColumnModel().getColumn(3).setHeaderRenderer(new IconRenderer("Images"));
    movieTable.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(20);
    imageURL = null;
    imageURL = MainWindow.class.getResource("images/Image.png");
    if (imageURL != null) {
      movieTable.getColumnModel().getColumn(3).setHeaderValue(new ImageIcon(imageURL));
    }

    // trailer column
    movieTable.getTableHeader().getColumnModel().getColumn(4).setHeaderRenderer(new IconRenderer("Trailer"));
    movieTable.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(20);
    imageURL = null;
    imageURL = MainWindow.class.getResource("images/ClapBoard.png");
    if (imageURL != null) {
      movieTable.getColumnModel().getColumn(4).setHeaderValue(new ImageIcon(imageURL));
    }

    // subtitles column
    movieTable.getTableHeader().getColumnModel().getColumn(5).setHeaderRenderer(new IconRenderer("Subtitles"));
    movieTable.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(20);
    imageURL = null;
    imageURL = MainWindow.class.getResource("images/subtitle.png");
    if (imageURL != null) {
      movieTable.getColumnModel().getColumn(5).setHeaderValue(new ImageIcon(imageURL));
    }

    movieTable.setSelectionModel(selectionModel.getSelectionModel());
    // selecting first movie at startup
    if (movieList.getMovies() != null && movieList.getMovies().size() > 0) {
      ListSelectionModel selectionModel = movieTable.getSelectionModel();
      if (selectionModel.isSelectionEmpty()) {
        selectionModel.setSelectionInterval(0, 0);
      }
    }

    JScrollPane scrollPane = ZebraJTable.createStripedJScrollPane(movieTable);
    add(scrollPane, "2, 3, 2, 1, fill, fill");
  }

  private void buildSearchPanel() {
    JPanel panelExtendedSearch = new MovieExtendedSearchPanel(selectionModel);
    add(panelExtendedSearch, "2, 5, 2, 1, fill, fill");
  }

  private void buildStatusPanel() {
    JPanel panelStatus = new JPanel();
    add(panelStatus, "2, 6, 2, 1");
    panelStatus.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("1px"),
        ColumnSpec.decode("146px:grow"), FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), }, new RowSpec[] { RowSpec
        .decode("fill:default:grow"), }));

    JPanel panelMovieCount = new JPanel();
    panelStatus.add(panelMovieCount, "3, 1, left, fill");

    JLabel lblMovieCount = new JLabel("Movies:");
    panelMovieCount.add(lblMovieCount);

    lblMovieCountFiltered = new JLabel("");
    panelMovieCount.add(lblMovieCountFiltered);

    JLabel lblMovieCountOf = new JLabel("of");
    panelMovieCount.add(lblMovieCountOf);

    JLabel lblMovieCountTotal = new JLabel("");
    panelMovieCount.add(lblMovieCountTotal);
  }
}

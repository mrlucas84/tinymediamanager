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

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.tinymediamanager.core.movie.Movie;
import org.tinymediamanager.core.movie.MovieList;
import org.tinymediamanager.ui.BorderCellRenderer;
import org.tinymediamanager.ui.IconHeaderRenderer;
import org.tinymediamanager.ui.MainWindow;
import org.tinymediamanager.ui.components.TmmTable;

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

  private static final long serialVersionUID = -1681460428331929420L;

  MovieSelectionModel       selectionModel;

  private JTextField        searchField;
  private JTable            movieTable;
  private JLabel            lblMovieCountFiltered;

  public MovieListPanel() {
    putClientProperty("class", "roundedPanel");

    setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("400px:grow") }, new RowSpec[] { RowSpec.decode("15dlu"),
        RowSpec.decode("fill:max(400px;default):grow"), FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        RowSpec.decode("15dlu"), }));

    buildTable();
    buildSearchPanel();
    buildStatusPanel();
  }

  private void buildTable() {
    // build the list (wrap it with all necessary glazedlists types), build the tablemodel and the selectionmodel
    MovieList movieList = MovieList.getInstance();
    SortedList<Movie> sortedMovies = new SortedList<Movie>(GlazedListsSwing.swingThreadProxyList(movieList.getMovies()), new MovieComparator());
    sortedMovies.setMode(SortedList.AVOID_MOVING_ELEMENTS);

    // FIXME
    searchField = new JTextField();

    MatcherEditor<Movie> textMatcherEditor = new TextComponentMatcherEditor<Movie>(searchField, new MovieFilterator());
    MovieMatcherEditor movieMatcherEditor = new MovieMatcherEditor();
    FilterList<Movie> extendedFilteredMovies = new FilterList<Movie>(sortedMovies, movieMatcherEditor);
    FilterList<Movie> textFilteredMovies = new FilterList<Movie>(extendedFilteredMovies, textMatcherEditor);
    selectionModel = new MovieSelectionModel(sortedMovies, textFilteredMovies, movieMatcherEditor);
    final DefaultEventTableModel<Movie> movieTableModel = new DefaultEventTableModel<Movie>(textFilteredMovies, new MovieTableFormat());

    // build the table
    movieTable = new TmmTable(movieTableModel);

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
    movieTable.getTableHeader().getColumnModel().getColumn(1).setMinWidth(40);
    movieTable.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(50);

    // NFO column
    movieTable.getTableHeader().getColumnModel().getColumn(2).setHeaderRenderer(new IconHeaderRenderer("NFO"));
    movieTable.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(25);
    URL imageURL = MainWindow.class.getResource("/org/tinymediamanager/ui/images/NFO_Icon.png");
    if (imageURL != null) {
      movieTable.getColumnModel().getColumn(2).setHeaderValue(new ImageIcon(imageURL));
    }

    // Images column
    movieTable.getTableHeader().getColumnModel().getColumn(3).setHeaderRenderer(new IconHeaderRenderer("Images"));
    movieTable.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(25);
    imageURL = null;
    imageURL = MainWindow.class.getResource("/org/tinymediamanager/ui/images/Images_Icon.png");
    if (imageURL != null) {
      movieTable.getColumnModel().getColumn(3).setHeaderValue(new ImageIcon(imageURL));
    }

    // trailer column
    movieTable.getTableHeader().getColumnModel().getColumn(4).setHeaderRenderer(new IconHeaderRenderer("Trailer"));
    movieTable.getTableHeader().getColumnModel().getColumn(4).setMaxWidth(25);
    imageURL = null;
    imageURL = MainWindow.class.getResource("/org/tinymediamanager/ui/images/Trailer_Icon.png");
    if (imageURL != null) {
      movieTable.getColumnModel().getColumn(4).setHeaderValue(new ImageIcon(imageURL));
    }

    // subtitles column
    movieTable.getTableHeader().getColumnModel().getColumn(5).setHeaderRenderer(new IconHeaderRenderer("Subtitles"));
    movieTable.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(25);
    imageURL = null;
    imageURL = MainWindow.class.getResource("/org/tinymediamanager/ui/images/Subs_Icon.png");
    if (imageURL != null) {
      movieTable.getColumnModel().getColumn(5).setHeaderValue(new ImageIcon(imageURL));
    }

    int[] cols = { 0 };
    JScrollPane scrollPane = TmmTable.createJScrollPane(movieTable, cols);
    add(scrollPane, "1, 2, 1, 1, fill, fill");
  }

  private void buildSearchPanel() {
    JPanel panelExtendedSearch = new MovieExtendedSearchPanel(selectionModel);
    add(panelExtendedSearch, "1, 4, fill, fill");
  }

  private void buildStatusPanel() {
    JPanel panelStatus = new JPanel();
    add(panelStatus, "1, 5");
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

  void setInitialSelection() {
    movieTable.setSelectionModel(selectionModel.getSelectionModel());
    // selecting first movie at startup
    if (MovieList.getInstance().getMovies() != null && MovieList.getInstance().getMovies().size() > 0) {
      ListSelectionModel selectionModel = movieTable.getSelectionModel();
      if (selectionModel.isSelectionEmpty()) {
        selectionModel.setSelectionInterval(0, 0);
      }
    }
  }
}

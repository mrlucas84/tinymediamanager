package org.tinymediamanager.scraper.traktv;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.TmmModuleManager;
import org.tinymediamanager.core.movie.MovieModuleManager;
import org.tinymediamanager.core.tvshow.TvShowModuleManager;
import org.tinymediamanager.scraper.trakttv.TraktTv;

import com.jakewharton.trakt.entities.Genre;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Extended;

public class TraktTvTest {

  private static final TraktTv t = new TraktTv();

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    TmmModuleManager.getInstance().startUp();
    MovieModuleManager.getInstance().startUp();
    TvShowModuleManager.getInstance().startUp();
    // t.getManager().setIsDebug(true); // http debug
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    TmmModuleManager.getInstance().shutDown();
    MovieModuleManager.getInstance().shutDown();
    TvShowModuleManager.getInstance().shutDown();
  }

  @Test
  public void syncTraktMovieCollection() {
    t.syncTraktMovieCollection();
  }

  @Test
  public void syncTraktMovieWatched() {
    t.syncTraktMovieWatched();
  }

  @Test
  public void syncTraktTvShowCollection() {
    t.syncTraktTvShowCollection();
  }

  @Test
  public void syncTraktTvShowWatched() {
    t.syncTraktTvShowWatched();
  }

  @Test
  public void getTvLib() {
    List<TvShow> shows = t.getManager().userService().libraryShowsWatched(Globals.settings.getTraktUsername(), Extended.MIN);
    System.out.println(shows.size());
  }

  @Test
  public void getGenres() {
    List<Genre> mg = t.getManager().genreService().movies();
    mg.addAll(t.getManager().genreService().shows());
    for (Genre genre : mg) {
      System.out.println(genre.name);
    }
  }

}

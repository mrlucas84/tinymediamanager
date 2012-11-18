package org.tinymediamanager.scraper.imdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.tinymediamanager.Globals;
import org.tinymediamanager.scraper.CastMember;
import org.tinymediamanager.scraper.Certification;
import org.tinymediamanager.scraper.CountryCode;
import org.tinymediamanager.scraper.MediaArt;
import org.tinymediamanager.scraper.MediaGenres;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaSearchResult;
import org.tinymediamanager.scraper.MediaType;
import org.tinymediamanager.scraper.SearchQuery;

public class ImdbMetadataProviderTest {

  @Test
  public void testSearch() {
    ImdbMetadataProvider mp = null;
    List<MediaSearchResult> results = null;

    /*
     * test on akas.imdb.com - "9"
     */
    results = null;
    try {
      mp = new ImdbMetadataProvider(ImdbSiteDefinition.IMDB_COM);
      results = mp.search(new SearchQuery(MediaType.MOVIE, SearchQuery.Field.QUERY, "9"));
    }
    catch (Exception e) {
    }

    // did we get a result?
    assertNotNull("Result", results);

    // result count
    assertEquals("Result count", 5, results.size());

    // check first result (9 - 2009 - tt0472033)
    MediaSearchResult result = results.get(0);
    checkSearchResult("9", "2009", "tt0472033", result);

    // check second result (9 - 2005 - tt0443424)
    result = results.get(1);
    checkSearchResult("9", "2005", "tt0443424", result);

    // check third result (9 - 2002 - tt0342012)
    result = results.get(2);
    checkSearchResult("9", "2002", "tt0342012", result);

    // check fourth result (9 - 2009 - tt1430606)
    result = results.get(3);
    checkSearchResult("9", "2009", "tt1430606", result);

    // check fifth result (9 - 1996 - tt0191312)
    result = results.get(4);
    checkSearchResult("9", "1996", "tt0191312", result);

    /*
     * test on akas.imdb.com - "Inglorious Basterds"
     */
    results = null;
    try {
      mp = new ImdbMetadataProvider(ImdbSiteDefinition.IMDB_COM);
      results = mp.search(new SearchQuery(MediaType.MOVIE, SearchQuery.Field.QUERY, "Inglorious Basterds"));
    }
    catch (Exception e) {
    }

    // did we get a result?
    assertNotNull("Result", results);

    // result count
    assertEquals("Result count", 10, results.size());

    // check first result (Inglourious Basterds - 2009 - tt0361748)
    result = results.get(0);
    checkSearchResult("Inglourious Basterds", "2009", "tt0361748", result);

    // check second result (Quel maledetto treno blindato - 1978 - tt0076584)
    result = results.get(1);
    checkSearchResult("Quel maledetto treno blindato", "1978", "tt0076584", result);

    // check third result (Eroi dell'inferno - 1987 - tt0342012)
    result = results.get(2);
    checkSearchResult("Eroi dell'inferno", "1987", "tt0422182", result);

    // check 4. result (Inglourious Basterds: Done in 60 seconds - 2010 -
    // tt1430606)
    result = results.get(3);
    checkSearchResult("Inglourious Basterds: Done in 60 seconds", "2010", "tt1837578", result);

    // check 5. result (Inglourious Basterds: Movie Special - 2009 -
    // tt1515156)
    result = results.get(4);
    checkSearchResult("Inglourious Basterds: Movie Special", "2009", "tt1515156", result);

    // check 6. result (Quelli del Maledetto Treno Blindato - Making of
    // 'Inglorious Bastards' - 2007 -
    // tt1028564)
    result = results.get(5);
    checkSearchResult("Quelli del Maledetto Treno Blindato - Making of 'Inglorious Bastards'", "2007", "tt1028564", result);

    // check 7. result (Merah Putih - 2009 - tt1438496)
    result = results.get(6);
    checkSearchResult("Merah Putih", "2009", "tt1438496", result);

    // check 8. result (Joshikyôei hanrangun - 2007 - tt1134826)
    result = results.get(7);
    checkSearchResult("Joshikyôei hanrangun", "2007", "tt1134826", result);

    // check 9. result (Inglorious Bumblers - 2009 - tt1529278)
    result = results.get(8);
    checkSearchResult("Inglorious Bumblers", "2009", "tt1529278", result);

    // check 10. result (Inglorious Relations - 2008 - tt1378268)
    result = results.get(9);
    checkSearchResult("Inglorious Relations", "2008", "tt1378268", result);

    /*
     * test on akas.imdb.com - "16 Blocks" (redirect to page)
     */
    results = null;
    try {
      mp = new ImdbMetadataProvider(ImdbSiteDefinition.IMDB_COM);
      results = mp.search(new SearchQuery(MediaType.MOVIE, SearchQuery.Field.QUERY, "16 Blocks"));
    }
    catch (Exception e) {
    }

    // did we get a result?
    assertNotNull("Result", results);

    // result count
    assertEquals("Result count", 1, results.size());

    // check first result (16 Blocks - 2006 - tt0450232)
    result = results.get(0);
    checkSearchResult("16 Blocks", "2006", "tt0450232", result);

  }

  private void checkSearchResult(String title, String year, String imdbId, MediaSearchResult result) {
    // title
    assertEquals("title", title, result.getTitle());
    // year
    assertEquals("year", year, result.getYear());
    // imdbId
    assertEquals("imdbId", imdbId, result.getIMDBId());
  }

  @Test
  public void testScrape() {
    ImdbMetadataProvider mp = null;
    MediaSearchResult sr = null;
    MediaMetadata md = null;

    /*
     * scrape akas.imdb.com - 9 - tt0472033
     */
    mp = new ImdbMetadataProvider(ImdbSiteDefinition.IMDB_COM);
    sr = new MediaSearchResult();
    sr.setIMDBId("tt0472033");
    sr.setTitle("9");
    sr.setYear("2009");

    md = null;
    try {
      Globals.settings.setCertificationCountry(CountryCode.US);
      md = mp.getMetaData(sr);
    }
    catch (Exception e) {
    }

    // did we get metadata?
    assertNotNull("MediaMetadata", md);

    // check moviedetails
    checkMovieDetails("9", "2009", "9", 7.0, 63365, "(7) To Defend Us...", 79, "Shane Acker", "Pamela Pettler, Shane Acker", "PG-13", md);

    // check poster
    // checkMoviePoster("http://ia.media-imdb.com/images/M/MV5BMTY2ODE1MTgxMV5BMl5BanBnXkFtZTcwNTM1NTM2Mg@@._V1._SX195_SY195_.jpg",
    // md);

    // check genres
    List<MediaGenres> genres = new ArrayList<MediaGenres>();
    genres.add(MediaGenres.ANIMATION);
    genres.add(MediaGenres.ACTION);
    genres.add(MediaGenres.ADVENTURE);
    genres.add(MediaGenres.FANTASY);
    genres.add(MediaGenres.MYSTERY);
    genres.add(MediaGenres.SCIENCE_FICTION);
    genres.add(MediaGenres.THRILLER);
    checkGenres(genres, md);

    // check plot
    checkPlot(
        "In a world destroyed in a war between man and machine, a hand-stitched doll with the number 9 written on its back comes to life. The world he has awakened in is frightening, but he quickly learns that he is not alone and that there are others like him, also with a single digit written on their back. The first one he encounters is 2 who tells him something of what happened to the world. 2 is also thrilled with the disk 9 is carrying, one with three unique symbols on the front. 9 soon learns that the disk and some of the other dolls who are prepared to die for the good of humankind may be the last hope for man's salvation.",
        md);

    // check cast
    List<CastMember> castMembers = new ArrayList<CastMember>();
    CastMember cm = new CastMember();
    cm.setName("Christopher Plummer");
    cm.setCharacter("#1 (voice)");
    cm.setImageUrl("http://ia.media-imdb.com/images/M/MV5BMTU5MzQ5MDY3NF5BMl5BanBnXkFtZTcwNzMxOTU5Ng@@._V1._SY125_SX100_.jpg");
    cm.setType(CastMember.ACTOR);
    castMembers.add(cm);

    cm = new CastMember();
    cm.setName("Martin Landau");
    cm.setCharacter("#2 (voice)");
    cm.setImageUrl("http://ia.media-imdb.com/images/M/MV5BMTI0MzkxNzg0OF5BMl5BanBnXkFtZTcwNDUzOTc5MQ@@._V1._SY125_SX100_.jpg");
    cm.setType(CastMember.ACTOR);
    castMembers.add(cm);

    checkCastMembers(castMembers, 10, md);

    // check production company
    checkProductionCompany("Focus Features, Relativity Media, Arc Productions, Starz Animation, Tim Burton Productions", md);

    /*
     * scrape akas.imdb.com - 12 Monkeys - tt0114746
     */
    mp = new ImdbMetadataProvider(ImdbSiteDefinition.IMDB_COM);
    sr = new MediaSearchResult();
    sr.setIMDBId("tt0114746");
    sr.setTitle("Twelve Monkeys");
    sr.setYear("1995");

    md = null;
    try {
      Globals.settings.setCertificationCountry(CountryCode.DE);
      md = mp.getMetaData(sr);
    }
    catch (Exception e) {
    }

    // did we get metadata?
    assertNotNull("MediaMetadata", md);

    // check moviedetails
    checkMovieDetails("Twelve Monkeys", "1995", "Twelve Monkeys", 8.1, 262821, "The future is history.", 129, "Terry Gilliam",
        "Chris Marker, David Webb Peoples", "16", md);

    // check poster
    // checkMoviePoster("http://ia.media-imdb.com/images/M/MV5BMTQ4OTM3NzkyN15BMl5BanBnXkFtZTcwMzIwMzgyMQ@@._V1._SX195_SY195_.jpg",
    // md);

    // check genres
    genres = new ArrayList<MediaGenres>();
    genres.add(MediaGenres.MYSTERY);
    genres.add(MediaGenres.SCIENCE_FICTION);
    genres.add(MediaGenres.THRILLER);
    checkGenres(genres, md);

    // check plot
    checkPlot(
        "An unknown and lethal virus has wiped out five billion people in 1996. Only 1% of the population has survived by the year 2035, and is forced to live underground. A convict (James Cole) reluctantly volunteers to be sent back in time to 1996 to gather information about the origin of the epidemic (who he's told was spread by a mysterious \"Army of the Twelve Monkeys\") and locate the virus before it mutates so that scientists can study it. Unfortunately Cole is mistakenly sent to 1990, six years earlier than expected, and is arrested and locked up in a mental institution, where he meets Dr. Kathryn Railly, a psychiatrist, and Jeffrey Goines, the insane son of a famous scientist and virus expert.",
        md);

    // check cast
    castMembers = new ArrayList<CastMember>();
    cm = new CastMember();
    cm.setName("Joseph Melito");
    cm.setCharacter("Young Cole");
    cm.setImageUrl("");
    cm.setType(CastMember.ACTOR);
    castMembers.add(cm);

    cm = new CastMember();
    cm.setName("Bruce Willis");
    cm.setCharacter("James Cole");
    cm.setImageUrl("http://ia.media-imdb.com/images/M/MV5BMjA0MjMzMTE5OF5BMl5BanBnXkFtZTcwMzQ2ODE3Mw@@._V1._SY125_SX100_.jpg");
    cm.setType(CastMember.ACTOR);
    castMembers.add(cm);

    checkCastMembers(castMembers, 82, md);

    // check production company
    checkProductionCompany("Universal Pictures, Atlas Entertainment, Classico", md);

    /*
     * scrape akas.imdb.com - Brave - tt1217209
     */
    mp = new ImdbMetadataProvider(ImdbSiteDefinition.IMDB_COM);
    sr = new MediaSearchResult();
    sr.setIMDBId("tt1217209");
    sr.setTitle("Brave");
    sr.setYear("2012");

    md = null;
    try {
      Globals.settings.setCertificationCountry(CountryCode.GB);
      md = mp.getMetaData(sr);
    }
    catch (Exception e) {
    }

    // did we get metadata?
    assertNotNull("MediaMetadata", md);

    // check moviedetails
    checkMovieDetails("Brave", "2012", "Brave", 7.4, 52871, "Change your fate.", 93, "Mark Andrews, Brenda Chapman", "Brenda Chapman, Mark Andrews",
        "PG", md);

    // check poster
    // checkMoviePoster("http://ia.media-imdb.com/images/M/MV5BMzgwODk3ODA1NF5BMl5BanBnXkFtZTcwNjU3NjQ0Nw@@._V1._SX195_SY195_.jpg",
    // md);

    // check genres
    genres = new ArrayList<MediaGenres>();
    genres.add(MediaGenres.ANIMATION);
    genres.add(MediaGenres.ACTION);
    genres.add(MediaGenres.ADVENTURE);
    genres.add(MediaGenres.COMEDY);
    genres.add(MediaGenres.FAMILY);
    genres.add(MediaGenres.FANTASY);
    checkGenres(genres, md);

    // check plot
    checkPlot(
        "Set in Scotland in a rugged and mythical time, \"Brave\" features Merida, an aspiring archer and impetuous daughter of royalty. Merida makes a reckless choice that unleashes unintended peril and forces her to spring into action to set things right.",
        md);

    // check cast
    castMembers = new ArrayList<CastMember>();
    cm = new CastMember();
    cm.setName("Kelly Macdonald");
    cm.setCharacter("Merida (voice)");
    cm.setImageUrl("http://ia.media-imdb.com/images/M/MV5BMjE0ODMzMjMyOV5BMl5BanBnXkFtZTcwMTYzNTA0NA@@._V1._SY125_SX100_.jpg");
    cm.setType(CastMember.ACTOR);
    castMembers.add(cm);

    cm = new CastMember();
    cm.setName("Billy Connolly");
    cm.setCharacter("Fergus (voice)");
    cm.setImageUrl("http://ia.media-imdb.com/images/M/MV5BMTQzMzM2MTA4Ml5BMl5BanBnXkFtZTYwMzIxNTM1._V1._SY125_SX100_.jpg");
    cm.setType(CastMember.ACTOR);
    castMembers.add(cm);

    checkCastMembers(castMembers, 14, md);

    // check production company
    checkProductionCompany("Walt Disney Pictures, Pixar Animation Studios", md);

  }

  private void checkMovieDetails(String title, String year, String originalTitle, double rating, int voteCount, String tagline, int runtime,
      String director, String writer, String certification, MediaMetadata md) {
    // title
    assertEquals("title ", title, md.getMediaTitle());
    // year
    assertEquals("year", year, md.getYear());
    // original title
    assertEquals("originalTitle", originalTitle, md.getOriginalTitle());
    // rating
    assertEquals("rating", rating, md.getUserRating(), 0.01);
    // count (only check if parsed cout count is smaller than the given
    // votecount)
    if (voteCount > md.getVoteCount()) {
      assertEquals("count", voteCount, md.getVoteCount());
    }
    // tagline
    assertEquals("tagline", tagline, md.getTagline());
    // runtime
    assertEquals("runtime", runtime, md.getRuntime());
    // director
    StringBuilder sb = new StringBuilder();
    for (CastMember cm : md.getCastMembers(CastMember.DIRECTOR)) {
      if (StringUtils.isNotEmpty(sb)) {
        sb.append(", ");
      }
      sb.append(cm.getName());
    }
    assertEquals("director", director, sb.toString());
    // writer
    sb = new StringBuilder();
    for (CastMember cm : md.getCastMembers(CastMember.WRITER)) {
      if (StringUtils.isNotEmpty(sb)) {
        sb.append(", ");
      }
      sb.append(cm.getName());
    }
    assertEquals("writer", writer, sb.toString());
    // certification
    assertEquals("certification", Certification.getCertification(Globals.settings.getCertificationCountry(), certification), md.getCertifications()
        .get(0));
  }

  private void checkMoviePoster(String url, MediaMetadata md) {
    // check poster
    List<MediaArt> mediaArt = md.getFanart();
    assertEquals("fanart count", 1, mediaArt.size());
    MediaArt art = mediaArt.get(0);
    assertEquals("poster", url, art.getDownloadUrl());
  }

  private void checkGenres(List<MediaGenres> genres, MediaMetadata md) {
    // cehck not null
    assertNotNull("genres", md.getGenres());

    // check the size
    assertEquals("genres count", genres.size(), md.getGenres().size());

    // check each genre if there is a counterpart in metadata
    for (MediaGenres genre : genres) {
      assertTrue("contains genre", md.getGenres().contains(genre));
    }
  }

  private void checkPlot(String plot, MediaMetadata md) {
    // plot
    assertEquals("plot", plot, md.getPlot());
  }

  private void checkCastMembers(List<CastMember> castMembers, int count, MediaMetadata md) {
    // not null
    assertNotNull(md.getCastMembers(CastMember.ACTOR));
    // count of castmembers
    assertEquals("castMember count", count, md.getCastMembers(CastMember.ACTOR).size());
    // check all defined members
    for (int i = 0; i < castMembers.size(); i++) {
      CastMember expected = castMembers.get(i);
      CastMember actual = md.getCastMembers(CastMember.ACTOR).get(i);

      // name
      assertEquals("name", expected.getName(), actual.getName());

      // character
      assertEquals("character", expected.getCharacter(), actual.getCharacter());

      // thumb
      assertEquals("thumb", expected.getImageUrl(), actual.getImageUrl());
    }
  }

  private void checkProductionCompany(String company, MediaMetadata md) {
    assertEquals("production company", company, md.getCompany());
  }

}

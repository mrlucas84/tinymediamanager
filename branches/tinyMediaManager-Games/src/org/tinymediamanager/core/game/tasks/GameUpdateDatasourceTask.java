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
 * limitations under the License. = game;
 */
package org.tinymediamanager.core.game.tasks;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.TmmThreadPool;
import org.tinymediamanager.core.ImageCacheTask;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileInformationFetcherTask;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.Message.MessageLevel;
import org.tinymediamanager.core.MessageManager;
import org.tinymediamanager.core.Utils;
import org.tinymediamanager.core.game.Game;
import org.tinymediamanager.core.game.GameFanartNaming;
import org.tinymediamanager.core.game.GameList;
import org.tinymediamanager.core.game.GamePosterNaming;
import org.tinymediamanager.core.game.connector.GameToXbmcNfoConnector;
import org.tinymediamanager.scraper.MediaTrailer;
import org.tinymediamanager.scraper.util.ParserUtils;

/**
 * The Class GameUpdateDataSourcesTask.
 * 
 * @author masterlilou
 */
public class GameUpdateDatasourceTask extends TmmThreadPool {

  private static final Logger       LOGGER      = LoggerFactory.getLogger(GameUpdateDatasourceTask.class);

  // skip well-known, but unneeded BD & DVD folders
  private static final List<String> skipFolders = Arrays.asList("CERTIFICATE", "BACKUP", "PLAYLIST", "CLPINF", "SSIF", "AUXDATA", "AUDIO_TS");

  private List<String>              dataSources;
  private GameList                  gameList;
  private HashSet<File>             filesFound  = new HashSet<File>();

  /**
   * Instantiates a new scrape task.
   * 
   */
  public GameUpdateDatasourceTask() {
    gameList = GameList.getInstance();
    dataSources = new ArrayList<String>(Globals.settings.getGameSettings().getGameDataSource());
  }

  public GameUpdateDatasourceTask(String datasource) {
    gameList = GameList.getInstance();
    dataSources = new ArrayList<String>(1);
    dataSources.add(datasource);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.SwingWorker#doInBackground()
   */
  @Override
  public Void doInBackground() {
    try {
      long start = System.currentTimeMillis();
      // cleanup just added for a new UDS run
      for (Game game : gameList.getGames()) {
        game.justAdded = false;
      }

      for (String ds : dataSources) {
        startProgressBar("prepare scan '" + ds + "'");
        LOGGER.info("start scan of datasource: " + ds);
        initThreadPool(1, "update"); // use only one, since the multiDir detection relies on accurate values...

        File dir = new File(ds);
        File[] dirs = dir.listFiles();
        if (dirs == null) {
          // error - continue with next datasource
          MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, "update.datasource", "update.datasource.unavailable",
              new String[] { ds }));
          continue;
        }

        // dig deeper in this dir
        submitTask(new FindGameTask(dir, ds));

        waitForCompletionOrCancel();

        if (cancel) {
          break;
        }

        startProgressBar("getting Mediainfo & cleanup...");
        initThreadPool(1, "mediainfo");
        LOGGER.info("removing orphaned games/files...");
        for (int i = gameList.getGames().size() - 1; i >= 0; i--) {
          if (cancel) {
            break;
          }
          Game game = gameList.getGames().get(i);

          // check only games matching datasource
          if (!new File(ds).equals(new File(game.getDataSource()))) {
            continue;
          }

          File gameDir = new File(game.getPath());
          if (!filesFound.contains(gameDir)) {
            // dir is not in hashset - check with exit to be sure it is not here
            if (!gameDir.exists()) {
              LOGGER.debug("game directory '" + gameDir + "' not found, removing...");
              gameList.removeGame(game);
            }
            else {
              LOGGER.warn("dir " + game.getPath() + " not in hashset, but on hdd!");
            }
          }
          else {
            // have a look if that game has just been added -> so we don't need any cleanup
            if (!game.justAdded) {
              // check and delete all not found MediaFiles
              List<MediaFile> mediaFiles = new ArrayList<MediaFile>(game.getMediaFiles());
              for (MediaFile mf : mediaFiles) {
                if (!filesFound.contains(mf.getFile())) {
                  if (!mf.exists()) {
                    game.removeFromMediaFiles(mf);
                  }
                  else {
                    LOGGER.warn("file " + mf.getFile().getAbsolutePath() + " not in hashset, but on hdd!");
                  }
                }
              }
              game.saveToDb();
            }
            submitTask(new MediaFileInformationFetcherTask(game.getMediaFiles(), game, false));
          }
        } // end game loop
        waitForCompletionOrCancel();
        if (cancel) {
          break;
        }

        // build image cache on import
        if (Globals.settings.getGameSettings().isBuildImageCacheOnImport()) {
          List<File> imageFiles = new ArrayList<File>();
          for (Game game : gameList.getGames()) {
            if (!new File(ds).equals(new File(game.getDataSource()))) {
              // check only games matching datasource
              continue;
            }
            imageFiles.addAll(game.getImagesToCache());
          }

          ImageCacheTask task = new ImageCacheTask(imageFiles);
          Globals.executor.execute(task);
        }

      } // END datasource loop
      long end = System.currentTimeMillis();
      LOGGER.info("Done updating datasource :) - took " + Utils.MSECtoHHMMSS(end - start));

      if (cancel) {
        cancel(false);// swing cancel
      }
    }
    catch (Exception e) {
      LOGGER.error("Thread crashed", e);
      MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, "update.datasource", "message.update.threadcrashed"));
    }
    return null;
  }

  /**
   * parses a list of GAME files in a dir and creates games out of it
   * 
   * @param files
   *          list of video files
   * @param datasource
   *          our root datasource
   */
  public void parseMultiGameDir(File[] files, String datasource) {
    if (files == null) {
      return;
    }
    for (File file : files) {

      Game game = null;
      MediaFile mf = new MediaFile(file);
      String basename = Utils.cleanStackingMarkers(mf.getBasename());

      // 1) check if MF is already assigned to a game within path
      for (Game m : gameList.getGamesByPath(mf.getFile().getParentFile())) {
        if (m.getMediaFiles(MediaFileType.GAME).contains(mf)) {
          // ok, our MF is already in an game
          LOGGER.debug("found game from MediaFile");
          game = m;
          break;
        }
        for (MediaFile mfile : m.getMediaFiles(MediaFileType.GAME)) {
          // try to match like if we would create a new game
          if (ParserUtils.detectCleanGamename(Utils.cleanStackingMarkers(mfile.getBasename())).equals(
              ParserUtils.detectCleanGamename(Utils.cleanStackingMarkers(mf.getBasename())))) {
            LOGGER.debug("found possible game from filename");
            game = m;
            break;
          }
        }
      }

      if (game == null) {
        // 2) create if not found
        MediaFile nfo = new MediaFile(new File(datasource, basename + ".nfo"), MediaFileType.NFO);
        // from NFO?
        if (nfo.exists()) {
          LOGGER.debug("found NFO - try to parse");
          switch (Globals.settings.getGameSettings().getGameConnector()) {
            case XBMC:
              game = GameToXbmcNfoConnector.getData(nfo.getFile());
              break;
            default:
              break;
          }
        }
        if (game != null) {
          // valid NFO found, so add itself as MF
          LOGGER.debug("NFO valid - add it");
          game.addToMediaFiles(nfo);
        }
        else {
          // still NULL, create new game game from file
          LOGGER.debug("create new game");
          game = new Game();
          String[] ty = ParserUtils.detectCleanMovienameAndYear(basename);
          game.setTitle(ty[0]);
          if (!ty[1].isEmpty()) {
            game.setYear(ty[1]);
          }
          game.setDateAdded(new Date());
          game.saveToDb();
        }
        game.setDataSource(datasource);
        game.setNewlyAdded(true);
        game.setPath(mf.getPath());
      }

      game.addToMediaFiles(mf);
      game.setMultiGameDir(true);

      // 3) find named fanart files
      File gfx = new File(mf.getPath(), game.getFanartFilename(GameFanartNaming.FILENAME_FANART_JPG, file.getName()));
      addFileToGame(game, gfx, MediaFileType.FANART);

      gfx = new File(mf.getPath(), game.getFanartFilename(GameFanartNaming.FILENAME_FANART_PNG, file.getName()));
      addFileToGame(game, gfx, MediaFileType.FANART);

      gfx = new File(mf.getPath(), game.getFanartFilename(GameFanartNaming.FILENAME_FANART2_JPG, file.getName()));
      addFileToGame(game, gfx, MediaFileType.FANART);

      gfx = new File(mf.getPath(), game.getFanartFilename(GameFanartNaming.FILENAME_FANART2_PNG, file.getName()));
      addFileToGame(game, gfx, MediaFileType.FANART);

      // 4) find named poster files
      gfx = new File(mf.getPath(), game.getPosterFilename(GamePosterNaming.FILENAME_POSTER_JPG, file.getName()));
      addFileToGame(game, gfx, MediaFileType.POSTER);

      gfx = new File(mf.getPath(), game.getPosterFilename(GamePosterNaming.FILENAME_POSTER_PNG, file.getName()));
      addFileToGame(game, gfx, MediaFileType.POSTER);

      game.saveToDb();
      if (game.getGameSet() != null) {
        LOGGER.debug("game is part of a gameset");
        // game.getGameSet().addGame(game);
        game.getGameSet().insertGame(game);
        gameList.sortGamesInGameSet(game.getGameSet());
        game.getGameSet().saveToDb();
        game.saveToDb();
      }
      game.justAdded = true;
      gameList.addGame(game);
    }
  }

  private void addFileToGame(Game game, File file, MediaFileType type) {
    if (file.exists()) {
      // store file for faster cleanup
      synchronized (filesFound) {
        filesFound.add(file);
      }
      game.addToMediaFiles(new MediaFile(file, type));
    }
  }

  /**
   * ThreadpoolWorker to work off ONE possible game from root datasource directory
   * 
   * @author Myron Boyle
   * @version 1.0
   */
  private class FindGameTask implements Callable<Object> {

    private File   subdir     = null;
    private String datasource = "";

    public FindGameTask(File subdir, String datasource) {
      this.subdir = subdir;
      this.datasource = datasource;
    }

    @Override
    public String call() throws Exception {
      // find all possible game folders recursive
      ArrayList<File> mov = getRootGameDirs(subdir, 1);
      // remove dupe game dirs
      HashSet<File> h = new HashSet<File>(mov);
      mov.clear();
      mov.addAll(h);
      for (File gameDir : mov) {
        // check if multiple games or a single one
        parseGameDirectory(gameDir, datasource);
      }
      // return first level folder name... uhm. yeah
      return subdir.getName();
    }
  }

  /**
   * parses the complete game directory, and adds a game with all found MediaFiles
   * 
   * @param gameDir
   * @param dataSource
   */
  private void parseGameDirectory(File gameDir, String dataSource) {
    try {
      // store dir for faster cleanup
      synchronized (filesFound) {
        filesFound.add(gameDir);
      }

      // list all type GAME files
      File[] files = gameDir.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return new MediaFile(new File(dir, name)).getType().equals(MediaFileType.GAME); // no trailer or extra vids!
        }
      });

      // check if we have more than one game in dir
      HashSet<String> h = new HashSet<String>();
      for (File file : files) {
        MediaFile mf = new MediaFile(file);
        LOGGER.debug("file : " + file);
        h.add(ParserUtils.detectCleanGamename(Utils.cleanStackingMarkers(FilenameUtils.getBaseName(file.getName()))));
      }
      // more than 1, or if DS=dir then assume a multi dir (only second level is a normal game dir)
      if (h.size() > 1 || gameDir.equals(new File(dataSource))) {
        LOGGER.debug("WOOT - we have a multi game directory: " + gameDir);
        if (Globals.settings.getGameSettings().isDetectGameMultiDir()) {
          parseMultiGameDir(files, dataSource);
        }
        else {
          MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, "update.datasource", "update.datasource.gameinroot",
              new String[] { gameDir.getName() }));
        }
      }
      else {
        LOGGER.debug("PATH - normal game directory: " + gameDir);

        Game game = gameList.getGameByPath(gameDir);
        ArrayList<MediaFile> mfs = getAllMediaFilesRecursive(gameDir);

        if (game == null) {
          LOGGER.info("parsing game " + gameDir);
          game = new Game();

          // first round - try to parse NFO(s) first
          for (MediaFile mf : mfs) {
            if (mf.getType().equals(MediaFileType.NFO)) {
              LOGGER.debug("parsing NFO " + mf.getFilename());
              Game nfo = null;
              switch (Globals.settings.getGameSettings().getGameConnector()) {
                case XBMC:
                  nfo = GameToXbmcNfoConnector.getData(mf.getFile());
                  break;

                default:
                  break;
              }
              if (nfo != null) {
                game = nfo;
                game.addToMediaFiles(mf);
              } // end NFO null
            }
          }

          if (game.getTitle().isEmpty()) {
            String[] ty = ParserUtils.detectCleanGamenameAndYear(gameDir.getName());
            game.setTitle(ty[0]);
            if (!ty[1].isEmpty()) {
              game.setYear(ty[1]);
            }
          }
          game.setPath(gameDir.getPath());
          game.setDataSource(dataSource);
          game.setDateAdded(new Date());
          game.setNewlyAdded(true);

          LOGGER.debug("store game into DB " + gameDir.getName());
          game.saveToDb(); // savepoint

          if (game.getGameSet() != null) {
            LOGGER.debug("game is part of a gameset");
            // game.getGameSet().addGame(game);
            game.getGameSet().insertGame(game);
            gameList.sortGamesInGameSet(game.getGameSet());
            game.getGameSet().saveToDb();
            game.saveToDb();
          }

        } // end game is null

        List<MediaFile> current = game.getMediaFiles();

        // second round - now add all the other known files
        for (MediaFile mf : mfs) {
          if (!current.contains(mf)) { // a new mediafile was found!
            switch (mf.getType()) {
              case GAME:
                LOGGER.debug("parsing game file " + mf.getFilename());
                game.addToMediaFiles(mf);
                break;

              case TRAILER:
                LOGGER.debug("parsing trailer " + mf.getFilename());
                mf.gatherMediaInformation(); // do this exceptionally here, to set quality in one rush
                MediaTrailer mt = new MediaTrailer();
                mt.setName(mf.getFilename());
                mt.setProvider("downloaded");
                mt.setQuality(mf.getVideoFormat());
                mt.setInNfo(false);
                mt.setUrl(mf.getFile().toURI().toString());
                game.addTrailer(mt);
                game.addToMediaFiles(mf);
                break;

              case SUBTITLE:
                LOGGER.debug("parsing subtitle " + mf.getFilename());
                if (!mf.isPacked()) {
                  game.setSubtitles(true);
                  game.addToMediaFiles(mf);
                }
                break;

              case POSTER:
                LOGGER.debug("parsing poster " + mf.getFilename());
                game.addToMediaFiles(mf);
                break;

              case FANART:
                if (mf.getPath().toLowerCase().contains("extrafanart")) {
                  // there shouldn't be any files here
                  LOGGER.warn("problem: detected media file type FANART in extrafanart folder: " + mf.getPath());
                  continue;
                }
                LOGGER.debug("parsing fanart " + mf.getFilename());
                game.addToMediaFiles(mf);
                break;

              case EXTRAFANART:
                LOGGER.debug("parsing extrafanart " + mf.getFilename());
                game.addToMediaFiles(mf);
                break;

              case THUMB:
                LOGGER.debug("parsing thumbnail " + mf.getFilename());
                game.addToMediaFiles(mf);
                break;

              case AUDIO:
                LOGGER.debug("parsing audio stream " + mf.getFilename());
                game.addToMediaFiles(mf);
                break;

              case GRAPHIC:
              case UNKNOWN:
              default:
                LOGGER.debug("NOT adding unknown media file type: " + mf.getFilename());
                // game.addToMediaFiles(mf); // DO NOT ADD UNKNOWN
                break;
            } // end switch type
          } // end new MF found
        } // end MF loop

        // third round - try to match unknown graphics like title.ext or filename.ext as poster
        if (game.getPoster().isEmpty()) {
          for (MediaFile mf : mfs) {
            if (mf.getType().equals(MediaFileType.GRAPHIC)) {
              LOGGER.debug("parsing unknown graphic " + mf.getFilename());
              List<MediaFile> vid = game.getMediaFiles(MediaFileType.GAME);
              if (vid != null && !vid.isEmpty()) {
                String vfilename = FilenameUtils.getBaseName(vid.get(0).getFilename());
                if (vfilename.equals(FilenameUtils.getBaseName(mf.getFilename())) // basename match
                    || Utils.cleanStackingMarkers(vfilename).trim().equals(FilenameUtils.getBaseName(mf.getFilename())) // basename w/o stacking
                    || game.getTitle().equals(FilenameUtils.getBaseName(mf.getFilename()))) { // title match
                  mf.setType(MediaFileType.POSTER);
                  game.addToMediaFiles(mf);
                }
              }
            }
          }
        }

        game.saveToDb();
        game.justAdded = true;
        gameList.addGame(game);
      }
    }
    catch (NullPointerException e) {
      LOGGER.error("NPE:", e);
      MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, gameDir.getPath(), "message.update.errorgamedir"));
    }
    catch (Exception e) {
      LOGGER.error("error update Datasources", e);
      MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, gameDir.getPath(), "message.update.errorgamedir", new String[] { ":",
          e.getLocalizedMessage() }));
    }
  }

  /**
   * searches for file type GAME and tries to detect the root game directory
   * 
   * @param directory
   *          start dir
   * @param level
   *          the level how deep we are (level 0 = datasource root)
   * @return arraylist of abolute game dirs
   */
  public ArrayList<File> getRootGameDirs(File directory, int level) {
    ArrayList<File> ar = new ArrayList<File>();

    // separate files & dirs
    ArrayList<File> files = new ArrayList<File>();
    ArrayList<File> dirs = new ArrayList<File>();
    File[] list = directory.listFiles();
    if (list == null) {
      LOGGER.error("Whops. Cannot access directory: " + directory);
      return ar;
    }
    for (File file : list) {
      if (file.isFile()) {
        files.add(file);
      }
      else {
        // ignore .folders and others
        if (!skipFolders.contains(file.getName().toUpperCase()) && !file.getName().startsWith(".")) {
          dirs.add(file);
        }
      }
    }
    list = null;

    for (File f : files) {
      MediaFile mf = new MediaFile(f);

      if (mf.getType().equals(MediaFileType.GAME)) {

        // get current folder
        File gamedir = f.getParentFile();

        // ok, regular structure
        if (dirs.isEmpty() && level > 1 && !Utils.getStackingMarker(gamedir.getName()).isEmpty()) {
          // no more dirs in that directory
          // and at least 2 levels deep
          // stacking found (either on file or parent dir)
          // -> assume parent as game dir"
          gamedir = gamedir.getParentFile();
          ar.add(gamedir);
        }
        else {
          // -> assume current dir as game dir"
          ar.add(gamedir);
        }
      }
    }

    for (File dir : dirs) {
      ar.addAll(getRootGameDirs(dir, level + 1));
    }

    return ar;
  }

  /**
   * recursively gets all MediaFiles from a gamedir
   * 
   * @param dir
   *          the game root dir
   * @return list of files
   */
  public ArrayList<MediaFile> getAllMediaFilesRecursive(File dir) {
    ArrayList<MediaFile> mv = new ArrayList<MediaFile>();

    File[] list = dir.listFiles();
    for (File file : list) {
      if (file.isFile()) {
        mv.add(new MediaFile(file));
        // store dir for faster cleanup
        synchronized (filesFound) {
          filesFound.add(file);
        }
      }
      else {
        // ignore .folders and others
        if (!skipFolders.contains(file.getName().toUpperCase()) && !file.getName().startsWith(".")) {
          mv.addAll(getAllMediaFilesRecursive(file));
        }
      }
    }

    return mv;
  }

  /*
   * Executed in event dispatching thread
   */
  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.SwingWorker#done()
   */
  @Override
  public void done() {
    stopProgressBar();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tinymediamanager.ui.TmmSwingWorker#cancel()
   */
  @Override
  public void cancel() {
    cancel = true;
    // cancel(false);
  }

  @Override
  public void callback(Object obj) {
    startProgressBar((String) obj, getTaskcount(), getTaskdone());
  }
}

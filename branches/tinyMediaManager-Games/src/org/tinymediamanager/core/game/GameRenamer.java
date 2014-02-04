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
package org.tinymediamanager.core.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.Globals;
import org.tinymediamanager.core.ImageCache;
import org.tinymediamanager.core.MediaFile;
import org.tinymediamanager.core.MediaFileSubtitle;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.Message.MessageLevel;
import org.tinymediamanager.core.MessageManager;
import org.tinymediamanager.core.Utils;
import org.tinymediamanager.core.game.connector.GameConnectors;
import org.tinymediamanager.scraper.Certification;

/**
 * The Class GameRenamer.
 * 
 * @author Manuel Laggner / Myron Boyle
 */
public class GameRenamer {

  /** The Constant LOGGER. */
  private final static Logger LOGGER = LoggerFactory.getLogger(GameRenamer.class);

  private static void renameSubtitles(Game m) {
    // build language lists
    Set<String> langArray = Utils.KEY_TO_LOCALE_MAP.keySet();

    for (MediaFile sub : m.getMediaFiles(MediaFileType.SUBTITLE)) {
      String lang = "";
      String forced = "";
      List<MediaFileSubtitle> mfsl = sub.getSubtitles();

      if (mfsl != null && mfsl.size() > 0) {
        // use internal values
        MediaFileSubtitle mfs = mfsl.get(0);
        lang = mfs.getLanguage();
        if (mfs.isForced()) {
          forced = ".forced";
        }
      }
      else {
        // detect from filename, if we don't have a MediaFileSubtitle entry!

        // FIXME: DOES NOT WORK, game already renamed!!! - execute before game rename?!
        // remove the filename of game from subtitle, to ease parsing
        List<MediaFile> mfs = m.getMediaFiles(MediaFileType.GAME);
        String shortname = sub.getBasename().toLowerCase();
        if (mfs != null && mfs.size() > 0) {
          String vname = Utils.cleanStackingMarkers(mfs.get(0).getBasename()).toLowerCase();
          shortname = sub.getBasename().toLowerCase().replace(vname, "");
        }

        if (sub.getFilename().toLowerCase().contains("forced")) {
          // add "forced" prior language
          forced = ".forced";
          shortname = shortname.replaceAll("\\p{Punct}*forced", "");
        }
        // shortname = shortname.replaceAll("\\p{Punct}", "").trim(); // NEVER EVER!!!

        for (String l : langArray) {
          if (shortname.equalsIgnoreCase(l) || shortname.matches("(?i).*[ _.-]+" + l + "$")) {
            lang = Utils.getDisplayLanguage(l);
            LOGGER.debug("found language '" + l + "' in subtitle; displaying it as '" + lang + "'");
            break;
          }
        }
      }

      // rebuild new filename
      String newSubName = "";

      if (sub.getStacking() == 0) {
        // fine, so match to first game file
        MediaFile mf = m.getMediaFiles(MediaFileType.GAME).get(0);
        newSubName = mf.getBasename() + forced;
        if (!lang.isEmpty()) {
          newSubName += "." + lang;
        }
      }
      else {
        // with stacking info; try to match
        for (MediaFile mf : m.getMediaFiles(MediaFileType.GAME)) {
          if (mf.getStacking() == sub.getStacking()) {
            newSubName = mf.getBasename() + forced;
            if (!lang.isEmpty()) {
              newSubName += "." + lang;
            }
          }
        }
      }
      newSubName += "." + sub.getExtension();

      File newFile = new File(m.getPath(), newSubName);
      try {
        boolean ok = Utils.moveFileSafe(sub.getFile(), newFile);
        if (ok) {
          m.removeFromMediaFiles(sub);
          MediaFile mf = new MediaFile(newFile);
          MediaFileSubtitle mfs = new MediaFileSubtitle();
          if (!lang.isEmpty()) {
            mfs.setLanguage(lang);
          }
          if (!forced.isEmpty()) {
            mfs.setForced(true);
          }
          mfs.setCodec(sub.getExtension());
          mf.setContainerFormat(sub.getExtension()); // set containerformat, so mediainfo deos not overwrite our new array
          mf.addSubtitle(mfs);
          m.addToMediaFiles(mf);
        }
        else {
          MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, sub.getFilename(), "message.renamer.failedrename"));
        }
      }
      catch (Exception e) {
        LOGGER.error("error moving subtitles", e);
        MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, sub.getFilename(), "message.renamer.failedrename", new String[] { ":",
            e.getLocalizedMessage() }));
      }
    } // end MF loop
    m.saveToDb();
  }

  /**
   * Rename game.
   * 
   * @param game
   *          the game
   */
  public static void renameGame(Game game) {
    boolean posterRenamed = false;
    boolean fanartRenamed = false;

    // check if a datasource is set
    if (StringUtils.isEmpty(game.getDataSource())) {
      LOGGER.error("no Datasource set");
      return;
    }

    // all the good & needed mediafiles
    ArrayList<MediaFile> needed = new ArrayList<MediaFile>();
    ArrayList<MediaFile> cleanup = new ArrayList<MediaFile>();

    LOGGER.info("Renaming game: " + game.getTitle());
    LOGGER.debug("game year: " + game.getYear());
    LOGGER.debug("game path: " + game.getPath());
    if (game.getGameSet() != null) {
      LOGGER.debug("gameset: " + game.getGameSet().getTitle());
    }
    LOGGER.debug("path expression: " + Globals.settings.getGameSettings().getGameRenamerPathname());
    LOGGER.debug("file expression: " + Globals.settings.getGameSettings().getGameRenamerFilename());

    String newPathname = createDestinationForFoldername(Globals.settings.getGameSettings().getGameRenamerPathname(), game);
    String oldPathname = game.getPath();
    boolean resetMultidir = false;

    if (!newPathname.isEmpty()) {
      newPathname = game.getDataSource() + File.separator + newPathname;
      File srcDir = new File(oldPathname);
      File destDir = new File(newPathname);
      // move directory if needed
      if (!srcDir.equals(destDir)) {
        if (!game.isMultiGameDir()) {
          boolean ok = false;
          try {
            // FileUtils.moveDirectory(srcDir, destDir);
            ok = Utils.moveDirectorySafe(srcDir, destDir);
            if (ok) {
              game.updateMediaFilePath(srcDir, destDir);
              game.setPath(newPathname);
              game.saveToDb();
            }
          }
          catch (Exception e) {
            LOGGER.error("error moving folder: ", e);
            MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, srcDir.getPath(), "message.renamer.failedrename", new String[] { ":",
                e.getLocalizedMessage() }));
          }
          if (!ok) {
            // FIXME: when we were not able to rename folder, display error msg
            // and abort!!!
            return;
          }
        }
        else {
          // game in multidir; just create structure...
          if (!destDir.exists()) {
            destDir.mkdirs();
          }
          resetMultidir = true;
        }
      }
    }
    else {
      LOGGER.info("Folder rename settings were empty - NOT renaming folder");
      // set it to current for file renaming
      newPathname = game.getPath();
    }

    // if empty, do not rename file, but DO move them to game root
    boolean renameFiles = !Globals.settings.getGameSettings().getGameRenamerFilename().isEmpty();

    // cleanup with old game name
    for (GameNfoNaming s : GameNfoNaming.values()) {
      // mark all known variants for cleanup
      MediaFile del = new MediaFile(new File(game.getPath(), game.getNfoFilename(s)));
      cleanup.add(del);
    }
    for (GamePosterNaming s : GamePosterNaming.values()) {
      // mark all known variants for cleanup
      MediaFile del = new MediaFile(new File(game.getPath(), game.getPosterFilename(s)));
      cleanup.add(del);
    }
    for (GameFanartNaming s : GameFanartNaming.values()) {
      // mark all known variants for cleanup
      MediaFile del = new MediaFile(new File(game.getPath(), game.getFanartFilename(s)));
      cleanup.add(del);
    }

    // ######################################################################
    // ## rename VIDEO
    // ######################################################################
    String newGameFilename = "";
    for (MediaFile vid : game.getMediaFiles(MediaFileType.GAME)) {
      LOGGER.debug("testing file " + vid.getFile().getAbsolutePath());
      File f = vid.getFile();
      boolean testRenameOk = false;
      for (int i = 0; i < 5; i++) {
        testRenameOk = f.renameTo(f); // haahaa, try to rename to itself :P
        if (testRenameOk) {
          break; // ok it worked, step out
        }
        try {
          LOGGER.debug("rename did not work - sleep a while and try again...");
          Thread.sleep(1000);
        }
        catch (InterruptedException e) {
          LOGGER.warn("I'm so excited - could not sleep");
        }
      }
      if (!testRenameOk) {
        LOGGER.warn("File " + vid.getFile().getAbsolutePath() + " is not accessible!");
        MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, vid.getFilename(), "message.renamer.failedrename"));
        return;
      }
    }
    for (MediaFile vid : game.getMediaFiles(MediaFileType.GAME)) {
      LOGGER.info("rename file " + vid.getFile().getAbsolutePath());

      String newFilename = vid.getFilename();
      // String newPath = game.getPath() + File.separator;
      String fileExtension = FilenameUtils.getExtension(vid.getFilename());

      if (!game.isDisc()) {
        if (!vid.isDiscFile()) { // separate check, if we have old entries in DB
          cleanup.add(new MediaFile(vid)); // mark old file for cleanup (clone current)
          if (renameFiles) {
            // create new filename according to template
            newFilename = createDestinationForFilename(Globals.settings.getGameSettings().getGameRenamerFilename(), game);
            // is there any stacking information in the filename?
            // use vid.getStacking() != 0 for custom stacking format?
            String stacking = Utils.getStackingMarker(vid.getFilename());
            String delimiter = " ";
            if (Globals.settings.getGameSettings().isGameRenamerSpaceSubstitution()) {
              delimiter = Globals.settings.getGameSettings().getGameRenamerSpaceReplacement();
            }
            if (!stacking.isEmpty()) {
              newFilename += delimiter + stacking;
            }
            else if (vid.getStacking() != 0) {
              newFilename += delimiter + "CD" + vid.getStacking();
            }
            newFilename += "." + fileExtension;
          }

          // save new game filename for further operations
          if (StringUtils.isBlank(newGameFilename)) {
            newGameFilename = newFilename;
          }

          MediaFile newMF = new MediaFile(vid);
          File newFile = new File(newPathname, newFilename);
          try {
            boolean ok = Utils.moveFileSafe(vid.getFile(), newFile);
            if (ok) {
              newMF.setPath(newPathname);
              newMF.setFilename(newFilename);
            }
            else {
              return; // rename failed
            }
          }
          catch (FileNotFoundException e) {
            LOGGER.error("error moving video file - file not found", e);
            MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, vid.getFilename(), "message.renamer.failedrename", new String[] {
                ":", e.getLocalizedMessage() }));
            return; // rename failed
          }
          catch (Exception e) {
            LOGGER.error("error moving video file", e);
            MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, vid.getFilename(), "message.renamer.failedrename", new String[] {
                ":", e.getLocalizedMessage() }));
            return; // rename failed
          }
          needed.add(newMF);
        }
      }
      else {
        LOGGER.info("Game is a DVD/BluRay disc folder - NOT renaming file");
        needed.add(vid); // but keep it
      }
    }

    MediaFile mf = null;

    // ######################################################################
    // ## rename NFO
    // ######################################################################
    List<MediaFile> mfl = game.getMediaFiles(MediaFileType.NFO);
    if (mfl != null && mfl.size() > 0) {
      mf = mfl.get(0);
      cleanup.add(new MediaFile(mf)); // mark old file for cleanup (clone current)
      String newFilename = mf.getFilename();
      // String newPath = game.getPath() + File.separator;

      List<GameNfoNaming> nfonames = new ArrayList<GameNfoNaming>();
      if (game.isMultiGameDir()) {
        // Fixate the name regardless of setting
        nfonames.add(GameNfoNaming.FILENAME_NFO);
      }
      else {
        nfonames = Globals.settings.getGameSettings().getGameNfoFilenames();
      }
      for (GameNfoNaming name : nfonames) {
        MediaFile newMF = new MediaFile(mf);
        newFilename = game.getNfoFilename(name, newGameFilename);
        File newFile = new File(newPathname, newFilename);
        try {
          boolean ok = copyFile(mf.getFile(), newFile);
          if (ok) {
            newMF.setPath(newPathname);
            newMF.setFilename(newFilename);
          }
        }
        catch (Exception e) {
          LOGGER.error("error renaming Nfo", e);
          MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, mf.getFilename(), "message.renamer.failedrename", new String[] { ":",
              e.getLocalizedMessage() }));
        }
        needed.add(newMF);
      }
    }

    // ######################################################################
    // ## rename POSTER
    // ######################################################################
    mfl = game.getMediaFiles(MediaFileType.POSTER);
    if (mfl != null && mfl.size() > 0) {
      mf = mfl.get(0);
      cleanup.add(new MediaFile(mf)); // mark old file for cleanup (clone current)
      String newFilename = mf.getFilename();
      // String newPath = game.getPath() + File.separator;

      List<GamePosterNaming> posternames = new ArrayList<GamePosterNaming>();
      if (game.isMultiGameDir()) {
        // Fixate the name regardless of setting
        posternames.add(GamePosterNaming.FILENAME_POSTER_JPG);
        posternames.add(GamePosterNaming.FILENAME_POSTER_PNG);
      }
      else {
        posternames = Globals.settings.getGameSettings().getGamePosterFilenames();
      }
      for (GamePosterNaming name : posternames) {
        newFilename = game.getPosterFilename(name, newGameFilename);
        if (newFilename != null && !newFilename.isEmpty()) {
          String curExt = mf.getExtension();
          if (curExt.equalsIgnoreCase("tbn")) {
            String cont = mf.getContainerFormat();
            if (cont.equalsIgnoreCase("PNG")) {
              curExt = "png";
            }
            else if (cont.equalsIgnoreCase("JPEG")) {
              curExt = "jpg";
            }
          }
          if (!curExt.equals(FilenameUtils.getExtension(newFilename))) {
            // match extension to not rename PNG to JPG and vice versa
            continue;
          }
          posterRenamed = true;

          MediaFile newMF = new MediaFile(mf);
          File newFile = new File(newPathname, newFilename);
          try {
            boolean ok = copyFile(mf.getFile(), newFile);
            if (ok) {
              newMF.setPath(newPathname);
              newMF.setFilename(newFilename);
            }
          }
          catch (Exception e) {
            LOGGER.error("error renaming poster", e);
            MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, mf.getFilename(), "message.renamer.failedrename", new String[] { ":",
                e.getLocalizedMessage() }));
          }
          needed.add(newMF);
        }
      }
    }

    // ######################################################################
    // ## rename FANART
    // ######################################################################
    mfl = game.getMediaFiles(MediaFileType.FANART);
    if (mfl != null && mfl.size() > 0) {
      mf = mfl.get(0);
      cleanup.add(new MediaFile(mf)); // mark old file for cleanup (clone current)

      String newFilename = mf.getFilename();
      // String newPath = game.getPath() + File.separator;

      List<GameFanartNaming> fanartnames = new ArrayList<GameFanartNaming>();
      if (game.isMultiGameDir()) {
        // Fixate the name regardless of setting
        fanartnames.add(GameFanartNaming.FILENAME_FANART_JPG);
        fanartnames.add(GameFanartNaming.FILENAME_FANART_PNG);
      }
      else {
        fanartnames = Globals.settings.getGameSettings().getGameFanartFilenames();
      }
      for (GameFanartNaming name : fanartnames) {
        newFilename = game.getFanartFilename(name, newGameFilename);
        if (newFilename != null && !newFilename.isEmpty()) {
          String curExt = mf.getExtension();
          if (curExt.equalsIgnoreCase("tbn")) {
            String cont = mf.getContainerFormat();
            if (cont.equalsIgnoreCase("PNG")) {
              curExt = "png";
            }
            else if (cont.equalsIgnoreCase("JPEG")) {
              curExt = "jpg";
            }
          }
          if (!curExt.equals(FilenameUtils.getExtension(newFilename))) {
            // match extension to not rename PNG to JPG and vice versa
            continue;
          }
          fanartRenamed = true;

          MediaFile newMF = new MediaFile(mf);
          File newFile = new File(newPathname, newFilename);
          try {
            boolean ok = copyFile(mf.getFile(), newFile);
            if (ok) {
              newMF.setPath(newPathname);
              newMF.setFilename(newFilename);
            }
          }
          catch (Exception e) {
            LOGGER.error("error renaming fanart", e);
            MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, mf.getFilename(), "message.renamer.failedrename", new String[] { ":",
                e.getLocalizedMessage() }));
          }
          needed.add(newMF);
        }
      }
    }

    // ######################################################################
    // ## rename TRAILER
    // ######################################################################
    mfl = game.getMediaFiles(MediaFileType.TRAILER);
    if (mfl != null && mfl.size() > 0) {
      mf = mfl.get(0);
      cleanup.add(new MediaFile(mf)); // mark old file for cleanup (clone current)
      String newFilename = mf.getFilename();
      // String newPath = game.getPath() + File.separator;
      String fileExtension = FilenameUtils.getExtension(mf.getFilename());
      newFilename = createDestinationForFilename(Globals.settings.getGameSettings().getGameRenamerFilename(), game) + "-trailer." + fileExtension;
      MediaFile newMF = new MediaFile(mf);
      if (renameFiles) { // renamer template was not empty
        File newFile = new File(newPathname, newFilename);
        try {
          boolean ok = Utils.moveFileSafe(mf.getFile(), newFile);
          if (ok) {
            newMF.setPath(newPathname);
            newMF.setFilename(newFilename);
          }
        }
        catch (Exception e) {
          LOGGER.error("error renaming trailer", e);
          MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, mf.getFilename(), "message.renamer.failedrename", new String[] { ":",
              e.getLocalizedMessage() }));
        }
      }
      needed.add(newMF);
    }

    // ######################################################################
    // ## rename SUBTITLE
    // ######################################################################
    for (MediaFile sub : game.getMediaFiles(MediaFileType.SUBTITLE)) {
      needed.add(sub); // keep all subtitles, will be cleaned afterwards
    }

    // ######################################################################
    // ## rename EXTRAFANART
    // ######################################################################
    for (MediaFile extra : game.getMediaFiles(MediaFileType.EXTRAFANART)) {
      needed.add(extra); // keep all unknown
    }

    // ######################################################################
    // ## rename VIDEO_EXTRA
    // ######################################################################
    for (MediaFile vextra : game.getMediaFiles(MediaFileType.VIDEO_EXTRA)) {
      needed.add(vextra); // keep all extras
      // TODO: game to extras folder (or name)
    }

    // ######################################################################
    // ## rename THUMBNAILS
    // ######################################################################
    for (MediaFile unk : game.getMediaFiles(MediaFileType.THUMB)) {
      needed.add(unk); // keep all unknown
    }

    // ######################################################################
    // ## rename UNKNOWN
    // ######################################################################
    for (MediaFile unk : game.getMediaFiles(MediaFileType.UNKNOWN)) {
      needed.add(unk); // keep all unknown
    }

    // ######################################################################
    // ## invalidade image cache
    // ######################################################################
    for (MediaFile all : game.getMediaFiles()) {
      switch (all.getType()) {
        case BANNER:
        case FANART:
        case EXTRAFANART:
        case GRAPHIC:
        case POSTER:
        case THUMB:
        case UNKNOWN:
          ImageCache.invalidateCachedImage(all.getPath() + File.separator + all.getFilename());
        default:
          break;
      }
    }

    // remove duplicate MediaFiles
    Set<MediaFile> newMFs = new LinkedHashSet<MediaFile>(needed);
    needed.clear();
    needed.addAll(newMFs);

    if (resetMultidir) {
      game.setMultiGameDir(false);
    }
    game.removeAllMediaFiles();
    game.addToMediaFiles(needed);
    game.setPath(newPathname);
    game.saveToDb();

    // cleanup & rename subtitle files
    renameSubtitles(game);

    game.gatherMediaFileInformation(false);
    game.saveToDb();

    // rewrite NFO if it's a MP NFO and there was a change with poster/fanart
    if (Globals.settings.getGameSettings().getGameConnector() != GameConnectors.XBMC && (posterRenamed || fanartRenamed)) {
      game.writeNFO();
    }

    // ######################################################################
    // ## CLEANUP
    // ######################################################################
    LOGGER.info("Cleanup...");
    for (int i = cleanup.size() - 1; i >= 0; i--) {
      // cleanup files which are not needed
      if (!needed.contains(cleanup.get(i))) {
        MediaFile cl = cleanup.get(i);
        if (cl.getFile().exists()) { // unneded, but for not diplaying wrong deletes in logger...
          LOGGER.debug("Deleting " + cl.getFilename());
          FileUtils.deleteQuietly(cl.getFile()); // delete cleanup file
        }
        File[] list = cl.getFile().getParentFile().listFiles();
        if (list != null && list.length == 0) {
          // if directory is empty, delete it as well
          LOGGER.debug("Deleting empty Directory" + cl.getFile().getParentFile().getAbsolutePath());
          FileUtils.deleteQuietly(cl.getFile().getParentFile());
        }
      }
    }

    // clean all non tmm nfos
    if (Globals.settings.getGameSettings().isGameRenamerNfoCleanup()) {
      File[] content = new File(game.getPath()).listFiles();
      for (File file : content) {
        if (file.isFile() && file.getName().toLowerCase().endsWith(".nfo")) {
          // check if it's a tmm nfo
          boolean supported = false;
          for (MediaFile nfo : game.getMediaFiles(MediaFileType.NFO)) {
            if (nfo.getFilename().equals(file.getName())) {
              supported = true;
            }
          }
          if (!supported) {
            LOGGER.debug("Deleting " + file.getName());
            FileUtils.deleteQuietly(file);
          }
        }
      }
    }
  }

  /**
   * Creates the new filename according to template string
   * 
   * @param template
   *          the template
   * @param game
   *          the game
   * @return the string
   */
  public static String createDestinationForFilename(String template, Game game) {
    // replace optional group first
    Pattern regex = Pattern.compile("\\{(.*?)\\}");
    Matcher mat = regex.matcher(template);
    while (mat.find()) {
      template = template.replace(mat.group(0), replaceOptionalVariable(mat.group(1), game, true));
    }
    return createDestination(template, game, true);
  }

  /**
   * Creates the new filename according to template string
   * 
   * @param template
   *          the template
   * @param game
   *          the game
   * @return the string
   */
  public static String createDestinationForFoldername(String template, Game game) {
    // replace optional group first
    Pattern regex = Pattern.compile("\\{(.*?)\\}");
    Matcher mat = regex.matcher(template);
    while (mat.find()) {
      template = template.replace(mat.group(0), replaceOptionalVariable(mat.group(1), game, false));
    }
    return createDestination(template, game, false);
  }

  /**
   * replaces an optional variable, eg "{ Year $Y }"<br>
   * if we have a year, "Year 2013" will be returned<br>
   * if $Y replacement was empty, the complete optional tag will be empty.
   * 
   * @param s
   * @param game
   * @param forFilename
   * @return
   */
  private static String replaceOptionalVariable(String s, Game game, boolean forFilename) {
    Pattern regex = Pattern.compile("\\$.{1}");
    Matcher mat = regex.matcher(s);
    if (mat.find()) {
      String rep = createDestination(mat.group(), game, true);
      if (rep.isEmpty()) {
        return "";
      }
      else {
        return s.replace(mat.group(), rep);
      }
    }
    else {
      return "";
    }
  }

  /**
   * Creates the new file/folder name according to template string
   * 
   * @param template
   *          the template
   * @param game
   *          the game
   * @param forFilename
   *          replace for filename (=true)? or for a foldername (=false)
   * @return the string
   */
  private static String createDestination(String template, Game game, boolean forFilename) {
    String newDestination = template;

    // replace token title ($T)
    if (newDestination.contains("$T")) {
      newDestination = replaceToken(newDestination, "$T", game.getTitle());
    }

    // replace token first letter of title ($1)
    if (newDestination.contains("$1")) {
      newDestination = replaceToken(newDestination, "$1", StringUtils.isNotBlank(game.getTitle()) ? game.getTitle().substring(0, 1).toUpperCase()
          : "");
    }

    // replace token first letter of sort title ($2)
    if (newDestination.contains("$2")) {
      newDestination = replaceToken(newDestination, "$2", StringUtils.isNotBlank(game.getTitleSortable()) ? game.getTitleSortable().substring(0, 1)
          .toUpperCase() : "");
    }

    // replace token year ($Y)
    if (newDestination.contains("$Y")) {
      if (game.getYear().equals("0")) {
        newDestination = newDestination.replace("$Y", "");
      }
      else {
        newDestination = replaceToken(newDestination, "$Y", game.getYear());
      }
    }

    // replace token orignal title ($O)
    if (newDestination.contains("$O")) {
      newDestination = replaceToken(newDestination, "$O", game.getOriginalTitle());
    }

    // replace token Game set title - sorted ($M)
    if (newDestination.contains("$M")) {
      if (game.getGameSet() != null
          && (game.getGameSet().getGames().size() > 1 || Globals.settings.getGameSettings().isGameRenamerCreateGamesetForSingleGame())) {
        newDestination = replaceToken(newDestination, "$M", game.getGameSet().getTitleSortable());
      }
      else {
        newDestination = newDestination.replace("$M", "");
      }
    }

    // replace token Game set title ($N)
    if (newDestination.contains("$N")) {
      if (game.getGameSet() != null
          && (game.getGameSet().getGames().size() > 1 || Globals.settings.getGameSettings().isGameRenamerCreateGamesetForSingleGame())) {
        newDestination = replaceToken(newDestination, "$N", game.getGameSet().getTitle());
      }
      else {
        newDestination = newDestination.replace("$N", "");
      }
    }

    // replace token IMDBid ($I)
    if (newDestination.contains("$I")) {
      newDestination = replaceToken(newDestination, "$I", game.getId("default").toString());
    }

    // replace token sort title ($E)
    if (newDestination.contains("$E")) {
      newDestination = replaceToken(newDestination, "$E", game.getTitleSortable());
    }

    // replace certification ($C)
    if (newDestination.contains("$C")) {
      if (game.getCertification() != Certification.NOT_RATED) {
        newDestination = replaceToken(newDestination, "$C", game.getCertification().getName());
      }
      else {
        newDestination = newDestination.replace("$C", "");
      }
    }

    if (game.getMediaFiles(MediaFileType.GAME).size() > 0) {
      MediaFile mf = game.getMediaFiles(MediaFileType.GAME).get(0);
      // replace token resolution ($R)
      if (newDestination.contains("$R")) {
        newDestination = replaceToken(newDestination, "$R", mf.getVideoResolution());
      }

      // replace token audio codec + channels ($A)
      if (newDestination.contains("$A")) {
        newDestination = replaceToken(newDestination, "$A", mf.getAudioCodec() + (mf.getAudioCodec().isEmpty() ? "" : "-") + mf.getAudioChannels());
      }

      // replace token video codec + format ($V)
      if (newDestination.contains("$V")) {
        newDestination = replaceToken(newDestination, "$V", mf.getVideoCodec() + (mf.getVideoCodec().isEmpty() ? "" : "-") + mf.getVideoFormat());
      }

      // replace token video format ($F)
      if (newDestination.contains("$F")) {
        newDestination = replaceToken(newDestination, "$F", mf.getVideoFormat());
      }
    }
    else {
      // no mediafiles; remove at least token (if available)
      newDestination = newDestination.replace("$R", "");
      newDestination = newDestination.replace("$A", "");
      newDestination = newDestination.replace("$V", "");
      newDestination = newDestination.replace("$F", "");
    }

    // replace token media source (BluRay|DVD|TV|...) ($S)
    // if (newDestination.contains("$S")) {
    // newDestination = newDestination.replaceAll("\\$S",
    // game.getMediaSource());
    // }

    // replace empty brackets
    newDestination = newDestination.replaceAll("\\(\\)", "");
    newDestination = newDestination.replaceAll("\\[\\]", "");
    newDestination = newDestination.replaceAll("\\{\\}", "");

    // if there are multiple file separators in a row - strip them out
    if (SystemUtils.IS_OS_WINDOWS) {
      // we need to mask it in windows
      newDestination = newDestination.replaceAll("\\\\{2,}", "\\\\");
      newDestination = newDestination.replaceAll("^\\\\", "");
    }
    else {
      newDestination = newDestination.replaceAll(File.separator + "{2,}", File.separator);
      newDestination = newDestination.replaceAll("^" + File.separator, "");
    }

    // replace ALL directory separators, if we generate this for filenames!
    if (forFilename) {
      newDestination = newDestination.replaceAll("\\/", " ");
      newDestination = newDestination.replaceAll("\\\\", " ");
    }

    // replace multiple spaces with a single one
    newDestination = newDestination.replaceAll(" +", " ").trim();

    // replace spaces with underscores if needed
    if (Globals.settings.getGameSettings().isGameRenamerSpaceSubstitution()) {
      String replacement = Globals.settings.getGameSettings().getGameRenamerSpaceReplacement();
      newDestination = newDestination.replace(" ", replacement);

      // also replace now multiple replacements with one to avoid strange looking results;
      // example:
      // Abraham Lincoln - Vapire Hunter -> Abraham-Lincoln---Vampire-Hunter
      newDestination = newDestination.replaceAll(Pattern.quote(replacement) + "+", replacement);
    }

    return newDestination.trim();
  }

  private static String replaceToken(String destination, String token, String replacement) {
    String replacingCleaned = "";
    if (StringUtils.isNotBlank(replacement)) {
      // replace illegal characters
      // http://msdn.microsoft.com/en-us/library/windows/desktop/aa365247%28v=vs.85%29.aspx
      replacingCleaned = replacement.replaceAll("([\"\\:<>|/?*])", "");
    }
    return destination.replace(token, replacingCleaned);
  }

  /**
   * copies or moves file.
   * 
   * @param oldFilename
   *          the old filename
   * @param newFilename
   *          the new filename
   * @throws Exception
   *           the exception
   */
  public static boolean copyFile(File oldFilename, File newFilename) throws Exception {
    if (!oldFilename.equals(newFilename)) {
      LOGGER.info("copy file " + oldFilename + " to " + newFilename);
      if (newFilename.exists()) {
        // overwrite?
        LOGGER.warn(newFilename + " exists - do nothing.");
        return true;
      }
      else {
        if (oldFilename.exists()) {
          FileUtils.copyFile(oldFilename, newFilename, true);
          return true;
        }
        else {
          throw new FileNotFoundException(oldFilename.getAbsolutePath());
        }
      }
    }
    else { // file is the same
      return false;
    }
  }
}

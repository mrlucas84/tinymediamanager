package org.tinymediamanager.core.platform;

/**
 * The class PlatformScraper.
 * 
 * @author masterlilou
 */
public class Platformscraper {
  int    id;
  String scraperName;
  String machineName;

  public String getMachineName() {
    return machineName;
  }

  public void setMachineName(String machineName) {
    this.machineName = machineName;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getScraperName() {
    return scraperName;
  }

  public void setScraperName(String scraperName) {
    this.scraperName = scraperName;
  }
}
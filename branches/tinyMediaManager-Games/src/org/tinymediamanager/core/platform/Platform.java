package org.tinymediamanager.core.platform;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The class Platforms. Used to represent a game platform
 * 
 * @author masterlilou
 */
@XmlRootElement()
@XmlType(propOrder = { "shortName", "longName", "id", "deck", "companyName", "platformName", "platformscraper", "images" })
public class Platform {

  private String                     shortName;
  private String                     longName;
  private Integer                    id;
  private String                     deck;
  private String                     companyName;
  private ArrayList<String>          platformName;
  private ArrayList<Platformscraper> platformscraper;
  private PlatformImages             images;

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getLongName() {
    return longName;
  }

  public void setLongName(String longName) {
    this.longName = longName;
  }

  public ArrayList<Platformscraper> getPlatformscraper() {
    return platformscraper;
  }

  public void setPlatformscraper(ArrayList<Platformscraper> platformscraper) {
    this.platformscraper = platformscraper;
  }

  public void addPlatformscraper(Platformscraper platformscraper) {
    if (this.platformscraper == null) {
      ArrayList<Platformscraper> p = new ArrayList<Platformscraper>();
      this.platformscraper = p;
      this.platformscraper.add(platformscraper);
    }
    else {
      this.platformscraper.add(platformscraper);
    }
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ArrayList<String> getPlatformName() {
    return platformName;
  }

  public void setPlatformName(ArrayList<String> platformName) {
    this.platformName = platformName;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getDeck() {
    return deck;
  }

  public void setDeck(String deck) {
    this.deck = deck;
  }

  public PlatformImages getImages() {
    return images;
  }

  public void setImages(PlatformImages images) {
    this.images = images;
  }
}
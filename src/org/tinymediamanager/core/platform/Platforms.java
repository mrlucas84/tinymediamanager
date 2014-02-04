package org.tinymediamanager.core.platform;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.Message.MessageLevel;
import org.tinymediamanager.core.MessageManager;

/**
 * The class Platforms. Used to load all known game platforms from a xml file
 * 
 * @author masterlilou
 */
@XmlRootElement(namespace = "fr.masterlilou.xml.jaxb.model")
public class Platforms {
  private static final Logger LOGGER            = LoggerFactory.getLogger(Platforms.class);
  private static final String platformSTORE_XML = "./resources/config/platformstore.xml";
  private static Platforms    instance;

  // XmLElementWrapper generates a wrapper element around XML representation
  @XmlElementWrapper(name = "Platforms")
  // XmlElement sets the name of the entities
  @XmlElement(name = "platform")
  private ArrayList<Platform> Platforms;

  public static Platforms getInstance() {
    if (instance == null) {
      // try to parse XML
      JAXBContext context;
      try {
        context = JAXBContext.newInstance(Platforms.class);
        Unmarshaller um = context.createUnmarshaller();
        try {
          Reader in = new InputStreamReader(new FileInputStream(platformSTORE_XML), "UTF-8");
          instance = (Platforms) um.unmarshal(in);
        }
        catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      catch (JAXBException e) {
        LOGGER.error("getInstance", e);
        MessageManager.instance.pushMessage(new Message(MessageLevel.ERROR, "tmm.settings", "message.config.loadsettingserror"));
      }
    }
    return instance;
  }

  private Platforms() {
  }

  public void setplatformList(ArrayList<Platform> platformList) {
    this.Platforms = platformList;
  }

  public ArrayList<Platform> getPlatformList() {
    return Platforms;
  }

  public boolean removeItem(Object o) {
    return Platforms.remove(o);
  }

  public boolean addItem(Platform p) {
    return Platforms.add(p);
  }

  public String getPlatformNamebyMachineName(String scraperName, String machineName) {
    for (Platform p : Platforms) {
      for (String n : p.getPlatformName()) {
        if (n.equalsIgnoreCase(machineName)) {
          for (Platformscraper s : p.getPlatformscraper()) {
            if (s.getScraperName().equalsIgnoreCase(scraperName)) {
              return p.getLongName();
            }
          }
        }
      }
    }
    return "None";
  }

  /**
   * Get MachineName of a related scraperName
   * 
   * @param scraperName
   * 
   * @param globalMachineName
   * 
   */

  public String getScraperPlatformNamebyMachineName(String scraperName, String globalMachineName) {
    for (Platform p : Platforms) {
      for (String n : p.getPlatformName()) {
        if (n.equalsIgnoreCase(globalMachineName)) {
          for (Platformscraper s : p.getPlatformscraper()) {
            if (s.getScraperName().equalsIgnoreCase(scraperName)) {
              return s.getMachineName();
            }
          }
        }
      }
    }
    return "None";
  }

  public boolean isPlatformName(String Name) {
    for (Platform p : Platforms) {
      if (p.getLongName().equalsIgnoreCase(Name)) {
        return true;
      }
    }
    return false;
  }

  public String getShortName(String longName) {
    for (Platform p : Platforms) {
      if (p.getLongName().equalsIgnoreCase(longName)) {
        return p.getShortName();
      }
    }
    return null;
  }

  public Integer getPlatformIdbyMachineId(String scraperName, Integer machineNameId) {
    for (Platform p : Platforms) {
      for (Platformscraper s : p.getPlatformscraper()) {
        if ((s.getId() == machineNameId) && (s.getScraperName().equalsIgnoreCase(scraperName))) {
          return p.getId();
        }
      }
    }
    return 0;
  }

  public String getPlatformNamebyMachineId(String scraperName, Integer machineNameId) {
    for (Platform p : Platforms) {
      for (Platformscraper s : p.getPlatformscraper()) {
        if ((s.getId() == machineNameId) && (s.getScraperName().equalsIgnoreCase(scraperName))) {
          return p.getLongName();
        }
      }
    }
    return "None";
  }

  public String getScaperPlatformNamebyName(String scraperName, String machineName) {
    for (Platform p : Platforms) {
      for (String n : p.getPlatformName()) {
        if (n.equalsIgnoreCase(machineName)) {
          for (Platformscraper s : p.getPlatformscraper()) {
            if (s.getScraperName().equalsIgnoreCase(scraperName)) {
              return s.getMachineName();
            }
          }
        }
      }
    }
    return "None";
  }
}

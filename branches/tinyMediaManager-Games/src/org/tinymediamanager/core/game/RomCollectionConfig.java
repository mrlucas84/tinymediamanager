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

import javax.xml.bind.annotation.XmlRootElement;

import org.tinymediamanager.core.AbstractModelObject;

/**
 * The class RomCollectionConfig - config of romCollection
 * 
 * @author masterlilou
 * 
 */
@XmlRootElement(name = "romCollection")
public class RomCollectionConfig extends AbstractModelObject {

  private String  shortName;
  private boolean iscollection;
  private String  emulatorExecutable;
  private String  emulatorParam;
  private String  romsCollectionPath;
  private String  romFileMask;
  private String  romsCollectionAntworkPath;

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public boolean isIscollection() {
    return iscollection;
  }

  public void setIscollection(boolean iscollection) {
    this.iscollection = iscollection;
  }

  public String getEmulatorExecutable() {
    return emulatorExecutable;
  }

  public void setEmulatorExecutable(String emulatorExecutable) {
    this.emulatorExecutable = emulatorExecutable;
  }

  public String getEmulatorParam() {
    return emulatorParam;
  }

  public void setEmulatorParam(String emulatorParam) {
    this.emulatorParam = emulatorParam;
  }

  public String getRomsCollectionPath() {
    return romsCollectionPath;
  }

  public void setRomsCollectionPath(String romsCollectionPath) {
    this.romsCollectionPath = romsCollectionPath;
  }

  public String getRomFileMask() {
    return romFileMask;
  }

  public void setRomFileMask(String romFileMask) {
    this.romFileMask = romFileMask;
  }

  public String getRomsCollectionAntworkPath() {
    return romsCollectionAntworkPath;
  }

  public void setRomsCollectionAntworkPath(String romsCollectionAntworkPath) {
    this.romsCollectionAntworkPath = romsCollectionAntworkPath;
  }

}

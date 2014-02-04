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
package org.tinymediamanager.ui.games;

import java.util.List;

import org.tinymediamanager.core.game.Game;

import ca.odell.glazedlists.TextFilterator;

/**
 * The Class GameFilterator is used to search games.
 * 
 * @author Manuel Laggner
 */
public class GameFilterator implements TextFilterator<Game> {

  /*
   * (non-Javadoc)
   * 
   * @see ca.odell.glazedlists.TextFilterator#getFilterStrings(java.util.List, java.lang.Object)
   */
  @Override
  public void getFilterStrings(List<String> baseList, Game game) {
    baseList.add(game.getTitle());
    baseList.add(game.getOriginalTitle());
  }
}

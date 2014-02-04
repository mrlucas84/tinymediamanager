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

import java.text.RuleBasedCollator;
import java.util.Comparator;

import org.tinymediamanager.core.game.Game;

/**
 * The Class GameComparator is used to (initial) sort the games in the gamepanel.
 * 
 * @author Manuel Laggner
 */
public class GameComparator implements Comparator<Game> {
  private RuleBasedCollator stringCollator = (RuleBasedCollator) RuleBasedCollator.getInstance();

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Game game1, Game game2) {
    if (stringCollator != null) {
      return stringCollator.compare(game1.getTitleSortable().toLowerCase(), game2.getTitleSortable().toLowerCase());
    }
    return game1.getTitleSortable().toLowerCase().compareTo(game2.getTitleSortable().toLowerCase());
  }

}

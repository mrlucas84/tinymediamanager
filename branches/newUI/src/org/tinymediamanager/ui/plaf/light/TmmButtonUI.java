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
package org.tinymediamanager.ui.plaf.light;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.BaseButtonUI;

/**
 * @author Manuel Laggner
 * 
 */
public class TmmButtonUI extends BaseButtonUI {

  private boolean isFlatButton = false;

  public static ComponentUI createUI(JComponent c) {
    return new TmmButtonUI();
  }

  @Override
  public void installDefaults(AbstractButton b) {
    super.installDefaults(b);

    Object prop = b.getClientProperty("flatButton");
    if (prop != null && prop instanceof Boolean) {
      Boolean flat = (Boolean) prop;
      isFlatButton = flat;
    }

    b.setOpaque(false);
  }

  @Override
  protected void paintBackground(Graphics g, AbstractButton b) {
    if (isFlatButton) {
      return;
    }

    super.paintBackground(g, b);
  }

  @Override
  protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
  }
}

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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseTabbedPaneUI;

/**
 * The Class TmmLightTabbedPaneUI.
 * 
 * @author Manuel Laggner
 */
public class TmmLightTabbedPaneUI extends BaseTabbedPaneUI {

  protected static int BORDER_RADIUS = 10;

  public static ComponentUI createUI(JComponent c) {
    return new TmmLightTabbedPaneUI();
  }

  @Override
  public void installDefaults() {
    super.installDefaults();
    tabAreaInsets = new Insets(2, 10, 2, 10);
    contentBorderInsets = new Insets(0, 0, 0, 0);
  }

  @Override
  protected Font getTabFont(boolean isSelected) {
    return super.getTabFont(isSelected).deriveFont(16f);
  }

  protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
    Graphics2D g2D = (Graphics2D) g;
    RenderingHints savedRenderingHints = g2D.getRenderingHints();
    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (isSelected) {
      g.setColor(AbstractLookAndFeel.getBackgroundColor());

    }
    else {
      g.setColor(new Color(163, 163, 163));
    }

    if (tabPlacement == TOP) {
      g.fillRoundRect(x, y, w, h, BORDER_RADIUS, BORDER_RADIUS);
      g.fillRect(x, y + BORDER_RADIUS, w, h);
      // g.fillRect(x + 1, y + 1, w - 1, h + 2);
    }
    else if (tabPlacement == LEFT) {
      g.fillRect(x + 1, y + 1, w + 2, h - 1);
    }
    else if (tabPlacement == BOTTOM) {
      g.fillRect(x + 1, y - 2, w - 1, h + 2);
    }
    else {
      g.fillRect(x - 2, y + 1, w + 2, h - 1);
    }

    g2D.setRenderingHints(savedRenderingHints);
  }

  protected void paintRoundedTopTabBorder(int tabIndex, Graphics g, int x1, int y1, int x2, int y2, boolean isSelected) {
  }

}

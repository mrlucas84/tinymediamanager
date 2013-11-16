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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;

import com.jtattoo.plaf.BaseIcons;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Manuel Laggner
 * 
 */
public class TmmLightIcons extends BaseIcons {

  public static Icon getCheckBoxIcon() {
    if (checkBoxIcon == null) {
      checkBoxIcon = new CheckBoxIcon();
    }
    return checkBoxIcon;
  }

  private static class CheckBoxIcon implements Icon {
    private static final int   RADIUS           = 8;
    private static final int   SELECTED_RADIUS  = 4;
    private static final Color SHADOW_COLOR     = new Color(215, 215, 215);
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color SELECTED_COLOR   = new Color(141, 165, 179);

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      if (!JTattooUtilities.isLeftToRight(c)) {
        x += 3;
      }

      AbstractButton b = (AbstractButton) c;
      ButtonModel model = b.getModel();

      Graphics2D g2D = (Graphics2D) g;
      Object savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
      g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      // paint background (and shadow)
      g.setColor(SHADOW_COLOR);
      g.fillOval(x, y + 1, 2 * RADIUS, 2 * RADIUS);
      g.setColor(BACKGROUND_COLOR);
      g.fillOval(x, y, 2 * RADIUS, 2 * RADIUS);

      if (model.isSelected()) {
        g.setColor(SELECTED_COLOR);
        g.fillOval(x + RADIUS - SELECTED_RADIUS, y + RADIUS - SELECTED_RADIUS, 2 * SELECTED_RADIUS, 2 * SELECTED_RADIUS);
      }

      g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRenderingHint);
    }

    @Override
    public int getIconWidth() {
      return 2 * RADIUS + 2;
    }

    @Override
    public int getIconHeight() {
      return 2 * RADIUS;
    }
  }
}

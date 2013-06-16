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
package org.tinymediamanager.ui.components;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.tinymediamanager.ui.RoundedCornerBorder;

/**
 * The class ToolbarButton. Represents a button in the main toolbar of tmm
 * 
 * @author Manuel Laggner
 * 
 */
public class ToolbarButton extends JPanel implements MouseListener {
  private static final long serialVersionUID = 6535223530465800467L;

  private JLabel            lblIcon;
  private JLabel            lblText;

  public ToolbarButton(String text, Icon icon) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setOpaque(false);

    lblIcon = new JLabel();
    lblIcon.setIcon(icon);
    lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
    lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
    add(lblIcon);

    lblText = new JLabel(text);
    lblText.setAlignmentX(Component.CENTER_ALIGNMENT);
    lblText.setHorizontalAlignment(SwingConstants.CENTER);
    add(lblText);

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseClicked(MouseEvent arg0) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseEntered(MouseEvent arg0) {
    setBorder(new RoundedCornerBorder());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseExited(MouseEvent arg0) {
    setBorder(BorderFactory.createEmptyBorder());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  @Override
  public void mousePressed(MouseEvent arg0) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseReleased(MouseEvent arg0) {
    // TODO Auto-generated method stub

  }
}

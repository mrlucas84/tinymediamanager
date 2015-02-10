package org.tinymediamanager.ui.plaf.light;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.BaseTextAreaUI;

public class TmmLightTextAreaUI extends BaseTextAreaUI {
  private FocusListener focusListener = null;

  public static ComponentUI createUI(JComponent c) {
    return new TmmLightTextAreaUI();
  }

  public void installDefaults() {
    super.installDefaults();
    getComponent().setBorder(TmmLightBorderFactory.getInstance().getTextBorder());
  }

  protected void installListeners() {
    super.installListeners();

    focusListener = new FocusListener() {

      public void focusGained(FocusEvent e) {
        if (getComponent() != null) {
          getComponent().invalidate();
          getComponent().repaint();
        }
      }

      public void focusLost(FocusEvent e) {
        if (getComponent() != null) {
          getComponent().invalidate();
          getComponent().repaint();
        }
      }
    };
    getComponent().addFocusListener(focusListener);
  }

  protected void uninstallListeners() {
    getComponent().removeFocusListener(focusListener);
    focusListener = null;
    super.uninstallListeners();
  }
}

package org.tinymediamanager.ui.plaf;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javax.swing.plaf.FontUIResource;

import com.jtattoo.plaf.AbstractTheme;

public class TmmTheme extends AbstractTheme {

  public static final java.lang.String ROBOTO = "Roboto";

  static {
    try {
      // Font dialogRegular = Font.createFont(Font.PLAIN, TmmTheme.class.getResource("Roboto-Regular.ttf").openStream());
      Font dialogRegular = Font.createFont(Font.PLAIN, TmmTheme.class.getResource("Roboto-Light.ttf").openStream());
      GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(dialogRegular);
    }
    catch (FontFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public FontUIResource getControlTextFont() {
    if (controlFont == null) {
      controlFont = new FontUIResource(ROBOTO, Font.PLAIN, 12);
    }
    return controlFont;
  }

  @Override
  public FontUIResource getSystemTextFont() {
    if (systemFont == null) {
      systemFont = new FontUIResource(ROBOTO, Font.PLAIN, 12);

    }
    return systemFont;
  }

  @Override
  public FontUIResource getUserTextFont() {
    if (userFont == null) {
      userFont = new FontUIResource(ROBOTO, Font.PLAIN, 12);

    }
    return userFont;
  }

  @Override
  public FontUIResource getMenuTextFont() {
    if (menuFont == null) {
      menuFont = new FontUIResource(ROBOTO, Font.PLAIN, 12);

    }
    return menuFont;
  }

  @Override
  public FontUIResource getWindowTitleFont() {
    if (windowTitleFont == null) {
      windowTitleFont = new FontUIResource(ROBOTO, Font.BOLD, 12);
    }
    return windowTitleFont;
  }

  @Override
  public FontUIResource getSubTextFont() {
    if (smallFont == null) {
      smallFont = new FontUIResource(ROBOTO, Font.PLAIN, 10);
    }
    return smallFont;
  }

}

package org.tinymediamanager.ui.components;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import org.tinymediamanager.ui.MainWindow;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class NotificationMessage extends JPanel implements ActionListener {
  private static final long serialVersionUID = -3575919232938540443L;

  /** Stroke size. it is recommended to set it to 1 for better view */
  protected int             strokeSize       = 1;
  /** Color of shadow */
  protected Color           shadowColor      = Color.black;
  /** Sets if it drops shadow */
  protected boolean         shady            = false;
  /** Sets if it has an High Quality view */
  protected boolean         highQuality      = true;
  /** Double values for Horizontal and Vertical radius of corner arcs */
  protected Dimension       arcs             = new Dimension(10, 10);
  /** Distance between shadow border and opaque panel border */
  protected int             shadowGap        = 4;
  /** The offset of shadow. */
  protected int             shadowOffset     = 4;
  /** The transparency value of shadow. ( 0 - 255) */
  protected int             shadowAlpha      = 100;

  private float             opacity          = 0f;
  private Timer             fadeTimer;
  private Timer             disposeTimer;

  public NotificationMessage(String text) {
    super();
    setOpaque(false);
    setLayout(new FormLayout(new ColumnSpec[] { FormFactory.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("200px"),
        FormFactory.LABEL_COMPONENT_GAP_COLSPEC, }, new RowSpec[] { FormFactory.NARROW_LINE_GAP_ROWSPEC, RowSpec.decode("fill:min(75px;default)"),
        FormFactory.NARROW_LINE_GAP_ROWSPEC, }));

    JTextArea tpMessage = new JTextArea();
    tpMessage.setLineWrap(true);
    tpMessage.setWrapStyleWord(true);
    tpMessage.setOpaque(false);
    tpMessage.setText(text);
    add(tpMessage, "2, 2, fill, fill");
    beginFade();
  }

  public void beginFade() {
    fadeTimer = new javax.swing.Timer(75, this);
    fadeTimer.setInitialDelay(0);
    fadeTimer.start();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == fadeTimer) {
      // fading
      opacity += .1;
      if (opacity > 1) {
        opacity = 1;
        fadeTimer.stop();
        fadeTimer = null;

        // start dispose timer
        disposeTimer = new javax.swing.Timer(10000, this);
        disposeTimer.start();
      }
      repaint();
    }
    if (e.getSource() == disposeTimer) {
      // disposing
      disposeTimer.stop();
      disposeTimer = null;
      MainWindow.getActiveInstance().removeMessage(this);
    }
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

    int width = getWidth();
    int height = getHeight();
    int shadowGap = this.shadowGap;
    Color shadowColorA = new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), shadowAlpha);
    Graphics2D graphics = (Graphics2D) g;

    // Sets antialiasing if HQ.
    if (highQuality) {
      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    // Draws shadow borders if any.
    if (shady) {
      graphics.setColor(shadowColorA);
      graphics.fillRoundRect(shadowOffset,// X position
          shadowOffset,// Y position
          width - strokeSize - shadowOffset, // width
          height - strokeSize - shadowOffset, // height
          arcs.width, arcs.height);// arc Dimension
    }
    else {
      shadowGap = 1;
    }

    // Draws the rounded opaque panel with borders.
    graphics.setColor(getBackground());
    graphics.fillRoundRect(0, 0, width - shadowGap, height - shadowGap, arcs.width, arcs.height);
    graphics.setColor(getForeground());
    graphics.setStroke(new BasicStroke(strokeSize));
    graphics.drawRoundRect(0, 0, width - shadowGap, height - shadowGap, arcs.width, arcs.height);

    // Sets strokes to default, is better.
    graphics.setStroke(new BasicStroke());
  }
}
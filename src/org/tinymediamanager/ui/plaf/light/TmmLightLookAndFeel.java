package org.tinymediamanager.ui.plaf.light;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.UIDefaults;

import com.jtattoo.plaf.AbstractBorderFactory;
import com.jtattoo.plaf.AbstractIconFactory;
import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.AbstractTheme;
import com.jtattoo.plaf.BaseCheckBoxMenuItemUI;
import com.jtattoo.plaf.BaseCheckBoxUI;
import com.jtattoo.plaf.BaseDesktopPaneUI;
import com.jtattoo.plaf.BaseEditorPaneUI;
import com.jtattoo.plaf.BaseFileChooserUI;
import com.jtattoo.plaf.BaseFormattedTextFieldUI;
import com.jtattoo.plaf.BaseLabelUI;
import com.jtattoo.plaf.BaseMenuBarUI;
import com.jtattoo.plaf.BaseMenuItemUI;
import com.jtattoo.plaf.BaseMenuUI;
import com.jtattoo.plaf.BasePasswordFieldUI;
import com.jtattoo.plaf.BasePopupMenuUI;
import com.jtattoo.plaf.BaseProgressBarUI;
import com.jtattoo.plaf.BaseRadioButtonMenuItemUI;
import com.jtattoo.plaf.BaseRadioButtonUI;
import com.jtattoo.plaf.BaseScrollPaneUI;
import com.jtattoo.plaf.BaseSeparatorUI;
import com.jtattoo.plaf.BaseSliderUI;
import com.jtattoo.plaf.BaseSpinnerUI;
import com.jtattoo.plaf.BaseSplitPaneUI;
import com.jtattoo.plaf.BaseTableUI;
import com.jtattoo.plaf.BaseTextAreaUI;
import com.jtattoo.plaf.BaseTextFieldUI;
import com.jtattoo.plaf.BaseToggleButtonUI;
import com.jtattoo.plaf.BaseToolTipUI;
import com.jtattoo.plaf.BaseTreeUI;
import com.jtattoo.plaf.JTattooUtilities;
import com.jtattoo.plaf.luna.LunaBorderFactory;
import com.jtattoo.plaf.luna.LunaButtonUI;
import com.jtattoo.plaf.luna.LunaComboBoxUI;
import com.jtattoo.plaf.luna.LunaIconFactory;
import com.jtattoo.plaf.luna.LunaInternalFrameUI;
import com.jtattoo.plaf.luna.LunaRootPaneUI;
import com.jtattoo.plaf.luna.LunaTableHeaderUI;
import com.jtattoo.plaf.luna.LunaToolBarUI;

public class TmmLightLookAndFeel extends AbstractLookAndFeel {

  private static final long           serialVersionUID = 7771660244511173072L;

  private static TmmLightDefaultTheme myTheme          = null;

  private static final List<String>   themesList       = new ArrayList<String>();
  private static final HashMap        themesMap        = new HashMap();
  private static final Properties     defaultProps     = new Properties();
  private static final Properties     smallFontProps   = new Properties();
  private static final Properties     largeFontProps   = new Properties();
  private static final Properties     giantFontProps   = new Properties();

  static {
    smallFontProps.setProperty("controlTextFont", "Roboto 10");
    smallFontProps.setProperty("systemTextFont", "Roboto 10");
    smallFontProps.setProperty("userTextFont", "Roboto 10");
    smallFontProps.setProperty("menuTextFont", "Roboto 10");
    smallFontProps.setProperty("windowTitleFont", "Roboto bold 10");
    smallFontProps.setProperty("subTextFont", "Roboto 8");

    largeFontProps.setProperty("controlTextFont", "Roboto 14");
    largeFontProps.setProperty("systemTextFont", "Roboto 14");
    largeFontProps.setProperty("userTextFont", "Roboto 14");
    largeFontProps.setProperty("menuTextFont", "Roboto 14");
    largeFontProps.setProperty("windowTitleFont", "Roboto bold 14");
    largeFontProps.setProperty("subTextFont", "Roboto 12");

    giantFontProps.setProperty("controlTextFont", "Roboto 18");
    giantFontProps.setProperty("systemTextFont", "Roboto 18");
    giantFontProps.setProperty("userTextFont", "Roboto 18");
    giantFontProps.setProperty("menuTextFont", "Roboto 18");
    giantFontProps.setProperty("windowTitleFont", "Roboto 18");
    giantFontProps.setProperty("subTextFont", "Roboto 16");

    themesList.add("Default");
    themesList.add("Small-Font");
    themesList.add("Large-Font");
    themesList.add("Giant-Font");

    themesMap.put("Default", defaultProps);
    themesMap.put("Small-Font", smallFontProps);
    themesMap.put("Large-Font", largeFontProps);
    themesMap.put("Giant-Font", giantFontProps);
  }

  public static List<String> getThemes() {
    return themesList;
  }

  public static Properties getThemeProperties(String name) {
    return ((Properties) themesMap.get(name));
  }

  public static void setTheme(String name) {
    setTheme((Properties) themesMap.get(name));
    if (myTheme != null) {
      AbstractTheme.setInternalName(name);
    }
  }

  public static void setTheme(String name, String licenseKey, String logoString) {
    Properties props = (Properties) themesMap.get(name);
    if (props != null) {
      props.put("licenseKey", licenseKey);
      props.put("logoString", logoString);
      setTheme(props);
      if (myTheme != null) {
        AbstractTheme.setInternalName(name);
      }
    }
  }

  public static void setTheme(Properties themesProps) {
    if (myTheme == null) {
      myTheme = new TmmLightDefaultTheme();
    }
    if ((myTheme != null) && (themesProps != null)) {
      myTheme.setUpColor();
      myTheme.setProperties(themesProps);
      myTheme.setUpColorArrs();
      AbstractLookAndFeel.setTheme(myTheme);
    }
  }

  public static void setCurrentTheme(Properties themesProps) {
    setTheme(themesProps);
  }

  public String getName() {
    return "tmmLight";
  }

  public String getID() {
    return "tmmLight";
  }

  public String getDescription() {
    return "The tinyMediaManager light Look and Feel";
  }

  public boolean isNativeLookAndFeel() {
    return false;
  }

  public boolean isSupportedLookAndFeel() {
    return true;
  }

  public AbstractBorderFactory getBorderFactory() {
    return LunaBorderFactory.getInstance();
  }

  public AbstractIconFactory getIconFactory() {
    return LunaIconFactory.getInstance();
  }

  protected void createDefaultTheme() {
    if (myTheme == null) {
      myTheme = new TmmLightDefaultTheme();
    }
    setTheme(myTheme);
  }

  protected void initComponentDefaults(UIDefaults table) {
    super.initComponentDefaults(table);
    table.put("ScrollBar.incrementButtonGap", new Integer(-1));
    table.put("ScrollBar.decrementButtonGap", new Integer(-1));
  }

  protected void initClassDefaults(UIDefaults table) {
    super.initClassDefaults(table);
    // @formatter:off
    Object[] uiDefaults = {
        // BaseLookAndFeel classes
        "LabelUI", BaseLabelUI.class.getName(), 
        "ToggleButtonUI", BaseToggleButtonUI.class.getName(), 
        "SeparatorUI", BaseSeparatorUI.class.getName(),
        "TextFieldUI", BaseTextFieldUI.class.getName(), 
        "TextAreaUI", BaseTextAreaUI.class.getName(), 
        "EditorPaneUI", BaseEditorPaneUI.class.getName(), 
        "PasswordFieldUI", BasePasswordFieldUI.class.getName(), 
        "CheckBoxUI", BaseCheckBoxUI.class.getName(),
        "RadioButtonUI", BaseRadioButtonUI.class.getName(), 
        "ToolTipUI", BaseToolTipUI.class.getName(), 
        "TreeUI", BaseTreeUI.class.getName(),
        "TableUI", BaseTableUI.class.getName(), 
        "SliderUI", BaseSliderUI.class.getName(), 
        "ProgressBarUI", BaseProgressBarUI.class.getName(),
        "ScrollPaneUI", BaseScrollPaneUI.class.getName(), 
        "SplitPaneUI", BaseSplitPaneUI.class.getName(),
        "FileChooserUI", BaseFileChooserUI.class.getName(), 
        "MenuBarUI", BaseMenuBarUI.class.getName(), 
        "MenuUI", BaseMenuUI.class.getName(),
        "PopupMenuUI", BasePopupMenuUI.class.getName(), 
        "MenuItemUI", BaseMenuItemUI.class.getName(), 
        "CheckBoxMenuItemUI", BaseCheckBoxMenuItemUI.class.getName(), 
        "RadioButtonMenuItemUI", BaseRadioButtonMenuItemUI.class.getName(), 
        "PopupMenuSeparatorUI", BaseSeparatorUI.class.getName(), 
        "DesktopPaneUI", BaseDesktopPaneUI.class.getName(),

        // LunaLookAndFeel classes
        "ButtonUI", LunaButtonUI.class.getName(), 
        "ComboBoxUI", LunaComboBoxUI.class.getName(), 
        "TableHeaderUI", LunaTableHeaderUI.class.getName(), 
        "ToolBarUI", LunaToolBarUI.class.getName(), 
        "InternalFrameUI", LunaInternalFrameUI.class.getName(), 
        "RootPaneUI", LunaRootPaneUI.class.getName(), 
        
        // TmmLookAndFeel classes
        "PanelUI", TmmLightPanelUI.class.getName(), 
        "ScrollBarUI", TmmLightScrollBarUI.class.getName(),
        "TabbedPaneUI", TmmLightTabbedPaneUI.class.getName(),
    };
    table.putDefaults(uiDefaults);
    // @formatter:on

    if (JTattooUtilities.getJavaVersion() >= 1.5) {
      table.put("FormattedTextFieldUI", BaseFormattedTextFieldUI.class.getName());
      table.put("SpinnerUI", BaseSpinnerUI.class.getName());
    }
  }
}
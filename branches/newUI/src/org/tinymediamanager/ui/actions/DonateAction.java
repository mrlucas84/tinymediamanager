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
package org.tinymediamanager.ui.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DonateAction to redirect to the donate page
 * 
 * @author Manuel Laggner
 */
public class DonateAction extends AbstractAction {
  private static final long   serialVersionUID = 1668251251156765161L;
  private static final Logger LOGGER           = LoggerFactory.getLogger(DonateAction.class);

  @Override
  public void actionPerformed(ActionEvent e) {
    try {
      String url = StringEscapeUtils
          .unescapeHtml4("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&amp;business=manuel%2elaggner%40gmail%2ecom&amp;lc=GB&amp;item_name=tinyMediaManager&amp;currency_code=EUR&amp;bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted");
      Desktop.getDesktop().browse(new URI(url));
    }
    catch (Exception e1) {
      LOGGER.error("Donate", e1);
    }
  }
}
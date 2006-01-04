/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * --LICENSE NOTICE--
 */

package org.radeox.macro;

import org.radeox.filter.interwiki.InterWiki;
import org.radeox.macro.parameter.MacroParameter;

import java.io.IOException;
import java.io.Writer;

/*
 * Macro that shows all know interwiki mappings.
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class InterWikiMacro extends BaseLocaleMacro {
    private String[] paramDescription =
     {"none"};

  public String[] getParamDescription() {
    return paramDescription;
  }

  public String getLocaleKey() {
    return "macro.interwiki";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {
    InterWiki interWiki = InterWiki.getInstance();
    interWiki.appendTo(writer);
    return;
  }
}

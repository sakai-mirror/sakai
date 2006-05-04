/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * --LICENSE NOTICE--
 */
package org.radeox.macro;

import org.radeox.macro.parameter.MacroParameter;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
 * MacroListMacro displays a list of all known macros of the EngineManager
 * with their name, parameters and a description.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class MacroListMacro extends BaseLocaleMacro {
  public String getLocaleKey() {
    return "macro.macrolist";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {
    if (params.getLength() == 0) {
      appendTo(writer);
    } else {
      throw new IllegalArgumentException("MacroListMacro: number of arguments does not match");
    }
  }

  public Writer appendTo(Writer writer) throws IOException {
    List macroList = MacroRepository.getInstance().getPlugins();
    Collections.sort(macroList);
    Iterator iterator = macroList.iterator();
    writer.write("{table}\n");
    writer.write("Macro|Description|Parameters\n");
    while (iterator.hasNext()) {
      Macro macro = (Macro) iterator.next();
      writer.write(macro.getName());
      writer.write("|");
      writer.write(macro.getDescription());
      writer.write("|");
      String[] params = macro.getParamDescription();
      if (params.length == 0) {
        writer.write("none");
      } else {
        for (int i = 0; i < params.length; i++) {
          String description = params[i];
          if (description.startsWith("?")) {
            writer.write(description.substring(1));
            writer.write(" (optional)");
          } else {
            writer.write(params[i]);
          }
          writer.write("\\\\");
        }
      }
      writer.write("\n");
    }
    writer.write("{table}");
    return writer;
  }

}

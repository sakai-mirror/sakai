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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.macro.parameter.MacroParameter;

import java.io.IOException;
import java.io.Writer;

/*
 * Displays a file path. This is used to store a filepath in an
 * OS independent way and then display the file path as needed.
 * This macro also solves the problems with to many backslashes
 * in Windows filepaths when they are entered in Snipsnap.
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class FilePathMacro extends LocalePreserved {
  private static Log log = LogFactory.getLog(FilePathMacro.class);

  private String[] paramDescription = {"1: file path"};

   public String getLocaleKey() {
    return "macro.filepath";
  }

  public FilePathMacro() {
    addSpecial('\\');
  }

  public String getDescription() {
    return "Displays a file system path. The file path should use slashes. Defaults to windows.";
  }

  public String[] getParamDescription() {
    return paramDescription;
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {

    if (params.getLength() == 1) {
      String path = params.get("0").replace('/', '\\');
      writer.write(replace(path));
    }
    return;
  }
}

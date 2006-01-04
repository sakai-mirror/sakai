/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
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
 */

/* 

 */


package org.apache.wsrp4j.log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log
{
    public final static String OUT_FILE = "wsrp4jout.log";
    public final static String ERR_FILE = "wsrp4jerr.log";

    public static PrintWriter out;
    public static PrintWriter err;

    static {
        try
        {
            FileWriter file;

            file = new FileWriter(OUT_FILE);
            out = new PrintWriter(file, true);

            file = new FileWriter(ERR_FILE);
            err = new PrintWriter(file, true);
        } catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }
}

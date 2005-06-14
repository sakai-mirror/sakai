/*
 * Created on Nov 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package org.sakaiproject.util;

import java.util.Iterator;


/**
 * @author haines
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ParameterParser {
    /**
     * A maximum lenght of a single header line that will be
     * parsed. (1024 bytes).
     */
    public static final int MAX_HEADER_SIZE = 1024;

    /**
     * HTTP header.
     */
    public static final String CONTENT_TYPE = "Content-type";

    /**
     * HTTP header.
     */
    public static final String CONTENT_DISPOSITION = "Content-disposition";

    /**
     * HTTP header base type.
     */
    public static final String MULTIPART = "multipart";

    /**
     * HTTP header base type modifier.
     */
    public static final String FORM_DATA = "form-data";

    /**
     * HTTP header base type modifier.
     */
    public static final String MIXED = "mixed";

    /**
     * HTTP header.
     */
    public static final String MULTIPART_FORM_DATA = MULTIPART + '/'
            + FORM_DATA;

    /**
     * HTTP header.
     */
    public static final String MULTIPART_MIXED = MULTIPART + '/' + MIXED;

    /**
     * Access the parameter names.
     * @return An Iterator of parameter names (String).
     */
    public abstract Iterator getNames();

    /**
     * Get a (String) parameter by name.
     * @param name The parameter name.
     * @return The parameter value, or null if it's not defined.
     */
    public abstract String get(String name);

    /**
     * Get a (String) parameter by name.
     * @param name The parameter name.
     * @return The parameter value, or null if it's not defined.
     */
    public abstract String getString(String name);

    /**
     * Get a (String[]) multi-valued parameter by name.
     * @param name The parameter name.
     * @return The parameter values array (of String), or null if it's not defined.
     */
    public abstract String[] getStrings(String name);

    /**
     * Get a boolean parameter by name.
     * @param name The parameter name.
     * @return The parameter boolean value, or false if it's not defined.
     */
    public abstract boolean getBoolean(String name);

    /**
     * Get an int parameter by name, with default.
     * @param name The parameter name.
     * @param deflt The default value.
     * @return The parameter int value, or the default if it's not defined or not int.
     */
    public abstract int getInt(String name, int deflt);

    /**
     * Get an int parameter by name.
     * @param name The parameter name.
     * @return The parameter int value, or 0 if it's not defined or not int.
     */
    public abstract int getInt(String name);

    /**
     * Get a FileItem parameter by name.
     * @param name The parameter name.
     * @return The parameter FileItem value, or null if it's not defined.
     */
    public abstract FileItem getFileItem(String name);

    /**
     * Clean the user input string of strange newlines, etc.
     * @param value The user input string.
     * @return value cleaned of string newlines, etc.
     */
    public abstract String getCleanString(String name);

    /**
     * Access the pathInfo, properly translated into Unicode.
     * @return The pathInfo, properly translated into Unicode.
     */
    public abstract String getPath();
}
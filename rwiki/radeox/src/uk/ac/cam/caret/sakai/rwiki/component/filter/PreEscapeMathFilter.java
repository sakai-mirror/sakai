package uk.ac.cam.caret.sakai.rwiki.component.filter;

import org.radeox.filter.CacheFilter;
import org.radeox.filter.FilterPipe;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.regex.RegexTokenFilter;
import org.radeox.regex.MatchResult;

public class PreEscapeMathFilter extends RegexTokenFilter implements CacheFilter {

    public PreEscapeMathFilter() {
        super("(?<!\\\\)(\\{[^:}]+(?::[^}]*)?\\})(.*?)\\{math}", SINGLELINE);
    }

    public void handleMatch(StringBuffer buffer, MatchResult result,
            FilterContext context) {
        buffer.append(result.group(1));

        escape(buffer, result.group(2));

        buffer.append("{math}");
    }

    public void escape(StringBuffer buffer, String toEscape) {
        // Get the character array as it is easier to use than the string
        char[] chars = toEscape.toCharArray();
        // maximal escape is 3 chars per normal char (i.e. all '\')
        char[] escaped = new char[chars.length * 3];

        // index in the escaped array
        int e = 0;

        int i = 0;

        while (i < chars.length) {
            char c = chars[i++];
            if (c == '\\') {
                // '\' requires an extra '\' to escape
                escaped[e++] = '\\';
            }
            // Escape the character
            escaped[e++] = '\\';
            escaped[e++] = c;
        }

        buffer.append(escaped, 0, e);
    }

    public String[] before() {
        return FilterPipe.FIRST_BEFORE;
    }

}

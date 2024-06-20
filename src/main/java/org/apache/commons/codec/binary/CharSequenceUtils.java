package org.apache.commons.codec.binary;

/**
 * Operations on {@link CharSequence} that are {@code null} safe.
 *
 * @see CharSequence
 * @since 1.10
 */
public class CharSequenceUtils {

    // Private constructor to prevent instantiation
    private CharSequenceUtils() {
        throw new AssertionError("This class should not be instantiated.");
    }

    /**
     * Green implementation of regionMatches.
     *
     * @param cs         the {@code CharSequence} to be processed
     * @param ignoreCase whether or not to be case-insensitive
     * @param thisStart  the index to start on the {@code cs} CharSequence
     * @param substring  the {@code CharSequence} to be looked for
     * @param start      the index to start on the {@code substring} CharSequence
     * @param length     character length of the region
     * @return whether the region matched
     */
    static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
                                 final CharSequence substring, final int start, final int length) {
        if (cs == null || substring == null) {
            return false;
        }

        if (thisStart < 0 || start < 0 || length < 0) {
            return false;
        }

        if (cs.length() - thisStart < length || substring.length() - start < length) {
            return false;
        }

        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }

        int index1 = thisStart;
        int index2 = start;
        int endIndex = thisStart + length;

        while (index1 < endIndex) {
            char c1 = cs.charAt(index1++);
            char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!ignoreCase) {
                return false;
            }

            // Handle case insensitivity
            if (Character.toLowerCase(c1) != Character.toLowerCase(c2) &&
                Character.toUpperCase(c1) != Character.toUpperCase(c2)) {
                return false;
            }
        }

        return true;
    }
}

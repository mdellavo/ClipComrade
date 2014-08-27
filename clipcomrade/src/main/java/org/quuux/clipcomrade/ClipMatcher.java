package org.quuux.clipcomrade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


class ClipMatcher {

    private final Pattern mPattern;
    private final MatchType mType;
    private List<Match> mMatches = new ArrayList<Match>();

    ClipMatcher(final MatchType type, final Pattern pattern) {
        mType = type;
        mPattern = pattern;
    }

    boolean match(final CharSequence text) {
        final java.util.regex.Matcher matcher = mPattern.matcher(text);

        mMatches.clear();

        while (matcher.find())
            mMatches.add(new Match(mType, matcher.group()));

        return mMatches.size() > 0;
    }

    List<Match> getMatches() {
        return mMatches;
    }

    enum MatchType {
        PHONE,
        WEB,
        EMAIL
    }

    static class Match implements Serializable {

        final MatchType type;
        final CharSequence text;

        Match(final MatchType type, final CharSequence text) {
            this.type = type;
            this.text = text;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || ((Object)this).getClass() != o.getClass()) return false;

            final Match match = (Match) o;

            if (!text.equals(match.text)) return false;
            if (type != match.type) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + text.hashCode();
            return result;
        }
    }
}

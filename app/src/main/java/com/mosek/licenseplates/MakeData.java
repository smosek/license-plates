package com.mosek.licenseplates;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Owner on 22/03/2018.
 */
class MakeData {
    private String make;
    private SortedSet<Integer> licensePrefixes;

    MakeData(String make) {
        this.make = make;
        this.licensePrefixes = new TreeSet();
    }

    public String getMake() {
        return make;
    }

    public Set<Integer> getLicensePrefixes() {
        return licensePrefixes;
    }

    public void addPrefix(int prefix) {
        licensePrefixes.add(prefix);
    }

    private class LicensePrefixStringBuilder {
        private SortedSet<Integer> prefixes;
        private Iterator<Integer> iterator;
        private StringBuilder sb;
        private int rangeStart = 0;
        private int rangeEnd = 0;
        private static final int RANGE_MAX_DIFF = 1;

        public LicensePrefixStringBuilder(SortedSet<Integer> prefixes) {
            this.prefixes = prefixes;
            buildString();
        }

        private void processValue(int value) {
            if (sb.length() == 0) {
                handleFirstValue(value);
            } else if (inCurrentRange(value)) {
                handleRangeValue(value);
            } else {
                handleOutOfRangeValue(value);
            }
        }

        private void handleOutOfRangeValue(int value) {
            endRangeIfNeeded();
            startRange(value);
        }

        private void endRangeIfNeeded() {
            if (rangeEnd > rangeStart) {
                endRange(rangeEnd);
            }
        }

        private void startRange(int value) {
            sb.append(",");
            sb.append(value);
            rangeStart = value;
            rangeEnd = value;
        }

        private void endRange(int rangeEnd) {
            sb.append("-");
            sb.append(rangeEnd);
        }

        private void handleRangeValue(int value) {
            rangeEnd = value;
        }

        private boolean inCurrentRange(int value) {
            return value <= rangeEnd + RANGE_MAX_DIFF;
        }

        private void handleFirstValue(int value) {
            sb.append(value);
            rangeStart = value;
            rangeEnd = value;
        }

        private void buildString() {
            sb = new StringBuilder();
            for (Integer value : prefixes) {
                processValue(value);
            }
            endRangeIfNeeded();
        }

        public String getString() {
            return sb.toString();
        }
    }

    public String getLicensePrefixesString() {
        LicensePrefixStringBuilder builder = new LicensePrefixStringBuilder(licensePrefixes);
        return builder.getString();
    }
}

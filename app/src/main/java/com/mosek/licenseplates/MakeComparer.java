package com.mosek.licenseplates;

import java.util.HashSet;
import java.util.Set;

class MakeComparer {
    MakeData firstMake;
    MakeData secondMake;

    public MakeComparer(MakeData firstMake, MakeData secondMake) {
        this.firstMake = firstMake;
        this.secondMake = secondMake;
    }

    public double compare() {
        Set<Integer> firstPrefixes = firstMake.getLicensePrefixes();
        Set<Integer> secondPrefixes = secondMake.getLicensePrefixes();
        int nFirst = firstPrefixes.size();
        int nSecond = secondPrefixes.size();
        Set<Integer> shared = new HashSet<Integer>(firstPrefixes);
        shared.retainAll(secondPrefixes);
        int nShared = shared.size();
        return calculateMatch(nFirst, nSecond, nShared);
    }

    private double calculateMatch(int nFirst, int nSecond, int nShared) {
        double pctFirst = (double)nShared / nFirst;
        double pctSecond = (double)nShared / nSecond;
        return ((pctFirst * nSecond) + (pctSecond * nFirst)) / (nFirst + nSecond);
    }
}

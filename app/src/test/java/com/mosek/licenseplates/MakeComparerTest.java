package com.mosek.licenseplates;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by Owner on 22/03/2018.
 */
public class MakeComparerTest {
    @Test
    public void compareIdentical() throws Exception {
        runCompareTest(new Integer[] {123, 124, 125, 126, 127, 128}, new Integer[] {123, 124, 125, 126, 127, 128}, 1);
    }

    @Test
    public void compareSimilarLongAndShort() throws Exception {
        runCompareTest(new Integer[] {123, 124, 125, 126, 127, 128}, new Integer[] {126, 127}, 0.8333);
    }

    @Test
    public void compareNotSoSimilarLongAndShort() throws Exception {
        runCompareTest(new Integer[] {123, 124, 125, 126, 127, 128}, new Integer[] {127, 128, 129, 130}, 0.4333);
    }

    @Test
    public void compareDifferent() throws  Exception {
        runCompareTest(new Integer[] {123, 124, 125, 501}, new Integer[] {201, 202, 203, 501}, 0.25);
    }

    @Test
    public void compareCompletelyDifferent() throws  Exception {
        runCompareTest(new Integer[] {123, 124, 125}, new Integer[] {201, 202, 203}, 0);
    }

    private void runCompareTest(Integer[] one, Integer[] two, double expected) {
        MakeData makeOne = new MakeData("One");
        MakeData makeTwo = new MakeData("Two");
        for (int i : one) makeOne.addPrefix(i);
        for (int i : two) makeTwo.addPrefix(i);
        MakeComparer comparer = new MakeComparer(makeOne, makeTwo);
        assertEquals(expected, comparer.compare(), 0.001);

    }


}
package net.automatalib.util.automata.oca;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.automatalib.automata.oca.DefaultROCA;
import net.automatalib.automata.oca.ROCALocation;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.Alphabets;

public class ROCAEquivalenceTest {
    @Test(timeOut = 1000)
    public void testTermination() {
        // We take a simple complete ROCA that rejects every word
        Alphabet<Character> alphabet = Alphabets.singleton('a');
        DefaultROCA<Character> roca = new DefaultROCA<>(alphabet);

        ROCALocation q0 = roca.addInitialLocation(false);

        roca.addSuccessor(q0, 0, 'a', 0, q0);

        Assert.assertNull(OCAUtil.findSeparatingWord(roca, roca, alphabet));
        Assert.assertTrue(OCAUtil.testEquivalence(roca, roca, alphabet));
    }

    @Test(timeOut = 1000)
    public void testEquivalenceWithSelf() {
        Alphabet<Character> alphabet = Alphabets.characters('a', 'b');
        DefaultROCA<Character> roca = new DefaultROCA<>(alphabet);

        ROCALocation q0 = roca.addInitialLocation(false);
        ROCALocation q1 = roca.addLocation(true);

        roca.addSuccessor(q0, 0, 'a', +1, q0);
        roca.addSuccessor(q0, 1, 'a', +1, q0);
        roca.addSuccessor(q0, 1, 'b', 0, q1);

        roca.addSuccessor(q1, 0, 'a', 0, q1);
        roca.addSuccessor(q1, 1, 'a', -1, q1);

        Assert.assertTrue(OCAUtil.testEquivalence(roca, roca, alphabet));
        Assert.assertNull(OCAUtil.findSeparatingWord(roca, roca, alphabet));
    }

    @Test(timeOut = 1000)
    public void testEquivalenceSameLanguage() {
        // The language of both ROCAs is {a^n b a^m | 0 < n <= m}
        Alphabet<Character> alphabet = Alphabets.characters('a', 'b');
        DefaultROCA<Character> roca1 = new DefaultROCA<>(alphabet);

        ROCALocation q0_1 = roca1.addInitialLocation(false);
        ROCALocation q1_1 = roca1.addLocation(true);
        ROCALocation q2_1 = roca1.addLocation(false);

        roca1.addSuccessor(q0_1, 0, 'a', +1, q0_1);
        roca1.addSuccessor(q0_1, 0, 'b', 0, q2_1);
        roca1.addSuccessor(q0_1, 1, 'a', +1, q0_1);
        roca1.addSuccessor(q0_1, 1, 'b', 0, q1_1);

        roca1.addSuccessor(q1_1, 0, 'a', 0, q1_1);
        roca1.addSuccessor(q1_1, 0, 'b', 0, q2_1);
        roca1.addSuccessor(q1_1, 1, 'a', -1, q1_1);
        roca1.addSuccessor(q1_1, 1, 'b', 0, q2_1);

        roca1.addSuccessor(q2_1, 0, 'a', 0, q2_1);
        roca1.addSuccessor(q2_1, 0, 'b', 0, q2_1);
        roca1.addSuccessor(q2_1, 1, 'a', 0, q2_1);
        roca1.addSuccessor(q2_1, 1, 'b', 0, q2_1);

        DefaultROCA<Character> roca2 = new DefaultROCA<>(alphabet);
        
        ROCALocation q0_2 = roca2.addInitialLocation(false);
        ROCALocation q1_2 = roca2.addLocation(false);
        ROCALocation q2_2 = roca2.addLocation(false);
        ROCALocation q3_2 = roca2.addLocation(true);
        ROCALocation q4_2 = roca2.addLocation(false);
        ROCALocation q5_2 = roca2.addLocation(false);
        ROCALocation q6_2 = roca2.addLocation(false);
        ROCALocation q7_2 = roca2.addLocation(false);
        ROCALocation q8_2 = roca2.addLocation(false);

        roca2.addSuccessor(q0_2, 0, 'a', 0, q1_2);

        roca2.addSuccessor(q1_2, 0, 'a', +1, q4_2);
        roca2.addSuccessor(q1_2, 0, 'b', 0, q2_2);

        roca2.addSuccessor(q2_2, 0, 'a', 0, q3_2);
        roca2.addSuccessor(q2_2, 1, 'a', -1, q7_2);

        roca2.addSuccessor(q3_2, 0, 'a', 0, q3_2);

        roca2.addSuccessor(q4_2, 1, 'a', 0, q5_2);
        roca2.addSuccessor(q4_2, 1, 'b', 0, q2_2);

        roca2.addSuccessor(q5_2, 1, 'a', +1, q6_2);
        roca2.addSuccessor(q5_2, 1, 'b', 0, q8_2);

        roca2.addSuccessor(q6_2, 1, 'a', 0, q5_2);
        roca2.addSuccessor(q6_2, 1, 'b', 0, q2_2);

        roca2.addSuccessor(q7_2, 0, 'a', 0, q3_2);
        roca2.addSuccessor(q7_2, 1, 'a', 0, q2_2);

        roca2.addSuccessor(q8_2, 1, 'a', 0, q2_2);

        Assert.assertNull(OCAUtil.findSeparatingWord(roca1, roca2, alphabet));
        Assert.assertTrue(OCAUtil.testEquivalence(roca1, roca2, alphabet));
    }

    @Test(timeOut = 1000)
    public void testEquivalenceEmptyAndFull() {
        // L_1 = {a^n | n >= 0}
        // L_2 = {}
        Alphabet<Character> alphabet = Alphabets.singleton('a');
        DefaultROCA<Character> roca1 = new DefaultROCA<>(alphabet);
        
        ROCALocation q0_1 = roca1.addInitialLocation(true);

        roca1.addSuccessor(q0_1, 0, 'a', 0, q0_1);

        DefaultROCA<Character> roca2 = new DefaultROCA<>(alphabet);

        ROCALocation q0_2 = roca2.addInitialLocation(false);

        roca2.addSuccessor(q0_2, 0, 'a', 0, q0_2);

        Assert.assertNotNull(OCAUtil.findSeparatingWord(roca1, roca2, alphabet));
        Assert.assertFalse(OCAUtil.testEquivalence(roca1, roca2, alphabet));
    }

    @Test(timeOut = 1000)
    public void testNonEquivalence() {
        // L_1 = {a^n b^{n+1} a | n > 0}
        // L_2 = {a^n b^{n+1} | n > 0}
        Alphabet<Character> alphabet = Alphabets.characters('a', 'b');
        DefaultROCA<Character> roca1 = new DefaultROCA<>(alphabet);
        
        ROCALocation q0_1 = roca1.addInitialLocation(false);
        ROCALocation q1_1 = roca1.addLocation(false);
        ROCALocation q2_1 = roca1.addLocation(true);

        roca1.addSuccessor(q0_1, 0, 'a', +1, q0_1);
        roca1.addSuccessor(q0_1, 1, 'a', +1, q0_1);
        roca1.addSuccessor(q0_1, 1, 'b', 0, q1_1);

        roca1.addSuccessor(q1_1, 0, 'a', 0, q2_1);
        roca1.addSuccessor(q1_1, 1, 'b', -1, q1_1);

        DefaultROCA<Character> roca2 = new DefaultROCA<>(alphabet);

        ROCALocation q0_2 = roca2.addInitialLocation(false);
        ROCALocation q1_2 = roca2.addLocation(false);

        roca2.addSuccessor(q0_2, 0, 'a', +1, q0_2);
        roca2.addSuccessor(q0_2, 1, 'a', +1, q0_2);
        roca2.addSuccessor(q0_2, 1, 'b', 0, q1_2);

        roca2.addSuccessor(q1_2, 1, 'b', -1, q1_2);

        Assert.assertNotNull(OCAUtil.findSeparatingWord(roca1, roca2, alphabet));
        Assert.assertFalse(OCAUtil.testEquivalence(roca1, roca2, alphabet));
    }

    @Test(timeOut = 1000)
    public void testCompleteAndNonComplete() {
        Alphabet<Character> alphabet = Alphabets.characters('a', 'b');
        DefaultROCA<Character> complete = new DefaultROCA<>(alphabet);

        ROCALocation q0_1 = complete.addInitialLocation(false);
        ROCALocation q1_1 = complete.addLocation(true);
        ROCALocation q2_1 = complete.addLocation(false);

        complete.setSuccessor(q0_1, 0, 'a', +1, q0_1);
        complete.setSuccessor(q0_1, 0, 'b', 0, q2_1);
        complete.setSuccessor(q0_1, 1, 'a', +1, q0_1);
        complete.setSuccessor(q0_1, 1, 'b', -1, q1_1);

        complete.setSuccessor(q1_1, 0, 'a', 0, q2_1);
        complete.setSuccessor(q1_1, 0, 'b', 0, q1_1);
        complete.setSuccessor(q1_1, 1, 'a', 0, q2_1);
        complete.setSuccessor(q1_1, 1, 'b', -1, q1_1);

        complete.setSuccessor(q2_1, 0, 'a', 0, q2_1);
        complete.setSuccessor(q2_1, 0, 'b', 0, q2_1);
        complete.setSuccessor(q2_1, 1, 'a', 0, q2_1);
        complete.setSuccessor(q2_1, 1, 'b', 0, q2_1);

        DefaultROCA<Character> nonComplete = new DefaultROCA<>(alphabet);

        ROCALocation q0_2 = nonComplete.addInitialLocation(false);
        ROCALocation q1_2 = nonComplete.addLocation(true);

        nonComplete.setSuccessor(q0_2, 0, 'a', +1, q0_2);
        nonComplete.setSuccessor(q0_2, 1, 'a', +1, q0_2);
        nonComplete.setSuccessor(q0_2, 1, 'b', -1, q1_2);

        nonComplete.setSuccessor(q1_2, 0, 'b', 0, q1_2);
        nonComplete.setSuccessor(q1_2, 1, 'b', -1, q1_2);

        Assert.assertNull(OCAUtil.findSeparatingWord(complete, nonComplete, alphabet));
        Assert.assertTrue(OCAUtil.testEquivalence(complete, nonComplete, alphabet));

        DefaultROCA<Character> nonEquivalent = new DefaultROCA<>(alphabet);

        ROCALocation q0_3 = nonEquivalent.addInitialLocation(false);
        ROCALocation q1_3 = nonEquivalent.addLocation(true);

        nonEquivalent.setSuccessor(q0_3, 0, 'a', +1, q0_3);
        nonEquivalent.setSuccessor(q0_3, 1, 'a', +1, q0_3);
        nonEquivalent.setSuccessor(q0_3, 1, 'b', 0, q1_3);

        nonEquivalent.setSuccessor(q1_3, 0, 'b', 0, q1_3);
        nonEquivalent.setSuccessor(q1_3, 1, 'b', -1, q1_3);

        Assert.assertNotNull(OCAUtil.findSeparatingWord(complete, nonEquivalent, alphabet));
        Assert.assertNotNull(OCAUtil.findSeparatingWord(nonComplete, nonEquivalent, alphabet));
        Assert.assertFalse(OCAUtil.testEquivalence(complete, nonEquivalent, alphabet));
        Assert.assertFalse(OCAUtil.testEquivalence(nonComplete, nonEquivalent, alphabet));
    }

    @Test(timeOut = 1000)
    public void testOneFunctionAndTwoFunctions() {
        // Both automata accept L = {epsilon}.
        // The second automata increments its counter value, while the first never changes it.
        Alphabet<Character> alphabet = Alphabets.characters('a', 'b');
        DefaultROCA<Character> oneFunction = new DefaultROCA<>(alphabet);

        ROCALocation q0_1 = oneFunction.addInitialLocation(true);
        ROCALocation q1_1 = oneFunction.addLocation(false);

        oneFunction.setSuccessor(q0_1, 0, 'a', 0, q1_1);
        oneFunction.setSuccessor(q0_1, 0, 'b', 0, q1_1);

        oneFunction.setSuccessor(q1_1, 0, 'a', 0, q1_1);
        oneFunction.setSuccessor(q1_1, 0, 'b', 0, q1_1);

        DefaultROCA<Character> twoFunctions = new DefaultROCA<>(alphabet);

        ROCALocation q0_2 = twoFunctions.addInitialLocation(true);
        ROCALocation q1_2 = twoFunctions.addLocation(false);

        twoFunctions.setSuccessor(q0_2, 0, 'a', +1, q0_2);
        twoFunctions.setSuccessor(q0_2, 1, 'a', +1, q0_2);
        twoFunctions.setSuccessor(q0_2, 1, 'b', -1, q1_2);

        twoFunctions.setSuccessor(q1_2, 1, 'b', -1, q1_2);

        Assert.assertNull(OCAUtil.findSeparatingWord(oneFunction, twoFunctions, alphabet));
        Assert.assertTrue(OCAUtil.testEquivalence(oneFunction, twoFunctions, alphabet));
    }
}

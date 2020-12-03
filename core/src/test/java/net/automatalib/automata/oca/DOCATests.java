package net.automatalib.automata.oca;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

/**
 * @author Gaëtan Staquet
 */
public class DOCATests {
    private DefaultDOCA<Character> buildDOCA(AcceptanceMode mode) {
        // If mode == ACCEPTING_LOCATION:
        // L = {a^n b^n | n > 0} U {a^n b^m c b* | 0 < m < n}
        // If mode == COUNTER_ZERO:
        // L = {epsilon} U {a^n b^n | n > 0} U {a^n b^m c b^l | n > 0, m > 0 and m + l = n}
        // If mode == BOTH:
        // L = {a^n b^n | n > 0} U {a^n b^m c b^l | n > 0 and m + l = n}
        Alphabet<Character> alphabet = Alphabets.characters('a', 'c');
        DefaultDOCA<Character> doca = new DefaultDOCA<>(alphabet, mode);

        DOCALocation q0 = doca.addInitialLocation(false);
        DOCALocation q1 = doca.addLocation(false);
        DOCALocation q2 = doca.addLocation(true);
        DOCALocation q3 = doca.addLocation(true);

        doca.setSuccessor(q0, 0, 'a', +1, q0);
        doca.setSuccessor(q0, 1, 'a', +1, q0);
        doca.setSuccessor(q0, 1, 'b', -1, q1);

        doca.setEpsilonSuccessor(q1, 0, 0, q2);
        doca.setSuccessor(q1, 1, 'b', -1, q1);
        doca.setSuccessor(q1, 1, 'c', 0, q3);

        doca.setSuccessor(q3, 1, 'b', -1, q3);

        return doca;
    }

    @Test
    public void testAcceptanceAccepting() {
        DefaultDOCA<Character> doca = buildDOCA(AcceptanceMode.ACCEPTING_LOCATION);

        Assert.assertTrue(doca.accepts(Word.fromString("ab")));
        Assert.assertTrue(doca.accepts(Word.fromString("aaabcbb")));
        Assert.assertTrue(doca.accepts(Word.fromString("aaabc")));
        Assert.assertTrue(doca.accepts(Word.fromString("aaabbb")));
        Assert.assertTrue(doca.accepts(Word.fromString("aaabbcb")));

        Assert.assertFalse(doca.accepts(Word.epsilon()));
        Assert.assertFalse(doca.accepts(Word.fromString("aaa")));
        Assert.assertFalse(doca.accepts(Word.fromString("abc")));
        Assert.assertFalse(doca.accepts(Word.fromString("aab")));
        Assert.assertFalse(doca.accepts(Word.fromString("abcb")));
    }

    @Test
    public void testAcceptanceZero() {
        DefaultDOCA<Character> doca = buildDOCA(AcceptanceMode.COUNTER_ZERO);

        Assert.assertTrue(doca.accepts(Word.epsilon()));
        Assert.assertTrue(doca.accepts(Word.fromString("ab")));
        Assert.assertTrue(doca.accepts(Word.fromString("aaabcbb")));
        Assert.assertTrue(doca.accepts(Word.fromString("aaabbb")));
        Assert.assertTrue(doca.accepts(Word.fromString("aaabbcb")));

        Assert.assertFalse(doca.accepts(Word.fromString("aaa")));
        Assert.assertFalse(doca.accepts(Word.fromString("abc")));
        Assert.assertFalse(doca.accepts(Word.fromString("abcb")));
        Assert.assertFalse(doca.accepts(Word.fromString("aab")));
    }

    @Test
    public void testAcceptanceBoth() {
        DefaultDOCA<Character> doca = buildDOCA(AcceptanceMode.BOTH);

        Assert.assertTrue(doca.accepts(Word.fromString("ab")));
        Assert.assertTrue(doca.accepts(Word.fromString("aaabcbb")));
        Assert.assertTrue(doca.accepts(Word.fromString("aaabbb")));
        Assert.assertTrue(doca.accepts(Word.fromString("aaabbcb")));

        Assert.assertFalse(doca.accepts(Word.epsilon()));
        Assert.assertFalse(doca.accepts(Word.fromString("aaa")));
        Assert.assertFalse(doca.accepts(Word.fromString("abc")));
        Assert.assertFalse(doca.accepts(Word.fromString("abcb")));
        Assert.assertFalse(doca.accepts(Word.fromString("aab")));
    }
}

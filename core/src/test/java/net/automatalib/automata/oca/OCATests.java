package net.automatalib.automata.oca;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

/**
 * @author Gaëtan Staquet
 */
public class OCATests {
    private DefaultOCA<Character> buildOCA(AcceptanceMode mode) {
        // If mode == ACCEPTING_LOCATION:
        // L = {a^n b* c* : n > 1}
        // If mode == COUNTER_ZERO:
        // L = {epsilon} U {a^{n+1} b^m c^l : n > 1 and m + l = n}
        // If mode == BOTH:
        // L = {a^{n+1} b^m c^l : n > 1 and m + l = n}
        Alphabet<Character> alphabet = Alphabets.characters('a', 'c');
        DefaultOCA<Character> oca = new DefaultOCA<>(alphabet, mode);

        OCALocation q0 = oca.addInitialLocation(false);
        OCALocation q1 = oca.addLocation(true);
        OCALocation q2 = oca.addLocation(true);

        oca.addSuccessor(q0, 0, 'a', +1, q0);
        oca.addSuccessor(q0, 1, 'a', +1, q0);
        oca.addSuccessor(q0, 1, 'a', 0, q1);

        oca.addSuccessor(q1, 1, 'b', -1, q1);
        oca.addEpsilonSuccessor(q1, 1, 0, q2);

        oca.addSuccessor(q2, 1, 'c', -1, q2);

        return oca;
    }

    @Test
    public void testAcceptanceAccepting() {
        DefaultOCA<Character> oca = buildOCA(AcceptanceMode.ACCEPTING_LOCATION);

        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aa")));
        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aab")));
        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aac")));
        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aaabc")));
        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aaaabcc")));

        Assert.assertFalse(oca.accepts(Word.epsilon()));
        Assert.assertFalse(oca.accepts(Word.fromCharSequence("abc")));
        Assert.assertFalse(oca.accepts(Word.fromCharSequence("aabb")));
        Assert.assertFalse(oca.accepts(Word.fromCharSequence("aaaaccbb")));
    }

    @Test
    public void testAcceptanceZero() {
        DefaultOCA<Character> oca = buildOCA(AcceptanceMode.COUNTER_ZERO);

        Assert.assertTrue(oca.accepts(Word.epsilon()));
        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aab")));
        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aac")));
        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aaabc")));
        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aaaabcc")));

        Assert.assertFalse(oca.accepts(Word.fromCharSequence("abc")));
        Assert.assertFalse(oca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(oca.accepts(Word.fromCharSequence("aabb")));
        Assert.assertFalse(oca.accepts(Word.fromCharSequence("aacc")));
        Assert.assertFalse(oca.accepts(Word.fromCharSequence("aabc")));
        Assert.assertFalse(oca.accepts(Word.fromCharSequence("aaaaccbb")));
    }

    @Test
    public void testAcceptanceBoth() {
        DefaultOCA<Character> oca = buildOCA(AcceptanceMode.BOTH);

        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aab")));
        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aac")));
        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aaabc")));
        Assert.assertTrue(oca.accepts(Word.fromCharSequence("aaaabcc")));

        Assert.assertFalse(oca.accepts(Word.epsilon()));
        Assert.assertFalse(oca.accepts(Word.fromCharSequence("abc")));
        Assert.assertFalse(oca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(oca.accepts(Word.fromCharSequence("aaaaccbb")));
    }
}

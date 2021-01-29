package net.automatalib.automata.oca;

import java.util.LinkedList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.automatalib.commons.util.Pair;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

public class ROCATests {

    DefaultROCA<Character> buildROCA(final AcceptanceMode acceptanceMode) {
        // If mode == ACCEPTING_LOCATION:
        // L = {a^n b^n a | n > 0} U {a^n b^m a b^* | 0 < m < n}
        // If mode == COUNTER_ZERO:
        // L = {epsilon} U {a^n b^n a | n > 0} U {a^n b^m a b^l | 0 < n, m, l and n = m + l}
        // If mode == BOTH:
        // L = {a^n b^n a | n > 0} U {a^n b^m a b^l | 0 < n, m, l and n = m + l}
        Alphabet<Character> alphabet = Alphabets.characters('a', 'b');
        DefaultROCA<Character> roca = new DefaultROCA<>(alphabet, acceptanceMode);

        ROCALocation q0 = roca.addInitialLocation(false);
        ROCALocation q1 = roca.addLocation(false);
        ROCALocation q2 = roca.addLocation(true);
        ROCALocation q3 = roca.addLocation(true);

        roca.addSuccessor(q0, 0, 'a', +1, q0);
        roca.addSuccessor(q0, 1, 'a', +1, q0);
        roca.addSuccessor(q0, 1, 'b', -1, q1);

        roca.addSuccessor(q1, 0, 'a', 0, q2);
        roca.addSuccessor(q1, 1, 'b', -1, q1);
        roca.addSuccessor(q1, 1, 'a', 0, q3);

        roca.addSuccessor(q3, 1, 'b', -1, q3);

        return roca;
    }

    @Test
    public void testAcceptanceAccepting() {
        DefaultROCA<Character> roca = buildROCA(AcceptanceMode.ACCEPTING_LOCATION);

        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aabba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aabab")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aaaababbb")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aaaabab")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aaba")));

        Assert.assertFalse(roca.accepts(Word.epsilon()));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aab")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("abab")));
    }
    
    @Test
    public void testAcceptanceZero() {
        DefaultROCA<Character> roca = buildROCA(AcceptanceMode.COUNTER_ZERO);

        Assert.assertTrue(roca.accepts(Word.epsilon()));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aabba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aabab")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aaaababbb")));

        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aab")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aaba")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("abab")));
    }
    
    @Test
    public void testAcceptanceBoth() {
        DefaultROCA<Character> roca = buildROCA(AcceptanceMode.BOTH);

        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aabba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aabab")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aaaababbb")));

        Assert.assertFalse(roca.accepts(Word.epsilon()));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aab")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aaba")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("abab")));
    }

    @Test
    public void toVCAConstruction() {
        DefaultROCA<Character> roca = buildROCA(AcceptanceMode.BOTH);

        DefaultVCA<Pair<Character, Integer>> vca = roca.toVCA();

        List<Pair<Character, Integer>> word = new LinkedList<>();
        Assert.assertFalse(vca.accepts(word));

        word.add(Pair.of('a', +1));
        word.add(Pair.of('b', -1));
        word.add(Pair.of('a', 0));
        Assert.assertTrue(vca.accepts(word));

        word.clear();
        word.add(Pair.of('a', +1));
        word.add(Pair.of('a', +1));
        word.add(Pair.of('a', +1));
        word.add(Pair.of('b', -1));
        word.add(Pair.of('b', -1));
        word.add(Pair.of('a', 0));
        word.add(Pair.of('b', -1));
        Assert.assertTrue(vca.accepts(word));

        word.clear();
        word.add(Pair.of('a', +1));
        word.add(Pair.of('a', +1));
        word.add(Pair.of('a', +1));
        word.add(Pair.of('a', +1));
        word.add(Pair.of('b', -1));
        word.add(Pair.of('a', 0));
        Assert.assertFalse(vca.accepts(word));
    }
}

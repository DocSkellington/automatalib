package net.automatalib.util.automata.oca;

import java.util.LinkedList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.automatalib.automata.oca.AcceptanceMode;
import net.automatalib.automata.oca.DFAWithCounterValues;
import net.automatalib.automata.oca.DefaultROCA;
import net.automatalib.automata.oca.DefaultVCA;
import net.automatalib.automata.oca.ROCALocation;
import net.automatalib.commons.util.Pair;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

public class ROCAToRestrictedAutomatonTest {

    private DefaultROCA<Character> buildROCA() {
        Alphabet<Character> alphabet = Alphabets.characters('a', 'b');
        DefaultROCA<Character> roca = new DefaultROCA<>(alphabet, AcceptanceMode.BOTH);

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
    public void VCAToRestrictedAutomaton() {
        DefaultROCA<Character> roca = buildROCA();
        DefaultVCA<Pair<Character, Integer>> vca = roca.toVCA();

        DFAWithCounterValues<Pair<Character, Integer>> dfa = OCAUtil.constructRestrictedAutomaton(vca, 2);

        Assert.assertEquals(dfa.size(), 8);

        List<Pair<Character, Integer>> word = new LinkedList<>();

        word.add(Pair.of('a', +1));
        Assert.assertFalse(dfa.accepts(word)); // a
        word.add(Pair.of('a', +1));
        Assert.assertFalse(dfa.accepts(word)); // aa
        word.add(Pair.of('a', +1));
        Assert.assertFalse(dfa.accepts(word)); // aaa
        word.remove(word.size() - 1);
        word.add(Pair.of('b', -1));
        Assert.assertFalse(dfa.accepts(word)); // aab
        word.add(Pair.of('a', 0));
        Assert.assertFalse(dfa.accepts(word)); // aaba
        word.add(Pair.of('a', 0));
        Assert.assertFalse(dfa.accepts(word)); // aabaa
        word.remove(word.size() - 1);
        word.add(Pair.of('b', -1));
        Assert.assertTrue(dfa.accepts(word));  // aabab
    }
    
    @Test
    public void ROCAToRestrictedAutomaton() {
        DefaultROCA<Character> roca = buildROCA();

        DFAWithCounterValues<Character> dfa = OCAUtil.constructRestrictedAutomaton(roca, 2);

        // If we minimize the configuration graph constructed from the VCA, we should have 7 states
        Assert.assertEquals(dfa.size(), 8);

        Assert.assertTrue(dfa.accepts(Word.fromCharSequence("aba")));
        Assert.assertTrue(dfa.accepts(Word.fromCharSequence("aabba")));
        Assert.assertTrue(dfa.accepts(Word.fromCharSequence("aabab")));

        Assert.assertFalse(dfa.accepts(Word.fromCharSequence("a")));
        Assert.assertFalse(dfa.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(dfa.accepts(Word.fromCharSequence("aaa")));
        Assert.assertFalse(dfa.accepts(Word.fromCharSequence("aab")));
        Assert.assertFalse(dfa.accepts(Word.fromCharSequence("aaba")));
        Assert.assertFalse(dfa.accepts(Word.fromCharSequence("aabb")));
        Assert.assertFalse(dfa.accepts(Word.fromCharSequence("ab")));
    }
}

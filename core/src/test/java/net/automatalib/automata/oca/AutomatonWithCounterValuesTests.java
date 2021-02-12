package net.automatalib.automata.oca;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.automatalib.automata.oca.automatoncountervalues.AcceptingOrExit;
import net.automatalib.automata.oca.automatoncountervalues.AutomatonWithCounterValues;
import net.automatalib.automata.oca.automatoncountervalues.DefaultAutomatonWithCounterValues;
import net.automatalib.automata.oca.automatoncountervalues.DefaultAutomatonWithCounterValuesState;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

public class AutomatonWithCounterValuesTests {

    private DefaultAutomatonWithCounterValues<Character> buildAutomatonPeriodOne() {
        Alphabet<Character> alphabet = Alphabets.characters('a', 'b');
        DefaultAutomatonWithCounterValues<Character> automaton = new DefaultAutomatonWithCounterValues<>(alphabet);

        DefaultAutomatonWithCounterValuesState q0 = automaton.addInitialState(AcceptingOrExit.REJECTING, 0);
        DefaultAutomatonWithCounterValuesState q1 = automaton.addState(AcceptingOrExit.REJECTING, 1);
        DefaultAutomatonWithCounterValuesState q2 = automaton.addState(AcceptingOrExit.REJECTING, 2);
        DefaultAutomatonWithCounterValuesState q3 = automaton.addState(AcceptingOrExit.EXIT, 3);
        DefaultAutomatonWithCounterValuesState q4 = automaton.addState(AcceptingOrExit.ACCEPTING, 0);
        DefaultAutomatonWithCounterValuesState q5 = automaton.addState(AcceptingOrExit.REJECTING, 1);
        DefaultAutomatonWithCounterValuesState q6 = automaton.addState(AcceptingOrExit.REJECTING, 2);

        automaton.setTransition(q0, 'a', q1);
        automaton.setTransition(q0, 'b', q4);

        automaton.setTransition(q1, 'a', q2);
        automaton.setTransition(q1, 'b', q5);

        automaton.setTransition(q2, 'a', q3);
        automaton.setTransition(q2, 'b', q6);

        automaton.setTransition(q6, 'a', q5);

        automaton.setTransition(q5, 'a', q4);

        return automaton;
    }

    private DefaultAutomatonWithCounterValues<Character> buildAutomatonPeriodTwo() {
        Alphabet<Character> alphabet = Alphabets.characters('a', 'b');
        DefaultAutomatonWithCounterValues<Character> automaton = new DefaultAutomatonWithCounterValues<>(alphabet);

        DefaultAutomatonWithCounterValuesState[] states = new DefaultAutomatonWithCounterValuesState[11];
        for (int i = 0; i < states.length; i++) {
            if (i == 0) {
                states[0] = automaton.addInitialState(AcceptingOrExit.REJECTING, 0);
            } else if (i == 10) {
                states[i] = automaton.addState(AcceptingOrExit.EXIT, 5);
            } else if (i == 5) {
                states[i] = automaton.addState(AcceptingOrExit.ACCEPTING, 0);
            } else {
                states[i] = automaton.addState(AcceptingOrExit.REJECTING, i % 5);
            }
        }

        for (int i = 0; i <= 3; i++) {
            automaton.setTransition(states[i], 'a', states[i + 1]);
        }
        for (int i = 9; i >= 6; i--) {
            automaton.setTransition(states[i], 'a', states[i - 1]);
        }
        for (int i = 0; i <= 4; i++) {
            if (i % 2 == 0) {
                automaton.setTransition(states[i], 'b', states[i + 5]);
            } else {
                automaton.setTransition(states[i + 5], 'b', states[i]);
            }
        }
        automaton.setTransition(states[1], 'b', states[1]);
        automaton.setTransition(states[3], 'b', states[3]);
        automaton.setTransition(states[5], 'a', states[5]);
        automaton.setTransition(states[5], 'b', states[5]);
        automaton.setTransition(states[7], 'b', states[7]);
        automaton.setTransition(states[9], 'b', states[9]);

        automaton.setTransition(states[4], 'a', states[10]);

        return automaton;
    }

    @Test
    public void testAcceptance() {
        DefaultAutomatonWithCounterValues<Character> automaton = buildAutomatonPeriodOne();

        Assert.assertEquals(automaton.computeOutput(Word.epsilon()), AcceptingOrExit.REJECTING);

        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("a")), AcceptingOrExit.REJECTING);
        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("b")), AcceptingOrExit.ACCEPTING);

        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aa")), AcceptingOrExit.REJECTING);
        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("ab")), AcceptingOrExit.REJECTING);

        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aaa")), AcceptingOrExit.EXIT);
        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aab")), AcceptingOrExit.REJECTING);

        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aaaa")), AcceptingOrExit.EXIT);
        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aaab")), AcceptingOrExit.EXIT);

        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aaba")), AcceptingOrExit.REJECTING);
        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aabb")), AcceptingOrExit.REJECTING);

        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aabaa")), AcceptingOrExit.ACCEPTING);
        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aabab")), AcceptingOrExit.REJECTING);

        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aabaaa")), AcceptingOrExit.REJECTING);
        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aabaab")), AcceptingOrExit.REJECTING);

        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("aba")), AcceptingOrExit.ACCEPTING);
        Assert.assertEquals(automaton.computeOutput(Word.fromCharSequence("abb")), AcceptingOrExit.REJECTING);
    }

    @Test
    public void RestrictedAutomatonAsROCA() {
        DefaultAutomatonWithCounterValues<Character> automaton = buildAutomatonPeriodOne();

        ROCA<?, Character> roca = automaton.asROCA();

        Assert.assertFalse(roca.accepts(Word.epsilon()));

        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("a")));
        Assert.assertTrue(automaton.accepts(Word.fromCharSequence("b")));

        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("ab")));

        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("aaa")));
        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("aab")));

        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("aaaa")));
        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("aaab")));

        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("aaba")));
        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("aabb")));

        Assert.assertTrue(automaton.accepts(Word.fromCharSequence("aabaa")));
        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("aabab")));

        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("aabaaa")));
        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("aabaab")));

        Assert.assertTrue(automaton.accepts(Word.fromCharSequence("aba")));
        Assert.assertFalse(automaton.accepts(Word.fromCharSequence("abb")));
    }

    @Test
    public void RestrictedAutomatonPeriodOneToROCAs() {
        AutomatonWithCounterValues<?, Character> automaton = buildAutomatonPeriodOne();

        List<ROCA<?, Character>> rocas = automaton.toROCAs(2);

        Assert.assertEquals(rocas.size(), 3);

        ROCA<?, Character> roca = rocas.get(0);
        Assert.assertEquals(roca.size(), 2);
        Assert.assertFalse(roca.accepts(Word.epsilon()));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("a")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("b")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("ab")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("ba")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("bb")));

        roca = rocas.get(1);
        Assert.assertEquals(roca.size(), 4);
        Assert.assertFalse(roca.accepts(Word.epsilon()));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("a")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("b")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("ab")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("ba")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("bb")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aaaabaaaa")));

        roca = rocas.get(2);
        Assert.assertEquals(roca.size(), 4);
        Assert.assertFalse(roca.accepts(Word.epsilon()));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("a")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("b")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("ab")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("ba")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("bb")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aba")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aaaabaaaa")));
    }

    @Test
    public void RestrictedAutomatonPeriodTwoToROCAs() {
        AutomatonWithCounterValues<?, Character> automaton = buildAutomatonPeriodTwo();
        List<ROCA<?, Character>> rocas = automaton.toROCAs(4);

        Assert.assertEquals(rocas.size(), 5);

        ROCA<?, Character> roca = rocas.get(0);
        Assert.assertEquals(roca.size(), 2);
        Assert.assertFalse(roca.accepts(Word.epsilon()));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("b")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("ba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("bb")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("a")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("ab")));

        roca = rocas.get(1);
        Assert.assertEquals(roca.size(), 6);
        Assert.assertFalse(roca.accepts(Word.epsilon()));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("a")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("b")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("ab")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("ba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("bb")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aabaa")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aababaaabbaaaa")));

        roca = rocas.get(2);
        Assert.assertEquals(roca.size(), 4);
        Assert.assertFalse(roca.accepts(Word.epsilon()));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("a")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("b")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("ab")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("ba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("bb")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aabaa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aababaaabbaaaa")));

        roca = rocas.get(3);
        Assert.assertEquals(roca.size(), 6);
        Assert.assertFalse(roca.accepts(Word.epsilon()));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("a")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("b")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("ab")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("ba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("bb")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aabaa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aababaaabbaaaa")));

        roca = rocas.get(4);
        Assert.assertEquals(roca.size(), 8);
        Assert.assertFalse(roca.accepts(Word.epsilon()));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("a")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("b")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("ab")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("ba")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("bb")));
        Assert.assertTrue(roca.accepts(Word.fromCharSequence("aabaa")));
        Assert.assertFalse(roca.accepts(Word.fromCharSequence("aababaaabbaaaa")));
    }
}

package net.automatalib.examples.oca;

import javax.naming.spi.DirStateFactory.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.automatalib.automata.oca.DefaultOCA;
import net.automatalib.automata.oca.DefaultROCA;
import net.automatalib.automata.oca.OCA;
import net.automatalib.automata.oca.OCALocation;
import net.automatalib.automata.oca.ROCALocation;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

public final class OCAExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(OCAExample.class);
    private static final Alphabet<Character> ALPHABET = Alphabets.characters('a', 'b');

    public static void main(String[] args) {
        final DefaultOCA<Character> oca = buildOCA();

        logAcceptance(oca, "aab");
        logAcceptance(oca, "aaabbbaab");
        logAcceptance(oca, "aabbbbb");
        logAcceptance(oca, "a");
        logAcceptance(oca, "aaabbba");

        Visualization.visualize(oca);

        final DefaultROCA<Character> roca = buildROCA();

        logAcceptance(roca, "a");
        logAcceptance(roca, "ab");
        logAcceptance(roca, "aaba");
        logAcceptance(roca, "aba");
        logAcceptance(roca, "aaabaaaaa");

        Visualization.visualize(roca);
    }

    private static void logAcceptance(OCA<?, Character> oca, String input) {
        final boolean accept = oca.accepts(Word.fromCharSequence(input));

        LOGGER.info("The ROCA does {}accept the word '{}'", accept ? "" : "not ", input);
    }

    private static DefaultOCA<Character> buildOCA() {
        final DefaultOCA<Character> oca = new DefaultOCA<>(ALPHABET);

        final OCALocation q0 = oca.addInitialLocation(false);
        final OCALocation q1 = oca.addLocation(true);

        oca.addSuccessor(q0, 0, 'a', +1, q0);
        oca.addSuccessor(q0, 1, 'a', +1, q0);
        oca.addSuccessor(q0, 1, 'a', 0, q1);

        oca.addSuccessor(q1, 0, 'b', 0, q0);
        oca.addSuccessor(q1, 0, 'b', 0, q1);
        oca.addSuccessor(q1, 1, 'b', -1, q1);

        return oca;
    }

    private static DefaultROCA<Character> buildROCA() {
        final DefaultROCA<Character> roca = new DefaultROCA<>(ALPHABET);

        final ROCALocation q0 = roca.addInitialLocation(false);
        final ROCALocation q1 = roca.addLocation(true);

        roca.setSuccessor(q0, 0, 'a', +1, q0);
        roca.setSuccessor(q0, 1, 'a', +1, q0);
        roca.setSuccessor(q0, 1, 'b', 0, q1);

        roca.setSuccessor(q1, 0, 'a', 0, q1);
        roca.setSuccessor(q1, 1, 'a', -1, q1);

        return roca;
    }
}

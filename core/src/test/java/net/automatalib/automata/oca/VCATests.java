package net.automatalib.automata.oca;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.automatalib.words.VPDAlphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.DefaultVPDAlphabet;

/**
 * @author Gaëtan Staquet
 */
public class VCATests {

    private DefaultVCA<Character> buildVCA(AcceptanceMode mode) {
        // If mode == BOTH:
        // L = {a^n c b^n c | n > 1} U {a^n c b^n | n > 1}
        // If mode == FINAL_STATE:
        // L = {a^n c b^m | n > 1 and 0 <= m <= n} U {a^n c b^n | n > 1}
        // If mode == COUNTER_ZERO:
        // L = {epsilon} U {a^n c b^n c | n > 1} U {a^n c b^n | n > 1}
        final VPDAlphabet<Character> sigma = new DefaultVPDAlphabet<>(Arrays.asList('c'), Arrays.asList('a'), Arrays.asList('b'));
        final DefaultVCA<Character> vca = new DefaultVCA<Character>(2, sigma, mode);

        final VCALocation q0 = vca.addInitialLocation(false);
        final VCALocation q1 = vca.addLocation(true);
        final VCALocation q2 = vca.addLocation(true);

        vca.setSuccessor(q0, 0, 'a', q0);
        vca.setSuccessor(q0, 1, 'a', q0);
        vca.setSuccessor(q0, 2, 'a', q0);
        vca.setSuccessor(q0, 2, 'c', q1);

        vca.setSuccessor(q1, 1, 'b', q1);
        vca.setSuccessor(q1, 2, 'b', q1);
        
        vca.setSuccessor(q1, 0, 'c', q2);

        return vca;
    }

    @Test
    public void testAcceptanceFinal() {
        DefaultVCA<Character> vca = buildVCA(AcceptanceMode.ACCEPTING_LOCATION);

        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aacbb")));
        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aacbbc")));
        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aaacbbb")));
        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aaacbb")));
        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aaaacbbbbc")));

        Assert.assertFalse(vca.accepts(Word.epsilon()));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("acb")));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aaaabbbb")));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aaacbbbb")));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aaacbbc")));
    }

    @Test
    public void testAcceptanceZero() {
        DefaultVCA<Character> vca = buildVCA(AcceptanceMode.COUNTER_ZERO);

        Assert.assertTrue(vca.accepts(Word.epsilon()));
        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aacbb")));
        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aacbbc")));
        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aaacbbb")));
        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aaaacbbbbc")));

        Assert.assertFalse(vca.accepts(Word.fromCharSequence("acb")));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aaaabbbb")));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aaacbb")));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aaacbbbb")));
    }

    @Test
    public void testAcceptanceBoth() {
        DefaultVCA<Character> vca = buildVCA(AcceptanceMode.BOTH);

        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aacbb")));
        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aacbbc")));
        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aaacbbb")));
        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aaaacbbbbc")));

        Assert.assertFalse(vca.accepts(Word.epsilon()));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("acb")));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aaaabbbb")));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aaaabbbbc")));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aaacbb")));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aaacbbbb")));
    }

    @Test
    public void testNonNegative() {
        VPDAlphabet<Character> alphabet = new DefaultVPDAlphabet<>(Arrays.asList('c'), Arrays.asList('a'), Arrays.asList('b'));
        DefaultVCA<Character> vca = new DefaultVCA<>(0, alphabet);

        VCALocation q0 = vca.addInitialLocation(false);
        VCALocation q1 = vca.addLocation(true);

        vca.setSuccessor(q0, 0, 'a', q0);
        vca.setSuccessor(q0, 0, 'c', q1);
        vca.setSuccessor(q1, 0, 'b', q1);

        Assert.assertTrue(vca.accepts(Word.fromCharSequence("aacbb")));

        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aacbbb")));
        Assert.assertFalse(vca.accepts(Word.fromCharSequence("aacbbbb")));
    }
}

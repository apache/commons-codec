package org.apache.commons.codec.net;

import java.nio.charset.Charset;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Percent-Encoding cοdec test cases
 */
public class PercentCodecTest {

    @Test
    public void testBasicEncodeDecode() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        final String input = "abcdABCD";
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String(encoded, "UTF-8");
        byte[] decoded = percentCodec.decode(encoded);
        final String decodedS = new String(decoded, "UTF-8");
        assertEquals("Basic Percent-Encoding encoding test", input, encodedS);
        assertEquals("Basic Percent-Encoding decoding test", input, decodedS);
    }

    @Test
    public void testSafeCharEncodeDecodeObject() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        final String input = "abc123_-.*";
        Object encoded = percentCodec.encode((Object) input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String((byte[]) encoded, "UTF-8");
        Object decoded = percentCodec.decode(encoded);
        final String decodedS = new String((byte[]) decoded, "UTF-8");
        assertEquals("Basic Percent-Encoding encoding test", input, encodedS);
        assertEquals("Basic Percent-Encoding decoding test", input, decodedS);
    }

    @Test
    public void testUnsafeCharEncodeDecode() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        final String input = "\u03B1\u03B2\u03B3\u03B4\u03B5\u03B6% ";
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String(encoded, "UTF-8");
        byte[] decoded = percentCodec.decode(encoded);
        final String decodedS = new String(decoded, "UTF-8");
        assertEquals("Basic Percent-Encoding encoding test", "%CE%B1%CE%B2%CE%B3%CE%B4%CE%B5%CE%B6%25 ", encodedS);
        assertEquals("Basic Percent-Encoding decoding test", input, decodedS);
    }

    @Test
    public void testConfigurablePercentEncoder() throws Exception {
        final String input = "abc123_-.* ";
        PercentCodec percentCodec = new PercentCodec(input.getBytes("UTF-8"), false);
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String(encoded, "UTF-8");
        assertEquals("Basic Percent-Encoding encoding test", "%61%62%63%31%32%33%5F%2D%2E%2A%20", encodedS);
    }

    @Test
    public void testPercentEncoderWithPlusForSpace() throws Exception {
        final String input = "a b c d";
        PercentCodec percentCodec = new PercentCodec(null, true);
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String(encoded, "UTF-8");
        assertEquals("Basic Percent-Encoding encoding test", "a+b+c+d", encodedS);
    }

    @Test(expected = EncoderException.class)
    public void testEncodeUnsupportedObject() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        percentCodec.encode("test");
    }

    @Test(expected = DecoderException.class)
    public void testDecodeUnsupportedObject() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        percentCodec.decode("test");
    }
}

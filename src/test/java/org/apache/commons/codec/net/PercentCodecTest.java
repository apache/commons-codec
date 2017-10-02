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
        final String input = "Hello world!";
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String(encoded, "UTF-8");
        byte[] decoded = percentCodec.decode(encoded);
        final String decodedS = new String(decoded, "UTF-8");
        assertEquals("Basic Percent-Encoding test", "Hello world!", encodedS);
        assertEquals("Basic URL decoding test", input, decodedS);
    }

    @Test
    public void testSafeCharEncodeDecodeObject() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        final String input = "abc123_-.*";
        Object encoded = percentCodec.encode((Object) input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String((byte[]) encoded, "UTF-8");
        Object decoded = percentCodec.decode(encoded);
        final String decodedS = new String((byte[]) decoded, "UTF-8");
        assertEquals("Basic Percent-Encoding test", input, encodedS);
        assertEquals("Basic URL decoding test", input, decodedS);
    }

    @Test
    public void testUnsafeCharEncodeDecode() throws Exception {
        PercentCodec percentCodec = new PercentCodec();
        final String input = "\u03B1\u03B2\u03B3\u03B4\u03B5\u03B6% ";
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String(encoded, "UTF-8");
        byte[] decoded = percentCodec.decode(encoded);
        final String decodedS = new String(decoded, "UTF-8");
        assertEquals("Basic Percent-Encoding test", "%CE%B1%CE%B2%CE%B3%CE%B4%CE%B5%CE%B6%25 ", encodedS);
        assertEquals("Basic URL decoding test", input, decodedS);
    }

    @Test
    public void testConfigurablePercentEncoder() throws Exception {
        final String input = "abc123_-.* ";
        PercentCodec percentCodec = new PercentCodec(input.toCharArray());
        byte[] encoded = percentCodec.encode(input.getBytes(Charset.forName("UTF-8")));
        final String encodedS = new String(encoded, "UTF-8");
        assertEquals("Basic Percent-Encoding test", "%61%62%63%31%32%33%5F%2D%2E%2A%20", encodedS);
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

package org.apache.commons.codec.net;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.BitSet;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/**
 * Implements the Percent-Encoding scheme, as described in HTTP 1.1 specification.
 *
 * @see <a href="https://tools.ietf.org/html/rfc3986#section-2.1">Percent-Encoding</a>
 */
public class PercentCodec implements BinaryEncoder, BinaryDecoder {

    private final byte ESCAPE_CHAR = '%';

    private BitSet alwaysEncodeChars = new BitSet();

    public PercentCodec() {
        alwaysEncodeChars.set(ESCAPE_CHAR);
    }

    public PercentCodec(char[] alwaysEncodeCharArray) {
        super();
        fillBitSet(alwaysEncodeCharArray);
    }

    private void fillBitSet(char[] alwaysEncodeCharArray) {
        if (alwaysEncodeCharArray == null)
            return;
        for (int i = 0; i < alwaysEncodeCharArray.length; i++) {
            this.alwaysEncodeChars.set(alwaysEncodeCharArray[i]);
        }
    }

    /**
     * Percent-Encoding implementation based on RFC 3986
     */
    @Override
    public byte[] encode(final byte[] bytes) throws EncoderException {
        if (bytes == null) {
            return null;
        }

        final CharsetEncoder characterSetEncoder = Charset.forName("US-ASCII").newEncoder();

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (final byte c : bytes) {
            int b = c;
            if (b < 0) {
                b = 256 + b;
            }
            if (characterSetEncoder.canEncode((char) b) && !alwaysEncodeChars.get(c)) {
                buffer.write(b);
            } else {
                buffer.write(ESCAPE_CHAR);
                final char hex1 = Utils.hexDigit(b >> 4);
                final char hex2 = Utils.hexDigit(b);
                buffer.write(hex1);
                buffer.write(hex2);
            }
        }
        return buffer.toByteArray();
    }

    @Override
    public byte[] decode(final byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; i++) {
            final int b = bytes[i];
            if (b == ESCAPE_CHAR) {
                try {
                    final int u = Utils.digit16(bytes[++i]);
                    final int l = Utils.digit16(bytes[++i]);
                    buffer.write((char) ((u << 4) + l));
                } catch (final ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid percent decoding: ", e);
                }
            } else {
                buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }

    @Override
    public Object encode(Object obj) throws EncoderException {
        if (obj == null) {
            return null;
        } else if (obj instanceof byte[]) {
            return encode((byte[]) obj);
        } else {
            throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be Percent encoded");
        }
    }

    @Override
    public Object decode(Object obj) throws DecoderException {
        if (obj == null) {
            return null;
        } else if (obj instanceof byte[]) {
            return decode((byte[]) obj);
        } else {
            throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be Percent decoded");
        }
    }
}

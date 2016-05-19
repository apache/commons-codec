package org.apache.commons.codec.digest;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for PureJavaCrc32C.
 * Test data was derived from
 * https://tools.ietf.org/html/rfc3720#appendix-B.4
 */
public class PureJavaCrc32CTest {

    private final PureJavaCrc32C crc = new PureJavaCrc32C();
    
    private byte[] data = new byte[32];

    @Test
    public void testZeros() {
        for(int i = 0; i < data.length; i ++) {
            data[i]= (byte) 0;
        }        
        check(0x8a9136aa); // aa 36 91 8a
    }

    @Test
    public void testOnes() {
        for(int i = 0; i < data.length; i ++) {
            data[i]= (byte) 0xFF;
        }
        check(0x62a8ab43); // 43 ab a8 62
    }

    @Test
    public void testIncreasing() {
        for(int i = 0; i < data.length; i ++) {
            data[i]= (byte) i;
        }        
        check(0x46dd794e); // 4e 79 dd 46
    }

    @Test
    public void testDecreasing() {
        for(int i = 0; i < data.length; i ++) {
            data[i]= (byte) (31-i);
        }        
        check(0x113fdb5c); // 5c db 3f 11
    }

    // Using int because only want 32 bits
    private void check(int expected) {
        crc.reset();
        crc.update(data, 0, data.length);
        int actual = (int) crc.getValue();
        Assert.assertEquals(Integer.toHexString(expected), Integer.toHexString(actual));
    }

}

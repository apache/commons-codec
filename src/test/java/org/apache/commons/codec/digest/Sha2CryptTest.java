package org.apache.commons.codec.digest;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class Sha2CryptTest {

    @Test
    public void testCtor() {
        assertNotNull(new Sha2Crypt());
    }

}

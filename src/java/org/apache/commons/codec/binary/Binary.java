/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package org.apache.commons.codec.binary ;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/**
 * Encodes and decodes binary to and from ascii bit Strings.
 *
 * @todo may want to add more bit vector functions like and/or/xor/nand
 * @todo also might be good to generate boolean[] from byte[] et. cetera.
 * @author Apache Software Foundation
 * @since 1.3
 * @version $Revision: 1.6 $
 */
public class Binary implements BinaryDecoder, BinaryEncoder
{
    /** Mask for bit 0 of a byte. */
    public static final int BIT_0 = 1 ;
    /** Mask for bit 1 of a byte. */
    public static final int BIT_1 = 1 << 1 ;
    /** Mask for bit 2 of a byte. */
    public static final int BIT_2 = 1 << 2 ;
    /** Mask for bit 3 of a byte. */
    public static final int BIT_3 = 1 << 3 ;
    /** Mask for bit 4 of a byte. */
    public static final int BIT_4 = 1 << 4 ;
    /** Mask for bit 5 of a byte. */
    public static final int BIT_5 = 1 << 5 ;
    /** Mask for bit 6 of a byte. */
    public static final int BIT_6 = 1 << 6 ;
    /** Mask for bit 7 of a byte. */
    public static final int BIT_7 = 1 << 7 ;
    
    public static final int [] BITS = 
    {
      BIT_0, BIT_1, BIT_2, BIT_3, BIT_4, BIT_5, BIT_6, BIT_7
    } ;
    
    
    /**
     * Converts an array of raw binary data into an array of ascii 0 and 1
     * characters.
     * 
     * @param raw the raw binary data to convert
     * @return 0 and 1 ascii character bytes one for each bit of the argument
     * @see org.apache.commons.codec.BinaryEncoder#encode(byte[])
     */
    public byte[] encode( byte[] raw )
    {
        return toAsciiBytes( raw ) ;
    }
    

    /**
     * Converts an array of raw binary data into an array of ascii 0 and 1
     * chars.
     * 
     * @param raw the raw binary data to convert
     * @return 0 and 1 ascii character chars one for each bit of the argument
     * @throws EncoderException if the argument is not a byte[]
     * @see org.apache.commons.codec.Encoder#encode(java.lang.Object)
     */
    public Object encode( Object raw ) throws EncoderException
    {
        if ( ! ( raw instanceof byte [] ) )
        {
            throw new EncoderException( "argument not a byte array" ) ;
        }
        
        return toAsciiChars( ( byte [] ) raw ) ;
    }
    

    /**
     * Decodes a byte array where each byte represents an ascii '0' or '1'.
     * 
     * @param ascii each byte represents an ascii '0' or '1'
     * @return the raw encoded binary where each bit corresponds to a byte in
     *      the byte array argument
     * @throws DecoderException if argument is not a byte[], char[] or String
     * @see org.apache.commons.codec.Decoder#decode(java.lang.Object)
     */
    public Object decode( Object ascii ) throws DecoderException
    {
        if ( ascii instanceof byte[] )
        {
            return fromAscii( ( byte[] ) ascii ) ;
        }
        
        if ( ascii instanceof char[] )
        {
            return fromAscii( ( char[] ) ascii ) ;
        }
        
        if ( ascii instanceof String )
        {
            return fromAscii( ( ( String ) ascii ).toCharArray() ) ;
        }

        throw new DecoderException( "argument not a byte array" ) ;
    }

    
    /**
     * Decodes a byte array where each byte represents an ascii '0' or '1'.
     * 
     * @param ascii each byte represents an ascii '0' or '1'
     * @return the raw encoded binary where each bit corresponds to a byte in
     *      the byte array argument 
     * @see org.apache.commons.codec.Decoder#decode(Object)
     */
    public byte[] decode( byte[] ascii ) 
    {
        return fromAscii( ascii ) ;
    }
    
    
    /**
     * Decodes a String where each char of the String represents an ascii '0' 
     * or '1'.
     * 
     * @param ascii String of '0' and '1' characters
     * @return the raw encoded binary where each bit corresponds to a byte in
     *      the byte array argument 
     * @see org.apache.commons.codec.Decoder#decode(Object)
     */
    public byte[] decode( String ascii )
    {
        return fromAscii( ascii.toCharArray() ) ;
    }
    
    
    /**
     * Decodes a byte array where each char represents an ascii '0' or '1'.
     * 
     * @param ascii each char represents an ascii '0' or '1'
     * @return the raw encoded binary where each bit corresponds to a char in
     *      the char array argument 
     */
    public static byte[] fromAscii( char[] ascii )
    {
        // get length/8 times bytes with 3 bit shifts to the right of the length
        byte[] l_raw = new byte[ ascii.length >> 3 ] ;
        
        /*
         * Yah its long and repetitive but I unraveled an internal loop to 
         * check each bit of a byte for speed using the bit masks that are
         * precomputed which is another PITA but it makes it faster.
         * 
         * We also decr index jj by 8 as we go along to not recompute indices
         * using multiplication every time inside the loop.
         * 
         * @todo might want another nested loop to use BITS[] now that its here
         */
        for ( int ii=0, jj=ascii.length-1; ii < l_raw.length; ii++, jj-=8 )
        {
            if ( ascii[jj] == '1' )
            {
                l_raw[ii] |= BIT_0 ;
            }
            
            if ( ascii[jj - 1] == '1' )
            {
                l_raw[ii] |= BIT_1 ;
            }

            if ( ascii[jj - 2] == '1' )
            {
                l_raw[ii] |= BIT_2 ;
            }

            if ( ascii[jj - 3] == '1' )
            {
                l_raw[ii] |= BIT_3 ;
            }

            if ( ascii[jj - 4] == '1' )
            {
                l_raw[ii] |= BIT_4 ;
            }

            if ( ascii[jj - 5] == '1' )
            {
                l_raw[ii] |= BIT_5 ;
            }

            if ( ascii[jj - 6] == '1' )
            {
                l_raw[ii] |= BIT_6 ;
            }

            if ( ascii[jj - 7] == '1' )
            {
                l_raw[ii] |= BIT_7 ;
            }
        }
        
        return l_raw ;
    }

    
    /**
     * Decodes a byte array where each byte represents an ascii '0' or '1'.
     * 
     * @param ascii each byte represents an ascii '0' or '1'
     * @return the raw encoded binary where each bit corresponds to a byte in
     *      the byte array argument 
     */
    public static byte[] fromAscii( byte[] ascii )
    {
        // get length/8 times bytes with 3 bit shifts to the right of the length
        byte[] l_raw = new byte[ ascii.length >> 3 ] ;
        
        /*
         * Yah its long and repetitive but I unraveled an internal loop to 
         * check each bit of a byte for speed using the bit masks that are
         * precomputed which is another PITA but it makes it faster.
         * 
         * We also decr index jj by 8 as we go along to not recompute indices
         * using multiplication every time inside the loop.
         * 
         * @todo might want another nested loop to use BITS[] now that its here
         */
        for ( int ii=0, jj=ascii.length-1; ii < l_raw.length; ii++, jj-=8 )
        {
            if ( ascii[jj] == '1' )
            {
                l_raw[ii] |= BIT_0 ;
            }
            
            if ( ascii[jj - 1] == '1' )
            {
                l_raw[ii] |= BIT_1 ;
            }

            if ( ascii[jj - 2] == '1' )
            {
                l_raw[ii] |= BIT_2 ;
            }

            if ( ascii[jj - 3] == '1' )
            {
                l_raw[ii] |= BIT_3 ;
            }

            if ( ascii[jj - 4] == '1' )
            {
                l_raw[ii] |= BIT_4 ;
            }

            if ( ascii[jj - 5] == '1' )
            {
                l_raw[ii] |= BIT_5 ;
            }

            if ( ascii[jj - 6] == '1' )
            {
                l_raw[ii] |= BIT_6 ;
            }

            if ( ascii[jj - 7] == '1' )
            {
                l_raw[ii] |= BIT_7 ;
            }
        }
        
        return l_raw ;
    }

    
    /**
     * Converts an array of raw binary data into an array of ascii 0 and 1
     * character bytes - each byte is a truncated char.
     * 
     * @param raw the raw binary data to convert
     * @return an array of 0 and 1 character bytes for each bit of the argument
     * @see org.apache.commons.codec.BinaryEncoder#encode(byte[])
     */
    public static byte[] toAsciiBytes( byte[] raw )
    {
        // get 8 times the bytes with 3 bit shifts to the left of the length
        byte [] l_ascii = new byte[ raw.length << 3 ] ;
        
        /*
         * Yah its long and repetitive but I unraveled an internal loop to 
         * check each bit of a byte for speed using the bit masks that are
         * precomputed which is another PITA but it makes it faster.
         * 
         * We also decr index jj by 8 as we go along to not recompute indices
         * using multiplication every time inside the loop.
         * 
         * @todo might want another nested loop to use BITS[] now that its here
         */
        for ( int ii=0, jj=l_ascii.length-1; ii < raw.length; ii++, jj-=8 )
        {
            if ( ( raw[ii] & BIT_0 ) == 0 )
            {
                l_ascii[jj] = '0' ;
            }
            else
            {
                l_ascii[jj] = '1' ;
            }
            
            if ( ( raw[ii] & BIT_1 ) == 0 )
            {
                l_ascii[jj - 1] = '0' ;
            }
            else
            {
                l_ascii[jj - 1] = '1' ;
            }

            if ( ( raw[ii] & BIT_2 ) == 0 )
            {
                l_ascii[jj - 2] = '0' ;
            }
            else
            {
                l_ascii[jj - 2] = '1' ;
            }

            if ( ( raw[ii] & BIT_3 ) == 0 )
            {
                l_ascii[jj - 3] = '0' ;
            }
            else
            {
                l_ascii[jj - 3] = '1' ;
            }

            if ( ( raw[ii] & BIT_4 ) == 0 )
            {
                l_ascii[jj - 4] = '0' ;
            }
            else
            {
                l_ascii[jj - 4] = '1' ;
            }

            if ( ( raw[ii] & BIT_5 ) == 0 )
            {
                l_ascii[jj - 5] = '0' ;
            }
            else
            {
                l_ascii[jj - 5] = '1' ;
            }

            if ( ( raw[ii] & BIT_6 ) == 0 )
            {
                l_ascii[jj - 6] = '0' ;
            }
            else
            {
                l_ascii[jj - 6] = '1' ;
            }

            if ( ( raw[ii] & BIT_7 ) == 0 )
            {
                l_ascii[jj - 7] = '0' ;
            }
            else
            {
                l_ascii[jj - 7] = '1' ;
            }
        }
        
        return l_ascii ;
    }


    /**
     * Converts an array of raw binary data into an array of ascii 0 and 1
     * characters.
     * 
     * @param raw the raw binary data to convert
     * @return an array of 0 and 1 characters for each bit of the argument
     * @see org.apache.commons.codec.BinaryEncoder#encode(byte[])
     */
    public static char[] toAsciiChars( byte[] raw )
    {
        // get 8 times the bytes with 3 bit shifts to the left of the length
        char [] l_ascii = new char[ raw.length << 3 ] ;
        
        /*
         * Yah its long and repetitive but I unraveled an internal loop to 
         * check each bit of a byte for speed using the bit masks that are
         * precomputed which is another PITA but it makes it faster.
         * 
         * We also grow index jj by 8 as we go along to not recompute indices
         * using multiplication every time inside the loop.
         * 
         * @todo might want another nested loop to use BITS[] now that its here
         */
        for ( int ii=0, jj=l_ascii.length-1; ii < raw.length; ii++, jj-=8 )
        {
            if ( ( raw[ii] & BIT_0 ) == 0 )
            {
                l_ascii[jj] = '0' ;
            }
            else
            {
                l_ascii[jj] = '1' ;
            }
            
            if ( ( raw[ii] & BIT_1 ) == 0 )
            {
                l_ascii[jj - 1] = '0' ;
            }
            else
            {
                l_ascii[jj - 1] = '1' ;
            }

            if ( ( raw[ii] & BIT_2 ) == 0 )
            {
                l_ascii[jj - 2] = '0' ;
            }
            else
            {
                l_ascii[jj - 2] = '1' ;
            }

            if ( ( raw[ii] & BIT_3 ) == 0 )
            {
                l_ascii[jj - 3] = '0' ;
            }
            else
            {
                l_ascii[jj - 3] = '1' ;
            }

            if ( ( raw[ii] & BIT_4 ) == 0 )
            {
                l_ascii[jj - 4] = '0' ;
            }
            else
            {
                l_ascii[jj - 4] = '1' ;
            }

            if ( ( raw[ii] & BIT_5 ) == 0 )
            {
                l_ascii[jj - 5] = '0' ;
            }
            else
            {
                l_ascii[jj - 5] = '1' ;
            }

            if ( ( raw[ii] & BIT_6 ) == 0 )
            {
                l_ascii[jj - 6] = '0' ;
            }
            else
            {
                l_ascii[jj - 6] = '1' ;
            }

            if ( ( raw[ii] & BIT_7 ) == 0 )
            {
                l_ascii[jj - 7] = '0' ;
            }
            else
            {
                l_ascii[jj - 7] = '1' ;
            }
        }
        
        return l_ascii ;
    }


    /**
     * Converts an array of raw binary data into a String of ascii 0 and 1
     * characters.
     * 
     * @param raw the raw binary data to convert
     * @return a String of 0 and 1 characters representing the binary data
     * @see org.apache.commons.codec.BinaryEncoder#encode(byte[])
     */
    public static String toAsciiString( byte[] raw )
    {
        return new String( toAsciiChars( raw ) ) ;
    }
}

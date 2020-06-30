/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.binary;

import java.util.Random;

/**
 * This random data was encoded by OpenSSL. Java had nothing to do with it. This data helps us test interop between
 * Commons-Codec and OpenSSL. Notice that OpenSSL creates 64 character lines instead of the 76 of Commons-Codec.
 *
 * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>
 * @since 1.4
 */
public class Base64TestData {

    public static final String CODEC_101_MULTIPLE_OF_3 = "124";

    public static final String CODEC_98_NPE
        = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjAxMjM";

    public static final String CODEC_98_NPE_DECODED
        = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123";


    // OpenSSL doesn't include the final \n, but it would be annoying beyond belief
    // to further parameterize commons-codec to support this pointless variation.
    final static String ENCODED_64_CHARS_PER_LINE
            = "9IPNKwUvdLiIAp6ctz12SiQmOGstWyYvSPeevufDhrzaws65voykKjbIj33YWTa9\n"
            + "xA7c/FHypWclrZhQ7onfc3JE93BJ5fT4R9zAEdjbjy1hv4ZYNnET4WJeXMLJ/5p+\n"
            + "qBpTsPpepW8DNVYy1c02/1wyC+kgA6CvRUd9cSr/lt88AEdsTV4GMCn1+EwuAiYd\n"
            + "ivxuzn+cLM8q2jewqlI52tP9J7Cs8vqG71s6+WAELKvm/UovvyaOi+OdMUfjQ0JL\n"
            + "iLkHu6p9OwUgvQqiDKzEv/Augo0dTPZzYGEyCP5GVrle3QQdgciIHnpdd4VUTPGR\n"
            + "UbXeKbh++U3fbJIng/sQXM3IYByMZ7xt9HWS1LUcRdQ7Prwn/IlQWxOMeq+KZJSo\n"
            + "AviWtdserXyHbIEa//hmr4p/j80k0g9q35hq1ayGM9984ALTSaZ8WeyFbZx1CxC/\n"
            + "Qoqf92UH/ylBRnSJNn4sS0oa3uUbNvOnpkB4D9V7Ut9atinCJrw+wiJcMl+9kp25\n"
            + "1IUxBGA4cUxh0eaxk3ODWnwI95EktmWOKwCSP0xjWwIMxDjygwAG5R8fk9H9bVi1\n"
            + "thMavm4nDc4vaNoSE1RnZNYwbiUVlVPM9EclvJWTWd6igWeA0MxHAA8iOM5Vnmqp\n"
            + "/WGM7UDq59rBIdNQCoeTJaAkEtAuLL5zogOa5e+MzVjvB5MYQlOlaaTtQrRApXa5\n"
            + "Z4VfEanu9UK2fi1T8jJPFC2PmXebxp0bnO+VW+bgyEdIIkIQCaZq1MKWC3KuiOS9\n"
            + "BJ1t7O0A2JKJKvoE4UNulzV2TGCC+KAnmjRqQBqXlJmgjHQAoHNZKOma/uIQOsvf\n"
            + "DnqicYdDmfyCYuV89HjA1H8tiDJ85VfsrFHdcbPAoNCpi65awJSHfdPO1NDONOK+\n"
            + "+S7Y0VXUgoYYrBV4Y7YbC8wg/nqcimr3lm3tRyp+QsgKzdREbfNRk0F5PLyLfsUE\n"
            + "lepjs1QdV3fEV1LJtiywA3ubVNQJRxhbYxa/C/Xy2qxpm6vvdL92l3q1ccev35Ic\n"
            + "aOiSx7Im+/GxV2lVKdaOvYVGDD1zBRe6Y2CwQb9p088l3/93qGR5593NCiuPPWcs\n"
            + "DWwUShM1EyW0FNX1F8bnzHnYijoyE/jf4s/l9bBd7yJdRWRCyih2WcypAiOIEkBs\n"
            + "H+dCTgalu8sRDoMh4ZIBBdgHfoZUycLqReQFLZZ4Sl4zSmzt5vQxQFhEKb9+ff/4\n"
            + "rb1KAo6wifengxVfIsa2b5ljXzAqXs7JkPvmC6fa7X4ZZndRokaxYlu3cg8OV+uG\n"
            + "/6YAHZilo8at0OpkkNdNFuhwuGlkBqrZKNUj/gSiYYc06gF/r/z6iWAjpXJRW1qq\n"
            + "3CLZXdZFZ/VrqXeVjtOAu2A=\n";

    final static String ENCODED_76_CHARS_PER_LINE
            = "9IPNKwUvdLiIAp6ctz12SiQmOGstWyYvSPeevufDhrzaws65voykKjbIj33YWTa9xA7c/FHypWcl\n"
            + "rZhQ7onfc3JE93BJ5fT4R9zAEdjbjy1hv4ZYNnET4WJeXMLJ/5p+qBpTsPpepW8DNVYy1c02/1wy\n"
            + "C+kgA6CvRUd9cSr/lt88AEdsTV4GMCn1+EwuAiYdivxuzn+cLM8q2jewqlI52tP9J7Cs8vqG71s6\n"
            + "+WAELKvm/UovvyaOi+OdMUfjQ0JLiLkHu6p9OwUgvQqiDKzEv/Augo0dTPZzYGEyCP5GVrle3QQd\n"
            + "gciIHnpdd4VUTPGRUbXeKbh++U3fbJIng/sQXM3IYByMZ7xt9HWS1LUcRdQ7Prwn/IlQWxOMeq+K\n"
            + "ZJSoAviWtdserXyHbIEa//hmr4p/j80k0g9q35hq1ayGM9984ALTSaZ8WeyFbZx1CxC/Qoqf92UH\n"
            + "/ylBRnSJNn4sS0oa3uUbNvOnpkB4D9V7Ut9atinCJrw+wiJcMl+9kp251IUxBGA4cUxh0eaxk3OD\n"
            + "WnwI95EktmWOKwCSP0xjWwIMxDjygwAG5R8fk9H9bVi1thMavm4nDc4vaNoSE1RnZNYwbiUVlVPM\n"
            + "9EclvJWTWd6igWeA0MxHAA8iOM5Vnmqp/WGM7UDq59rBIdNQCoeTJaAkEtAuLL5zogOa5e+MzVjv\n"
            + "B5MYQlOlaaTtQrRApXa5Z4VfEanu9UK2fi1T8jJPFC2PmXebxp0bnO+VW+bgyEdIIkIQCaZq1MKW\n"
            + "C3KuiOS9BJ1t7O0A2JKJKvoE4UNulzV2TGCC+KAnmjRqQBqXlJmgjHQAoHNZKOma/uIQOsvfDnqi\n"
            + "cYdDmfyCYuV89HjA1H8tiDJ85VfsrFHdcbPAoNCpi65awJSHfdPO1NDONOK++S7Y0VXUgoYYrBV4\n"
            + "Y7YbC8wg/nqcimr3lm3tRyp+QsgKzdREbfNRk0F5PLyLfsUElepjs1QdV3fEV1LJtiywA3ubVNQJ\n"
            + "RxhbYxa/C/Xy2qxpm6vvdL92l3q1ccev35IcaOiSx7Im+/GxV2lVKdaOvYVGDD1zBRe6Y2CwQb9p\n"
            + "088l3/93qGR5593NCiuPPWcsDWwUShM1EyW0FNX1F8bnzHnYijoyE/jf4s/l9bBd7yJdRWRCyih2\n"
            + "WcypAiOIEkBsH+dCTgalu8sRDoMh4ZIBBdgHfoZUycLqReQFLZZ4Sl4zSmzt5vQxQFhEKb9+ff/4\n"
            + "rb1KAo6wifengxVfIsa2b5ljXzAqXs7JkPvmC6fa7X4ZZndRokaxYlu3cg8OV+uG/6YAHZilo8at\n"
            + "0OpkkNdNFuhwuGlkBqrZKNUj/gSiYYc06gF/r/z6iWAjpXJRW1qq3CLZXdZFZ/VrqXeVjtOAu2A=\n";
}

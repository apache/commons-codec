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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.Decoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.Encoder;
import org.apache.commons.codec.EncoderException;
import org.junit.Test;

/**
 * Tests to make sure future versions of commons-codec.jar have identical Base64
 * behavior as commons-codec-1.3.jar.
 *
 * @since Mar 25, 2010
 */
public class Base64Codec13Test {

    private final static String[] STRINGS = new String[181];
    private final static String[] CHUNKED_STRINGS = new String[STRINGS.length];
    private final static byte[][] BYTES = new byte[STRINGS.length][];

    static {
        initSTRINGS();
        initCHUNKED_STRINGS();
        initBYTES();
    }

    /* These strings were generated from random byte[] arrays fed into commons-codec-1.3.jar */
    private static void initSTRINGS() {
        final String[] s = STRINGS;
        s[0] = "";
        s[1] = "uA==";
        s[2] = "z9w=";
        s[3] = "TQ+Z";
        s[4] = "bhjUYA==";
        s[5] = "1cO9td8=";
        s[6] = "sMxHoJf5";
        s[7] = "jQcPlDsZzw==";
        s[8] = "TAaPnfW/CYU=";
        s[9] = "cXTZuwzXPONS";
        s[10] = "Ltn/ZTV4IjT6OA==";
        s[11] = "6fh+jdU31SOmWrc=";
        s[12] = "E/5MoD4qCvDuTcFA";
        s[13] = "2n9YyfCMyMXembtssQ==";
        s[14] = "qBka3Bq6V31HmcmHjkY=";
        s[15] = "WvyZe6kQ8py7m25ucawJ";
        s[16] = "oYpxMy6BSpZXhVpOR6dXmA==";
        s[63] = "yexFaNKaP+VkVwEUvxQXbC0HSCi/srOY7c036lT25frs4xjIvp214JHCg7OL/XZW3IMe6CDgSMCaaI91eRgM";
        s[64] = "vkqgnuJ3plxNtoveiMJuYLN6wZQLpb3Fg48/zHkdFuDucuMKWVRO/niFRgKsFqcxq6VgCxwQbePiP9sRnxz7wg==";
        s[65] = "YHks3GCb2uVF47Y2tCieiDGJ879Rm5dBhQhQizYhfWOo+BrB2/K+E7wZWTid14lMpM60+5C0N9JNKAcBESeqZZI=";
        s[66] = "z8551jmQp/Qs95tw226+HCHWruKx/JvfBCfQ5p0fF77mkSpp66E552ik2gycrBBsMC/NbznAgTZoMzZxehfJwu49";
        s[67] = "VsR2whqq/qQm342+TNz1lgOZMoWbpCz+kj2zflq0nx7S/ReEVPUJcqgMtVzrr2FVQPfBAH5VRjR+hkxwv4bssQms8Q==";
        s[68] = "5xmIp8dUJZzkisIkuEPsnPXvzDeMo48qWFfHIn2av3jr5qoZHCs0LfNEyepC3v2sa0nCU0FlqsmDyTI5/2jt5zsLtV0=";
        s[69] = "tGEgXjglUB9NbCtiS88AetLtRqhCAnhzOUKVgvbJZHqOA6x8uOorA1t3NcaIA00ZqbPYALu4LzIm4i4lAL9QgiH/Jg7b";
        s[70] = "gFtxEhYJahDmU5dpudYs6ZTsqAx+s2j+06A0zeyb3U7nhZFsbkCDlds0EYUoqerZqZPm7F6CDOOD3dU7nYmelE0DxyMO9A==";
        s[71] = "j/h/1JygYA5bjttxzQxr5gBtgh+AYVozhF4WgvcU/g49v0hUy6FdhfZewGK+Phtzj7RabI5p2zXyzvkmLQdFhdI5Um4O5sw=";
        s[72] = "m+kYVGojIR5pgbz7pGJm2g+3qqk7fhl3cowa2eVrhki7WofyywnRezqTxOkBgVFz6nKs8qxpbbbzALctcPeMsp9dpXUfuUJr";
        s[73] = "pPaGnMr0UYmldnJVk/F+3WCJJ1r2otvD5KJdt2u1RnS6LwhHhwLCqfW8O/QEg43WdxKomGL/JM33tn/B9pMPoIU0QTGjq2GRow==";
        s[74] = "mOxzGyym6T/BxCV5nSiIYMlfAUmCN7gt7+ZTMg1kd8Ptirk+JF5dk8USbWBu/9ZvNg5ZuiJCeGwfaVpqpZ3K9ySF7C87Jvu1RUE=";
        s[75] = "VYLOIE4DnhxJn3FKS/2RHHHYLlZiGVdV/k4uBbtAYHUSTpRzaaYPGNAVjdNwbTHIihmeuk/5YQUy8NFsxIom+Li7bnWiBoHKBPP7";
        s[76] = "7foMpJ0TknCatjUiSxtnuKSiz4Qvl/idWY9UKiTljoQHZ+C8bcUscnI/bZr13e6AvyUh47MlpdGvzIm05qUdMWWLoZJOaGYvDmvrWQ==";
        s[77] = "jxQSGiFs+b1TfE4lDUAPUPJ0SWeUgl03rK6auieWJcarDIHM97gGOHMmyHudRMqkZmIkxYRgYgCCUg/JeU91OZD3tL4U+wNhShywe88=";
        s[78] = "UGmH/gl7t3Dk801vRhRDEbgsfShtHZ1gZQn4KNZ5Qsw3WiGjW0ImInVHa+LSHBzLUjwC0Z3nXO4i4+CiKYqAspOViE6WqVUY8ZSV0Og4";
        s[79] = "wdEoYmJuRng2z2IkAiSgJ1CW2VE7H7oXpYWEFFO8nG0bZn7PHhT8KyhaO2ridl8eUEysja0VXFDyQqSgZnvdUKrCGepWGZbw0/0bDws3Ag==";
        s[80] = "5kZqgrUbt+rN5BWAddeUtm9TGT43vYpB6PeyQwTyy9Vbr0+U/4Qzy0Iw37Ra293HmkmpgQzuScVpcIiFGXPAFWwToR+bovwu7aXji/FnMwk=";
        s[81] = "E467MMmJbmwv8Omc2TdcyMr/30B8izWbf+CAuJtw67b1g9trhC6n4GYnXjeW9DYvmWoIJPx0zvU/Q+gqv0cteg2bx9P2mrgMDARb6egowqjx";
        s[82] = "Vpt8hYb4jx1F+7REX7K65v6eO5F1GDg/K8SVLWDSp0srupYEQkBVRxnB9dmhSo9XHpz4C8pRl8r82fxXZummEf4U2Oh0Dip5rnNtDL+IJvL8lQ==";
        s[121] = "hf69xr9mtFf4N3j2uA9MgLL5Zy94Hjv+VQi94+LS8972JJgDHCQOwP5whdQkV+SJpXkiyHGaSsQ4fhepPwzuZcEpYER+beny1j+M0HSZe36MdRIhlTqno+4qsXckL0CjXsYkJJM0NAfOYjHAus5G1bgi9VhmiMfAMA==";
        s[122] = "yKzTh5hPp9/PBeZtrZXsFHAR9ywOM3hRaBDkgG9E09wFW8MZD0xMGegcp0QrTJcP8QYOaYhTDVimPqsNTVOmjdjkvS+2WhjJW4mVOXQ8KID91yaBtPo+DStL5GMgctcP5MoVf1Vp8w/mYtofqbllfDm5NfYzh2A7ijY=";
        s[123] = "csFmwvDzoO4IO6ySDA4B2V7emEetAwCgO66zzlfWb3KDrPfFZc3Jgr4+dlaUkEIDHYeLHETdTssWOl2KrPHBEsULpDTR+3OhurXb1Qr2NvHiHFuqT3Geb6EYw2albfTmXxch82ablt4WKl4qPcSey54v6tsCuUuZzrkt";
        s[124] = "5InxvKwpoCV7EK78OzU/tL9/NmK14Prw9tOCAyK+xpUNLZriuVEVdgpoZ05rliufaUDGHH8dPAem8G9DN/VOPktB6vXKtc2kMUgnMTiZwv/UVd+xyqnT8PLEdNQ8rCWxyiCcLdXFf0+xcE7qCcwzC+D7+cRW+i6dnpZkyw==";
        s[125] = "cEx7oTsSHWUFPj92cstdy5wGbRcxH+VRWN8kaNTTCPWqSckyU9Xk/jj5/gj9DFwjfsCSp60xutf4/rFanjtwqtRg6dJLP4JAgpOKswDlHi6Vt7zF/w7HidMf2sdtlsqzayZmT2Hn7iOo3CExzr5Z5JfmMFAX8R9peUN4t5U=";
        s[126] = "AeXetVbj+7mmGiCs3BGUSZDLlq2odMsN8JAHQM64Cly8y5jw75PpISocWRFFQmmXYP7ckKmtuhIvD69HtZxGhNRsl1l1gXzKFhsWykcRtG87F8sS1Uv/i6QvGiRIDVEGGIzWrzRIISkBb9wCxJ2HESfleWwrz/GqryjoN26B";
        s[127] = "aj1/8/+U8VO3D2iAwvQXZ4H0KwtuzDm4JCC8+22ccqk+UzKvqjGs87XngwfsMfSkeGVAi6VB6tfNJTjYctaj7R8dwh2PIfLSrvaphw4wNB2REjplnPojoOb9bNUNtUvdK3b1bArOWugIRJWLnMl72yEHFb1iBfBmn7uIa7KT2Q==";
        s[128] = "kiMuF/1CMRlgoS/uLKn1mNZFZNHJNkRQnivOrzj8HQAagwzvTXvsGgI9hXX3qaeZL9/x/Oq+Y5F6Dh+wXo+0kp6JagFjvbTJcQSowFzIwg7/V9sans0NE0Ejow5BfZKvihFI46sHiALl1qzoXqLQq+5fZGIFRyyY8wFW1uiNu9k=";
        s[129] = "YXmCWhiNz4/IhyxQIYjgNvjX+XwDiPTSBMaFELm5X8Y4knGRnkF4/zix8l9wHBb+7Cgfrr46rF7eiIzaAFLjLjjewy63duBJiVbEWjqFO0fu6T9iIjuEaF2sTppvuZNPHx80vN+HLAnAVmgFESw5APXWn15dizvuUfVHd5isCqbA";
        s[130] = "GJfUoBivW5uqDpUxTRfjGWNYfN3/dTFtdRqCIH5L2c1nWX0dgY3ba1+fW/YX1Oh5aw4lC6BIiiThjJoV1VrNnlXzbcUcMy+GsDUUB8Qe8lBvfe/t4RmNPB0hVgrS89ntbuU0SsCmWw+9DqM3nidPebANKERig1zZTBBKgpVf7HPFCA==";
        s[131] = "eTerNs7dOqJAxAxcMQRnUDc2cBj2x0jU1g1D3G+b22kDz7JBzOy/mxhGXAQ3130lavWMhImSBkReU+z0A13EYVMUv9PFzD747KCvns+SCo52YNHB0896b4l47q8hu8jsD17TZ2uWWJhS4cNnSE1jeM6NoXGKvf90yxfzwucNYc4RdaQ=";
        s[132] = "lbrGsjbzE521q8tzVHV7vcTPchUnzI/UfeR2R+cmOa29YljPWLht9Wx2JrjiKv4Of5nXe7kvhi+LYUuFVqgaqIFhC/PLbqOFiT2VZcXorToaRT9CLiqV5b6nHN/Txz6SI7MiD3hnk7psDbglPOo+ytqc9sFHj7UkR1ZctQjwFYwJjlxf";
        s[133] = "mQwAPzYzfxz9FXEiZ6M8u1oN3EJbFYmNVfpm+j0DqgU+OPI4URHBIrF4xvdMvAPn0WuarbQy/ZVN0eKL7S4K3Mvan0flAwaZdI+e5HpkfxOoGTp8Dk5EFTXjmZ/s+GonePEQEGNVPL1WYoD6xXqAAvMLKtyrFcpoiGS9eDBlsZDQzPzz/g==";
        s[134] = "3G6d12UY4l5W7Nnw0BL0HnViVg9SdEuLMqeZwy0RlJR/Ytcgd/mIxIuXXAlGhvhoX6Xc2BGU7RpTi1jYKzA86yul0j96dbcE4OtrP9lUBJlcY9eWz59dvLqKxwt3cEOBwrPf69MHuIa256es3AOCobfC8RQScW0PQ0QUa1VHB/eXSsVTSWg=";
        s[135] = "AxgrZKTFk5JvLC3DBACHwp266FxKI/yn9F+1tYkzL57RVs5HCJYS47VuG0T0E2wqzHqcLKPQMZWU7vbRoyMGNL3ZtaHoZqTqcq9KWtODC+OnEvSS7+1P4SmQDuyL2MJ/eJABJKNcu1K/Lk0buAaO0FvX6OGBcPzu1+dv/ZkwuORK07qRnxqQ";
        s[136] = "atkG8l2U/Nnm+zLu7zjenrfcAVQJMUqHimFZ3cQfaUp4qyrFn1UiwZ33j66Vt63eVaT/FXx+LlEnsHn6ATPBMp3iEYoBJYyNpjz/zROhBbcznQAMdWUTcyKInvnG3x6ExgBXpyqfxxp/Pm8by7LlSUT5MKHdcu+TEfUXRokCr2iPnt7NYsNDfA==";
        s[137] = "dciU1mSHGcBxOAVBgJx75J6DaQdBZfTIzQ04WhkkdRMupOZWSnv19xhz8hiO+ZnbBtDJuO5rHsUGzH/jYacfZyCQ924roRvkh3T1yxsLq3abZJUCD9HYnPTELVhv1+cEF4aoO3vGOu2FpfLUn5QTd0PozsIqcqZVB2V57B7DjfizUe3D8Yf5Vco=";
        s[138] = "dgR1PPacBvtILBmg33s9KWtuc67ndh3rCHZ/lIN7sENgbFqJQy5DC3XIeHTV7oWd+tJQaXxoC71/SU7Rz6OClAMKXLbMz8U6RPiqn3M7MRCQcDfNjA5cCNknXT9Ehz/IZF/7lcWrwxBKYm4B98lPkpZtR2QHndiQ3venzWrP0P5y27mReaFuaJ++";
        s[139] = "1Q8rfp1HuGsxurTgGMakxj5SwNF7EixXxVPnfGADWDcygh5C1BMXqiL1AuVXOVFOsaydfLWGC8Kbh/JiL6H+12lYrNGUT9yJRIzRDi4XylMnrYwBtwCJjoHSi4exz5K2ih54utVAuzXZg6mnc9ied/mNRjj9d2HFD5mv0w/qTN/WFxEmtuZM/nMMag==";
        s[140] = "w01TnPP/F3Vni3fBdV32Bnbb4J1FcbaE+Xn44no5ug77U8FS1gSm3LqJ8yTyXduzl5v2dwBEfziEfTuyqgeLLsCYTBjXmYOIHQosEl6DyAknu4XK52eQW+Fes9eSs2Nq+G4aaR4y4leeFNmCoZ9BQmAAZr0LFkqnvqaKmBVgcgxPs7/8SQHnpqvaU6Y=";
        s[141] = "OfzIF38Tp5g1W5eVbrkrMe0Mm7e0wBYg5hVvLpn/5MW5OFcmRDuBp15ayRBnJ1sBI93+CNl0LwP8Q0z9IXFjTER5gHZ1KfG8NV+oacKNG7aYrbUftkSL/oPfRNPez6U0FuWgvVrXUB6cwKTWvwb9KoD7s6AGYRdH50ZgJdBniFD7dKoOQnJ/ECuTUXI+";
        s[142] = "4hoX0sjqlkSJPUq627iJkNYRbtD+V2qErCuTikaeRDEZvVHWvzdvwj4W1xxJjz+yHAN6z2EjCWidcSsVgTejQ1bH8uTzJgh/zq3yGUSsJoJWrecqxsge8bEBjkm+qUO8G3kAnC6FMjJ2NYQeXf6OK6OgsqyJwlHPTyAms2/IoYTB4iEqgIFG/2fNEJEIag==";
        s[143] = "M/dy14xNbZMRfHiKKFdmD/OrEB+8MexrRO8mMh0i5LrNA5WUtLXdwUfAysYmal94MSoNJfgmwGCoqNwlWZBW1kpQaPdqsrn2cvc6JcZW9FlOx07DERJGbZ6l6ofbzZWgF+yf+hvT6jnJvXBVCTT3lfO3qo4leNuEJwsuU6erXGC3Ch53uPtGIrdDUpcX6/U=";
        s[144] = "GgLr2hd3nK64IZv0JksKAT/yJNQ38ayuWyBnWLjXbEmT048UDppsrrnP6hikRo5v2TlHGhD2dcwG9NLK3Ph8IoIo9Wf2vZWBB+SMI9FpgZxBWLEjwHbOWsHaEQMVsQfk38EWQP0Fr6VyMKlEQfpsRkuCpp1KxscaxK7g5BgXUlb0a2x0F+C9hEB0OVPsj4JN";
        s[145] = "W9lKcLDqNGQAG/sKQNaRmeOUmLJ7GcMNqBaGZ659Rnjr6RTrfnmkp5Z9meALnwXoHjPjzSQDJnVYsY+xyMnuPgl6gMVAhAm+XprYVpsne4vt+7ojUqavVPBqLy5dtnhp1qfcnAiV5cZhHXX7NbxkUOzptjEGCQjnhSH4rPbZtgoIWE8Z6boF3l/thLnFX+AiiQ==";
        s[146] = "iYLn5h9lIhD/x9moaPRnTX6mJEJKThg4WXxS7IrR2zblH26uOkINz0dJNTJVets0ZNYDnsnT7J2iI3Y6hTVWPGoYU49J3B2LhCREs0DZQ3C7080FtiOcfHbfBLNn0DyCK1LeAC7YB/bNdiyhLqH8fKl+0+KhiPDIUBJY2e7IbZR/9t0sxJbIXx6cRvI5AXex12o=";
        s[147] = "SlRJEc7npTUvQq8SgBYKmVY/3wHYp2gsDxafN/JLUuEqEjmWMtW7fxASi+ePX4gmJJqLhD5t+AZxiCwYK3L3ceuJx4TiqVgJz8d6sc7fgWXluh1K+BcGPbZ7+Cq4Vsga7JEBVekviEZ5Ah4apNr8RkB7oMOUVPGxRcyyaVE4zBW+scA6c1yi/HQXddQ9rWyLUsVo";
        s[148] = "AAlbGR6ekLOzx4hpqZTUqVUQ0FL2CFpgCMOp6CuuUzkSnWXpUjvOiSDkNPgoTPgpgmg3uYvMsX43mkPGUGC9awDThXyGQh6u3WfWtmhiPRqXnjFek+EPd0LYXps71non6C9m7nUlYNWzBJ1YzrzWjlB5LLPBN8bsZG6RbdZkYMxJ9J5ta/c30m8wDDNuTm0nEE0ZVQ==";
        s[149] = "nWWbBhzObqwEFh/TiKREcsqLYbRjIcZflJpol16Mi4YDL6EZri22qRnTgrBtIY+HieTYWuLaCSk/B9WcYujoS6Jb5dyg3FQ6XF9bYaNQGx2w8DHgx0k2nqH/0U1sAU0kft32aD2orqCMIprbO1WJIt2auRnvcOTFoOax926nAkxvR3nrFVDevFjDbugpWHkGwic6G7o=";
        s[150] = "WNk1Rn2qtG+gk0AEewrgo+aRbNrG4CgQpOR8Uo7c2m2XQY8MVDu4uRA6rzYGGdgqTcICKky9MvHeJeNWVAXOxmA4EdXQ2xItFJdQtxBt56cad9FBXXsz21yVsPr5d453abi7T3XfHVTToekiOlxAJs+bpat9cFRbIdHghO9wc/ucoArT53vpYsnyeVnmZG2PX48lXpNS";
        s[151] = "wVmiO6mdf2aahrJlcmnBD0Qa58y8AvzXtJ54ImxgPLPn0NCQIrmUxzNZNTODE3WO6kZMECaT/REqT3PoOBp9stCHCFNXOM7979J44C1ZRU0yPCha00kQZBF5EmcLitVCz10tP8gG1fiIvMjwpd2ZTOaY4/g4NeJHLjJPll0c5nbH7n4v+1I+xG7/7k7G6N8sp21pbgpTYA==";
        s[152] = "OoiVZosI+KG0EZTu+WpH7kKISxmO1zRYaSPMBMW0AyRiC2iZVEkOMiKn12XPqIDSW/kVA58cvv/ysTAzKLTu78Uo+sVcJe3AtLdgeA9vORFELTP4v9DQ/mAmehe3N8xk+VTLY6xHWi6f4j9cTDW/BDyJSDRY00oYoHlvnjgHo4CHBo0sMGgX3CwcnK2hpMFVtB/3qPl6v2w=";
        s[153] = "97ZVsTYwD8VrgN1FOIRZ8jm8OMgrxG3o1aJoYtPVWXp9cjjlgXqTMZVsoWr3pr7pudw+LYo1Ejz3JpiUPHqWcZ2PWrWs7PR1akYGuwdCBHYvCGTcZYFe/yu1AB8w5zYsl1eJR45g0u1DlXfx5BUAUzc4yJDjc48Ls62bn8t0EJ7+30sWwifqKuz2EHpsqp1j/iMlwzKJGjGE";
        s[154] = "0NSYKTvBKKInwL9PJ/pWUWVX4gjF3igsA2qqQMsRew0lI1LcCB18eGCYk0AnyUCe99w5lWHGFUMMeH6DZciAylWGeDn19JdzVOTevBWk3LIujI1GvsEB3oVqf2Zl9IZeDGCT4+bQKBWvgcXHjysZfnn/5z9Xz06qrPqac5LfS36fDcwnkrUYQWDsL2Ike32ALmOnkcDjNq1BoA==";
        s[155] = "5ok+rYI4LCgGa2psGUN/fdkT2gQEToB9HRiFXQpe2YcQvEN2z7YlJCETx4jSWw06p5Y8tZcp08moKNYwUJ40DvPpGlDG+wUpFhC4kkfo6vj6TrCj5yoWJi5D+qdgH2T0JeWM80cYN0bsOsetdaqNhDONlYXZ2lVYkyVS/wzw8K5xX87EWktwOwFq/yYhuWCYJ9GZL7QuDipJjEE=";
        s[156] = "KHzTalU9wPSnIjh5g0eHi1HUaFufxJpXpjDe0N3wEKINqbgzhbj3Kf4qWjb2d1A+0Mlu9tYF/kA9ONjda5jYfRgCHm5mUrjU0TAyT7EQFZ2u6WFK/sFHP++ycJQk8k7KLPUWA5OWScy1EO+dYF4d0r6K5O+7H/rpknxN6M9FlP8sH83DXK1Sd+UXL32D+4flF580FaZ5B3Tkx3dH";
        s[157] = "RrJVxIKoDXtCviWMv/SXMO42Dn6UWOKDy2hh2ASXssT0e+G6m7F1230iJWlEN0wBR8p+BlTdBhQrn25098P3K16rBmZpzw/5dmesIJxhYPaM4GiaOgztFjuScTgkmV0Jl/vZ9eCXdEVNISeXkIixM4pssTFuUV7PY/Upzdj55rDKGLr1eT7AFVSNP30PhL8zZs8MANqKBeKBBDvtww==";
        s[158] = "sy4t5rFA75GRBE+Dqa9sQxjPluKt/JnEY54guHnKqccmx3HGiyJ0jUA+et4XO8Xg69wCA9xVxJZQL73z80mVfIf43HIKOxgxT2IjG7EKMOD/qx6NnMTve4BryggLtbLQUeRfhriQeY7h65tD9ierhccXoXDpGnmBn9m2BQP78y+Qhc4eIsa3LcxbQJUIwvURjFp/rgMD7lhOLboa/2Y=";
        s[159] = "Zs/BfFoWImYau2dZLb7JneeTQp7sQ06yonEq0Ya4BNOJGy/5dGH42ozt0PpP2IZ/S58X7esVgc6jA1y3Bcxj3MPoDjQJSZHFEtR3G31T8eF5OpPVC4dw9s9clllM05tvcemssLdcd85UP/xBaDrmpAl8ZDSc73zflK3nJw8e0HQFYntNnlZPFyyyBLHnLycb6Jlvq7F2OqrZR+FXZnL3";
        s[160] = "hdeDJuRnmb8q9EYec8+futT/CvqhpqoUdtmG6E31RrYJDs96M5Wfng90IEqrncZe4rVYDocRZK23dvqtJaPhTUBXXh42IyMlUnro69KI+075FvYYwgVaUd10r7ExWM5Z7DCQ2x8Tm1meK2YCTPkF1VXXexl1UjYCnRQuQxppdophMwroJK8VqlJbFFslchTSBFuI7wgcdy+f/LHMbMsusQ==";
        s[161] = "ClCCOv0mD9//LR0OisHfamxcTvmlwMLgAIQt3hbOjRPkXwEgaDzP0u6LN8BNwdOVw+LhrbzMI8zQzHvo7mGPkQICeeim/x+xmGPQtmWnXxCWiL1uf/8eR5Wuy9Er8skTB8rG4/ubb2ssvCkubObPAkMSOgFnX9UtxnCN4+nMNV2vvn4xMTSvvQyYWewfnlNkflTyva1epE9RVW2RqRtJikY=";
        s[162] = "Fs+AmFCnUr/imw8D0GpNidIP9qwW8yRAzmtqPS+vT6n5U4YFQcpgbznrYO4TPqkVF2oz1mpgLYIgx/u2XsrtljGX46LfY8OyUPaw4/da38QGngoIlS2cN01cgN3efSjMlnZFo1x8T9p0Nn1IgRgevOd5ezVUL7WdY7eeiE1pXXcGBgDYn7NDQph0dC6HDlBiS95bDFcZ+6FYigE4WybpsOHL";
        s[163] = "wgO4DdGZy9g13IuOhkJGJcToyLuCBVm9T/c8qY4NOheVU1NW2g8sPIo+RiEsSST8sx6+Jh/A/kaCxYvJ9CsgnBjZMMWRsd383HZAoJtkxwKvyoeXzzD+puFvqKQBEKrlBEwffXhLDoFQAW2ycYtBGztl0GsUtoOob2nv7ienx1xD6KNZNaxYx2ObRAYS/e8LS3pg5dku9MPBp1X12m8ZIXRAaw==";
        s[164] = "EkXt02SaRUIjFmoLxyO6N+giL4iA4fY0Exao+mjgEfZ+Wv6w95GXHBI1xlYMVLkOcnu9nescvcXQH0OrqL9uforEUTGTSg2ci67m4GrwAryMy+5eUo77Q5GpWKfsg8nDbD8a3gUI/9EE0sCNp7tMaKwoJ56cxhbG3TJYpqWTpq3/S3q76x+3ETL+zxh6EMh8MJPfWcIxlDS7evKqdPgS00KgUtk=";
        s[165] = "OuBqx5LFJroRbDn41+4azFHlKgw6bMgbsRGaK9UnPvi5xfmV4SLQ2YzIhopGi1F57L6vKukaW0XlFk/Ff5Td5IMC7U+kvXKlf8fGIIQ8FaHI0vbIX89OJlBqlICqftSNiVRxtaE+aCh0rBoDfgPwuC8qBC20I1O3ZLuKfeUVGkMOLEWeZLS6mmcn3cSERj9o/dEl8QYwQvhH+VG6YWF//yki1Vu8";
        s[166] = "SO/vDsqZDdImOdH19sZI7FUVhlx1EI0XRr8ArTuAG5F8LDK76Bct2C7fXTUowilXnJWhQxvbGiulkUGSuVjVP12zac9bShRpr1L3ucde7n1f9y/NcHJCwdqTLq7RYygItQ4ppQGiP9jXf2Dn/qmVZZTh+SY3AZCIS+OVo2LAiYJHWnzzoX8Zt+dOYiOA/ZQKZieVJlc8ks+2xqYPD55eH0btZn5hzA==";
        s[167] = "tZL/qACMO9SzmgJhWQMsbKgE5lPAEbxn3NR7504ilgArR8j7uv1KF46uQyjrkEnyBormYB/6nLGlHht62IQftMYf5gHpHFfTvukRKF8728yIYAAYHPQ/WjHzHdVSqUJqF2a8RE6SvvY+KSKWLMU3hjn1f6dqX599hYD7AnbPGTpFKDU5sLFOXbuynU1sPUhP+a4Hev9yNU6atLDo4CkX/Yq3FbpWVuQ=";
        s[168] = "GRe7uF1wH5/71B3vmGF+pN3H9PKO1tLwsnb0D4/Pm7Pu5KAe4OfelkfFIBgyjuoZrpeEkGZnb+qf+Kn7Kt1hDwYr/Mb9ewuwOXsbIpLQMgfh0I5XsPrWocduVzn+u/cm3cr0Z11zsx0AZjTqvslACkDqiquY41JhtGdc22RCvIYom2l+zzMIMyCPsHeCSB1MBu8EWK3iP7SD3dWttwzMg0xanoPDgk0U";
        s[169] = "8BDFu+29lptlGSjcZe7ghWaUgIzbuUpM5XDFbtJVQPEd3bAE0cGRlQE9EhKXi5J/IskYNrQ357tBhA+UNDNXCibT2AZGpzWAcwE6dP+14FuRL5Gxqh/teuPYKr5IIn7M3SbgOychZzLI7HGCvVhBUiJBu8orI3WmAIVcUhAsMGHsFveck/ZCXQA+Uq/WVnW1VNs6hSmIhsAFc51qsmsQK07z2Wptx4rRjw==";
        s[170] = "siPSXD4u36WYtTvvDzRlFPiuZMnRczrL3eA15955JDCc6/V2Cvu6m/HPO6JxogxO0aYTZ5tejYDOIZgBy40DgUZMqPJ2IpYjsmUbjjJU8u/OpwhMon525m3v0EYlvyj2Qp3pwFKDkvncK3aNjN3KaaX6HuIy6kyqsDl0BTEnB5iJyHLRBCkeznTK019u48Yfsrz2oGuZcWzNj5/vKMdxQPiyJ9EHyox8Ark=";
        s[171] = "+/14PnFQVZ7BTHKUvkTtRrYS7WnPND5gZ5byMhUrDLkJa6UPBV7z0nrDMifEo/dQfUq3EjCiG6xGVhrUvAzgxqOQZTW1Y9p9M0KWW+E0XvCQppHFpuMqF1vYsF0OD6AMiE9JtGnWs3JcaWP/XBF/CvhQlFGbHi3fbrD/haTEBnmpJWBgMdKribdbXHtBSFZ2MzCX2eDtxoDdRdEVGs4v/q8gVBS+WsnZ3TTF";
        s[172] = "31I1ja+B+aopKkztGzJYvJEWAshyoAAV5yve4LnP0kdImUQaVETSuo5CDIYr7zM8MCD1eYPpLicmGnA+C927o9QGAVL3ctO/DCWhNinW7NmeYIM+o4diKBkDPjHmSWa+nq4nr+gOap4CtwL2wW2B5Yqt26pKgN9uAU5CmTL26hYFgMEOZfrkQ7XdYGy2CN8RJLmjeSFVVNBG/FTaK7tpuy0LQSkko6wczBYGeg==";
        s[173] = "XbRfDqGe3eeI1tHx8UnPneDB57N8VeSSzXzVCNSgxOEfd6d/un5CDxHG+m4w3tIbtSky4R2+zMF+S5cRvTOwZ/veegYtLKTxA0mVedWLFkfh/v4NgPJ+NEU+cylbSSZLAeBofDvoJwnYKujN2KFa8PGAxr3Y8ry3qdkS8Ob1ZiHYAmLvKS9sGb/vjTvRy+a4Q7kOepsm7PYisinKelBAvDnjli6/lOutGrenjX4=";
        s[174] = "jGEj/AaBefac9uOcmGuO9nH+N+zMsC4qAe6ZUEMMIXdTGnSWl7Xt0/nKqyOj3ZH249HwkJ8bn5C+0bzOpQ1eA3PxEq6RfKMrjHJPJmTZXrSESTjfj3oNLU/CqqDOqd8znTgN6nvnUdCeStLMh9bmWF1+0G11nDwg6GQWWQ0zjVDTq5j7ocXcFOyUcu0cyl5YDcUP0i2mA2JullInU2uBte7nToeSGB3FJxKueBbv";
        s[175] = "RAzNCxlP2S/8LfbGtlSDShox8cSgmJMOc2xPFs8egZVJiwlmnS3aBWKPRbbxkZiVVYlu4GNJNwbocc6dgrl28HXAsYikE5wwoQ1MeOJWU3zzFiYENh7SLBQfjVPQHucctr8P6Rl7YL5wHc+aC+m92R3bnzm5rp1PeHm7uzy2iUUN0cgfbwJ4FrpXhVMTsAUpTbg1+037EWcGOuxir4dG2xBfgOwa+ejFHkw7y0LWRw==";
        s[176] = "08hmZptBGKKqR6Qz9GNc2Wk1etgU/KogbiPQmAh5IXlTBc97DuEToL4Bb889nfObVQ/WelmiCS8wEjSBdmnlkkU7/b5UT3P4k1pB6ZxPH9Qldj5aazkA/yCb0kzDfJlcdFOh1eAcu5LvwTXOizmPwsDvJEnOkaDZrKESZshsHU2A6Mx6awk9/orf6iBlJHQIIH3l4o3b1gx2TNb/hUgdAlwtQDhvKO3skB0PS+rcWAw=";
        s[177] = "0GhrgbSSHPLWtyS2mnSxrNAj/dyrFQcxIgPjT7+78SZ1ZTGc03vsmlZ4Z/bOO84E9yKblaI5dSHVXrx57L0kikL8tgKCsAkUNO3l/4zv5FfCrRTgGx4sFTFB1NNcLcwagkvFzde764DjYmj4YZhYsXSZCVKi0uu5M8fpgGDZ9UMSFR008cbhaIoFLWSANqiNJYSvTQZhGWfLtIPGLN+gIOMcaKhx1b5vg6OYSz6ScAM/";
        s[178] = "2VGiMV/f2hNZAjw3fdeHx/wRIVzeP018lZynzwSySG/zQBxyRmi3YmKVZmh3aJunuiqmvdt0kJ6lX7M8BajYHPCBkqJOx8oPJ/K1oADxVgnavZ69dKYrSy9/Pm6sHxjFrdSz9TelUK9sgoFTWS6GxgzWEqXRBDDpGUnsNbSEcWLPKVLNNoYAcltY98JZaNSZBXcpa9FeSN7sVU43q2IEcDx3ZkJzRJpl/lb7n+ivMwX/OQ==";
        s[179] = "iMSCh1m5vct3C7LEn5wKRYtalzvG6pKahG19rTb6Z9q7+buDsML5yM6NqDvoVxt3Dv7KRwdS3xG/Pyb7bJGvQ2a4FhRnTa4HvPvl3cpJdMgCCvsXeXXoML4pHzFlpP0bNsMoupmhQ0khAW51PAr4B165u1y5ULpruxE+dGx/HJUQyMfGhOSZ5jDKKxD5TNYQkDEY28Xqln6Fj8duzQLzMIgSoD8KGZKD8jm6/f8Vwvf43NE=";
        s[180] = "hN4+x/sK9FRZn5llaw7/XDGwht3BcIxAFP4JoGqVQCw8c5IOlSqKEOViYss1mnvko6kVrc2iMEA8h8RssJ4dJBpFDZ/bkehCyhQmWpspZtAvRN59mj6nx0SBglYGccPyrn3e0uvvGJ5nYmjTA7gqB0Y+FFGAYwgAO345ipxTrMFsnJ8a913GzpobJdcHiw5hfqYK2iqo8STzVljaGMc5WSzP69vFDTHSS39YSfbE890TPBgm";
    }

    /* These are chunked versions of the strings above (chunked by commons-codec-1.3.jar) */
    private static void initCHUNKED_STRINGS() {
        final String[] c = CHUNKED_STRINGS;
        c[0] = "";
        c[1] = "uA==\r\n";
        c[2] = "z9w=\r\n";
        c[3] = "TQ+Z\r\n";
        c[4] = "bhjUYA==\r\n";
        c[5] = "1cO9td8=\r\n";
        c[6] = "sMxHoJf5\r\n";
        c[7] = "jQcPlDsZzw==\r\n";
        c[8] = "TAaPnfW/CYU=\r\n";
        c[9] = "cXTZuwzXPONS\r\n";
        c[10] = "Ltn/ZTV4IjT6OA==\r\n";
        c[11] = "6fh+jdU31SOmWrc=\r\n";
        c[12] = "E/5MoD4qCvDuTcFA\r\n";
        c[13] = "2n9YyfCMyMXembtssQ==\r\n";
        c[14] = "qBka3Bq6V31HmcmHjkY=\r\n";
        c[15] = "WvyZe6kQ8py7m25ucawJ\r\n";
        c[16] = "oYpxMy6BSpZXhVpOR6dXmA==\r\n";
        c[63] = "yexFaNKaP+VkVwEUvxQXbC0HSCi/srOY7c036lT25frs4xjIvp214JHCg7OL/XZW3IMe6CDgSMCa\r\naI91eRgM\r\n";
        c[64] = "vkqgnuJ3plxNtoveiMJuYLN6wZQLpb3Fg48/zHkdFuDucuMKWVRO/niFRgKsFqcxq6VgCxwQbePi\r\nP9sRnxz7wg==\r\n";
        c[65] = "YHks3GCb2uVF47Y2tCieiDGJ879Rm5dBhQhQizYhfWOo+BrB2/K+E7wZWTid14lMpM60+5C0N9JN\r\nKAcBESeqZZI=\r\n";
        c[66] = "z8551jmQp/Qs95tw226+HCHWruKx/JvfBCfQ5p0fF77mkSpp66E552ik2gycrBBsMC/NbznAgTZo\r\nMzZxehfJwu49\r\n";
        c[67] = "VsR2whqq/qQm342+TNz1lgOZMoWbpCz+kj2zflq0nx7S/ReEVPUJcqgMtVzrr2FVQPfBAH5VRjR+\r\nhkxwv4bssQms8Q==\r\n";
        c[68] = "5xmIp8dUJZzkisIkuEPsnPXvzDeMo48qWFfHIn2av3jr5qoZHCs0LfNEyepC3v2sa0nCU0FlqsmD\r\nyTI5/2jt5zsLtV0=\r\n";
        c[69] = "tGEgXjglUB9NbCtiS88AetLtRqhCAnhzOUKVgvbJZHqOA6x8uOorA1t3NcaIA00ZqbPYALu4LzIm\r\n4i4lAL9QgiH/Jg7b\r\n";
        c[70] = "gFtxEhYJahDmU5dpudYs6ZTsqAx+s2j+06A0zeyb3U7nhZFsbkCDlds0EYUoqerZqZPm7F6CDOOD\r\n3dU7nYmelE0DxyMO9A==\r\n";
        c[71] = "j/h/1JygYA5bjttxzQxr5gBtgh+AYVozhF4WgvcU/g49v0hUy6FdhfZewGK+Phtzj7RabI5p2zXy\r\nzvkmLQdFhdI5Um4O5sw=\r\n";
        c[72] = "m+kYVGojIR5pgbz7pGJm2g+3qqk7fhl3cowa2eVrhki7WofyywnRezqTxOkBgVFz6nKs8qxpbbbz\r\nALctcPeMsp9dpXUfuUJr\r\n";
        c[73] = "pPaGnMr0UYmldnJVk/F+3WCJJ1r2otvD5KJdt2u1RnS6LwhHhwLCqfW8O/QEg43WdxKomGL/JM33\r\ntn/B9pMPoIU0QTGjq2GRow==\r\n";
        c[74] = "mOxzGyym6T/BxCV5nSiIYMlfAUmCN7gt7+ZTMg1kd8Ptirk+JF5dk8USbWBu/9ZvNg5ZuiJCeGwf\r\naVpqpZ3K9ySF7C87Jvu1RUE=\r\n";
        c[75] = "VYLOIE4DnhxJn3FKS/2RHHHYLlZiGVdV/k4uBbtAYHUSTpRzaaYPGNAVjdNwbTHIihmeuk/5YQUy\r\n8NFsxIom+Li7bnWiBoHKBPP7\r\n";
        c[76] = "7foMpJ0TknCatjUiSxtnuKSiz4Qvl/idWY9UKiTljoQHZ+C8bcUscnI/bZr13e6AvyUh47MlpdGv\r\nzIm05qUdMWWLoZJOaGYvDmvrWQ==\r\n";
        c[77] = "jxQSGiFs+b1TfE4lDUAPUPJ0SWeUgl03rK6auieWJcarDIHM97gGOHMmyHudRMqkZmIkxYRgYgCC\r\nUg/JeU91OZD3tL4U+wNhShywe88=\r\n";
        c[78] = "UGmH/gl7t3Dk801vRhRDEbgsfShtHZ1gZQn4KNZ5Qsw3WiGjW0ImInVHa+LSHBzLUjwC0Z3nXO4i\r\n4+CiKYqAspOViE6WqVUY8ZSV0Og4\r\n";
        c[79] = "wdEoYmJuRng2z2IkAiSgJ1CW2VE7H7oXpYWEFFO8nG0bZn7PHhT8KyhaO2ridl8eUEysja0VXFDy\r\nQqSgZnvdUKrCGepWGZbw0/0bDws3Ag==\r\n";
        c[80] = "5kZqgrUbt+rN5BWAddeUtm9TGT43vYpB6PeyQwTyy9Vbr0+U/4Qzy0Iw37Ra293HmkmpgQzuScVp\r\ncIiFGXPAFWwToR+bovwu7aXji/FnMwk=\r\n";
        c[81] = "E467MMmJbmwv8Omc2TdcyMr/30B8izWbf+CAuJtw67b1g9trhC6n4GYnXjeW9DYvmWoIJPx0zvU/\r\nQ+gqv0cteg2bx9P2mrgMDARb6egowqjx\r\n";
        c[82] = "Vpt8hYb4jx1F+7REX7K65v6eO5F1GDg/K8SVLWDSp0srupYEQkBVRxnB9dmhSo9XHpz4C8pRl8r8\r\n2fxXZummEf4U2Oh0Dip5rnNtDL+IJvL8lQ==\r\n";
        c[121] = "hf69xr9mtFf4N3j2uA9MgLL5Zy94Hjv+VQi94+LS8972JJgDHCQOwP5whdQkV+SJpXkiyHGaSsQ4\r\nfhepPwzuZcEpYER+beny1j+M0HSZe36MdRIhlTqno+4qsXckL0CjXsYkJJM0NAfOYjHAus5G1bgi\r\n9VhmiMfAMA==\r\n";
        c[122] = "yKzTh5hPp9/PBeZtrZXsFHAR9ywOM3hRaBDkgG9E09wFW8MZD0xMGegcp0QrTJcP8QYOaYhTDVim\r\nPqsNTVOmjdjkvS+2WhjJW4mVOXQ8KID91yaBtPo+DStL5GMgctcP5MoVf1Vp8w/mYtofqbllfDm5\r\nNfYzh2A7ijY=\r\n";
        c[123] = "csFmwvDzoO4IO6ySDA4B2V7emEetAwCgO66zzlfWb3KDrPfFZc3Jgr4+dlaUkEIDHYeLHETdTssW\r\nOl2KrPHBEsULpDTR+3OhurXb1Qr2NvHiHFuqT3Geb6EYw2albfTmXxch82ablt4WKl4qPcSey54v\r\n6tsCuUuZzrkt\r\n";
        c[124] = "5InxvKwpoCV7EK78OzU/tL9/NmK14Prw9tOCAyK+xpUNLZriuVEVdgpoZ05rliufaUDGHH8dPAem\r\n8G9DN/VOPktB6vXKtc2kMUgnMTiZwv/UVd+xyqnT8PLEdNQ8rCWxyiCcLdXFf0+xcE7qCcwzC+D7\r\n+cRW+i6dnpZkyw==\r\n";
        c[125] = "cEx7oTsSHWUFPj92cstdy5wGbRcxH+VRWN8kaNTTCPWqSckyU9Xk/jj5/gj9DFwjfsCSp60xutf4\r\n/rFanjtwqtRg6dJLP4JAgpOKswDlHi6Vt7zF/w7HidMf2sdtlsqzayZmT2Hn7iOo3CExzr5Z5Jfm\r\nMFAX8R9peUN4t5U=\r\n";
        c[126] = "AeXetVbj+7mmGiCs3BGUSZDLlq2odMsN8JAHQM64Cly8y5jw75PpISocWRFFQmmXYP7ckKmtuhIv\r\nD69HtZxGhNRsl1l1gXzKFhsWykcRtG87F8sS1Uv/i6QvGiRIDVEGGIzWrzRIISkBb9wCxJ2HESfl\r\neWwrz/GqryjoN26B\r\n";
        c[127] = "aj1/8/+U8VO3D2iAwvQXZ4H0KwtuzDm4JCC8+22ccqk+UzKvqjGs87XngwfsMfSkeGVAi6VB6tfN\r\nJTjYctaj7R8dwh2PIfLSrvaphw4wNB2REjplnPojoOb9bNUNtUvdK3b1bArOWugIRJWLnMl72yEH\r\nFb1iBfBmn7uIa7KT2Q==\r\n";
        c[128] = "kiMuF/1CMRlgoS/uLKn1mNZFZNHJNkRQnivOrzj8HQAagwzvTXvsGgI9hXX3qaeZL9/x/Oq+Y5F6\r\nDh+wXo+0kp6JagFjvbTJcQSowFzIwg7/V9sans0NE0Ejow5BfZKvihFI46sHiALl1qzoXqLQq+5f\r\nZGIFRyyY8wFW1uiNu9k=\r\n";
        c[129] = "YXmCWhiNz4/IhyxQIYjgNvjX+XwDiPTSBMaFELm5X8Y4knGRnkF4/zix8l9wHBb+7Cgfrr46rF7e\r\niIzaAFLjLjjewy63duBJiVbEWjqFO0fu6T9iIjuEaF2sTppvuZNPHx80vN+HLAnAVmgFESw5APXW\r\nn15dizvuUfVHd5isCqbA\r\n";
        c[130] = "GJfUoBivW5uqDpUxTRfjGWNYfN3/dTFtdRqCIH5L2c1nWX0dgY3ba1+fW/YX1Oh5aw4lC6BIiiTh\r\njJoV1VrNnlXzbcUcMy+GsDUUB8Qe8lBvfe/t4RmNPB0hVgrS89ntbuU0SsCmWw+9DqM3nidPebAN\r\nKERig1zZTBBKgpVf7HPFCA==\r\n";
        c[131] = "eTerNs7dOqJAxAxcMQRnUDc2cBj2x0jU1g1D3G+b22kDz7JBzOy/mxhGXAQ3130lavWMhImSBkRe\r\nU+z0A13EYVMUv9PFzD747KCvns+SCo52YNHB0896b4l47q8hu8jsD17TZ2uWWJhS4cNnSE1jeM6N\r\noXGKvf90yxfzwucNYc4RdaQ=\r\n";
        c[132] = "lbrGsjbzE521q8tzVHV7vcTPchUnzI/UfeR2R+cmOa29YljPWLht9Wx2JrjiKv4Of5nXe7kvhi+L\r\nYUuFVqgaqIFhC/PLbqOFiT2VZcXorToaRT9CLiqV5b6nHN/Txz6SI7MiD3hnk7psDbglPOo+ytqc\r\n9sFHj7UkR1ZctQjwFYwJjlxf\r\n";
        c[133] = "mQwAPzYzfxz9FXEiZ6M8u1oN3EJbFYmNVfpm+j0DqgU+OPI4URHBIrF4xvdMvAPn0WuarbQy/ZVN\r\n0eKL7S4K3Mvan0flAwaZdI+e5HpkfxOoGTp8Dk5EFTXjmZ/s+GonePEQEGNVPL1WYoD6xXqAAvML\r\nKtyrFcpoiGS9eDBlsZDQzPzz/g==\r\n";
        c[134] = "3G6d12UY4l5W7Nnw0BL0HnViVg9SdEuLMqeZwy0RlJR/Ytcgd/mIxIuXXAlGhvhoX6Xc2BGU7RpT\r\ni1jYKzA86yul0j96dbcE4OtrP9lUBJlcY9eWz59dvLqKxwt3cEOBwrPf69MHuIa256es3AOCobfC\r\n8RQScW0PQ0QUa1VHB/eXSsVTSWg=\r\n";
        c[135] = "AxgrZKTFk5JvLC3DBACHwp266FxKI/yn9F+1tYkzL57RVs5HCJYS47VuG0T0E2wqzHqcLKPQMZWU\r\n7vbRoyMGNL3ZtaHoZqTqcq9KWtODC+OnEvSS7+1P4SmQDuyL2MJ/eJABJKNcu1K/Lk0buAaO0FvX\r\n6OGBcPzu1+dv/ZkwuORK07qRnxqQ\r\n";
        c[136] = "atkG8l2U/Nnm+zLu7zjenrfcAVQJMUqHimFZ3cQfaUp4qyrFn1UiwZ33j66Vt63eVaT/FXx+LlEn\r\nsHn6ATPBMp3iEYoBJYyNpjz/zROhBbcznQAMdWUTcyKInvnG3x6ExgBXpyqfxxp/Pm8by7LlSUT5\r\nMKHdcu+TEfUXRokCr2iPnt7NYsNDfA==\r\n";
        c[137] = "dciU1mSHGcBxOAVBgJx75J6DaQdBZfTIzQ04WhkkdRMupOZWSnv19xhz8hiO+ZnbBtDJuO5rHsUG\r\nzH/jYacfZyCQ924roRvkh3T1yxsLq3abZJUCD9HYnPTELVhv1+cEF4aoO3vGOu2FpfLUn5QTd0Po\r\nzsIqcqZVB2V57B7DjfizUe3D8Yf5Vco=\r\n";
        c[138] = "dgR1PPacBvtILBmg33s9KWtuc67ndh3rCHZ/lIN7sENgbFqJQy5DC3XIeHTV7oWd+tJQaXxoC71/\r\nSU7Rz6OClAMKXLbMz8U6RPiqn3M7MRCQcDfNjA5cCNknXT9Ehz/IZF/7lcWrwxBKYm4B98lPkpZt\r\nR2QHndiQ3venzWrP0P5y27mReaFuaJ++\r\n";
        c[139] = "1Q8rfp1HuGsxurTgGMakxj5SwNF7EixXxVPnfGADWDcygh5C1BMXqiL1AuVXOVFOsaydfLWGC8Kb\r\nh/JiL6H+12lYrNGUT9yJRIzRDi4XylMnrYwBtwCJjoHSi4exz5K2ih54utVAuzXZg6mnc9ied/mN\r\nRjj9d2HFD5mv0w/qTN/WFxEmtuZM/nMMag==\r\n";
        c[140] = "w01TnPP/F3Vni3fBdV32Bnbb4J1FcbaE+Xn44no5ug77U8FS1gSm3LqJ8yTyXduzl5v2dwBEfziE\r\nfTuyqgeLLsCYTBjXmYOIHQosEl6DyAknu4XK52eQW+Fes9eSs2Nq+G4aaR4y4leeFNmCoZ9BQmAA\r\nZr0LFkqnvqaKmBVgcgxPs7/8SQHnpqvaU6Y=\r\n";
        c[141] = "OfzIF38Tp5g1W5eVbrkrMe0Mm7e0wBYg5hVvLpn/5MW5OFcmRDuBp15ayRBnJ1sBI93+CNl0LwP8\r\nQ0z9IXFjTER5gHZ1KfG8NV+oacKNG7aYrbUftkSL/oPfRNPez6U0FuWgvVrXUB6cwKTWvwb9KoD7\r\ns6AGYRdH50ZgJdBniFD7dKoOQnJ/ECuTUXI+\r\n";
        c[142] = "4hoX0sjqlkSJPUq627iJkNYRbtD+V2qErCuTikaeRDEZvVHWvzdvwj4W1xxJjz+yHAN6z2EjCWid\r\ncSsVgTejQ1bH8uTzJgh/zq3yGUSsJoJWrecqxsge8bEBjkm+qUO8G3kAnC6FMjJ2NYQeXf6OK6Og\r\nsqyJwlHPTyAms2/IoYTB4iEqgIFG/2fNEJEIag==\r\n";
        c[143] = "M/dy14xNbZMRfHiKKFdmD/OrEB+8MexrRO8mMh0i5LrNA5WUtLXdwUfAysYmal94MSoNJfgmwGCo\r\nqNwlWZBW1kpQaPdqsrn2cvc6JcZW9FlOx07DERJGbZ6l6ofbzZWgF+yf+hvT6jnJvXBVCTT3lfO3\r\nqo4leNuEJwsuU6erXGC3Ch53uPtGIrdDUpcX6/U=\r\n";
        c[144] = "GgLr2hd3nK64IZv0JksKAT/yJNQ38ayuWyBnWLjXbEmT048UDppsrrnP6hikRo5v2TlHGhD2dcwG\r\n9NLK3Ph8IoIo9Wf2vZWBB+SMI9FpgZxBWLEjwHbOWsHaEQMVsQfk38EWQP0Fr6VyMKlEQfpsRkuC\r\npp1KxscaxK7g5BgXUlb0a2x0F+C9hEB0OVPsj4JN\r\n";
        c[145] = "W9lKcLDqNGQAG/sKQNaRmeOUmLJ7GcMNqBaGZ659Rnjr6RTrfnmkp5Z9meALnwXoHjPjzSQDJnVY\r\nsY+xyMnuPgl6gMVAhAm+XprYVpsne4vt+7ojUqavVPBqLy5dtnhp1qfcnAiV5cZhHXX7NbxkUOzp\r\ntjEGCQjnhSH4rPbZtgoIWE8Z6boF3l/thLnFX+AiiQ==\r\n";
        c[146] = "iYLn5h9lIhD/x9moaPRnTX6mJEJKThg4WXxS7IrR2zblH26uOkINz0dJNTJVets0ZNYDnsnT7J2i\r\nI3Y6hTVWPGoYU49J3B2LhCREs0DZQ3C7080FtiOcfHbfBLNn0DyCK1LeAC7YB/bNdiyhLqH8fKl+\r\n0+KhiPDIUBJY2e7IbZR/9t0sxJbIXx6cRvI5AXex12o=\r\n";
        c[147] = "SlRJEc7npTUvQq8SgBYKmVY/3wHYp2gsDxafN/JLUuEqEjmWMtW7fxASi+ePX4gmJJqLhD5t+AZx\r\niCwYK3L3ceuJx4TiqVgJz8d6sc7fgWXluh1K+BcGPbZ7+Cq4Vsga7JEBVekviEZ5Ah4apNr8RkB7\r\noMOUVPGxRcyyaVE4zBW+scA6c1yi/HQXddQ9rWyLUsVo\r\n";
        c[148] = "AAlbGR6ekLOzx4hpqZTUqVUQ0FL2CFpgCMOp6CuuUzkSnWXpUjvOiSDkNPgoTPgpgmg3uYvMsX43\r\nmkPGUGC9awDThXyGQh6u3WfWtmhiPRqXnjFek+EPd0LYXps71non6C9m7nUlYNWzBJ1YzrzWjlB5\r\nLLPBN8bsZG6RbdZkYMxJ9J5ta/c30m8wDDNuTm0nEE0ZVQ==\r\n";
        c[149] = "nWWbBhzObqwEFh/TiKREcsqLYbRjIcZflJpol16Mi4YDL6EZri22qRnTgrBtIY+HieTYWuLaCSk/\r\nB9WcYujoS6Jb5dyg3FQ6XF9bYaNQGx2w8DHgx0k2nqH/0U1sAU0kft32aD2orqCMIprbO1WJIt2a\r\nuRnvcOTFoOax926nAkxvR3nrFVDevFjDbugpWHkGwic6G7o=\r\n";
        c[150] = "WNk1Rn2qtG+gk0AEewrgo+aRbNrG4CgQpOR8Uo7c2m2XQY8MVDu4uRA6rzYGGdgqTcICKky9MvHe\r\nJeNWVAXOxmA4EdXQ2xItFJdQtxBt56cad9FBXXsz21yVsPr5d453abi7T3XfHVTToekiOlxAJs+b\r\npat9cFRbIdHghO9wc/ucoArT53vpYsnyeVnmZG2PX48lXpNS\r\n";
        c[151] = "wVmiO6mdf2aahrJlcmnBD0Qa58y8AvzXtJ54ImxgPLPn0NCQIrmUxzNZNTODE3WO6kZMECaT/REq\r\nT3PoOBp9stCHCFNXOM7979J44C1ZRU0yPCha00kQZBF5EmcLitVCz10tP8gG1fiIvMjwpd2ZTOaY\r\n4/g4NeJHLjJPll0c5nbH7n4v+1I+xG7/7k7G6N8sp21pbgpTYA==\r\n";
        c[152] = "OoiVZosI+KG0EZTu+WpH7kKISxmO1zRYaSPMBMW0AyRiC2iZVEkOMiKn12XPqIDSW/kVA58cvv/y\r\nsTAzKLTu78Uo+sVcJe3AtLdgeA9vORFELTP4v9DQ/mAmehe3N8xk+VTLY6xHWi6f4j9cTDW/BDyJ\r\nSDRY00oYoHlvnjgHo4CHBo0sMGgX3CwcnK2hpMFVtB/3qPl6v2w=\r\n";
        c[153] = "97ZVsTYwD8VrgN1FOIRZ8jm8OMgrxG3o1aJoYtPVWXp9cjjlgXqTMZVsoWr3pr7pudw+LYo1Ejz3\r\nJpiUPHqWcZ2PWrWs7PR1akYGuwdCBHYvCGTcZYFe/yu1AB8w5zYsl1eJR45g0u1DlXfx5BUAUzc4\r\nyJDjc48Ls62bn8t0EJ7+30sWwifqKuz2EHpsqp1j/iMlwzKJGjGE\r\n";
        c[154] = "0NSYKTvBKKInwL9PJ/pWUWVX4gjF3igsA2qqQMsRew0lI1LcCB18eGCYk0AnyUCe99w5lWHGFUMM\r\neH6DZciAylWGeDn19JdzVOTevBWk3LIujI1GvsEB3oVqf2Zl9IZeDGCT4+bQKBWvgcXHjysZfnn/\r\n5z9Xz06qrPqac5LfS36fDcwnkrUYQWDsL2Ike32ALmOnkcDjNq1BoA==\r\n";
        c[155] = "5ok+rYI4LCgGa2psGUN/fdkT2gQEToB9HRiFXQpe2YcQvEN2z7YlJCETx4jSWw06p5Y8tZcp08mo\r\nKNYwUJ40DvPpGlDG+wUpFhC4kkfo6vj6TrCj5yoWJi5D+qdgH2T0JeWM80cYN0bsOsetdaqNhDON\r\nlYXZ2lVYkyVS/wzw8K5xX87EWktwOwFq/yYhuWCYJ9GZL7QuDipJjEE=\r\n";
        c[156] = "KHzTalU9wPSnIjh5g0eHi1HUaFufxJpXpjDe0N3wEKINqbgzhbj3Kf4qWjb2d1A+0Mlu9tYF/kA9\r\nONjda5jYfRgCHm5mUrjU0TAyT7EQFZ2u6WFK/sFHP++ycJQk8k7KLPUWA5OWScy1EO+dYF4d0r6K\r\n5O+7H/rpknxN6M9FlP8sH83DXK1Sd+UXL32D+4flF580FaZ5B3Tkx3dH\r\n";
        c[157] = "RrJVxIKoDXtCviWMv/SXMO42Dn6UWOKDy2hh2ASXssT0e+G6m7F1230iJWlEN0wBR8p+BlTdBhQr\r\nn25098P3K16rBmZpzw/5dmesIJxhYPaM4GiaOgztFjuScTgkmV0Jl/vZ9eCXdEVNISeXkIixM4ps\r\nsTFuUV7PY/Upzdj55rDKGLr1eT7AFVSNP30PhL8zZs8MANqKBeKBBDvtww==\r\n";
        c[158] = "sy4t5rFA75GRBE+Dqa9sQxjPluKt/JnEY54guHnKqccmx3HGiyJ0jUA+et4XO8Xg69wCA9xVxJZQ\r\nL73z80mVfIf43HIKOxgxT2IjG7EKMOD/qx6NnMTve4BryggLtbLQUeRfhriQeY7h65tD9ierhccX\r\noXDpGnmBn9m2BQP78y+Qhc4eIsa3LcxbQJUIwvURjFp/rgMD7lhOLboa/2Y=\r\n";
        c[159] = "Zs/BfFoWImYau2dZLb7JneeTQp7sQ06yonEq0Ya4BNOJGy/5dGH42ozt0PpP2IZ/S58X7esVgc6j\r\nA1y3Bcxj3MPoDjQJSZHFEtR3G31T8eF5OpPVC4dw9s9clllM05tvcemssLdcd85UP/xBaDrmpAl8\r\nZDSc73zflK3nJw8e0HQFYntNnlZPFyyyBLHnLycb6Jlvq7F2OqrZR+FXZnL3\r\n";
        c[160] = "hdeDJuRnmb8q9EYec8+futT/CvqhpqoUdtmG6E31RrYJDs96M5Wfng90IEqrncZe4rVYDocRZK23\r\ndvqtJaPhTUBXXh42IyMlUnro69KI+075FvYYwgVaUd10r7ExWM5Z7DCQ2x8Tm1meK2YCTPkF1VXX\r\nexl1UjYCnRQuQxppdophMwroJK8VqlJbFFslchTSBFuI7wgcdy+f/LHMbMsusQ==\r\n";
        c[161] = "ClCCOv0mD9//LR0OisHfamxcTvmlwMLgAIQt3hbOjRPkXwEgaDzP0u6LN8BNwdOVw+LhrbzMI8zQ\r\nzHvo7mGPkQICeeim/x+xmGPQtmWnXxCWiL1uf/8eR5Wuy9Er8skTB8rG4/ubb2ssvCkubObPAkMS\r\nOgFnX9UtxnCN4+nMNV2vvn4xMTSvvQyYWewfnlNkflTyva1epE9RVW2RqRtJikY=\r\n";
        c[162] = "Fs+AmFCnUr/imw8D0GpNidIP9qwW8yRAzmtqPS+vT6n5U4YFQcpgbznrYO4TPqkVF2oz1mpgLYIg\r\nx/u2XsrtljGX46LfY8OyUPaw4/da38QGngoIlS2cN01cgN3efSjMlnZFo1x8T9p0Nn1IgRgevOd5\r\nezVUL7WdY7eeiE1pXXcGBgDYn7NDQph0dC6HDlBiS95bDFcZ+6FYigE4WybpsOHL\r\n";
        c[163] = "wgO4DdGZy9g13IuOhkJGJcToyLuCBVm9T/c8qY4NOheVU1NW2g8sPIo+RiEsSST8sx6+Jh/A/kaC\r\nxYvJ9CsgnBjZMMWRsd383HZAoJtkxwKvyoeXzzD+puFvqKQBEKrlBEwffXhLDoFQAW2ycYtBGztl\r\n0GsUtoOob2nv7ienx1xD6KNZNaxYx2ObRAYS/e8LS3pg5dku9MPBp1X12m8ZIXRAaw==\r\n";
        c[164] = "EkXt02SaRUIjFmoLxyO6N+giL4iA4fY0Exao+mjgEfZ+Wv6w95GXHBI1xlYMVLkOcnu9nescvcXQ\r\nH0OrqL9uforEUTGTSg2ci67m4GrwAryMy+5eUo77Q5GpWKfsg8nDbD8a3gUI/9EE0sCNp7tMaKwo\r\nJ56cxhbG3TJYpqWTpq3/S3q76x+3ETL+zxh6EMh8MJPfWcIxlDS7evKqdPgS00KgUtk=\r\n";
        c[165] = "OuBqx5LFJroRbDn41+4azFHlKgw6bMgbsRGaK9UnPvi5xfmV4SLQ2YzIhopGi1F57L6vKukaW0Xl\r\nFk/Ff5Td5IMC7U+kvXKlf8fGIIQ8FaHI0vbIX89OJlBqlICqftSNiVRxtaE+aCh0rBoDfgPwuC8q\r\nBC20I1O3ZLuKfeUVGkMOLEWeZLS6mmcn3cSERj9o/dEl8QYwQvhH+VG6YWF//yki1Vu8\r\n";
        c[166] = "SO/vDsqZDdImOdH19sZI7FUVhlx1EI0XRr8ArTuAG5F8LDK76Bct2C7fXTUowilXnJWhQxvbGiul\r\nkUGSuVjVP12zac9bShRpr1L3ucde7n1f9y/NcHJCwdqTLq7RYygItQ4ppQGiP9jXf2Dn/qmVZZTh\r\n+SY3AZCIS+OVo2LAiYJHWnzzoX8Zt+dOYiOA/ZQKZieVJlc8ks+2xqYPD55eH0btZn5hzA==\r\n";
        c[167] = "tZL/qACMO9SzmgJhWQMsbKgE5lPAEbxn3NR7504ilgArR8j7uv1KF46uQyjrkEnyBormYB/6nLGl\r\nHht62IQftMYf5gHpHFfTvukRKF8728yIYAAYHPQ/WjHzHdVSqUJqF2a8RE6SvvY+KSKWLMU3hjn1\r\nf6dqX599hYD7AnbPGTpFKDU5sLFOXbuynU1sPUhP+a4Hev9yNU6atLDo4CkX/Yq3FbpWVuQ=\r\n";
        c[168] = "GRe7uF1wH5/71B3vmGF+pN3H9PKO1tLwsnb0D4/Pm7Pu5KAe4OfelkfFIBgyjuoZrpeEkGZnb+qf\r\n+Kn7Kt1hDwYr/Mb9ewuwOXsbIpLQMgfh0I5XsPrWocduVzn+u/cm3cr0Z11zsx0AZjTqvslACkDq\r\niquY41JhtGdc22RCvIYom2l+zzMIMyCPsHeCSB1MBu8EWK3iP7SD3dWttwzMg0xanoPDgk0U\r\n";
        c[169] = "8BDFu+29lptlGSjcZe7ghWaUgIzbuUpM5XDFbtJVQPEd3bAE0cGRlQE9EhKXi5J/IskYNrQ357tB\r\nhA+UNDNXCibT2AZGpzWAcwE6dP+14FuRL5Gxqh/teuPYKr5IIn7M3SbgOychZzLI7HGCvVhBUiJB\r\nu8orI3WmAIVcUhAsMGHsFveck/ZCXQA+Uq/WVnW1VNs6hSmIhsAFc51qsmsQK07z2Wptx4rRjw==\r\n";
        c[170] = "siPSXD4u36WYtTvvDzRlFPiuZMnRczrL3eA15955JDCc6/V2Cvu6m/HPO6JxogxO0aYTZ5tejYDO\r\nIZgBy40DgUZMqPJ2IpYjsmUbjjJU8u/OpwhMon525m3v0EYlvyj2Qp3pwFKDkvncK3aNjN3KaaX6\r\nHuIy6kyqsDl0BTEnB5iJyHLRBCkeznTK019u48Yfsrz2oGuZcWzNj5/vKMdxQPiyJ9EHyox8Ark=\r\n";
        c[171] = "+/14PnFQVZ7BTHKUvkTtRrYS7WnPND5gZ5byMhUrDLkJa6UPBV7z0nrDMifEo/dQfUq3EjCiG6xG\r\nVhrUvAzgxqOQZTW1Y9p9M0KWW+E0XvCQppHFpuMqF1vYsF0OD6AMiE9JtGnWs3JcaWP/XBF/CvhQ\r\nlFGbHi3fbrD/haTEBnmpJWBgMdKribdbXHtBSFZ2MzCX2eDtxoDdRdEVGs4v/q8gVBS+WsnZ3TTF\r\n";
        c[172] = "31I1ja+B+aopKkztGzJYvJEWAshyoAAV5yve4LnP0kdImUQaVETSuo5CDIYr7zM8MCD1eYPpLicm\r\nGnA+C927o9QGAVL3ctO/DCWhNinW7NmeYIM+o4diKBkDPjHmSWa+nq4nr+gOap4CtwL2wW2B5Yqt\r\n26pKgN9uAU5CmTL26hYFgMEOZfrkQ7XdYGy2CN8RJLmjeSFVVNBG/FTaK7tpuy0LQSkko6wczBYG\r\neg==\r\n";
        c[173] = "XbRfDqGe3eeI1tHx8UnPneDB57N8VeSSzXzVCNSgxOEfd6d/un5CDxHG+m4w3tIbtSky4R2+zMF+\r\nS5cRvTOwZ/veegYtLKTxA0mVedWLFkfh/v4NgPJ+NEU+cylbSSZLAeBofDvoJwnYKujN2KFa8PGA\r\nxr3Y8ry3qdkS8Ob1ZiHYAmLvKS9sGb/vjTvRy+a4Q7kOepsm7PYisinKelBAvDnjli6/lOutGren\r\njX4=\r\n";
        c[174] = "jGEj/AaBefac9uOcmGuO9nH+N+zMsC4qAe6ZUEMMIXdTGnSWl7Xt0/nKqyOj3ZH249HwkJ8bn5C+\r\n0bzOpQ1eA3PxEq6RfKMrjHJPJmTZXrSESTjfj3oNLU/CqqDOqd8znTgN6nvnUdCeStLMh9bmWF1+\r\n0G11nDwg6GQWWQ0zjVDTq5j7ocXcFOyUcu0cyl5YDcUP0i2mA2JullInU2uBte7nToeSGB3FJxKu\r\neBbv\r\n";
        c[175] = "RAzNCxlP2S/8LfbGtlSDShox8cSgmJMOc2xPFs8egZVJiwlmnS3aBWKPRbbxkZiVVYlu4GNJNwbo\r\ncc6dgrl28HXAsYikE5wwoQ1MeOJWU3zzFiYENh7SLBQfjVPQHucctr8P6Rl7YL5wHc+aC+m92R3b\r\nnzm5rp1PeHm7uzy2iUUN0cgfbwJ4FrpXhVMTsAUpTbg1+037EWcGOuxir4dG2xBfgOwa+ejFHkw7\r\ny0LWRw==\r\n";
        c[176] = "08hmZptBGKKqR6Qz9GNc2Wk1etgU/KogbiPQmAh5IXlTBc97DuEToL4Bb889nfObVQ/WelmiCS8w\r\nEjSBdmnlkkU7/b5UT3P4k1pB6ZxPH9Qldj5aazkA/yCb0kzDfJlcdFOh1eAcu5LvwTXOizmPwsDv\r\nJEnOkaDZrKESZshsHU2A6Mx6awk9/orf6iBlJHQIIH3l4o3b1gx2TNb/hUgdAlwtQDhvKO3skB0P\r\nS+rcWAw=\r\n";
        c[177] = "0GhrgbSSHPLWtyS2mnSxrNAj/dyrFQcxIgPjT7+78SZ1ZTGc03vsmlZ4Z/bOO84E9yKblaI5dSHV\r\nXrx57L0kikL8tgKCsAkUNO3l/4zv5FfCrRTgGx4sFTFB1NNcLcwagkvFzde764DjYmj4YZhYsXSZ\r\nCVKi0uu5M8fpgGDZ9UMSFR008cbhaIoFLWSANqiNJYSvTQZhGWfLtIPGLN+gIOMcaKhx1b5vg6OY\r\nSz6ScAM/\r\n";
        c[178] = "2VGiMV/f2hNZAjw3fdeHx/wRIVzeP018lZynzwSySG/zQBxyRmi3YmKVZmh3aJunuiqmvdt0kJ6l\r\nX7M8BajYHPCBkqJOx8oPJ/K1oADxVgnavZ69dKYrSy9/Pm6sHxjFrdSz9TelUK9sgoFTWS6GxgzW\r\nEqXRBDDpGUnsNbSEcWLPKVLNNoYAcltY98JZaNSZBXcpa9FeSN7sVU43q2IEcDx3ZkJzRJpl/lb7\r\nn+ivMwX/OQ==\r\n";
        c[179] = "iMSCh1m5vct3C7LEn5wKRYtalzvG6pKahG19rTb6Z9q7+buDsML5yM6NqDvoVxt3Dv7KRwdS3xG/\r\nPyb7bJGvQ2a4FhRnTa4HvPvl3cpJdMgCCvsXeXXoML4pHzFlpP0bNsMoupmhQ0khAW51PAr4B165\r\nu1y5ULpruxE+dGx/HJUQyMfGhOSZ5jDKKxD5TNYQkDEY28Xqln6Fj8duzQLzMIgSoD8KGZKD8jm6\r\n/f8Vwvf43NE=\r\n";
        c[180] = "hN4+x/sK9FRZn5llaw7/XDGwht3BcIxAFP4JoGqVQCw8c5IOlSqKEOViYss1mnvko6kVrc2iMEA8\r\nh8RssJ4dJBpFDZ/bkehCyhQmWpspZtAvRN59mj6nx0SBglYGccPyrn3e0uvvGJ5nYmjTA7gqB0Y+\r\nFFGAYwgAO345ipxTrMFsnJ8a913GzpobJdcHiw5hfqYK2iqo8STzVljaGMc5WSzP69vFDTHSS39Y\r\nSfbE890TPBgm\r\n";

    }

    /* Here are the randomly generated byte[] arrays we generated to exercise commons-codec-1.3.jar */
    private static void initBYTES() {
        final byte[][] b = BYTES;
        b[0] = new byte[]{};
        b[1] = new byte[]{-72};
        b[2] = new byte[]{-49, -36};
        b[3] = new byte[]{77, 15, -103};
        b[4] = new byte[]{110, 24, -44, 96};
        b[5] = new byte[]{-43, -61, -67, -75, -33};
        b[6] = new byte[]{-80, -52, 71, -96, -105, -7};
        b[7] = new byte[]{-115, 7, 15, -108, 59, 25, -49};
        b[8] = new byte[]{76, 6, -113, -99, -11, -65, 9, -123};
        b[9] = new byte[]{113, 116, -39, -69, 12, -41, 60, -29, 82};
        b[10] = new byte[]{46, -39, -1, 101, 53, 120, 34, 52, -6, 56};
        b[11] = new byte[]{-23, -8, 126, -115, -43, 55, -43, 35, -90, 90, -73};
        b[12] = new byte[]{19, -2, 76, -96, 62, 42, 10, -16, -18, 77, -63, 64};
        b[13] = new byte[]{-38, 127, 88, -55, -16, -116, -56, -59, -34, -103, -69, 108, -79};
        b[14] = new byte[]{-88, 25, 26, -36, 26, -70, 87, 125, 71, -103, -55, -121, -114, 70};
        b[15] = new byte[]{90, -4, -103, 123, -87, 16, -14, -100, -69, -101, 110, 110, 113, -84, 9};
        b[16] = new byte[]{-95, -118, 113, 51, 46, -127, 74, -106, 87, -123, 90, 78, 71, -89, 87, -104};
        b[63] = new byte[]{-55, -20, 69, 104, -46, -102, 63, -27, 100, 87, 1, 20, -65, 20, 23, 108, 45, 7, 72, 40, -65, -78, -77, -104, -19, -51, 55, -22, 84, -10, -27, -6, -20, -29, 24, -56, -66, -99, -75, -32, -111, -62, -125, -77, -117, -3, 118, 86, -36, -125, 30, -24, 32, -32, 72, -64, -102, 104, -113, 117, 121, 24, 12};
        b[64] = new byte[]{-66, 74, -96, -98, -30, 119, -90, 92, 77, -74, -117, -34, -120, -62, 110, 96, -77, 122, -63, -108, 11, -91, -67, -59, -125, -113, 63, -52, 121, 29, 22, -32, -18, 114, -29, 10, 89, 84, 78, -2, 120, -123, 70, 2, -84, 22, -89, 49, -85, -91, 96, 11, 28, 16, 109, -29, -30, 63, -37, 17, -97, 28, -5, -62};
        b[65] = new byte[]{96, 121, 44, -36, 96, -101, -38, -27, 69, -29, -74, 54, -76, 40, -98, -120, 49, -119, -13, -65, 81, -101, -105, 65, -123, 8, 80, -117, 54, 33, 125, 99, -88, -8, 26, -63, -37, -14, -66, 19, -68, 25, 89, 56, -99, -41, -119, 76, -92, -50, -76, -5, -112, -76, 55, -46, 77, 40, 7, 1, 17, 39, -86, 101, -110};
        b[66] = new byte[]{-49, -50, 121, -42, 57, -112, -89, -12, 44, -9, -101, 112, -37, 110, -66, 28, 33, -42, -82, -30, -79, -4, -101, -33, 4, 39, -48, -26, -99, 31, 23, -66, -26, -111, 42, 105, -21, -95, 57, -25, 104, -92, -38, 12, -100, -84, 16, 108, 48, 47, -51, 111, 57, -64, -127, 54, 104, 51, 54, 113, 122, 23, -55, -62, -18, 61};
        b[67] = new byte[]{86, -60, 118, -62, 26, -86, -2, -92, 38, -33, -115, -66, 76, -36, -11, -106, 3, -103, 50, -123, -101, -92, 44, -2, -110, 61, -77, 126, 90, -76, -97, 30, -46, -3, 23, -124, 84, -11, 9, 114, -88, 12, -75, 92, -21, -81, 97, 85, 64, -9, -63, 0, 126, 85, 70, 52, 126, -122, 76, 112, -65, -122, -20, -79, 9, -84, -15};
        b[68] = new byte[]{-25, 25, -120, -89, -57, 84, 37, -100, -28, -118, -62, 36, -72, 67, -20, -100, -11, -17, -52, 55, -116, -93, -113, 42, 88, 87, -57, 34, 125, -102, -65, 120, -21, -26, -86, 25, 28, 43, 52, 45, -13, 68, -55, -22, 66, -34, -3, -84, 107, 73, -62, 83, 65, 101, -86, -55, -125, -55, 50, 57, -1, 104, -19, -25, 59, 11, -75, 93};
        b[69] = new byte[]{-76, 97, 32, 94, 56, 37, 80, 31, 77, 108, 43, 98, 75, -49, 0, 122, -46, -19, 70, -88, 66, 2, 120, 115, 57, 66, -107, -126, -10, -55, 100, 122, -114, 3, -84, 124, -72, -22, 43, 3, 91, 119, 53, -58, -120, 3, 77, 25, -87, -77, -40, 0, -69, -72, 47, 50, 38, -30, 46, 37, 0, -65, 80, -126, 33, -1, 38, 14, -37};
        b[70] = new byte[]{-128, 91, 113, 18, 22, 9, 106, 16, -26, 83, -105, 105, -71, -42, 44, -23, -108, -20, -88, 12, 126, -77, 104, -2, -45, -96, 52, -51, -20, -101, -35, 78, -25, -123, -111, 108, 110, 64, -125, -107, -37, 52, 17, -123, 40, -87, -22, -39, -87, -109, -26, -20, 94, -126, 12, -29, -125, -35, -43, 59, -99, -119, -98, -108, 77, 3, -57, 35, 14, -12};
        b[71] = new byte[]{-113, -8, 127, -44, -100, -96, 96, 14, 91, -114, -37, 113, -51, 12, 107, -26, 0, 109, -126, 31, -128, 97, 90, 51, -124, 94, 22, -126, -9, 20, -2, 14, 61, -65, 72, 84, -53, -95, 93, -123, -10, 94, -64, 98, -66, 62, 27, 115, -113, -76, 90, 108, -114, 105, -37, 53, -14, -50, -7, 38, 45, 7, 69, -123, -46, 57, 82, 110, 14, -26, -52};
        b[72] = new byte[]{-101, -23, 24, 84, 106, 35, 33, 30, 105, -127, -68, -5, -92, 98, 102, -38, 15, -73, -86, -87, 59, 126, 25, 119, 114, -116, 26, -39, -27, 107, -122, 72, -69, 90, -121, -14, -53, 9, -47, 123, 58, -109, -60, -23, 1, -127, 81, 115, -22, 114, -84, -14, -84, 105, 109, -74, -13, 0, -73, 45, 112, -9, -116, -78, -97, 93, -91, 117, 31, -71, 66, 107};
        b[73] = new byte[]{-92, -10, -122, -100, -54, -12, 81, -119, -91, 118, 114, 85, -109, -15, 126, -35, 96, -119, 39, 90, -10, -94, -37, -61, -28, -94, 93, -73, 107, -75, 70, 116, -70, 47, 8, 71, -121, 2, -62, -87, -11, -68, 59, -12, 4, -125, -115, -42, 119, 18, -88, -104, 98, -1, 36, -51, -9, -74, 127, -63, -10, -109, 15, -96, -123, 52, 65, 49, -93, -85, 97, -111, -93};
        b[74] = new byte[]{-104, -20, 115, 27, 44, -90, -23, 63, -63, -60, 37, 121, -99, 40, -120, 96, -55, 95, 1, 73, -126, 55, -72, 45, -17, -26, 83, 50, 13, 100, 119, -61, -19, -118, -71, 62, 36, 94, 93, -109, -59, 18, 109, 96, 110, -1, -42, 111, 54, 14, 89, -70, 34, 66, 120, 108, 31, 105, 90, 106, -91, -99, -54, -9, 36, -123, -20, 47, 59, 38, -5, -75, 69, 65};
        b[75] = new byte[]{85, -126, -50, 32, 78, 3, -98, 28, 73, -97, 113, 74, 75, -3, -111, 28, 113, -40, 46, 86, 98, 25, 87, 85, -2, 78, 46, 5, -69, 64, 96, 117, 18, 78, -108, 115, 105, -90, 15, 24, -48, 21, -115, -45, 112, 109, 49, -56, -118, 25, -98, -70, 79, -7, 97, 5, 50, -16, -47, 108, -60, -118, 38, -8, -72, -69, 110, 117, -94, 6, -127, -54, 4, -13, -5};
        b[76] = new byte[]{-19, -6, 12, -92, -99, 19, -110, 112, -102, -74, 53, 34, 75, 27, 103, -72, -92, -94, -49, -124, 47, -105, -8, -99, 89, -113, 84, 42, 36, -27, -114, -124, 7, 103, -32, -68, 109, -59, 44, 114, 114, 63, 109, -102, -11, -35, -18, -128, -65, 37, 33, -29, -77, 37, -91, -47, -81, -52, -119, -76, -26, -91, 29, 49, 101, -117, -95, -110, 78, 104, 102, 47, 14, 107, -21, 89};
        b[77] = new byte[]{-113, 20, 18, 26, 33, 108, -7, -67, 83, 124, 78, 37, 13, 64, 15, 80, -14, 116, 73, 103, -108, -126, 93, 55, -84, -82, -102, -70, 39, -106, 37, -58, -85, 12, -127, -52, -9, -72, 6, 56, 115, 38, -56, 123, -99, 68, -54, -92, 102, 98, 36, -59, -124, 96, 98, 0, -126, 82, 15, -55, 121, 79, 117, 57, -112, -9, -76, -66, 20, -5, 3, 97, 74, 28, -80, 123, -49};
        b[78] = new byte[]{80, 105, -121, -2, 9, 123, -73, 112, -28, -13, 77, 111, 70, 20, 67, 17, -72, 44, 125, 40, 109, 29, -99, 96, 101, 9, -8, 40, -42, 121, 66, -52, 55, 90, 33, -93, 91, 66, 38, 34, 117, 71, 107, -30, -46, 28, 28, -53, 82, 60, 2, -47, -99, -25, 92, -18, 34, -29, -32, -94, 41, -118, -128, -78, -109, -107, -120, 78, -106, -87, 85, 24, -15, -108, -107, -48, -24, 56};
        b[79] = new byte[]{-63, -47, 40, 98, 98, 110, 70, 120, 54, -49, 98, 36, 2, 36, -96, 39, 80, -106, -39, 81, 59, 31, -70, 23, -91, -123, -124, 20, 83, -68, -100, 109, 27, 102, 126, -49, 30, 20, -4, 43, 40, 90, 59, 106, -30, 118, 95, 30, 80, 76, -84, -115, -83, 21, 92, 80, -14, 66, -92, -96, 102, 123, -35, 80, -86, -62, 25, -22, 86, 25, -106, -16, -45, -3, 27, 15, 11, 55, 2};
        b[80] = new byte[]{-26, 70, 106, -126, -75, 27, -73, -22, -51, -28, 21, -128, 117, -41, -108, -74, 111, 83, 25, 62, 55, -67, -118, 65, -24, -9, -78, 67, 4, -14, -53, -43, 91, -81, 79, -108, -1, -124, 51, -53, 66, 48, -33, -76, 90, -37, -35, -57, -102, 73, -87, -127, 12, -18, 73, -59, 105, 112, -120, -123, 25, 115, -64, 21, 108, 19, -95, 31, -101, -94, -4, 46, -19, -91, -29, -117, -15, 103, 51, 9};
        b[81] = new byte[]{19, -114, -69, 48, -55, -119, 110, 108, 47, -16, -23, -100, -39, 55, 92, -56, -54, -1, -33, 64, 124, -117, 53, -101, 127, -32, -128, -72, -101, 112, -21, -74, -11, -125, -37, 107, -124, 46, -89, -32, 102, 39, 94, 55, -106, -12, 54, 47, -103, 106, 8, 36, -4, 116, -50, -11, 63, 67, -24, 42, -65, 71, 45, 122, 13, -101, -57, -45, -10, -102, -72, 12, 12, 4, 91, -23, -24, 40, -62, -88, -15};
        b[82] = new byte[]{86, -101, 124, -123, -122, -8, -113, 29, 69, -5, -76, 68, 95, -78, -70, -26, -2, -98, 59, -111, 117, 24, 56, 63, 43, -60, -107, 45, 96, -46, -89, 75, 43, -70, -106, 4, 66, 64, 85, 71, 25, -63, -11, -39, -95, 74, -113, 87, 30, -100, -8, 11, -54, 81, -105, -54, -4, -39, -4, 87, 102, -23, -90, 17, -2, 20, -40, -24, 116, 14, 42, 121, -82, 115, 109, 12, -65, -120, 38, -14, -4, -107};
        b[121] = new byte[]{-123, -2, -67, -58, -65, 102, -76, 87, -8, 55, 120, -10, -72, 15, 76, -128, -78, -7, 103, 47, 120, 30, 59, -2, 85, 8, -67, -29, -30, -46, -13, -34, -10, 36, -104, 3, 28, 36, 14, -64, -2, 112, -123, -44, 36, 87, -28, -119, -91, 121, 34, -56, 113, -102, 74, -60, 56, 126, 23, -87, 63, 12, -18, 101, -63, 41, 96, 68, 126, 109, -23, -14, -42, 63, -116, -48, 116, -103, 123, 126, -116, 117, 18, 33, -107, 58, -89, -93, -18, 42, -79, 119, 36, 47, 64, -93, 94, -58, 36, 36, -109, 52, 52, 7, -50, 98, 49, -64, -70, -50, 70, -43, -72, 34, -11, 88, 102, -120, -57, -64, 48};
        b[122] = new byte[]{-56, -84, -45, -121, -104, 79, -89, -33, -49, 5, -26, 109, -83, -107, -20, 20, 112, 17, -9, 44, 14, 51, 120, 81, 104, 16, -28, -128, 111, 68, -45, -36, 5, 91, -61, 25, 15, 76, 76, 25, -24, 28, -89, 68, 43, 76, -105, 15, -15, 6, 14, 105, -120, 83, 13, 88, -90, 62, -85, 13, 77, 83, -90, -115, -40, -28, -67, 47, -74, 90, 24, -55, 91, -119, -107, 57, 116, 60, 40, -128, -3, -41, 38, -127, -76, -6, 62, 13, 43, 75, -28, 99, 32, 114, -41, 15, -28, -54, 21, 127, 85, 105, -13, 15, -26, 98, -38, 31, -87, -71, 101, 124, 57, -71, 53, -10, 51, -121, 96, 59, -118, 54};
        b[123] = new byte[]{114, -63, 102, -62, -16, -13, -96, -18, 8, 59, -84, -110, 12, 14, 1, -39, 94, -34, -104, 71, -83, 3, 0, -96, 59, -82, -77, -50, 87, -42, 111, 114, -125, -84, -9, -59, 101, -51, -55, -126, -66, 62, 118, 86, -108, -112, 66, 3, 29, -121, -117, 28, 68, -35, 78, -53, 22, 58, 93, -118, -84, -15, -63, 18, -59, 11, -92, 52, -47, -5, 115, -95, -70, -75, -37, -43, 10, -10, 54, -15, -30, 28, 91, -86, 79, 113, -98, 111, -95, 24, -61, 102, -91, 109, -12, -26, 95, 23, 33, -13, 102, -101, -106, -34, 22, 42, 94, 42, 61, -60, -98, -53, -98, 47, -22, -37, 2, -71, 75, -103, -50, -71, 45};
        b[124] = new byte[]{-28, -119, -15, -68, -84, 41, -96, 37, 123, 16, -82, -4, 59, 53, 63, -76, -65, 127, 54, 98, -75, -32, -6, -16, -10, -45, -126, 3, 34, -66, -58, -107, 13, 45, -102, -30, -71, 81, 21, 118, 10, 104, 103, 78, 107, -106, 43, -97, 105, 64, -58, 28, 127, 29, 60, 7, -90, -16, 111, 67, 55, -11, 78, 62, 75, 65, -22, -11, -54, -75, -51, -92, 49, 72, 39, 49, 56, -103, -62, -1, -44, 85, -33, -79, -54, -87, -45, -16, -14, -60, 116, -44, 60, -84, 37, -79, -54, 32, -100, 45, -43, -59, 127, 79, -79, 112, 78, -22, 9, -52, 51, 11, -32, -5, -7, -60, 86, -6, 46, -99, -98, -106, 100, -53};
        b[125] = new byte[]{112, 76, 123, -95, 59, 18, 29, 101, 5, 62, 63, 118, 114, -53, 93, -53, -100, 6, 109, 23, 49, 31, -27, 81, 88, -33, 36, 104, -44, -45, 8, -11, -86, 73, -55, 50, 83, -43, -28, -2, 56, -7, -2, 8, -3, 12, 92, 35, 126, -64, -110, -89, -83, 49, -70, -41, -8, -2, -79, 90, -98, 59, 112, -86, -44, 96, -23, -46, 75, 63, -126, 64, -126, -109, -118, -77, 0, -27, 30, 46, -107, -73, -68, -59, -1, 14, -57, -119, -45, 31, -38, -57, 109, -106, -54, -77, 107, 38, 102, 79, 97, -25, -18, 35, -88, -36, 33, 49, -50, -66, 89, -28, -105, -26, 48, 80, 23, -15, 31, 105, 121, 67, 120, -73, -107};
        b[126] = new byte[]{1, -27, -34, -75, 86, -29, -5, -71, -90, 26, 32, -84, -36, 17, -108, 73, -112, -53, -106, -83, -88, 116, -53, 13, -16, -112, 7, 64, -50, -72, 10, 92, -68, -53, -104, -16, -17, -109, -23, 33, 42, 28, 89, 17, 69, 66, 105, -105, 96, -2, -36, -112, -87, -83, -70, 18, 47, 15, -81, 71, -75, -100, 70, -124, -44, 108, -105, 89, 117, -127, 124, -54, 22, 27, 22, -54, 71, 17, -76, 111, 59, 23, -53, 18, -43, 75, -1, -117, -92, 47, 26, 36, 72, 13, 81, 6, 24, -116, -42, -81, 52, 72, 33, 41, 1, 111, -36, 2, -60, -99, -121, 17, 39, -27, 121, 108, 43, -49, -15, -86, -81, 40, -24, 55, 110, -127};
        b[127] = new byte[]{106, 61, 127, -13, -1, -108, -15, 83, -73, 15, 104, -128, -62, -12, 23, 103, -127, -12, 43, 11, 110, -52, 57, -72, 36, 32, -68, -5, 109, -100, 114, -87, 62, 83, 50, -81, -86, 49, -84, -13, -75, -25, -125, 7, -20, 49, -12, -92, 120, 101, 64, -117, -91, 65, -22, -41, -51, 37, 56, -40, 114, -42, -93, -19, 31, 29, -62, 29, -113, 33, -14, -46, -82, -10, -87, -121, 14, 48, 52, 29, -111, 18, 58, 101, -100, -6, 35, -96, -26, -3, 108, -43, 13, -75, 75, -35, 43, 118, -11, 108, 10, -50, 90, -24, 8, 68, -107, -117, -100, -55, 123, -37, 33, 7, 21, -67, 98, 5, -16, 102, -97, -69, -120, 107, -78, -109, -39};
        b[128] = new byte[]{-110, 35, 46, 23, -3, 66, 49, 25, 96, -95, 47, -18, 44, -87, -11, -104, -42, 69, 100, -47, -55, 54, 68, 80, -98, 43, -50, -81, 56, -4, 29, 0, 26, -125, 12, -17, 77, 123, -20, 26, 2, 61, -123, 117, -9, -87, -89, -103, 47, -33, -15, -4, -22, -66, 99, -111, 122, 14, 31, -80, 94, -113, -76, -110, -98, -119, 106, 1, 99, -67, -76, -55, 113, 4, -88, -64, 92, -56, -62, 14, -1, 87, -37, 26, -98, -51, 13, 19, 65, 35, -93, 14, 65, 125, -110, -81, -118, 17, 72, -29, -85, 7, -120, 2, -27, -42, -84, -24, 94, -94, -48, -85, -18, 95, 100, 98, 5, 71, 44, -104, -13, 1, 86, -42, -24, -115, -69, -39};
        b[129] = new byte[]{97, 121, -126, 90, 24, -115, -49, -113, -56, -121, 44, 80, 33, -120, -32, 54, -8, -41, -7, 124, 3, -120, -12, -46, 4, -58, -123, 16, -71, -71, 95, -58, 56, -110, 113, -111, -98, 65, 120, -1, 56, -79, -14, 95, 112, 28, 22, -2, -20, 40, 31, -82, -66, 58, -84, 94, -34, -120, -116, -38, 0, 82, -29, 46, 56, -34, -61, 46, -73, 118, -32, 73, -119, 86, -60, 90, 58, -123, 59, 71, -18, -23, 63, 98, 34, 59, -124, 104, 93, -84, 78, -102, 111, -71, -109, 79, 31, 31, 52, -68, -33, -121, 44, 9, -64, 86, 104, 5, 17, 44, 57, 0, -11, -42, -97, 94, 93, -117, 59, -18, 81, -11, 71, 119, -104, -84, 10, -90, -64};
        b[130] = new byte[]{24, -105, -44, -96, 24, -81, 91, -101, -86, 14, -107, 49, 77, 23, -29, 25, 99, 88, 124, -35, -1, 117, 49, 109, 117, 26, -126, 32, 126, 75, -39, -51, 103, 89, 125, 29, -127, -115, -37, 107, 95, -97, 91, -10, 23, -44, -24, 121, 107, 14, 37, 11, -96, 72, -118, 36, -31, -116, -102, 21, -43, 90, -51, -98, 85, -13, 109, -59, 28, 51, 47, -122, -80, 53, 20, 7, -60, 30, -14, 80, 111, 125, -17, -19, -31, 25, -115, 60, 29, 33, 86, 10, -46, -13, -39, -19, 110, -27, 52, 74, -64, -90, 91, 15, -67, 14, -93, 55, -98, 39, 79, 121, -80, 13, 40, 68, 98, -125, 92, -39, 76, 16, 74, -126, -107, 95, -20, 115, -59, 8};
        b[131] = new byte[]{121, 55, -85, 54, -50, -35, 58, -94, 64, -60, 12, 92, 49, 4, 103, 80, 55, 54, 112, 24, -10, -57, 72, -44, -42, 13, 67, -36, 111, -101, -37, 105, 3, -49, -78, 65, -52, -20, -65, -101, 24, 70, 92, 4, 55, -41, 125, 37, 106, -11, -116, -124, -119, -110, 6, 68, 94, 83, -20, -12, 3, 93, -60, 97, 83, 20, -65, -45, -59, -52, 62, -8, -20, -96, -81, -98, -49, -110, 10, -114, 118, 96, -47, -63, -45, -49, 122, 111, -119, 120, -18, -81, 33, -69, -56, -20, 15, 94, -45, 103, 107, -106, 88, -104, 82, -31, -61, 103, 72, 77, 99, 120, -50, -115, -95, 113, -118, -67, -1, 116, -53, 23, -13, -62, -25, 13, 97, -50, 17, 117, -92};
        b[132] = new byte[]{-107, -70, -58, -78, 54, -13, 19, -99, -75, -85, -53, 115, 84, 117, 123, -67, -60, -49, 114, 21, 39, -52, -113, -44, 125, -28, 118, 71, -25, 38, 57, -83, -67, 98, 88, -49, 88, -72, 109, -11, 108, 118, 38, -72, -30, 42, -2, 14, 127, -103, -41, 123, -71, 47, -122, 47, -117, 97, 75, -123, 86, -88, 26, -88, -127, 97, 11, -13, -53, 110, -93, -123, -119, 61, -107, 101, -59, -24, -83, 58, 26, 69, 63, 66, 46, 42, -107, -27, -66, -89, 28, -33, -45, -57, 62, -110, 35, -77, 34, 15, 120, 103, -109, -70, 108, 13, -72, 37, 60, -22, 62, -54, -38, -100, -10, -63, 71, -113, -75, 36, 71, 86, 92, -75, 8, -16, 21, -116, 9, -114, 92, 95};
        b[133] = new byte[]{-103, 12, 0, 63, 54, 51, 127, 28, -3, 21, 113, 34, 103, -93, 60, -69, 90, 13, -36, 66, 91, 21, -119, -115, 85, -6, 102, -6, 61, 3, -86, 5, 62, 56, -14, 56, 81, 17, -63, 34, -79, 120, -58, -9, 76, -68, 3, -25, -47, 107, -102, -83, -76, 50, -3, -107, 77, -47, -30, -117, -19, 46, 10, -36, -53, -38, -97, 71, -27, 3, 6, -103, 116, -113, -98, -28, 122, 100, 127, 19, -88, 25, 58, 124, 14, 78, 68, 21, 53, -29, -103, -97, -20, -8, 106, 39, 120, -15, 16, 16, 99, 85, 60, -67, 86, 98, -128, -6, -59, 122, -128, 2, -13, 11, 42, -36, -85, 21, -54, 104, -120, 100, -67, 120, 48, 101, -79, -112, -48, -52, -4, -13, -2};
        b[134] = new byte[]{-36, 110, -99, -41, 101, 24, -30, 94, 86, -20, -39, -16, -48, 18, -12, 30, 117, 98, 86, 15, 82, 116, 75, -117, 50, -89, -103, -61, 45, 17, -108, -108, 127, 98, -41, 32, 119, -7, -120, -60, -117, -105, 92, 9, 70, -122, -8, 104, 95, -91, -36, -40, 17, -108, -19, 26, 83, -117, 88, -40, 43, 48, 60, -21, 43, -91, -46, 63, 122, 117, -73, 4, -32, -21, 107, 63, -39, 84, 4, -103, 92, 99, -41, -106, -49, -97, 93, -68, -70, -118, -57, 11, 119, 112, 67, -127, -62, -77, -33, -21, -45, 7, -72, -122, -74, -25, -89, -84, -36, 3, -126, -95, -73, -62, -15, 20, 18, 113, 109, 15, 67, 68, 20, 107, 85, 71, 7, -9, -105, 74, -59, 83, 73, 104};
        b[135] = new byte[]{3, 24, 43, 100, -92, -59, -109, -110, 111, 44, 45, -61, 4, 0, -121, -62, -99, -70, -24, 92, 74, 35, -4, -89, -12, 95, -75, -75, -119, 51, 47, -98, -47, 86, -50, 71, 8, -106, 18, -29, -75, 110, 27, 68, -12, 19, 108, 42, -52, 122, -100, 44, -93, -48, 49, -107, -108, -18, -10, -47, -93, 35, 6, 52, -67, -39, -75, -95, -24, 102, -92, -22, 114, -81, 74, 90, -45, -125, 11, -29, -89, 18, -12, -110, -17, -19, 79, -31, 41, -112, 14, -20, -117, -40, -62, 127, 120, -112, 1, 36, -93, 92, -69, 82, -65, 46, 77, 27, -72, 6, -114, -48, 91, -41, -24, -31, -127, 112, -4, -18, -41, -25, 111, -3, -103, 48, -72, -28, 74, -45, -70, -111, -97, 26, -112};
        b[136] = new byte[]{106, -39, 6, -14, 93, -108, -4, -39, -26, -5, 50, -18, -17, 56, -34, -98, -73, -36, 1, 84, 9, 49, 74, -121, -118, 97, 89, -35, -60, 31, 105, 74, 120, -85, 42, -59, -97, 85, 34, -63, -99, -9, -113, -82, -107, -73, -83, -34, 85, -92, -1, 21, 124, 126, 46, 81, 39, -80, 121, -6, 1, 51, -63, 50, -99, -30, 17, -118, 1, 37, -116, -115, -90, 60, -1, -51, 19, -95, 5, -73, 51, -99, 0, 12, 117, 101, 19, 115, 34, -120, -98, -7, -58, -33, 30, -124, -58, 0, 87, -89, 42, -97, -57, 26, 127, 62, 111, 27, -53, -78, -27, 73, 68, -7, 48, -95, -35, 114, -17, -109, 17, -11, 23, 70, -119, 2, -81, 104, -113, -98, -34, -51, 98, -61, 67, 124};
        b[137] = new byte[]{117, -56, -108, -42, 100, -121, 25, -64, 113, 56, 5, 65, -128, -100, 123, -28, -98, -125, 105, 7, 65, 101, -12, -56, -51, 13, 56, 90, 25, 36, 117, 19, 46, -92, -26, 86, 74, 123, -11, -9, 24, 115, -14, 24, -114, -7, -103, -37, 6, -48, -55, -72, -18, 107, 30, -59, 6, -52, 127, -29, 97, -89, 31, 103, 32, -112, -9, 110, 43, -95, 27, -28, -121, 116, -11, -53, 27, 11, -85, 118, -101, 100, -107, 2, 15, -47, -40, -100, -12, -60, 45, 88, 111, -41, -25, 4, 23, -122, -88, 59, 123, -58, 58, -19, -123, -91, -14, -44, -97, -108, 19, 119, 67, -24, -50, -62, 42, 114, -90, 85, 7, 101, 121, -20, 30, -61, -115, -8, -77, 81, -19, -61, -15, -121, -7, 85, -54};
        b[138] = new byte[]{118, 4, 117, 60, -10, -100, 6, -5, 72, 44, 25, -96, -33, 123, 61, 41, 107, 110, 115, -82, -25, 118, 29, -21, 8, 118, 127, -108, -125, 123, -80, 67, 96, 108, 90, -119, 67, 46, 67, 11, 117, -56, 120, 116, -43, -18, -123, -99, -6, -46, 80, 105, 124, 104, 11, -67, 127, 73, 78, -47, -49, -93, -126, -108, 3, 10, 92, -74, -52, -49, -59, 58, 68, -8, -86, -97, 115, 59, 49, 16, -112, 112, 55, -51, -116, 14, 92, 8, -39, 39, 93, 63, 68, -121, 63, -56, 100, 95, -5, -107, -59, -85, -61, 16, 74, 98, 110, 1, -9, -55, 79, -110, -106, 109, 71, 100, 7, -99, -40, -112, -34, -9, -89, -51, 106, -49, -48, -2, 114, -37, -71, -111, 121, -95, 110, 104, -97, -66};
        b[139] = new byte[]{-43, 15, 43, 126, -99, 71, -72, 107, 49, -70, -76, -32, 24, -58, -92, -58, 62, 82, -64, -47, 123, 18, 44, 87, -59, 83, -25, 124, 96, 3, 88, 55, 50, -126, 30, 66, -44, 19, 23, -86, 34, -11, 2, -27, 87, 57, 81, 78, -79, -84, -99, 124, -75, -122, 11, -62, -101, -121, -14, 98, 47, -95, -2, -41, 105, 88, -84, -47, -108, 79, -36, -119, 68, -116, -47, 14, 46, 23, -54, 83, 39, -83, -116, 1, -73, 0, -119, -114, -127, -46, -117, -121, -79, -49, -110, -74, -118, 30, 120, -70, -43, 64, -69, 53, -39, -125, -87, -89, 115, -40, -98, 119, -7, -115, 70, 56, -3, 119, 97, -59, 15, -103, -81, -45, 15, -22, 76, -33, -42, 23, 17, 38, -74, -26, 76, -2, 115, 12, 106};
        b[140] = new byte[]{-61, 77, 83, -100, -13, -1, 23, 117, 103, -117, 119, -63, 117, 93, -10, 6, 118, -37, -32, -99, 69, 113, -74, -124, -7, 121, -8, -30, 122, 57, -70, 14, -5, 83, -63, 82, -42, 4, -90, -36, -70, -119, -13, 36, -14, 93, -37, -77, -105, -101, -10, 119, 0, 68, 127, 56, -124, 125, 59, -78, -86, 7, -117, 46, -64, -104, 76, 24, -41, -103, -125, -120, 29, 10, 44, 18, 94, -125, -56, 9, 39, -69, -123, -54, -25, 103, -112, 91, -31, 94, -77, -41, -110, -77, 99, 106, -8, 110, 26, 105, 30, 50, -30, 87, -98, 20, -39, -126, -95, -97, 65, 66, 96, 0, 102, -67, 11, 22, 74, -89, -66, -90, -118, -104, 21, 96, 114, 12, 79, -77, -65, -4, 73, 1, -25, -90, -85, -38, 83, -90};
        b[141] = new byte[]{57, -4, -56, 23, 127, 19, -89, -104, 53, 91, -105, -107, 110, -71, 43, 49, -19, 12, -101, -73, -76, -64, 22, 32, -26, 21, 111, 46, -103, -1, -28, -59, -71, 56, 87, 38, 68, 59, -127, -89, 94, 90, -55, 16, 103, 39, 91, 1, 35, -35, -2, 8, -39, 116, 47, 3, -4, 67, 76, -3, 33, 113, 99, 76, 68, 121, -128, 118, 117, 41, -15, -68, 53, 95, -88, 105, -62, -115, 27, -74, -104, -83, -75, 31, -74, 68, -117, -2, -125, -33, 68, -45, -34, -49, -91, 52, 22, -27, -96, -67, 90, -41, 80, 30, -100, -64, -92, -42, -65, 6, -3, 42, -128, -5, -77, -96, 6, 97, 23, 71, -25, 70, 96, 37, -48, 103, -120, 80, -5, 116, -86, 14, 66, 114, 127, 16, 43, -109, 81, 114, 62};
        b[142] = new byte[]{-30, 26, 23, -46, -56, -22, -106, 68, -119, 61, 74, -70, -37, -72, -119, -112, -42, 17, 110, -48, -2, 87, 106, -124, -84, 43, -109, -118, 70, -98, 68, 49, 25, -67, 81, -42, -65, 55, 111, -62, 62, 22, -41, 28, 73, -113, 63, -78, 28, 3, 122, -49, 97, 35, 9, 104, -99, 113, 43, 21, -127, 55, -93, 67, 86, -57, -14, -28, -13, 38, 8, 127, -50, -83, -14, 25, 68, -84, 38, -126, 86, -83, -25, 42, -58, -56, 30, -15, -79, 1, -114, 73, -66, -87, 67, -68, 27, 121, 0, -100, 46, -123, 50, 50, 118, 53, -124, 30, 93, -2, -114, 43, -93, -96, -78, -84, -119, -62, 81, -49, 79, 32, 38, -77, 111, -56, -95, -124, -63, -30, 33, 42, -128, -127, 70, -1, 103, -51, 16, -111, 8, 106};
        b[143] = new byte[]{51, -9, 114, -41, -116, 77, 109, -109, 17, 124, 120, -118, 40, 87, 102, 15, -13, -85, 16, 31, -68, 49, -20, 107, 68, -17, 38, 50, 29, 34, -28, -70, -51, 3, -107, -108, -76, -75, -35, -63, 71, -64, -54, -58, 38, 106, 95, 120, 49, 42, 13, 37, -8, 38, -64, 96, -88, -88, -36, 37, 89, -112, 86, -42, 74, 80, 104, -9, 106, -78, -71, -10, 114, -9, 58, 37, -58, 86, -12, 89, 78, -57, 78, -61, 17, 18, 70, 109, -98, -91, -22, -121, -37, -51, -107, -96, 23, -20, -97, -6, 27, -45, -22, 57, -55, -67, 112, 85, 9, 52, -9, -107, -13, -73, -86, -114, 37, 120, -37, -124, 39, 11, 46, 83, -89, -85, 92, 96, -73, 10, 30, 119, -72, -5, 70, 34, -73, 67, 82, -105, 23, -21, -11};
        b[144] = new byte[]{26, 2, -21, -38, 23, 119, -100, -82, -72, 33, -101, -12, 38, 75, 10, 1, 63, -14, 36, -44, 55, -15, -84, -82, 91, 32, 103, 88, -72, -41, 108, 73, -109, -45, -113, 20, 14, -102, 108, -82, -71, -49, -22, 24, -92, 70, -114, 111, -39, 57, 71, 26, 16, -10, 117, -52, 6, -12, -46, -54, -36, -8, 124, 34, -126, 40, -11, 103, -10, -67, -107, -127, 7, -28, -116, 35, -47, 105, -127, -100, 65, 88, -79, 35, -64, 118, -50, 90, -63, -38, 17, 3, 21, -79, 7, -28, -33, -63, 22, 64, -3, 5, -81, -91, 114, 48, -87, 68, 65, -6, 108, 70, 75, -126, -90, -99, 74, -58, -57, 26, -60, -82, -32, -28, 24, 23, 82, 86, -12, 107, 108, 116, 23, -32, -67, -124, 64, 116, 57, 83, -20, -113, -126, 77};
        b[145] = new byte[]{91, -39, 74, 112, -80, -22, 52, 100, 0, 27, -5, 10, 64, -42, -111, -103, -29, -108, -104, -78, 123, 25, -61, 13, -88, 22, -122, 103, -82, 125, 70, 120, -21, -23, 20, -21, 126, 121, -92, -89, -106, 125, -103, -32, 11, -97, 5, -24, 30, 51, -29, -51, 36, 3, 38, 117, 88, -79, -113, -79, -56, -55, -18, 62, 9, 122, -128, -59, 64, -124, 9, -66, 94, -102, -40, 86, -101, 39, 123, -117, -19, -5, -70, 35, 82, -90, -81, 84, -16, 106, 47, 46, 93, -74, 120, 105, -42, -89, -36, -100, 8, -107, -27, -58, 97, 29, 117, -5, 53, -68, 100, 80, -20, -23, -74, 49, 6, 9, 8, -25, -123, 33, -8, -84, -10, -39, -74, 10, 8, 88, 79, 25, -23, -70, 5, -34, 95, -19, -124, -71, -59, 95, -32, 34, -119};
        b[146] = new byte[]{-119, -126, -25, -26, 31, 101, 34, 16, -1, -57, -39, -88, 104, -12, 103, 77, 126, -90, 36, 66, 74, 78, 24, 56, 89, 124, 82, -20, -118, -47, -37, 54, -27, 31, 110, -82, 58, 66, 13, -49, 71, 73, 53, 50, 85, 122, -37, 52, 100, -42, 3, -98, -55, -45, -20, -99, -94, 35, 118, 58, -123, 53, 86, 60, 106, 24, 83, -113, 73, -36, 29, -117, -124, 36, 68, -77, 64, -39, 67, 112, -69, -45, -51, 5, -74, 35, -100, 124, 118, -33, 4, -77, 103, -48, 60, -126, 43, 82, -34, 0, 46, -40, 7, -10, -51, 118, 44, -95, 46, -95, -4, 124, -87, 126, -45, -30, -95, -120, -16, -56, 80, 18, 88, -39, -18, -56, 109, -108, 127, -10, -35, 44, -60, -106, -56, 95, 30, -100, 70, -14, 57, 1, 119, -79, -41, 106};
        b[147] = new byte[]{74, 84, 73, 17, -50, -25, -91, 53, 47, 66, -81, 18, -128, 22, 10, -103, 86, 63, -33, 1, -40, -89, 104, 44, 15, 22, -97, 55, -14, 75, 82, -31, 42, 18, 57, -106, 50, -43, -69, 127, 16, 18, -117, -25, -113, 95, -120, 38, 36, -102, -117, -124, 62, 109, -8, 6, 113, -120, 44, 24, 43, 114, -9, 113, -21, -119, -57, -124, -30, -87, 88, 9, -49, -57, 122, -79, -50, -33, -127, 101, -27, -70, 29, 74, -8, 23, 6, 61, -74, 123, -8, 42, -72, 86, -56, 26, -20, -111, 1, 85, -23, 47, -120, 70, 121, 2, 30, 26, -92, -38, -4, 70, 64, 123, -96, -61, -108, 84, -15, -79, 69, -52, -78, 105, 81, 56, -52, 21, -66, -79, -64, 58, 115, 92, -94, -4, 116, 23, 117, -44, 61, -83, 108, -117, 82, -59, 104};
        b[148] = new byte[]{0, 9, 91, 25, 30, -98, -112, -77, -77, -57, -120, 105, -87, -108, -44, -87, 85, 16, -48, 82, -10, 8, 90, 96, 8, -61, -87, -24, 43, -82, 83, 57, 18, -99, 101, -23, 82, 59, -50, -119, 32, -28, 52, -8, 40, 76, -8, 41, -126, 104, 55, -71, -117, -52, -79, 126, 55, -102, 67, -58, 80, 96, -67, 107, 0, -45, -123, 124, -122, 66, 30, -82, -35, 103, -42, -74, 104, 98, 61, 26, -105, -98, 49, 94, -109, -31, 15, 119, 66, -40, 94, -101, 59, -42, 122, 39, -24, 47, 102, -18, 117, 37, 96, -43, -77, 4, -99, 88, -50, -68, -42, -114, 80, 121, 44, -77, -63, 55, -58, -20, 100, 110, -111, 109, -42, 100, 96, -52, 73, -12, -98, 109, 107, -9, 55, -46, 111, 48, 12, 51, 110, 78, 109, 39, 16, 77, 25, 85};
        b[149] = new byte[]{-99, 101, -101, 6, 28, -50, 110, -84, 4, 22, 31, -45, -120, -92, 68, 114, -54, -117, 97, -76, 99, 33, -58, 95, -108, -102, 104, -105, 94, -116, -117, -122, 3, 47, -95, 25, -82, 45, -74, -87, 25, -45, -126, -80, 109, 33, -113, -121, -119, -28, -40, 90, -30, -38, 9, 41, 63, 7, -43, -100, 98, -24, -24, 75, -94, 91, -27, -36, -96, -36, 84, 58, 92, 95, 91, 97, -93, 80, 27, 29, -80, -16, 49, -32, -57, 73, 54, -98, -95, -1, -47, 77, 108, 1, 77, 36, 126, -35, -10, 104, 61, -88, -82, -96, -116, 34, -102, -37, 59, 85, -119, 34, -35, -102, -71, 25, -17, 112, -28, -59, -96, -26, -79, -9, 110, -89, 2, 76, 111, 71, 121, -21, 21, 80, -34, -68, 88, -61, 110, -24, 41, 88, 121, 6, -62, 39, 58, 27, -70};
        b[150] = new byte[]{88, -39, 53, 70, 125, -86, -76, 111, -96, -109, 64, 4, 123, 10, -32, -93, -26, -111, 108, -38, -58, -32, 40, 16, -92, -28, 124, 82, -114, -36, -38, 109, -105, 65, -113, 12, 84, 59, -72, -71, 16, 58, -81, 54, 6, 25, -40, 42, 77, -62, 2, 42, 76, -67, 50, -15, -34, 37, -29, 86, 84, 5, -50, -58, 96, 56, 17, -43, -48, -37, 18, 45, 20, -105, 80, -73, 16, 109, -25, -89, 26, 119, -47, 65, 93, 123, 51, -37, 92, -107, -80, -6, -7, 119, -114, 119, 105, -72, -69, 79, 117, -33, 29, 84, -45, -95, -23, 34, 58, 92, 64, 38, -49, -101, -91, -85, 125, 112, 84, 91, 33, -47, -32, -124, -17, 112, 115, -5, -100, -96, 10, -45, -25, 123, -23, 98, -55, -14, 121, 89, -26, 100, 109, -113, 95, -113, 37, 94, -109, 82};
        b[151] = new byte[]{-63, 89, -94, 59, -87, -99, 127, 102, -102, -122, -78, 101, 114, 105, -63, 15, 68, 26, -25, -52, -68, 2, -4, -41, -76, -98, 120, 34, 108, 96, 60, -77, -25, -48, -48, -112, 34, -71, -108, -57, 51, 89, 53, 51, -125, 19, 117, -114, -22, 70, 76, 16, 38, -109, -3, 17, 42, 79, 115, -24, 56, 26, 125, -78, -48, -121, 8, 83, 87, 56, -50, -3, -17, -46, 120, -32, 45, 89, 69, 77, 50, 60, 40, 90, -45, 73, 16, 100, 17, 121, 18, 103, 11, -118, -43, 66, -49, 93, 45, 63, -56, 6, -43, -8, -120, -68, -56, -16, -91, -35, -103, 76, -26, -104, -29, -8, 56, 53, -30, 71, 46, 50, 79, -106, 93, 28, -26, 118, -57, -18, 126, 47, -5, 82, 62, -60, 110, -1, -18, 78, -58, -24, -33, 44, -89, 109, 105, 110, 10, 83, 96};
        b[152] = new byte[]{58, -120, -107, 102, -117, 8, -8, -95, -76, 17, -108, -18, -7, 106, 71, -18, 66, -120, 75, 25, -114, -41, 52, 88, 105, 35, -52, 4, -59, -76, 3, 36, 98, 11, 104, -103, 84, 73, 14, 50, 34, -89, -41, 101, -49, -88, -128, -46, 91, -7, 21, 3, -97, 28, -66, -1, -14, -79, 48, 51, 40, -76, -18, -17, -59, 40, -6, -59, 92, 37, -19, -64, -76, -73, 96, 120, 15, 111, 57, 17, 68, 45, 51, -8, -65, -48, -48, -2, 96, 38, 122, 23, -73, 55, -52, 100, -7, 84, -53, 99, -84, 71, 90, 46, -97, -30, 63, 92, 76, 53, -65, 4, 60, -119, 72, 52, 88, -45, 74, 24, -96, 121, 111, -98, 56, 7, -93, -128, -121, 6, -115, 44, 48, 104, 23, -36, 44, 28, -100, -83, -95, -92, -63, 85, -76, 31, -9, -88, -7, 122, -65, 108};
        b[153] = new byte[]{-9, -74, 85, -79, 54, 48, 15, -59, 107, -128, -35, 69, 56, -124, 89, -14, 57, -68, 56, -56, 43, -60, 109, -24, -43, -94, 104, 98, -45, -43, 89, 122, 125, 114, 56, -27, -127, 122, -109, 49, -107, 108, -95, 106, -9, -90, -66, -23, -71, -36, 62, 45, -118, 53, 18, 60, -9, 38, -104, -108, 60, 122, -106, 113, -99, -113, 90, -75, -84, -20, -12, 117, 106, 70, 6, -69, 7, 66, 4, 118, 47, 8, 100, -36, 101, -127, 94, -1, 43, -75, 0, 31, 48, -25, 54, 44, -105, 87, -119, 71, -114, 96, -46, -19, 67, -107, 119, -15, -28, 21, 0, 83, 55, 56, -56, -112, -29, 115, -113, 11, -77, -83, -101, -97, -53, 116, 16, -98, -2, -33, 75, 22, -62, 39, -22, 42, -20, -10, 16, 122, 108, -86, -99, 99, -2, 35, 37, -61, 50, -119, 26, 49, -124};
        b[154] = new byte[]{-48, -44, -104, 41, 59, -63, 40, -94, 39, -64, -65, 79, 39, -6, 86, 81, 101, 87, -30, 8, -59, -34, 40, 44, 3, 106, -86, 64, -53, 17, 123, 13, 37, 35, 82, -36, 8, 29, 124, 120, 96, -104, -109, 64, 39, -55, 64, -98, -9, -36, 57, -107, 97, -58, 21, 67, 12, 120, 126, -125, 101, -56, -128, -54, 85, -122, 120, 57, -11, -12, -105, 115, 84, -28, -34, -68, 21, -92, -36, -78, 46, -116, -115, 70, -66, -63, 1, -34, -123, 106, 127, 102, 101, -12, -122, 94, 12, 96, -109, -29, -26, -48, 40, 21, -81, -127, -59, -57, -113, 43, 25, 126, 121, -1, -25, 63, 87, -49, 78, -86, -84, -6, -102, 115, -110, -33, 75, 126, -97, 13, -52, 39, -110, -75, 24, 65, 96, -20, 47, 98, 36, 123, 125, -128, 46, 99, -89, -111, -64, -29, 54, -83, 65, -96};
        b[155] = new byte[]{-26, -119, 62, -83, -126, 56, 44, 40, 6, 107, 106, 108, 25, 67, 127, 125, -39, 19, -38, 4, 4, 78, -128, 125, 29, 24, -123, 93, 10, 94, -39, -121, 16, -68, 67, 118, -49, -74, 37, 36, 33, 19, -57, -120, -46, 91, 13, 58, -89, -106, 60, -75, -105, 41, -45, -55, -88, 40, -42, 48, 80, -98, 52, 14, -13, -23, 26, 80, -58, -5, 5, 41, 22, 16, -72, -110, 71, -24, -22, -8, -6, 78, -80, -93, -25, 42, 22, 38, 46, 67, -6, -89, 96, 31, 100, -12, 37, -27, -116, -13, 71, 24, 55, 70, -20, 58, -57, -83, 117, -86, -115, -124, 51, -115, -107, -123, -39, -38, 85, 88, -109, 37, 82, -1, 12, -16, -16, -82, 113, 95, -50, -60, 90, 75, 112, 59, 1, 106, -1, 38, 33, -71, 96, -104, 39, -47, -103, 47, -76, 46, 14, 42, 73, -116, 65};
        b[156] = new byte[]{40, 124, -45, 106, 85, 61, -64, -12, -89, 34, 56, 121, -125, 71, -121, -117, 81, -44, 104, 91, -97, -60, -102, 87, -90, 48, -34, -48, -35, -16, 16, -94, 13, -87, -72, 51, -123, -72, -9, 41, -2, 42, 90, 54, -10, 119, 80, 62, -48, -55, 110, -10, -42, 5, -2, 64, 61, 56, -40, -35, 107, -104, -40, 125, 24, 2, 30, 110, 102, 82, -72, -44, -47, 48, 50, 79, -79, 16, 21, -99, -82, -23, 97, 74, -2, -63, 71, 63, -17, -78, 112, -108, 36, -14, 78, -54, 44, -11, 22, 3, -109, -106, 73, -52, -75, 16, -17, -99, 96, 94, 29, -46, -66, -118, -28, -17, -69, 31, -6, -23, -110, 124, 77, -24, -49, 69, -108, -1, 44, 31, -51, -61, 92, -83, 82, 119, -27, 23, 47, 125, -125, -5, -121, -27, 23, -97, 52, 21, -90, 121, 7, 116, -28, -57, 119, 71};
        b[157] = new byte[]{70, -78, 85, -60, -126, -88, 13, 123, 66, -66, 37, -116, -65, -12, -105, 48, -18, 54, 14, 126, -108, 88, -30, -125, -53, 104, 97, -40, 4, -105, -78, -60, -12, 123, -31, -70, -101, -79, 117, -37, 125, 34, 37, 105, 68, 55, 76, 1, 71, -54, 126, 6, 84, -35, 6, 20, 43, -97, 110, 116, -9, -61, -9, 43, 94, -85, 6, 102, 105, -49, 15, -7, 118, 103, -84, 32, -100, 97, 96, -10, -116, -32, 104, -102, 58, 12, -19, 22, 59, -110, 113, 56, 36, -103, 93, 9, -105, -5, -39, -11, -32, -105, 116, 69, 77, 33, 39, -105, -112, -120, -79, 51, -118, 108, -79, 49, 110, 81, 94, -49, 99, -11, 41, -51, -40, -7, -26, -80, -54, 24, -70, -11, 121, 62, -64, 21, 84, -115, 63, 125, 15, -124, -65, 51, 102, -49, 12, 0, -38, -118, 5, -30, -127, 4, 59, -19, -61};
        b[158] = new byte[]{-77, 46, 45, -26, -79, 64, -17, -111, -111, 4, 79, -125, -87, -81, 108, 67, 24, -49, -106, -30, -83, -4, -103, -60, 99, -98, 32, -72, 121, -54, -87, -57, 38, -57, 113, -58, -117, 34, 116, -115, 64, 62, 122, -34, 23, 59, -59, -32, -21, -36, 2, 3, -36, 85, -60, -106, 80, 47, -67, -13, -13, 73, -107, 124, -121, -8, -36, 114, 10, 59, 24, 49, 79, 98, 35, 27, -79, 10, 48, -32, -1, -85, 30, -115, -100, -60, -17, 123, -128, 107, -54, 8, 11, -75, -78, -48, 81, -28, 95, -122, -72, -112, 121, -114, -31, -21, -101, 67, -10, 39, -85, -123, -57, 23, -95, 112, -23, 26, 121, -127, -97, -39, -74, 5, 3, -5, -13, 47, -112, -123, -50, 30, 34, -58, -73, 45, -52, 91, 64, -107, 8, -62, -11, 17, -116, 90, 127, -82, 3, 3, -18, 88, 78, 45, -70, 26, -1, 102};
        b[159] = new byte[]{102, -49, -63, 124, 90, 22, 34, 102, 26, -69, 103, 89, 45, -66, -55, -99, -25, -109, 66, -98, -20, 67, 78, -78, -94, 113, 42, -47, -122, -72, 4, -45, -119, 27, 47, -7, 116, 97, -8, -38, -116, -19, -48, -6, 79, -40, -122, 127, 75, -97, 23, -19, -21, 21, -127, -50, -93, 3, 92, -73, 5, -52, 99, -36, -61, -24, 14, 52, 9, 73, -111, -59, 18, -44, 119, 27, 125, 83, -15, -31, 121, 58, -109, -43, 11, -121, 112, -10, -49, 92, -106, 89, 76, -45, -101, 111, 113, -23, -84, -80, -73, 92, 119, -50, 84, 63, -4, 65, 104, 58, -26, -92, 9, 124, 100, 52, -100, -17, 124, -33, -108, -83, -25, 39, 15, 30, -48, 116, 5, 98, 123, 77, -98, 86, 79, 23, 44, -78, 4, -79, -25, 47, 39, 27, -24, -103, 111, -85, -79, 118, 58, -86, -39, 71, -31, 87, 102, 114, -9};
        b[160] = new byte[]{-123, -41, -125, 38, -28, 103, -103, -65, 42, -12, 70, 30, 115, -49, -97, -70, -44, -1, 10, -6, -95, -90, -86, 20, 118, -39, -122, -24, 77, -11, 70, -74, 9, 14, -49, 122, 51, -107, -97, -98, 15, 116, 32, 74, -85, -99, -58, 94, -30, -75, 88, 14, -121, 17, 100, -83, -73, 118, -6, -83, 37, -93, -31, 77, 64, 87, 94, 30, 54, 35, 35, 37, 82, 122, -24, -21, -46, -120, -5, 78, -7, 22, -10, 24, -62, 5, 90, 81, -35, 116, -81, -79, 49, 88, -50, 89, -20, 48, -112, -37, 31, 19, -101, 89, -98, 43, 102, 2, 76, -7, 5, -43, 85, -41, 123, 25, 117, 82, 54, 2, -99, 20, 46, 67, 26, 105, 118, -118, 97, 51, 10, -24, 36, -81, 21, -86, 82, 91, 20, 91, 37, 114, 20, -46, 4, 91, -120, -17, 8, 28, 119, 47, -97, -4, -79, -52, 108, -53, 46, -79};
        b[161] = new byte[]{10, 80, -126, 58, -3, 38, 15, -33, -1, 45, 29, 14, -118, -63, -33, 106, 108, 92, 78, -7, -91, -64, -62, -32, 0, -124, 45, -34, 22, -50, -115, 19, -28, 95, 1, 32, 104, 60, -49, -46, -18, -117, 55, -64, 77, -63, -45, -107, -61, -30, -31, -83, -68, -52, 35, -52, -48, -52, 123, -24, -18, 97, -113, -111, 2, 2, 121, -24, -90, -1, 31, -79, -104, 99, -48, -74, 101, -89, 95, 16, -106, -120, -67, 110, 127, -1, 30, 71, -107, -82, -53, -47, 43, -14, -55, 19, 7, -54, -58, -29, -5, -101, 111, 107, 44, -68, 41, 46, 108, -26, -49, 2, 67, 18, 58, 1, 103, 95, -43, 45, -58, 112, -115, -29, -23, -52, 53, 93, -81, -66, 126, 49, 49, 52, -81, -67, 12, -104, 89, -20, 31, -98, 83, 100, 126, 84, -14, -67, -83, 94, -92, 79, 81, 85, 109, -111, -87, 27, 73, -118, 70};
        b[162] = new byte[]{22, -49, -128, -104, 80, -89, 82, -65, -30, -101, 15, 3, -48, 106, 77, -119, -46, 15, -10, -84, 22, -13, 36, 64, -50, 107, 106, 61, 47, -81, 79, -87, -7, 83, -122, 5, 65, -54, 96, 111, 57, -21, 96, -18, 19, 62, -87, 21, 23, 106, 51, -42, 106, 96, 45, -126, 32, -57, -5, -74, 94, -54, -19, -106, 49, -105, -29, -94, -33, 99, -61, -78, 80, -10, -80, -29, -9, 90, -33, -60, 6, -98, 10, 8, -107, 45, -100, 55, 77, 92, -128, -35, -34, 125, 40, -52, -106, 118, 69, -93, 92, 124, 79, -38, 116, 54, 125, 72, -127, 24, 30, -68, -25, 121, 123, 53, 84, 47, -75, -99, 99, -73, -98, -120, 77, 105, 93, 119, 6, 6, 0, -40, -97, -77, 67, 66, -104, 116, 116, 46, -121, 14, 80, 98, 75, -34, 91, 12, 87, 25, -5, -95, 88, -118, 1, 56, 91, 38, -23, -80, -31, -53};
        b[163] = new byte[]{-62, 3, -72, 13, -47, -103, -53, -40, 53, -36, -117, -114, -122, 66, 70, 37, -60, -24, -56, -69, -126, 5, 89, -67, 79, -9, 60, -87, -114, 13, 58, 23, -107, 83, 83, 86, -38, 15, 44, 60, -118, 62, 70, 33, 44, 73, 36, -4, -77, 30, -66, 38, 31, -64, -2, 70, -126, -59, -117, -55, -12, 43, 32, -100, 24, -39, 48, -59, -111, -79, -35, -4, -36, 118, 64, -96, -101, 100, -57, 2, -81, -54, -121, -105, -49, 48, -2, -90, -31, 111, -88, -92, 1, 16, -86, -27, 4, 76, 31, 125, 120, 75, 14, -127, 80, 1, 109, -78, 113, -117, 65, 27, 59, 101, -48, 107, 20, -74, -125, -88, 111, 105, -17, -18, 39, -89, -57, 92, 67, -24, -93, 89, 53, -84, 88, -57, 99, -101, 68, 6, 18, -3, -17, 11, 75, 122, 96, -27, -39, 46, -12, -61, -63, -89, 85, -11, -38, 111, 25, 33, 116, 64, 107};
        b[164] = new byte[]{18, 69, -19, -45, 100, -102, 69, 66, 35, 22, 106, 11, -57, 35, -70, 55, -24, 34, 47, -120, -128, -31, -10, 52, 19, 22, -88, -6, 104, -32, 17, -10, 126, 90, -2, -80, -9, -111, -105, 28, 18, 53, -58, 86, 12, 84, -71, 14, 114, 123, -67, -99, -21, 28, -67, -59, -48, 31, 67, -85, -88, -65, 110, 126, -118, -60, 81, 49, -109, 74, 13, -100, -117, -82, -26, -32, 106, -16, 2, -68, -116, -53, -18, 94, 82, -114, -5, 67, -111, -87, 88, -89, -20, -125, -55, -61, 108, 63, 26, -34, 5, 8, -1, -47, 4, -46, -64, -115, -89, -69, 76, 104, -84, 40, 39, -98, -100, -58, 22, -58, -35, 50, 88, -90, -91, -109, -90, -83, -1, 75, 122, -69, -21, 31, -73, 17, 50, -2, -49, 24, 122, 16, -56, 124, 48, -109, -33, 89, -62, 49, -108, 52, -69, 122, -14, -86, 116, -8, 18, -45, 66, -96, 82, -39};
        b[165] = new byte[]{58, -32, 106, -57, -110, -59, 38, -70, 17, 108, 57, -8, -41, -18, 26, -52, 81, -27, 42, 12, 58, 108, -56, 27, -79, 17, -102, 43, -43, 39, 62, -8, -71, -59, -7, -107, -31, 34, -48, -39, -116, -56, -122, -118, 70, -117, 81, 121, -20, -66, -81, 42, -23, 26, 91, 69, -27, 22, 79, -59, 127, -108, -35, -28, -125, 2, -19, 79, -92, -67, 114, -91, 127, -57, -58, 32, -124, 60, 21, -95, -56, -46, -10, -56, 95, -49, 78, 38, 80, 106, -108, -128, -86, 126, -44, -115, -119, 84, 113, -75, -95, 62, 104, 40, 116, -84, 26, 3, 126, 3, -16, -72, 47, 42, 4, 45, -76, 35, 83, -73, 100, -69, -118, 125, -27, 21, 26, 67, 14, 44, 69, -98, 100, -76, -70, -102, 103, 39, -35, -60, -124, 70, 63, 104, -3, -47, 37, -15, 6, 48, 66, -8, 71, -7, 81, -70, 97, 97, 127, -1, 41, 34, -43, 91, -68};
        b[166] = new byte[]{72, -17, -17, 14, -54, -103, 13, -46, 38, 57, -47, -11, -10, -58, 72, -20, 85, 21, -122, 92, 117, 16, -115, 23, 70, -65, 0, -83, 59, -128, 27, -111, 124, 44, 50, -69, -24, 23, 45, -40, 46, -33, 93, 53, 40, -62, 41, 87, -100, -107, -95, 67, 27, -37, 26, 43, -91, -111, 65, -110, -71, 88, -43, 63, 93, -77, 105, -49, 91, 74, 20, 105, -81, 82, -9, -71, -57, 94, -18, 125, 95, -9, 47, -51, 112, 114, 66, -63, -38, -109, 46, -82, -47, 99, 40, 8, -75, 14, 41, -91, 1, -94, 63, -40, -41, 127, 96, -25, -2, -87, -107, 101, -108, -31, -7, 38, 55, 1, -112, -120, 75, -29, -107, -93, 98, -64, -119, -126, 71, 90, 124, -13, -95, 127, 25, -73, -25, 78, 98, 35, -128, -3, -108, 10, 102, 39, -107, 38, 87, 60, -110, -49, -74, -58, -90, 15, 15, -98, 94, 31, 70, -19, 102, 126, 97, -52};
        b[167] = new byte[]{-75, -110, -1, -88, 0, -116, 59, -44, -77, -102, 2, 97, 89, 3, 44, 108, -88, 4, -26, 83, -64, 17, -68, 103, -36, -44, 123, -25, 78, 34, -106, 0, 43, 71, -56, -5, -70, -3, 74, 23, -114, -82, 67, 40, -21, -112, 73, -14, 6, -118, -26, 96, 31, -6, -100, -79, -91, 30, 27, 122, -40, -124, 31, -76, -58, 31, -26, 1, -23, 28, 87, -45, -66, -23, 17, 40, 95, 59, -37, -52, -120, 96, 0, 24, 28, -12, 63, 90, 49, -13, 29, -43, 82, -87, 66, 106, 23, 102, -68, 68, 78, -110, -66, -10, 62, 41, 34, -106, 44, -59, 55, -122, 57, -11, 127, -89, 106, 95, -97, 125, -123, -128, -5, 2, 118, -49, 25, 58, 69, 40, 53, 57, -80, -79, 78, 93, -69, -78, -99, 77, 108, 61, 72, 79, -7, -82, 7, 122, -1, 114, 53, 78, -102, -76, -80, -24, -32, 41, 23, -3, -118, -73, 21, -70, 86, 86, -28};
        b[168] = new byte[]{25, 23, -69, -72, 93, 112, 31, -97, -5, -44, 29, -17, -104, 97, 126, -92, -35, -57, -12, -14, -114, -42, -46, -16, -78, 118, -12, 15, -113, -49, -101, -77, -18, -28, -96, 30, -32, -25, -34, -106, 71, -59, 32, 24, 50, -114, -22, 25, -82, -105, -124, -112, 102, 103, 111, -22, -97, -8, -87, -5, 42, -35, 97, 15, 6, 43, -4, -58, -3, 123, 11, -80, 57, 123, 27, 34, -110, -48, 50, 7, -31, -48, -114, 87, -80, -6, -42, -95, -57, 110, 87, 57, -2, -69, -9, 38, -35, -54, -12, 103, 93, 115, -77, 29, 0, 102, 52, -22, -66, -55, 64, 10, 64, -22, -118, -85, -104, -29, 82, 97, -76, 103, 92, -37, 100, 66, -68, -122, 40, -101, 105, 126, -49, 51, 8, 51, 32, -113, -80, 119, -126, 72, 29, 76, 6, -17, 4, 88, -83, -30, 63, -76, -125, -35, -43, -83, -73, 12, -52, -125, 76, 90, -98, -125, -61, -126, 77, 20};
        b[169] = new byte[]{-16, 16, -59, -69, -19, -67, -106, -101, 101, 25, 40, -36, 101, -18, -32, -123, 102, -108, -128, -116, -37, -71, 74, 76, -27, 112, -59, 110, -46, 85, 64, -15, 29, -35, -80, 4, -47, -63, -111, -107, 1, 61, 18, 18, -105, -117, -110, 127, 34, -55, 24, 54, -76, 55, -25, -69, 65, -124, 15, -108, 52, 51, 87, 10, 38, -45, -40, 6, 70, -89, 53, -128, 115, 1, 58, 116, -1, -75, -32, 91, -111, 47, -111, -79, -86, 31, -19, 122, -29, -40, 42, -66, 72, 34, 126, -52, -35, 38, -32, 59, 39, 33, 103, 50, -56, -20, 113, -126, -67, 88, 65, 82, 34, 65, -69, -54, 43, 35, 117, -90, 0, -123, 92, 82, 16, 44, 48, 97, -20, 22, -9, -100, -109, -10, 66, 93, 0, 62, 82, -81, -42, 86, 117, -75, 84, -37, 58, -123, 41, -120, -122, -64, 5, 115, -99, 106, -78, 107, 16, 43, 78, -13, -39, 106, 109, -57, -118, -47, -113};
        b[170] = new byte[]{-78, 35, -46, 92, 62, 46, -33, -91, -104, -75, 59, -17, 15, 52, 101, 20, -8, -82, 100, -55, -47, 115, 58, -53, -35, -32, 53, -25, -34, 121, 36, 48, -100, -21, -11, 118, 10, -5, -70, -101, -15, -49, 59, -94, 113, -94, 12, 78, -47, -90, 19, 103, -101, 94, -115, -128, -50, 33, -104, 1, -53, -115, 3, -127, 70, 76, -88, -14, 118, 34, -106, 35, -78, 101, 27, -114, 50, 84, -14, -17, -50, -89, 8, 76, -94, 126, 118, -26, 109, -17, -48, 70, 37, -65, 40, -10, 66, -99, -23, -64, 82, -125, -110, -7, -36, 43, 118, -115, -116, -35, -54, 105, -91, -6, 30, -30, 50, -22, 76, -86, -80, 57, 116, 5, 49, 39, 7, -104, -119, -56, 114, -47, 4, 41, 30, -50, 116, -54, -45, 95, 110, -29, -58, 31, -78, -68, -10, -96, 107, -103, 113, 108, -51, -113, -97, -17, 40, -57, 113, 64, -8, -78, 39, -47, 7, -54, -116, 124, 2, -71};
        b[171] = new byte[]{-5, -3, 120, 62, 113, 80, 85, -98, -63, 76, 114, -108, -66, 68, -19, 70, -74, 18, -19, 105, -49, 52, 62, 96, 103, -106, -14, 50, 21, 43, 12, -71, 9, 107, -91, 15, 5, 94, -13, -46, 122, -61, 50, 39, -60, -93, -9, 80, 125, 74, -73, 18, 48, -94, 27, -84, 70, 86, 26, -44, -68, 12, -32, -58, -93, -112, 101, 53, -75, 99, -38, 125, 51, 66, -106, 91, -31, 52, 94, -16, -112, -90, -111, -59, -90, -29, 42, 23, 91, -40, -80, 93, 14, 15, -96, 12, -120, 79, 73, -76, 105, -42, -77, 114, 92, 105, 99, -1, 92, 17, 127, 10, -8, 80, -108, 81, -101, 30, 45, -33, 110, -80, -1, -123, -92, -60, 6, 121, -87, 37, 96, 96, 49, -46, -85, -119, -73, 91, 92, 123, 65, 72, 86, 118, 51, 48, -105, -39, -32, -19, -58, -128, -35, 69, -47, 21, 26, -50, 47, -2, -81, 32, 84, 20, -66, 90, -55, -39, -35, 52, -59};
        b[172] = new byte[]{-33, 82, 53, -115, -81, -127, -7, -86, 41, 42, 76, -19, 27, 50, 88, -68, -111, 22, 2, -56, 114, -96, 0, 21, -25, 43, -34, -32, -71, -49, -46, 71, 72, -103, 68, 26, 84, 68, -46, -70, -114, 66, 12, -122, 43, -17, 51, 60, 48, 32, -11, 121, -125, -23, 46, 39, 38, 26, 112, 62, 11, -35, -69, -93, -44, 6, 1, 82, -9, 114, -45, -65, 12, 37, -95, 54, 41, -42, -20, -39, -98, 96, -125, 62, -93, -121, 98, 40, 25, 3, 62, 49, -26, 73, 102, -66, -98, -82, 39, -81, -24, 14, 106, -98, 2, -73, 2, -10, -63, 109, -127, -27, -118, -83, -37, -86, 74, -128, -33, 110, 1, 78, 66, -103, 50, -10, -22, 22, 5, -128, -63, 14, 101, -6, -28, 67, -75, -35, 96, 108, -74, 8, -33, 17, 36, -71, -93, 121, 33, 85, 84, -48, 70, -4, 84, -38, 43, -69, 105, -69, 45, 11, 65, 41, 36, -93, -84, 28, -52, 22, 6, 122};
        b[173] = new byte[]{93, -76, 95, 14, -95, -98, -35, -25, -120, -42, -47, -15, -15, 73, -49, -99, -32, -63, -25, -77, 124, 85, -28, -110, -51, 124, -43, 8, -44, -96, -60, -31, 31, 119, -89, 127, -70, 126, 66, 15, 17, -58, -6, 110, 48, -34, -46, 27, -75, 41, 50, -31, 29, -66, -52, -63, 126, 75, -105, 17, -67, 51, -80, 103, -5, -34, 122, 6, 45, 44, -92, -15, 3, 73, -107, 121, -43, -117, 22, 71, -31, -2, -2, 13, -128, -14, 126, 52, 69, 62, 115, 41, 91, 73, 38, 75, 1, -32, 104, 124, 59, -24, 39, 9, -40, 42, -24, -51, -40, -95, 90, -16, -15, -128, -58, -67, -40, -14, -68, -73, -87, -39, 18, -16, -26, -11, 102, 33, -40, 2, 98, -17, 41, 47, 108, 25, -65, -17, -115, 59, -47, -53, -26, -72, 67, -71, 14, 122, -101, 38, -20, -10, 34, -78, 41, -54, 122, 80, 64, -68, 57, -29, -106, 46, -65, -108, -21, -83, 26, -73, -89, -115, 126};
        b[174] = new byte[]{-116, 97, 35, -4, 6, -127, 121, -10, -100, -10, -29, -100, -104, 107, -114, -10, 113, -2, 55, -20, -52, -80, 46, 42, 1, -18, -103, 80, 67, 12, 33, 119, 83, 26, 116, -106, -105, -75, -19, -45, -7, -54, -85, 35, -93, -35, -111, -10, -29, -47, -16, -112, -97, 27, -97, -112, -66, -47, -68, -50, -91, 13, 94, 3, 115, -15, 18, -82, -111, 124, -93, 43, -116, 114, 79, 38, 100, -39, 94, -76, -124, 73, 56, -33, -113, 122, 13, 45, 79, -62, -86, -96, -50, -87, -33, 51, -99, 56, 13, -22, 123, -25, 81, -48, -98, 74, -46, -52, -121, -42, -26, 88, 93, 126, -48, 109, 117, -100, 60, 32, -24, 100, 22, 89, 13, 51, -115, 80, -45, -85, -104, -5, -95, -59, -36, 20, -20, -108, 114, -19, 28, -54, 94, 88, 13, -59, 15, -46, 45, -90, 3, 98, 110, -106, 82, 39, 83, 107, -127, -75, -18, -25, 78, -121, -110, 24, 29, -59, 39, 18, -82, 120, 22, -17};
        b[175] = new byte[]{68, 12, -51, 11, 25, 79, -39, 47, -4, 45, -10, -58, -74, 84, -125, 74, 26, 49, -15, -60, -96, -104, -109, 14, 115, 108, 79, 22, -49, 30, -127, -107, 73, -117, 9, 102, -99, 45, -38, 5, 98, -113, 69, -74, -15, -111, -104, -107, 85, -119, 110, -32, 99, 73, 55, 6, -24, 113, -50, -99, -126, -71, 118, -16, 117, -64, -79, -120, -92, 19, -100, 48, -95, 13, 76, 120, -30, 86, 83, 124, -13, 22, 38, 4, 54, 30, -46, 44, 20, 31, -115, 83, -48, 30, -25, 28, -74, -65, 15, -23, 25, 123, 96, -66, 112, 29, -49, -102, 11, -23, -67, -39, 29, -37, -97, 57, -71, -82, -99, 79, 120, 121, -69, -69, 60, -74, -119, 69, 13, -47, -56, 31, 111, 2, 120, 22, -70, 87, -123, 83, 19, -80, 5, 41, 77, -72, 53, -5, 77, -5, 17, 103, 6, 58, -20, 98, -81, -121, 70, -37, 16, 95, -128, -20, 26, -7, -24, -59, 30, 76, 59, -53, 66, -42, 71};
        b[176] = new byte[]{-45, -56, 102, 102, -101, 65, 24, -94, -86, 71, -92, 51, -12, 99, 92, -39, 105, 53, 122, -40, 20, -4, -86, 32, 110, 35, -48, -104, 8, 121, 33, 121, 83, 5, -49, 123, 14, -31, 19, -96, -66, 1, 111, -49, 61, -99, -13, -101, 85, 15, -42, 122, 89, -94, 9, 47, 48, 18, 52, -127, 118, 105, -27, -110, 69, 59, -3, -66, 84, 79, 115, -8, -109, 90, 65, -23, -100, 79, 31, -44, 37, 118, 62, 90, 107, 57, 0, -1, 32, -101, -46, 76, -61, 124, -103, 92, 116, 83, -95, -43, -32, 28, -69, -110, -17, -63, 53, -50, -117, 57, -113, -62, -64, -17, 36, 73, -50, -111, -96, -39, -84, -95, 18, 102, -56, 108, 29, 77, -128, -24, -52, 122, 107, 9, 61, -2, -118, -33, -22, 32, 101, 36, 116, 8, 32, 125, -27, -30, -115, -37, -42, 12, 118, 76, -42, -1, -123, 72, 29, 2, 92, 45, 64, 56, 111, 40, -19, -20, -112, 29, 15, 75, -22, -36, 88, 12};
        b[177] = new byte[]{-48, 104, 107, -127, -76, -110, 28, -14, -42, -73, 36, -74, -102, 116, -79, -84, -48, 35, -3, -36, -85, 21, 7, 49, 34, 3, -29, 79, -65, -69, -15, 38, 117, 101, 49, -100, -45, 123, -20, -102, 86, 120, 103, -10, -50, 59, -50, 4, -9, 34, -101, -107, -94, 57, 117, 33, -43, 94, -68, 121, -20, -67, 36, -118, 66, -4, -74, 2, -126, -80, 9, 20, 52, -19, -27, -1, -116, -17, -28, 87, -62, -83, 20, -32, 27, 30, 44, 21, 49, 65, -44, -45, 92, 45, -52, 26, -126, 75, -59, -51, -41, -69, -21, -128, -29, 98, 104, -8, 97, -104, 88, -79, 116, -103, 9, 82, -94, -46, -21, -71, 51, -57, -23, -128, 96, -39, -11, 67, 18, 21, 29, 52, -15, -58, -31, 104, -118, 5, 45, 100, -128, 54, -88, -115, 37, -124, -81, 77, 6, 97, 25, 103, -53, -76, -125, -58, 44, -33, -96, 32, -29, 28, 104, -88, 113, -43, -66, 111, -125, -93, -104, 75, 62, -110, 112, 3, 63};
        b[178] = new byte[]{-39, 81, -94, 49, 95, -33, -38, 19, 89, 2, 60, 55, 125, -41, -121, -57, -4, 17, 33, 92, -34, 63, 77, 124, -107, -100, -89, -49, 4, -78, 72, 111, -13, 64, 28, 114, 70, 104, -73, 98, 98, -107, 102, 104, 119, 104, -101, -89, -70, 42, -90, -67, -37, 116, -112, -98, -91, 95, -77, 60, 5, -88, -40, 28, -16, -127, -110, -94, 78, -57, -54, 15, 39, -14, -75, -96, 0, -15, 86, 9, -38, -67, -98, -67, 116, -90, 43, 75, 47, 127, 62, 110, -84, 31, 24, -59, -83, -44, -77, -11, 55, -91, 80, -81, 108, -126, -127, 83, 89, 46, -122, -58, 12, -42, 18, -91, -47, 4, 48, -23, 25, 73, -20, 53, -76, -124, 113, 98, -49, 41, 82, -51, 54, -122, 0, 114, 91, 88, -9, -62, 89, 104, -44, -103, 5, 119, 41, 107, -47, 94, 72, -34, -20, 85, 78, 55, -85, 98, 4, 112, 60, 119, 102, 66, 115, 68, -102, 101, -2, 86, -5, -97, -24, -81, 51, 5, -1, 57};
        b[179] = new byte[]{-120, -60, -126, -121, 89, -71, -67, -53, 119, 11, -78, -60, -97, -100, 10, 69, -117, 90, -105, 59, -58, -22, -110, -102, -124, 109, 125, -83, 54, -6, 103, -38, -69, -7, -69, -125, -80, -62, -7, -56, -50, -115, -88, 59, -24, 87, 27, 119, 14, -2, -54, 71, 7, 82, -33, 17, -65, 63, 38, -5, 108, -111, -81, 67, 102, -72, 22, 20, 103, 77, -82, 7, -68, -5, -27, -35, -54, 73, 116, -56, 2, 10, -5, 23, 121, 117, -24, 48, -66, 41, 31, 49, 101, -92, -3, 27, 54, -61, 40, -70, -103, -95, 67, 73, 33, 1, 110, 117, 60, 10, -8, 7, 94, -71, -69, 92, -71, 80, -70, 107, -69, 17, 62, 116, 108, 127, 28, -107, 16, -56, -57, -58, -124, -28, -103, -26, 48, -54, 43, 16, -7, 76, -42, 16, -112, 49, 24, -37, -59, -22, -106, 126, -123, -113, -57, 110, -51, 2, -13, 48, -120, 18, -96, 63, 10, 25, -110, -125, -14, 57, -70, -3, -1, 21, -62, -9, -8, -36, -47};
        b[180] = new byte[]{-124, -34, 62, -57, -5, 10, -12, 84, 89, -97, -103, 101, 107, 14, -1, 92, 49, -80, -122, -35, -63, 112, -116, 64, 20, -2, 9, -96, 106, -107, 64, 44, 60, 115, -110, 14, -107, 42, -118, 16, -27, 98, 98, -53, 53, -102, 123, -28, -93, -87, 21, -83, -51, -94, 48, 64, 60, -121, -60, 108, -80, -98, 29, 36, 26, 69, 13, -97, -37, -111, -24, 66, -54, 20, 38, 90, -101, 41, 102, -48, 47, 68, -34, 125, -102, 62, -89, -57, 68, -127, -126, 86, 6, 113, -61, -14, -82, 125, -34, -46, -21, -17, 24, -98, 103, 98, 104, -45, 3, -72, 42, 7, 70, 62, 20, 81, -128, 99, 8, 0, 59, 126, 57, -118, -100, 83, -84, -63, 108, -100, -97, 26, -9, 93, -58, -50, -102, 27, 37, -41, 7, -117, 14, 97, 126, -90, 10, -38, 42, -88, -15, 36, -13, 86, 88, -38, 24, -57, 57, 89, 44, -49, -21, -37, -59, 13, 49, -46, 75, 127, 88, 73, -10, -60, -13, -35, 19, 60, 24, 38};
    }

    /**
     * Tests to make sure Base64's implementation of the org.apache.commons.codec.Encoder
     * interface is behaving identical to commons-codec-1.3.jar.
     *
     * @throws EncoderException problem
     */
    @Test
    public void testEncoder() throws EncoderException {
        final Encoder enc = new Base64();
        for (int i = 0; i < STRINGS.length; i++) {
            if (STRINGS[i] != null) {
                final byte[] base64 = utf8(STRINGS[i]);
                final byte[] binary = BYTES[i];
                final boolean b = Arrays.equals(base64, (byte[]) enc.encode(binary));
                assertTrue("Encoder test-" + i, b);
            }
        }
    }

    /**
     * Tests to make sure Base64's implementation of the org.apache.commons.codec.Decoder
     * interface is behaving identical to commons-codec-1.3.jar.
     *
     * @throws DecoderException problem
     */
    @Test
    public void testDecoder() throws DecoderException {
        final Decoder dec = new Base64();
        for (int i = 0; i < STRINGS.length; i++) {
            if (STRINGS[i] != null) {
                final byte[] base64 = utf8(STRINGS[i]);
                final byte[] binary = BYTES[i];
                final boolean b = Arrays.equals(binary, (byte[]) dec.decode(base64));
                assertTrue("Decoder test-" + i, b);
            }
        }
    }

    /**
     * Tests to make sure Base64's implementation of the org.apache.commons.codec.BinaryEncoder
     * interface is behaving identical to commons-codec-1.3.jar.
     *
     * @throws EncoderException problem
     */
    @Test
    public void testBinaryEncoder() throws EncoderException {
        final BinaryEncoder enc = new Base64();
        for (int i = 0; i < STRINGS.length; i++) {
            if (STRINGS[i] != null) {
                final byte[] base64 = utf8(STRINGS[i]);
                final byte[] binary = BYTES[i];
                final boolean b = Arrays.equals(base64, enc.encode(binary));
                assertTrue("BinaryEncoder test-" + i, b);
            }
        }
    }

    /**
     * Tests to make sure Base64's implementation of the org.apache.commons.codec.BinaryDecoder
     * interface is behaving identical to commons-codec-1.3.jar.
     *
     * @throws DecoderException problem
     */
    @Test
    public void testBinaryDecoder() throws DecoderException {
        final BinaryDecoder dec = new Base64();
        for (int i = 0; i < STRINGS.length; i++) {
            if (STRINGS[i] != null) {
                final byte[] base64 = utf8(STRINGS[i]);
                final byte[] binary = BYTES[i];
                final boolean b = Arrays.equals(binary, dec.decode(base64));
                assertTrue("BinaryDecoder test-" + i, b);
            }
        }
    }

    /**
     * Tests to make sure Base64's implementation of Base64.encodeBase64()
     * static method is behaving identical to commons-codec-1.3.jar.
     *
     * @throws EncoderException problem
     */
    @Test
    public void testStaticEncode() throws EncoderException {
        for (int i = 0; i < STRINGS.length; i++) {
            if (STRINGS[i] != null) {
                final byte[] base64 = utf8(STRINGS[i]);
                final byte[] binary = BYTES[i];
                final boolean b = Arrays.equals(base64, Base64.encodeBase64(binary));
                assertTrue("static Base64.encodeBase64() test-" + i, b);
            }
        }
    }

    /**
     * Tests to make sure Base64's implementation of Base64.decodeBase64()
     * static method is behaving identical to commons-codec-1.3.jar.
     *
     * @throws DecoderException problem
     */
    @Test
    public void testStaticDecode() throws DecoderException {
        for (int i = 0; i < STRINGS.length; i++) {
            if (STRINGS[i] != null) {
                final byte[] base64 = utf8(STRINGS[i]);
                final byte[] binary = BYTES[i];
                final boolean b = Arrays.equals(binary, Base64.decodeBase64(base64));
                assertTrue("static Base64.decodeBase64() test-" + i, b);
            }
        }
    }

    /**
     * Tests to make sure Base64's implementation of Base64.encodeBase64Chunked()
     * static method is behaving identical to commons-codec-1.3.jar.
     *
     * @throws EncoderException problem
     */
    @Test
    public void testStaticEncodeChunked() throws EncoderException {
        for (int i = 0; i < STRINGS.length; i++) {
            if (STRINGS[i] != null) {
                final byte[] base64Chunked = utf8(CHUNKED_STRINGS[i]);
                final byte[] binary = BYTES[i];
                final boolean b = Arrays.equals(base64Chunked, Base64.encodeBase64Chunked(binary));
                assertTrue("static Base64.encodeBase64Chunked() test-" + i, b);
            }
        }
    }

    /**
     * Tests to make sure Base64's implementation of Base64.decodeBase64()
     * static method is behaving identical to commons-codec-1.3.jar when
     * supplied with chunked input.
     *
     * @throws DecoderException problem
     */
    @Test
    public void testStaticDecodeChunked() throws DecoderException {
        for (int i = 0; i < STRINGS.length; i++) {
            if (STRINGS[i] != null) {
                final byte[] base64Chunked = utf8(CHUNKED_STRINGS[i]);
                final byte[] binary = BYTES[i];
                final boolean b = Arrays.equals(binary, Base64.decodeBase64(base64Chunked));
                assertTrue("static Base64.decodeBase64Chunked() test-" + i, b);
            }
        }
    }

    private static byte[] utf8(final String s) {

        // We would use commons-codec-1.4.jar own utility method for this, but we
        // need this class to be able to run against commons-codec-1.3.jar, hence the
        // duplication here.

        return s != null ? s.getBytes(Charsets.UTF_8) : null;
    }
}

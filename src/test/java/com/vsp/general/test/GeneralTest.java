package com.vsp.general.test;

import org.junit.Test;

import java.math.BigDecimal;

public class GeneralTest {
    @Test
    public void bigDecimal() throws Exception {
        String value = "130";
        System.out.println(new BigDecimal(value));

        value = "0";
        System.out.println(new BigDecimal(value));
        // those 2 above are able to display the right values

        // those 2 below get exception
        value = "";
        System.out.println(new BigDecimal(value));

        value = null;
        System.out.println(new BigDecimal(value));
    }
}

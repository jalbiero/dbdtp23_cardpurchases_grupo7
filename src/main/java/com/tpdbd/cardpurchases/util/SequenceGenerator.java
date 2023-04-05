package com.tpdbd.cardpurchases.util;

/** 
 * Very simple sequence generator
 */
public class SequenceGenerator {
    private int value;
    private String prefix;

    public SequenceGenerator() {
        this(0, "");
    }

    public SequenceGenerator(int initialValue) {
        this(initialValue, "");
    }

    public SequenceGenerator(String prefix) {
        this(0, prefix);
    }

    public SequenceGenerator(int initialValue, String prefix) {
        this.value = initialValue;
        this.prefix= prefix;
    }

    public String getNextValue() {
        return String.format("%s%d", this.prefix, this.value++);
    }
}
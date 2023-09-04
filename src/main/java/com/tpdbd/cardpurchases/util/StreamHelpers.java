package com.tpdbd.cardpurchases.util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamHelpers {
    public static <T> Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> Stream<T> toStream(Iterable<T> iterable, boolean parallel) {
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }
}

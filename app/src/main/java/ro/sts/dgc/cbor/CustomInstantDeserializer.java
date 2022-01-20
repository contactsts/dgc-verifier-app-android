package ro.sts.dgc.cbor;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CustomInstantDeserializer extends InstantDeserializer<Instant> {

    private static final long serialVersionUID = 3929100820024454525L;

    public static final CustomInstantDeserializer INSTANT = new CustomInstantDeserializer(
            Instant.class, DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            Instant::from,
            a -> Instant.ofEpochMilli(a.value),
            a -> Instant.ofEpochSecond(a.integer, a.fraction),
            null,
            true);

    protected CustomInstantDeserializer(final Class<Instant> supportedType,
                                        final DateTimeFormatter formatter,
                                        final Function<TemporalAccessor, Instant> parsedToValue,
                                        final Function<FromIntegerArguments, Instant> fromMilliseconds,
                                        final Function<FromDecimalArguments, Instant> fromNanoseconds,
                                        final BiFunction<Instant, ZoneId, Instant> adjust,
                                        final boolean replaceZeroOffsetAsZ) {

        super(supportedType, formatter, parsedToValue, fromMilliseconds, fromNanoseconds, adjust, replaceZeroOffsetAsZ);
    }
}

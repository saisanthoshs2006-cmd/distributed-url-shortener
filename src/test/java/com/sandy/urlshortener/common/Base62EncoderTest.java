package com.sandy.urlshortener.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    void encodesDatabaseIdsWithStableBase62Values() {
        assertThat(encoder.encode(0)).isEqualTo("0");
        assertThat(encoder.encode(61)).isEqualTo("Z");
        assertThat(encoder.encode(62)).isEqualTo("10");
        assertThat(encoder.encode(3_844)).isEqualTo("100");
    }

    @Test
    void rejectsNegativeIds() {
        assertThatThrownBy(() -> encoder.encode(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

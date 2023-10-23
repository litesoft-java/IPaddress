package org.litesoft.networkaddress;

import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import org.litesoft.annotations.Significant;
import org.litesoft.pragmatics.Context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class IPaddressTest {
    @Test
    void validate() {
        assertEquals( "0.0.0.0", expectSuccess( IPaddress::validate, "000.0.00.000" ) );
        assertEquals( "100.20.30.40", expectSuccess( IPaddress::validate, "100.20.30.040" ) );

        assertEquals( "IPaddress IP V4 must be at least 7 characters, but was 6, IP Address was: 0.0..0",
                      expectException( IPaddress::validate, "0.0..0" ) );
        assertEquals( "IPaddress IP V4 must be 4 sections, but found 3, IP Address was: 100.20.300",
                      expectException( IPaddress::validate, "100.20.300" ) );
        assertEquals( "IPaddress IP V4 section 3 value not in range (0-255), but was 300, IP Address was: 100.20.300.40",
                      expectException( IPaddress::validate, "100.20.300.40" ) );
        assertEquals( "IPaddress IP V4 section 3 value not a number, but was '', IP Address was: 100.20..40",
                      expectException( IPaddress::validate, "100.20..40" ) );

        assertEquals( "0000:0000:0000:0000:0000:0000:0000:0000",
                      expectSuccess( IPaddress::validate, "0000:0000:0000:0000:0000:0000:0000:0000" ) );
        assertEquals( "1234:5678:90AB:CDEF:0123:4567:890A:BCDE",
                      expectSuccess( IPaddress::validate, "1234:5678:90ab:cdef:0123:4567:890A:bCde" ) );

        assertEquals( "IPaddress IP V4 must be 4 sections, but found 1, IP Address was: 100:20:300",
                      expectException( IPaddress::validate, "100:20:300" ) );
        assertEquals( "IPaddress IP V6 must be 8 sections, but found 7, IP Address was: 1000:2000:3000:4000:5000:6000:7000-8000",
                      expectException( IPaddress::validate, "1000:2000:3000:4000:5000:6000:7000-8000" ) );
        assertEquals( "IPaddress IP V6 must be 8 sections, but found 9, IP Address was: 1000:2000:3000:4000:5000:6000:7000:8:00",
                      expectException( IPaddress::validate, "1000:2000:3000:4000:5000:6000:7000:8:00" ) );
        assertEquals( "IPaddress IP V6 section 7 value not 4 Hexa-Decimal 'digits', but was '7-00', IP Address was: 1000:2000:3000:4000:5000:6000:7-00:8000",
                      expectException( IPaddress::validate, "1000:2000:3000:4000:5000:6000:7-00:8000" ) );
    }

    @Test
    void validate_v4() {
        assertEquals( "0.0.0.0", expectSuccess( IPaddress::validate_v4, "000.0.00.000" ) );
        assertEquals( "100.20.30.40", expectSuccess( IPaddress::validate_v4, "100.20.30.040" ) );

        assertEquals( "IPaddress IP V4 must be at least 7 characters, but was 6, IP Address was: 0.0..0",
                      expectException( IPaddress::validate_v4, "0.0..0" ) );
        assertEquals( "IPaddress IP V4 must be 4 sections, but found 3, IP Address was: 100.20.300",
                      expectException( IPaddress::validate_v4, "100.20.300" ) );
        assertEquals( "IPaddress IP V4 section 3 value not in range (0-255), but was 300, IP Address was: 100.20.300.40",
                      expectException( IPaddress::validate_v4, "100.20.300.40" ) );
        assertEquals( "IPaddress IP V4 section 3 value not a number, but was '', IP Address was: 100.20..40",
                      expectException( IPaddress::validate_v4, "100.20..40" ) );
    }

    @Test
    void validate_v6() {
        assertEquals( "0000:0000:0000:0000:0000:0000:0000:0000",
                      expectSuccess( IPaddress::validate_v6, "0000:0000:0000:0000:0000:0000:0000:0000" ) );
        assertEquals( "1234:5678:90AB:CDEF:0123:4567:890A:BCDE",
                      expectSuccess( IPaddress::validate_v6, "1234:5678:90ab:cdef:0123:4567:890A:bCde" ) );

        assertEquals( "IPaddress IP V6 expected length of 39, but was 10, IP Address was: 100:20:300",
                      expectException( IPaddress::validate_v6, "100:20:300" ) );
        assertEquals( "IPaddress IP V6 must be 8 sections, but found 7, IP Address was: 1000:2000:3000:4000:5000:6000:7000-8000",
                      expectException( IPaddress::validate_v6, "1000:2000:3000:4000:5000:6000:7000-8000" ) );
        assertEquals( "IPaddress IP V6 must be 8 sections, but found 9, IP Address was: 1000:2000:3000:4000:5000:6000:7000:8:00",
                      expectException( IPaddress::validate_v6, "1000:2000:3000:4000:5000:6000:7000:8:00" ) );
        assertEquals( "IPaddress IP V6 section 7 value not 4 Hexa-Decimal 'digits', but was '7-00', IP Address was: 1000:2000:3000:4000:5000:6000:7-00:8000",
                      expectException( IPaddress::validate_v6, "1000:2000:3000:4000:5000:6000:7-00:8000" ) );
    }

    private String expectSuccess( BiFunction<Context, String, String> validateMethod, @Significant String ipAddress ) {
        return validateMethod.apply( null, ipAddress );
    }

    private String expectException( BiFunction<Context, String, String> validateMethod, @Significant String ipAddress ) {
        String result;
        try {
            result = validateMethod.apply( null, ipAddress );
        }
        catch ( RuntimeException e ) {
            return e.getMessage();
        }
        fail( "Expected Exception from '" + ipAddress + "', but got result of: '" + result + "'" );
        return null;
    }
}
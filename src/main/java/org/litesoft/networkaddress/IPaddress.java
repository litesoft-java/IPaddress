package org.litesoft.networkaddress;

import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Significant;
import org.litesoft.pragmatics.Context;

public class IPaddress {
    private static final Context CONTEXT = new Context( "IPaddress" );
    private static final int IPV6_LENGTH = 39;
    private static final int IPV4_MIN_LENGTH = 7;

    public static String validate( @NotNull Context context, @Significant String ipAddress ) {
        return new IPaddress( context, ipAddress ).check_either();
    }

    @SuppressWarnings("unused")
    public static String validate_v4( @NotNull Context context, @Significant String ipAddress ) {
        return new IPaddress( context, ipAddress ).check_v4();
    }

    @SuppressWarnings("unused")
    public static String validate_v6( @NotNull Context context, @Significant String ipAddress ) {
        return new IPaddress( context, ipAddress ).check_v6();
    }

    private final Context context;
    private final String ipAddress;

    private IPaddress( Context context, String ipAddress ) {
        this.context = NotNull.ConstrainTo.valueOr( context, CONTEXT );
        this.ipAddress = Significant.AssertArgument.contextValue( this.context, ipAddress );
    }

    String check_either() {
        return (ipAddress.length() == IPV6_LENGTH) ? check_v6() : check_v4();
    }

    String check_v4() {
        int len = ipAddress.length();
        if ( len < IPV4_MIN_LENGTH ) {
            throw error( "IP V4 must be at least " + IPV4_MIN_LENGTH + " characters, but was " + len );
        }
        String[] parts = ipAddress.split( "\\." );
        if ( parts.length != 4 ) {
            throw error( "IP V4 must be 4 sections, but found " + parts.length );
        }
        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < 4; i++ ) {
            sb.append( '.' ).append( checkV4section( i, parts[i] ) );
        }
        return sb.deleteCharAt( 0 ).toString();
    }

    private int checkV4section( int offset, String part ) {
        part = part.trim();
        try {
            int value = Integer.parseInt( part );
            if ( (0 <= value) && (value <= 255) ) {
                return value;
            }
            throw error( "IP V4 section " + (offset + 1) + " value not in range (0-255), but was " + value );
        }
        catch ( NumberFormatException e ) {
            throw error( "IP V4 section " + (offset + 1) + " value not a number, but was '" + part + "'" );
        }
    }

    String check_v6() {
        int len = ipAddress.length();
        if ( len != IPV6_LENGTH ) {
            throw error( "IP V6 expected length of " + IPV6_LENGTH + ", but was " + len );
        }
        String[] parts = ipAddress.split( ":" );
        if ( parts.length != 8 ) {
            throw error( "IP V6 must be 8 sections, but found " + parts.length );
        }
        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < 8; i++ ) {
            sb.append( ':' ).append( checkV6section( i, parts[i] ) );
        }
        return sb.deleteCharAt( 0 ).toString();
    }

    private String checkV6section( int offset, String part ) {
        if ( part.length() == 4 ) {
            StringBuilder sb = new StringBuilder();
            for ( int i = 0; i < 4; i++ ) {
                sb.append( normalizeHex( part.charAt( i ) ) );
            }
            if ( sb.length() == 4 ) {
                return sb.toString();
            }
        }
        throw error( "IP V6 section " + (offset + 1) + " value not 4 Hexa-Decimal 'digits', but was '" + part + "'" );
    }

    private Character normalizeHex( char c ) {
        if ( ('0' <= c) && (c <= '9') ) {
            return c;
        }
        c = Character.toUpperCase( c );
        if ( ('A' <= c) && (c <= 'F') ) {
            return c;
        }
        return null;
    }

    IllegalArgumentException error( String why ) {
        return new IllegalArgumentException( context.get() + " " + why + ", IP Address was: " + ipAddress );
    }
}

/*
 * The MIT License (MIT)
 *
 * Copyright (c) Open Application Platform Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package oap.application.daf.geo;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Optional;

@Slf4j
public class MaxmindGeodata implements Closeable, Geodata {
    private DatabaseReader database;
    private final String mmdb;

    public MaxmindGeodata( String mmdb ) {
        this.mmdb = mmdb;
    }

    @SneakyThrows
    public void start() {
        log.debug( "loading maxmind database {}", mmdb );
        try( InputStream stream = getClass().getResourceAsStream( mmdb ) ) {
            if( stream == null ) throw new IOException( "cannot load " + mmdb );
            database = new DatabaseReader.Builder( stream ).build();
            log.debug( "maxmind database loaded {}", database.getMetadata() );
        }
    }


    @Override
    public void close() throws IOException {
        database.close();
    }

    @Override
    public Optional<Geo> byIp( String ip ) {
        try {
            return database.tryCountry( InetAddress.getByName( ip ) )
                .flatMap( r -> Optional.ofNullable( r.getCountry() )
                    .map( country -> Geo.of( country.getIsoCode(), country.getName() ) ) );
        } catch( IOException | GeoIp2Exception e ) {
            log.error( e.getMessage(), e );
            return Optional.empty();
        }
    }
}

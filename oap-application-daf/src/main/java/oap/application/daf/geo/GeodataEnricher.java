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

import lombok.extern.slf4j.Slf4j;
import oap.application.daf.Request;
import oap.application.daf.RequestEnricher;
import oap.application.daf.device.WithDevice;

@Slf4j
public class GeodataEnricher<Rx extends Request & WithDevice & WithGeo> implements RequestEnricher<Rx> {
    private Geodata geodata;

    public GeodataEnricher( Geodata geodata ) {
        this.geodata = geodata;
    }

    @Override
    public void enrich( Rx request ) {
        String ip = request.device().ip;
        geodata.byIp( ip ).ifPresentOrElse( geo -> {
            request.enrichGeo( geo );
            log.debug( "{} -> {}", ip, geo );
        }, () -> log.debug( "cannot resolve geo for {}", ip ) );
    }
}

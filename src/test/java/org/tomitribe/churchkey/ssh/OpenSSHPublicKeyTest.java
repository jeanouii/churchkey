/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.tomitribe.churchkey.ssh;

import org.junit.Test;
import org.tomitribe.churchkey.Key;
import org.tomitribe.churchkey.Keys;
import org.tomitribe.churchkey.Resource;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.Assert.assertEquals;

public class OpenSSHPublicKeyTest {

    @Test
    public void testRSADecode1024() throws Exception {
        assertDecodeRsaPublicKey(1024, 256);
    }

    @Test
    public void testRSADecode2048() throws Exception {
        assertDecodeRsaPublicKey(2048, 256);
    }

    private void assertDecodeRsaPublicKey(final int rsaBits, final int shaBits) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        final Resource resource = Resource.resource("rsa", rsaBits, shaBits);

        final KeyFactory rsa = KeyFactory.getInstance("RSA");
        final RSAPublicKey expected = (RSAPublicKey) rsa.generatePublic(new X509EncodedKeySpec(resource.bytes("public.pkcs8.der")));

        final Key key = Keys.decode(resource.bytes("public.openssh"));
        assertEquals(Key.Algorithm.RSA, key.getAlgorithm());
        assertEquals(Key.Type.PUBLIC, key.getType());
        assertEquals(Key.Format.OPENSSH, key.getFormat());

        final RSAPublicKey actual = (RSAPublicKey) key.getKey();

        assertEquals(expected.getPublicExponent(), actual.getPublicExponent());
        assertEquals(expected.getModulus(), actual.getModulus());
    }

    @Test
    public void testDSADecode1024() throws Exception {
        assertDecodeRsaPublicKey(1024, 256);
    }

    @Test
    public void testDSADecode2048() throws Exception {
        assertDecodeRsaPublicKey(2048, 256);
    }

    private void assertDecodeDsaPublicKey(final int rsaBits, final int shaBits) throws Exception {
        final Resource resource = Resource.resource("dsa", rsaBits, shaBits);

        final KeyFactory rsa = KeyFactory.getInstance("DSA");
        final DSAPublicKey expected = (DSAPublicKey) rsa.generatePublic(new X509EncodedKeySpec(resource.bytes("public.pkcs8.der")));

        final Key key = Keys.decode(resource.bytes("public.openssh"));
        assertEquals(Key.Algorithm.DSA, key.getAlgorithm());
        assertEquals(Key.Type.PUBLIC, key.getType());
        assertEquals(Key.Format.OPENSSH, key.getFormat());

        final DSAPublicKey actual = (DSAPublicKey) key.getKey();

        assertEquals(expected.getY(), actual.getY());
        assertEquals(expected.getParams().getG(), actual.getParams().getG());
        assertEquals(expected.getParams().getQ(), actual.getParams().getQ());
        assertEquals(expected.getParams().getP(), actual.getParams().getP());
    }

    @Test
    public void testEncode1024() throws Exception {
        final Resource resource = Resource.resource("rsa", 1024, 256);

        final KeyFactory rsa = KeyFactory.getInstance("RSA");
        final RSAPublicKey rsaPublicKey = (RSAPublicKey) rsa.generatePublic(new X509EncodedKeySpec(resource.bytes("public.pkcs8.der")));

        final String expected = new String(resource.bytes("public.openssh"), "UTF-8");
        final String actual = OpenSSHParser.OpenSSH.formatSshPublicKey(rsaPublicKey, "dblevins@mingus.lan");

        assertEquals(expected, actual);
    }

}
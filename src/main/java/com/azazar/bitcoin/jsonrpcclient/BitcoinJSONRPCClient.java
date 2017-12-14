/*
 * Bitcoin-JSON-RPC-Client License
 * 
 * Copyright (c) 2013, Mikhail Yevchenko.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.azazar.bitcoin.jsonrpcclient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import com.azazar.biz.source_code.base64Coder.Base64Coder;
import com.azazar.krotjson.JSON;

/**
 *
 * @author Mikhail Yevchenko <m.ṥῥẚɱ.ѓѐḿởύḙ@azazar.com>
 */
public class BitcoinJSONRPCClient extends AbstractBitcoinClient {

    private static final Logger logger = Logger.getLogger(BitcoinJSONRPCClient.class.getCanonicalName());

    public final URL rpcURL;
    
    private URL noAuthURL;
    private String authStr;

    public BitcoinJSONRPCClient(String rpcUrl) throws MalformedURLException {
        this(new URL(rpcUrl));
    }

    public BitcoinJSONRPCClient(URL rpc) {
        this.rpcURL = rpc;
        try {
            noAuthURL = new URI(rpc.getProtocol(), null, rpc.getHost(), rpc.getPort(), rpc.getPath(), rpc.getQuery(), null).toURL();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(rpc.toString(), ex);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(rpc.toString(), ex);
        }
        authStr = rpc.getUserInfo() == null ? null : String.valueOf(Base64Coder.encode(rpc.getUserInfo().getBytes(Charset.forName("ISO8859-1"))));
    }

    public static final URL DEFAULT_JSONRPC_URL;
    public static final URL DEFAULT_JSONRPC_TESTNET_URL;
    
    static {
        String user = "user";
        String password = "pass";
        String host = "localhost";
        String port = null;

        try {
            File f;
            File home = new File(System.getProperty("user.home"));

            if ((f = new File(home, ".bitcoin" + File.separatorChar + "bitcoin.conf")).exists()) {
            } else if ((f = new File(home, "AppData" + File.separatorChar + "Roaming" + File.separatorChar + "Bitcoin" + File.separatorChar + "bitcoin.conf")).exists()) {
            } else { f = null; }
            
            if (f != null) {
                logger.fine("Bitcoin configuration file found");
                
                Properties p = new Properties();
                FileInputStream i = new FileInputStream(f);
                try {
                    p.load(i);
                } finally {
                    i.close();
                }
                
                user = p.getProperty("rpcuser", user);
                password = p.getProperty("rpcpassword", password);
                host = p.getProperty("rpcconnect", host);
                port = p.getProperty("rpcport", port);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
        try {
            DEFAULT_JSONRPC_URL = new URL("http://"+user+':'+password+"@"+host+":"+(port==null?"8332":port)+"/");
            DEFAULT_JSONRPC_TESTNET_URL = new URL("http://"+user+':'+password+"@"+host+":"+(port==null?"18332":port)+"/");
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public BitcoinJSONRPCClient(boolean testNet) {
        this(testNet ? DEFAULT_JSONRPC_TESTNET_URL : DEFAULT_JSONRPC_URL);
    }

    public BitcoinJSONRPCClient() {
        this(DEFAULT_JSONRPC_TESTNET_URL);
    }

    private HostnameVerifier hostnameVerifier = null;
    private SSLSocketFactory sslSocketFactory = null;
    private int connectTimeout = 0;

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public void setConnectTimeout(int timeout) {
        if(timeout < 0) {
            throw new IllegalArgumentException("timeout can not be negative");
        } else {
            this.connectTimeout = timeout;
        }
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public static final Charset QUERY_CHARSET = Charset.forName("ISO8859-1");

    public byte[] prepareRequest(final String method, final Object... params) {
        return JSON.stringify(new LinkedHashMap() {
            {
                put("method", method);
                put("params", params);
                put("id", "1");
            }
        }).getBytes(QUERY_CHARSET);
    }

    private static byte[] loadStream(InputStream in, boolean close) throws IOException {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for(;;) {
            int nr = in.read(buffer);

            if (nr == -1)
                break;
            if (nr == 0)
                throw new IOException("Read timed out");

            o.write(buffer, 0, nr);
        }
        return o.toByteArray();
    }

    public Object loadResponse(InputStream in, Object expectedID, boolean close) throws IOException, BitcoinException {
        try {
            String r = new String(loadStream(in, close), QUERY_CHARSET);
            logger.log(Level.FINE, "Bitcoin JSON-RPC response:\n{0}", r);
            try {
                Map response = (Map) JSON.parse(r);
                
                if (!expectedID.equals(response.get("id")))
                    throw new BitcoinRPCException("Wrong response ID (expected: "+String.valueOf(expectedID) + ", response: "+response.get("id")+")");

                if (response.get("error") != null)
                    throw new BitcoinException(JSON.stringify(response.get("error")));

                return response.get("result");
            } catch (ClassCastException ex) {
                throw new BitcoinRPCException("Invalid server response format (data: \"" + r + "\")");
            }
        } finally {
            if (close)
                in.close();
        }
    }

    public Object query(String method, Object... o) throws BitcoinException {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) noAuthURL.openConnection();

            if (connectTimeout != 0)
                conn.setConnectTimeout(connectTimeout);

            conn.setDoOutput(true);
            conn.setDoInput(true);

            if (conn instanceof HttpsURLConnection) {
                if (hostnameVerifier != null)
                    ((HttpsURLConnection)conn).setHostnameVerifier(hostnameVerifier);
                if (sslSocketFactory != null)
                    ((HttpsURLConnection)conn).setSSLSocketFactory(sslSocketFactory);
            }

//            conn.connect();

            ((HttpURLConnection)conn).setRequestProperty("Authorization", "Basic " + authStr);
            byte[] r = prepareRequest(method, o);
            logger.log(Level.FINE, "Bitcoin JSON-RPC request:\n{0}", new String(r, QUERY_CHARSET));
            conn.getOutputStream().write(r);
            conn.getOutputStream().close();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200)
                throw new BitcoinRPCException("RPC Query Failed (method: "+ method +", params: " + Arrays.deepToString(o) + ", response header: "+ responseCode + " " + conn.getResponseMessage() + ", response: " + new String(loadStream(conn.getErrorStream(), true)));
            return loadResponse(conn.getInputStream(), "1", true);
        } catch (IOException ex) {
            throw new BitcoinRPCException("RPC Query Failed (method: "+ method +", params: " + Arrays.deepToString(o) + ")", ex);
        }
    }

}

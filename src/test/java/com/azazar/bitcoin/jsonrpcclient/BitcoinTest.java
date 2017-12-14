package com.azazar.bitcoin.jsonrpcclient;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BitcoinTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws MalformedURLException, BitcoinException {
		BitcoinJSONRPCClient client = new BitcoinJSONRPCClient(new URL("http://admin1:123@192.168.1.246:19001"));
		assertTrue(client.getBalance() >= 0);
	}

}

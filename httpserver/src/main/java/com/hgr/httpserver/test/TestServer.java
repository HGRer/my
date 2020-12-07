package com.hgr.httpserver.test;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class TestServer {
	public static void main(String[] args) {
		try {
			HttpServer hs = HttpServer.create(new InetSocketAddress(9001), 0);
			hs.createContext("/test", new TestHander());
			hs.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class TestHander implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			exchange.sendResponseHeaders(200, 0);
			exchange.getResponseBody().write("this is text message".getBytes("GBK"));
			exchange.getResponseBody().close();
			System.out.println("TestHander end");
		}
	}
}

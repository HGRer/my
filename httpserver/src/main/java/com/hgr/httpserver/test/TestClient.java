package com.hgr.httpserver.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {
	public static void main(String[] args) {
		System.out.println("TestClient running");
		try (Socket clientSocket = new Socket("127.0.0.1", 9001);) {
			OutputStream out = clientSocket.getOutputStream();
			PrintWriter pw = new PrintWriter(out);
			pw.write("This is clientSocket");
			pw.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

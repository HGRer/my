package com.hgr.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(9001);) {
			try (Socket socket = serverSocket.accept();) {
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

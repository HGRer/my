package com.hgr.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import com.hgr.httpserver.constant.HttpUtil;

public class HttpServer {
	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(9001);) {
			try (Socket socket = serverSocket.accept();) {
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				
				HttpUtil.parseRequestStream2(in);
				
				try (PrintWriter pw = new PrintWriter(out)) {
					pw.write("Hello!");
					pw.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("HttpServer close");
	}
}

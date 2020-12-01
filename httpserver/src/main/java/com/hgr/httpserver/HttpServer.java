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
//			while(true) {
				Socket socket = serverSocket.accept();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							InputStream in = socket.getInputStream();
							OutputStream out = socket.getOutputStream();
							HttpUtil.parseRequestStream(in);
							try (PrintWriter pw = new PrintWriter(out)) {
								pw.write("Hello!");
								pw.flush();
							}
						} catch(IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("HttpServer close");
	}
}

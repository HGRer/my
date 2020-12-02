package com.hgr.httpserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.hgr.httpserver.constant.HttpUtil;

public class HttpServer {
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(9001);
		while (true) {
			Socket socket = serverSocket.accept();
			new Thread(new Runnable() {
				public void run() {
					try (InputStream in = socket.getInputStream();
							OutputStream out = socket.getOutputStream();) {
						HttpUtil.parseRequestStream(in);
						
						PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
						byte[] temp = "HTTP/1.1 200 OK\r\n".getBytes();
						byte[] byteBuffer = new byte[temp.length + 2];
						for (int i = 0; i < temp.length; i++) {
							byteBuffer[i] = temp[i];
						}
						byteBuffer[byteBuffer.length - 2] = 13;
						byteBuffer[byteBuffer.length - 1] = 10;
						pw.println(new String(byteBuffer));
					} catch (Exception e) {
						printError(e);
					}
				}
			}).start();
		}
	}

	public synchronized static void printError(Exception e) {
		System.out.println(Thread.currentThread().getName() + "--------------------");
		e.printStackTrace();
	}
}

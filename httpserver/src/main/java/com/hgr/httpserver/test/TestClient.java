package com.hgr.httpserver.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class TestClient {
	public static void main(String[] args) throws Exception {
		File file = new File(Paths.get(System.getProperty("user.dir")).toString()
				, "\\src\\main\\java\\com\\hgr\\httpserver\\test\\request.txt");
		try (Socket clientSocket = new Socket("127.0.0.1", 9001);
				FileInputStream fileIn = new FileInputStream(file);) {
			OutputStream out = clientSocket.getOutputStream();
			while(fileIn.available() > 0) {
				out.write(fileIn.read());
			}
			out.flush();
			
			BufferedInputStream bin = new BufferedInputStream(clientSocket.getInputStream());
			byte[] buffer = new byte[2048];
			if (bin.available() > 0) {
				bin.read(buffer);
				System.out.println(new String(buffer));
			}
//			PrintWriter pw = new PrintWriter(out);
//			pw.write("This is clientSocket");
//			pw.flush();
		}
	}
}

package com.hgr.httpserver.constant;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class HttpUtil {
	private static Map<String, Object> requestMap = new HashMap<>();
	
	/**
	 *  解析Request字节流.
	 * <p>当前方法仅仅适合与HTTP1.1版本，因为该版本的客户端对Request格式有严格的标准。
	 * @param in
	 * @throws IOException
	 */
	public static Map<String, Object> parseRequestStream(InputStream in) throws IOException {
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(in);) {
			// 解析request-line
			int request_line_crlf_index = -1;
			boolean request_line_find = false;
			bufferedInputStream.mark(0);
			while (bufferedInputStream.available() > 0) {
				request_line_crlf_index++;
				if (bufferedInputStream.read() == 13) {
					if (bufferedInputStream.available() > 0) {
						request_line_crlf_index++;
						if (bufferedInputStream.read() == 10) {
							request_line_find = true;
							break;
						}
					}
				}
			}
			
			if (request_line_find) {
				// 解析header
				int header_crlf_index = -1;
				if (bufferedInputStream.available() > 0) {
					header_crlf_index = parseHeader(request_line_crlf_index, bufferedInputStream);
				}
				
				// 将requestLine值解析放入到requestMap
				bufferedInputStream.reset();
				byte[] byteArray = new byte[request_line_crlf_index + 1];
				bufferedInputStream.read(byteArray);
				String requestLine = new String(byteArray);
				String[] requestLineArray = requestLine.split(" ");
				if (requestLineArray.length != 3) {
					throw new IOException("HttpRequest_requestLine format error/request_line格式错误");
				}
				requestMap.put("REQUEST_METHOD", requestLineArray[0]);
				requestMap.put("REQUEST_URI", requestLineArray[1]);
				requestMap.put("REQUEST_HTTP_Version", requestLineArray[2]);
				System.out.println(requestMap);
				
				if (header_crlf_index != -1) {
					byte[] headerBuffer = new byte[header_crlf_index - request_line_crlf_index];
					bufferedInputStream.read(headerBuffer);
					String header = new String(headerBuffer);
					System.out.println(header);
				}
				
				// 根据Request Method 决定是否需要解析body
				if (bufferedInputStream.available() > 0 && !"GET".equals(requestMap.get("REQUEST_METHOD"))) {
					
				}
			} else {
				throw new IOException("HttpRequest format error/没有找到request_line");
			}
			
			bufferedInputStream.reset();
			byte[] byteArray = new byte[1024];
			bufferedInputStream.read(byteArray);
			System.out.println();
			System.out.println("Total: ");
			System.out.println(new String(byteArray));
		}
		
		return requestMap;
	}
	
	private static int parseHeader(int request_line_crlf_index, BufferedInputStream bufferedInputStream) throws IOException {
		// 解析headers
		int header_crlf_index = request_line_crlf_index;
		
		boolean header_find = false;
		while (bufferedInputStream.available() > 0) {
			header_crlf_index++;
			if (bufferedInputStream.read() == 13) {
				if (bufferedInputStream.available() > 0) {
					header_crlf_index++;
					if (bufferedInputStream.read() == 10) {
						if (bufferedInputStream.available() > 0) {
							header_crlf_index++;
							if (bufferedInputStream.read() == 13) {
								if (bufferedInputStream.available() > 0) {
									header_crlf_index++;
									if (bufferedInputStream.read() == 10) {
										header_find = true;
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		
		if (header_find) {
			return header_crlf_index;
		}
		
		return -1;
	}
}

package com.hgr.httpserver.constant;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class HttpUtil {
	// Request，CRLF的二进制是1310
	// start-line+CRLF
	// headers+CRLF
	// CRLF
	// body
	/**
	 * 解析Request字节流.
	 * <p>当前方法仅仅适合与HTTP1.1版本，因为该版本的客户端对Request格式有严格的标准。
	 * @throws IOException 
	 * @throws Exception
	 */
	public static void parseRequestStream(InputStream in) throws IOException {
		// 先接收完再解析
		long start = System.currentTimeMillis();
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(in);) {
			byte[] buffer1 = new byte[1024];
			int read_result = bufferedInputStream.read(buffer1);
			int buffer_map_key = 1;
			Map<Integer, byte[]> buffer_map = new HashMap<>();
			buffer_map.put(buffer_map_key, buffer1);

			while (read_result == 1024 && bufferedInputStream.available() > 0) {
				// 还有数据要读取
				byte[] temp = new byte[1024];
				read_result = bufferedInputStream.read(temp);
				buffer_map.put(buffer_map_key++, temp);
			}

			// start-line标准： Method SP Request-URI SP HTTP-Version CRLF
			buffer_map_key = 1;
			int start_line_index = -1;
			while (buffer_map.get(buffer_map_key) != null && start_line_index == -1) {
				byte[] temp_buffer = buffer_map.get(buffer_map_key);
				int temp_index = 0;
				while (temp_buffer.length > (temp_index + 1)) {
					// 以两个字节为一组找到CRLF，即找到start-line
					if (temp_buffer[temp_index] == 13 && temp_buffer[temp_index + 1] == 10) {
						start_line_index = temp_index;
						break;
					} else {
						temp_index++;
					}
				}
				
				if (start_line_index == -1) {
					buffer_map_key++; // 从下一个字节数组找CRLF
				} else {
					break;
				}
			}
			
			System.out.println("buffer_map_index: " + buffer_map_key + ", start_line_index: " + start_line_index);
			
			if (start_line_index != -1) {
				System.out.println(new String(buffer_map.get(buffer_map_key), 0, start_line_index));
			}
		}
		System.out.println("cost:" + (System.currentTimeMillis() - start));
	}
	
	/**
	 *  解析Request字节流.
	 * <p>当前方法仅仅适合与HTTP1.1版本，因为该版本的客户端对Request格式有严格的标准。
	 * @param in
	 * @throws IOException
	 */
	public static void parseRequestStream2(InputStream in) throws IOException {
		// 先接收完再解析
		long start = System.currentTimeMillis();
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(in);) {
			// 解析start-line
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
			
			int header_crlf_index = request_line_crlf_index;
			System.out.println("begin of header index:" + header_crlf_index);
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
			System.out.println("header_crlf_index: " + header_crlf_index);
			
			// body
			if (bufferedInputStream.available() > 0) {
				
			} else {
				
			}
			
			bufferedInputStream.reset();
			System.out.println();
			if (request_line_find) {
				byte[] byteArray = new byte[request_line_crlf_index + 1];
				bufferedInputStream.read(byteArray);
				System.out.println("request_line is: " + new String(byteArray));
			}
			
			if (header_find) {
				byte[] byteArray = new byte[header_crlf_index - request_line_crlf_index];
				bufferedInputStream.read(byteArray);
				System.out.println("header_line is:");
				System.out.print(new String(byteArray));
			} else {
				System.out.println("not find header_line");
			}
			
			bufferedInputStream.reset();
			byte[] byteArray = new byte[1024];
			bufferedInputStream.read(byteArray);
			System.out.println();
			System.out.println("Total: ");
			System.out.println(new String(byteArray));
		}
		System.out.println("cost:" + (System.currentTimeMillis() - start));
	}
}

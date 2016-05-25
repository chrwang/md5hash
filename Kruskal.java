package net.mrpaul.aoa.px180.ps02;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;

public class Kruskal {
	
	public static void main(String[] args) throws IOException {
		BufferedInputStream in = new BufferedInputStream(System.in);
		
		int numBytesRead = 0;
		byte[] block = new byte[8];
		while ((numBytesRead = in.read(block)) == 8) {
			System.out.println(Arrays.toString(block));
			
			// Process the block here
		}
		
		// TODO: Handle padding
	}
}

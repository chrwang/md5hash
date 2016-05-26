package hashbrown5;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;

public class MD5 {
	
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
	
	private byte[] f(byte[] b, byte[] c, byte[] d){
		if(b.length !=4 || c.length != 4 || d.length != 4){
			throw new IllegalArgumentException(":'(  -- f");
		}
		
		
		byte[] output = new byte[4];
		for(int i = 0; i < 4; i++){
			output[i] = (byte) ((b[i]&c[i]) | ((~b[i])&d[i]));
		}
		
		return output;
	}
	
	private byte[] g(byte[] b, byte[] c, byte[] d){
		if(b.length !=4 || c.length != 4 || d.length != 4){
			throw new IllegalArgumentException(":'(  -- h");
		}
		
		byte[] output = new byte[4];
		for(int i = 0; i < 4; i++){
			output[i] = (byte) ((b[i]&d[i]) | (c[i]&(~d[i])));
		}
		
		return output;
		
	}
	
	private byte[] h(byte[] b, byte[] c, byte[] d){
		if(b.length !=4 || c.length != 4 || d.length != 4){
			throw new IllegalArgumentException(":'(  -- g");
		}
		
		byte[] output = new byte[4];
		for(int i = 0; i < 4; i++){
			output[i] = (byte) (b[i]^c[i]^d[i]);
		}
		
		return output;
			
	}
	
	private byte[] i(byte[] b, byte[] c, byte[] d){
		if(b.length !=4 || c.length != 4 || d.length != 4){
			throw new IllegalArgumentException(":'(  -- i");
		}
		
		byte[] output = new byte[4];
		for(int i = 0; i < 4; i++){
			output[i] = (byte) (c[i] ^ (b[i] | (~d[i])));
		}
		
		return output;
	}
}
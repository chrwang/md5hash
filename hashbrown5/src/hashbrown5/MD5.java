package hashbrown5;

/**
 * A class which implements the RFC 1321 definition of the Message Digest 5 Hash Algorithm.
 *
 * @author Eric Shen, Arman Siddique, Chris Wang
 * @version 1.0
 */
public class MD5 {
    //Some definitions of variables
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();//hex char table

    private static final int[] s = { //Shift constants. Defined in RFC 1321.
            7, 12, 17, 22,
            5, 9, 14, 20,
            4, 11, 16, 23,
            6, 10, 15, 21
    };

    //Initial values for dwords A, B, C, and D. Defined as such by RFC 1321.
    private static final int INIT_A = 0x67452301;
    private static final int INIT_B = (int) 0xEFCDAB89L;
    private static final int INIT_C = (int) 0x98BADCFEL;
    private static final int INIT_D = 0x10325476;

    public static int[] K = new int[64]; //Array of constants used during the rounds of MD5.

    static {//calculate said constants
        for (int i = 0; i < 64; i++) {
            K[i] = (int) (long) ((1L << 32)/* This is 2^32*/ * Math.abs(Math.sin(i + 1))); //Also defined to be such in the RFC.
        }
    }

    /**
     * Main method. Can call MD5 method compute().
     *
     * @param args
     *         command line args, ignored
     */
    public static void main(String[] args) {
        String input = "";//Input here
        System.out.println(bytesToHex(compute(input.getBytes())));
    }

    /**
     * Converts a byte array into a String with the hex characters that the bytes represent.
     *
     * @param bytes
     *         the byte array to turn into hex
     * @return the resulting hex string
     * @author maybeWeCouldStealAVan (via StackOverflow)
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Nonlinear function f which performs bitwise operations on inputs.
     *
     * @param b
     *         dword b in MD5
     * @param c
     *         dword c in MD5
     * @param d
     *         dword d in MD5
     * @return the result of applying the function
     */
    private static int f(int b, int c, int d) {
        return (b & c) | ((~b) & d);
    }

    /**
     * Nonlinear function g which performs bitwise operations on inputs.
     *
     * @param b
     *         dword b in MD5
     * @param c
     *         dword c in MD5
     * @param d
     *         dword d in MD5
     * @return the result of applying the function
     */
    private static int g(int b, int c, int d) {
        return (b & d) | (c & (~d));
    }

    /**
     * Nonlinear function h which performs bitwise operations on inputs.
     *
     * @param b
     *         dword b in MD5
     * @param c
     *         dword c in MD5
     * @param d
     *         dword d in MD5
     * @return the result of applying the function
     */
    private static int h(int b, int c, int d) {
        return b ^ c ^ d;
    }

    /**
     * Nonlinear function i which performs bitwise operations on inputs.
     *
     * @param b
     *         dword b in MD5
     * @param c
     *         dword c in MD5
     * @param d
     *         dword d in MD5
     * @return the result of applying the function
     */
    private static int i(int b, int c, int d) {
        return c ^ (b | (~d));
    }

    /**
     * Computes the MD5 hash of the given message.
     *
     * @param message
     *         the message to find the hash of
     * @return a byte array containing the resulting dwords
     */
    private static byte[] compute(byte[] message) {
        int lenBytes = message.length;
        //Add 8 to account for necessary padding, divides by 64 since each block is 64 bytes, then adds 1 because the minimum possible number of blocks is 1.
        int numBlocks = ((lenBytes + 8) >>> 6) + 1;
        //finds the size that the message should be in bytes after padding.
        int lenAfterPad = numBlocks << 6;
        //Initialises an array which represents the bits to be padded. The length is the byte length of the padded message - the byte length of the original message.
        byte[] pad = new byte[lenAfterPad - lenBytes];
        /*
        Padding scheme is as follows:
        1. Append a single "1" bit.
        2. Append zeroes until the length of the message modulo 512 is 448.
        3. Append the original length of the message in bits, modulo 2^64.
         */
        pad[0] = (byte) 0x80;//10000000 in binary, accomplishes step 1 and begins step 2. The rest of step two is automatically accomplished as Java default initialises bytes as
        // zeroes.
        //Calculates the length of the message in bits. Uses longs since the Java long is 64 bits, and we need 64 bits per the specification.
        long lenBits = (long) lenBytes << 3;

        //Appends the length of the message(modulo 2 pow 64). This is accomplished by replacing the zero bytes at the end of the pad array with the length of the message.
        for (int i = 0; i < 8; i++) {
            pad[pad.length - 8 + i] = (byte) lenBits;
            lenBits >>>= 8; //shifts eight bits over to get the next byte.
        }

        //A buffer to hold the 32 bit dwords which are to be processed.
        int[] buffer = new int[16];

        //Initialise internal dwords.
        int a = INIT_A;
        int b = INIT_B;
        int c = INIT_C;
        int d = INIT_D;

        //Process all blocks.
        for (int i = 0; i < numBlocks; i++) {
            int ind = i << 6; //Converts the current block(i) to a byte offset. This byte offset is how many bytes of the message have already been processed.
            //Parses the 512 bit block into 16 32-bit dwords, which are placed into the buffer as ints.
            for (int j = 0; j < 64; j++, ind++) {
                buffer[j / 4] = (int) ((ind < lenBytes) ? message[ind] : pad[ind - lenBytes]) << 24/*Shift to make room for data before pad*/ | (buffer[j >>> 2] >>> 8 /*Or with
                old data */);
                //TODO Explain this line
            }

            //***Rounds to process dwords***
            for (int j = 0; j < 64; j++) {
                int t = j / 16;
                int f = 0; //result of nonlinear function
                int g = 0; //which message dword to use

                switch (t) {
                    case 0:
                        f = f(b, c, d); //Applies the nonlinear function f
                        g = j; //Chooses the message block to use
                        break;
                    case 1:
                        f = g(b, c, d); //Applies the nonlinear function g
                        g = (5 * j + 1) % 16; //Chooses the message block to use
                        break;
                    case 2:
                        f = h(b, c, d); //Applies the nonlinear function h
                        g = (3 * j + 5) % 16; //Chooses the message block to use
                        break;
                    case 3:
                        f = i(b, c, d); //Applies the nonlinear function i
                        g = (7 * j) % 16; //Chooses the message block to use
                        break;
                }

                int temp = d;
                d = c;
                c = b;
                b = b + Integer.rotateLeft((a + f + K[j] + buffer[g]), s[t << 2 | (j & 3)]);
                /*The magical bit shifting produces this sequence:
                0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3,
                4, 5, 6, 7, 4, 5, 6, 7, 4, 5, 6, 7, 4, 5, 6, 7,
                8, 9, 10, 11, 8, 9, 10, 11, 8, 9, 10, 11, 8, 9, 10, 11,
                12, 13, 14, 15, 12, 13, 14, 15, 12, 13, 14, 15, 12, 13, 14, 15,
                which is the index used for the per round shift array.
                */
                a = temp;
            }

            //Add original constants back in.
            a += INIT_A;
            b += INIT_B;
            c += INIT_C;
            d += INIT_D;
        }

        //Process to form output.
        byte[] result = new byte[16];
        int ind = 0;

        for (int i = 0; i < 4; i++) {
            int n = (i == 0) ? a : ((i == 1) ? b : (i == 2) ? c : d); //goes through all four dwords
            for (int j = 0; j < 4; j++) {
                result[ind++] = (byte) (n); //truncates first 24 bits of n, leaves last 8 bits
                n >>>= 8; //shift over 8 to access next 8 bits
                //TODO Draw example on board
            }
        }
        return result;
    }
}
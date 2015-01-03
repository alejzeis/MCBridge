package mcbridge;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte flag = (byte) (0 | 2 << 5);
		System.out.println((byte) ((flag & 0xFF) | 0x10));

	}

}

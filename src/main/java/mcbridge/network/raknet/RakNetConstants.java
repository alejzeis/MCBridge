package mcbridge.network.raknet;

public class RakNetConstants {
	public final static byte RAKNET_PROTOCOL_VERSION = 5;
	public final static byte RAKNET_BROADCAST_PING_1                =       0x01;
	public final static byte RAKNET_BROADCAST_PING_2                =       0x02;
	public final static byte RAKNET_OPEN_CONNECTION_REQUEST_1       =       0x05;
	public final static byte RAKNET_OPEN_CONNECTION_REPLY_1         =       0x06;
	public final static byte RAKNET_OPEN_CONNECTION_REQUEST_2       =       0x07;
	public final static byte RAKNET_OPEN_CONNECTION_REPLY_2         =       0x08;
	public final static byte RAKNET_INCOMPATIBLE_PROTOCOL_VERSION   =       0x1A;
	public final static byte RAKNET_BROADCAST_PONG_1                =       0x1C;
	public final static byte RAKNET_BROADCAST_PONG_2                =       0x1D;
	public final static byte RAKNET_CUSTOM_PACKET_MIN               = (byte)0x80;
	public final static byte RAKNET_CUSTOM_PACKET_MAX               = (byte)0x8F;
	public final static byte RAKNET_CUSTOM_PACKET_DEFAULT           = (byte)0x84;
	public final static byte RAKNET_NACK                            = (byte)0xA0;
	public final static byte RAKNET_ACK                             = (byte)0xC0;
	public final static byte[] RAKNET_HAS_MESSAGE_RELIABILITIES = {
		0x02, 0x03, 0x04, 0x06, 0x07
	};
	public final static byte[] RAKNET_HAS_ORDER_RELIABILITIES = {
		0x01, 0x03, 0x04, 0x07
	};
	
	public static final byte DATA_PACKET_0 = (byte)0x80;
	public static final byte DATA_PACKET_1 = (byte)0x81;
	public static final byte DATA_PACKET_2 = (byte)0x82;
	public static final byte DATA_PACKET_3 = (byte)0x83;
	public static final byte DATA_PACKET_4 = (byte)0x84;
	public static final byte DATA_PACKET_5 = (byte)0x85;
	public static final byte DATA_PACKET_6 = (byte)0x86;
	public static final byte DATA_PACKET_7 = (byte)0x87;
	public static final byte DATA_PACKET_8 = (byte)0x88;
	public static final byte DATA_PACKET_9 = (byte)0x89;
	public static final byte DATA_PACKET_A = (byte)0x8a;
	public static final byte DATA_PACKET_B = (byte)0x8b;
	public static final byte DATA_PACKET_C = (byte)0x8c;
	public static final byte DATA_PACKET_D = (byte)0x8d;
	public static final byte DATA_PACKET_E = (byte)0x8e;
	public static final byte DATA_PACKET_F = (byte)0x8f;

}

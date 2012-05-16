package common;

/* This class will be used by all classes to parse the incoming message or to create the outgoing messages
 * 
 */
public class MessageParser {
	
	/*
	 * This is the different type of indicators
	 */
	public enum TypeIndicator{
		VERSION(1), GAME(2), CLOSE_CONNECTION(3), CHALLENGE_CONNECTION(4);
		private int indicator;
		TypeIndicator(int indicator)
		{
			this.setIndicator(indicator);
		}
		public int getIndicator() {
			return indicator;
		}
		public void setIndicator(int indicator) {
			this.indicator = indicator;
		}
	}
	
	public enum VersionIndicator{
		CLIENT_VERSION(1), VERSION_REQUIREMENT(2), VERSION_ACK(3), VERSION_UPGRADE(4);
		private int indicator;
		VersionIndicator(int indicator)
		{
			this.setIndicator(indicator);
		}
		public int getIndicator() {
			return indicator;
		}
		public void setIndicator(int indicator) {
			this.indicator = indicator;
		}
	}
	
	public int GetVersion(byte[] buffer, int iSize)
	{
		int version = -1;
		if (iSize >= 2)
		{
			version = (((buffer[0] << 8) | buffer[1]) & 0xFFFF );
		}
		return version;
	}
	
	public int GetTypeIndicator(byte[] buffer, int iSize)
	{
		int Indicator = -1;
		if (iSize >= 4)
		{
			Indicator = (((buffer[2] << 8) | buffer[3]) & 0xFFFF);
		}
		return Indicator;
	}
	
	/* 
	 * These next couple of messages are version specific messages
	 * Therefore the length of the message is checked for accuracy
	 */
	public short GetVersionType(byte[] buffer, int iSize)
	{
		short type = -1;
		if (iSize == 12)
		{
			type = (short)(buffer[4] & 0xFF);
		}
		return type;
	}
	
	public short GetMinorVersion(byte[] buffer, int iSize)
	{
		short version = -1;
		if (iSize == 12)
		{
			version = (short)(buffer[5] & 0xFF);
		}
		return version;
	}
	
	public long GetVersionBankAmount(byte[] buffer, int iSize)
	{
		long amount = -1;
		if (iSize == 12)
		{
			short byte1 = (short)(buffer[8] & 0xFF);
			short byte2 = (short)(buffer[9] & 0xFF);
			short byte3 = (short)(buffer[10] & 0xFF);
			short byte4 = (short)(buffer[11] & 0xFF);
			amount = (long)(((byte1 << 24) |
							(byte2 << 16) |
							(byte3 << 8) |
							byte4) & 0xFFFFFFFF);
		}
		return amount;
	}
	
	/* This function will create the version message */
	public byte[] CreateVersionMessage(int iVersion, int iType, short nVersionType, short nMinorVersion, long lBankAmount)
	{
		byte[] buffer = new byte[12];
		buffer[0] = (byte)((iVersion & 0xFF00) >> 8);
		buffer[1] = (byte)(iVersion & 0xFF);
		buffer[2] = (byte)((iType & 0xFF00) >> 8);
		buffer[3] = (byte)(iType & 0xFF);
		buffer[4] = (byte)(nVersionType & 0xFF);
		buffer[5] = (byte)(nMinorVersion & 0xFF);
		buffer[6] = 0;
		buffer[7] = 0;
		buffer[8] = (byte)((lBankAmount & 0xFF000000) >> 24);
		buffer[9] = (byte)((lBankAmount & 0xFF0000) >> 16);
		buffer[10] = (byte)((lBankAmount & 0xFF00) >> 8);
		buffer[11] = (byte)(lBankAmount & 0xFF);
		return buffer;
	}
	
	/* These next messages are for the game messages */

}

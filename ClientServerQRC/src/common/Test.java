package common;

import common.MessageParser.GameIndicator;
import common.MessageParser.GameTypeCode;
import common.MessageParser.GameTypeResponse;
import common.MessageParser.ServerSetGameMessage;
import common.MessageParser.TypeIndicator;

public class Test {
 
    public static void main(String args[]){
    	MessageParser messageParser=new MessageParser();
    	
    	MessageParser.ServerSetGameMessage svrMsg = messageParser.new ServerSetGameMessage(1, TypeIndicator.SET, GameIndicator.SET_GAME, GameTypeCode.TEXAS_HOLDEM, GameTypeResponse.ACK);
//    	System.out.println(svrMsg.getTypeCode());
//    	System.out.println(svrMsg.getTypeCode().getIndicator());
//    	System.out.println(svrMsg.getGameIndicator());
//		System.out.println(svrMsg.getGameTypeCode());
//		System.out.println(svrMsg.getGameTypeResponse());
		
    	byte[] outputBuffer=messageParser.CreateServerSetGameMessage(svrMsg);
    	for(int i=0;i<outputBuffer.length;i++)
    	System.out.println(outputBuffer[i]);
    	
    	System.out.println(messageParser.GetTypeIndicator(outputBuffer, outputBuffer.length));
		MessageParser.ServerSetGameMessage cltMsg = messageParser.GetServerSetGameMessage(outputBuffer, outputBuffer.length);
		System.out.println(cltMsg.getGameIndicator().getIndicator());
		System.out.println(cltMsg.getGameTypeCode().getGameTypeCode());
		System.out.println(cltMsg.getGameTypeResponse().getGameTypeResponse());
		System.out.println(cltMsg.getVersion());
		byte buffer2=0;
		byte buffer3=3;
		System.out.println((int)(((buffer2 << 8) | buffer3) & 0xFFFF));
    }
}

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
    	System.out.println(svrMsg.getGameIndicator());
		System.out.println(svrMsg.getGameTypeCode());
		System.out.println(svrMsg.getGameTypeResponse());
		
    	byte[] outputBuffer=messageParser.CreateServerSetGameMessage(svrMsg);
		MessageParser.ServerSetGameMessage cltMsg = messageParser.GetServerSetGameMessage(outputBuffer, outputBuffer.length);
		System.out.println(cltMsg.getGameIndicator());
		System.out.println(cltMsg.getGameTypeCode());
		System.out.println(cltMsg.getGameTypeResponse());
    }
}

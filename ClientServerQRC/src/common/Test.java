package common;

import common.MessageParser.ServerSetGameMessage;

public class Test {
 
    public static void main(String args[]){
    	MessageParser messageParser=new MessageParser();
    	
    	MessageParser.ServerSetGameMessage svrMsg = messageParser.new ServerSetGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_SET_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_TYPE_RESPONSE_ACK);
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

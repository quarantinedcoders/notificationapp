package com.notification.api.broadcast;

import org.springframework.stereotype.Component;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Component
public class TwilioWhatsAppService implements BroadCastServiceI {

	public static final String ACCOUNT_SID = "AC0cbc38ff9827db4186c394f25efee523";
	public static final String AUTH_TOKEN = "6c4450886553d20760273aac79c9ba55";

	@Override
	public void send(String messageToSend) {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		Message.creator(new com.twilio.type.PhoneNumber("whatsapp:+918652583163"),
				new com.twilio.type.PhoneNumber("whatsapp:+14155238886"), messageToSend).create();
	}
}

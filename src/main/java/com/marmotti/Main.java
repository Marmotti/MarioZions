package com.marmotti;

import static spark.Spark.*;
import com.clivern.racter.BotPlatform;
import com.clivern.racter.receivers.webhook.*;

import com.clivern.racter.senders.*;
import com.clivern.racter.senders.templates.*;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class Main {

    public static void main(String[] args)throws IOException{

        //Veryfying Token Route
        get("/", (request, response) -> {
            BotPlatform platform = new BotPlatform("src/main/resources/config.properties");
            platform.getVerifyWebhook().setHubMode((request.queryParams("hub.mode") != null) ? request.queryParams("hub.mode") : "");
            platform.getVerifyWebhook().setHubVerifyToken((request.queryParams("hub.verify_token") != null) ? request.queryParams("hub.verify_token") : "");
            platform.getVerifyWebhook().setHubChallenge((request.queryParams("hub.challenge") != null) ? request.queryParams("hub.challenge") : "");

            if(platform.getVerifyWebhook().challenge()){
                platform.finish();
                response.status(200);
                return (request.queryParams("hub.challenge") != null) ? request.queryParams("hub.challenge") : "";
            }

            platform.finish();
            response.status(403);
            return "Verification token mismatch";
        });

        post("/", (request, response) -> {
            String body = request.body();
            BotPlatform platform = new BotPlatform("src/main/resources/config.properties");
            platform.getBaseReceiver().set(body).parse();
            HashMap<String, MessageReceivedWebhook> messages = (HashMap<String, MessageReceivedWebhook>) platform.getBaseReceiver().getMessages();
            for (MessageReceivedWebhook message : messages.values()){

                String user_id = (message.hasUserId()) ? message.getUserId() : "";
                String page_id = (message.hasPageId()) ? message.getPageId() : "";
                String message_id = (message.hasMessageId()) ? message.getMessageId() : "";
                String message_text = (message.hasMessageText()) ? message.getMessageText() : "";
                String quick_reply_payload = (message.hasQuickReplyPayload()) ? message.getQuickReplyPayload() : "";
                Long timestamp = (message.hasTimestamp()) ? message.getTimestamp() : 0;
                HashMap<String, String> attachments = (message.hasAttachment()) ? (HashMap<String, String>) message.getAttachment() : new HashMap<String, String>();

                platform.getLogger().info("User ID#:" + user_id);
                platform.getLogger().info("Page ID#:" + page_id);
                platform.getLogger().info("Message ID#:" + message_id);
                platform.getLogger().info("Message Text#:" + message_text);
                platform.getLogger().info("Quick Reply Payload#:" + quick_reply_payload);

                for (String attachment : attachments.values()){
                    platform.getLogger().info("Attachment#: " + attachment);
                }

                String text = message.getMessageText();
                MessageTemplate message_tpl = platform.getBaseSender().getMessageTemplate();

                if(text.equals("Dzień dobry")){
                    message_tpl.setRecipientId(message.getUserId());
                    message_tpl.setMessageText("Dzień dobry, nazywam się Mario Zions");
                    message_tpl.setNotificationType("REGULAR");
                    platform.getBaseSender().send(message_tpl);
                }
                return "ok";
            }
            return "egeheyeeghy";
        });
    }
}

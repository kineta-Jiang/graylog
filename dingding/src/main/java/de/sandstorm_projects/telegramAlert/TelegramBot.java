package de.sandstorm_projects.telegramAlert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.configuration.Configuration;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import de.sandstorm_projects.telegramAlert.config.Config;

class TelegramBot {
    private static final String API = "https://api.telegram.org/bot%s/%s";

    private String token;
    private String chat;
    private Logger logger;
    private String parseMode;
    private String proxy;

    TelegramBot(Configuration config) {
        this.token = config.getString(Config.TOKEN);
        this.chat = config.getString(Config.CHAT);
        this.parseMode = config.getString(Config.PARSE_MODE);
        this.proxy = config.getString(Config.PROXY);

        logger = Logger.getLogger("TelegramAlert");
    }

    void sendMessage(String msg) throws AlarmCallbackException {
        final CloseableHttpClient client;


        client = HttpClients.createDefault();
        logger.warning(msg);

        HttpPost request = new HttpPost(token);
        request.addHeader("Content-Type", "application/json; charset=utf-8");
        String msgtype=parseMode;
        if (parseMode.equals("Markdown"))
            msgtype="markdown";
        String str="";
        str+="{\"msgtype\":\""+msgtype+"\",";
        if(parseMode.equals("text")){
            str+="\"text\":{\"content\":\""+msg+"\"}}";
        }else{
            str+="\"markdown\":{\"title\":\"markdown format\",\"text\":\""+msg+"\"}}";
        }
String textMsg = "{ \"msgtype\": \"text\", \"text\": {\"content\": \"this is a test\"}}";
        StringEntity se = new StringEntity(str, "utf-8");
        logger.warning(str);
        try {
            request.setEntity(se);

            HttpResponse response = client.execute(request);
            int status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                String body = new BasicResponseHandler().handleResponse(response);
                String error = String.format("API request was unsuccessful (%d): %s", status, body);
                logger.warning(error);
                throw new AlarmCallbackException(error);
            }
        } catch (IOException e) {
            String error = "API request failed: " + e.getMessage();
            logger.warning(error);
            e.printStackTrace();
            throw new AlarmCallbackException(error);
        }
    }
}
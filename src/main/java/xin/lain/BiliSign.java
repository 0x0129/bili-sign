package xin.lain;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BiliSign {


    public static void main(String[] args) {
        Config.config();

        DefaultBotOptions botOptions = new DefaultBotOptions();
        // botOptions.setProxyHost("127.0.0.1");
        // botOptions.setProxyPort(7897);
        // botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

        DefaultBotSession defaultBotSession = new DefaultBotSession();
        defaultBotSession.setOptions(botOptions);
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(defaultBotSession.getClass());
            TelegramBot bot = new TelegramBot(botOptions);
            telegramBotsApi.registerBot(bot);
            System.out.println("已连接到 Telegram");
        } catch (TelegramApiException e) {
            System.err.println("连接 Telegram 时出现问题");
        }
    }

}

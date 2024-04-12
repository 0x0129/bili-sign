package xin.lain;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelegramBot extends TelegramLongPollingBot {
    private static String av;
    private static String bv;
    private static String title;
    private static String pic;
    private static String desc;
    private static String cid;

    public TelegramBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotToken() {
        return Config.token;
    }

    @Override
    public String getBotUsername() {
        return Config.username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            try {
                Message message = update.getMessage();
                String text = message.getText();
                if (text.startsWith("/start")) {
                    sendMsg("[/sign] 直播签到\n3000用户经验和2包辣条");
                    sendMsg("[/watch] 观看视频\n5经验");
                    sendMsg("[/share] 分享视频\n5经验");
                    sendMsg("[/coin] 给视频投币\n每次10经验，上限50经验");
                    sendMsg("[/csrf] 获取CSRF");
                } else if (text.startsWith("/sign")) {
                    sendMsg("[+] 正在执行签到");
                    if (Objects.equals(GetResponse.get("https://api.live.bilibili.com/xlive/web-ucenter/v1/sign/DoSign", "code", Config.cookies), "0")) {
                        sendMsg("[+] 签到成功，获得3000用户经验和2包辣条");
                    } else {
                        sendMsg("[-] 签到失败，您可能已经签到过了");
                    }
                } else if (text.startsWith("/watch")) {
                    if (extractArgument(text) == null) {
                        avInfo();
                        if (Objects.equals(GetResponse.post("https://api.bilibili.com/x/v2/history/report", "code", "application/x-www-form-urlencoded", "aid=" + av + "&cid=" + cid + "&progress=300&csrf=" + Config.csrf, Config.cookies), "0")) {
                            sendMsg("[+] 观看成功，总共看了300秒");
                        } else {
                            sendMsg("[-] 观看失败");
                        }
                    } else {
                        bvInfo(text);
                        if (Objects.equals(GetResponse.post("https://api.bilibili.com/x/v2/history/report", "code", "application/x-www-form-urlencoded", "aid=" + Converter.bv2av(bv) + "&cid=" + cid + "&progress=300&csrf=" + Config.csrf, Config.cookies), "0")) {
                            sendMsg("[+] 观看成功，总共看了300秒");
                        } else {
                            sendMsg("[-] 观看失败");
                        }
                    }
                } else if (text.startsWith("/share")) {
                    if (extractArgument(text) == null) {
                        avInfo();
                        if (Objects.equals(GetResponse.post("https://api.bilibili.com/x/web-interface/share/add", "code", "application/x-www-form-urlencoded", "aid=" + av + "&csrf=" + Config.csrf, Config.cookies), "0")) {
                            sendMsg("[+] 分享成功");
                        } else {
                            sendMsg("[-] 分享失败");
                        }
                    } else {
                        bvInfo(text);
                        if (Objects.equals(GetResponse.post("https://api.bilibili.com/x/web-interface/share/add", "code", "application/x-www-form-urlencoded", "bvid=" + bv + "&csrf=" + Config.csrf, Config.cookies), "0")) {
                            sendMsg("[+] 分享成功");
                        } else {
                            sendMsg("[-] 分享失败");
                        }
                    }
                } else if (text.startsWith("/coin")) {
                    if (extractArgument(text) == null) {
                        avInfo();
                        if (Objects.equals(GetResponse.post("https://api.bilibili.com/x/web-interface/coin/add", "code", "application/x-www-form-urlencoded", "aid=" + av + "&multiply=1&csrf=" + Config.csrf, Config.cookies), "0")) {
                            sendMsg("[+] 投币成功，获得了10经验");
                        } else {
                            sendMsg("[-] 投币失败");
                        }
                    } else {
                        bvInfo(text);
                        if (Objects.equals(GetResponse.post("https://api.bilibili.com/x/web-interface/coin/add", "code", "application/x-www-form-urlencoded", "bvid=" + bv + "&multiply=1&csrf=" + Config.csrf, Config.cookies), "0")) {
                            sendMsg("[+] 投币成功，获得了10经验");
                        } else {
                            sendMsg("[-] 投币失败");
                        }
                    }
                } else if (text.startsWith("/csrf")) {
                    sendMsg(Config.csrf);
                } else {
                    sendMsg("[-] 未知命令，请重新输入");
                }
            } catch (Exception e) {
                sendMsg("[-] 出现错误了\n" + e.getMessage());
            }
        }
    }

    public void avInfo() {
        sendMsg("[+] 正在获取首页推荐视频");
        av = GetResponse.get("https://api.bilibili.com/x/web-interface/index/top/rcmd", "data/item/0/id", Config.cookies);
        sendMsg("[+] 获取成功\nhttps://b23.tv/av" + av);
        sendMsg("[+] 正在获取视频信息\n");
        title = GetResponse.get("https://api.bilibili.com/x/web-interface/view?aid=" + av, "data/title", Config.cookies);
        pic = GetResponse.get("https://api.bilibili.com/x/web-interface/view?aid=" + av, "data/pic", Config.cookies);
        desc = GetResponse.get("https://api.bilibili.com/x/web-interface/view?aid=" + av, "data/desc", Config.cookies);
        cid = GetResponse.get("https://api.bilibili.com/x/web-interface/view?aid=" + av, "data/cid", Config.cookies);
        sendPhoto(pic);
        sendMsg("[+] 视频标题\n" + title);
        sendMsg("[+] 视频简介\n" + desc);
    }

    public void bvInfo(String text) {
        bv = extractArgument(text);
        sendMsg("[+] 检测到视频\nhttps://b23.tv/" + bv);
        sendMsg("[+] 正在获取视频信息\n");
        title = GetResponse.get("https://api.bilibili.com/x/web-interface/view?bvid=" + bv, "data/title", Config.cookies);
        pic = GetResponse.get("https://api.bilibili.com/x/web-interface/view?bvid=" + bv, "data/pic", Config.cookies);
        desc = GetResponse.get("https://api.bilibili.com/x/web-interface/view?bvid=" + bv, "data/desc", Config.cookies);
        cid = GetResponse.get("https://api.bilibili.com/x/web-interface/view?bvid=" + bv, "data/cid", Config.cookies);
        sendPhoto(pic);
        sendMsg("[+] 视频标题\n" + title);
        sendMsg("[+] 视频简介\n" + desc);
    }

    public void sendMsg(String text) {
        SendMessage response = new SendMessage();
        response.setChatId(Config.telegram);
        response.setText(text);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            System.err.println("Telegram Bot 发送消息时出现问题");
        }
    }

    public void sendPhoto(String link) {
        SendPhoto response = new SendPhoto();
        response.setChatId(Config.telegram);
        response.setPhoto(new InputFile(link));
        try {
            execute(response);
        } catch (TelegramApiException e) {
            System.err.println("Telegram Bot 发送消息时出现问题");
        }
    }

    public static String extractArgument(String input) {
        Pattern pattern = Pattern.compile("/\\w+\\s+([^ ]+)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}


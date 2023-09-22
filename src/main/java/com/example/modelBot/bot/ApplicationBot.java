package com.example.modelBot.bot;

import com.example.modelBot.bot.config.BotConfig;
import com.example.modelBot.entities.DTO.PostDTO;
import com.example.modelBot.entities.UserEntity;
import com.example.modelBot.services.PostsService;
import com.example.modelBot.services.impl.PostService;
import com.example.modelBot.services.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApplicationBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final UserServiceImpl userService;
    private final PostService postService;
    private final Map<Long, PostDTO> map = new HashMap<>();
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            var message = update.getMessage();
            var userId = message.getChatId();
            if (userService.getUserById(userId) ==null ){
                var user = UserEntity.builder()
                        .userId(userId)
                        .userName(message.getFrom().getUserName())
                        .firstName(message.getFrom().getFirstName())
                        .build();
                userService.saveUser(user);
            }
            if (message.hasText()){
                var messageText = message.getText();
                switch (messageText){
                    case "/start" -> {
                        SendFirstMessage(userId);
                        map.remove(userId);
                    }
                    case "Salon narxlarini korish" -> {
                        SendPriceInfo(userId);
                    }
                    case "Sotaman" ->{
                        map.remove(userId);
                        map.put(userId,new PostDTO());
                        SendModelReq(userId);
                    }case "Tekshirish" -> {
                        SendCheckPost(userId);
                        SendToCheck(userId,message.getMessageId()+1);
                    }
                    case "ok"->{
                        postService.addPost(map.get(userId),userId);
                    }
                    default -> {
                        if (map.containsKey(userId)){
                            var post = map.get(userId);
                            if (post.getModel() == null){
                                post.setModel(messageText);
                                SendPozReq(userId);
                                map.put(userId,post);
                            }
                            else if (post.getPozitsiya() == null) {
                                post.setPozitsiya(messageText);
                                SendPaintReq(userId);
                                map.put(userId,post);
                            }else if (post.getPaint() == null){
                                post.setPaint(messageText);
                                SendColorReq(userId);
                                map.put(userId,post);
                            } else if (post.getColor() == null) {
                                post.setColor(messageText);
                                SendDateReq(userId);
                                map.put(userId,post);
                            } else if (post.getDate_of() == null) {
                                post.setDate_of(messageText);
                                SendProbegReq(userId);
                                map.put(userId,post);
                            } else if (post.getMillage() == null) {
                                post.setMillage(messageText);
                                SendFuelReq(userId);
                                map.put(userId,post);
                            }else if (post.getFuel() == null){
                                post.setFuel(messageText);
                                SendPriceReq(userId);
                                map.put(userId,post);
                            } else if (post.getPrise() == null) {
                                post.setPrise(messageText);
                                SendContactReq(userId);
                                map.put(userId,post);
                            } else if (post.getContact() == null) {
                                post.setContact(messageText);
                                SendAddressReq(userId);
                                map.put(userId,post);
                            } else if (post.getAddress() == null) {
                                post.setAddress(messageText);
                                SendPhotoReq(userId);
                                map.put(userId,post);
                            }

                        }
                    }
                }
            } else if (message.hasPhoto()) {
                if (map.containsKey(userId)){
                    PostDTO post = map.get(userId);
                    if (post.getPhotos() == null){
                        post.setPhotos(new ArrayList<>(List.of(message.getPhoto().get(message.getPhoto().size()-1).getFileId())));
                        map.put(userId,post);
                    }else {
                        List<String> photos = post.getPhotos();
                        photos.add(message.getPhoto().get(message.getPhoto().size()-1).getFileId());
                        post.setPhotos(photos);
                        map.put(userId,post);
                    }

                }
                SendMessage sendMessage = new SendMessage(userId.toString(),"Rasm yuklandi.Post tayyormi?");
                sendMessage.setReplyMarkup(sendMarkup());

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Message message = update.getCallbackQuery().getMessage();
            Long userid = message.getFrom().getId();
            if (data.equals("sendToCheck")) {
                System.out.println(message);
                SendToAdmin(userid, message);
            }
        }
    }

    private void SendToAdmin(Long fromChatId, Message reply) {
        CopyMessage copyMessage = new CopyMessage();
        copyMessage.setFromChatId(fromChatId);
        copyMessage.setChatId(5094739326L);
        copyMessage.setMessageId(reply.getReplyToMessage().getMessageId());

        copyMessage.setCaption(reply.getCaption());
        copyMessage.setReplyMarkup(reply.getReplyMarkup());

        try {
            execute(copyMessage);
        } catch (TelegramApiException e) {
                System.out.println(e.getMessage());
        }
    }

    private void SendCheckPost(Long userId) {
        PostDTO dto = map.get(userId);
        SendMediaGroup mediaGroup = new SendMediaGroup();
        mediaGroup.setChatId(userId);
        List<InputMedia> list = new ArrayList<>();
        for (int i = 0;i<map.get(userId).getPhotos().size();i++) {
            InputMediaPhoto inputMediaPhoto;
            if (i == 0) {
                inputMediaPhoto = new InputMediaPhoto(map.get(userId).getPhotos().get(i));
                inputMediaPhoto.setCaption("\uD83D\uDE98Moshina modeli: #"+dto.getModel()+"\n" +
                        "\uD83D\uDD22Pozitsiya: "+dto.getPozitsiya()+"\n" +
                        "\uD83D\uDD04Kraska: "+dto.getPaint()+" \n" +
                        "♻️Rangi: "+dto.getColor()+" \n" +
                        "\uD83D\uDCF6Yil: "+dto.getDate_of()+"\n" +
                        "\uD83C\uDD99Probeg: "+dto.getMillage()+"\n" +
                        "⛽️"+dto.getFuel()+" \n" +
                        "\uD83D\uDCB2Narxi: "+dto.getPrise()+" \n" +
                        "\uD83D\uDCDETel: "+dto.getContact()+"\n" +
                        "\uD83D\uDEA9 "+dto.getAddress()+" \n\n" +
                        "Bizzi Kanaldi reklamasi)");
            }else {
                inputMediaPhoto = new InputMediaPhoto(map.get(userId).getPhotos().get(i));
            }
            list.add(inputMediaPhoto);
        }
        mediaGroup.setMedias(list);
        try {
            execute(mediaGroup);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendFirstMessage(Long userId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Assalomu alaykum siz Moshina bozor kanalining rasmiy botiga tashrif buyurdingiz\n." +
                "O'zingiz uchun kerakli bo'limni tanlang.");
        sendMessage.setChatId(userId);
        sendMessage.setReplyMarkup(firstMarkup());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendPriceInfo(Long userId){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(userId);
        sendPhoto.setPhoto(new InputFile(new File("/home/nodirbek/Downloads/Telegram Desktop/photo_2023-09-13_21-04-47.jpg")));

        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendModelReq(Long userId){
        SendMessage sendMessage = new SendMessage(userId.toString(),"Model kiriting: Masalan: Damas");
        sendMessage.setReplyMarkup(new  ReplyKeyboardRemove(true));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendPozReq(Long userId){
        SendMessage sendMessage = new SendMessage(userId.toString(),"Pozitsiya kiriting:");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendPaintReq(Long userId){
        SendMessage sendMessage = new SendMessage(userId.toString(),"Kraska kiriting:\n Masalan: Toza ,Radnoy");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendColorReq(Long userId){
        SendMessage sendMessage = new SendMessage(userId.toString(),"Rang kiriting:");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendDateReq(Long userId){
        SendMessage sendMessage = new SendMessage(userId.toString(),"Yilini kiriting:");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendProbegReq(Long userId){
        SendMessage sendMessage = new SendMessage(userId.toString(),"Probeg kiriting:");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendFuelReq(Long userId){
        SendMessage sendMessage = new SendMessage(userId.toString(),"Yoqilg'i turrini kiriting:");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendPriceReq(Long userId){
        SendMessage sendMessage = new SendMessage(userId.toString(),"Narxini kiriting:");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendContactReq(Long userId){
        SendMessage sendMessage = new SendMessage(userId.toString(),"Telefon raqamini kiriting:");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendAddressReq(Long userId){
        SendMessage sendMessage = new SendMessage(userId.toString(),"Hudud kiriting:");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendPhotoReq(Long userId){
        SendMessage sendMessage = new SendMessage(userId.toString(),"Rasm Yuboring:");
//        sendMessage.setReplyMarkup(sendMarkup());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    void SendToCheck(Long userId,int messageId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setReplyMarkup(sendToCheckMarkup());
        sendMessage.setText("Tekshirish uchun yborasizmi?");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    // Markups

    ReplyKeyboardMarkup firstMarkup(){
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton();
        button.setText("Sotaman");
        row.add(button);
        button = new KeyboardButton();
        button.setText("Sotib Olaman");
        row.add(button);
        rowList.add(row);
        row = new KeyboardRow();
        button = new KeyboardButton("Salon narxlarini korish");
        row.add(button);
        button = new KeyboardButton("Mening elonlarim");
        row.add(button);
        rowList.add(row);
        markup.setKeyboard(rowList);
        return markup;
    }
    ReplyKeyboardMarkup sendMarkup(){
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton("Tekshirish");
        row.add(button);
        keyboard.add(row);
        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);
        return markup;
    }
    InlineKeyboardMarkup sendToCheckMarkup(){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Tekshirish uchun adminga yuborish");
        button.setCallbackData("sendToCheck");
        row.add(button);
        keyboard.add(row);
        markup.setKeyboard(keyboard);
        return markup;
    }

}

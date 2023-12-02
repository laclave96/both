package com.insightdev.both;


import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.greenrobot.eventbus.EventBus;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Conversations extends Application {

    private static ChatDatabaseDriver chatDatabaseDriver;
    private static PendingMessagesDriver pendingMessagesDriver;
    private static ArrayList<Chat> conversations = new ArrayList<>();
    private static ArrayList<Msg> notifications = new ArrayList<>();
    private static ArrayList<PendingMessage> pendingToSend = new ArrayList<>();
    private static Pair<Boolean, String> INSIDE_CHAT = new Pair<>(false, "");
    private static ExecutorService chatDatabaseExecutor;

    public static void loadDatabaseDrivers(Context context) {
        chatDatabaseExecutor = Executors.newSingleThreadExecutor();
        chatDatabaseDriver = new ChatDatabaseDriver(context);
        pendingMessagesDriver = new PendingMessagesDriver(context);

    }

    public static ArrayList<Chat> getConversations() {
        return conversations;
    }

    public static void loadConversations() {
        Conversations.conversations = chatDatabaseDriver.getChats();
        Collections.sort(conversations,Collections.reverseOrder());
        ChatUiUpdater.loadChats();
    }

    public static void addConversation(Chat chat, Context context) {
        conversations.add(0, chat);
        chatDatabaseExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    chatDatabaseDriver.addChat(chat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ChatUiUpdater.newChat(chat, context);

    }

    public static ArrayList<PendingMessage> getPendingToSend() {
        return pendingToSend;
    }

    public static void loadPendingToSend() {
        Conversations.pendingToSend = pendingMessagesDriver.getPendingMessages();
    }

    public static void addPendingToSendMessage(PendingMessage pending) {
        pendingMessagesDriver.addMessage(pending);
        pendingToSend.add(pending);

    }

    public static void removePendingToSendMessage(Object message) {
        pendingMessagesDriver.deleteMessage((PendingMessage) message);
        pendingToSend.remove(message);

    }

    public static void cleanAllPendingToSendMessages() {
        pendingToSend = new ArrayList<>();
        pendingMessagesDriver.cleanAll();
    }

    public static int getPosByUser(String username) {
        for (int i = 0; i < conversations.size(); i++)
            if (conversations.get(i).getUsername().contentEquals(username)) {
                return i;
            }
        return -1;

    }

    public static Chat getChat(String username) {
        int pos = getPosByUser(username);
        if (pos != -1) {
            return conversations.get(pos);
        }
        return null;
    }

    public static int setChat(String username, Chat chat) {
        int pos = getPosByUser(username);
        conversations.set(pos, chat);
        return pos;
    }

    public static void addMsgToChat(String from, Msg msg, Context context) {
        Log.d("AddMessage","init");
        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                int pos = getPosByUser(from);
                Log.d("AddMessage","pos"+pos);
                if (pos != -1) {
                    Chat last = conversations.get(pos);
                    last.getMessages().add(msg);
                    conversations.add(0, last);
                    conversations.remove(pos + 1);
                    Log.d("AddMessage","adjustPos");
                    chatDatabaseExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("AddMessage","adingMessage");
                            chatDatabaseDriver.addMessage(from, msg);
                            Log.d("AddMessage","addeddatabased");
                        }
                    });
                    Log.d("AddMessage","chatUI");
                    ChatUiUpdater.addMessage(from, msg.getType().contentEquals("out"), context);
                    Log.d("AddMessage","chatUINotified");
                }
            }
        });


    }

    public static void deleteChat(String username) {
        int pos = Conversations.getPosByUser(username);
        if (pos != -1) {
            cleanAllPendingToSendMessages();
            chatDatabaseExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    Log.d("DeleteChat", "begin");
                    chatDatabaseDriver.deleteChat(username);
                    Log.d("DeleteChat", "deletedFromDb");
                    conversations.remove(pos);
                    Log.d("DeleteChat", "removeArray");
                    ChatUiUpdater.deleteChat(username);
                    Log.d("DeleteChat", "UiUpdate");
                    EventBus.getDefault().post(new LoadingDismissEvent());
                }
            });

        }else{
            ChatUiUpdater.deleteChat(username);
            EventBus.getDefault().post(new LoadingDismissEvent());
        }

    }

    public static void cleanAllMessages(String username) {
        int pos = getPosByUser(username);
        if (pos != -1) {
            cleanAllPendingToSendMessages();
            chatDatabaseExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    chatDatabaseDriver.deleteChat(username);
                    conversations.remove(pos);
                    ChatUiUpdater.cleanMessages(username);
                }
            });

        }

    }

    public static void setMuted(String username, boolean state) {
        int pos = getPosByUser(username);
        if (pos != -1) {
            conversations.get(pos).setMuted(state);
            chatDatabaseExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    chatDatabaseDriver.changeMuted(username, state);
                }
            });
            ChatUiUpdater.changeMuted(pos);
        }
    }

    public static void updateYourPublicKey(String with, String key, Context context) {
        int pos = getPosByUser(with);
        PublicKey publicKey = Tools.getPublicKeyFromString(key);
        ContactsManager.getContact(with, context).setPublicKey(key);
        if (pos != -1) {
            if (!conversations.get(pos).getPublicKeyAsString().contentEquals(key)) {
                conversations.get(pos).setPublicKey(publicKey);
                chatDatabaseExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        chatDatabaseDriver.updateYourPublicKey(with, key);
                    }
                });
                setPendingToSendAesKey(with, true);
                updateMyAesKey(with, conversations.get(pos).getMyAESKey());
            }

        }

    }

    public static void updateYourAesKey(String with, String key) {
        int pos = getPosByUser(with);
        if (pos != -1) {
            conversations.get(pos).setYourAESKey(key);
            chatDatabaseExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    chatDatabaseDriver.updateYourAesKey(with, key);
                }
            });
        }

    }

    public static void updateMyAesKey(String with, String key) {
        int pos = getPosByUser(with);
        if (pos != -1) {
            chatDatabaseExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    chatDatabaseDriver.updateMyAesKey(with, key);
                }
            });
        }


    }

    public static void changeMessageStatus(String with, String id, String status, String type) {
        Chat chat = getChat(with);
        if (chat != null) {
            List<Msg> messages = chat.getMessages();
            for (int i = 0; i < messages.size(); i++) {
                Msg message = messages.get(i);
                if (message.getType().contentEquals(type))
                    if (message.getId().contentEquals(id)) {
                        message.setStatus(status);
                        chatDatabaseExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                chatDatabaseDriver.changeMessageStatus(with, id, status, type);
                            }
                        });
                        ChatUiUpdater.changeMessageStatus(with, i, status);
                        break;
                    }
            }

        }

    }

    public static void setToListenAudio(String with, String id, boolean updateUi) {
        Chat chat = getChat(with);
        if (chat != null) {
            List<Msg> messages = chat.getMessages();
            for (int i = 0; i < messages.size(); i++) {
                Msg message = messages.get(i);
                if (message instanceof AudioMessage)
                    if (message.getId().contentEquals(id)) {
                        ((AudioMessage) message).setListen(true);
                        chatDatabaseExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                chatDatabaseDriver.setToListenAudio(with, id);
                            }
                        });
                        if (updateUi)
                            ChatUiUpdater.changeMessageStatus(with, i, "listen");
                        break;
                    }
            }

        }
    }

    public static void setPendingReading(String with, boolean state) {
        int pos = getPosByUser(with);
        if (pos != -1) {
            conversations.get(pos).setPendingReading(state);
            chatDatabaseExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    chatDatabaseDriver.changePendingReading(with, state);
                }
            });
            ChatUiUpdater.changePendingReading(pos);

        }

    }

    public static void setPendingToSendAesKey(String with, boolean state) {
        int pos = getPosByUser(with);
        if (pos != -1) {
            conversations.get(pos).setPendingSendAesKey(state);
            chatDatabaseExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    chatDatabaseDriver.changePendingSendAesKey(with, state);
                }
            });

        }

    }

    public static ArrayList<String> changeReceivedToRead(String with) {
        ArrayList<String> notify_read = new ArrayList<>();
        Chat chat = getChat(with);
        if (chat != null) {
            List<Msg> messages = chat.getMessages();
            for (int i = 0; i < messages.size(); i++)
                if (messages.get(i).getType().contentEquals("in"))
                    if (messages.get(i).getStatus().contentEquals("received")) {
                        messages.get(i).setStatus("read");
                        String id = messages.get(i).getId();
                        notify_read.add(id);
                        int finalI = i;
                        chatDatabaseExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                chatDatabaseDriver.changeMessageStatus(with, id, "read", "in");
                            }
                        });
                        ChatUiUpdater.changeMessageStatus(with, i, "read");
                    }

        }
        return notify_read;
    }

    public static Pair<Boolean, String> getInsideChat() {
        return INSIDE_CHAT;
    }

    public static void setInsideChat(Pair<Boolean, String> insideChat) {
        INSIDE_CHAT = insideChat;
    }


}

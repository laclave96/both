package com.insightdev.both;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.Nullable;
import org.jivesoftware.smack.roster.Roster;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ContactsManager extends Application {

    public static ArrayList<Contact> contacts = new ArrayList<>();

    public synchronized static ArrayList<Contact> getContacts() {
        return contacts;
    }

    public static ContactsDBDriver contactsDBDriver;

    public static void loadContactDBDriver(Context context) {

        Log.d("anlskdamd", "WTFFF");
        if (contactsDBDriver == null)
            contactsDBDriver = new ContactsDBDriver(context);

    }

    public static void addContactToDB(String id, Context context) {

        if (contactsDBDriver == null)
            loadContactDBDriver(context);
        contactsDBDriver.addContact(id);

    }


    public static void removeContactFromDB(String id, Context context) {

        if (contactsDBDriver == null)
            loadContactDBDriver(context);
        contactsDBDriver.deleteContact(id);

    }

    public static ArrayList<ExpDateContact> getContactFromDB(Context context) {
        if (contactsDBDriver == null)
            loadContactDBDriver(context);
        return contactsDBDriver.getContacts();

    }


    public static void setContacts(ArrayList<Contact> contacts) {
        ContactsManager.contacts = contacts;
        Log.d("NNLOG", ContactsManager.contacts.size() + " l");
    }

    public static void addContact(Contact contact) {
        contacts.add(contact);
        Log.d("Activity_", "AddingContact" + contact.getType() + "//" + contact.getName());
       /* if (contact.getType().contentEquals(Tools.getString(R.string.like, context)))
            ContactsUiUpdater.notifyLike(contact, context);*/
        Log.d("Activity_", "verfiyngMatch");

    }

    public static boolean checkAddContact(Contact contact, Context context) {
        if (getPosByUsername(contact.getChatUsername(context), context) == -1)
            return contacts.add(contact);
        return false;
    }

    public static void removeContact(int pos) {
        Log.d("ñeñer", "removeCOntactelement " + contacts.remove(pos));
        ContactsUiUpdater.changeContacts();
    }

    public static int getPosByUsername(String username, Context context) {
        for (int i = 0; i < contacts.size(); i++)
            if (contacts.get(i).getChatUsername(context).contentEquals(username)) {
                return i;
            }
        return -1;

    }

    public static Contact getContact(String username, Context context) {
        int pos = getPosByUsername(username, context);
        if (pos != -1) {
            return contacts.get(pos);
        }
        return null;
    }


    @Nullable
    public static Contact getContact(int id) {
        for (int i = 0; i < contacts.size(); i++)
            if (contacts.get(i).getId().contentEquals(id + "")) {
                return contacts.get(i);
            }
        return null;
    }

    public static void updateContact(Contact contact, Context context) {
        int pos = getPosByUsername(contact.getChatUsername(context), context);
        if (pos == -1)
            return;
        String auxType = contacts.get(pos).getType();
        String type = contact.getType();
        Log.d("kasdadad", "update " + auxType + " / " + type);
        contacts.set(pos, contact);
        contacts.get(pos).setType(auxType);
        ContactsManager.addType(contacts.get(pos).getChatUsername(context), type, context);

        Log.d("kasdadad", "update " + contacts.get(pos).getType());

    }

    public static void notifyMatch(Context context, boolean myLike, Contact contact) {

        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {

                ContactsUiUpdater.notifyMatch(contact, context);
                ContactsUiUpdater.openMatchScreen(contact.getChatUsername(context));
                if (XMPPMessageServer.getConnection() != null) {
                    XMPPMessageServer.getChatHandler().addRosterEntry(contact.getChatUsername(context), contact.getName());
                    Roster roster = XMPPMessageServer.getRoster();
                    String with = contact.getChatUsername(context);
                    BareJid jid = null;
                    try {
                        jid = JidCreate.bareFrom(with);
                    } catch (XmppStringprepException e) {
                        e.printStackTrace();
                    }
                    AppHandler.updateStatusByPresence(roster.getPresence(jid), context);
                }


                AppHandler.executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.contacts, context), ContactsManager.getSerializedContacts(), context);
                    }
                });
                ContactsUiUpdater.changeContacts();
                if (myLike)
                    AppHandler.updatePendingActions(context);
            }
        });


    }

    public static void verifyingMatches(Context context, boolean myLike) {

        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                ArrayList<Integer> forRemove = new ArrayList<>();
                boolean matching = false;
                Log.d("NNLOG", ContactsManager.contacts.size() + " lçk1");
                for (int i = 0; i < contacts.size() - 1; i++)
                    for (int j = i + 1; j < contacts.size(); j++) {
                        String iType = contacts.get(i).getType();
                        String jType = contacts.get(j).getType();
                        String match = Tools.getString(R.string.match, context);
                        if (!iType.contains(match) && !jType.contains(match) && !iType.contains(jType))
                            if (contacts.get(i).getId().contentEquals(contacts.get(j).getId())) {
                                int keep = i, delete = j;
                                if (contacts.get(i).getLastUpdate().getTime() < contacts.get(j).getLastUpdate().getTime()) {
                                    keep = j;
                                    delete = i;
                                }
                                // contacts.get(keep).setType(match);
                                addType(contacts.get(keep).getChatUsername(context), match, context);
                                Contact contact = contacts.get(keep);
                                forRemove.add(delete);
                                ContactsUiUpdater.notifyMatch(contact, context);
                                ContactsUiUpdater.openMatchScreen(contact.getChatUsername(context));
                                if (XMPPMessageServer.getConnection() != null) {
                                    XMPPMessageServer.getChatHandler().addRosterEntry(contact.getChatUsername(context), contact.getName());
                                    Roster roster = XMPPMessageServer.getRoster();
                                    String with = contact.getChatUsername(context);
                                    BareJid jid = null;
                                    try {
                                        jid = JidCreate.bareFrom(with);
                                    } catch (XmppStringprepException e) {
                                        e.printStackTrace();
                                    }
                                    AppHandler.updateStatusByPresence(roster.getPresence(jid), context);
                                }
                                matching = true;

                            }
                    }
                for (Integer i : forRemove) {
                    if (i != contacts.size())
                        contacts.remove((int) i);
                }
                AppHandler.executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        Profile.setData(Tools.getString(R.string.contacts, context), ContactsManager.getSerializedContacts(), context);
                    }
                });
                ContactsUiUpdater.changeContacts();
                if (matching && myLike)
                    AppHandler.updatePendingActions(context);
            }
        });


    }

    public static void updateStatus(String from, String status, Context context) {

        int posUser = getPosByUsername(from, context);
        if (posUser == -1)
            return;
        contacts.get(posUser).setStatus(status);
        ChatUiUpdater.changeContactStatus(from, status);
    }

    public static void addType(String from, String type, Context context) {

        int posUser = getPosByUsername(from, context);

        if (posUser == -1)
            return;

        String auxType = contacts.get(posUser).getType();

        if ((type.equals("like") && auxType.contains("megusta")) || (type.equals("megusta") && auxType.contains("like"))) {


            auxType = auxType.replace("megusta", "");

            auxType = auxType.replace("like", "");
            contacts.get(posUser).setType(auxType + " " + "match");

            boolean my_like = false;

            if (type.equals("megusta"))
                my_like = true;

            notifyMatch(context, my_like, contacts.get(posUser));

        } else if (!auxType.contains(type))
            contacts.get(posUser).setType(auxType + " " + type);


    }


    public static void removeType(String from, String type, Context context) {

        int posUser = getPosByUsername(from, context);

        if (posUser == -1)
            return;

        String auxType = contacts.get(posUser).getType();

        auxType = auxType.replace(type, "");

        contacts.get(posUser).setType(auxType);

    }

    public static String getSerializedContacts() {
        ArrayList<String> contacts = new ArrayList<>();
        for (Contact contact : ContactsManager.contacts) {
            contacts.add(new Gson().toJson(contact));
        }
        Gson gson = new Gson();
        return gson.toJson(contacts);
    }

    public static ArrayList<Contact> getMatches(Context context) {
        ArrayList<Contact> array = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++)
            if (contacts.get(i).getType().contains(Tools.getString(R.string.match, context))) {
                array.add(contacts.get(i));
            }
        return array;
    }

    public static ArrayList<Contact> getLikes(Context context) {
        ArrayList<Contact> array = new ArrayList<>();

        Log.d("alkskjd", contacts.size() + " ");
        for (int i = 0; i < contacts.size(); i++)
            if (contacts.get(i).getType().contains("like")) {
                array.add(contacts.get(i));

            }
        return array;
    }
    public static ArrayList<Contact> getExpDates(Context context) {
        ArrayList<Contact> array = new ArrayList<>();

        Log.d("alkskjd", contacts.size() + " ");
        for (int i = 0; i < contacts.size(); i++)
            if (contacts.get(i).getType().contains("expDate")) {
                array.add(contacts.get(i));

            }
        return array;
    }

    public static ArrayList<Contact> getFullList(Context context) {
        ArrayList<Contact> array = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            String type = contacts.get(i).getType();
            if (type.contains(Tools.getString(R.string.like, context)) ||
                    type.contains(Tools.getString(R.string.match, context))) {
                array.add(contacts.get(i));
            }
        }
        return array;
    }

    public static int getCantLikes(Context context) {
        if (contacts.isEmpty())
            return 0;
        int likes = 0;
        String like = Tools.getString(R.string.like, context);
        String match = Tools.getString(R.string.match, context);
        Log.d("lkasdkad", contacts.size() + "");
        for (Contact contact : contacts) {
            if (contact == null || contact.getType() == null)
                continue;
            String type = contact.getType();
            if (type.contains(like) || type.contains(match))
                likes++;
        }

        return likes;

    }

    public static int getCantMatches(Context context) {
        if (contacts.isEmpty())
            return 0;
        int matches = 0;
        String match = Tools.getString(R.string.match, context);
        for (Contact contact : contacts) {
            if (contact == null || contact.getType() == null)
                continue;
            String type = contact.getType();
            if (type.contains(match))
                matches++;
        }
        return matches;
    }

    public static String getLastLikeImage(Context context) {
        String photo = "";
        String like = Tools.getString(R.string.like, context);
        for (Contact contact : contacts) {
            String type = contact.getType();
            if (type.contentEquals(like))
                photo = contact.getProfilePhotoMedium(context);
        }
        Log.d("LastImage_", "url" + photo);
        return photo;
    }


}

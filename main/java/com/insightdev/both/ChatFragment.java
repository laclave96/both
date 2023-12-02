package com.insightdev.both;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private RecyclerView contactRecycler;
    private TextView allTitle;
    private String typeContacts;
    private ImageView backChat;
    private EditText editText;
    private RecyclerView chatRecycler;
    private MotionLayout motionChat;
    private LottieAnimationView view_button;

    private ConstraintLayout noChats;

    private ArrayList<ContactItem> contactsItems = new ArrayList<>();
    private ArrayList<ChatItem> chatItems = new ArrayList<>();
    private ArrayList<Chat> chats = new ArrayList<>();
    private ContactsAdapter contactsAdapter;
    private ChatListAdapter chatListAdapter;
    private ContactsAdapter.OnItemCLickListener contactLisItem;
    private ChatListAdapter.OnItemCLickListener chatListItem;
    private WrapContentLinearLayoutManager.onStopScroll contactsScrollListener;
    private WrapContentLinearLayoutManager.onStopScroll chatListScrollListener;
    private boolean pendingLoadContacts = false;
    private boolean layoutCompleted = true;
    private TextView noChatText;
    private ImageView noChatImg;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        view_button = view.findViewById(R.id.view_button);
        contactRecycler = view.findViewById(R.id.matchesRecylcer);
        chatRecycler = view.findViewById(R.id.chatRecycler);
        backChat = view.findViewById(R.id.backChat);
        motionChat = view.findViewById(R.id.messageLay);
        noChats = view.findViewById(R.id.no_chats);
        noChatText = view.findViewById(R.id.no_chat_text);
        noChatImg = view.findViewById(R.id.no_chat_img);
        editText = view.findViewById(R.id.searchView);
        allTitle = view.findViewById(R.id.allTitle);


        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(getActivity()));
        typeContacts = Tools.getString(R.string.like, getContext());

        contactsScrollListener = new WrapContentLinearLayoutManager.onStopScroll() {
            @Override
            public void stopScroll(boolean completed) {
                layoutCompleted = completed;
                if (pendingLoadContacts)
                    EventBus.getDefault().post(new ChangeContactsEvent());
            }
        };

        chatListScrollListener = new WrapContentLinearLayoutManager.onStopScroll() {
            @Override
            public void stopScroll(boolean completed) {

            }
        };

        contactLisItem = new ContactsAdapter.OnItemCLickListener() {
            @Override
            public void OnItemClick(int position) {
                if (position == -1 || position == contactsItems.size())
                    return;
                Contact contact = ContactsManager.getContact(Integer.parseInt(contactsAdapter.getList().get(position).getId()));

                String type = contact.getType();
                if (type.contains(Tools.getString(R.string.match, getContext()))) {
                    Intent intent = new Intent(getActivity(), ChatRoom.class);
                    intent.putExtra(Tools.getString(R.string.username, getContext()), contact.getChatUsername(getContext()));
                    startActivity(intent);
                } else {
                    ProfileDialogFragment profileDialogFragment = new ProfileDialogFragment();
                    if (profileDialogFragment.isAdded())
                        return;
                    Bundle bundle = new Bundle();
                    bundle.putString("person", new Gson().toJson(contact));
                    profileDialogFragment.setArguments(bundle);

                    profileDialogFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "ProfileDialog");
                }
            }

        };

        chatListItem = new ChatListAdapter.OnItemCLickListener() {
            @Override
            public void OnItemClick(int position) {
                Log.d("Lifecycle", "startActivity" + position);
                if (position == -1 || position == chats.size())
                    return;
                Intent intent = new Intent(getActivity(), ChatRoom.class);
                intent.putExtra(Tools.getString(R.string.username, getContext()), chatListAdapter.getList().get(position).getUsername());
                startActivity(intent);
            }

            @Override
            public void OnImageClick(int position) {
                if (position == -1 || position == chats.size())
                    return;
                Contact contact = ContactsManager.getContact(chatListAdapter.getList().get(position).getUsername(), getContext());
                if (contact != null) {

                    ProfileDialogFragment profileDialogFragment = new ProfileDialogFragment();
                    if (profileDialogFragment.isAdded())
                        return;
                    Bundle bundle = new Bundle();
                    bundle.putString("person", new Gson().toJson(contact));
                    profileDialogFragment.setArguments(bundle);

                    profileDialogFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "ProfileDialog");

                }

            }
        };


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (chatListAdapter != null)
                    chatListAdapter.getFilter().filter(s);
                if (contactsAdapter != null)
                    contactsAdapter.getFilter().filter(s);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motionChat.transitionToStart();

            }
        });

        motionChat.addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                editText.setText("");
                chatListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

        view_button.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("Connection_", "AnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("Connection_", "AnimationEnd");
                EventBus.getDefault().post(new ChangeContactsEvent());
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("Connection_", "AnimationRepeat");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        view_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch ((int) (view_button.getMaxFrame() + 0.1)) {


                    default:
                        Log.d("Chat_", "uno");
                        view_button.setMinAndMaxFrame(0, 16);
                        allTitle.setText("Matches");
                        typeContacts = Tools.getString(R.string.match, getContext());
                        break;
                    case 32:
                        Log.d("Chat_", "tres");
                        view_button.setMinAndMaxFrame(32, 49);

                        allTitle.setText("Likes");
                        typeContacts = Tools.getString(R.string.like, getContext());
                        break;
                    case 17:
                        Log.d("Chat_", "dos");
                        view_button.setMinAndMaxFrame(16, 31);
                        allTitle.setText("Todos");
                        typeContacts = Tools.getString(R.string.full, getContext());
                        break;


                }
                view_button.playAnimation();
            }
        });

        EventBus.getDefault().register(this);
        Log.d("Connection", "OnCreate");
        EventBus.getDefault().post(new ChatListEvent());
        EventBus.getDefault().post(new ContactListEvent());
        EventBus.getDefault().post(new ChangeContactsEvent());
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadChatList(ChatListEvent event) {
        Log.d("ChangeContac", "ChatLisRbeneldkl");
        configChatListAdapter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void newChat(NewChatEvent event) {
        configChatListAdapter();

    }

    private void configChatListAdapter() {
        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                chats = Conversations.getConversations();
                Log.d("configChatList", "chats" + chats.size());
                loadChatItems();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatListAdapter = new ChatListAdapter(chatItems, getContext());
                        //chatRecycler.setItemAnimator(chatListAdapter.animator);
                        chatRecycler.setHasFixedSize(false);
                        //chatListAdapter.setHasStableIds(true);
                        SimpleItemAnimator animator = (SimpleItemAnimator) chatRecycler.getItemAnimator();
                        animator.setSupportsChangeAnimations(false);
                        animator.setChangeDuration(0);
                        Log.d("Animator_", "deactivated");
                        WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false, "chatList");
                        linearLayoutManager.setOnStopScroll(chatListScrollListener);
                        chatRecycler.setLayoutManager(linearLayoutManager);
                        chatRecycler.setAdapter(chatListAdapter);
                        chatListAdapter.setOnItemClickListener(chatListItem);
                        Log.d("nasñldmasda", "1111 " + chatItems.size());
                        toggleNoChatLayout(chatItems.size());
                    }
                });
            }
        });

    }

    private void loadChatItems() {
        chatItems.clear();
        for (Chat chat : chats) {
            if (isPersonExist(chat.getUsername()))
                chatItems.add(loadChatItem(chat));
            else
                Conversations.deleteChat(chat.getUsername());
        }
    }

    private boolean isPersonExist(String username) {
        return ContactsManager.getContact(username, getContext()) != null;
    }

    private ChatItem loadChatItem(Chat chat) {
        Msg msg;
        String photo, name, time, lastMessage, msgStatus, status, lastType, personType;
        boolean isMuted, isPending;
        int cantPending;
        msg = chat.getMessages().get(chat.getMessages().size() - 1);
        photo = ContactsManager.getContact(chat.getUsername(), getContext()).getProfilePhotoMedium(getContext());
        personType = ContactsManager.getContact(chat.getUsername(), getContext()).getType();
        name = Tools.getFirstWords(chat.getName(), 1);
        time = getLastMsgDate(new Date(msg.getTime()));
        status = ContactsManager.getContact(chat.getUsername(), getContext()).getStatus();
        lastMessage = msg.getBody();
        msgStatus = msg.getStatus();
        if (msg instanceof AudioMessage) {
            lastMessage = "Audio (" + ((AudioMessage) msg).getDuration() + ")";
        }
        lastType = msg.getType();
        isMuted = chat.isMuted();
        isPending = chat.isPendingReading();
        cantPending = chat.getPendingReading();
        return new ChatItem(name, chat.getUsername(), photo, time, lastMessage, msgStatus, status, lastType, personType, isPending, isMuted, cantPending);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateChatItem(UpdateChatItemEvent event) {
        Log.d("ChAT", "item");
        int pos = event.getPosition();
        if (pos != -1)
            AppHandler.executor.submit(new Runnable() {
                @Override
                public void run() {
                    chatItems.set(pos, loadChatItem(chats.get(pos)));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatListAdapter.notifyItemChanged(pos);
                        }
                    });
                }
            });


    }

    public void sayHiChatItem() {


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateChats(UpdateChatsEvent event) {
        Log.d("nasñldmasda", "1111");
        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                loadChatItems();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatListAdapter.notifyDataSetChanged();
                        Log.d("nasñldmasda", "anja??");
                        toggleNoChatLayout(chatItems.size());
                    }
                });
            }
        });
    }

    private ArrayList<ContactItem> getContactsItems(ArrayList<Contact> contacts) {
        ArrayList<ContactItem> items = new ArrayList<>();

        Log.d("ChangeContac", "processContact");
        Collections.reverse(contacts);
        for (Contact contact : contacts) {
            items.add(new ContactItem(Tools.getFirstWords(contact.getName(), 1), contact.getId(),
                    contact.getProfilePhotoLow(getContext())));
        }
        Log.d("ChangeContac", "getContactsItrm");
        return items;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void configContactAdapter(ContactListEvent event) {
        String textLikes = "";
       /* int likes = ContactsManager.getCantLikes(getContext());
        if (likes > 0) {
            String cantLikes = Tools.getStringFromInt(likes);
            textLikes = "Tienes " + cantLikes + " likes";
        } else {
            textLikes = "Mira a quién le gustas";
        }*/
        //String lastImage = ContactsManager.getLastLikeImage(getContext());
        String finalTextLikes = textLikes;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contactsAdapter = new ContactsAdapter(getContext(), contactsItems, finalTextLikes, "lastImage");
                contactRecycler.setHasFixedSize(true);
                WrapContentLinearLayoutManager linearLayoutManager = (new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false, "contactList"));
                linearLayoutManager.setOnStopScroll(contactsScrollListener);
                contactRecycler.setLayoutManager(linearLayoutManager);
                contactRecycler.setAdapter(contactsAdapter);
                contactsAdapter.setOnItemClickListener(contactLisItem);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateContacts(ChangeContactsEvent event) {
        Log.d("ContactsInternal", "ChangeContac");
        if (layoutCompleted)
            loadContacts();
        else
            pendingLoadContacts = true;

    }

    private void loadContacts() {
        AppHandler.executor.submit(new Runnable() {
            @Override
            public void run() {
                pendingLoadContacts = false;
                contactsItems.clear();
                Log.d("ChangeContac", "clear");
                if (typeContacts.contentEquals(Tools.getString(R.string.match, getContext()))) {
                    contactsItems.addAll(getContactsItems(ContactsManager.getMatches(getContext())));
                } else if (typeContacts.contentEquals(Tools.getString(R.string.like, getContext()))) {
                    contactsItems.addAll(getContactsItems(ContactsManager.getLikes(getContext())));
                } else {
                    contactsItems.addAll(getContactsItems(ContactsManager.getFullList(getContext())));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (contactsAdapter != null) {
                            contactsAdapter.notifyDataSetChanged();

                        }

                    }
                });
            }
        });


    }

    private static String getLastMsgDate(Date lastMsgDate) {
        Date now = new Date();
        String lastMessage;
        String pattern = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String hours = sdf.format(lastMsgDate);

        int dif = now.getDate() - lastMsgDate.getDate();
        if (dif == 0 && lastMsgDate.getMonth() == now.getMonth() && lastMsgDate.getYear() == now.getYear()) {
            lastMessage = hours;
        } else if (dif == 1 || ((dif > -31 && dif < -27) && lastMsgDate.getDate() == 1)) {
            lastMessage = "Ayer";
        } else {
            lastMessage = lastMsgDate.getDate() + "/" + (lastMsgDate.getMonth() + 1) + "/" + (lastMsgDate.getYear() + "").substring(1, 3);
        }
        return lastMessage;
    }

    private void toggleNoChatLayout(int i) {

        Log.d("nalskjdañsda", "wtf "+i);
        if (i == 0) {
            noChatImg.setVisibility(View.VISIBLE);
            noChatText.setVisibility(View.VISIBLE);
        } else {
            noChatImg.setVisibility(View.GONE);
            noChatText.setVisibility(View.GONE);

        }
    }

}
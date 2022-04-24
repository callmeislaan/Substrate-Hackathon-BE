package com.backend.apiserver.utils;

import com.backend.apiserver.bean.request.NotificationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    public static String createToken(String username) throws FirebaseAuthException {
        String customToken = auth.createCustomToken(username);
        return customToken;
    }

    public static void pushNotification(NotificationRequest notificationRequest, String username) {
        DatabaseReference notiReference = database.getReference("notifications");
        notiReference.child(username).push().setValueAsync(notificationRequest);
    }

    public static void createSubscribedChannel(Long requestId, String username, String currentMentor) {
        DatabaseReference subscribedChannelReference = database.getReference("messages/subscribed_channels");
        subscribedChannelReference.child(username).child(String.valueOf(requestId)).child("currentMentor").setValueAsync(currentMentor);
        subscribedChannelReference.child(currentMentor).child(String.valueOf(requestId)).child("currentMentor").setValueAsync(currentMentor);
    }

    public static void deleteSubscribedChannel(Long requestId, String username, String currentMentor) {
        DatabaseReference subscribedChannelReference = database.getReference("messages/subscribed_channels");
        subscribedChannelReference.child(username).child(String.valueOf(requestId)).setValueAsync(null);
        subscribedChannelReference.child(currentMentor).child(String.valueOf(requestId)).setValueAsync(null);
    }
}
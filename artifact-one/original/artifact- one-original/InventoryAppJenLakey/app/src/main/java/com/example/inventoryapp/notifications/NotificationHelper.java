package com.example.inventoryapp.notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.inventoryapp.R;
import com.example.inventoryapp.ui.item.ItemDetailActivity;

/**
 * NotificationHelper
 * ------------------
 * Utility class for sending inventory-related alerts.
 * - Builds and displays zero-stock notifications.
 * - Optionally sends SMS alerts if SEND_SMS permission is granted.
 */
public class NotificationHelper {

    // --- Notification channel constants ---
    private static final String CHANNEL_ID = "inventory_alerts";
    private static final String CHANNEL_NAME = "Inventory Alerts";

    /**
     * Sends a notification when an item reaches zero stock.
     *
     * @param ctx      Application context
     * @param itemId   ID of the item (used for PendingIntent and notification ID)
     * @param itemName Name of the item to display in the notification
     */
    public static void sendZeroStock(Context ctx, int itemId, String itemName) {
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O+ (required)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            nm.createNotificationChannel(channel);
        }

        // Intent to open ItemDetailActivity when notification is tapped
        Intent intent = new Intent(ctx, ItemDetailActivity.class);
        intent.putExtra("item_id", itemId);

        PendingIntent pi = PendingIntent.getActivity(
                ctx,
                itemId, // unique request code per item
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.inventory_alert) // ensure drawable exists
                .setContentTitle("Item out of stock")
                .setContentText(itemName + " has reached quantity 0.")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Show notification (use itemId as unique notification ID)
        nm.notify(itemId, builder.build());
    }

    /**
     * Sends both a notification and an SMS alert when an item reaches zero stock.
     *
     * @param ctx        Application context
     * @param itemId     ID of the item
     * @param itemName   Name of the item
     * @param phoneNumber Destination phone number (should come from User entity)
     */
    public static void sendZeroStockAlert(Context ctx, int itemId, String itemName, String phoneNumber) {
        // Always send the notification
        sendZeroStock(ctx, itemId, itemName);

        // SMS alert if permission granted and phone number is valid
        if (phoneNumber != null && !phoneNumber.isEmpty() &&
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(
                        phoneNumber,
                        null,
                        "Alert: " + itemName + " is out of stock!",
                        null,
                        null
                );
            } catch (Exception e) {
                // Optional: log or handle SMS send failure
                e.printStackTrace();
            }
        }
    }
}

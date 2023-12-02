package com.insightdev.both;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String message = "Sender : " + smsMessage.getDisplayOriginatingAddress()
                    + "Message: " + smsMessage.getMessageBody();
            if (message.contains("PAGOxMOVIL")) {
                String cardNumber = "";
                String amount = "";
                try {
                    if (message.contains("Beneficiario")) {

                        cardNumber = message.substring(message.indexOf("Beneficiario") + 14, message.indexOf("Beneficiario") + 30);
                        amount = message.substring(message.indexOf("Monto") + 7, message.indexOf("CUP") - 1);
                    } else if (message.contains("ha realizado una transferencia a la cuenta")) {

                        cardNumber = message.substring(message.indexOf("cuenta") + 7, message.indexOf("cuenta") + 23);
                        amount = message.substring(message.indexOf(cardNumber + " de") + 20, message.indexOf("CUP") - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!cardNumber.contentEquals(Tools.getCardNumber(context)) || amount.isEmpty())
                    continue;

                if (Double.parseDouble(amount) < Tools.getPremiumPrices(context)[2])
                    continue;
                processPayment(Double.parseDouble(amount), context);
            }
        }
    }

    private void processPayment(double amount, Context context) {
        int[] prices = Tools.getPremiumPrices(context);
        int len = prices.length;
        if (amount > prices[0]*6) {
            int times = ((int)(amount / (prices[0]*6)));
            if (times > 1) {
                AppHandler.changeToPremium(context, 180 * times, false);
                return;
            }
        }
        if (amount == prices[0]*6){
            AppHandler.changeToPremium(context, 180, false);
            return;
        }
        if (amount >= prices[1]*3 && amount < prices[0]*6){
            AppHandler.changeToPremium(context, 90, false);
            return;
        }

        if (amount >= prices[len-1] && amount < prices[1]*3){
            AppHandler.changeToPremium(context, 30, false);
        }

    }

}

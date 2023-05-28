package com.tanphuong.milktea.bill.data;

import com.tanphuong.milktea.bill.data.model.Card;

public final class CardFactory {
    private static Card cardInfo = new Card(
            "TRUONG HONG PHUC",
            "02/27",
            "4665 8491 1086 9547",
            222
    );

    public static Card getCardInfo() {
        return cardInfo;
    }

    public static String getCardSummaryInfo() {
        if (cardInfo == null) {
            return "Thêm thẻ ATM";
        }
        String cardNumber = cardInfo.getCardNumber();
        String summary = cardInfo.getCardNumber().substring(cardNumber.length() - 4, cardNumber.length());
        return "Thẻ ATM **** " + summary;
    }

    public static void changeCardInfo(Card newInfo) {
        cardInfo = newInfo;
    }
}

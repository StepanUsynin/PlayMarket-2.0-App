package com.blockchain.store.playstore;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by samsheff on 06/09/2017.
 */

public class EthereumPrice {

    public BigDecimal wei = BigDecimal.ZERO;

    public EthereumPrice (String price) {
        wei = new BigDecimal(price);
    }

    public String getUnits() {
        if (inEther().compareTo(new BigDecimal(0.0001)) == 1) {
            return "ETH";
        } else if (inGwei().compareTo(new BigDecimal(0.0001)) == 1) {
            return "Gwei";
        } else {
            return "Wei";
        }
    }

    public BigDecimal inWei() {
        return wei;
    }

    public String inWeiString() {
        return wei.toPlainString();
    }

    public BigDecimal inGwei() {
        return wei.divide(new BigDecimal("1000000000.0"));
    }
    public BigDecimal inEther() {
        return inGwei().divide(new BigDecimal("1000000000.0"));
    }

    public String getDisplayPrice() {

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);

        String priceUnit = getUnits();
        if  (priceUnit.equals("ETH")) {
            return df.format(inEther()) + " ETH";
        } else if (priceUnit.equals("Gwei")) {
            return df.format(inGwei()) + " Gwei";
        } else {
            return df.format(inWei()) + " Wei";
        }
    }
}

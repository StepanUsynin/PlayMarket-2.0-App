package com.blockchain.store.playstore;

/**
 * Created by samsheff on 06/09/2017.
 */

public class EthereumPrice {

    public long wei = 0;

    public EthereumPrice (String price) {
        wei = Long.decode(price);
    }

    public String getUnits() {
        if (inEther() > 0.0001) {
            return "ETH";
        } else if (inGwei() > 0.0001) {
            return "Gwei";
        } else {
            return "Wei";
        }
    }

    public long inWei() {
        return wei;
    }
    public double inGwei() {
        return wei / 1000000000.0;
    }
    public double inEther() {
        return inGwei() / 1000000000.0;
    }
}

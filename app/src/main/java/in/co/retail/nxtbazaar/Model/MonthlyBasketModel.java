package in.co.retail.nxtbazaar.Model;

import java.io.Serializable;

/**
 * Created by Niha on 1/15/2016.
 */
public class MonthlyBasketModel implements Serializable {
    private String MonthlyBasketName;
    private String MonthlyBasketId;

    public String getMonthlyBasketName() {
        return this.MonthlyBasketName;
    }

    public void setMonthlyBasketName(String name) {
        this.MonthlyBasketName = name;
    }

    public String getMonthlyBasketId() {
        return this.MonthlyBasketId;
    }

    public void setMonthlyBasketId(String id) {
        this.MonthlyBasketId = id;
    }
}

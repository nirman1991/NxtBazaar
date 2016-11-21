package in.co.retail.nxtbazaar.Model;

/**
 * Created by Niha on 12/5/15.
 */
public class LoggedUser
{
    public static CustomerDetailsModel customer = null;

    public static void init(CustomerDetailsModel model)
    {
        customer = model;
    }
}

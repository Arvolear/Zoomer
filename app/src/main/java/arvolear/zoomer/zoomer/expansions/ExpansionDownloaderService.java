package arvolear.zoomer.zoomer.expansions;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class ExpansionDownloaderService extends DownloaderService
{
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqJLlolFFCk2ALb8eJshy+ZvbQpPTKtI5BbTNMAKCYMPNhqsKb6aIgXiZ31NkG5ZmWKVZ8cY2JJn89L7x+ACFWQPFvoEii1R2bKNMLG16tefHUps+L7ZZ0CrqGl+hK4ks/OAw0/1KIgl0SzuOkP12BnU9mcSL56Iyt0yM1XzDBWuOqsb/2zRVX1gJUC+zLNTjksUPDSlwLSm7vsBL/8TGC4Zq/lK0MNCkN+EZ1ZFM3OgeLa4SuFV/Arvzskfv/BclJj6wq4e52F8OCeZHMvguAB7BrsbBrf3vo5xlG+OT/H7SYa+Eo2PT0q5vBs/OkyA3BonVk2adWEQuoWOCVRuH5wIDAQAB";

    private static final byte[] SALT = new byte[]
            {
                23, -10, 45, 99, 123, -111, -4, -1,
                66, 90, -123, -1, 77, 78, 98, 31, 45,
                87, 11, 8, -2, -8, 19, 72, 55, 66, 77
            };

    @Override
    public String getPublicKey()
    {
        return BASE64_PUBLIC_KEY;
    }

    @Override
    public byte[] getSALT()
    {
        return SALT;
    }

    @Override
    public String getAlarmReceiverClassName()
    {
        return ExpansionAlarmReceiver.class.getName();
    }
}

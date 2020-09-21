package arvolear.zoomer.zoomer.buy;

import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.global_gui.CoinCollector;
import arvolear.zoomer.zoomer.utility.SoundsPlayer;

public class Buyer implements PurchasesUpdatedListener, ConsumeResponseListener
{
    private AppCompatActivity activity;
    private View.OnClickListener controller;
    private SoundsPlayer soundsPlayer;

    private BuyDisplayer buyDisplayer;
    private CoinCollector coinCollector;

    public static final String SKU_10X = "coins_10x";
    public static final String SKU_100X = "coins_100x";

    private BillingClient billingClient;
    private long lastPurchaseClickTime = 0;

    private List<String> skuList;
    private List<SkuDetails> skuDetailsList;

    public Buyer(AppCompatActivity activity, View.OnClickListener controller, SoundsPlayer soundsPlayer, BuyDisplayer buyDisplayer, CoinCollector coinCollector)
    {
        this.activity = activity;
        this.controller = controller;
        this.soundsPlayer = soundsPlayer;

        this.buyDisplayer = buyDisplayer;
        this.coinCollector = coinCollector;

        skuList = new ArrayList<>();
        skuDetailsList = new ArrayList<>();

        skuList.add(SKU_10X);
        skuList.add(SKU_100X);

        setupBillingClient();
    }

    private void setupBillingClient()
    {
        billingClient = BillingClient
                .newBuilder(activity)
                .enablePendingPurchases()
                .setListener(this)
                .build();

        billingClient.startConnection(new BillingClientStateListener()
        {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult)
            {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                {
                    checkCompleted();
                    getAvailableProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected()
            {
                buyDisplayer.setDisconnectedStore();
            }
        });
    }

    private void checkCompleted()
    {
        if (billingClient.isReady())
        {
            Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
            List<Purchase> purchases = result.getPurchasesList();

            if (purchases == null)
            {
                return;
            }

            for (Purchase item : purchases)
            {
                ConsumeParams consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(item.getPurchaseToken())
                        .build();

                billingClient.consumeAsync(consumeParams, Buyer.this);
            }
        }
    }

    private void getAvailableProducts()
    {
        if (billingClient.isReady())
        {
            SkuDetailsParams params = SkuDetailsParams
                    .newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.INAPP)
                    .build();

            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener()
            {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, List<SkuDetails> skuDetailsList)
                {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                    {
                        Buyer.this.skuDetailsList = skuDetailsList;

                        for (SkuDetails skuDetails : skuDetailsList)
                        {
                            BuyElement element = new BuyElement(activity, controller, "assets/textures/all/coin", skuDetails.getSku());

                            String title = skuDetails.getTitle();
                            title = title.substring(0, title.indexOf(" ", title.indexOf(" ") + 1));

                            element.setTitle(title);
                            element.setPrice(skuDetails.getPrice());

                            buyDisplayer.addBuyElement(element);
                        }

                        checkPending();
                    }
                }
            });
        }
    }

    private void checkPending()
    {
        Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        List<Purchase> purchases = result.getPurchasesList();

        if (purchases == null)
        {
            return;
        }

        boolean hasPending = false;

        for (Purchase item : purchases)
        {
            if (item.getPurchaseState() == Purchase.PurchaseState.PENDING)
            {
                hasPending = true;
                break;
            }
        }

        if (hasPending)
        {
            ArrayList<BuyElement> buyElements = buyDisplayer.getElements();

            for (BuyElement element : buyElements)
            {
                element.disable();

                for (Purchase item : purchases)
                {
                    if (element.getSku().equals(item.getSku()) && item.getPurchaseState() == Purchase.PurchaseState.PENDING)
                    {
                        element.setInProgress();
                    }
                }
            }
        }
    }

    public void purchase(String sku)
    {
        if (SystemClock.elapsedRealtime() - lastPurchaseClickTime < 2000)
        {
            return;
        }

        lastPurchaseClickTime = SystemClock.elapsedRealtime();

        ArrayList<BuyElement> buyElements = buyDisplayer.getElements();

        for (BuyElement element : buyElements)
        {
            if (element.getSku().equals(sku))
            {
                element.setInProgress();
            }
            else
            {
                element.disable();
            }
        }

        for (SkuDetails skuDetails : skuDetailsList)
        {
            if (sku.equals(skuDetails.getSku()))
            {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();

                billingClient.launchBillingFlow(activity, flowParams);
                break;
            }
        }
    }

    private void normalize()
    {
        ArrayList<BuyElement> buyElements = buyDisplayer.getElements();

        for (int i = 0; i < buyElements.size(); i++)
        {
            buyElements.get(i).setNormal();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases)
    {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null)
        {
            for (Purchase purchase : purchases)
            {
                handlePurchase(purchase);
            }
        }
        else
        {
            if (purchases != null)
            {
                Toast.makeText(activity, "Please try again later", Toast.LENGTH_SHORT).show();
            }

            normalize();
        }
    }

    private void handlePurchase(Purchase purchase)
    {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
        {
            Toast.makeText(activity, "Successful", Toast.LENGTH_SHORT).show();

            applyPurchase(purchase);
            normalize();

            soundsPlayer.play("assets/sounds/market/buy_item.mp3", false);

            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

            billingClient.consumeAsync(consumeParams, this);
        }
        else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
        {
            Toast.makeText(activity, "Pending...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();

            normalize();
        }
    }

    private void applyPurchase(Purchase purchase)
    {
        Animation jump = AnimationUtils.loadAnimation(activity, R.anim.coin_collector_hidden_add);

        BigInteger coins = new BigInteger(coinCollector.getActualCoins());

        if (purchase.getSku().equals(SKU_10X))
        {
            coinCollector.setActualCoins(coins.multiply(BigInteger.TEN).toString(), jump);
        }
        else if (purchase.getSku().equals(SKU_100X))
        {
            coinCollector.setActualCoins(coins.multiply(BigInteger.TEN).multiply(BigInteger.TEN).toString(), jump);
        }
    }

    @Override
    public void onConsumeResponse(BillingResult billingResult, @NonNull String purchaseToken)
    {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
        {
            // better to reward user here
        }
    }
}

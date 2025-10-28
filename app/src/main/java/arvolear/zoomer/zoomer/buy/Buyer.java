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
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import arvolear.zoomer.zoomer.R;
import arvolear.zoomer.zoomer.global_gui.CoinCollector;
import arvolear.zoomer.zoomer.utility.SoundsPlayer;

public class Buyer implements PurchasesUpdatedListener, ConsumeResponseListener, PurchasesResponseListener
{
    private AppCompatActivity activity;
    private View.OnClickListener controller;
    private SoundsPlayer soundsPlayer;

    private BuyDisplayer buyDisplayer;
    private CoinCollector coinCollector;

    public QueryProductDetailsParams.Product product10x;
    public QueryProductDetailsParams.Product product100x;

    private BillingClient billingClient;
    private long lastPurchaseClickTime = 0;

    private List<QueryProductDetailsParams.Product> productList;
    private List<ProductDetails> productDetailsList;

    public Buyer(AppCompatActivity activity, View.OnClickListener controller, SoundsPlayer soundsPlayer, BuyDisplayer buyDisplayer, CoinCollector coinCollector)
    {
        this.activity = activity;
        this.controller = controller;
        this.soundsPlayer = soundsPlayer;

        this.buyDisplayer = buyDisplayer;
        this.coinCollector = coinCollector;

        productList = new ArrayList<>();
        productDetailsList = new ArrayList<>();

        product10x = QueryProductDetailsParams.Product.newBuilder().setProductType(BillingClient.ProductType.INAPP).setProductId("coins_10x").build();
        product100x = QueryProductDetailsParams.Product.newBuilder().setProductType(BillingClient.ProductType.INAPP).setProductId("coins_100x").build();

        productList.add(product10x);
        productList.add(product100x);

        setupBillingClient();
    }

    private void setupBillingClient()
    {
        PendingPurchasesParams pendingPurchasesParams = PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build();

        billingClient = BillingClient
                .newBuilder(activity)
                .enablePendingPurchases(pendingPurchasesParams)
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
            QueryPurchasesParams params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build();

            billingClient.queryPurchasesAsync(params, this);
        }
    }

    private void getAvailableProducts()
    {
        if (billingClient.isReady())
        {

            QueryProductDetailsParams params = QueryProductDetailsParams
                    .newBuilder()
                    .setProductList(productList)
                    .build();

            billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener()
            {
                @Override
                public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull QueryProductDetailsResult queryProductDetailsResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                    {
                        Buyer.this.productDetailsList = queryProductDetailsResult.getProductDetailsList();

                        for (ProductDetails productDetails : Buyer.this.productDetailsList)
                        {
                            BuyElement element = new BuyElement(activity, controller, "assets/textures/all/coin", productDetails.getProductId());

                            String title = productDetails.getTitle();
                            title = title.substring(0, title.indexOf(" ", title.indexOf(" ") + 1));

                            element.setTitle(title);
                            element.setPrice(productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice());

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
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build();

        billingClient.queryPurchasesAsync(params, this);
    }

    public void purchase(String productId)
    {
        if (SystemClock.elapsedRealtime() - lastPurchaseClickTime < 2000)
        {
            return;
        }

        lastPurchaseClickTime = SystemClock.elapsedRealtime();

        ArrayList<BuyElement> buyElements = buyDisplayer.getElements();

        for (BuyElement element : buyElements)
        {
            if (element.getProductId().equals(productId))
            {
                element.setInProgress();
            }
            else
            {
                element.disable();
            }
        }

        for (ProductDetails productDetails : productDetailsList)
        {
            if (productId.equals(productDetails.getProductId()))
            {
                BillingFlowParams.ProductDetailsParams params =
                        BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(productDetails).build();

                List<BillingFlowParams.ProductDetailsParams> list = new ArrayList<>();
                list.add(params);

                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(list)
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

        if (purchase.getProducts().get(0).equals(product10x.zza()))
        {
            coinCollector.setActualCoins(coins.multiply(BigInteger.TEN).toString(), jump);
        }
        else if (purchase.getProducts().get(0).equals(product100x.zza()))
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

    @Override
    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchases) {
        boolean hasPending = false;

        for (Purchase item : purchases)
        {
            if (item.getPurchaseState() == Purchase.PurchaseState.PENDING)
            {
                hasPending = true;
                break;
            } else if (item.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                ConsumeParams consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(item.getPurchaseToken())
                        .build();

                billingClient.consumeAsync(consumeParams, Buyer.this);
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
                    if (element.getProductId().equals(item.getProducts().get(0)) && item.getPurchaseState() == Purchase.PurchaseState.PENDING)
                    {
                        element.setInProgress();
                    }
                }
            }
        }
    }
}

package com.cho.googlepay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    PurchasesUpdatedListener purchasesUpdatedListener;
    BillingClient billingClient;
    Activity activity = this;


    CardView btnPurchase, btnSubscribe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitBillingClient();
        Init();

    }

    private void Init(){
        btnPurchase = (CardView)findViewById(R.id.card_purchase_item);
        btnSubscribe = (CardView)findViewById(R.id.card_subscribe_item);


        //Event
        btnPurchase.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PurchaseItemActivity.class));
            }
        });
        btnSubscribe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SubscribeActivity.class));
            }
        });
    }
    private void InitBillingClient(){
        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                // To be implemented in a later section.
            }
        };

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
    }

    public void ConnectGoogleplay(View v){
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    CharSequence text = "Hello toast!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    List<String> skuList = new ArrayList<>();
                    skuList.add("item1");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    // Process the result.
                                    Toast.makeText(getApplicationContext(), Integer.valueOf(skuDetailsList.size()).toString(), Toast.LENGTH_LONG).show();
                                    Toast.makeText(getApplicationContext(), "onSkuDetailsResponse", Toast.LENGTH_SHORT).show();
                                    for(SkuDetails skuDetail: skuDetailsList){
                                        Toast.makeText(getApplicationContext(), "for in", Toast.LENGTH_LONG).show();
                                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                                .setSkuDetails(skuDetail)
                                                .build();
                                        int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();

                                        Toast.makeText(getApplicationContext(), Integer.valueOf(responseCode).toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

}
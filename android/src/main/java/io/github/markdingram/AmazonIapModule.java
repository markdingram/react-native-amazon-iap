package io.github.markdingram;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import java9.util.function.Consumer;

public class AmazonIapModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private AmazonIapStore store;

    public AmazonIapModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.store = new AmazonIapStore(reactContext);
    }

    @Override
    public String getName() {
        return "AmazonIap";
    }

    @ReactMethod
    public void getUser(final Promise promise) {
        store.getUser().thenAccept(promiseConsumer(promise));
    }

    @ReactMethod
    public void getPurchaseUpdates(final Promise promise) {
        store.getPurchaseUpdates().thenAccept(promiseConsumer(promise));
    }

    @ReactMethod
    public void getProducts(ReadableArray skus, final Promise promise) {
        store.getProducts(convert(skus)).thenAccept(promiseConsumer(promise));
    }

    @ReactMethod
    public void purchase(String sku, final Promise promise) {
        store.purchase(sku).thenAccept(promiseConsumer(promise));
    }

    @ReactMethod
    public void notifyFulfillment(String receiptId, String fulfillmentResult) {
        store.notifyFulfillment(receiptId, fulfillmentResult);
    }


    private static Consumer<JSONObject> promiseConsumer(final Promise promise) {
        return new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject o) {
                try {
                    Log.d("amazon-iap","promiseConsumer - " + o.toString());
                    promise.resolve(Json.convertJsonToMap(o));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static Set<String> convert(ReadableArray array) {
        Set<String> vals = new HashSet<>();
        for (int i = 0; i < array.size(); i++ ) {
            vals.add(array.getString(i));
        }
        return vals;
    }
}

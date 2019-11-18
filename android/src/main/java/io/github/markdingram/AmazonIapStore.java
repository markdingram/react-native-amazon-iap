package io.github.markdingram;

import android.content.Context;

import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import java9.util.concurrent.CompletableFuture;

public class AmazonIapStore implements PurchasingListener {

    private static final boolean PURCHASE_UPDATES_RESET = false;

    private final Map<RequestId, CompletableFuture<JSONObject>> pending = new HashMap<>();

    public AmazonIapStore(Context context) {
        PurchasingService.registerListener(context, this);
    }

    public CompletableFuture<JSONObject> getUser() {
        synchronized (pending) {
            RequestId requestId = PurchasingService.getUserData();
            CompletableFuture<JSONObject> future = new CompletableFuture<>();
            pending.put(requestId, future);
            return future;
        }
    }

    public CompletableFuture<JSONObject> getProducts(Set<String> productSkus) {
        synchronized (pending) {
            RequestId requestId = PurchasingService.getProductData(productSkus);
            CompletableFuture<JSONObject> future = new CompletableFuture<>();
            pending.put(requestId, future);
            return future;
        }
    }

    public CompletableFuture<JSONObject> refresh() {
        synchronized (pending) {
            RequestId requestId = PurchasingService.getPurchaseUpdates(PURCHASE_UPDATES_RESET);
            CompletableFuture<JSONObject> future = new CompletableFuture<>();
            pending.put(requestId, future);
            return future;
        }
    }

    public CompletableFuture<JSONObject> purchase(String sku) {
        synchronized (pending) {
            RequestId requestId = PurchasingService.purchase(sku);
            CompletableFuture<JSONObject> future = new CompletableFuture<>();
            pending.put(requestId, future);
            return future;
        }
    }

    @Override
    public void onUserDataResponse(UserDataResponse response) {
        synchronized (pending) {
            CompletableFuture<JSONObject> future = pending.remove(response.getRequestId());
            if (future != null) {
                try {
                    JSONObject object = response.toJSON();
                    future.complete(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onProductDataResponse(ProductDataResponse response) {
        synchronized (pending) {
            CompletableFuture<JSONObject> future = pending.remove(response.getRequestId());
            if (future != null) {
                try {
                    JSONObject object = response.toJSON();
                    future.complete(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPurchaseResponse(PurchaseResponse response) {
        synchronized (pending) {
            CompletableFuture<JSONObject> future = pending.remove(response.getRequestId());
            if (future != null) {
                try {
                    JSONObject object = response.toJSON();
                    future.complete(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse response) {
        synchronized (pending) {
            CompletableFuture<JSONObject> future = pending.remove(response.getRequestId());
            if (future != null) {
                try {
                    JSONObject object = response.toJSON();
                    future.complete(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

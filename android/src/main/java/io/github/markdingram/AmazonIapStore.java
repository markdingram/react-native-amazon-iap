package io.github.markdingram;

import android.content.Context;

import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;

import org.json.JSONArray;
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

    public CompletableFuture<JSONObject> getPurchaseUpdates() {
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

    public void notifyFulfillment(String receiptId, String fulfillmentResult) {
        PurchasingService.notifyFulfillment(receiptId, FulfillmentResult.valueOf(fulfillmentResult));
    }

    @Override
    public void onUserDataResponse(UserDataResponse response) {
        synchronized (pending) {
            CompletableFuture<JSONObject> future = pending.remove(response.getRequestId());
            if (future != null) {
                try {
                    JSONObject o = new JSONObject();
                    o.put("requestStatus", response.getRequestStatus());
                    o.put("userData", response.getUserData().toJSON());
                    future.complete(o);
                } catch (JSONException e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
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
                    JSONObject o = new JSONObject();
                    o.put("requestStatus", response.getRequestStatus());

                    JSONArray a = new JSONArray();
                    for (Product pd : response.getProductData().values()) {
                        a.put(pd.toJSON());
                    }
                    o.put("productData", a);
                    o.put("unavailableSkus", response.getUnavailableSkus());

                    future.complete(o);
                } catch (JSONException e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
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
                    future.completeExceptionally(e);
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
                    JSONObject o = new JSONObject();
                    o.put("requestStatus", response.getRequestStatus());
                    o.put("userData", response.getUserData().toJSON());
                    JSONArray a = new JSONArray();
                    for (Receipt r : response.getReceipts()) {
                        a.put(r.toJSON());
                    }
                    o.put("receipts", a);
                    o.put("hasMore", response.hasMore());
                    future.complete(o);
                } catch (JSONException e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        }
    }


}

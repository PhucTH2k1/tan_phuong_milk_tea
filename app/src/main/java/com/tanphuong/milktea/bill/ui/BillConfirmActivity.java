package com.tanphuong.milktea.bill.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.tanphuong.milktea.R;
import com.tanphuong.milktea.authorization.data.UserFactory;
import com.tanphuong.milktea.bill.data.BillUploader;
import com.tanphuong.milktea.bill.data.CardFactory;
import com.tanphuong.milktea.bill.data.MilkTeaOrderFactory;
import com.tanphuong.milktea.bill.data.model.Bill;
import com.tanphuong.milktea.bill.data.model.BillStatus;
import com.tanphuong.milktea.bill.data.model.PaymentMethod;
import com.tanphuong.milktea.bill.ui.adapter.OrderSummaryAdapter;
import com.tanphuong.milktea.bill.util.PaymentUtils;
import com.tanphuong.milktea.databinding.ActivityBillConfirmBinding;
import com.tanphuong.milktea.drink.data.model.MilkTeaOrder;
import com.tanphuong.milktea.shipment.ui.ShipmentMapsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;

public class BillConfirmActivity extends AppCompatActivity {
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private static final int EDIT_CARD_REQUEST_CODE = 137;

    private ActivityBillConfirmBinding binding;
    private BottomSheetBehavior sheetBehavior;
    private OrderSummaryAdapter adapter;
    private PaymentsClient paymentsClient;
    private PaymentMethod paymentMethod = PaymentMethod.CASH;
    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        initializeUi();

        // Thanh toán với GPay
        paymentsClient = PaymentUtils.createPaymentsClient(this);
        possiblyShowGooglePayButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                }

                // Re-enables the Google Pay payment button.
                binding.btnPay.setClickable(true);
                break;
            case EDIT_CARD_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    binding.tvInfoCard.setText(CardFactory.getCardSummaryInfo());
                }
                break;
        }
    }

    private void initializeUi() {
        sheetBehavior = BottomSheetBehavior.from(binding.bsPaymentMethod.bottomSheet);
        hideBottomSheet();
        binding.llPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay();
            }
        });
        binding.bsPaymentMethod.paymentMethodOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                hideBottomSheet();
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    binding.tvInfoCard.setVisibility(View.GONE);
                    binding.btnPay.setEnabled(true);
                    String presentMethod = "";
                    switch (checkedId) {
                        case R.id.option_cash:
                            paymentMethod = PaymentMethod.CASH;
                            presentMethod = "Tiền mặt";
                            break;
                        case R.id.option_atm:
                            paymentMethod = PaymentMethod.ATM;
                            binding.tvInfoCard.setVisibility(View.VISIBLE);
                            binding.tvInfoCard.setText(CardFactory.getCardSummaryInfo());
                            binding.btnPay.setEnabled(CardFactory.getCardInfo() != null);
                            presentMethod = "Thẻ ATM";
                            break;
                        case R.id.option_gpay:
                            paymentMethod = PaymentMethod.G_PAY;
                            presentMethod = "Google Pay";
                            break;
                    }
                    binding.tvPaymentMethodValue.setText(presentMethod);
                }
            }
        });
        binding.tvInfoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BillConfirmActivity.this, EditCardActivity.class);
                startActivityForResult(intent, EDIT_CARD_REQUEST_CODE);
            }
        });

        // Tổng quan đơn hàng
        List<MilkTeaOrder> orders = MilkTeaOrderFactory.showCart();
        adapter = new OrderSummaryAdapter(orders);
        binding.rlOrderSummary.setLayoutManager(new LinearLayoutManager(this));
        binding.rlOrderSummary.setAdapter(adapter);
        int orderPrice = MilkTeaOrderFactory.estimatePrice();
        totalPrice = orderPrice + Bill.SHIP_COST;
        binding.tvPriceTotalSelectProduct.setText(orderPrice + "đ");
        binding.tvBillTotalPayment.setText(totalPrice + "đ");
    }

    private void pay() {
        // Tạo bill
        Bill bill = new Bill();
        bill.setOrders(MilkTeaOrderFactory.showCart());
        bill.setUser(UserFactory.getCurrentUser());
        bill.setShipper(null);
        bill.setPaymentMethod(paymentMethod);
        bill.setStatus(BillStatus.SHIPPER_FINDING);

        // Xoá cart
        MilkTeaOrderFactory.clearCart();

        // Upload Bill
        BillUploader.upload(bill, new BillUploader.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        });

        binding.btnPay.setClickable(false);
        switch (paymentMethod) {
            case CASH:
            case ATM:
                goToShipmentTracking();
                break;
            case G_PAY:
                requestGooglePayment();
                break;
        }
    }

    private void goToShipmentTracking() {
        binding.btnPay.setClickable(true);
        Intent intent = new Intent(BillConfirmActivity.this, ShipmentMapsActivity.class);
        startActivity(intent);
        finish();
    }

    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = PaymentUtils.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this,
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            setGooglePayAvailable(task.getResult());
                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });
    }

    private void setGooglePayAvailable(boolean available) {
        if (available) {
            binding.btnPay.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, R.string.googlepay_status_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {
            return;
        }

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            final String token = tokenizationData.getString("token");
            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");
            Toast.makeText(
                    this, getString(R.string.payments_show_name, billingName),
                    Toast.LENGTH_LONG).show();

            goToShipmentTracking();

            // Logging token string.
            Log.d("Google Pay token: ", token);

        } catch (JSONException e) {
            throw new RuntimeException("The selected garment cannot be parsed from the list of elements");
        }
    }

    private void handleError(int statusCode) {
        Log.e("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

    public void requestGooglePayment() {
        Optional<JSONObject> paymentDataRequestJson = PaymentUtils.getPaymentDataRequest(totalPrice);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }

        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }

    private void showBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void hideBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}
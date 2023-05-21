package com.tanphuong.milktea.authorization.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.tanphuong.milktea.authorization.data.AuthorizationValidator;
import com.tanphuong.milktea.authorization.data.PhoneSignIn;
import com.tanphuong.milktea.authorization.data.SocialSignIn;
import com.tanphuong.milktea.authorization.data.model.UserInfoService;
import com.tanphuong.milktea.databinding.ActivitySignInBinding;
import com.tanphuong.milktea.home.ui.HomeActivity;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 33;
    private static final int FACEBOOK_SIGN_IN_REQUEST_CODE = 47;
    private ActivitySignInBinding binding;
    private BottomSheetBehavior sheetBehavior;
    private PhoneSignIn phoneSignIn;
    private SocialSignIn socialSignIn;
    private String currentVerificationId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        setContentView(binding.getRoot());
        init();
        references();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            signInSuccess(currentUser);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GOOGLE_SIGN_IN_REQUEST_CODE:
                Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                // check condition
                if (signInAccountTask.isSuccessful()) {
                    try {
                        // Initialize sign in account
                        GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                        // Check condition
                        if (googleSignInAccount != null) {
                            // When sign in account is not equal to null initialize auth credential
                            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                            // Check credential
                            FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCredential:success");
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        signInSuccess(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                                        signInFailure();
                                    }
                                }
                            });
                        }
                    } catch (ApiException e) {
                        signInFailure(e.getMessage());
                    }
                }
                break;
            case FACEBOOK_SIGN_IN_REQUEST_CODE:
                break;
        }
    }

    private void signInSuccess(FirebaseUser userInfo) {
        UserInfoService.upload(userInfo, new UserInfoService.OnUploadUserCallback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure() {
                Toast.makeText(SignInActivity.this, "Có lỗi bất thường xảy ra!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInFailure() {
        signInFailure(null);
    }

    private void signInFailure(String errorMessage) {
        String showMessage = errorMessage != null ? errorMessage : "Đăng nhập thất bại, vui lòng thử lại!";
        Toast.makeText(this, showMessage, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        phoneSignIn = new PhoneSignIn(this, new PhoneSignIn.SignInCallback() {
            @Override
            public void onSuccess(FirebaseUser userInfo) {
                hideBottomSheetOtp();
                signInSuccess(userInfo);
            }

            @Override
            public void onFailure(Exception error) {
                hideBottomSheetOtp();
                if (error instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (error instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (error instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }
                signInFailure(error.getMessage());
            }

            @Override
            public void codeSent(String verificationId) {
                currentVerificationId = verificationId;
                showBottomSheetOtp();
            }
        });
        socialSignIn = new SocialSignIn();
    }

    private void references() {
        binding.tvSendPhone.setEnabled(false);
        sheetBehavior = BottomSheetBehavior.from(binding.layoutBottomSheet.bottomSheetOtp);
        hideBottomSheetOtp();
        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (AuthorizationValidator.checkPhoneValid(s.toString())) {
                    binding.tvInvalidPhone.setVisibility(View.INVISIBLE);
                    binding.tvSendPhone.setEnabled(true);
                } else {
                    binding.tvInvalidPhone.setVisibility(View.VISIBLE);
                    binding.tvSendPhone.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.tvSendPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Bắt đầu gửi OTP
                phoneSignIn.startPhoneNumberVerification("+84" + binding.edtPhone.getText());
            }
        });
        binding.layoutBottomSheet.otpView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    phoneSignIn.verifyPhoneNumberWithCode(currentVerificationId, s.toString());
                    binding.layoutBottomSheet.btnCheckOtp.setEnabled(true);
                } else {
                    binding.layoutBottomSheet.btnCheckOtp.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.layoutBottomSheet.btnCheckOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneSignIn.verifyPhoneNumberWithCode(currentVerificationId, binding.layoutBottomSheet.otpView.getText().toString());
            }
        });
        binding.llSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("672860158503-798kkjcheu79n60ibpgkv5u03voafiji.apps.googleusercontent.com")
                        .requestEmail()
                        .build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(SignInActivity.this, googleSignInOptions);
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST_CODE);
            }
        });
    }

    private void showBottomSheetOtp() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
    private void hideBottomSheetOtp() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}
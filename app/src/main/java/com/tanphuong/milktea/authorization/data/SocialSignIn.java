package com.tanphuong.milktea.authorization.data;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;

public class SocialSignIn {
    BeginSignInRequest googleSignInRequest;

    public void signInGoogle() {
        googleSignInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("672860158503-798kkjcheu79n60ibpgkv5u03voafiji.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();
    }
}

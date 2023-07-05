package com.orbaic.miner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class FirebaseDynamicLink extends AppCompatActivity {

    private Uri mInvitationUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_dynamic_link);
    }

    public void createLink() {
        // [START ddl_referral_create_link]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String link = "https://blog.orbaic.com/?invitedby=" + uid;
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://orbaicminer.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.orbaic.miner")
                                .setMinimumVersion(125)
                                .build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl = shortDynamicLink.getShortLink();
                        // ...
                    }
                });
        // [END ddl_referral_create_link]
    }

    public void sendInvitation() {
        // [START ddl_referral_send]
        String referrerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String subject = String.format("%s wants you to play MyExampleGame!", referrerName);
        String invitationLink = mInvitationUrl.toString();
        String msg = "Let's Mining together! Use my referrer link: "
                + invitationLink;
        String msgHtml = String.format("<p>Let's play MyExampleGame together! Use my "
                + "<a href=\"%s\">referrer link</a>!</p>", invitationLink);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        // [END ddl_referral_send]
    }
}
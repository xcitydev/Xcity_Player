package com.xcityprime.myaudioplayer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

public class About_Creator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about__creator);
        AboutBuilder builder = AboutBuilder.with(this)
                .setBriefColor(Color.WHITE)
                .setBackgroundColor(Color.DKGRAY)
                .setAppIcon(R.drawable.picsart2)
                .setAppName(R.string.app_name)
                .setPhoto(R.drawable.img_profile)
                .setCover(R.mipmap.profile_cover)
                .setLinksAnimated(true)
                .setDividerDashGap(13)
                .setName("Chukwuemeka Kingsley")
                .setSubTitle("Web and Mobile Developer")
                .setLinksColumnsCount(3)
                .setBrief("In software, the most beautiful code, the most beautiful function and the most beautiful programs are sometimes not there at all")
                .addGooglePlayStoreLink("https://play.google.com/store/apps/developer?id=Chukwuemeka+Kingsley")
                .addGitHubLink("ChukwuemekaKingsley")
                .addBitbucketLink("user")
                .addFacebookLink("user")
                .addTwitterLink("user")
                .addInstagramLink("Official_xcity")
                .addGooglePlusLink("user")
                .addYoutubeChannelLink("https://www.youtube.com/channel/UCqOqvqgnFMrYvj3zcwf6qyw")
                .addDribbbleLink("user")
                .addLinkedInLink("user")
                .addEmailLink("kingsleychukwuemeka912@gmail.com")
                .addWhatsappLink("X-city Prime", "+2349031657666")
                .addSkypeLink("user")
                .addGoogleLink("user")
                .addAndroidLink("user")
                .addWebsiteLink("site")
                .addFiveStarsAction("play.google.com/store/apps/details?id=com.xcityprime.myaudioplayer")
                .addMoreFromMeAction("Chukwuemeka Kingsley")
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .addUpdateAction()
                .setActionsColumnsCount(2)
                .addFeedbackAction("kingsleychukwuemeka912@gmail.com")
                .addPrivacyPolicyAction("http://www.docracy.com/2d0kis6uc2")
                .addIntroduceAction((Intent) null)
                .addHelpAction((Intent) null)
                .addChangeLogAction((Intent) null)
                .addRemoveAdsAction((Intent) null)
                .addDonateAction((Intent) null)
                .setWrapScrollView(true)
                .setShowAsCard(true);
        AboutView view = builder.build();
        FrameLayout frameLayout = findViewById(R.id.about_creator);
        frameLayout.addView(view);
    }
}
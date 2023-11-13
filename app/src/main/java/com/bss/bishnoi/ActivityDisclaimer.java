package com.bss.bishnoi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityDisclaimer extends AppCompatActivity {

    TextView tvDisclaimer;
    ImageView btnBack;

    String textDisclaimer =
            "यह ऐप बिश्नोई समुदाय और उसके संस्कृतिक धरोहर के संबंध में जानकारी, संसाधन और सेवाओं को प्रदान करने के उद्देश्य से विकसित किया गया है। यह ऐप केवल सूचनात्मक उद्देश्यों के लिए है और इसे पेशेवर सलाह के रूप में नहीं देखा जाना चाहिए। इस ऐप में प्रदान की जाने वाली सामग्री और सेवाएं विशेषज्ञों द्वारा प्राप्त सलाह या प्रोफेशनल राय के बदले नहीं होती हैं।\n" +
            "\n" +
            "**विज्ञापन डिस्क्लेमर:**\n" +
            "इस ऐप में विज्ञापन और प्रमोशनल कंटेंट भी शामिल हो सकता है, जिन्हें तृतीय-पक्ष प्रदाता द्वारा प्रदान किया जाता है। विज्ञापनों का उद्देश्य विशिष्ट उत्पाद, सेवा या सामग्री की प्रचार-प्रसार करना हो सकता है। विज्ञापन उपभोक्ताओं के लिए उपलब्ध किए गए होते हैं जिन्हें उनकी प्राथमिकता आधारित किया जाता है। इन विज्ञापनों की प्राप्ति, प्रदर्शन और अन्तर्गत लिए गए कार्रवाईयों के लिए हम जिम्मेदार नहीं होंगे।\n" +
            "\n" +
            "कृपया ध्यान दें कि आपके द्वारा किसी विज्ञापन पर किए जाने वाले कार्रवाईयों, खरीददारी, या लेन-देन के लिए उद्दीप्त होने से पहले, आपको संबंधित विज्ञापन प्रदाता द्वारा प्रदान किए गए शर्तों, नियमों और गाइडलाइंस को ध्यान से पढ़ना और समझना चाहिए। आपके द्वारा ऐप का उपयोग करते समय किसी भी विज्ञापन या तृतीय-पक्ष प्रदाता के द्वारा प्रदान किए गए किसी भी सामग्री, उत्पाद या सेवा की गुणवत्ता या प्रदाता की विश्वसनीयता के लिए हमारी कोई जिम्मेदारी नहीं होती है।\n" +
            "\n" +
            "यदि आपको ऐप के उपयोग के दौरान विज्ञापनों से संबंधित किसी भी समस्या का सामना करना पड़ता है, तो कृपया हमसे संपर्क करें। हम उपयोगकर्ताओं की सहायता करने के लिए पूरी कोशिश करेंगे, लेकिन हम विज्ञापनों या तृतीय-पक्ष प्रदाताओं द्वारा प्रदान की गई जानकारी या सेवाओं की वैधता, विश्वसनीयता और निष्पक्षता के लिए किसी भी प्रकार की जिम्मेदारी नहीं लेते हैं।\n" +
            "\n" +
            "आपके द्वारा ऐप का उपयोग करते समय किसी भी व्यक्तिगत या सांस्कृतिक नियमों का पालन करना आपकी जिम्मेदारी होता है। हम उपयोगकर्ताओं के बीच सम्बन्धों और सामाजिक या नैतिक उच्चता के लिए भी जिम्मेदार नहीं हैं।";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        setWindow();
        initViews();
    }

    private void initViews() {
        tvDisclaimer = findViewById(R.id.text_disclaimer);
        btnBack = findViewById(R.id.btn_back);

        tvDisclaimer.setText(textDisclaimer);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityDisclaimer.super.onBackPressed();
            }
        });
    }

    private void setWindow() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.saffron));
//        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();

        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }
}
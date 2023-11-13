package com.bss.bishnoi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityAbout extends AppCompatActivity {


    TextView tvAbout;
    ImageView btnBack;

    String textAbout =
            "बिश्नोई ऐप एक ऐसा संसाधन है जो आपको हिंदी तिथि, बिश्नोई संगीत, मासिक अमावस्या जानकारी, नियम और शब्दवाणी प्रदान करता है। हम बिश्नोई समुदाय के लोकप्रिय और महत्वपूर्ण सामाजिक, धार्मिक और सांस्कृतिक ज्ञान को एक साथ एकत्र करते हैं ताकि आप अपने समुदाय के संस्कृति और धरोहर को और अधिक समझ सकें।\n" +
            "\n" +
            "इस ऐप के माध्यम से आप हिंदी तिथियों की जानकारी प्राप्त कर सकते हैं, जो आपको हिंदू पंचांग के अनुसार साल में आने वाले महत्वपूर्ण तिथियों की जानकारी देती है। इसके अलावा, हम बिश्नोई संगीत को सुनने का अवसर प्रदान करते हैं जो आपको आपके समुदाय के संगीत की धुन में ले जाता है।\n" +
            "\n" +
            "मासिक अमावस्या जानकारी के द्वारा आप आने वाले महीने में होने वाली अमावस्या तिथियों को जान सकते हैं जो आपके धार्मिक और आध्यात्मिक आयाम को और अधिक समृद्ध करते हैं।\n" +
            "\n" +
            "ऐप में बिश्नोई समुदाय के नियम और शब्दवाणी को प्रस्तुत किया जाता है, जो आपको आपके समुदाय के मूल्यों, अभिप्रेति और संस्कृति के साथ जुड़ने में मदद करते हैं। यहां आपको बिश्नोई समुदाय के धरोहर और अनमोल संस्कृति के बारे में अधिक जानने का अवसर मिलता है।\n" +
            "\n" +
            "हम बिश्नोई ऐप के माध्यम से आपको अपने समुदाय के साथ जुड़ने का एक सुविधाजनक साधन प्रदान करते हैं और आपको अपने समुदाय की संस्कृति और परंपरा को समर्थन करने के लिए प्रेरित करते हैं। हमारा मकसद बिश्नोई समुदाय के सभी सदस्यों को उनके धार्मिक मूल्यों, संस्कृति और संस्कृति को समझने और समर्थन करने में मदद करना है।\n" +
            "\n" +
            "धन्यवाद, और बिश्नोई समुदाय के साथ जुड़कर हमारे संसाधन से लाभान्वित होने के लिए आपका स्वागत है।";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setWindow();
        initViews();
    }

    private void initViews() {
        tvAbout = findViewById(R.id.text_about);
        btnBack = findViewById(R.id.btn_back);

        tvAbout.setText(textAbout);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityAbout.super.onBackPressed();
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
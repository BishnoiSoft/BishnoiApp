package com.bss.bishnoi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityHelp extends AppCompatActivity {

    TextView tvHelp;
    ImageView btnBack;

    String textHelp =
            "1. मदद और समर्थन:\n" +
            "   यह सेक्शन आपकी सभी समस्याओं का समाधान प्रदान करने के लिए है। यदि आपके मन में किसी प्रकार का प्रश्न है, तो आप यहां दिए गए संपर्क विवरण पर संपर्क करके हमसे संपर्क कर सकते हैं। हम आपकी सहायता के लिए यहां हैं।\n" +
            "\n" +
            "2. कैसे काम करें ट्यूटोरियल:\n" +
            "   यदि आप पहली बार ऐप का उपयोग कर रहे हैं, तो इस सेक्शन में हमने एक विस्तृत ट्यूटोरियल प्रदान किया है जो आपको बताएगा कि ऐप का उपयोग कैसे करें।\n" +
            "\n" +
            "3. संपर्क करें:\n" +
            "   इस सेक्शन में हमने ऐप के विकासकर्ताओं से संपर्क करने के लिए विवरण प्रदान किए हैं। यदि आपके पास कोई सवाल, सुझाव या समस्या है, तो आप हमें इस सेक्शन के माध्यम से संपर्क कर सकते हैं।\n" +
            "\n" +
            "4. अपडेट्स और सूचनाएँ:\n" +
            "   इस सेक्शन में हम आपको ऐप के नवीनतम अपडेट्स और समाचार से अपडेट रखने के लिए जानकारी प्रदान करते हैं।\n" +
            "\n" +
            "आपकी प्रतिक्रिया और शिकायतें हमारे लिए महत्वपूर्ण हैं।\n" +
            "   हम ऐप के विकास में और बेहतरी के लिए आपकी प्रतिक्रिया और सुझाव का स्वागत करते हैं। यदि आपको ऐप का उपयोग करते समय किसी भी प्रकार की समस्या या शिकायत होती है, तो कृपया इस ईमेल पर हमें बताएं: bishnoisoftwaresolutions@gmail.com\n" +
            "\n" +
            "हमारे समुदाय के सदस्यों के साथ मिलकर हम ऐप को और बेहतर बनाने में मदद करने के लिए तत्पर रहते हैं। हमारे यहां आपके शिकायतों का समाधान करने और आपके सुझावों को अपनाने की कोशिश होती है। आपके सहयोग के लिए धन्यवाद!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setWindow();
        initViews();
    }

    private void initViews() {
        tvHelp = findViewById(R.id.text_help);
        btnBack = findViewById(R.id.btn_back);

        tvHelp.setText(textHelp);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelp.super.onBackPressed();
            }
        });
    }

    private void setWindow() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.saffron));

        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();

        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }
}
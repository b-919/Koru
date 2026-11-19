package io.anuke.koru.android;

import android.os.*;
import arc.backend.android.*;
import io.anuke.koru.*;

public class AndroidLauncher extends AndroidApplication{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useGyroscope = false;
        config.useCompass = false;
        config.useWakelock = true;
        config.useImmersiveMode = true;
        config.hideStatusBar = true;

        initialize(new Koru(), config);
    }
}

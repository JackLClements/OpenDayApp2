package uk.ac.uea.framework;

/**
 * Created by Jack L. Clements on 09/10/2015.
 */
import android.app.Activity;

import uk.ac.uea.framework.implementation.AndroidAudio;

public class SoundResource {
    private Audio myAudio;
    private Sound mySound;

    public SoundResource(Activity act){
        myAudio = new AndroidAudio(act);
    }

    public void load(String resourcePath){
        mySound = myAudio.createSound(resourcePath);
    }

    public void play(){
        mySound.play((float)0.9);
    }


    public void stop(){
        mySound.stop();
    }
}

package uet.oop.bomberman.output;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import static uet.oop.bomberman.output.Audio.Loopable.NONELOOP;

public class Audio implements Runnable {
    // Ten file cac audio

    public static final String BACKGROUND_MUSIC = "bg_Gallant_Challenge";
    public static final String PLACE_BOMB = "place_bomb";
    public static final String POWER_UP = "power_up";
    public static final String EXPLOSION = " explosion";
    public static final String DEAD = "dead";

    private Clip clip;

    public enum Loopable{
        NONELOOP, LOOP;
    }
    // Mac dinh ban dau la khong phat lai
    private Loopable _loopable = NONELOOP;

    public Audio(String fileName) {
        String fname = "/audio/" + fileName + ".wav";

        try {
            URL defaultSound = getClass().getResource(fname);
            AudioInputStream sound = AudioSystem.getAudioInputStream(defaultSound);
            clip = AudioSystem.getClip();
            clip.open(sound);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Malformed URL: " + e);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Input Error: " + e);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Line Unavailable Error: " + e);
        }
    }

    public Loopable getLoopable() {
        return _loopable;
    }
    public void setLoopable(Loopable loopable) {
        this._loopable = loopable;
    }

    @Override
    public void run() {
        switch (_loopable) {
            case LOOP:
                this.loop();
                break;
            case NONELOOP:
                this.play();
                break;
        }
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void play() {
        clip.setFramePosition(0);
        clip.start();
    }
    public void stop() {
        clip.stop();
    }
}

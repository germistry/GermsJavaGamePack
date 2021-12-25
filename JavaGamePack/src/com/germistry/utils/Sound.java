package com.germistry.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.*;

public class Sound {

	public static final Sound CLICK = new Sound("/sounds/click4.wav");
	
	private final Set<Clip> clips = Collections.synchronizedSet(new HashSet<>());

    private final AudioFormat format;
    private final byte[] bytes;

    public Sound(String path) {
    	try {
    		AudioInputStream stream = AudioSystem.getAudioInputStream(
                new BufferedInputStream(Sound.class.getResourceAsStream(path)));

            this.format = stream.getFormat();
            this.bytes = stream.readAllBytes();

            for (int i = 0; i < 4; i++) {
                this.createNewClip();
            }
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new Error(e);
        }
    }
    
    private Clip createNewClip() {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(this.format, this.bytes, 0, this.bytes.length);
            clips.add(clip);
            return clip;
        } catch (LineUnavailableException e) {
            throw new Error(e);
        }
    }

    public void play() {
        new Thread(() -> {
            Clip clip = clips.stream()
                .filter(c ->
                    c.getFramePosition() == 0 ||
                        c.getFramePosition() == c.getFrameLength())
                .findFirst()
                .orElseGet(this::createNewClip);

            clip.setFramePosition(0);
            clip.start();
        }).start();
    }
}

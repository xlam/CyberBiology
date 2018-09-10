package ru.cyberbiology.prototype.record;

import ru.cyberbiology.prototype.IBot;

public interface IRecordManager {

    public int getBufferSize();

    public int getFrameSavedCounter();

    public int getFrameSkipSize();

    public boolean isRecording();

    public void startFrame();

    public void startRecording();

    public void stopFrame();

    public boolean stopRecording();

    public void writeBot(IBot bot, int x, int y);

    public boolean haveRecord();

    public void makeSnapShot();

}

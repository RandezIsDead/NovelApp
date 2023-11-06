package com.randez_trying.novel.Views.VoiceRecorder;

public interface OnRecordListener {
    void onStart();
    void onCancel();
    void onFinish(long recordTime,boolean limitReached);
    void onLessThanSecond();
    void onLock();
}
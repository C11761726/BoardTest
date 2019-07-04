package com.example.boardtest;

import android.graphics.SurfaceTexture;

import com.serenegiant.usb.USBMonitor;
import com.serenegiant.widget.UVCCameraTextureView;

import java.io.Serializable;

public class CameraResStruct {
    private USBMonitor.UsbControlBlock ctrlBlock;
    private UVCCameraTextureView mUVCCameraView;

    public CameraResStruct() {
        ctrlBlock = null;
        mUVCCameraView = null;
    }

    public USBMonitor.UsbControlBlock getCtrlBlock() {
        return ctrlBlock;
    }

    public void setCtrlBlock(USBMonitor.UsbControlBlock ctrlBlock) {
        this.ctrlBlock = ctrlBlock;
    }

    public UVCCameraTextureView getmUVCCameraView() {
        return mUVCCameraView;
    }

    public void setmUVCCameraView(UVCCameraTextureView mUVCCameraView) {
        this.mUVCCameraView = mUVCCameraView;
    }
}

package com.serenegiant.common;

import android.graphics.SurfaceTexture;

import com.serenegiant.widget.UVCCameraTextureView;

import java.io.Serializable;

public class UVCStruct implements Serializable {
    UVCCameraTextureView mUVCCameraView;

    public UVCCameraTextureView getmUVCCameraView() {
        return mUVCCameraView;
    }

    public void setmUVCCameraView(UVCCameraTextureView mUVCCameraView) {
        this.mUVCCameraView = mUVCCameraView;
    }
}

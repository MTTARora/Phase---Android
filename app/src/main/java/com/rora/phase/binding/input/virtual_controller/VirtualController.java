/**
 * Created by Karim Mreisi.
 */

package com.rora.phase.binding.input.virtual_controller;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rora.phase.R;
import com.rora.phase.binding.input.ControllerHandler;
import com.rora.phase.preferences.PreferenceConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class VirtualController {
    public static class ControllerInputContext {
        public short inputMap = 0x0000;
        public byte leftTrigger = 0x00;
        public byte rightTrigger = 0x00;
        public short rightStickX = 0x0000;
        public short rightStickY = 0x0000;
        public short leftStickX = 0x0000;
        public short leftStickY = 0x0000;
    }

    public enum ControllerMode {
        Active,
        MoveButtons,
        ResizeButtons,
        HideButtons
    }

    private static final boolean _PRINT_DEBUG_INFORMATION = false;

    private ControllerHandler controllerHandler;
    private Context context = null;

    private FrameLayout frame_layout = null;
    private RelativeLayout relative_layout = null;

    private Timer retransmitTimer;

    ControllerMode currentMode = ControllerMode.Active;
    ControllerInputContext inputContext = new ControllerInputContext();

    //private Button buttonConfigure = null;

    private List<VirtualControllerElement> elements = new ArrayList<>();

    public VirtualController(final ControllerHandler controllerHandler, FrameLayout layout, final Context context) {
        this.controllerHandler = controllerHandler;
        this.frame_layout = layout;
        this.context = context;


        PreferenceConfiguration configuration = PreferenceConfiguration.readPreferences(context);
        currentMode = configuration.getOnscreenController() ? ControllerMode.Active : ControllerMode.HideButtons;

        relative_layout = new RelativeLayout(context);

        frame_layout.addView(relative_layout);

        //buttonConfigure = new Button(context);
        //buttonConfigure.setAlpha(0.25f);
        //buttonConfigure.setFocusable(false);
        //buttonConfigure.setBackgroundResource(R.drawable.ic_settings);
        //buttonConfigure.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        updateControllerMode(currentMode);
        //    }
        //});

    }

    private void setVisibilityAllExceptSettingButton() {
        for (VirtualControllerElement el : elements) {
            el.setVisibility(currentMode == ControllerMode.HideButtons ? View.GONE : View.VISIBLE);
        }
    }

    public void hide() {
        retransmitTimer.cancel();
        relative_layout.setVisibility(View.INVISIBLE);
    }

    public void show() {
        relative_layout.setVisibility(View.VISIBLE);

        // HACK: GFE sometimes discards gamepad packets when they are received
        // very shortly after another. This can be critical if an axis zeroing packet
        // is lost and causes an analog stick to get stuck. To avoid this, we send
        // a gamepad input packet every 100 ms to ensure any loss can be recovered.
        retransmitTimer = new Timer("OSC timer", true);
        retransmitTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendControllerInputContext();
            }
        }, 100, 100);
        setVisibilityAllExceptSettingButton();
    }

    public void removeElements() {
        for (VirtualControllerElement element : elements) {
            relative_layout.removeView(element);
        }
        elements.clear();
    }

    public void setOpacity(int opacity) {
        for (VirtualControllerElement element : elements) {
            element.setOpacity(opacity);
        }
    }


    public void addElement(VirtualControllerElement element, int x, int y, int width, int height) {
        elements.add(element);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams.setMargins(x, y, 0, 0);

        relative_layout.addView(element, layoutParams);
    }

    public List<VirtualControllerElement> getElements() {
        return elements;
    }

    private static final void _DBG(String text) {
        if (_PRINT_DEBUG_INFORMATION) {
            System.out.println("VirtualController: " + text);
        }
    }

    public void refreshLayout() {
        relative_layout.removeAllViews();
        removeElements();

        //DisplayMetrics screen = context.getResources().getDisplayMetrics();

        //int buttonSize = (int)(screen.heightPixels*0.06f);
        //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
        //params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        //params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        //params.leftMargin = 15;
        //params.topMargin = 15;
        //relative_layout.addView(buttonConfigure, params);

        // Start with the default layout
        VirtualControllerConfigurationLoader.createDefaultLayout(this, context);

        // Apply user preferences onto the default layout
        VirtualControllerConfigurationLoader.loadFromPreferences(this, context);
    }

    public ControllerMode getControllerMode() {
        return currentMode;
    }

    public ControllerInputContext getControllerInputContext() {
        return inputContext;
    }

    void sendControllerInputContext() {
        _DBG("INPUT_MAP + " + inputContext.inputMap);
        _DBG("LEFT_TRIGGER " + inputContext.leftTrigger);
        _DBG("RIGHT_TRIGGER " + inputContext.rightTrigger);
        _DBG("LEFT STICK X: " + inputContext.leftStickX + " Y: " + inputContext.leftStickY);
        _DBG("RIGHT STICK X: " + inputContext.rightStickX + " Y: " + inputContext.rightStickY);

        if (controllerHandler != null) {
            controllerHandler.reportOscState(
                    inputContext.inputMap,
                    inputContext.leftStickX,
                    inputContext.leftStickY,
                    inputContext.rightStickX,
                    inputContext.rightStickY,
                    inputContext.leftTrigger,
                    inputContext.rightTrigger
            );
        }
    }

    public void updateControllerMode(ControllerMode mode) {
        currentMode = mode;

        switch (mode) {
            case MoveButtons:
            case ResizeButtons:
                VirtualControllerConfigurationLoader.saveProfile(VirtualController.this, context);
                break;
            case Active:
            case HideButtons:
                VirtualControllerConfigurationLoader.saveControllerOnOff(mode == ControllerMode.Active, context);
                break;
        }
        setVisibilityAllExceptSettingButton();
        relative_layout.invalidate();

        for (VirtualControllerElement element : elements) {
            element.invalidate();
        }
    }
}

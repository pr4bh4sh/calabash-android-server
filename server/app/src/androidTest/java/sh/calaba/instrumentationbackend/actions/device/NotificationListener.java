package sh.calaba.instrumentationbackend.actions.device;


import android.app.UiAutomation;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sh.calaba.instrumentationbackend.InstrumentationBackend;

import static java.lang.System.currentTimeMillis;

/**
 * Created by PrabhashSingh on 31/10/21.
 * ref: https://github.com/appium/appium-uiautomator2-server/pull/37/files#diff-b35e59a59edb422b0d036a54251f78ef57aaa02cf91d77a1cb2ade6116985df0R73
 */

public final class NotificationListener {
    private static List<CharSequence> toastMessages = new ArrayList<CharSequence>();
    private final Listener listener = new Listener();
    private boolean stopLooping = false;
    private final static NotificationListener INSTANCE = new NotificationListener();
    // clears the toastMessage value every 6 seconds
    private final int TOAST_CLEAR_TIMEOUT = 6000;

    private NotificationListener() {
    }

    public static NotificationListener getInstance() {
        return INSTANCE;
    }

    /**
     * Listens for Notification Messages
     */
    public void start() {
        listener.start();
        Log.d("Toast listener", "Started");
    }

    public void stop() {
        Log.d("Toast listener", "Stopped");
        stopLooping = true;
    }

    public static List<CharSequence> getToastMSGs() {
        return toastMessages;
    }


    private class Listener extends Thread {

        private long previousTime = currentTimeMillis();

        public void run() {
            while (true) {
                AccessibilityEvent accessibilityEvent = null;
                toastMessages = init();

                //return true if the AccessibilityEvent type is NOTIFICATION type
                UiAutomation.AccessibilityEventFilter eventFilter = new UiAutomation.AccessibilityEventFilter() {
                    @Override
                    public boolean accept(AccessibilityEvent event) {
                        return event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
                    }
                };
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Not performing any event.
                    }
                };

                try {
                    //wait for AccessibilityEvent filter
                    UiAutomation uiAutomation = (android.app.UiAutomation) invoke(method(UiDevice.class, "getUiAutomation"), InstrumentationBackend.getUiDevice());

                    accessibilityEvent = uiAutomation.executeAndWaitForEvent(runnable /*executable event*/, eventFilter /* event to filter*/, 500 /*time out in ms*/);
                } catch (Exception ignore) {
                }

                if (accessibilityEvent != null) {
                    toastMessages = accessibilityEvent.getText();
                    Log.d("Toast listener", "Text Found: " + toastMessages.toString());
                    previousTime = currentTimeMillis();
                }
                if (stopLooping) {
                    break;
                }
            }
        }

        // Reflection related methods START
        public Object invoke(final Method method, final Object object, final Object... parameters) {
            try {
                return method.invoke(object, parameters);
            } catch (final Exception e) {
                final String msg = String.format("error while invoking method %s on object %s with parameters %s", method, object, Arrays.toString(parameters));
                throw new RuntimeException(msg, e);
            }
        }

        public Method method(final Class clazz, final String methodName, final Class... parameterTypes) {
            try {
                //noinspection unchecked
                final Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (final Exception e) {
                final String msg = String.format("error while getting method %s from class %s with parameter types %s", methodName, clazz, Arrays.toString(parameterTypes));
                throw new RuntimeException(msg, e);
            }
        }

        public Method method(final String className, final String method, final Class... parameterTypes) {
            return method(getClass(className), method, parameterTypes);
        }

        public Class getClass(final String name) {
            try {
                return Class.forName(name);
            } catch (final ClassNotFoundException e) {
                final String msg = String.format("unable to find class %s", name);
                throw new RuntimeException(msg, e);
            }
        }

        public List<CharSequence> init() {
            if (currentTimeMillis() - previousTime > TOAST_CLEAR_TIMEOUT) {
                return new ArrayList<CharSequence>();
            }
            return toastMessages;
        }
        // Reflection related methods END

    }
}
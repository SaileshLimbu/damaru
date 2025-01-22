package com.powersoft.damaruserver.dispatcher;

import android.accessibilityservice.AccessibilityService;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.powersoft.damaruserver.service.DeviceControlService;

import java.util.LinkedList;

/**
 * The type Dispatcher base.
 */
public abstract class DispatcherBase extends InjectBase {

    DispatcherBase dispatcherBase = null;

    @Override
    public boolean injectKeyEvent(KeyEvent keyEvent) {
        DeviceControlService a = DeviceControlService.Companion.getInstance();
        if (a == null) {
            return false;
        }
        int action = keyEvent.getAction();
        int keyCode = keyEvent.getKeyCode();
        if (action == 0) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_HOME: {
                    return a.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                }
                case KeyEvent.KEYCODE_BACK: {
                    return a.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }
                case KeyEvent.KEYCODE_APP_SWITCH: {
                    return a.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                }
                default:
                    return true;
            }

        } else
            return true;
    }


    private AccessibilityNodeInfo m2891a(AccessibilityNodeInfo accessibilityNodeInfo, boolean z, boolean z2) {
        if (accessibilityNodeInfo == null) {
            return null;
        }
        if ((!z || accessibilityNodeInfo.isFocused()) && (!z2 || accessibilityNodeInfo.isEditable())) {
            return accessibilityNodeInfo;
        }
        int childCount = accessibilityNodeInfo.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = accessibilityNodeInfo.getChild(i);
            if (child != null && ((!z || child.isFocused()) && (!z2 || child.isEditable()))) {
                return child;
            }
        }
        for (int i2 = 0; i2 < childCount; i2++) {
            AccessibilityNodeInfo a = m2891a(accessibilityNodeInfo.getChild(i2), z, z2);
            if (a != null) {
                return a;
            }
        }
        return null;
    }

    private void m2894a(LinkedList<AccessibilityNodeInfo> linkedList, AccessibilityNodeInfo accessibilityNodeInfo) {
        if (accessibilityNodeInfo != null) {
            linkedList.add(accessibilityNodeInfo);
        }
    }

    private LinkedList<AccessibilityNodeInfo> m2893a(boolean z) {
        LinkedList<AccessibilityNodeInfo> linkedList = new LinkedList<>();
        DeviceControlService a = DeviceControlService.Companion.getInstance();
        if (a == null) {
            return linkedList;
        }
        AccessibilityNodeInfo findFocus = a.findFocus(1);
        AccessibilityNodeInfo findFocus2 = a.findFocus(2);
        AccessibilityNodeInfo rootInActiveWindow = a.getRootInActiveWindow();
        m2894a(linkedList, findFocus);
        m2894a(linkedList, findFocus2);
        m2894a(linkedList, m2891a(findFocus, true, true));
        m2894a(linkedList, m2891a(findFocus2, true, true));
        if (!z) {
            m2894a(linkedList, m2891a(findFocus, true, false));
            m2894a(linkedList, m2891a(findFocus2, true, false));
        }
        m2894a(linkedList, m2891a(rootInActiveWindow, true, true));
        return linkedList;
    }
}

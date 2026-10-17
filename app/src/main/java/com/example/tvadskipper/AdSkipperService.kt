package com.example.tvadskipper

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AdSkipperService : AccessibilityService() {

    private var lastTriggerTime = 0L
    private val cooldownMillis = 3000L

    private val adKeywords = listOf(
        "廣告", "跳過廣告", "略過廣告", "秒後可跳過", "廣告時間", "Sponsored", "Skip Ad"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            val rootNode = rootInActiveWindow ?: return

            if (containsAdKeyword(rootNode)) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTriggerTime > cooldownMillis) {
                    performGlobalAction(GLOBAL_ACTION_BACK)
                    lastTriggerTime = currentTime
                }
            }
        }
    }

    private fun containsAdKeyword(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false

        val text = node.text?.toString() ?: ""
        val contentDesc = node.contentDescription?.toString() ?: ""

        for (keyword in adKeywords) {
            if (text.contains(keyword, ignoreCase = true) || 
                contentDesc.contains(keyword, ignoreCase = true)) {
                return true
            }
        }

        for (i in 0 until node.childCount) {
            if (containsAdKeyword(node.getChild(i))) {
                return true
            }
        }
        return false
    }

    override fun onInterrupt() {}
}

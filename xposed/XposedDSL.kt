import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedDSL {
    lateinit var param: XC_LoadPackage.LoadPackageParam
    private var targetPackage: String? = null

    /**
     * Set the package name for which hooks should be applied.
     */
    fun packageName(name: String) {
        targetPackage = name
    }

    /**
     * Hook a method with combined support for before, after, and replace hooks.
     */
    fun hookMethod(
        className: String,
        methodName: String,
        vararg parameterTypes: Any,
        init: (Hook.() -> Unit)
    ) {
        if (isTargetPackage()) {
            val hook = Hook()
            hook.init() // Apply user-provided hooks in DSL format

            // Combine before and after hooks in a single method
            XposedHelpers.findAndHookMethod(
                className,
                param.classLoader,
                methodName,
                *parameterTypes,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        hook.beforeCallback?.invoke(param)
                    }

                    override fun afterHookedMethod(param: MethodHookParam) {
                        hook.afterCallback?.invoke(param)
                    }
                }
            )
        }
    }

    /**
     * Hook a constructor with combined support for before and after hooks.
     */
    fun hookConstructor(
        className: String,
        vararg parameterTypes: Any,
        init: (Hook.() -> Unit)
    ) {
        if (isTargetPackage()) {
            val hook = Hook()
            hook.init() // Apply user-provided hooks in DSL format

            // Combine before and after hooks in a single constructor hook
            XposedHelpers.findAndHookConstructor(
                className,
                param.classLoader,
                *parameterTypes,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        hook.beforeCallback?.invoke(param)
                    }

                    override fun afterHookedMethod(param: MethodHookParam) {
                        hook.afterCallback?.invoke(param)
                    }
                }
            )
        }
    }

    /**
     * Replace a method's implementation entirely using [XC_MethodReplacement].
     */
    fun replaceMethod(
        className: String,
        methodName: String,
        vararg parameterTypes: Any,
        callback: (XC_MethodHook.MethodHookParam) -> Any?
    ) {
        if (isTargetPackage()) {
            XposedHelpers.findAndHookMethod(
                className,
                param.classLoader,
                methodName,
                *parameterTypes,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any? {
                        return callback(param)
                    }
                }
            )
        }
    }

    /**
     * Logs a custom message in the Xposed log.
     */
    fun log(message: String) {
        XposedBridge.log("[XposedDSL] $message")
    }


    private fun isTargetPackage(): Boolean {
        return targetPackage == null || param.packageName == targetPackage
    }


    companion object {
        fun xposed(init: XposedDSL.() -> Unit): XposedDSL {
            return XposedDSL().apply(init)
        }
    }

    /**
     * Internal helper class for managing `before` and `after` callbacks.
     */
    class Hook {
        var beforeCallback: ((XC_MethodHook.MethodHookParam) -> Unit)? = null
        var afterCallback: ((XC_MethodHook.MethodHookParam) -> Unit)? = null

        fun before(callback: (XC_MethodHook.MethodHookParam) -> Unit) {
            beforeCallback = callback
        }

        fun after(callback: (XC_MethodHook.MethodHookParam) -> Unit) {
            afterCallback = callback
        }
    }
}
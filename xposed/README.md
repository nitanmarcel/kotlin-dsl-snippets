# Kotlin DSL snippet for XPosed Framework

## Usage

Adapted from https://github.com/rovo89/xposedbridge/wiki/development-tutorial

```kotlin
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        xposed {
            param = lpparam
            packageName(lpparam.packageName)

            hookMethod("com.android.systemui.statusbar.policy.Clock", "updateClock") {
                before {
                    // this will be called before the clock was updated by the original method
                }

                after {
                    // this will be called after the clock was updated by the original method
                }
            }

        }
    }
```



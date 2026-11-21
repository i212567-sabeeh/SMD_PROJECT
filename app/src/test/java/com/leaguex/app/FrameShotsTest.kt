package com.leaguex.app

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.widget.NestedScrollView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leaguex.app.screens.FrameSection
import com.leaguex.app.screens.Frames
import com.leaguex.app.screens.StyleKitActivity
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowDialog
import java.io.File
import java.lang.reflect.Proxy
import kotlin.math.roundToInt

/**
 * Screenshots for the visual check (ruling S7): Robolectric's native graphics draw each screen at 412 x 917dp with
 * 1px = 1dp, the scale of the Figma PNGs in design/screens/shots. Runs only when Gradle gets -Pshots:
 *   -Pshots=<section slug>  that section's frames  -> shots/<slug>/<frame id>.png
 *   -Pshots=all             every section's frames
 *   -Pshots=kit             the Style kit screen's whole scrolling content -> shots/kit/style-kit.png
 * then `node tools/compare.mjs <slug|kit>` writes shots/<slug>/compare.html.
 *
 * A frame is every window it shows, bottom to top: the activity, a sheet's dialog (over its dim), then any popup.
 * The phone's bars are taken as the Figma draws them, a 40dp status bar and a 20dp gesture bar, so the content
 * lines up with the Figma frame.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w412dp-h917dp-mdpi")
class FrameShotsTest {

    private val target = System.getProperty("leaguex.shots").orEmpty()
    private val shotsDir = File(System.getProperty("leaguex.shotsDir") ?: "shots")

    @Test
    fun shoot() {
        assumeTrue("screenshots run only with -Pshots=<section slug|all|kit>", target.isNotEmpty())
        when (target) {
            "kit" -> shootKit()
            "all" -> Frames.sections.forEach(::shootSection)
            else -> shootSection(
                Frames.sections.firstOrNull { it.slug == target }
                    ?: error("-Pshots=$target: not a section; use kit, all or one of ${Frames.sections.map { it.slug }}"),
            )
        }
    }

    private fun shootSection(section: FrameSection) {
        val dir = File(shotsDir, section.slug).apply { mkdirs() }
        withFigmaSystemBars {
            for (frame in section.frames) {
                val launched = FrameHarness.launch(frame)
                save(shootWindows(launched.activity), File(dir, "${frame.id}.png"))
                launched.controller.pause().stop().destroy()
                FrameHarness.idle()
            }
        }
        println("FrameShotsTest: ${section.frames.size} frames -> $dir")
    }

    /** The Style kit's whole content in one image, 412 wide: the screen is made tall enough to show all of it. */
    private fun shootKit() {
        RuntimeEnvironment.setQualifiers("w412dp-h${KIT_HEIGHT_DP}dp-mdpi")
        val kit = Robolectric.buildActivity(StyleKitActivity::class.java).setup().get()
        FrameHarness.idle()
        val content = (findScroll(kit.window.decorView) ?: error("the Style kit has no scroll view")).getChildAt(0)
        check(content.height < KIT_HEIGHT_DP) { "the Style kit is ${content.height}dp tall; raise KIT_HEIGHT_DP" }
        val window = shootWindows(kit)
        val top = IntArray(2).also(content::getLocationInWindow)[1]
        val image = Bitmap.createBitmap(window, 0, top, window.width, content.height)
        save(image, File(File(shotsDir, "kit").apply { mkdirs() }, "style-kit.png"))
        println("FrameShotsTest: Style kit ${image.width} x ${image.height} -> $shotsDir")
    }

    /** Draws every window [activity] shows into one screen-sized bitmap, bottom to top. */
    private fun shootWindows(activity: Activity): Bitmap {
        val decor = activity.window.decorView
        val screen = Bitmap.createBitmap(decor.width, decor.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(screen)
        canvas.drawColor(Color.WHITE)
        // The activity's own window first: a sheet opened in onCreate is added before it, but a phone always shows
        // dialogs above their activity.
        for (root in windowRoots().sortedBy { if (it === decor) 0 else 1 }) {
            // The activity's own decor view has a DecorContext; its dialogs' and popups' roots have the activity's.
            val mine = root === decor || owner(root.context) === activity
            if (!mine || root.visibility != View.VISIBLE || root.width == 0) continue
            val params = root.layoutParams as? WindowManager.LayoutParams
            if (params != null && params.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND != 0) {
                canvas.drawColor(Color.argb((params.dimAmount * 255).roundToInt(), 0, 0, 0))
            }
            val at = IntArray(2).also(root::getLocationOnScreen)
            canvas.drawBitmap(shootWindow(root, windowOf(root, activity)), at[0].toFloat(), at[1].toFloat(), null)
        }
        return screen
    }

    /**
     * One window's root view. Activity and dialog windows go through PixelCopy, which Robolectric renders with its
     * hardware renderer (outline clipping, elevation shadows); anything else, or a failed copy, is drawn in software.
     */
    private fun shootWindow(root: View, window: Window?): Bitmap {
        val bitmap = Bitmap.createBitmap(root.width, root.height, Bitmap.Config.ARGB_8888)
        if (window != null) {
            var result = -1
            PixelCopy.request(window, bitmap, { result = it }, Handler(Looper.getMainLooper()))
            FrameHarness.idle()
            if (result == PixelCopy.SUCCESS) return bitmap
            println("FrameShotsTest: PixelCopy returned $result for $root; drawing it in software")
        }
        root.draw(Canvas(bitmap))
        return bitmap
    }

    private fun windowOf(root: View, activity: Activity): Window? =
        if (root === activity.window.decorView) {
            activity.window
        } else {
            ShadowDialog.getShownDialogs().map(Dialog::getWindow).firstOrNull { it?.decorView === root }
        }

    /** The root view of every window, in the order they were added, which is bottom to top. */
    @Suppress("UNCHECKED_CAST")
    private fun windowRoots(): List<View> {
        val global = Class.forName("android.view.WindowManagerGlobal").getMethod("getInstance").invoke(null)!!
        val views = global.javaClass.getDeclaredField("mViews").apply { isAccessible = true }.get(global)
        return ArrayList(views as List<View>)
    }

    private fun owner(context: Context): Activity? = when (context) {
        is Activity -> context
        is ContextWrapper -> owner(context.baseContext)
        else -> null
    }

    private fun findScroll(view: View): NestedScrollView? = when (view) {
        is NestedScrollView -> view
        is ViewGroup -> (0 until view.childCount).firstNotNullOfOrNull { findScroll(view.getChildAt(it)) }
        else -> null
    }

    private fun save(bitmap: Bitmap, file: File) {
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    /**
     * Runs [block] with Robolectric's simulated system bars set to the Figma's: a 40dp status bar on top and a 20dp
     * gesture bar at the bottom (Robolectric has none by default). Its SystemUi class isn't public, so it's reached
     * by reflection; if that ever fails the shots are taken without bars and say so.
     */
    private fun withFigmaSystemBars(block: () -> Unit) {
        val systemUi = runCatching { Class.forName("org.robolectric.shadows.SystemUi") }.getOrNull()
        val setBehavior = systemUi?.declaredMethods?.firstOrNull { it.name == "setBehavior" }?.apply { isAccessible = true }
        val ui = systemUi?.getDeclaredMethod("systemUiForDefaultDisplay")?.apply { isAccessible = true }?.invoke(null)
        if (systemUi == null || setBehavior == null || ui == null) {
            println("FrameShotsTest: no simulated system bars in this Robolectric; content starts at the top")
            return block()
        }
        val density = RuntimeEnvironment.getApplication().resources.displayMetrics.density
        val (statusType, navigationType) = setBehavior.parameterTypes
        setBehavior.invoke(ui, bar(statusType, "TOP", (40 * density).roundToInt()), bar(navigationType, "BOTTOM", (20 * density).roundToInt()))
        try {
            block()
        } finally {
            val none = { name: String -> systemUi.getDeclaredField(name).apply { isAccessible = true }.get(null) }
            setBehavior.invoke(ui, none("NO_STATUS_BAR"), none("NO_NAVIGATION_BAR"))
        }
    }

    /** A Robolectric system-bar behaviour: a bar of [size] px on [side]. */
    private fun bar(behavior: Class<*>, side: String, size: Int): Any {
        val sideClass = Class.forName("org.robolectric.shadows.SystemUi\$SystemBar\$Side")
        val sideValue = sideClass.enumConstants.orEmpty().first { (it as Enum<*>).name == side }
        return Proxy.newProxyInstance(behavior.classLoader, arrayOf(behavior)) { proxy, method, args ->
            when (method.name) {
                "calculateSide" -> sideValue
                "calculateSize" -> size
                "equals" -> proxy === args?.firstOrNull()
                "hashCode" -> System.identityHashCode(proxy)
                else -> "Figma $side bar, $size px"
            }
        }
    }

    private companion object {
        /** Taller than the Style kit's content, so the whole of it is on screen at once. */
        const val KIT_HEIGHT_DP = 10000
    }
}

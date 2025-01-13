@file:Suppress("FunctionName")

import net.miginfocom.swing.MigLayout
import javax.swing.JComponent
import javax.swing.JPanel

/** A DSL wrapper for configuring component constraints in MigLayout. */
class ComponentConstraints {
    /** List of constraint strings for the component or layout. */
    private val constraints = mutableListOf<String>()

    /** The Swing component being configured with constraints. */
    var component: JComponent? = null

    /** Adds a wrap constraint to move to the next row. */
    fun wrap() { constraints.add("wrap") }

    /** Makes the component grow and push in both directions. */
    fun grow() { constraints.add("grow, push") }

    /** Makes the component grow horizontally. */
    fun growX() { constraints.add("growx") }

    /** Makes the component grow vertically. */
    fun growY() { constraints.add("growy") }

    /** Makes the component push in both directions. */
    fun push() { constraints.add("push") }

    /** Sets component gaps with optional vertical gap. */
    fun gap(x: Int, y: Int = 0) {
        if (y == 0) constraints.add("gapx $x")
        else constraints.add("gap $x $y")
    }

    /** Sets cell spanning for columns and rows. */
    fun span(cols: Int = 0, rows: Int = 0) {
        when {
            cols > 0 && rows > 0 -> constraints.add("span $cols $rows")
            cols > 0 -> constraints.add("spanx $cols")
            rows > 0 -> constraints.add("spany $rows")
        }
    }

    /** Places the component in a specific grid cell. */
    fun cell(x: Int, y: Int) { constraints.add("cell $x $y") }

    /** Sets component width constraints with optional preferred and maximum values. */
    fun width(min: Int, preferred: Int? = null, max: Int? = null) {
        constraints.add(buildString {
            append("width $min")
            if (preferred != null) {
                append(":$preferred")
                if (max != null) {
                    append(":$max")
                }
            }
        })
    }

    /** Sets component height constraints with optional preferred and maximum values. */
    fun height(min: Int, preferred: Int? = null, max: Int? = null) {
        constraints.add(buildString {
            append("height $min")
            if (preferred != null) {
                append(":$preferred")
                if (max != null) {
                    append(":$max")
                }
            }
        })
    }

    /** Splits the current cell to contain multiple components. */
    fun split(count: Int) { constraints.add("split $count") }

    /** Builds the final constraint string by joining all constraints. */
    internal fun build() = constraints.joinToString(", ")
}

/** Provides a scope for configuring a MigLayout panel. */
class MigLayoutDSL {
    /** The underlying Swing panel with MigLayout. */
    private val panel = JPanel(MigLayout())

    /** Layout constraints for the entire panel. */
    private var layoutConstraints = ""

    /** Column constraints for the panel. */
    private var columnConstraints = ""

    /** Row constraints for the panel. */
    private var rowConstraints = ""

    /** Adds a component to the panel with specified constraints. */
    fun add(init: ComponentConstraints.() -> Unit) {
        val constraints = ComponentConstraints().apply(init)
        constraints.component?.let { component ->
            panel.add(component, constraints.build())
        }
    }

    /** Sets layout constraints for the entire panel. */
    fun layout(init: ComponentConstraints.() -> Unit) {
        layoutConstraints = ComponentConstraints().apply(init).build()
        updateLayout()
    }

    /** Sets column constraints for the panel. */
    fun columns(init: ComponentConstraints.() -> Unit) {
        columnConstraints = ComponentConstraints().apply(init).build()
        updateLayout()
    }

    /** Sets row constraints for the panel. */
    fun rows(init: ComponentConstraints.() -> Unit) {
        rowConstraints = ComponentConstraints().apply(init).build()
        updateLayout()
    }

    /** Updates the MigLayout with current constraints. */
    private fun updateLayout() {
        (panel.layout as MigLayout).setLayoutConstraints(layoutConstraints)
        (panel.layout as MigLayout).setColumnConstraints(columnConstraints)
        (panel.layout as MigLayout).setRowConstraints(rowConstraints)
    }

    /** Returns the configured JPanel. */
    internal fun build(): JPanel = panel

    companion object {
        /** Creates a new JPanel with MigLayout using the provided configuration scope. */
        fun MigPanel(init: MigLayoutDSL.() -> Unit): JPanel {
            return MigLayoutDSL().apply(init).build()
        }
    }
}

# Kotlin DSL snippet for MigLayout (Swing)

## Usage

```
fun main() {
    SwingUtilities.invokeLater {
        val panel = MigPanel {
            layout {
                gap(5, 10)
            }

            columns {
                grow()
            }

            rows {
                push()
            }

            add {
                component = JButton("Click Me")
                growX()
                wrap()
            }

            add {
                component = JTextField()
                width(100, 200, 300)
                span(2)
            }
        }

        val frame = JFrame("Example")
        frame.add(panel)
        frame.size = Dimension(800, 600)
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}
```
import com.nodil.algoquant.core.data.Symbols
import com.nodil.algoquant.core.tester.Tester
import com.nodil.algoquant.strategies.pinbar.PinbarSettings
import com.nodil.algoquant.strategies.pinbar.PinbarStrategy
import kotlin.system.measureTimeMillis

suspend fun main() {
    val time = measureTimeMillis {
        Tester().multipleTest(
            PinbarSettings.generate(),
            PinbarStrategy::class.java,
            "d",
            "5min_60days",
            Symbols.MAIN.toTypedArray(),
        )
    }
    println(time)



}
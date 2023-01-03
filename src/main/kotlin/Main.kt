import com.nodil.algoquant.core.data.Symbols
import com.nodil.algoquant.core.tester.Tester
import com.nodil.algoquant.strategies.pinbar.PinbarSettings
import com.nodil.algoquant.strategies.pinbar.PinbarStrategy
import com.nodil.algoquant.strategies.pinbar.volume.PinbarVolumeSettings
import com.nodil.algoquant.strategies.pinbar.volume.PinbarVolumeStrategy
import kotlin.system.measureTimeMillis

suspend fun main() {
    val time = measureTimeMillis {
        Tester().multipleTest(
            PinbarVolumeSettings.generate(),
            PinbarVolumeStrategy::class.java,
            "d",
            "3min_150days",
            Symbols.ALL.slice(0..40).toTypedArray(),
            false,
            100
        )
    }
    println(time)



}
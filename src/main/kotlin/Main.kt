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
            "1min_90days",
            arrayOf("ADAUSDT","OMGUSDT","ETCUSDT","LTCUSDT","LINKUSDT","ETHUSDT","XTZUSDT","ZECUSDT","ONTUSDT","BNBUSDT","NEOUSDT","ATOMUSDT","VETUSDT","IOTAUSDT","ZILUSDT","BATUSDT","COMPUSDT","ZRXUSDT"),//Symbols.ALL.slice(0..30).toTypedArray(),
            false,
            50
        )
    }
    println(time)



}
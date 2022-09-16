import com.algoquant.core.data.DumpLoader
import com.algoquant.core.tester.Tester
import com.algoquant.strategies.RsiDivergenceStrategy

fun main(args: Array<String>) {
    val tmp = Tester().createBackTest(RsiDivergenceStrategy.generate(), "BTC", DumpLoader.loadFromJson("15min", "XRPUSDT"))

}
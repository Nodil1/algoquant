import com.nodil.algoquant.core.data.DumpLoader
import com.nodil.algoquant.core.data.Symbols
import com.nodil.algoquant.core.tester.Tester
import com.nodil.algoquant.strategies.RsiDivergenceSettings
import com.nodil.algoquant.strategies.RsiDivergenceStrategy
import com.nodil.algoquant.strategies.extremumCrossMA.ExtremumCrossMASettings
import com.nodil.algoquant.strategies.extremumCrossMA.ExtremumCrossMAStrategy
import com.nodil.algoquant.strategies.harmonicPatterns.HarmonicPatternSettings
import com.nodil.algoquant.strategies.harmonicPatterns.HarmonicStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext

suspend fun main(args: Array<String>) {

    Tester().multipleTest(
        RsiDivergenceSettings.generate(),
        RsiDivergenceStrategy::class.java,
        "d",
        "1min_15days",
        Symbols.MAIN.toTypedArray(),
    )

/*    val jobs = mutableListOf<Job>()

    val context = newFixedThreadPoolContext(1, "co")
    Symbols.MAIN.onEach {
        val job = CoroutineScope(context).launch {
            val tmp =
                Tester().createBackTest(
                    ExtremumCrossMAStrategy.generate(),
                    it,
                    DumpLoader.loadFromJson("5min_60day", it)
                )
            tmp.printBest()
        }
        jobs.add(job)

    }

    for (i in jobs.indices) {
        jobs[i].join()
    }*/


}
import com.nodil.algoquant.core.data.Symbols
import com.nodil.algoquant.core.managers.deal.TrailStopManager
import com.nodil.algoquant.core.tester.Tester
import com.nodil.algoquant.strategies.RsiDivergenceStrategy

suspend fun main(args: Array<String>) {

    val dealManagers = arrayOf(
        TrailStopManager(6),
    )
    Tester().multipleTest(
        RsiDivergenceStrategy.generate(),
        "d",
        "5min_60day",
        Symbols.ALL.toTypedArray(),
    )

/*    val jobs = mutableListOf<Job>()

    val context = newFixedThreadPoolContext(1, "co")
    Symbols.MAIN.onEach {
        val job = CoroutineScope(context).launch {
            val tmp =
                Tester().createBackTest(RsiDivergenceStrategy.generate(), it, DumpLoader.loadFromJson("5min_30day", it))
            tmp.printBest()
        }
        jobs.add(job)

    }

    for (i in jobs.indices) {
        jobs[i].join()
    }*/


}
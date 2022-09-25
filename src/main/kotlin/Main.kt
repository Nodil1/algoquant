import com.algoquant.core.data.Symbols
import com.algoquant.core.managers.deal.TrailStopManager
import com.algoquant.core.tester.Tester
import com.algoquant.strategies.RsiDivergenceStrategy
import kotlinx.coroutines.Job

suspend fun main(args: Array<String>) {
    val dealManagers = arrayOf(
        TrailStopManager(2),
    )
    Tester().multipleTest(
        RsiDivergenceStrategy.generate(),
        "d",
        "5min_60days",
        Symbols.MAIN.toTypedArray(),
        dealManagers
    )

    val jobs = mutableListOf<Job>()

 /*   val context = newFixedThreadPoolContext(1, "co")
    Symbols.MAIN.onEach {
        val job = CoroutineScope(context).launch {
            val tmp = Tester().createBackTest(RsiDivergenceStrategy.generate(), it, DumpLoader.loadFromJson("15min", it))
            tmp.printBest()
        }
        jobs.add(job)

    }

    for (i in jobs.indices){
        jobs[i].join()
    }*/




}
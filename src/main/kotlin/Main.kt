import com.algoquant.core.data.DumpLoader
import com.algoquant.core.data.Symbols
import com.algoquant.core.tester.Tester
import com.algoquant.strategies.RsiDivergenceStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext

suspend fun main(args: Array<String>) {

    Tester().multipleTest(
        RsiDivergenceStrategy.generate(),
        "d",
        "30min".toString(),
        Symbols.MAIN.toTypedArray()
    )

/*       val jobs = mutableListOf<Job>()

       val context = newFixedThreadPoolContext(1, "co")
       Symbols.MAIN.onEach {
           val job = CoroutineScope(context).launch {
               val tmp = Tester().createBackTest(RsiDivergenceStrategy.generate(), it, DumpLoader.loadFromJson("3min", it))
               tmp.printBest()
           }
           jobs.add(job)

       }

       for (i in jobs.indices){
           jobs[i].join()
       }*/



}
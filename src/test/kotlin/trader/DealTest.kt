package trader

import com.nodil.algoquant.core.bars.Bar
import com.nodil.algoquant.core.trader.Deal
import com.nodil.algoquant.core.trader.DealSide
import com.nodil.algoquant.core.trader.Target
import org.junit.jupiter.api.Test

class DealTest {
    @Test
    fun test(){
        val deal = Deal(
            Bar(10.0, 11.0, 13.0, 9.0, 100.0, 100, 1111L, 2222L),
            DealSide.LONG,
            100.0,
            0.04,
            arrayOf(com.nodil.algoquant.core.trader.Target())
        )
    }
}
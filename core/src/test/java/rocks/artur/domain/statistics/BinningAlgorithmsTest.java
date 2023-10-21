package rocks.artur.domain.statistics;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BinningAlgorithmsTest {

    @Test
    void runBinningTest() {


        List<Float> list = Arrays.asList(1f,2f,3f,4f,5f,6f,7f,8f,9f,10f);
        List<PropertyValueStatistic> propertyValueStatistics = BinningAlgorithms.runBinning(list);

        Assert.assertEquals(6, propertyValueStatistics.size());
    }


    @Test
    void binSizeTest() {

        int binSize = BinningAlgorithms.getBinSize(10);
        Assert.assertEquals(5, binSize);
    }
}
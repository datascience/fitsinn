package rocks.artur.domain.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinningAlgorithms {

    public static List<PropertyValueStatistic> runBinning(List<Float> floats) {
        if (floats.size() == 0) {
            return new ArrayList<>();
        }
        int binSize = getBinSize(floats.size());
        float smallest = floats.get(0);
        float largest = floats.get(floats.size()-1);

        float binWidth = (largest - smallest) / binSize;

        Map<Integer, List<Float>> bins = new HashMap<>();

        for (float aFloat : floats) {
            int binId = (int) Math.floor ((aFloat - smallest) / binWidth);

            if (bins.containsKey(binId)) {
                bins.get(binId).add(aFloat);
            } else {
                List<Float> tmp = new ArrayList<>();
                tmp.add(aFloat);
                bins.put(binId, tmp);
            }
        }

        List<PropertyValueStatistic> result = new ArrayList<>();

        for (List<Float> floatList : bins.values()) {
            int avg = (int) floatList.stream().mapToDouble(d -> d).average().orElse(0);
            result.add(new PropertyValueStatistic((long) floatList.size(), String.valueOf(avg)));
        }


        return result;
    }

    static int getBinSize(int size) {
        return (int) Math.ceil(1f + Math.log(size) / Math.log(2));  //Sturge's rule
    }


}

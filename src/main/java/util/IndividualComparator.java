package util;

import algorithm.Individual;
import algorithm.Selection;

import java.util.Comparator;

public class IndividualComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual o1, Individual o2) {
        double difference = o1.getFitness() - o2.getFitness();
        return calcReturnValue(difference);
    }

    public static int calcReturnValue(double difference) {
        if (difference == 0) {
            return 0;
        } else if (difference > 0) {
            return 1;
        } else {
            return -1;
        }
    }

}

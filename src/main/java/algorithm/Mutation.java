package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.MutationSettings;
import util.MutationType;

import java.util.Collections;
import java.util.List;

import static util.RandomUtils.*;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class Mutation {

    private static Logger logger = LoggerFactory.getLogger(Problem.class);

    private MutationSettings settings = new MutationSettings();

    public void init(String[] params) {
        settings.init(params);
    }

    public void mutate(List<Individual> individuals) {
        logger.info(settings.toString());
        int size = individuals.get(0).getDimension();
        for (Individual individual : individuals) {
            if (isRunMutation()) {
                logger.debug("Запускаем мутацию индивида: " + individual.toString());
                int index0 = getRandomIndexExclude(null, size);
                int index1 = getRandomIndexExclude(null, size);
                if (index0 != index1) {
                    Collections.swap(individual.getPhenotype(), index0, index1);
                }
                logger.debug("Полученный индивид: " + individual.toString());
            }
        }
    }

    public boolean isRunMutation() {
        if (settings.getMutationType() == MutationType.TYPICAL) {
            return true;
        } else {
            return settings.getMutationProbability() >= random.nextDouble() ? true : false;
        }
    }

    public MutationSettings getSettings() {
        return settings;
    }

}
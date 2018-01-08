package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Population {

    private static Logger logger = LoggerFactory.getLogger(Population.class);

    private List<Individual> parents;
    private List<Individual> individuals;
    private Individual bestIndividual;
    private Integer individualsAmount;

    public void init(Integer individualDimension) {
        logger.info("Ининициализация популяции. Начало");
        individuals = new ArrayList<>();
        for (int i = 0; i < individualsAmount; i++) {
            individuals.add(Individual.createIndividual(individualDimension));
        }
        calcSuitability();
        findBest();
        logger.info("Ининициализация популяции. Окончание");
    }

    public void calcSuitability() {
        logger.info("Подсчёт значения функции пригодности в популяции. Начало");
        for (Individual individual : individuals) {
            individual.calcSuitability();
        }
        logger.info("Подсчёт значения функции пригодности в популяции. Окончание");
    }

    public Individual getBest(List<Individual> individuals) {
        int i = 0;
        int index = 0;
        Double bestSuitability = 0d;
        for (Individual individual : individuals) {
            if (individual.getSuitability() > bestSuitability) {
                bestSuitability = individual.getSuitability();
                index = i;
            }
            i++;
        }
        return individuals.get(index);
    }

    public void findBest() {
        logger.info("Выбор лучшего индивида в популяции. Начало");
        Individual best = getBest(individuals);
        // первое поколение - просто берём лучшего
        if (bestIndividual == null) {
            bestIndividual = Individual.clone(best);
        } else {
            // для всех остальных сравниваем пригодность
            if (bestIndividual.getSuitability() < best.getSuitability()) {
                logger.debug("Заменяем предыдущего лучшего индивида: " + bestIndividual.toString());
                logger.debug("Пригодность данного индивида: " + bestIndividual.getSuitability());
                logger.debug("Значение оптимизируемой функции для данного индивида: " + bestIndividual.getObjectiveFunctionValue());
                bestIndividual = Individual.clone(best);
            } else {
                logger.debug("Пригодность лучшего индивида в популяции: " + best.getSuitability());
                logger.debug("Значение оптимизируемой функции для данного индивида: " + best.getObjectiveFunctionValue());
                logger.debug("Сам индивид: " + best.toString());
                logger.debug("Он хуже предыдущего, замену не производим");
            }
        }

        logger.info("Лучший индивид: " + bestIndividual.toString());
        logger.info("Пригодность данного индивида: " + bestIndividual.getSuitability());
        logger.info("Значение оптимизируемой функции для данного индивида: " + bestIndividual.getObjectiveFunctionValue());
        logger.info("Выбор лучшего индивида в популяции. Окончание");
    }

    public void selection(Selection selection) {
        logger.info("Селекция в популяции. Начало");
        parents = selection.select(individuals, individualsAmount);
        logger.info("Селекция в популяции. Окончание");
    }

    public void recombination(Recombination recombination) {
        logger.info("Рекомбинация в популяции. Начало");
        individuals = recombination.recombine(parents, individualsAmount);
        logger.info("Рекомбинация в популяции. Окончание");
    }

    public void mutation(Mutation mutation) {
        logger.info("Мутация в популяции. Начало");
        mutation.mutate(individuals);
        logger.info("Мутация в популяции. Окончание");
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public void setIndividuals(List<Individual> individuals) {
        this.individuals = individuals;
    }

    public Individual getBestIndividual() {
        return bestIndividual;
    }

    public void setBestIndividual(Individual bestIndividual) {
        this.bestIndividual = bestIndividual;
    }

    public Integer getIndividualsAmount() {
        return individualsAmount;
    }

    public void setIndividualsAmount(Integer individualsAmount) {
        this.individualsAmount = individualsAmount;
    }

}

package algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SelectionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class Population {

    private static Logger logger = LoggerFactory.getLogger(Problem.class);

    private List<Individual> parents;
    private List<Individual> individuals;
    private Individual bestIndividual;
    private Selection selection;
    private Recombination recombination;
    private Integer individualsAmount;

    public void initialization(Settings settings, Integer individualDimension) {
        logger.info("Ининициализация популяции. Начало");
        this.individualsAmount = settings.getIndividualsAmount();
        this.selection = settings.getSelection();
        this.recombination = settings.getRecombination();
        this.individualsAmount = settings.getIndividualsAmount();
        this.selection = settings.getSelection();
        this.recombination = settings.getRecombination();
        individuals = new ArrayList<>();
        for (int i = 0; i < individualsAmount; i++) {
            individuals.add(Individual.createIndividual(individualDimension));
        }
        logger.info("Ининициализация популяции. Окончание");
    }

    public void calcSuitability() {
        logger.info("Подсчёт значения функции пригодности в популяции. Начало");
        for (Individual individual : individuals) {
            individual.calcSuitability();
        }
        logger.info("Подсчёт значения функции пригодности в популяции. Окончание");
    }

    public void findBest() {
        logger.info("Выбор лучшего индивида в популяции. Начало");
        int index = Individual.getIndexOfBest(individuals);
        bestIndividual = Individual.clone(individuals.get(index));
        logger.info("Полученный индивид: " + bestIndividual.toString());
        logger.info("Пригодность данного индивида: " + bestIndividual.getSuitability());
        logger.info("Выбор лучшего индивида в популяции. Окончание");
    }

    public void selection() {
        logger.info("Селекция в популяции. Начало");
        parents = selection.selection(individuals, individualsAmount);
        logger.info("Селекция в популяции. Окончание");
    }

    public void recombination() {
        logger.info("Рекомбинация в популяции. Начало");
        individuals = recombination.recombination(parents, individualsAmount);
        logger.info("Рекомбинация в популяции. Окончание");
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

    public Selection getSelection() {
        return selection;
    }

    public void setSelection(Selection selection) {
        this.selection = selection;
    }

    public Integer getIndividualsAmount() {
        return individualsAmount;
    }

    public void setIndividualsAmount(Integer individualsAmount) {
        this.individualsAmount = individualsAmount;
    }
}

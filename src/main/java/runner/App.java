package runner;

import algorithm.Population;
import algorithm.Problem;
import algorithm.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class App {

    static Logger logger = LoggerFactory.getLogger(Problem.class);

    public static void main(String[] args) {
        Problem problem = Problem.getInstance();
        Settings settings = new Settings();
        Population population = new Population();
        try {
            problem.init("C:\\Users\\Коваленко Никита\\Desktop\\problem.txt");
            settings.init("C:\\Users\\Коваленко Никита\\Desktop\\settings.txt");
            population.initialization(settings, problem.getDimension());
            population.calcSuitability();
            population.findBest();
            population.selection();
            population.recombination();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

}

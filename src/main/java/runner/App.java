package runner;

import algorithm.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class App {

    static Logger logger = LoggerFactory.getLogger(App.class);

    // при необходимости передавать в параметрах
    public static final String PATH = "C:\\Work\\projects\\GA-on-permutations\\";

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    List<String> result = new ArrayList<>();
                    for (int k = 0; k <= 99; k++) {
                        Algorithm algorithm = new Algorithm();
                        algorithm.init(PATH + "problem" + i + ".txt",
                                PATH + "settings" + j + ".txt",
                                PATH);
                        algorithm.process();
                        result.add(String.valueOf(algorithm.getPopulation().getBestIndividual().getObjectiveFunctionValue()));
                    }
                    Files.write(Paths.get(PATH + "result-problem" + i + "-settings" + j + ".txt"), result);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

}

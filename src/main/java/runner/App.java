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

    public static void main(String[] args) {
        try {
            List<String> result = new ArrayList<>();
            for (int i = 0; i <= 99; i++) {
                Algorithm algorithm = new Algorithm();
                algorithm.init("C:\\Work\\projects\\GA-on-permutations\\problem1.txt",
                        "C:\\Work\\projects\\GA-on-permutations\\settings0.txt",
                        "C:\\Work\\projects\\GA-on-permutations");
                algorithm.process();
                result.add(String.valueOf(algorithm.getPopulation().getBestIndividual().getObjectiveFunctionValue()));
            }
            Files.write(Paths.get("C:\\Work\\projects\\GA-on-permutations\\result-0.txt"), result);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

}

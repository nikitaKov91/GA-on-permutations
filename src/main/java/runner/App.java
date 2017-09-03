package runner;

import algorithm.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Коваленко Никита on 27.08.2017.
 */
public class App {

    static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try {
            Algorithm algorithm = new Algorithm();
            algorithm.init("C:\\Work\\projects\\GA-on-permutations\\problem.txt",
                    "C:\\Work\\projects\\GA-on-permutations\\settings.txt");
            algorithm.process();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

}

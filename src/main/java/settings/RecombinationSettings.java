package settings;

import util.RecombinationType;

import java.util.Arrays;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class RecombinationSettings {

    private RecombinationType recombinationType;

    public void init (String[] params) {
        try {
            recombinationType = RecombinationType.valueOf(params[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Передан неверный тип рекомбинации: " + params[0] +
                    " допустимые значения: " + Arrays.toString(RecombinationType.values()));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип селекции: ");
        switch (recombinationType) {
            case TYPICAL:
                sb.append("стандартная");
                break;
        }
        return sb.toString();
    }

    public RecombinationType getRecombinationType() {
        return recombinationType;
    }

    public void setRecombinationType(RecombinationType recombinationType) {
        this.recombinationType = recombinationType;
    }

}

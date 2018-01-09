package settings;

import util.RecombinationType;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class RecombinationSettings {

    private RecombinationType recombinationType;

    private RecombinationSettings() {
    }

    public static RecombinationSettings create() {
        return new RecombinationSettings();
    }

    public RecombinationSettings recombinationType(RecombinationType recombinationType) {
        this.recombinationType = recombinationType;
        return this;
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

package settings;

import util.OperatorType;
import util.RecombinationType;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class RecombinationSettings implements OperatorSettings {

    private OperatorType operatorType = OperatorType.RECOMBINATION;

    private RecombinationType recombinationType;

    private RecombinationSettings() {
    }

    public static RecombinationSettings create() {
        return new RecombinationSettings();
    }

    public RecombinationSettings init(List<String> content) {
        try {
            recombinationType = RecombinationType.valueOf(content.get(1));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Передан неверный тип рекомбинации: " + content.get(1) +
                    " допустимые значения: " + Arrays.toString(RecombinationType.values()));
        }
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип рекомбинации: ");
        switch (recombinationType) {
            case TYPICAL:
                sb.append("стандартная");
                break;
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecombinationSettings that = (RecombinationSettings) o;

        return recombinationType == that.recombinationType;
    }

    @Override
    public int hashCode() {
        return recombinationType.hashCode();
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(OperatorType operatorType) {
        this.operatorType = operatorType;
    }

    public RecombinationType getRecombinationType() {
        return recombinationType;
    }

    public void setRecombinationType(RecombinationType recombinationType) {
        this.recombinationType = recombinationType;
    }

}

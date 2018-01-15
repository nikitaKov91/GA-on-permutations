package settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.MutationProbabilityType;
import util.MutationType;
import util.OperatorType;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class MutationSettings implements OperatorSettings {

    private static Logger logger = LoggerFactory.getLogger(MutationSettings.class);

    private OperatorType operatorType = OperatorType.MUTATION;
    private MutationType mutationType;
    private MutationProbabilityType mutationProbabilityType;
    private Double mutationProbability;

    private MutationSettings() {
    }

    public static MutationSettings create() {
        return new MutationSettings();
    }

    public MutationSettings init(List<String> content) {
        try {
            mutationType = MutationType.valueOf(content.get(1));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Передан неверный тип мутации: " + content.get(1) +
                    " допустимые значения: " + Arrays.toString(MutationType.values()));
        }
        try {
            mutationProbabilityType = MutationProbabilityType.valueOf(content.get(2));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Передан неверный тип вероятности мутации: " + content.get(2) +
                    " допустимые значения: " + Arrays.toString(MutationProbabilityType.values()));
        }
        calcMutationProbability();
        return this;
    }

    public void calcMutationProbability() {
        logger.debug("Считаем вероятность мутации");
        double probability = 0;
        switch (mutationProbabilityType) {
            case VERY_LOW:
                probability = 0.2;
                break;
            case LOW:
                probability = 0.5;
                break;
            case AVERAGE:
                probability = 1;
                break;
            case HIGH:
                probability = 1.5;
                break;
            case VERY_HIGH:
                probability = 2;
                break;
        }
        mutationProbability = probability;
        logger.debug("Полученная вероятность: " + probability);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип мутации: ");
        switch (mutationType) {
            case BY_2_EXCHANGE:
                sb.append("стандартная. Вероятность мутации: " + mutationProbability);
                break;
            case BY_INVERSION:
                sb.append("мутация инверсией");
                break;
            case BY_INSERTION:
                sb.append("мутация вставкой");
                break;
            case BY_SHIFTING:
                sb.append("мутация переводом");
                break;
        }

        if (mutationType != MutationType.BY_2_EXCHANGE) {
            sb.append(". ");
            switch (mutationProbabilityType) {
                case VERY_LOW:
                    sb.append("Очень низкая");
                    break;
                case LOW:
                    sb.append("Низкая");
                    break;
                case AVERAGE:
                    sb.append("Средняя");
                    break;
                case HIGH:
                    sb.append("Высокая");
                    break;
                case VERY_HIGH:
                    sb.append("Очень высокая");
                    break;
            }
            sb.append(" вероятность мутации");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MutationSettings that = (MutationSettings) o;

        if (mutationType != that.mutationType) return false;
        if (mutationProbabilityType != that.mutationProbabilityType) return false;
        return mutationProbability.equals(that.mutationProbability);
    }

    @Override
    public int hashCode() {
        int result = mutationType.hashCode();
        result = 31 * result + mutationProbabilityType.hashCode();
        result = 31 * result + mutationProbability.hashCode();
        return result;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(OperatorType operatorType) {
        this.operatorType = operatorType;
    }

    public Double getMutationProbability() {
        return mutationProbability;
    }

    public void setMutationProbability(Double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public MutationProbabilityType getMutationProbabilityType() {
        return mutationProbabilityType;
    }

    public void setMutationProbabilityType(MutationProbabilityType mutationProbabilityType) {
        this.mutationProbabilityType = mutationProbabilityType;
    }

    public MutationType getMutationType() {
        return mutationType;
    }

    public void setMutationType(MutationType mutationType) {
        this.mutationType = mutationType;
    }

}

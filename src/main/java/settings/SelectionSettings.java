package settings;

import util.OperatorType;
import util.RankingSelectionType;
import util.SelectionType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Коваленко Никита on 03.09.2017.
 */
public class SelectionSettings implements OperatorSettings, Serializable {

    private OperatorType operatorType = OperatorType.SELECTION;
    private SelectionType selectionType;
    // для турнирной селекции
    private Integer tournamentSize;
    // для ранговой селекции
    private RankingSelectionType rankingSelectionType;
    private Double weight;

    private SelectionSettings() {
    }

    public static SelectionSettings create() {
        return new SelectionSettings();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип селекции: ");
        switch (selectionType) {
            case PROPORTIONAL:
                sb.append("пропорциональная");
                break;
            case RANKING:
                sb.append("ранговая, ");
                switch (rankingSelectionType) {
                    case LINEAR:
                        sb.append("линейная");
                        break;
                    case EXPONENTIAL:
                        sb.append("экспоненциальная.");
                        sb.append(". Вес: " + weight);
                        break;
                }
                break;
            case TOURNAMENT:
                sb.append("турнирная");
                sb.append(". Размер турнира: " + tournamentSize);
                break;
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectionSettings that = (SelectionSettings) o;

        if (selectionType != that.selectionType) return false;
        if (tournamentSize != null && !tournamentSize.equals(that.tournamentSize)) return false;
        if (rankingSelectionType != that.rankingSelectionType) return false;
        return weight.equals(that.weight);
    }

    @Override
    public int hashCode() {
        int result = selectionType.hashCode();
        if (tournamentSize != null) {
            result = 31 * result + tournamentSize.hashCode();
        }
        if (rankingSelectionType != null) {
            result = 31 * result + rankingSelectionType.hashCode();
        }
        if (weight != null) {
            result = 31 * result + weight.hashCode();
        }
        return result;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    public SelectionSettings init(List<String> content) {
        try {
            selectionType = SelectionType.valueOf(content.get(1));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Передан неверный тип селекции: " + content.get(1) +
                    " допустимые значения: " + Arrays.toString(SelectionType.values()));
        }
        switch (selectionType) {
            case PROPORTIONAL:
                break;
            case RANKING:
                try {
                    rankingSelectionType = RankingSelectionType.valueOf(content.get(2));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Передан неверный тип ранговой селекции: " + content.get(2) +
                            " допустимые значения: " + Arrays.toString(RankingSelectionType.values()));
                }
                if (rankingSelectionType == RankingSelectionType.EXPONENTIAL) {
                    weight = Double.parseDouble(content.get(3));
                }
                break;
            case TOURNAMENT:
                tournamentSize = Integer.parseInt(content.get(2));
                break;
        }
        return this;
    }

    public void setOperatorType(OperatorType operatorType) {
        this.operatorType = operatorType;
    }

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public Integer getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(Integer tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    public RankingSelectionType getRankingSelectionType() {
        return rankingSelectionType;
    }

    public void setRankingSelectionType(RankingSelectionType rankingSelectionType) {
        this.rankingSelectionType = rankingSelectionType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}

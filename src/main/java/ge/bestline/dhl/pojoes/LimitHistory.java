package ge.bestline.dhl.pojoes;

import java.io.Serializable;

public class LimitHistory implements Serializable {
    private int id;
    private int leadId;
    private Double decreaseAmount;
    private Double startAmount;
    private Double leftAmount;
    private String strCreateDate;
    private Integer deleted;

    public LimitHistory() {
    }

    public LimitHistory(int leadId, Double decreaseAmount, Double startAmount) {
        this.leadId = leadId;
        this.decreaseAmount = decreaseAmount;
        this.startAmount = startAmount;
    }

    public LimitHistory(int id, int leadId, Double decreaseAmount, Double startAmount, Double leftAmount, String strCreateDate) {
        this.id = id;
        this.leadId = leadId;
        this.decreaseAmount = decreaseAmount;
        this.startAmount = startAmount;
        this.leftAmount = leftAmount;
        this.strCreateDate = strCreateDate;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getStrCreateDate() {
        return strCreateDate;
    }

    public void setStrCreateDate(String strCreateDate) {
        this.strCreateDate = strCreateDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public Double getDecreaseAmount() {
        return decreaseAmount;
    }

    public void setDecreaseAmount(Double decreaseAmount) {
        this.decreaseAmount = decreaseAmount;
    }

    public Double getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(Double startAmount) {
        this.startAmount = startAmount;
    }

    public Double getLeftAmount() {
        return leftAmount;
    }

    public void setLeftAmount(Double leftAmount) {
        this.leftAmount = leftAmount;
    }
}

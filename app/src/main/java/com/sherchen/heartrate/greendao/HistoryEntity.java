package com.sherchen.heartrate.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table HISTORY_ENTITY.
 */
public class HistoryEntity {

    private Long id;
    private Long calculateTime;
    private Integer rate;

    //Not created by greenDAO, just use it by execute time not runtime
    private String strCalculateTime;

    public HistoryEntity() {
    }

    public HistoryEntity(Long id) {
        this.id = id;
    }

    public HistoryEntity(Long id, Long calculateTime, Integer rate) {
        this.id = id;
        this.calculateTime = calculateTime;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCalculateTime() {
        return calculateTime;
    }

    public void setCalculateTime(Long calculateTime) {
        this.calculateTime = calculateTime;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getStrCalculateTime() {
        return strCalculateTime;
    }

    public void setStrCalculateTime(String strCalculateTime) {
        this.strCalculateTime = strCalculateTime;
    }
}

package com.infosys.lex.rating.dto;

import javax.validation.constraints.NotNull;

public class RatingSearchDTO {
    /**
     *
     */
    private static final long serialVersionUID = 487478038111282255L;

    @NotNull(message = "{ratingservice.contentId.mandatory}")
    private String contentId;

    private Integer pageSize;

    private Integer pageNo;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}

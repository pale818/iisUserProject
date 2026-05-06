package hr.algebra.paola.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ReqResResponse {

    List<ReqResDto>reqResDtos;
    public ReqResResponse() {}


    private int page;
    private int total;
    @JsonProperty("total_pages")
    private int totalPages;
    @JsonProperty("per_page")
    private int perPage;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public List<ReqResDto> getReqResDtos() {
        return reqResDtos;
    }

    public void setReqResDtos(List<ReqResDto> reqResDtos) {
        this.reqResDtos = reqResDtos;
    }
}

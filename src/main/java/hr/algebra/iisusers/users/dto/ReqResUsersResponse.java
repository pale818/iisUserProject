package hr.algebra.iisusers.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// This DTO keeps only the parts of the ReqRes list response we currently care about.
public class ReqResUsersResponse {

    private Integer page;

    @JsonProperty("per_page")
    private Integer perPage;

    private Integer total;

    @JsonProperty("total_pages")
    private Integer totalPages;

    private List<ReqResUserDto> data;

    public ReqResUsersResponse() {
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<ReqResUserDto> getData() {
        return data;
    }

    public void setData(List<ReqResUserDto> data) {
        this.data = data;
    }
}

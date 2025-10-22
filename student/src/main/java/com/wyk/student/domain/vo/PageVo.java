package com.wyk.student.domain.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "分页参数")
public class PageVo<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "当前页码")
    private Long pageNum;
    @Schema(description = "每页数量")
    private Long pageSize;
    @Schema(description = "当前页面数据")
    private List<T> records;
    @Schema(description = "总数据量")
    private Long total;
    @Schema(description = "总页码")
    private Long totalPages;
    @Schema(description = "是否第一页")
    private boolean isFirstPage;
    @Schema(description = "是否最后一页")
    private boolean isLastPage;
    @Schema(description = "是否有下一页")
    private boolean hasNext;
    @Schema(description = "是否有上一页")
    private boolean hasPrevious;

    public PageVo(Long pageNum, Long pageSize, List<T> records, Long total) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.records = records;
        this.total = total;
    }

    public static <T> PageVo<T> of(Long pageNum, Long pageSize, List<T> records, Long total) {
        PageVo<T> pageVo = new PageVo<>(pageNum,pageSize,records,total);
        Long totalPages = (long) Math.ceil((double) total/pageSize);
        pageVo.setTotalPages(totalPages);
        pageVo.setFirstPage(pageNum == 1);
        pageVo.setLastPage(pageNum >= totalPages);
        pageVo.setHasNext(pageNum < totalPages);
        pageVo.setHasPrevious(pageNum > 1);
        return pageVo;
    }
    public static <T> PageVo<T> iPageToPageVo(IPage<T> iPage) {
        return of(iPage.getCurrent(),iPage.getSize(),iPage.getRecords(),iPage.getTotal());
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private Long pageNum;
        private Long pageSize;
        private List<T> records;
        private Long total;
        private Builder() {
            this.pageNum = 1L;
            this.pageSize = 10L;
        }
        public Builder<T> pageNum(Long pageNum) {
            this.pageNum = pageNum;
            return this;
        }
        public Builder<T> pageSize(Long pageSize) {
            this.pageSize = pageSize;
            return this;
        }
        public Builder<T> records(List<T> records) {
            this.records = records;
            return this;
        }
        public Builder<T> total(Long total) {
            this.total = total;
            return this;
        }
        public PageVo<T> build() {
            return PageVo.of(this.pageNum,this.pageSize,this.records,this.total);
        }
    }
}

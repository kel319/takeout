package com.wyk.student.domain.vo;

import com.wyk.student.domain.enums.StatusEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "套餐表Vo")
public class ComboVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "套餐表主键ID")
    private Long id;
    @Schema(description = "套餐名")
    private String name;
    @Schema(description = "套餐价格")
    private BigDecimal price;
    @Schema(description = "创建人")
    private Long createBy;
    @Schema(description = "套餐状态")
    private StatusEnums status;
    @Schema(description = "套餐菜品列表")
    private List<ComboDishVo> comboDishVos;

    public static ComboVo of(Long id,String name,BigDecimal price,Long createBy,StatusEnums status,List<ComboDishVo> comboDishVos){
        return new ComboVo(id,name,price,createBy,status,comboDishVos);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private BigDecimal price;
        private Long createBy;
        private StatusEnums status;
        private List<ComboDishVo> comboDishVos;
        public Builder() {
        }
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }
        public Builder createBy(Long createBy) {
            this.createBy = createBy;
            return this;
        }
        public Builder status(StatusEnums status) {
            this.status = status;
            return this;
        }
        public Builder comboDishVos(List<ComboDishVo> comboDishVos) {
            this.comboDishVos = comboDishVos;
            return this;
        }
        public ComboVo build() {
            return ComboVo.of(id,name,price,createBy,status,comboDishVos);
        }
    }
}

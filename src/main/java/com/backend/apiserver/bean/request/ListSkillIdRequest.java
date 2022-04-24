package com.backend.apiserver.bean.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ListSkillIdRequest {
    @NotNull
    private List<Long> skillIds;
}

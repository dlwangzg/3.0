package com.leadingsoft.bizfuse.base.dict.dto;

import com.leadingsoft.bizfuse.common.web.dto.JsTreeNodeDTO;

public class SortableTreeNodeDTO extends JsTreeNodeDTO {

    private int sortNum;

    public int getSortNum() {
        return this.sortNum;
    }

    public void setSortNum(final int sortNum) {
        this.sortNum = sortNum;
    }
}

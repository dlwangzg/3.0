package com.leadingsoft.bizfuse.base.dict.dto;

import com.leadingsoft.bizfuse.common.web.dto.AbstractDTO;

public class DictionaryCategoryDTO extends AbstractDTO {

    private String key;

    private String description;

    private Boolean discarded = false;

    public String getKey() {
        return this.key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Boolean getDiscarded() {
        return this.discarded;
    }

    public void setDiscarded(final Boolean discarded) {
        this.discarded = discarded;
    }

}

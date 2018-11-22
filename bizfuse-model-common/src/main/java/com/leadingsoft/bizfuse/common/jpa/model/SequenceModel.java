package com.leadingsoft.bizfuse.common.jpa.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

public class SequenceModel implements Persistable<Long> {
	private static final long serialVersionUID = 7205853521241442700L;

    // 支持Sequence的数据库Id生成策略，sequenceName为数据库的sequence表名
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_STORE")   
    @SequenceGenerator(name="SEQ_STORE", sequenceName="S_ORACLE",allocationSize = 1)   
    private Long id;

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Persistable#getId()
     */
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the id of the entity.
     *
     * @param id the id to set
     */
    protected void setId(final Long id) {
        this.id = id;
    }

    /**
     * Must be {@link Transient} in order to ensure that no JPA provider
     * complains because of a missing setter.
     *
     * @see DATAJPA-622
     * @see org.springframework.data.domain.Persistable#isNew()
     */
    @Override
    @Transient
    public boolean isNew() {
        return null == this.getId();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), this.getId());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }

        final Persistable<?> that = (Persistable<?>) obj;

        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int hashCode = 17;

        hashCode += null == this.getId() ? 0 : this.getId().hashCode() * 31;

        return hashCode;
    }
}

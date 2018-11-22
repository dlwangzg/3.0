package com.leadingsoft.bizfuse.base.authority.model.menu;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.leadingsoft.bizfuse.common.jpa.model.AbstractModel;

/**
 * 系统的菜单管理
 */
@Entity
public class SystemMenu extends AbstractModel {
    private static final long serialVersionUID = -1361957270222942619L;

    /**
     * 菜单标题
     */
    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 20, name = "key_value")
    private String key;

    /**
     * true 启用 false 禁用
     */
    @Column(nullable = false)
    private boolean enabled;

    /**
     * 菜单的访问路径
     */
    @Column(length = 300)
    private String href;

    /**
     * class name
     */
    @Column(length = 255)
    private String className;

    @OneToMany(mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.REMOVE })
    @OrderBy("sortNum ASC, id ASC")
    private List<SystemMenu> subMenus; //下级菜单

    @ManyToOne
    private SystemMenu parent;//父菜单

    @Enumerated(EnumType.STRING)
    private MenuType type;

    @Column
    private int sortNum;//序号

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(final String url) {
        this.href = url;
    }

    public List<SystemMenu> getSubMenus() {
        return this.subMenus;
    }

    public void setSubMenus(final List<SystemMenu> children) {
        this.subMenus = children;
    }

    public SystemMenu getParent() {
        return this.parent;
    }

    public void setParent(final SystemMenu parent) {
        this.parent = parent;
    }

    public MenuType getType() {
        return this.type;
    }

    public void setType(final MenuType type) {
        this.type = type;
    }

    public int getSortNum() {
        return this.sortNum;
    }

    public void setSortNum(final int order) {
        this.sortNum = order;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public static enum MenuType {
        site, url, button
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(final String key) {
        this.key = key;
    }
}

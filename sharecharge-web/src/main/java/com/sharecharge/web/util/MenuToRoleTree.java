package com.sharecharge.web.util;

import java.io.Serializable;
import java.util.List;

public class MenuToRoleTree implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private Boolean expand;
    private Boolean checked;
    private List<MenuToRoleTree> children;


    public Boolean getExpand() {
        return expand;
    }

    public void setExpand(Boolean expand) {
        this.expand = expand;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MenuToRoleTree> getChildren() {
        return children;
    }

    public void setChildren(List<MenuToRoleTree> children) {
        this.children = children;
    }
    /*
     {
            title: 'parent 1',
            id:1,
            expand: true,
            children: [
              {
                title: 'parent 1-1',
                expand: true,
                id:11,
                children: [
                  {
                    id:111,
                    title: 'leaf 1-1-1'
                  },
     */

}

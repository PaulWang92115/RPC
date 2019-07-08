package com.paul.serializer.Impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author liyebing created on 17/1/21.
 * @version $Id$
 */
public class User implements Serializable {

    private String name;
    private String email;
    private List<User> userList;
    private Map<String, User> userMap;

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Map<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

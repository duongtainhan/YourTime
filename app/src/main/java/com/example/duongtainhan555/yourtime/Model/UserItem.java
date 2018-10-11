package com.example.duongtainhan555.yourtime.Model;

import java.util.List;

public class UserItem {
    private String idUser;
    private List<DataItem> dataItems;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public List<DataItem> getDataItems() {
        return dataItems;
    }

    public void setDataItems(List<DataItem> dataItems) {
        this.dataItems = dataItems;
    }
}

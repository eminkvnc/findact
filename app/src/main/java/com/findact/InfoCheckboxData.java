package com.findact;

public class InfoCheckboxData {

    public boolean isClicked = false;
    public int index;

    public InfoCheckboxData(boolean isClicked, int index) {
        this.isClicked = isClicked;
        this.index = index;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public int getIndex() {
        return index;
    }
}

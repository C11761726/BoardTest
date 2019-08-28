package com.example.boardtest;

import java.util.ArrayList;

public class ExecuteAsRoot extends AExecuteAsRoot {
    ArrayList<String> list;

    public ExecuteAsRoot(ArrayList<String> list) {
        this.list = list;
    }

    @Override
    protected ArrayList<String> getCommandsToExecute() {
        return list;
    }
}

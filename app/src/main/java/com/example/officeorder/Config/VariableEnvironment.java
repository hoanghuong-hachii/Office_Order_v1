package com.example.officeorder.Config;

public class VariableEnvironment {

    private String variable;

    public VariableEnvironment(String variable) {
        this.variable = variable;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = "http://192.168.43.70:8080/";
    }

    public String Valiables(){
        return "http://192.168.43.70:8080/";
    }

}

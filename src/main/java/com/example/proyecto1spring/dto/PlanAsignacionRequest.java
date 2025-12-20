package com.example.proyecto1spring.dto;

public class PlanAsignacionRequest {
    private Long planId;
    private String notas;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}

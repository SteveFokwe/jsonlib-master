package com.company.test;

import com.company.jsonlib.annotations.FieldName;

public class ProgramDTO {
    @FieldName(override = "nomProgramme")
    private String name;

    private String code;
    private int domain;
    private boolean limited;
    private CoursDTO[] composition;

    public ProgramDTO() {
    }

    public ProgramDTO(String name, String code, int domain, boolean limited) {
        this.name = name;
        this.code = code;
        this.domain = domain;
        this.limited = limited;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDomain() {
        return domain;
    }

    public void setDomain(int domain) {
        this.domain = domain;
    }

    public boolean isLimited() {
        return limited;
    }

    public void setLimited(boolean limited) {
        this.limited = limited;
    }

    public CoursDTO[] getComposition() {
        return composition;
    }

    public void setComposition(CoursDTO[] composition) {
        this.composition = composition;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ProgramDTO{");
        sb.append("name='").append(name).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append(", domain=").append(domain);
        sb.append(", limited=").append(limited);
        sb.append(", composition=[");

        if (composition != null) {
            for (int i = 0; i < composition.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(composition[i]);
            }
        }

        sb.append("]}");
        return sb.toString();
    }
}
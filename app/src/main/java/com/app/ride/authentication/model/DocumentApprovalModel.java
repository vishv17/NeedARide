package com.app.ride.authentication.model;

import java.io.Serializable;

public class DocumentApprovalModel implements Serializable
{
    private String docUrl;
    private boolean approval;

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    public boolean isApproval() {
        return approval;
    }

    public void setApproval(boolean approval) {
        this.approval = approval;
    }
}

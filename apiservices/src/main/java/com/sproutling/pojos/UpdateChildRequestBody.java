package com.sproutling.pojos;

/**
 * Created by subram13 on 4/29/18.
 */

public class UpdateChildRequestBody extends Child {
    public UpdateChildRequestBody(Child child) {
        this.setFirstName(child.getFirstName());
        this.setLastName(child.getLastName());
        this.setGender(child.getGender());
        this.setAccountId(child.getAccountId());
        this.setId(child.getId());
        this.setCreatedAt(child.getCreatedAt());
        this.setUpdatedAt(child.getUpdatedAt());
        this.setPhotoUrl(child.getPhotoUrl());
        this.setTwinId(child.getTwinId());
        this.setBirthDate(child.getBirthDate());
        this.setDueDate(child.getDueDate());
    }
}

package com.kiss.kissnest.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CrumbEntity implements Serializable {

	private String _class;
	private String crumb;
	private String crumbRequestField;
}

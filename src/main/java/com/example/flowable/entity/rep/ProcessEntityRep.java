package com.example.flowable.entity.rep;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessEntityRep {
	String id;

	String deploymentId;

	String name;

	String resourceName;

	String key;

	String diagramResourceName;

	Integer version;

}

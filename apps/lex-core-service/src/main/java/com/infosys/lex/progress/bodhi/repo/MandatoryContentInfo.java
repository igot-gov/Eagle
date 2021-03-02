package com.infosys.lex.progress.bodhi.repo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MandatoryContentInfo {
	private String rootOrg;
	private String org;
	private String contentType;
	@Builder.Default
	private Float minProgressForCompletion = 0.0f;
	@Builder.Default
	private Float userProgress = 0.0f;
}
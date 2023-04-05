package com.panasi.qna.category.payload;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CategoryInput {
	
	private Integer parentId;
	
	@NotNull
	@Size(min=3, max=50)
	private String name;

}

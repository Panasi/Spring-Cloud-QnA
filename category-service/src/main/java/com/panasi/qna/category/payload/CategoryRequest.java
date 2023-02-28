package com.panasi.qna.category.payload;

import lombok.Data;

@Data
public class CategoryRequest {
	
	private String name;
	private Integer parentId;

}

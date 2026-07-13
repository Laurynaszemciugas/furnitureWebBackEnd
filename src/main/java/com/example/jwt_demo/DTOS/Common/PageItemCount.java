package com.example.jwt_demo.DTOS.Common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageItemCount {

    private Long pageCount;
    private Long itemCount;

}

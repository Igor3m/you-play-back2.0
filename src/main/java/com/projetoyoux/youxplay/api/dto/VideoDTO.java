package com.projetoyoux.youxplay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {

    private Long id;
    private String nome;
    private Long categoria;
    private String comentario;

}

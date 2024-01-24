package com.example.userPoemApi.dtos;

import jakarta.validation.constraints.NotBlank;

public record UsuarioRecordDto(@NotBlank String nome, @NotBlank String senha, @NotBlank String email) {
}

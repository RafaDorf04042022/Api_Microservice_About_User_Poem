package com.example.userPoemApi.controllers;

import com.example.userPoemApi.dtos.UsuarioRecordDto;
import com.example.userPoemApi.model.UsuarioModel;
import com.example.userPoemApi.repositores.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioModel> createUsuario(@RequestBody @Valid UsuarioRecordDto usuarioRecordDto){
        var usuarioModel = new UsuarioModel();
        BeanUtils.copyProperties(usuarioRecordDto, usuarioModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepository.save(usuarioModel));
    }
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioModel>> getAllUsuarios(){
        List<UsuarioModel> usuarioModelList = usuarioRepository.findAll();
        if (!usuarioModelList.isEmpty()){
            for(UsuarioModel usuario: usuarioModelList){
                String nome = usuario.getNome();
                String senha = usuario.getSenha();
                usuario.add(linkTo(methodOn(UsuarioController.class).getByNomeAndSenhaUsuario(nome, senha)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(usuarioModelList);
    }
    @GetMapping("/usuarios/{nome}/{senha}")
    public ResponseEntity<Object> getByNomeAndSenhaUsuario(@PathVariable(name = "nome") String nome,
                                                                 @PathVariable(name = "senha") String senha){
        Optional<UsuarioModel> usuarioModelOptional = usuarioRepository.findByNomeAndSenha(nome, senha);
        if (usuarioModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found this Usuario");
        }
        usuarioModelOptional.get().add(linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withRel("Link com todos os usuarios: "));
        return ResponseEntity.status(HttpStatus.OK).body(usuarioModelOptional.get());
    }
    @PutMapping("/usuarios/{nome}/{senha}")
    public ResponseEntity<Object> putByNomeAndSenhaUsuario(@PathVariable(name = "nome") String nome,
                                                           @PathVariable(name = "senha") String senha,
                                                           @RequestBody @Valid UsuarioRecordDto usuarioRecordDto){
        Optional<UsuarioModel> usuarioModelOptional = usuarioRepository.findByNomeAndSenha(nome, senha);
        if (usuarioModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found Usuario");
        }
        var copyUsuarioModelOptional = usuarioModelOptional.get();
        BeanUtils.copyProperties(usuarioRecordDto, copyUsuarioModelOptional);
        usuarioModelOptional.get().add(linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withRel("Link com todos os usuários: "));
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.save(copyUsuarioModelOptional));
    }
    @DeleteMapping("usuarios/{nome}/{senha}")
    public ResponseEntity<Object> deleteNomeAndSenhaUsuario(@PathVariable(name = "nome") String nome,
                                                                  @PathVariable(name = "senha") String senha){
        Optional<UsuarioModel> usuarioModelOptional = usuarioRepository.findByNomeAndSenha(nome, senha);
        if (usuarioModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found Usuario");
        }
        usuarioRepository.delete(usuarioModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Delete successfully");
    }
}

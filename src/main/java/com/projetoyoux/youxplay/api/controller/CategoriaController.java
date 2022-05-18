package com.projetoyoux.youxplay.api.controller;

import com.projetoyoux.youxplay.api.dto.CategoriaDTO;
import com.projetoyoux.youxplay.exception.RegraNegocioException;
import com.projetoyoux.youxplay.model.entity.Usuario;
import com.projetoyoux.youxplay.model.entity.lancamentos.Categoria;
import com.projetoyoux.youxplay.service.CategoriaService;
import com.projetoyoux.youxplay.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value ="nome" , required = false) String nome,
            @RequestParam("usuario") Long idUsuario
    ) {

        Categoria categoriaFiltro = new Categoria();
        categoriaFiltro.setNome(nome);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if(!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado.");
        }else {
            categoriaFiltro.setUsuario(usuario.get());
        }

        List<Categoria> Categorias = service.buscar(categoriaFiltro);
        return ResponseEntity.ok(Categorias);

    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody CategoriaDTO dto){
        try{
            Categoria entidade = converter(dto);
            entidade = service.salvar(entidade);
            return ResponseEntity.ok(entidade);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar( @PathVariable("id") Long id, @RequestBody CategoriaDTO dto ) {
        return service.obterPorId(id).map( entity -> {
            try {
                Categoria categoria = converter(dto);
                categoria.setId(entity.getId());
                service.atualizar(categoria);
                return ResponseEntity.ok(categoria);
            }catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () ->
                new ResponseEntity("Categoria não encontrada na base de Dados.", HttpStatus.BAD_REQUEST) );
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar( @PathVariable("id") Long id ) {
        return service.obterPorId(id).map( entidade -> {
            service.deletar(entidade);
            return new ResponseEntity( HttpStatus.NO_CONTENT );
        }).orElseGet( () ->
                new ResponseEntity("Categoria não encontrada na base de Dados.", HttpStatus.BAD_REQUEST) );
    }

    private Categoria converter(CategoriaDTO dto){
        Categoria categoria = new Categoria();
        categoria.setId(dto.getId());
        categoria.setNome(dto.getNome());

        Usuario usuario = usuarioService
                .obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado."));
        categoria.setUsuario(usuario);

        return categoria;
    }

}

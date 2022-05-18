package com.projetoyoux.youxplay.api.controller;

import com.projetoyoux.youxplay.api.dto.VideoDTO;
import com.projetoyoux.youxplay.exception.RegraNegocioException;
import com.projetoyoux.youxplay.model.entity.lancamentos.Categoria;
import com.projetoyoux.youxplay.model.entity.lancamentos.Video;
import com.projetoyoux.youxplay.service.CategoriaService;
import com.projetoyoux.youxplay.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService service;
    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value ="nome" , required = false) String nome,
            @RequestParam("categoria") Long idCategoria,
            @RequestParam(value ="comentario" , required = false) String comentario
    ) {

        Video videoFiltro = new Video();
        videoFiltro.setNome(nome);
        videoFiltro.setComentario(comentario);

        Optional<Categoria> categoria = categoriaService.obterPorId(idCategoria);
        if(!categoria.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Catgoria não encontrada para o Id informado.");
        }else {
            videoFiltro.setCategoria(categoria.get());
        }

        List<Video> Videos = service.buscar(videoFiltro);
        return ResponseEntity.ok(Videos);

    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody VideoDTO dto){
        try{
            Video entidade = converter(dto);
            entidade = service.salvar(entidade);
            return ResponseEntity.ok(entidade);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar( @PathVariable("id") Long id, @RequestBody VideoDTO dto ) {
        return service.obterPorId(id).map( entity -> {
            try {
                Video video = converter(dto);
                video.setId(entity.getId());
                service.atualizar(video);
                return ResponseEntity.ok(video);
            }catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () ->
                new ResponseEntity("Vídeo não encontrado na base de Dados.", HttpStatus.BAD_REQUEST) );
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar( @PathVariable("id") Long id ) {
        return service.obterPorId(id).map( entidade -> {
            service.deletar(entidade);
            return new ResponseEntity( HttpStatus.NO_CONTENT );
        }).orElseGet( () ->
                new ResponseEntity("Vídeo não encontrado na base de Dados.", HttpStatus.BAD_REQUEST) );
    }

    private Video converter(VideoDTO dto){
        Video video = new Video();
        video.setId(dto.getId());
        video.setNome(dto.getNome());

        Categoria categoria = categoriaService
                .obterPorId(dto.getCategoria())
                .orElseThrow(() -> new RegraNegocioException("Categoria não encontrada para o Id informado."));
        video.setCategoria(categoria);

        return video;
    }

}

package school.sptech.prova_ac1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        if (repository.findByEmail(usuario.getEmail()).isPresent()
                || repository.findByCpf(usuario.getCpf()).isPresent()) {
            return ResponseEntity.status(409).build(); // CONFLICT
        }
        Usuario salvo = repository.save(usuario);
        return ResponseEntity.status(201).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = repository.findAll();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.ok(usuarios); // 200
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        return repository.findById(id)
                .map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> filtrarPorData(@RequestParam LocalDate nascimento) {
        List<Usuario> usuarios = repository.findByDataNascimentoAfter(nascimento);
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Integer id,
                                             @RequestBody Usuario usuario) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Optional<Usuario> emailExistente = repository.findByEmail(usuario.getEmail());
        Optional<Usuario> cpfExistente = repository.findByCpf(usuario.getCpf());

        if (emailExistente.isPresent() && !emailExistente.get().getId().equals(id)) {
            return ResponseEntity.status(409).build(); // email já existe
        }
        if (cpfExistente.isPresent() && !cpfExistente.get().getId().equals(id)) {
            return ResponseEntity.status(409).build(); // cpf já existe
        }

        usuario.setId(id);
        Usuario atualizado = repository.save(usuario);
        return ResponseEntity.ok(atualizado);
    }
}

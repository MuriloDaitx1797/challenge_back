package com.Desafio.controladores;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.Desafio.FireDevDesafioApplication.User;
import com.Desafio.Autenticador.JWT;
import com.Desafio.Autenticador.MyUserDetailsService;
import com.Desafio.Repositorios.UserRep;
import com.Desafio.Autenticador.AuthenticationRequest;
import com.Desafio.Autenticador.AuthenticationResponse;

@RestController
//@RequestMapping("/usuarios")
public class UserController {
	
	@Autowired
	UserRep userRepo;
	
	@Autowired
    AuthenticationManager authenticationManager;
	
	@Autowired
    MyUserDetailsService userDetailsService;
	
	@Autowired
    JWT jwtUtil;

	@RequestMapping("/usuarios")
	
	public String testePrt(){
        return  "isso eh um teste, JWT esta funcionando";
    }
	
	@PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }
	
	@PostMapping(consumes = "application/json")
	public ResponseEntity<?> adicionaUser(@RequestBody User user){
		User userSalvo = userRepo.save(user);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/usuarios").path("/{id})")
				.buildAndExpand(userSalvo.getId()).toUri();
		
		return ResponseEntity.created(uri).build();	
	}
	
	@GetMapping
	public ResponseEntity<List<User>> listaUser(){
		return ResponseEntity.ok().body(userRepo.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<User> buscaPorId(@PathVariable long id){
		return ResponseEntity.ok().body(userRepo.findById(id).get());
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Optional<User>> deleteById(@PathVariable Long id){
		try {
			userRepo.deleteById(id);
			return new ResponseEntity<Optional<User>>(HttpStatus.OK);
		}catch (NoSuchElementException nsee) {
			return new ResponseEntity<Optional<User>>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping(value="/{id}")
	public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User newUser){
		return userRepo.findById(id)
				.map(user -> {
					user.setNome(newUser.getNome());
					User userUpdate = userRepo.save(user);
					return ResponseEntity.ok().body(userUpdate);
				}).orElse(ResponseEntity.notFound().build());
	}
}

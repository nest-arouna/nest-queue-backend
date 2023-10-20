package com.app.queue.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.app.queue.entities.Utilisateur;
import com.app.queue.repositories.IDaoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	@Autowired
    private IDaoUser daoUser;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Utilisateur> user = daoUser.findByEmail(email);

		if(!user.isPresent()) throw new UsernameNotFoundException(email);
		Collection<GrantedAuthority> authorities = new ArrayList<>();

		User userNew = new User(
				user.get().getEmail(),
				user.get().getPassword(),
				authorities
				);
		return userNew;
	}

}

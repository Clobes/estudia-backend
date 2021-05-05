package com.backend.estudia.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.estudia.entity.Course;
import com.backend.estudia.entity.User;
import com.backend.estudia.util.Roles;

public interface IUserRepository extends JpaRepository<User, Long> {
	
	//public Page<User> findAllByUserStatusTrue(Pageable pageable);
	public Page<User> findByEmailContainingIgnoreCase(String username, Pageable pageable);
	public Page<User> findByUserStatus(boolean userStatus, Pageable pageable);
	public Optional<User> findByCiAndUserStatusTrue(String ci);
	public Optional<User> findByIdAndUserStatus(Long id, boolean userStatus);
	public Optional<User> findByEmail(String email);
	public Optional<User> findById(Long id);
	public Optional<User> findByCi(String ci);
	public Page<User> findByIdInAndUserStatusTrue(Pageable pageable, Collection ids);
	//Filtro todos los campos ordenado por id
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderById(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String emailFilter, String roleFilter, boolean userStatus);
	//Filtro todos los campos ordenado por firstname
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String emailFilter, String roleFilter, boolean userStatus);
	//Filtro todos los campos ordenado por ci
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String emailFilter, String roleFilter, boolean userStatus);
	//Filtro todos los campos ordenado por email
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String emailFilter, String roleFilter, boolean userStatus);
	//Filtro todos los campos ordenado por roles
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String emailFilter, String roleFilter, boolean userStatus);
	//Filtro todos los campos sin ordenar
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String emailFilter, String roleFilter, boolean userStatus);
	//Filtro todos los campos excepto email  ordenado por id
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderById(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String roleFilter, boolean userStatus);
	//Filtro todos los campos excepto email  ordenado por name
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String roleFilter, boolean userStatus);
	//Filtro todos los campos excepto email  ordenado por ci
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String roleFilter, boolean userStatus);
	//Filtro todos los campos excepto email  ordenado por roles
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String roleFilter, boolean userStatus);
	//Filtro todos los campos excepto email  sin ordenar
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String roleFilter, boolean userStatus);
	//Filtro todos los campos excepto roles  ordenado por id
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderById(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String emailFilter, boolean userStatus);
	//Filtro todos los campos excepto roles  ordenado por Name
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String emailFilter, boolean userStatus);
	//Filtro todos los campos excepto roles  ordenado por ci
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String emailFilter, boolean userStatus);
	//Filtro todos los campos excepto roles  ordenado por email
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String emailFilter, boolean userStatus);
	//Filtro todos los campos excepto roles  sin ordenar
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatus(
			Pageable pageable, Long id, String nameFilter, String LastName, String ciFilter, String emailFilter, boolean userStatus);
	//Filtro todos los campos excepto ci ordenado por id
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderById(
			Pageable pageable, Long id, String nameFilter, String LastName, String roleFilter, String emailFilter, boolean userStatus);
	//Filtro todos los campos excepto ci ordenado por name
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, Long id, String nameFilter, String LastName, String roleFilter, String emailFilter, boolean userStatus);
	//Filtro todos los campos excepto ci ordenado por roles
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByRolesName(
			Pageable pageable, Long id, String nameFilter, String LastName, String roleFilter, String emailFilter, boolean userStatus);
	//Filtro todos los campos excepto ci ordenado por email
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, Long id, String nameFilter, String LastName, String roleFilter, String emailFilter, boolean userStatus);
	//Filtro todos los campos excepto ci sin ordenar
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatus(
			Pageable pageable, Long id, String nameFilter, String LastName, String roleFilter, String emailFilter, boolean userStatus);
	// Filtro todos los campos excepto id ordenado por id
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, String ciFilter, String nameFilter, String LastNameFilter, String roleFilter, String emailFilter, boolean userStatus);
	// Filtro todos los campos excepto id ordenado por name
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, String ciFilter, String nameFilter, String LastNameFilter, String roleFilter, String emailFilter, boolean userStatus);
	// Filtro todos los campos excepto id ordenado por role
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByRolesName(
			Pageable pageable, String ciFilter, String nameFilter, String LastNameFilter, String roleFilter, String emailFilter, boolean userStatus);
	// Filtro todos los campos excepto id ordenado por email
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, String ciFilter, String nameFilter, String LastNameFilter, String roleFilter, String emailFilter, boolean userStatus);
	// Filtro todos los campos excepto id  sin ordenar
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String ciFilter, String nameFilter, String LastNameFilter, String roleFilter, String emailFilter, boolean userStatus);
	// Filtro todos los campos excepto id y ci ordenado por name
	public Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, String nameFilter, String LastNameFilter, String emailFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto id y ci ordenado por roles
		public Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
				Pageable pageable, String nameFilter, String LastNameFilter, String emailFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto id y ci ordenado por email
	public Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, String nameFilter, String LastNameFilter, String emailFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto id y ci sin ordenar
	public Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String nameFilter, String LastNameFilter, String emailFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto id y name ordenado por ci
	public Page<User> findByCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, String ciFilter, String emailFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto id y name ordenado por email
	public Page<User> findByCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, String ciFilter, String emailFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto id y name ordenado por roles
	public Page<User> findByCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
			Pageable pageable, String ciFilter, String emailFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto id y name sin ordenar
	public Page<User> findByCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String ciFilter, String emailFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto id y email ordenado por ci
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, String ciFilter, String nameFilter, String lastNameFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto id y email ordenado por name
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, String ciFilter, String nameFilter, String lastNameFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto id y email ordenado por roles
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
			Pageable pageable, String ciFilter, String nameFilter, String lastNameFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto id y email sin ordenar
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String ciFilter, String nameFilter, String lastNameFilter, String roleFilter, boolean userStatus);
	// Filtro todos los campos excepto role y email ordenado por ci
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatusOrderByCi(
			Pageable pageable, String ciFilter, String nameFilter, String LastNameFilter, Long id, boolean userStatus);
	// Filtro todos los campos excepto role y email ordenado por name
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatusOrderByFirstName(
			Pageable pageable, String ciFilter, String nameFilter, String LastNameFilter, Long id, boolean userStatus);
	// Filtro todos los campos excepto role y email ordenado por id
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatusOrderById(
			Pageable pageable, String ciFilter, String nameFilter, String LastNameFilter, Long id, boolean userStatus);
	// Filtro todos los campos excepto role y email sin ordenar
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatus(
			Pageable pageable, String ciFilter, String nameFilter, String LastNameFilter, Long id, boolean userStatus);
	// Filtro todos los campos excepto role y ci ordenado por email
	public Page<User> findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatusOrderByEmail(
			Pageable pageable, String emailFilter, String nameFilter, String LastNameFilter, Long id, boolean userStatus);
	// Filtro todos los campos excepto role y ci ordenado por name
	public Page<User> findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatusOrderByFirstName(
			Pageable pageable, String emailFilter, String nameFilter, String LastNameFilter, Long id, boolean userStatus);
	// Filtro todos los campos excepto role y ci ordenado por id
		public Page<User> findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatusOrderById(
				Pageable pageable, String emailFilter, String nameFilter, String LastNameFilter, Long id, boolean userStatus);
	// Filtro todos los campos excepto role y ci sin ordenar
	public Page<User> findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatus(
			Pageable pageable, String emailFilter, String nameFilter, String LastNameFilter, Long id, boolean userStatus);
	// Filtro todos los campos excepto role y id ordenado por email
	public Page<User> findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, String emailFilter, String nameFilter, String LastNameFilter, String ciFilter, boolean userStatus);
	// Filtro todos los campos excepto role y id ordenado por name
	public Page<User> findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, String emailFilter, String nameFilter, String LastNameFilter, String ciFilter, boolean userStatus);
	// Filtro todos los campos excepto role y id ordenado por ci
	public Page<User> findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, String emailFilter, String nameFilter, String LastNameFilter, String ciFilter, boolean userStatus);
	// Filtro todos los campos excepto role y id sin ordenar
	public Page<User> findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String emailFilter, String nameFilter, String LastNameFilter, String ciFilter, boolean userStatus);
	// Filtro todos los campos excepto role y name ordenado por email
	public Page<User> findByEmailContainingIgnoreCaseAndIdAndCiContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, String emailFilter, Long id, String ciFilter, boolean userStatus);
	// Filtro todos los campos excepto role y name ordenado por id
	public Page<User> findByEmailContainingIgnoreCaseAndIdAndCiContainingIgnoreCaseAndUserStatusOrderById(
			Pageable pageable, String emailFilter, Long id, String ciFilter, boolean userStatus);
	// Filtro todos los campos excepto role y name ordenado por ci
	public Page<User> findByEmailContainingIgnoreCaseAndIdAndCiContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, String emailFilter, Long id, String ciFilter, boolean userStatus);
	// Filtro todos los campos excepto role y name sin ordenar
	public Page<User> findByEmailContainingIgnoreCaseAndIdAndCiContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String emailFilter, Long id, String ciFilter, boolean userStatus);
	// Filtro por id y email ordenado por id
	public Page<User> findByIdAndEmailContainingIgnoreCaseAndUserStatusOrderById(
			Pageable pageable, Long id, String emailFilter, boolean userStatus);
	// Filtro por id y email ordenado por email
	public Page<User> findByIdAndEmailContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, Long id, String emailFilter, boolean userStatus);
	// Filtro por id y email sin ordenar
	public Page<User> findByIdAndEmailContainingIgnoreCaseAndUserStatus(
			Pageable pageable, Long id, String emailFilter, boolean userStatus);
	// Filtro por id y name  ordenado por id
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderById(
			Pageable pageable, Long id, String nameFilter, String lastName ,boolean userStatus);
	// Filtro por id y name  ordenado por name
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, Long id, String nameFilter, String lastName ,boolean userStatus);
	// Filtro por id y name sin ordenado 
	public Page<User> findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, Long id, String nameFilter, String lastName ,boolean userStatus);
	// Filtro por id y ci ordenado por id
	public Page<User> findByIdAndCiContainingIgnoreCaseAndUserStatusOrderById(
			Pageable pageable, Long id, String ciFilter, boolean userStatus);
	// Filtro por id y ci ordenado por ci
	public Page<User> findByIdAndCiContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, Long id, String ciFilter, boolean userStatus);
	// Filtro por id y ci sin ordenar
	public Page<User> findByIdAndCiContainingIgnoreCaseAndUserStatus(
			Pageable pageable, Long id, String ciFilter, boolean userStatus);
	// Filtro por id y rol ordenado por id
	public Page<User>  findByIdAndRolesNameContainingIgnoreCaseAndUserStatusOrderById(
			Pageable pageable, Long id, String roleFilter, boolean userStatus);
	// Filtro por id y rol ordenado por rol
	public Page<User>  findByIdAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
			Pageable pageable, Long id, String roleFilter, boolean userStatus);
	// Filtro por id y rol sin ordenar
	public Page<User>  findByIdAndRolesNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, Long id, String roleFilter, boolean userStatus);
	// Filtro por email y name ordenado por email
	public Page<User>  findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, String emailFilter, String nameFilter, String LastNameFilter, boolean userStatus);
	// Filtro por email y name ordenado por name
	public Page<User>  findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, String emailFilter, String nameFilter, String LastNameFilter, boolean userStatus);
	// Filtro por email y name sin ordenar
	public Page<User>  findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String emailFilter, String nameFilter, String LastNameFilter, boolean userStatus);
	// Filtro por email y ci ordenado por email
	public Page<User> findByEmailContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, String emailFilter, String ciFilter, boolean userStatus);
	// Filtro por email y ci ordenado por ci
	public Page<User> findByEmailContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, String emailFilter, String ciFilter, boolean userStatus);
	// Filtro por email y ci sin ordenar
	public Page<User> findByEmailContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String emailFilter, String ciFilter, boolean userStatus);
	// Filtro por email y roles ordenado por email
	public Page<User> findByEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByEmail(
			Pageable pageable, String emailFilter, String roleFilter, boolean userStatus);
	// Filtro por email y roles ordenado por roles
	public Page<User> findByEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
			Pageable pageable, String emailFilter, String roleFilter, boolean userStatus);
	// Filtro por email y roles sin ordenar
	public Page<User> findByEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String emailFilter, String roleFilter, boolean userStatus);
	// Filtro por ci y rol ordenado por ci
	public Page<User> findByCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, String ciFilter, String roleFilter, boolean userStatus);
	// Filtro por ci y rol ordenado por rol
	public Page<User> findByCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
			Pageable pageable, String ciFilter, String roleFilter, boolean userStatus);
	// Filtro por ci y rol sin ordenar
	public Page<User> findByCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String ciFilter, String roleFilter, boolean userStatus);
	// Filtro por ci y name ordenado por ci
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByCi(
			Pageable pageable, String ciFilter, String nameFilter, String lastNameFilter, boolean userStatus);
	// Filtro por ci y name ordenado por name
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, String ciFilter, String nameFilter, String lastNameFilter, boolean userStatus);
	// Filtro por ci y name sin ordenar
	public Page<User> findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String ciFilter, String nameFilter, String lastNameFilter, boolean userStatus);
	//Filtrado por roles
	public Page<User>  findByRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(Pageable pageable, String roleFilter, boolean userStatus);
	//Filtro por roles
	public Page<User> findByRolesNameContainingIgnoreCaseAndUserStatus(Pageable pageable, String roleFilter, boolean userStatus);
	//Filtro por name
	public Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
			Pageable pageable, String nameFilter, String lastNameFilter, boolean userStatus);
	//Filtro por name
	public Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String nameFilter, String lastNameFilter, boolean userStatus);
	//Filtro por ci
	public Page<User> findByCiContainingIgnoreCaseAndUserStatusOrderByCi(Pageable pageable, String ciFilter, boolean userStatus);
	//Filtro por ci
	public Page<User> findByCiContainingIgnoreCaseAndUserStatus(Pageable pageable, String ciFilter, boolean userStatus);	
	//Filtro por email
	public Page<User> findByEmailContainingIgnoreCaseAndUserStatusOrderByEmail(Pageable pageable, String emailFilter, boolean userStatus);
	//Filtro por email
	public Page<User> findByEmailContainingIgnoreCaseAndUserStatus(Pageable pageable, String emailFilter, boolean userStatus);
	//Filtro por id
	public Page<User>  findByIdAndUserStatusOrderById(Pageable pageable, Long id, boolean userStatus);
	//Filtro por id
	public Page<User>  findByIdAndUserStatus(Pageable pageable, Long id, boolean userStatus);
	//Filtro global
	public Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrRolesNameContainingIgnoreCaseOrCiContainingIgnoreCaseOrEmailContainingIgnoreCaseAndUserStatus(
			Pageable pageable, String nameFilter, String lastNameFilter, String roleFilter, String ciFilter, String emailFilter, boolean userStatus);
	//Todo sin filtro ordenado por name
	public Page<User>  findAllByAndUserStatusOrderByFirstName(Pageable pageable, boolean userStatus);
	//Todo sin filtro ordenado por email
	public Page<User>  findAllByAndUserStatusOrderByEmail(Pageable pageable, boolean userStatus);
	//Todo sin filtro ordenado por ci
	public Page<User>  findAllByAndUserStatusOrderByCi(Pageable pageable, boolean userStatus);
	//Todo sin filtro ordenado por roles
	public Page<User>  findAllByAndUserStatusOrderByRolesName(Pageable pageable, boolean userStatus);
	//Todo sin filtro ordenado por id
	public Page<User>  findAllByAndUserStatusOrderById(Pageable pageable, boolean userStatus);
	public Optional<User> findByEmailIgnoreCase(String email);
	public Optional<User> findByEmailIgnoreCaseAndUserStatusTrue(String email);


}
